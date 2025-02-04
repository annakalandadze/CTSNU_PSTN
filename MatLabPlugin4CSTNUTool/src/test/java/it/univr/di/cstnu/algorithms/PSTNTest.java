package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.graph.*;
import it.univr.di.cstnu.util.LogNormalDistributionParameter;
import it.univr.di.cstnu.util.OptimizationEngine;
import it.univr.di.labeledvalue.ALabelAlphabet;
import it.univr.di.cstnu.matlabplugin.MatLabEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test PSTN class using MatLab Engine
 */
public class PSTNTest {

	TNGraph<STNUEdge> g;
	LabeledNode A, A1, B, B1, C, C1;
	ALabelAlphabet alphabet;

	final OptimizationEngine matLabEngine = new MatLabEngine();

	/**
	 * Test buildApprox
	 *
	 * @throws Exception if the write and read of the approximated STNU has a problem.
	 */
	@Test
	public void buildApproxSTNU() throws Exception {
		ALabelAlphabet.ALetter CLetter = new ALabelAlphabet.ALetter("C");
		STNUEdge e = new STNUEdgeInt("AC");
		e.setLabeledValue(CLetter, 1, false);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.g.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(CLetter, -10, true);
		this.g.addEdge(e, C, A);
		e.setConstraintType(Edge.ConstraintType.contingent);
		C.setContingent(true);

		CLetter = new ALabelAlphabet.ALetter("C1");
		e = new STNUEdgeInt("A1C1");
		e.setLabeledValue(CLetter, 3, false);
		this.g.addEdge(e, A1, C1);
		e.setConstraintType(Edge.ConstraintType.contingent);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(CLetter, -10, true);
		this.g.addEdge(e, C1, A1);
		e.setConstraintType(Edge.ConstraintType.contingent);
		C1.setContingent(true);

		e = new STNUEdgeInt("A1A");
		e.setValue(2);
		this.g.addEdge(e, A1, A);

		e = new STNUEdgeInt("CC1");
		e.setValue(2);
		this.g.addEdge(e, C, C1);

		final PSTN pstn = new PSTN(this.g, 1000, .7, matLabEngine);
//		System.out.println("PSTN: " +pstn.getG());

		final PSTN.PSTNCheckStatus result = pstn.buildApproxSTNU();

//		System.out.println("Execution time (ms): " + result.executionTimeNS / 1E6);
//		System.out.println("MatLab Execution time (ms): " + result.partialExecutionTimeNS / 1E6);
		assertNotNull(result.approximatingSTNU);

		pstn.setG(result.approximatingSTNU.getG());
		pstn.getG().setType(TNGraph.NetworkType.PSTN);
		final TNGraphMLWriter tnWriter = new TNGraphMLWriter(null);
		final Writer localWriter = new StringWriter();
		tnWriter.save(pstn.getG(), localWriter);
		final String network = localWriter.toString();
		final TNGraphMLReader<STNUEdge> reader = new TNGraphMLReader<>();
		final TNGraph<STNUEdge> inputGraph = reader.readGraph(network, STNUEdgeInt.class);
		final LabeledNode localC = inputGraph.getNode("C");
		assert localC != null;
		assertNotNull(localC.getLogNormalDistribution());
		assertTrue(localC.getLogNormalDistribution().getLocation() > 8.47);
	}

	/**
	 * Simple test for creating and parsing LogNormalDistributionParameter strings.
	 */
	@Test
	public void logNormalStringTest() {
		C.setContingent(true);
		C.setLogNormalDistributionParameter(new LogNormalDistributionParameter(1.59999, .59999));
		Assert.assertEquals("LogNormalDistributionParameter[location=1.599990, scale=0.599990, shift=0]", C.getLogNormalDistribution().toString());

		final LogNormalDistributionParameter distribution = LogNormalDistributionParameter.parse(C.getLogNormalDistribution().toString());
		Assert.assertEquals(1.59999, distribution.getLocation(), 0);
		Assert.assertEquals(.59999, distribution.getScale(), 0);
	}

	/**
	 * Prepare a network.
	 */
	@Before
	public void setUp() {
		g = new TNGraph<>("test", EdgeSupplier.DEFAULT_STNU_EDGE_CLASS);
		alphabet = g.getALabelAlphabet();
		A = new LabeledNode("A");
		A1 = new LabeledNode("A1");
		B = new LabeledNode("B");
		B1 = new LabeledNode("B1");
		C = new LabeledNode("C");
		C1 = new LabeledNode("C1");
		g.addVertex(A);
		g.addVertex(A1);
		g.addVertex(B);
		g.addVertex(B1);
		g.addVertex(C);
		g.addVertex(C1);
	}
}
