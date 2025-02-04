// SPDX-FileCopyrightText: 2020 Roberto Posenato <roberto.posenato@univr.it>
//
// SPDX-License-Identifier: LGPL-3.0-or-later
package it.univr.di.cstnu.matlabplugin;

import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.univr.di.Debug;
import it.univr.di.cstnu.algorithms.PSTN;
import it.univr.di.cstnu.algorithms.STNU;
import it.univr.di.cstnu.algorithms.WellDefinitionException;
import it.univr.di.cstnu.graph.STNUEdge;
import it.univr.di.cstnu.graph.STNUEdgeInt;
import it.univr.di.cstnu.graph.TNGraph;
import it.univr.di.cstnu.graph.TNGraphMLReader;
import it.univr.di.cstnu.util.OptimizationEngine;
import it.univr.di.cstnu.util.RunMeter;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple class to determine the average execution time (and std dev) of the STNU approximating method of PSTNs, considering as PSTNs some instances determined
 * from the STNUs benchmark.
 * <p>
 * The main idea is the following:
 * <ul>
 *  <li>give some DC STNUs, this runner builds the corresponding PSTNs where each original contingent link is described by a log-normal probability distribution
 *  determined generalizing the original contingent bounds. In this way, each PSTN has a significative probability to be not DC.
 *  <li>then, PSTN#makeSTNUInstance is called in order to obtain a new STNUs that is DC and captures the maximum conjuncted probabilitymass of
 *  all probabilistic contingent links.
 * </ul>
 *
 * @author posenato
 * @version $Rev: 732 $
 */
public class PSTNBenchmarkRunner {

	/**
	 * Represents a key composed by {@code (nodes, contingents)}.
	 * <p>
	 * Implements a natural order based on increasing pair {@code (nodes, contingents)}.
	 *
	 * @author posenato
	 */
	static private class GlobalStatisticsKey implements Comparable<GlobalStatisticsKey> {
		/**
		 * # contingents
		 */
		final int contingents;
		/**
		 * # of nodes
		 */
		final int nodes;

		/**
		 * default constructor
		 *
		 * @param inputNodes       #nodes
		 * @param inputContingents # cont
		 */
		GlobalStatisticsKey(final int inputNodes, final int inputContingents) {
			nodes = inputNodes;
			contingents = inputContingents;
		}

		/**
		 * The order is respect to #nodes,#contingents, and #propositions.
		 *
		 * @param o the object to be compared.
		 *
		 * @return negative if this has, in order, fewer nodes or fewer contingents or fewer proposition than the parameter 'o'; a positive value in the
		 * 	opposite case, 0 when all three values are equals to the corresponding values in 'o'.
		 */
		@Override
		public int compareTo(@Nonnull GlobalStatisticsKey o) {
			final long d = (long) nodes - o.nodes;
			if (d == 0) {
				return contingents - o.contingents;
			}
			return (int) d;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (!(o instanceof GlobalStatisticsKey o1)) {
				return false;
			}
			return o1.compareTo(this) == 0;
		}

		/**
		 * @return #cont
		 */
		public int getContingent() {
			return contingents;
		}

		/**
		 * @return #nodes
		 */
		public int getNodes() {
			return nodes;
		}

		@Override
		public int hashCode() {
			return (contingents) * 100000 + nodes;
		}
	}

	/**
	 * Each instance of this class represents a set of global statistics. Each global statistics element represents a map
	 * {@code (GlobalStatisticsKey, SummaryStatistics)}. Each SummaryStatistics element allows the determination of different statistics of all added item to
	 * the element.
	 */
	static private class GlobalStatistics {
		Object2ObjectMap<GlobalStatisticsKey, SummaryStatistics> networkEdges = new Object2ObjectAVLTreeMap<>();
		Object2ObjectMap<GlobalStatisticsKey, SummaryStatistics> globalExecTimeInSec = new Object2ObjectAVLTreeMap<>();
		Object2ObjectMap<GlobalStatisticsKey, SummaryStatistics> minimizationExecTimeInSec = new Object2ObjectAVLTreeMap<>();
		Object2ObjectMap<GlobalStatisticsKey, SummaryStatistics> numberMinimizationProblem = new Object2ObjectAVLTreeMap<>();
		Object2ObjectMap<GlobalStatisticsKey, SummaryStatistics> probConjunctedProbMass = new Object2ObjectAVLTreeMap<>();
		Object2ObjectMap<GlobalStatisticsKey, SummaryStatistics> solved = new Object2ObjectAVLTreeMap<>();
		Object2ObjectMap<GlobalStatisticsKey, Frequency> srncKindFreq = new Object2ObjectAVLTreeMap<>();
	}

