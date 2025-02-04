package it.univr.di.cstnu.algorithms;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.*;
import it.univr.di.cstnu.graph.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author posenato, ocampo
 */
public class STNTest {
	/**
	 * logger
	 */
	static Logger LOG = Logger.getLogger(STNTest.class.getName());

	/**
	 *
	 */
	STN stn;
	/**
	 *
	 */
	TNGraphMLReader<STNEdge> graphMLReader;
	/**
	 *
	 */
	String fileName = "src/test/resources/testSTNwithNegativeCycle.stn";
	/**
	 *
	 */
	String fileName2 = "src/test/resources/testSTNwithNegativeCycle8nodes.stn";
	/**
	 * 9 nodes (including Z)
	 * Z cannot reach any node.
	 * No rigid components.
	 */
	String stn20151201 = """
			<?xml version="1.0" encoding="UTF-8"?>
			<graphml xmlns="http://graphml.graphdrawing.org/xmlns/graphml"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/graphml">
			<key id="nContingent" for="graph">
			<desc>Number of contingents in the graph</desc>
			<default>0</default>
			</key>
			<key id="nObservedProposition" for="graph">
			<desc>Number of observed propositions in the graph</desc>
			<default>0</default>
			</key>
			<key id="NetworkType" for="graph">
			<desc>Network Type</desc>
			<default>CSTNU</default>
			</key>
			<key id="nEdges" for="graph">
			<desc>Number of edges in the graph</desc>
			<default>0</default>
			</key>
			<key id="nVertices" for="graph">
			<desc>Number of vertices in the graph</desc>
			<default>0</default>
			</key>
			<key id="Name" for="graph">
			<desc>Graph Name</desc>
			<default></default>
			</key>
			<key id="Obs" for="node">
			<desc>Proposition Observed. Value specification: [a-zA-F]</desc>
			<default></default>
			</key>
			<key id="x" for="node">
			<desc>The x coordinate for the visualization. A positive value.</desc>
			<default>0</default>
			</key>
			<key id="Label" for="node">
			<desc>Label. Format: [¬[a-zA-F]|[a-zA-F]]+|⊡</desc>
			<default>⊡</default>
			</key>
			<key id="y" for="node">
			<desc>The y coordinate for the visualization. A positive value.</desc>
			<default>0</default>
			</key>
			<key id="Potential" for="node">
			<desc>Labeled Potential Values. Format: {[('node name (no case modification)', 'integer', 'label') ]+}|{}</desc>
			<default></default>
			</key>
			<key id="Type" for="edge">
			<desc>Type: Possible values: contingent|requirement|derived|internal.</desc>
			<default>requirement</default>
			</key>
			<key id="Value" for="edge">
			<desc>Value for STN edge. Format: 'integer'</desc>
			<default></default>
			</key>
			<graph edgedefault="directed">
			<data key="NetworkType">STN</data>
			<data key="nEdges">13</data>
			<data key="nVertices">8</data>
			<data key="Name">stn20151201.stn</data>
			<node id="n1">
			<data key="x">172.0</data>
			<data key="y">239.0</data>
			</node>
			<node id="n2">
			<data key="x">229.0</data>
			<data key="y">152.0</data>
			</node>
			<node id="n6">
			<data key="x">668.0</data>
			<data key="y">168.0</data>
			</node>
			<node id="n4">
			<data key="x">360.0</data>
			<data key="y">151.0</data>
			</node>
			<node id="n9">
			<data key="x">386.0</data>
			<data key="y">350.0</data>
			</node>
			<node id="n3">
			<data key="x">229.0</data>
			<data key="y">345.0</data>
			</node>
			<node id="n7">
			<data key="x">642.0</data>
			<data key="y">337.0</data>
			</node>
			<node id="n5">
			<data key="x">536.0</data>
			<data key="y">149.0</data>
			</node>
			<edge id="e0" source="n1" target="n3">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e1" source="n3" target="n1">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e10" source="n9" target="n7">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			<edge id="e11" source="n7" target="n9">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			<edge id="e12" source="n3" target="n9">
			<data key="Type">requirement</data>
			<data key="Value">-6</data>
			</edge>
			<edge id="e2" source="n1" target="n2">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e3" source="n2" target="n1">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e4" source="n2" target="n4">
			<data key="Type">requirement</data>
			<data key="Value">-1</data>
			</edge>
			<edge id="e5" source="n4" target="n5">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e6" source="n5" target="n4">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e7" source="n5" target="n6">
			<data key="Type">requirement</data>
			<data key="Value">-1</data>
			</edge>
			<edge id="e8" source="n6" target="n7">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			<edge id="e9" source="n7" target="n6">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			</graph>
			</graphml>
			""";
	/**
	 * 2 rigid components: "n4, n5" and "n6, n7, n9"
	 */
	String twoRigidComponentsSTN = """
			<?xml version="1.0" encoding="UTF-8"?>
			<graphml xmlns="http://graphml.graphdrawing.org/xmlns/graphml"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \s
			xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/graphml">
			<key id="nContingent" for="graph">
			<desc>Number of contingents in the graph</desc>
			<default>0</default>
			</key>
			<key id="nObservedProposition" for="graph">
			<desc>Number of observed propositions in the graph</desc>
			<default>0</default>
			</key>
			<key id="NetworkType" for="graph">
			<desc>Network Type</desc>
			<default>CSTNU</default>
			</key>
			<key id="nEdges" for="graph">
			<desc>Number of edges in the graph</desc>
			<default>0</default>
			</key>
			<key id="nVertices" for="graph">
			<desc>Number of vertices in the graph</desc>
			<default>0</default>
			</key>
			<key id="Name" for="graph">
			<desc>Graph Name</desc>
			<default></default>
			</key>
			<key id="Obs" for="node">
			<desc>Proposition Observed. Value specification: [a-zA-F]</desc>
			<default></default>
			</key>
			<key id="x" for="node">
			<desc>The x coordinate for the visualization. A positive value.</desc>
			<default>0</default>
			</key>
			<key id="Label" for="node">
			<desc>Label. Format: [¬[a-zA-F]|[a-zA-F]]+|⊡</desc>
			<default>⊡</default>
			</key>
			<key id="y" for="node">
			<desc>The y coordinate for the visualization. A positive value.</desc>
			<default>0</default>
			</key>
			<key id="Potential" for="node">
			<desc>Labeled Potential Values. Format: {[('node name (no case modification)', 'integer', 'label') ]+}|{}</desc>
			<default></default>
			</key>
			<key id="Type" for="edge">
			<desc>Type: Possible values: contingent|requirement|derived|internal.</desc>
			<default>requirement</default>
			</key>
			<key id="Value" for="edge">
			<desc>Value for STN edge. Format: 'integer'</desc>
			<default></default>
			</key>
			<graph edgedefault="directed">
			<data key="NetworkType">STN</data>
			<data key="nEdges">14</data>
			<data key="nVertices">8</data>
			<data key="Name">stn20151201quar.stn</data>
			<node id="n1">
			<data key="x">172.0</data>
			<data key="y">239.0</data>
			</node>
			<node id="n6">
			<data key="x">699.0</data>
			<data key="y">165.0</data>
			</node>
			<node id="n7">
			<data key="x">642.0</data>
			<data key="y">337.0</data>
			</node>
			<node id="n9">
			<data key="x">386.0</data>
			<data key="y">350.0</data>
			</node>
			<node id="n2">
			<data key="x">229.0</data>
			<data key="y">152.0</data>
			</node>
			<node id="n4">
			<data key="x">360.0</data>
			<data key="y">151.0</data>
			</node>
			<node id="n5">
			<data key="x">536.0</data>
			<data key="y">149.0</data>
			</node>
			<node id="n3">
			<data key="x">229.0</data>
			<data key="y">345.0</data>
			</node>
			<edge id="e0" source="n1" target="n3">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e1" source="n3" target="n1">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e10" source="n9" target="n7">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			<edge id="e11" source="n7" target="n9">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			<edge id="e12" source="n3" target="n9">
			<data key="Type">requirement</data>
			<data key="Value">-6</data>
			</edge>
			<edge id="e2" source="n1" target="n2">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e24" source="n6" target="n9">
			<data key="Type">requirement</data>
			<data key="Value">-8</data>
			</edge>
			<edge id="e3" source="n2" target="n1">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e4" source="n2" target="n4">
			<data key="Type">requirement</data>
			<data key="Value">-1</data>
			</edge>
			<edge id="e5" source="n4" target="n5">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e6" source="n5" target="n4">
			<data key="Type">requirement</data>
			<data key="Value">-1</data>
			</edge>
			<edge id="e7" source="n5" target="n6">
			<data key="Type">requirement</data>
			<data key="Value">-1</data>
			</edge>
			<edge id="e8" source="n6" target="n7">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			<edge id="e9" source="n7" target="n6">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			</graph>
			</graphml>
			""";
	/**
	 * No rigid components, no ambiguous undominated edges.
	 */
	String stn20211230 = """
			<?xml version="1.0" encoding="UTF-8"?>
			<graphml xmlns="http://graphml.graphdrawing.org/xmlns/graphml"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \s
			xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/graphml">
			<key id="nContingent" for="graph">
			<desc>Number of contingents in the graph</desc>
			<default>0</default>
			</key>
			<key id="nObservedProposition" for="graph">
			<desc>Number of observed propositions in the graph</desc>
			<default>0</default>
			</key>
			<key id="NetworkType" for="graph">
			<desc>Network Type</desc>
			<default>CSTNU</default>
			</key>
			<key id="nEdges" for="graph">
			<desc>Number of edges in the graph</desc>
			<default>0</default>
			</key>
			<key id="nVertices" for="graph">
			<desc>Number of vertices in the graph</desc>
			<default>0</default>
			</key>
			<key id="Name" for="graph">
			<desc>Graph Name</desc>
			<default></default>
			</key>
			<key id="Obs" for="node">
			<desc>Proposition Observed. Value specification: [a-zA-F]</desc>
			<default></default>
			</key>
			<key id="x" for="node">
			<desc>The x coordinate for the visualization. A positive value.</desc>
			<default>0</default>
			</key>
			<key id="Label" for="node">
			<desc>Label. Format: [¬[a-zA-F]|[a-zA-F]]+|⊡</desc>
			<default>⊡</default>
			</key>
			<key id="y" for="node">
			<desc>The y coordinate for the visualization. A positive value.</desc>
			<default>0</default>
			</key>
			<key id="Potential" for="node">
			<desc>Labeled Potential Values. Format: {[('node name (no case modification)', 'integer', 'label') ]+}|{}</desc>
			<default></default>
			</key>
			<key id="Type" for="edge">
			<desc>Type: Possible values: contingent|requirement|derived|internal.</desc>
			<default>requirement</default>
			</key>
			<key id="Value" for="edge">
			<desc>Value for STN edge. Format: 'integer'</desc>
			<default></default>
			</key>
			<graph edgedefault="directed">
			<data key="NetworkType">STN</data>
			<data key="nEdges">15</data>
			<data key="nVertices">8</data>
			<data key="Name">stn20211230woRC.stn</data>
			<node id="n1">
			<data key="x">124.0</data>
			<data key="y">240.0</data>
			</node>
			<node id="n5">
			<data key="x">536.0</data>
			<data key="y">149.0</data>
			</node>
			<node id="n3">
			<data key="x">229.0</data>
			<data key="y">345.0</data>
			</node>
			<node id="n6">
			<data key="x">668.0</data>
			<data key="y">168.0</data>
			</node>
			<node id="n7">
			<data key="x">642.0</data>
			<data key="y">337.0</data>
			</node>
			<node id="n2">
			<data key="x">229.0</data>
			<data key="y">152.0</data>
			</node>
			<node id="n4">
			<data key="x">360.0</data>
			<data key="y">151.0</data>
			</node>
			<node id="n9">
			<data key="x">386.0</data>
			<data key="y">350.0</data>
			</node>
			<edge id="e12" source="n1" target="n2">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e13" source="n1" target="n3">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e21" source="n2" target="n1">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e24" source="n2" target="n4">
			<data key="Type">requirement</data>
			<data key="Value">-1</data>
			</edge>
			<edge id="e31" source="n3" target="n1">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e39" source="n3" target="n9">
			<data key="Type">requirement</data>
			<data key="Value">-6</data>
			</edge>
			<edge id="e45" source="n4" target="n5">
			<data key="Type">requirement</data>
			<data key="Value">3</data>
			</edge>
			<edge id="e54" source="n5" target="n4">
			<data key="Type">requirement</data>
			<data key="Value">-2</data>
			</edge>
			<edge id="e56" source="n5" target="n7">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e7" source="n5" target="n6">
			<data key="Type">requirement</data>
			<data key="Value">1</data>
			</edge>
			<edge id="e76" source="n7" target="n6">
			<data key="Type">requirement</data>
			<data key="Value">4</data>
			</edge>
			<edge id="e79" source="n7" target="n9">
			<data key="Type">requirement</data>
			<data key="Value">5</data>
			</edge>
			<edge id="e8" source="n6" target="n7">
			<data key="Type">requirement</data>
			<data key="Value">0</data>
			</edge>
			<edge id="e96" source="n9" target="n6">
			<data key="Type">requirement</data>
			<data key="Value">0</data>
			</edge>
			<edge id="e97" source="n9" target="n7">
			<data key="Type">requirement</data>
			<data key="Value">-4</data>
			</edge>
			</graph>
			</graphml>
			""";
	private LabeledNode X, W, Y, Z;
	/**
	 *
	 */
	private TNGraph<STNEdge> stnGraph;

