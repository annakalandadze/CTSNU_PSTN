package it.univr.di.cstnu.algorithms;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.univr.di.cstnu.graph.*;
import it.univr.di.cstnu.matlabplugin.MatLabEngine;
import it.univr.di.cstnu.util.LogNormalDistributionParameter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunPSTNPipeline {

    static Random rng = new Random(42);
    static int totalSamples = 500;



    public static void main(String[] args) throws WellDefinitionException, IOException {

        if (args.length < 2) {
            System.out.println("Usage: java PSTNBatchProcessor <input_directory> <output_csv_file>");
            return;
        }

        File inputDir = new File(args[0]);
        File outputFile = new File(args[1] + ".csv");
        File outputDetailedFile = new File(args[1] + "_detailed.csv");
        String profitFile = "/Users/akalandadze/Desktop/clean-stochastic-rcpsp/pstn-for-rcpsp-max/10_5_time_model.csv";

        if (!inputDir.isDirectory()) {
            System.out.println("Error: Input path is not a directory!");
            return;
        }

        List<File> pstnFiles = Arrays.asList(Objects.requireNonNull(inputDir.listFiles((dir, name) -> name.endsWith(".pstn"))));

        if (pstnFiles.isEmpty()) {
            System.out.println("No PSTN files found in the directory!");
            return;
        }
        PrintWriter writerDetailed = new PrintWriter(new FileWriter(outputDetailedFile));

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Filename,ProbabilityMass,AverageProfit");

            for (File pstnFile : pstnFiles) {
                System.out.println("Processing: " + pstnFile.getName());

                PSTN pstn = new PSTN();
                pstn.rangeFactor = 3.3;

                final TNGraphMLReader<STNUEdge> graphMLReader = new TNGraphMLReader<>();
                try {
                    pstn = new PSTN(graphMLReader.readGraph(pstnFile, STNUEdgeInt.class), 1000, 0.3, new MatLabEngine());
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    System.out.println("Error reading file: " + pstnFile.getName());
                    continue;
                }

                PSTN.PSTNCheckStatus status;
                while (true) {
                    try {
                        status = pstn.buildApproxSTNU();
                        break;
                    } catch (Exception e) {
                        pstn.setOptimizationEngine(new MatLabEngine());
                        System.out.println("Error in buildApproxSTNU. Retrying...");
                    }
                }

                if (!status.finished) {
                    System.out.println("Skipping file (not finished): " + pstnFile.getName());
                    writer.println(pstnFile.getName() + "," + 0 + "," + 0);
                    continue;
                }
                String filename = pstnFile.getName().replace(".pstn", "");
                String[] parts = filename.split("_");
                double std = Double.parseDouble(parts[3]);
                double increase = Double.parseDouble(parts[4]);
                if (status.exitFlag >= 1) {
                    double probabilityMass = status.probabilityMass;
                    int totalProfitSum = 0;
                    long totalTime = 0;
                    STNU stnu = status.approximatingSTNU;

                    for (int i = 0; i < totalSamples; i++) {
                        Map<LabeledNode, Integer> sample = new HashMap<>();
                        // sample for lognormal
                        for (LabeledNode node : stnu.getG().getNodesOrdered()) {
                            double stdNormal = rng.nextGaussian();
                            LogNormalDistributionParameter logN = node.getLogNormalDistribution();
                            if (logN != null) {
                                double mean = logN.getLocation();
                                double scale = logN.getScale();
                                int value = (int) Math.exp(scale * stdNormal + mean);
                                sample.put(node, value);
                            }
                        }

                        long startTime = System.nanoTime();

                        STNU cloneSTNU = new STNU(stnu);
                        STNU stnuToModify = new STNU(stnu);
                        cloneSTNU.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);
                        STNURTE rte = new STNURTE(cloneSTNU.getG(), true);
                        final STNURTE.Strategy rtedStrategy = STNURTE.StrategyEnum.FIRST_NODE_EARLY_EXECUTION_STRATEGY;
                        final STNURTE.Strategy environmentStrategy = STNURTE.StrategyEnum.LATE_EXECUTION_STRATEGY;
                        STNURTE.RTEState rteState = rte.rte(rtedStrategy, environmentStrategy, sample);
                        int currentProfit = 0;
                        List<Map<LabeledNode, Integer>> allSchedules = new ArrayList<>();
                        int prevTimestamp = 0;

                        while (!rteState.finished) {
                            Object2IntOpenHashMap<LabeledNode> schedule = rteState.schedule;
                            for (LabeledNode myN : schedule.keySet()) {
                                // Create a new map for each entry and store it in allSchedules
                                Map<LabeledNode, Integer> newSchedule = new HashMap<>();
                                newSchedule.put(myN, schedule.getInt(myN) + prevTimestamp);
                                allSchedules.add(newSchedule);
                            }
                            prevTimestamp = rteState.currentTime;
                            allSchedules.add(schedule);
                            // find jobs that just started and that completed
                            Map<String, Set<String>> nodes = processNodes(rteState);
                            Set<String> startOnly = nodes.get("startOnlyNodes");
                            Set<String> completed = nodes.get("completeNodes");
                            Map<String, Set<String>> startedMap = processNodeSets(startOnly);
                            Map<String, Set<String>> completedMap = processNodeSets(completed);
                            // create new STNU
                            STNU stnuCopy = process(stnuToModify, completedMap, startedMap, rteState);
                            stnuToModify = new STNU(stnuCopy);
                            stnuCopy.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);
                            rte = new STNURTE(stnuCopy.getG(), true);
                            rteState = rte.rte(rtedStrategy, environmentStrategy, sample);
                            currentProfit += calculateCompletedProductProfit(completedMap, profitFile);
                        }
                        long endTime = System.nanoTime();
                        totalTime += (endTime - startTime) / 1_000_000;
                        for (LabeledNode myN : rteState.schedule.keySet()) {
                            // Create a new map for each entry and store it in allSchedules
                            Map<LabeledNode, Integer> newSchedule = new HashMap<>();
                            newSchedule.put(myN, rteState.schedule.getInt(myN) + prevTimestamp);
                            allSchedules.add(newSchedule);
                        }
                        Map<LabeledNode, Integer> flattenedSchedule = new HashMap<>();
                        for (Map<LabeledNode, Integer> scheduleMap : allSchedules) {
                            for (Map.Entry<LabeledNode, Integer> entry : scheduleMap.entrySet()) {
                                flattenedSchedule.merge(entry.getKey(), entry.getValue(), Integer::sum);
                            }
                        }
                        saveToFile(flattenedSchedule, "schedule_" + i + "_" + filename + ".txt", filename  );
                        Map<String, Set<String>> nodes = processNodes(rteState);
                        currentProfit += calculateCompletedProductProfit(processNodeSets(nodes.get("completeNodes")), profitFile);
                        writerDetailed.println(pstnFile.getName() + "," + std + "," + increase + "," + probabilityMass + "," + currentProfit + "," + (endTime - startTime) + "," + i);
                    }
                    int averageProfit = totalSamples > 0 ? totalProfitSum / totalSamples : 0;
                    long averageTime = totalSamples > 0 ? totalTime / totalSamples : 0;
                    writer.println(pstnFile.getName() + "," + std + "," + increase + "," + probabilityMass + "," + averageProfit + "," + averageTime);
                    System.out.println("Finished: " + pstnFile.getName());
                } else {
                    writer.println(pstnFile.getName() + "," + std + "," + increase + "," + 0 + "," + 0 + "," + 0);
                }
            }

        } catch (IOException e) {
            System.out.println("Error writing output file: " + e.getMessage());
        }
    }

    public static void saveToFile(Map<LabeledNode, Integer> flattenedSchedule, String fileName, String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs(); // Create directories if they don't exist
        }

        File file = new File(dir, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<LabeledNode, Integer> entry : flattenedSchedule.entrySet()) {
                LabeledNode key = entry.getKey();
                Integer value = entry.getValue();

                writer.write(key.getName() + "," + value);
                writer.newLine();  // Move to the next line after writing each pair
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static STNU process(STNU stnu, Map<String, Set<String>> completed, Map<String, Set<String>> ongoing, STNURTE.RTEState rteState) {
        TNGraph stnuCopy = createFilteredGraph(stnu, completed, ongoing, rteState);
        return new STNU(stnuCopy);
    }

    private static Map<String, Set<String>> processNodes(STNURTE.RTEState rteState) throws IOException {
        Set<String> startNodes = new HashSet<>();
        Set<String> finishNodes = new HashSet<>();
        Set<String> completeNodes = new HashSet<>();
        Set<String> startOnlyNodes = new HashSet<>();
        Pattern pattern = Pattern.compile("^(\\d+_\\d+)_(\\d+)_(start|finish)$");

        for (LabeledNode node : rteState.schedule.keySet()) {
            Matcher matcher = pattern.matcher(node.getName());

            if (matcher.matches()) {
                String key = matcher.group(1);
                String job = matcher.group(2);
                String type = matcher.group(3);

                if (type.equals("start")) {
                    startNodes.add(key + "_" + job);
                    if (job.equals("0") || job.equals("11")) {
                        finishNodes.add(key + "_" + job);
                    }
                } else {
                    finishNodes.add(key + "_" + job);
                }
            }
        }

        for (String node : startNodes) {
            if (finishNodes.contains(node)) {
                completeNodes.add(node);
            } else {
                startOnlyNodes.add(node);
            }
        }
        Map<String, Set<String>> result = new HashMap<>();
        result.put("startOnlyNodes", startOnlyNodes);
        result.put("completeNodes", completeNodes);


        return result;
//        profit = calculateCompletedProductProfit(completed, "/Users/akalandadze/Desktop/Thesis/rcpsp-max-pstn/10_5_time_model.csv");

    }

    private static Map<String, Set<String>> processNodeSets(Set<String> nodeSet) {
        Map<String, Set<String>> nodeMap = new HashMap<>();
        Pattern pattern = Pattern.compile("^(\\d+_\\d+)_(\\d+)$");
        for (String node : nodeSet) {
            Matcher matcher = pattern.matcher(node);
            if (matcher.matches()) {
                String key = matcher.group(1);
                String job = matcher.group(2);
                nodeMap.computeIfAbsent(key, k -> new HashSet<>()).add(job);
            }
        }
        return nodeMap;
    }

    // rebuild STNU after disruption
    private static TNGraph createFilteredGraph(STNU stnu, Map<String, Set<String>> completed, Map<String, Set<String>> ongoing, STNURTE.RTEState rteState) {
        TNGraph filteredGraph = new TNGraph("filtered_graph", STNUEdgeInt.class);

        for (LabeledNode node : stnu.getG().getVertices()) {
            if (shouldIncludeNode(node, completed, ongoing)) {
                filteredGraph.addVertex(node);
            }
        }

        for (STNUEdge edge : stnu.getG().getEdges()) {
            if (shouldIncludeEdge(edge, stnu, completed, ongoing)) {
                // modify deadlines
                if (stnu.getG().getSource(edge.getName()).getName().equals("INITIAL_EVENT") && stnu.getG().getSource(edge.getName()).getName().contains("finish")) {
                    if (edge.getValue() - rteState.currentTime >= 0) {
                        edge.setValue(edge.getValue() - rteState.currentTime);
                        filteredGraph.addEdge(edge, stnu.getG().getSource(edge.getName()), stnu.getG().getDest(edge.getName()));
                    }
                } else {
                    filteredGraph.addEdge(edge, stnu.getG().getSource(edge.getName()), stnu.getG().getDest(edge.getName()));
                }
            }
        }

        return filteredGraph;
    }

    // check whether the node should be included in new STNU
    private static boolean shouldIncludeNode(LabeledNode node, Map<String, Set<String>> completed, Map<String, Set<String>> ongoing) {
        if (node.getName().equals("INITIAL_EVENT")) {
            return true;
        }

        String[] parts = node.getName().trim().split("_");
        if (parts.length == 4 && (parts[3].equals("start") || parts[3].equals("finish"))) {
            String id = parts[0] + "_" + parts[1];
            return !completed.containsKey(id) && !ongoing.containsKey(id);
        }

        return false;
    }

    // check for rebuilding STNU whether the edge should be still in STNU (if both nodes -- source and target -- still should be included)
    private static boolean shouldIncludeEdge(Edge edge, STNU stnu, Map<String, Set<String>> completed, Map<String, Set<String>> ongoing) {
        if (edge.getConstraintType().equals(Edge.ConstraintType.derived)) {
            return false;
        }

        String sourceName = stnu.getG().getSource(edge.getName()).getName().trim();
        String destName = stnu.getG().getDest(edge.getName()).getName().trim();

        String[] sourceParts = sourceName.split("_");
        String[] destParts = destName.split("_");

        return !isNodeExcluded(sourceParts, completed, ongoing) && !isNodeExcluded(destParts, completed, ongoing);
    }

    // check if the product to which job corresponds has jobs in completed or in ongoing -- if yes, exclude it
    private static boolean isNodeExcluded(String[] parts, Map<String, Set<String>> completed, Map<String, Set<String>> ongoing) {
        if (parts.length < 2) {
            return false;
        }
        String key = parts[0] + "_" + parts[1];
        return completed.containsKey(key) || ongoing.containsKey(key);
    }

    private static int calculateCompletedProductProfit(Map<String, Set<String>> completed, String csvFile) {
        Map<String, Integer> profitMap = loadProfitData(csvFile);
        int totalProfit = 0;

        for (Map.Entry<String, Set<String>> entry : completed.entrySet()) {
            String productId = entry.getKey();
            Set<String> jobs = entry.getValue();

            if (jobs.size() == 12) { // Product is fully completed, this is hardcoded, I use J10 -- each products has 10 jobs + head + tail --> 12 in total
                Integer profit = profitMap.get(productId);
                if (profit != null) {
                    totalProfit += profit;
                }
            }
        }
        return totalProfit;
    }

    // for this function, you need a profit map file, I attach the example format in the repository (profit_results.csv)
    private static Map<String, Integer> loadProfitData(String csvFile) {
        Map<String, Integer> profitMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // Assuming CSV uses commas
                if (parts.length == 2) {
                    profitMap.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        return profitMap;
    }


}