	/**
	 * CSV separator
	 */
	static final String CSVSep = ";\t";
	/**
	 * Global header
	 */
	static final String GLOBAL_HEADER =
		"%n%nGlobal statistics%n"
		+ "#networks" + CSVSep
		+ "#nodes" + CSVSep
		+ "#contingents" + CSVSep
		+ "#avgEdges" + CSVSep
		+ "stdDevEdges" + CSVSep
		+ "solved" + CSVSep
		+ "avgExeTime[s]" + CSVSep
		+ "stdDevExeTime[s]" + CSVSep
		+ "avgMinimExeTime[s]" + CSVSep
		+ "stdDevMinimExeTime[s]" + CSVSep
		+ "avgNumberOfMinimProb" + CSVSep
		+ "stdDevNumberOfMinimProb" + CSVSep
		+ "avgConjunctedProbMass" + CSVSep
		+ "stdDevConjunctedProbMass" + CSVSep
		+ "%%SRNC:PotFailure" + CSVSep
		+ "%%SRNC:ccLoop" + CSVSep
		+ "%%SRNC:InterCycle" + CSVSep
		+ "%n";
	/**
	 *
	 */
	static final String GLOBAL_HEADER_ROW =
		"%d" + CSVSep
		+ "%d" + CSVSep
		+ "%d" + CSVSep
		+ "%E" + CSVSep // avgEdges
		+ "%E" + CSVSep
		+ "%d" + CSVSep // solved
		+ "%E" + CSVSep // avgExeTime
		+ "%E" + CSVSep
		+ "%E" + CSVSep // avgMinimExeTime
		+ "%E" + CSVSep
		+ "%E" + CSVSep // avgNumberOfMinMinimProb
		+ "%E" + CSVSep
		+ "%E" + CSVSep // avgConjunctedProbMass
		+ "%E" + CSVSep
		+ "%f" + CSVSep // "%SRNC:PotFailure"
		+ "%f" + CSVSep // "%SRNC:ccLoop"
		+ "%f"  // "%SRNC:InterCycle"
		+ "%n";
	/**
	 * class logger
	 */
	static final Logger LOG = Logger.getLogger(PSTNBenchmarkRunner.class.getName());
	/**
	 * Output file header
	 */
	static final String OUTPUT_HEADER =
		"fileName" + CSVSep
		+ "#nodes" + CSVSep
		+ "#contingents" + CSVSep
		+ "#edges" + CSVSep
		+ "DC" + CSVSep
		+ "ExitFlag" + CSVSep
		+ "avgExeTime[s]" + CSVSep
		+ "std.dev.[s]" + CSVSep
		+ "minAvgExeTime[s]" + CSVSep
		+ "std.dev.[s]" + CSVSep
		+ "SRNCKind" + CSVSep
		+ "#MinProb" + CSVSep
		+ "ConjProbMass";
	/**
	 * OUTPUT_ROW is split in OUTPUT_ROW_GRAPH + OUTPUT_ROW_ALG_STATS
	 */
	static final String OUTPUT_ROW_GRAPH =
		"%s" + CSVSep //name
		+ "%d" + CSVSep //#nodes
		+ "%d" + CSVSep //#contingents
		+ "%d" + CSVSep;//#edges
	/**
	 * OUTPUT_ROW is split in OUTPUT_ROW_GRAPH + OUTPUT_ROW_ALG_STATS
	 */
	static final String OUTPUT_ROW_ALG_STATS =
		"%s" + CSVSep // DC
		+ "%3d" + CSVSep //exit flag
		+ "%E" + CSVSep // alg avgExe
		+ "%E" + CSVSep // alg avgExe dev std
		+ "%E" + CSVSep // minAvgExeTime
		+ "%E" + CSVSep // minAvgExeTime dev std
		+ "%16s" + CSVSep //"SRNCKind"
		+ "%3d" + CSVSep // #MinProb
		+ "%E" //ConjProbMass
		;
	/**
	 * Version
	 */
	static final String VERSIONandDATE = "1.0, June, 18 2024";
	/**
	 * Date formatter
	 */
	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	/**
	 * Allows to check the execution time of algorithms giving a set of instances.
	 *
	 * @param args an array of {@link String} objects.
	 */
	public static void main(String[] args) {

		LOG.finest("Checker " + VERSIONandDATE + "\nStart...");
		System.out.println("Checker " + VERSIONandDATE + "\n" + getNow() + ": Start of execution.");
		final PSTNBenchmarkRunner tester = new PSTNBenchmarkRunner();

		if (!tester.manageParameters(args)) {
			return;
		}
		LOG.finest("Parameters ok!");
		if (tester.versionReq) {
			return;
		}

		/*
		 * To collect statistics w.r.t. the dimension of networks
		 */
		final GlobalStatistics globalStatistics = new GlobalStatistics();

		final OptimizationEngine matLabEngine = new MatLabEngine();

		final RunMeter runMeter = new RunMeter(System.currentTimeMillis(), tester.instances.size(), 0);
		runMeter.printProgress(0);

		tester.output.println("*".repeat(79));
		tester.output.println("* Trial date: " + getNow());
		tester.output.println("*".repeat(79));
		tester.output.println(OUTPUT_HEADER);
		tester.output.flush();
		int nTaskSuccessfullyFinished = 0;
		for (final File file : tester.instances) {
			if (tester.worker(file, runMeter, matLabEngine, globalStatistics)) {
				nTaskSuccessfullyFinished++;
			}
		}
		final String msg = "Number of instances processed successfully over total: " + nTaskSuccessfullyFinished + "/" + tester.instances.size() + ".";
		LOG.info(msg);
		System.out.println("\n" + getNow() + ": " + msg);

		tester.output.printf(GLOBAL_HEADER);
		//Use one of the element in globalStatistics to extract all the possible globalStatisticsKeys
		for (final Object2ObjectMap.Entry<GlobalStatisticsKey, SummaryStatistics> entryNetworkEdges : globalStatistics.networkEdges.object2ObjectEntrySet()) {
			final GlobalStatisticsKey globalStatisticsKey = entryNetworkEdges.getKey();
			tester.output.printf(GLOBAL_HEADER_ROW,
			                     Long.valueOf(entryNetworkEdges.getValue().getN()),
			                     Integer.valueOf(globalStatisticsKey.getNodes()),
			                     Integer.valueOf(globalStatisticsKey.getContingent()),
			                     Double.valueOf(entryNetworkEdges.getValue().getMean()),
			                     Double.valueOf(entryNetworkEdges.getValue().getStandardDeviation()),
			                     Integer.valueOf((int) globalStatistics.solved.get(globalStatisticsKey).getN()),
			                     Double.valueOf(globalStatistics.globalExecTimeInSec.get(globalStatisticsKey).getMean()),
			                     Double.valueOf(globalStatistics.globalExecTimeInSec.get(globalStatisticsKey).getStandardDeviation()),
			                     Double.valueOf(globalStatistics.minimizationExecTimeInSec.get(globalStatisticsKey).getMean()),
			                     Double.valueOf(globalStatistics.minimizationExecTimeInSec.get(globalStatisticsKey).getStandardDeviation()),
			                     Double.valueOf(globalStatistics.numberMinimizationProblem.get(globalStatisticsKey).getMean()),
			                     Double.valueOf(globalStatistics.numberMinimizationProblem.get(globalStatisticsKey).getStandardDeviation()),
			                     Double.valueOf(globalStatistics.probConjunctedProbMass.get(globalStatisticsKey).getMean()),
			                     Double.valueOf(globalStatistics.probConjunctedProbMass.get(globalStatisticsKey).getStandardDeviation()),
			                     Double.valueOf(globalStatistics.srncKindFreq.get(globalStatisticsKey).getPct(STNU.STNUCheckStatus.SRNCKind.loGraphPotFailure)),
			                     Double.valueOf(globalStatistics.srncKindFreq.get(globalStatisticsKey).getPct(STNU.STNUCheckStatus.SRNCKind.ccLoop)),
			                     Double.valueOf(globalStatistics.srncKindFreq.get(globalStatisticsKey).getPct(STNU.STNUCheckStatus.SRNCKind.interruptionCycle))
			                    );
			tester.output.println();
			if (Debug.ON) {
				if (LOG.isLoggable(Level.FINEST)) {LOG.finest(globalStatistics.minimizationExecTimeInSec.get(globalStatisticsKey).toString());}
				tester.output.println(globalStatistics.minimizationExecTimeInSec.get(globalStatisticsKey).toString());
			}
		}
		tester.output.printf("%n%n%n");
		tester.output.close();
	}