	/**
	 *
	 */
	@Test
	public void depthFirstOrder() {
		final LabeledNode w1 = new LabeledNode("W1");
		stnGraph.addVertex(w1);

		STNEdge e = new STNUEdgeInt("zx", 3);
		stnGraph.addEdge(e, Z, X);

		e = new STNUEdgeInt("zy", 5);
		stnGraph.addEdge(e, Z, Y);

		e = new STNUEdgeInt("xw", 1);
		stnGraph.addEdge(e, X, W);

		e = new STNUEdgeInt("yw1", 4);
		stnGraph.addEdge(e, Y, w1);

		ObjectList<LabeledNode> finalOrder = new ObjectArrayList<>();

		String expected = "[❮W1❯, ❮Y❯, ❮W❯, ❮X❯, ❮Z❯]";
		STN.DEPTH_FIRST_ORDER(stnGraph, Z, finalOrder, null, false);
		assertEquals(expected, finalOrder.toString());

		expected = "[❮W❯, ❮X❯, ❮W1❯, ❮Y❯, ❮Z❯]";
		TNPredecessorGraph<STNEdge> stnPredecessorOfZ = STN.GET_STN_PRECEDESSOR_SUBGRAPH_OPTIMIZED(stnGraph, Z, null, null, null);
		if (stnPredecessorOfZ == null) {
			fail("stnPredecessorOfZ is null");
		}
		finalOrder = STN.GET_DEPTH_FIRST_ORDER(stnPredecessorOfZ, Z, null, false);
		assertEquals(expected, finalOrder.toString());

		e = new STNUEdgeInt("yx", 1);
		stnGraph.addEdge(e, Y, X);

		finalOrder.clear();
		expected = "[❮W1❯, ❮W❯, ❮X❯, ❮Y❯, ❮Z❯]";
		STN.DEPTH_FIRST_ORDER(stnGraph, Z, finalOrder, null, false);
		assertEquals(expected, finalOrder.toString());

		expected = "[❮W❯, ❮X❯, ❮W1❯, ❮Y❯, ❮Z❯]";
		stnPredecessorOfZ = STN.GET_STN_PRECEDESSOR_SUBGRAPH_OPTIMIZED(stnGraph, Z, null, null, null);
		if (stnPredecessorOfZ == null) {
			fail("stnPredecessorOfZ is null");
		}
		finalOrder = STN.GET_DEPTH_FIRST_ORDER(stnPredecessorOfZ, Z, null, false);
		assertEquals(expected, finalOrder.toString());

		stnGraph.removeEdge(e);
		e = new STNUEdgeInt("xy", 1);
		stnGraph.addEdge(e, X, Y);

		finalOrder.clear();
		expected = "[❮W1❯, ❮Y❯, ❮W❯, ❮X❯, ❮Z❯]";
		STN.DEPTH_FIRST_ORDER(stnGraph, Z, finalOrder, null, false);
		assertEquals(expected, finalOrder.toString());

		expected = "[❮W1❯, ❮Y❯, ❮W❯, ❮X❯, ❮Z❯]";
		stnPredecessorOfZ = STN.GET_STN_PRECEDESSOR_SUBGRAPH_OPTIMIZED(stnGraph, Z, null, null, null);
		if (stnPredecessorOfZ == null) {
			fail("stnPredecessorOfZ is null");
		}
		finalOrder = STN.GET_DEPTH_FIRST_ORDER(stnPredecessorOfZ, Z, null, false);
		assertEquals(expected, finalOrder.toString());
	}