	/**
	 * @return current time in {@link #dateFormatter} format
	 */
	private static String getNow() {
		return dateFormatter.format(new Date());
	}

	/**
	 * @param value value in nanoseconds
	 *
	 * @return the value in seconds
	 */
	private static double nanoSeconds2Seconds(double value) {
		return value / 1E9;
	}

	/**
	 * Class for representing edge .
	 */
	private final Class<STNUEdgeInt> currentEdgeImplClass = STNUEdgeInt.class;

	/**
	 * The input file names. Each file has to contain a CSTN graph in GraphML format.
	 */
	@Argument(required = true, usage = "Input files. Each input file has to be an STNU graph in GraphML format.",
		metaVar = "STNU_file_names", handler = StringArrayOptionHandler.class)
	private String[] inputFiles;
	/**
	 *
	 */
	private List<File> instances;

	/**
	 * Parameter for asking how many times to check the DC for each STNU.
	 */
	@Option(name = "--numRepetitionDCCheck", usage = "Number of time to re-execute DC checking")
	private int nDCRepetition = 1;

	/**
	 * Output stream to outputFile
	 */
	private PrintStream output;

	/**
	 * Output file where to write the determined experimental execution times in CSV format.
	 */
	@Option(name = "-o", aliases = "--output",
		usage = "Output to this file in CSV format. If file is already present, data will be added.",
		metaVar = "outputFile")
	private File outputFile;

	/**
	 * Determine min dispatchable.
	 */
	@Option(name = "--sigmaExpander", usage = "Factor for amplifying the std. dev. of each contingent link probability distribution. Usually it is .3")
	private double sigmaFactor = .3;

	/**
	 * Determine min dispatchable.
	 */
	@Option(name = "--rangeFactor", usage = "Factor for amplifying the std. dev. to determine the bounds of contingent links in the first approximating STNU. Usually it is 3.3")
	private double rangeFactor = 3.3;

	/**
	 * Determine min dispatchable.
	 */
	@Option(name = "--expander", usage = "Factor for amplifying the values of all edge values. Usually it is 1000.")
	private int expander = 1000;

	/**
	 *
	 */
	@Option(name = "--save", usage = "Save all checked instances in dispatchable form.")
	private boolean save;

	/**
	 *
	 */
	@Option(name = "--saveMinimized", usage = "Save all checked instances in minimized dispatchable form.")
	private boolean saveMinimized;

	/**
	 * Parameter for asking timeout in sec.
	 */
	@Option(name = "--timeOut", usage = "Time in seconds.")
	private int timeOut = 1800; // 20 min

	/**
	 * Software Version.
	 */
	@Option(name = "-v", aliases = "--version", usage = "Version")
	private boolean versionReq;