	/**
	 *
	 */
	@Test
	public void depthFirstOrder1() {
		STNEdge e = new STNUEdgeInt("zx", 3);
		stnGraph.addEdge(e, Z, X);

		e = new STNUEdgeInt("zy", 5);
		stnGraph.addEdge(e, Z, Y);

		e = new STNUEdgeInt("zw", 4);
		stnGraph.addEdge(e, Z, W);

		e = new STNUEdgeInt("xw", 1);
		stnGraph.addEdge(e, X, W);

		e = new STNUEdgeInt("wy", 1);
		stnGraph.addEdge(e, W, Y);

		ObjectList<LabeledNode> finalOrder = new ObjectArrayList<>();

		final String expected = "[❮Y❯, ❮W❯, ❮X❯, ❮Z❯]";
		STN.DEPTH_FIRST_ORDER(stnGraph, Z, finalOrder, null, false);
		assertEquals(expected, finalOrder.toString());


		final TNPredecessorGraph<STNEdge> stnPredecessorOfZ = STN.GET_STN_PRECEDESSOR_SUBGRAPH_OPTIMIZED(stnGraph, Z, null, null, null);
		if (stnPredecessorOfZ == null) {
			fail("stnPredecessorOfZ is null");
		}
		finalOrder = STN.GET_DEPTH_FIRST_ORDER(stnPredecessorOfZ, Z, null, false);
		assertEquals(expected, finalOrder.toString());
	}

	/**
	 *
	 */
	@Before
	public void setUp() {
		stn = new STN();
		graphMLReader = new TNGraphMLReader<>();
		X = new LabeledNode("X");
		Y = new LabeledNode("Y");
		W = new LabeledNode("W");
		Z = new LabeledNode("Z");
		stnGraph = new TNGraph<>("STNTest",STNEdgeInt.class);
		stnGraph.addVertex(Z);
		stnGraph.addVertex(X);
		stnGraph.addVertex(Y);
		stnGraph.addVertex(W);
	}

	/**
	 * @throws Exception if the input file is not available
	 */
	@Test
	public void testBFCT8Nodes() throws Exception {

		stn.fInput = new File(fileName2);

		stn.setG(graphMLReader.readGraph(stn.fInput, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}
		final boolean consistent = STN.SSSP_BFCT(stn.g, stn.g.getZ(), null, stn.horizon, stn.checkStatus);
		assertFalse(consistent);
		final ObjectList<LabeledNode> cycle = stn.getCheckStatus().negativeCycle;
		assertNotNull(cycle);
		assertEquals("Empty cycle",
				"[❮n9; Potential: -6❯, ❮Z; Potential: -6❯, ❮n3; Potential: -5❯, ❮n9; Potential: -6❯]",
				cycle.toString());
		assertEquals("Node n7 ", -2, stn.getG().getNode("n7").getPotential());

	}

	/**
	 * @throws Exception if the input file is not available
	 */
	@Test
	public void testBFCTWithNegativeCycle() throws Exception {

		stn.fInput = new File(fileName);

		stn.setG(graphMLReader.readGraph(stn.fInput, STNEdgeInt.class));

		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}
		final boolean consistent = STN.SSSP_BFCT(stn.g, stn.g.getZ(), null, stn.horizon, stn.checkStatus);

		assertFalse(consistent);
		final ObjectList<LabeledNode> cycle = stn.getCheckStatus().negativeCycle;
		assertNotNull(cycle);
		assertEquals("Negative cycle",
				"[❮1; Potential: 5❯, ❮Z; Potential: -1❯, ❮3; Potential: 2❯, ❮1; Potential: 5❯]", cycle.toString());

	}