	/**
	 * Checks the STNU graph of the PSTN and, if it is not DC, reduces the bounds of the possible contingent links in the SRN cycle
	 * solving a maximization problem. The maximization problem has the goal to reduce the bounds of one or more contingent links
	 * maximizing the conjunct probability mass of the contingent links.<br>
	 * Then, this method adds statistics to gExecTimeInSec and gNetworkEdges.<br>
	 * It returns the string representing the ({@link #OUTPUT_ROW_ALG_STATS}) part to add to the row in the statistics file.
	 */
	private String approximatingSTNUTest(
		@Nonnull PSTN pstn,
		@Nonnull String rowToWrite,
		@Nonnull SummaryStatistics gExecTimeInSec,
		@Nonnull SummaryStatistics gMinimizationExecTimeInSec,
		@Nonnull SummaryStatistics gSolved,
		@Nonnull SummaryStatistics gNumberMinimizationProblem,
		@Nonnull SummaryStatistics gProbConjunctedProbMass,
		@Nonnull Frequency gFrequency
	                                    ) {

		String msg;
		boolean checkInterrupted = false;
		final String fileName = pstn.getG().getFileName().getName();
		PSTN.PSTNCheckStatus status = null;

		final SummaryStatistics
			localExecTimeStat = new SummaryStatistics(),
			localPartialExecTimeStat = new SummaryStatistics(),
			localProbabilityMass = new SummaryStatistics();

		for (int j = 0; j < nDCRepetition && !checkInterrupted; j++) {
			LOG.info("Test " + (j + 1) + "/" + nDCRepetition + " for STNU " + fileName);
			status = new PSTN.PSTNCheckStatus();


			try {
				status = pstn.buildApproxSTNU();
			} catch (IllegalArgumentException e) {
				msg = getNow() + ": " + fileName + " cannot be approximated. Details:" + e.getMessage() + "\nIgnored.";
				System.out.println(msg);
				LOG.severe(msg);
				checkInterrupted = true;
				status.consistency = false;
//				error = e.getMessage();
				continue;
			}
			if (status.timeout) {
				msg = getNow() + ": " + fileName + " cannot be approximated for timeout.\nIgnored.";
				System.out.println(msg);
				LOG.severe(msg);
				status.consistency = false;
				checkInterrupted = true;
				break;
			}
			if (!status.consistency) {
				msg = getNow() + ": " + fileName + " cannot be approximated because not controllability was found.\nIgnored.";
				System.out.println(msg);
				LOG.info(msg);
				checkInterrupted = true;
				break;
			}
			localExecTimeStat.addValue(status.executionTimeNS);
			localPartialExecTimeStat.addValue(status.partialExecutionTimeNS);
			localProbabilityMass.addValue(status.getProbabilityMass());
		} // end for checking repetition for a single file

		assert status != null;
		if (status.timeout || checkInterrupted) {
			if (status.timeout) {
				msg = "\n\n" + getNow() + ": Timeout or interrupt occurred. " + fileName
				      + " STNU is ignored.\n";
				System.out.println(msg);
			}
			status.finished = false;
		} else {
			status.executionTimeNS = (long) localExecTimeStat.getMean();
			status.stdDevExecutionTimeNS = (long) localExecTimeStat.getStandardDeviation();
			status.partialExecutionTimeNS = (long) localPartialExecTimeStat.getMean();
			status.stdDevPartialExecutionTimeNS = (long) localPartialExecTimeStat.getStandardDeviation();
			status.probabilityMass = localProbabilityMass.getMean();
		}
		//NOW DETERMINE STATISTICS
//		"%s" + CSVSep // DC
//		+ "%d" + CSVSep //exit flag
//		+ "%E" + CSVSep // alg avgExe
//		+ "%E" + CSVSep // alg avgExe dev std
//		+ "%E" + CSVSep // minAvgExeTime
//		+ "%E" + CSVSep // minAvgExeTime dev std
//		+ "%d" + CSVSep //"SRNCKind"
//		+ "%d" + CSVSep // #MinProb
//		+ "%E" //ConjProbMass

		final double localAvgExeInSec = nanoSeconds2Seconds(status.executionTimeNS);
		final double localStdDevExeInSec = nanoSeconds2Seconds(status.stdDevExecutionTimeNS);
		final double localAvgMinProbExeInSec = nanoSeconds2Seconds(status.partialExecutionTimeNS);
		final double localStdDevMinProvExeInSec = nanoSeconds2Seconds(status.stdDevPartialExecutionTimeNS);

		if (!status.finished) {
			// time out or generic error
			rowToWrite += String.format(OUTPUT_ROW_ALG_STATS,
			                            "false",
			                            status.exitFlag,
			                            Double.valueOf(localAvgExeInSec),
			                            Double.valueOf(Double.NaN),
			                            Double.valueOf(localAvgMinProbExeInSec),
			                            Double.valueOf(Double.NaN),
			                            "-",
			                            Integer.valueOf(status.cycles),
			                            Double.valueOf(status.probabilityMass)
			                           );
			return rowToWrite;
		}

		if (status.consistency) {
			gSolved.addValue(1);
			gExecTimeInSec.addValue(localAvgExeInSec);
			gMinimizationExecTimeInSec.addValue(localAvgMinProbExeInSec);
			gNumberMinimizationProblem.addValue(status.cycles);
			gProbConjunctedProbMass.addValue(status.getProbabilityMass());
			if (status.getSrncKind() != null) {
				gFrequency.addValue(status.getSrncKind());
			}
		}

		LOG.info(fileName + " has been analyzed: Process ended? " + status.finished);
		LOG.info(fileName + " Is DC? " + status.consistency);
		LOG.info(fileName + " average checking time [s]: " + localAvgExeInSec);
		LOG.info(fileName + " std. deviation [s]: " + localStdDevExeInSec);
		rowToWrite += String.format(OUTPUT_ROW_ALG_STATS,
		                            status.consistency,
		                            status.exitFlag,
		                            Double.valueOf(localAvgExeInSec),
		                            Double.valueOf(localStdDevExeInSec),
		                            Double.valueOf(localAvgMinProbExeInSec),
		                            Double.valueOf(localStdDevMinProvExeInSec),
		                            status.getSrncKind(),
		                            Integer.valueOf(status.cycles),
		                            Double.valueOf(status.probabilityMass)
		                           );
		if ((this.save || this.saveMinimized) && status.consistency && status.approximatingSTNU != null) {
			//copy all found contingent bounds to this network before saving it.
			//minimize the approximating graph
			final STNU stnu = new STNU(status.approximatingSTNU.getG());
			try {
				stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.FD_STNU_IMPROVED);
			} catch (WellDefinitionException e) {
				throw new RuntimeException(e);
			}
			if (this.saveMinimized) {
				try {
					stnu.applyMinDispatchableESTNU();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
//			pstn.updateContingentBounds(status.approximatingSTNU); cannot be applied because the minimization can change some activation node and, therefore,
			//the name of some edges. So, updateContingentBounds could not find the correspondence of LC UC edges.
			pstn.setG(stnu.getG());//it is possible because stnu.getG() has nodes with the original probability distribution parameters.
			pstn.getG().setType(TNGraph.NetworkType.PSTN);//fundamental!
			//save the result
			final String outFileName = fileName.substring(0, fileName.length() - 5) + "-STNUApproximation.pstn";
			pstn.setfOutput(new File(outFileName));
			pstn.saveGraphToFile();
		}
		return rowToWrite;
	}

	/**
	 * Returns an STNU object filled with the given graph and timeOut set as the given parameter {@link #timeOut}.
	 *
	 * @param g input graph
	 *
	 * @return an stnu instance
	 */
	private STNU makeSTNUInstance(TNGraph<STNUEdge> g) {
		return new STNU(g, timeOut);
	}

	/**
	 * Simple method to manage command line parameters using {@code args4j} library.
	 *
	 * @param args input arguments
	 *
	 * @return false if a parameter is missing, or it is wrong. True if every parameter is given in a right format.
	 */
	private boolean manageParameters(String[] args) {
		final CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			// if there's a problem in the command line, you'll get this exception. this will report an error message.
			System.err.println(e.getMessage());
			System.err.println(
				"java -cp CSTNU-<version>.jar -cp it.univr.di.cstnu.DispatchabilityBenchmarkRunner [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();

			// print option sample. This is useful some time
			// System.err.println("Example: java -jar Checker.jar" + parser.printExample(OptionHandlerFilter.REQUIRED) +
			// " <STNU_file_name0> <STNU_file_name1>...");
			return false;
		}

		if (versionReq) {
			System.out.print(getClass().getName() + " " + VERSIONandDATE + ". Academic and non-commercial use only.\n"
			                 + "Copyright © 2017-2020, Roberto Posenato");
			return true;
		}

		if (outputFile != null) {
			if (outputFile.isDirectory()) {
				System.err.println("Output file is a directory.");
				parser.printUsage(System.err);
				System.err.println();
				return false;
			}
			// filename has to end with .csv
			if (!outputFile.getName().endsWith(".csv")) {
				if (!outputFile.renameTo(new File(outputFile.getAbsolutePath() + ".csv"))) {
					final String m = "File " + outputFile.getAbsolutePath() + " cannot be renamed!";
					LOG.severe(m);
					System.err.println(m);
					return false;
				}
			}
			try {
				output = new PrintStream(new FileOutputStream(outputFile, true), true, StandardCharsets.UTF_8);
			} catch (IOException e) {
				System.err.println("Output file cannot be created: " + e.getMessage());
				parser.printUsage(System.err);
				System.err.println();
				return false;
			}
		} else {
			output = System.out;
		}

		final String suffix = "stnu";

		instances = new ArrayList<>(inputFiles.length);
		for (final String fileName : inputFiles) {
			final File file = new File(fileName);
			if (!file.exists()) {
				System.err.println("File " + fileName + " does not exit.");
				parser.printUsage(System.err);
				System.err.println();
				return false;
			}
			if (!file.getName().endsWith(suffix)) {
				System.err.println("File " + fileName
				                   +
				                   " has not the right suffix associated to the suffix of the given network type (right suffix: "
				                   + suffix + "). Game over :-/");
				parser.printUsage(System.err);
				System.err.println();
				return false;
			}
			instances.add(file);
		}

		return true;
	}

	/**
	 * Loads the file and execute all the actions (specified as instance parameter) on the network represented by the file.
	 *
	 * @param file             input file
	 * @param runState         current state
	 * @param optimizationEngine    Optimization engine necessary to solve the non-linear optimization problem associated to the approximating STNU of a PSTN
	 * @param globalStatistics global statistics
	 *
	 * @return true if required task ends successfully, false otherwise.
	 */
	private boolean worker(
		@Nonnull File file,
		@Nonnull RunMeter runState,
		@Nonnull OptimizationEngine optimizationEngine,
		@Nonnull GlobalStatistics globalStatistics) {

		if (LOG.isLoggable(Level.FINER)) {
			LOG.finer("Loading " + file.getName() + "...");
		}
		final TNGraphMLReader<STNUEdge> graphMLReader = new TNGraphMLReader<>();
		final TNGraph<STNUEdge> graphToCheck;
		try {
			graphToCheck = graphMLReader.readGraph(file, currentEdgeImplClass);
		} catch (IOException | ParserConfigurationException | SAXException e2) {
			final String msg = "File " + file.getName() + " cannot be parsed. Details: " + e2.getMessage() + ".\nIgnored.";
			LOG.warning(msg);
			System.out.println(msg);
			return false;
		}
		LOG.finer("...done!");
		//NODES and EDGES in the original graph. DO NOT MOVE such variable.
		final int nNodes = graphToCheck.getVertexCount();
		final int nEdges = graphToCheck.getEdgeCount();

		final STNU stnu;
		stnu = makeSTNUInstance(graphToCheck);
		try {
			stnu.initAndCheck();
		} catch (Exception e) {
			final String msg = getNow() + ": " + file.getName() + " is not a not well-defined instance. Details:" + e.getMessage() + "\nIgnored.";
			System.out.println(msg);
			LOG.severe(msg);
			return false;
		}
		//Only now contingent node number is significative
		final int nContingents = graphToCheck.getContingentNodeCount();

		final GlobalStatisticsKey globalStatisticsKey = new GlobalStatisticsKey(nNodes, nContingents);

		SummaryStatistics gNetworkEdges = globalStatistics.networkEdges.get(globalStatisticsKey);
		if (gNetworkEdges == null) {
			gNetworkEdges = new SummaryStatistics();
			globalStatistics.networkEdges.put(globalStatisticsKey, gNetworkEdges);
		}
		gNetworkEdges.addValue(nEdges);

		SummaryStatistics gExecTimeInSec = globalStatistics.globalExecTimeInSec.get(globalStatisticsKey);
		if (gExecTimeInSec == null) {
			gExecTimeInSec = new SummaryStatistics();
			globalStatistics.globalExecTimeInSec.put(globalStatisticsKey, gExecTimeInSec);
		}

		SummaryStatistics gMinimizationExecTimeInSec = globalStatistics.minimizationExecTimeInSec.get(globalStatisticsKey);
		if (gMinimizationExecTimeInSec == null) {
			gMinimizationExecTimeInSec = new SummaryStatistics();
			globalStatistics.minimizationExecTimeInSec.put(globalStatisticsKey, gMinimizationExecTimeInSec);
		}

		SummaryStatistics gProbSolved = globalStatistics.solved.get(globalStatisticsKey);
		if (gProbSolved == null) {
			gProbSolved = new SummaryStatistics();
			globalStatistics.solved.put(globalStatisticsKey, gProbSolved);
		}

		SummaryStatistics gNumberMinimizationProblem = globalStatistics.numberMinimizationProblem.get(globalStatisticsKey);
		if (gNumberMinimizationProblem == null) {
			gNumberMinimizationProblem = new SummaryStatistics();
			globalStatistics.numberMinimizationProblem.put(globalStatisticsKey, gNumberMinimizationProblem);
		}

		SummaryStatistics gProbConjunctedProbMass = globalStatistics.probConjunctedProbMass.get(globalStatisticsKey);
		if (gProbConjunctedProbMass == null) {
			gProbConjunctedProbMass = new SummaryStatistics();
			globalStatistics.probConjunctedProbMass.put(globalStatisticsKey, gProbConjunctedProbMass);
		}

		Frequency gFreqSRNCKind = globalStatistics.srncKindFreq.get(globalStatisticsKey);
		if (gFreqSRNCKind == null) {
			gFreqSRNCKind = new Frequency();
			globalStatistics.srncKindFreq.put(globalStatisticsKey, gFreqSRNCKind);
		}

		LOG.info(getNow() + ": Creation of PSTN: start.");
		final PSTN pstn;
		try {
			//this constructor simply builds a PSTN determining a probability distribution for each contingent link from the present contingent bound present in the graph.
			//the graph could be NOT DC.
			pstn = new PSTN(graphToCheck, this.expander, this.sigmaFactor, this.rangeFactor, optimizationEngine);
		} catch (IllegalArgumentException e) {
			final String msg = getNow() + ": " + file.getName() + " cannot be converted to a PSTN. Details:" + e.getMessage() + "\nIgnored.";
			System.out.println(msg);
			LOG.severe(msg);
			return false;
		}
		LOG.info(getNow() + ": Creation of PSTN: finished.");

		String rowToWrite = String.format(OUTPUT_ROW_GRAPH, file.getName(), Integer.valueOf(nNodes), Integer.valueOf(nContingents), Integer.valueOf(nEdges));

//		final PSTN.PSTNCheckStatus status;
		LOG.info(getNow() + ": Creation of approximating STNU: start.");
		rowToWrite = approximatingSTNUTest(pstn,
		                                   rowToWrite,
		                                   gExecTimeInSec,
		                                   gMinimizationExecTimeInSec,
		                                   gProbSolved,
		                                   gNumberMinimizationProblem,
		                                   gProbConjunctedProbMass,
		                                   gFreqSRNCKind
		                                  );
		LOG.info(getNow() + ": Creation of approximating STNU: finished.");

		output.println(rowToWrite);
		output.flush();
		runState.printProgress();
		return true;
	}
}