	/**
	 * @throws Exception no problems
	 */
	@Test
	public void testCollapseRigidComponents() throws Exception {
		stn.setG(graphMLReader.readGraph(twoRigidComponentsSTN, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}
		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "__");

		stn.COLLAPSE_RIGID_COMPONENT(null);

		// there are two RC: <n4,n5> and <n6,n7,n9>. representative are n4 and n9.
		// n5, n6, n7 cannot have edges
		assertNull(stn.g.getNode("n5"));
//		assertEquals(0, stn.g.getIncidentEdges(stn.g.getNode("n5")).size());
		assertNull(stn.g.getNode("n6"));
//		assertEquals(0, stn.g.getIncidentEdges(stn.g.getNode("n6")).size());
		assertNull(stn.g.getNode("n7"));
//		assertEquals(0, stn.g.getIncidentEdges(stn.g.getNode("n7")).size());
		// n4 must have an edge to n9 with value -8
		assertEquals(-8, stn.g.findEdge("n4", "n9").getValue());

		// save the current STN collapsed
		final TNGraph<STNEdge> collapsed1 = stn.g;

		// other methods
		stn.setG(graphMLReader.readGraph(twoRigidComponentsSTN, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "__");
		final Object2IntMap<LabeledNode> potential = STN.GET_SSSP_BellmanFord(stn.g, stn.g.getZ(), stn.checkStatus);

		final Object2IntMap<LabeledNode> distancesFromSource = new Object2IntOpenHashMap<>();
		final TNGraph<STNEdge> zPredecessor = STN.GET_STN_PREDECESSOR_SUBGRAPH(stn.g, stn.g.getZ(), potential, distancesFromSource, null);
		assert zPredecessor != null;
		final ObjectList<ObjectList<LabeledNode>> rigidComponents = STN.GET_STRONG_CONNECTED_COMPONENTS(zPredecessor, zPredecessor.getZ());
		assertEquals("[[❮n7❯, ❮n9❯, ❮n6❯], [❮n5❯, ❮n4❯]]",
				rigidComponents.toString());
		final Pair<Object2ObjectMap<LabeledNode, LabeledNode>, Object2IntMap<LabeledNode>> repDistPair = STN.GET_REPRESENTATIVE_RIGID_COMPONENTS(rigidComponents,
				potential, stn.g.getZ());

		assertEquals(
				"{❮n7❯=>❮n9❯, ❮n9❯=>❮n9❯, ❮n6❯=>❮n9❯, ❮n5❯=>❮n4❯, ❮n4❯=>❮n4❯}",
				repDistPair.left().toString());
		assertEquals(0, repDistPair.right().getInt(stn.g.getNode("n4")));
		assertEquals(1, repDistPair.right().getInt(stn.g.getNode("n5")));
		assertEquals(0, repDistPair.right().getInt(stn.g.getNode("n9")));
		assertEquals(4, repDistPair.right().getInt(stn.g.getNode("n7")));
		assertEquals(8, repDistPair.right().getInt(stn.g.getNode("n6")));

		STN.COLLAPSE_RIGID_COMPONENT(stn.g, repDistPair.first(), repDistPair.second());
		assertEquals(-8, stn.g.findEdge("n4", "n9").getValue());

		assertEquals(collapsed1.toString(), stn.g.toString());
	}

	/**
	 * @throws Exception no problems
	 */
	@Test
	public void testGetRepRigidComponents() throws Exception {
		stn.setG(graphMLReader.readGraph(twoRigidComponentsSTN, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "PP");
		final Object2IntMap<LabeledNode> distancesFromZ = STN.GET_SSSP_BellmanFord(stn.g, stn.g.getZ(), stn.checkStatus);

		final ObjectList<ObjectList<LabeledNode>> rc = stn.getRigidComponents();
		final Instant startInstant = Instant.now();
		final Pair<Object2ObjectMap<LabeledNode, LabeledNode>, Object2IntMap<LabeledNode>> repDistPair = STN.GET_REPRESENTATIVE_RIGID_COMPONENTS(rc, distancesFromZ, stn.g.getZ());
		final Instant endInstant = Instant.now();

		assertEquals("n4", repDistPair.left().get(stn.g.getNode("n4")).getName());
		assertEquals("n4", repDistPair.left().get(stn.g.getNode("n5")).getName());
		assertEquals("n9", repDistPair.left().get(stn.g.getNode("n9")).getName());
		assertEquals("n9", repDistPair.left().get(stn.g.getNode("n7")).getName());
		assertEquals("n9", repDistPair.left().get(stn.g.getNode("n6")).getName());

		assertEquals(0, repDistPair.right().getInt(stn.g.getNode("n4")));
		assertEquals(1, repDistPair.right().getInt(stn.g.getNode("n5")));
		assertEquals(0, repDistPair.right().getInt(stn.g.getNode("n9")));
		assertEquals(4, repDistPair.right().getInt(stn.g.getNode("n7")));
		assertEquals(8, repDistPair.right().getInt(stn.g.getNode("n6")));

//		LOG.warning("\n\nDetermining the representative: "
//				+ (Duration.between(startInstant, endInstant).toNanos() / 10E6)
//				+ " ms\n\n");
	}

	/**
	 * Test GET_APSP_Johnson
	 *
	 * @throws IllegalArgumentException if anything is wrong
	 */
	@Test
	public void testJohnsonReadOnly() throws IllegalArgumentException {
		stnGraph.addEdge(new STNUEdgeInt("xy", 1), this.X, this.Y);
		stnGraph.addEdge(new STNUEdgeInt("xw", 1), this.X, this.W);
		stnGraph.addEdge(new STNUEdgeInt("zx", 1), this.Z, this.X);
		stnGraph.addEdge(new STNUEdgeInt("yw", -1), this.Y, this.W);
		stnGraph.addVertex(new LabeledNode("Ω"));
		stn.setG(stnGraph);
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}
		final TNGraph<STNEdge> finalG = STN.GET_APSP_Johnson(stn.g, stn.checkStatus);
		assertEquals("""
				%TNGraph: STNTest
				%Nodes:
				❮W❯
				❮X❯
				❮Y❯
				❮Z❯
				❮Ω❯
				%Edges:
				❮W❯--❮W-Z; internal; 0; ❯-->❮Z❯
				❮X❯--❮X-Z; internal; 0; ❯-->❮Z❯
				❮X❯--❮xw; requirement; 1; ❯-->❮W❯
				❮X❯--❮xy; requirement; 1; ❯-->❮Y❯
				❮Y❯--❮Y-Z; internal; 0; ❯-->❮Z❯
				❮Y❯--❮yw; requirement; -1; ❯-->❮W❯
				❮Z❯--❮zx; requirement; 1; ❯-->❮X❯
				❮Ω❯--❮Ω-Z; internal; 0; ❯-->❮Z❯
				""", stn.g.toString());
		assert finalG != null;
		assertEquals("""
				%TNGraph: STNTest
				%Nodes:
				❮W❯
				❮X❯
				❮Y❯
				❮Z❯
				❮Ω❯
				%Edges:
				❮W❯--❮W-X; derived; 1; ❯-->❮X❯
				❮W❯--❮W-Y; derived; 2; ❯-->❮Y❯
				❮W❯--❮W-Z; internal; 0; ❯-->❮Z❯
				❮W❯--❮W-Ω; derived; ∞; ❯-->❮Ω❯
				❮X❯--❮X-Z; internal; 0; ❯-->❮Z❯
				❮X❯--❮X-Ω; derived; ∞; ❯-->❮Ω❯
				❮X❯--❮xw; requirement; 0; ❯-->❮W❯
				❮X❯--❮xy; requirement; 1; ❯-->❮Y❯
				❮Y❯--❮Y-X; derived; 0; ❯-->❮X❯
				❮Y❯--❮Y-Z; internal; -1; ❯-->❮Z❯
				❮Y❯--❮Y-Ω; derived; ∞; ❯-->❮Ω❯
				❮Y❯--❮yw; requirement; -1; ❯-->❮W❯
				❮Z❯--❮Z-W; derived; 1; ❯-->❮W❯
				❮Z❯--❮Z-Y; derived; 2; ❯-->❮Y❯
				❮Z❯--❮Z-Ω; derived; ∞; ❯-->❮Ω❯
				❮Z❯--❮zx; requirement; 1; ❯-->❮X❯
				❮Ω❯--❮Ω-W; derived; 1; ❯-->❮W❯
				❮Ω❯--❮Ω-X; derived; 1; ❯-->❮X❯
				❮Ω❯--❮Ω-Y; derived; 2; ❯-->❮Y❯
				❮Ω❯--❮Ω-Z; internal; 0; ❯-->❮Z❯
				""", finalG.toString());
	}

	/**
	 * @throws Exception if the input file is not available
	 */
	@Test
	public void testMakeDispatchableStn20211230() throws Exception {
		stn.setG(graphMLReader.readGraph(stn20211230, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		stn.makeDispatchable();

		final ObjectSortedSet<String> undominatedEdgesMethod = new ObjectAVLTreeSet<>();
		for (final STNEdge e : stn.g.getEdges()) {
			undominatedEdgesMethod.add(e.getName());
		}

		final String expectedUndominatedEdges = "{___Z_n1, ___Z_n2, ___Z_n3, ___Z_n4, ___Z_n5, ___Z_n7, ___Z_n9, e12, e13, e21, e24, e31, e39, e45, e54, e7, e76, e79, e8, e97, n1-n4, n1-n9, n2-n9, n4-Z, n7-Z}";
		assertEquals(expectedUndominatedEdges, undominatedEdgesMethod.toString());

		// Second method
		stn.setG(graphMLReader.readGraph(stn20211230, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		final LabeledNode gZ = stn.g.getZ();

		STN.MAKE_NODES_REACHABLE_BY(stn.g, gZ, stn.horizon, "__");

		stn.makeMinimalDispatchable();
		undominatedEdgesMethod.clear();
		for (final STNEdge e : stn.g.getEdges()) {
			undominatedEdgesMethod.add(e.getName());
		}
		assertEquals(expectedUndominatedEdges, undominatedEdgesMethod.toString());

	}

	/**
	 * @throws Exception if the input file is not available
	 */
	@Test
	public void testMakeDispatchableWithRigidComponent() throws Exception {
		stn.setG(graphMLReader.readGraph(twoRigidComponentsSTN, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}
		String expectedUndominatedEdges = "{___Z_n1, ___Z_n2, ___Z_n3, ___Z_n4, ___Z_n9, e0, e1, e12, e2, e3, e4, n1-n4, n1-n9, n4-n9, n9-Z}";

		final ObjectSortedSet<String> undominatedEdgesMethod = new ObjectAVLTreeSet<>();

		// First method
		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "__");

		stn.COLLAPSE_RIGID_COMPONENT(null);
		stn.makeDispatchable();
		for (final STNEdge e : stn.g.getEdges()) {
			undominatedEdgesMethod.add(e.getName());
		}
		assertEquals(expectedUndominatedEdges, undominatedEdgesMethod.toString());

		// Second method
		stn.setG(graphMLReader.readGraph(twoRigidComponentsSTN, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		expectedUndominatedEdges = "{___Z_n1, ___Z_n2, ___Z_n3, ___Z_n4, ___Z_n9, e0, e1, e12, e2, e3, e4, n1-n4, n1-n9, n4-n5, n4-n9, n5-n4, n6-n9, n7-n9, n9-Z, n9-n6, n9-n7}";
		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "__");

		stn.makeMinimalDispatchable();
		undominatedEdgesMethod.clear();
		stn.g.getEdges().forEach((e) -> undominatedEdgesMethod.add(e.getName()));
		assertEquals(expectedUndominatedEdges, undominatedEdgesMethod.toString());
	}

	/**
	 * @throws Exception no problems
	 */
	@Test
	public void testPredecessor() throws Exception {
		stn.setG(graphMLReader.readGraph(stn20151201, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		final int n = stn.g.getVertexCount();

		final Object2ObjectMap<String, String> expected = new Object2ObjectArrayMap<>(n);
		expected.put("n1",
				"[❮n9-Z; internal; 0; ❯, ❮e0; requirement; 1; ❯, ❮e2; requirement; 1; ❯, ❮e4; requirement; -1; ❯, ❮e10; requirement; 4; ❯, ❮e12; requirement; -6; ❯, ❮e7; requirement; -1; ❯, ❮e5; requirement; 1; ❯]");
		expected.put("n2",
				"[❮e0; requirement; 1; ❯, ❮n9-Z; internal; 0; ❯, ❮e4; requirement; -1; ❯, ❮e3; requirement; 1; ❯, ❮e10; requirement; 4; ❯, ❮e12; requirement; -6; ❯, ❮e7; requirement; -1; ❯, ❮e5; requirement; 1; ❯]");
		expected.put("n3",
				"[❮e4; requirement; -1; ❯, ❮e2; requirement; 1; ❯, ❮n9-Z; internal; 0; ❯, ❮e1; requirement; 1; ❯, ❮e10; requirement; 4; ❯, ❮e12; requirement; -6; ❯, ❮e7; requirement; -1; ❯, ❮e5; requirement; 1; ❯]");
		expected.put("n4",
				"[❮e11; requirement; 4; ❯, ❮e8; requirement; 4; ❯, ❮n4-Z; internal; 0; ❯, ❮e7; requirement; -1; ❯, ❮e5; requirement; 1; ❯, ❮n6-Z; internal; 0; ❯]");
		expected.put("n5", "[❮e11; requirement; 4; ❯, ❮e8; requirement; 4; ❯, ❮e6; requirement; 1; ❯, ❮e7; requirement; -1; ❯, ❮n6-Z; internal; 0; ❯]");
		expected.put("n6", "[❮e11; requirement; 4; ❯, ❮e8; requirement; 4; ❯, ❮n6-Z; internal; 0; ❯]");
		expected.put("n7", "[❮e11; requirement; 4; ❯, ❮n7-Z; internal; 0; ❯, ❮e9; requirement; 4; ❯]");
		expected.put("n9", "[❮n9-Z; internal; 0; ❯, ❮e10; requirement; 4; ❯, ❮e9; requirement; 4; ❯]");
		expected.put("Z", "[]");

//		Object2ObjectMap<LabeledNode, TNGraph<STNEdge>> predecessor = new Object2ObjectArrayMap<>(n);

		// first method for predecessor
		Instant startInstant = Instant.now();
		for (final LabeledNode node : stn.g.getVertices()) {
			final TNGraph<STNEdge> p = stn.GET_STN_PREDECESSOR_SUBGRAPH(node);
//			predecessor.put(node, p);
			assertEquals(node.getName() + ":" + expected.get(node.getName()), node.getName() + ":" + p.getEdges());
		}
		Instant endInstant = Instant.now();
		final double TsamardinosGetPredecessorTime = (Duration.between(startInstant, endInstant).toNanos() / 10E6);
//		LOG.warning("\n\nTsamardinos getPredecessor time: "
//				+ TsamardinosGetPredecessorTime
//				+ " ms\n\n");

		// second method for predecessor
		startInstant = Instant.now();
		STN.GET_SSSP_BellmanFord(stn.g, stn.g.getZ(), stn.checkStatus);
		final Object2IntMap<LabeledNode> solution = new Object2IntOpenHashMap<>();
		for (final LabeledNode node : stn.g.getVertices()) {
			solution.put(node, node.getPotential());
		}
		final Object2IntMap<LabeledNode> distanceFromNode = new Object2IntOpenHashMap<>();
		for (final LabeledNode node : stn.g.getVertices()) {
			final TNGraph<STNEdge> p = STN.GET_STN_PREDECESSOR_SUBGRAPH(stn.g, node, solution, distanceFromNode, null);
//			predecessor.put(node, p);
			assert p != null;
			assertEquals(node.getName() + ":" + expected.get(node.getName()), node.getName() + ":" + p.getEdges());
		}
		endInstant = Instant.now();
		final double TsamardinosGetPredecessorTimeWithIST = (Duration.between(startInstant, endInstant).toNanos() / 10E6);
//		LOG.warning("\n\ngetPredecessor with initial solution time: "
//				+ TsamardinosGetPredecessorTimeWithIST
//				+ " ms\n\n");
		//for small graphs it is not always true
//		assertTrue(TsamardinosGetPredecessorTime >= TsamardinosGetPredecessorTimeWithIST);
	}

	/**
	 * @throws Exception no problems
	 */
	@Test
	public void testPredecessorSTN20211230() throws Exception {

		stn.setG(graphMLReader.readGraph(stn20211230, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "__");
		final int n = stn.g.getVertexCount();

		final Object2ObjectMap<String, String> expected = new Object2ObjectArrayMap<>(n);
		expected.put("n1",
				"[❮e45; requirement; 3; ❯, ❮e13; requirement; 1; ❯, ❮e39; requirement; -6; ❯, ❮e24; requirement; -1; ❯, ❮e12; requirement; 1; ❯, ❮n7-Z; internal; 0; ❯, ❮e97; requirement; -4; ❯, ❮e96; requirement; 0; ❯, ❮e76; requirement; 4; ❯]");
		expected.put("n2",
				"[❮e45; requirement; 3; ❯, ❮e21; requirement; 1; ❯, ❮e39; requirement; -6; ❯, ❮e24; requirement; -1; ❯, ❮n7-Z; internal; 0; ❯, ❮e97; requirement; -4; ❯, ❮e96; requirement; 0; ❯, ❮e76; requirement; 4; ❯, ❮e13; requirement; 1; ❯]");
		expected.put("n3",
				"[❮e45; requirement; 3; ❯, ❮e39; requirement; -6; ❯, ❮e31; requirement; 1; ❯, ❮e24; requirement; -1; ❯, ❮e12; requirement; 1; ❯, ❮n7-Z; internal; 0; ❯, ❮e97; requirement; -4; ❯, ❮e96; requirement; 0; ❯, ❮e76; requirement; 4; ❯]");
		expected.put("n4",
				"[❮e45; requirement; 3; ❯, ❮___Z_n1; internal; 48; ❯, ❮e8; requirement; 0; ❯, ❮n4-Z; internal; 0; ❯, ❮e7; requirement; 1; ❯, ❮e79; requirement; 5; ❯, ❮e56; requirement; 1; ❯, ❮___Z_n3; internal; 48; ❯, ❮___Z_n2; internal; 48; ❯]");
		expected.put("n5",
				"[❮___Z_n1; internal; 48; ❯, ❮e8; requirement; 0; ❯, ❮n4-Z; internal; 0; ❯, ❮e54; requirement; -2; ❯, ❮e7; requirement; 1; ❯, ❮___Z_n3; internal; 48; ❯, ❮___Z_n2; internal; 48; ❯, ❮e79; requirement; 5; ❯, ❮e56; requirement; 1; ❯]");
		expected.put("n6",
				"[❮___Z_n1; internal; 48; ❯, ❮___Z_n5; internal; 48; ❯, ❮e8; requirement; 0; ❯, ❮e54; requirement; -2; ❯, ❮n7-Z; internal; 0; ❯, ❮e79; requirement; 5; ❯, ❮___Z_n3; internal; 48; ❯, ❮___Z_n2; internal; 48; ❯, ❮n6-Z; internal; 0; ❯]");
		expected.put("n7",
				"[❮___Z_n1; internal; 48; ❯, ❮___Z_n5; internal; 48; ❯, ❮e54; requirement; -2; ❯, ❮n7-Z; internal; 0; ❯, ❮___Z_n3; internal; 48; ❯, ❮e76; requirement; 4; ❯, ❮e79; requirement; 5; ❯, ❮___Z_n2; internal; 48; ❯]");
		expected.put("n9",
				"[❮___Z_n1; internal; 48; ❯, ❮___Z_n5; internal; 48; ❯, ❮e54; requirement; -2; ❯, ❮n7-Z; internal; 0; ❯, ❮e97; requirement; -4; ❯, ❮e96; requirement; 0; ❯, ❮___Z_n3; internal; 48; ❯, ❮e76; requirement; 4; ❯, ❮___Z_n2; internal; 48; ❯]");
		expected.put("Z",
				"[❮e39; requirement; -6; ❯, ❮___Z_n1; internal; 48; ❯, ❮___Z_n5; internal; 48; ❯, ❮e54; requirement; -2; ❯, ❮e97; requirement; -4; ❯, ❮e96; requirement; 0; ❯, ❮e76; requirement; 4; ❯, ❮___Z_n3; internal; 48; ❯, ❮___Z_n2; internal; 48; ❯]");

		// first method for predecessor
		Instant startInstant = Instant.now();
		for (final LabeledNode node : stn.g.getVertices()) {
			final TNGraph<STNEdge> p = stn.GET_STN_PREDECESSOR_SUBGRAPH(node);
			assertEquals(node.getName() + ":" + expected.get(node.getName()), node.getName() + ":" + p.getEdges());
		}
		Instant endInstant = Instant.now();
		final double TsamardinosTime = (Duration.between(startInstant, endInstant).toNanos() / 10E6);
//		LOG.warning("\n\nTsamardinos getPredecessor time: "
//				+ TsamardinosTime
//				+ " ms\n\n");

		// second method for predecessor
		stn.setG(graphMLReader.readGraph(stn20211230, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		expected.put("n4",
				"[❮e45; requirement; 3; ❯, ❮___Z_n1; internal; 48; ❯, ❮e8; requirement; 0; ❯, ❮n4-Z; internal; 0; ❯, ❮e7; requirement; 1; ❯, ❮___Z_n3; internal; 48; ❯, ❮___Z_n2; internal; 48; ❯, ❮e79; requirement; 5; ❯, ❮e56; requirement; 1; ❯]");
		expected.put("n6",
				"[❮___Z_n1; internal; 48; ❯, ❮___Z_n5; internal; 48; ❯, ❮e8; requirement; 0; ❯, ❮e54; requirement; -2; ❯, ❮n7-Z; internal; 0; ❯, ❮___Z_n3; internal; 48; ❯, ❮e79; requirement; 5; ❯, ❮___Z_n2; internal; 48; ❯, ❮n6-Z; internal; 0; ❯]");

		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "__");
		startInstant = Instant.now();

		final Object2IntMap<LabeledNode> solution = STN.GET_SSSP_BellmanFord(stn.g, stn.g.getZ(), stn.checkStatus);
		final Object2IntMap<LabeledNode> distanceFromNode = new Object2IntOpenHashMap<>();
		for (final LabeledNode node : stn.g.getVertices()) {
			final TNGraph<STNEdge> p = STN.GET_STN_PREDECESSOR_SUBGRAPH(stn.g, node, solution, distanceFromNode, null);
			assert p != null;
			assertEquals(node.getName() + ":" + expected.get(node.getName()), node.getName() + ":" + p.getEdges());
		}
		endInstant = Instant.now();
		final double TsamardinosTimeOptimized = (Duration.between(startInstant, endInstant).toNanos() / 10E6);
//		LOG.warning("\n\ngetPredecessor with initial solution time: "
//				+ TsamardinosTimeOptimized
//				+ " ms\n\n");
		//for small graphs it is not always true
//		assertTrue("TsamardinosTime " + TsamardinosTime + ". TsamardinosTimeOptimized " + TsamardinosTimeOptimized, TsamardinosTime >= TsamardinosTimeOptimized);
	}

	/**
	 * @throws Exception no problems
	 */
	@Test
	public void testRigidComponentMethods() throws Exception {
		stn.setG(graphMLReader.readGraph(twoRigidComponentsSTN, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		final String expected = "[[❮n4❯, ❮n5❯], [❮n6❯, ❮n7❯, ❮n9❯]]";
		// first method for predecessor
//		Instant startInstant = Instant.now();
		final ObjectList<ObjectList<LabeledNode>> rc = stn.getRigidComponents();
//		Instant endInstant = Instant.now();
		assertEquals(expected, rc.toString());
//		LOG.warning("\n\nCormen rigid components time: "
//				+ (Duration.between(startInstant, endInstant).toNanos() / 10E6)
//				+ " ms\n\n");
	}

	/**
	 * @throws Exception no problems
	 */
	@Test
	public void testStrongConnectedComponents() throws Exception {
		stn.setG(graphMLReader.readGraph(twoRigidComponentsSTN, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}

		final String expected = "[[❮n6❯, ❮n7❯, ❮n9❯], [❮n5❯, ❮n4❯], [❮n2❯, ❮n3❯, ❮n1❯]]";
		// first method for predecessor
//		Instant startInstant = Instant.now();
		final ObjectList<ObjectList<LabeledNode>> rc = stn.GET_STRONG_CONNECTED_COMPONENTS(null);
//		Instant endInstant = Instant.now();
		assertEquals(expected, rc.toString());
//		LOG.warning("\n\nTarjan strong components time: "
//				+ (Duration.between(startInstant, endInstant).toNanos() / 10E6)
//				+ " ms\n\n");
	}

	/**
	 *
	 */
	@Test
	public void testUndominatedEdges1() {
		STNEdge e = new STNUEdgeInt("zx", 3);
		stnGraph.addEdge(e, Z, X);

		e = new STNUEdgeInt("zy", 5);
		stnGraph.addEdge(e, Z, Y);

		e = new STNUEdgeInt("zw", 4);
		stnGraph.addEdge(e, Z, W);

		e = new STNUEdgeInt("xw", 1);
		stnGraph.addEdge(e, X, W);

		e = new STNUEdgeInt("wy", 1);
		stnGraph.addEdge(e, W, Y);

		stn.setG(new TNGraph<>(stnGraph, STNEdgeInt.class));

		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e1) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e1.getMessage());
		}

		stn.makeDispatchable();

		final ObjectSortedSet<String> undominatedEdgesTsamardinos = new ObjectAVLTreeSet<>();
		for (final STNEdge e2 : stn.g.getEdges()) {
			undominatedEdgesTsamardinos.add(e2.getName());
		}

		String expectedUndominatedEdges = "{W-Z, X-Z, Y-Z, wy, xw, zx}";

		assertEquals(expectedUndominatedEdges, undominatedEdgesTsamardinos.toString());

		stn.setG(new TNGraph<>(stnGraph, STNEdgeInt.class));

		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e1) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e1.getMessage());
		}
		final ObjectArrayList<Pair<LabeledNode, STNEdge>> accumulatedUndominatedEdges = new ObjectArrayList<>();

		final Object2IntMap<LabeledNode> distanceFromSource = new Object2IntOpenHashMap<>();
		for (final LabeledNode n : stn.g.getVertices()) {
			final TNPredecessorGraph<STNEdge> predecessor = STN.GET_STN_PRECEDESSOR_SUBGRAPH_OPTIMIZED(stn.g, n, null, distanceFromSource, null);
			if (predecessor == null) {
				fail("Predecessor is null.");
			}
			accumulatedUndominatedEdges.addAll(STN.GET_UNDOMINATED_EDGES(n, predecessor, distanceFromSource, stn.g.getEdgeFactory()));
		}
		final ObjectSortedSet<String> accumulatedUndominatedEdgesSorted = new ObjectAVLTreeSet<>();

		expectedUndominatedEdges = "{W-Y, W-Z, X-W, X-Z, Y-Z, Z-X}";
		for (final Pair<LabeledNode, STNEdge> e1 : accumulatedUndominatedEdges) {
			accumulatedUndominatedEdgesSorted.add(e1.right().getName());
		}

		assertEquals(expectedUndominatedEdges, accumulatedUndominatedEdgesSorted.toString());

	}

	/**
	 * @throws Exception if the input file is not available
	 */
	@Test
	public void testUndominatedEdgesSTN20211230() throws Exception {
		stn.setG(graphMLReader.readGraph(stn20211230, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}
		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "__");

		stn.makeDispatchable();

		final ObjectSortedSet<String> undominatedEdgesTsamardinos = new ObjectAVLTreeSet<>();
		for (final STNEdge e : stn.g.getEdges()) {
			undominatedEdgesTsamardinos.add(e.getName());
		}

		String expectedUndominatedEdges = "{___Z_n1, ___Z_n2, ___Z_n3, ___Z_n4, ___Z_n5, ___Z_n7, ___Z_n9, e12, e13, e21, e24, e31, e39, e45, e54, e7, e76, e79, e8, e97, n1-n4, n1-n9, n2-n9, n4-Z, n7-Z}";

		assertEquals(expectedUndominatedEdges, undominatedEdgesTsamardinos.toString());

		final Object2ObjectMap<String, String> expected = new Object2ObjectArrayMap<>();
		expected.put("n1", "[<❮n2❯,❮n1-n2; derived; 1; ❯>, <❮n4❯,❮n1-n4; derived; 0; ❯>, <❮n3❯,❮n1-n3; derived; 1; ❯>, <❮n9❯,❮n1-n9; derived; -5; ❯>]");
		expected.put("n2", "[<❮n4❯,❮n2-n4; derived; -1; ❯>, <❮n1❯,❮n2-n1; derived; 1; ❯>, <❮n9❯,❮n2-n9; derived; -4; ❯>]");
		expected.put("n3", "[<❮n9❯,❮n3-n9; derived; -6; ❯>, <❮n1❯,❮n3-n1; derived; 1; ❯>]");
		expected.put("n4", "[<❮Z❯,❮n4-Z; derived; 0; ❯>, <❮n5❯,❮n4-n5; derived; 3; ❯>]");
		expected.put("n5", "[<❮n4❯,❮n5-n4; derived; -2; ❯>, <❮n6❯,❮n5-n6; derived; 1; ❯>]");
		expected.put("n6", "[<❮n7❯,❮n6-n7; derived; 0; ❯>]");
		expected.put("n7", "[<❮Z❯,❮n7-Z; derived; 0; ❯>, <❮n9❯,❮n7-n9; derived; 5; ❯>, <❮n6❯,❮n7-n6; derived; 4; ❯>]");
		expected.put("n9", "[<❮n7❯,❮n9-n7; derived; -4; ❯>]");
		expected.put("Z", "[<❮n2❯,❮Z-n2; derived; 48; ❯>, <❮n3❯,❮Z-n3; derived; 48; ❯>, <❮n9❯,❮Z-n9; derived; 42; ❯>," +
		                  " <❮n7❯,❮Z-n7; derived; 38; ❯>, <❮n5❯,❮Z-n5; derived; 48; ❯>, <❮n4❯,❮Z-n4; derived; 46; ❯>, <❮n1❯,❮Z-n1; derived; 48; ❯>]");
		stn.setG(graphMLReader.readGraph(stn20211230, STNEdgeInt.class));
		try {
			stn.initAndCheck();
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The STN graph has a problem, and it cannot be initialized: " + e.getMessage());
		}
		STN.MAKE_NODES_REACHABLE_BY(stn.g, stn.g.getZ(), stn.horizon, "__");
		final ObjectList<Pair<LabeledNode, STNEdge>> accumulatedUndominatedEdges = new ObjectArrayList<>();

		final Object2IntMap<LabeledNode> distanceFromSource = new Object2IntOpenHashMap<>();
		for (final LabeledNode n : stn.g.getVertices()) {
			final TNPredecessorGraph<STNEdge> predecessor = STN.GET_STN_PRECEDESSOR_SUBGRAPH_OPTIMIZED(stn.g, n, null, distanceFromSource, null);
			if (predecessor == null) {
				fail("Predecessor is null");
			}
			final ObjectList<Pair<LabeledNode, STNEdge>> undominated = STN.GET_UNDOMINATED_EDGES(n, predecessor,
					distanceFromSource, stn.g.getEdgeFactory());
			assertEquals(expected.get(n.getName()), undominated.toString());
			accumulatedUndominatedEdges.addAll(undominated);
		}
		assertEquals(undominatedEdgesTsamardinos.size(), accumulatedUndominatedEdges.size());
		final ObjectSortedSet<String> accumulatedUndominatedEdgesSorted = new ObjectAVLTreeSet<>();
		for (final Pair<LabeledNode, STNEdge> e1 : accumulatedUndominatedEdges) {
			accumulatedUndominatedEdgesSorted.add(e1.right().getName());
		}

		expectedUndominatedEdges = "{Z-n1, Z-n2, Z-n3, Z-n4, Z-n5, Z-n7, Z-n9, n1-n2, n1-n3, n1-n4, n1-n9, n2-n1, n2-n4, n2-n9, n3-n1, n3-n9, n4-Z, n4-n5, n5-n4, n5-n6, n6-n7, n7-Z, n7-n6, n7-n9, n9-n7}";
		assertEquals("Test finale", expectedUndominatedEdges, accumulatedUndominatedEdgesSorted.toString());

	}

	/**
	 * Check the velocity of stream() construct
	 */
	@Test
	public void testVelocity() {
		final TNGraph<STNEdge> g = new TNGraph<>("",stnGraph.getEdgeImplClass());

		for (int i = 0; i < 10000; i++) {
			final STNEdge e = g.getEdgeFactory().get("e" + i);
			e.setValue(i);
			g.addEdge(e, "n" + i, "n" + (i + 1));
		}

		final TNGraph<STNEdge> gCopy = new TNGraph<>(g, g.getEdgeImplClass());

		assertEquals(10000, g.getEdgeCount());
		assertEquals(10001, g.getVertexCount());

		Instant startInstant = Instant.now();
		for (final STNEdge e : g.getEdges()) {
			if (e.getValue() % 2 == 0)
				g.removeEdge(e);
		}
		Instant endInstant = Instant.now();
		final double forTime = Duration.between(startInstant, endInstant).toNanos() / 10E6;
//		LOG.warning("\n\nClassic (for cycle) method time for removing 500 edges having even weight: "
//				+ forTime
//				+ " ms\n\n");

		startInstant = Instant.now();
		gCopy.getEdges().stream().filter((e) -> e.getValue() % 2 == 0).forEach(gCopy::removeEdge);
		endInstant = Instant.now();
		final double streamTime = Duration.between(startInstant, endInstant).toNanos() / 10E6;
//		LOG.warning("\n\nStream method time for removing 500 edges having even weight: "
//				+ streamTime
//				+ " ms\n\n");
		//It is not always true that forTime >= streamTime. For example: when the source code was just compiled. But, then, it is true!
		if (forTime < streamTime) {
			//maybe the class was just compiled, we re-try at risk to create an infinite loop ;-)
			testVelocity();
			return;
		}
		assertTrue("With for " + forTime + ". With stream " + streamTime, forTime >= streamTime);
	}

}
