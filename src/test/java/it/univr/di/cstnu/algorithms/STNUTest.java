/**
 *
 */
package it.univr.di.cstnu.algorithms;

import it.unimi.dsi.fastutil.objects.*;
import it.univr.di.cstnu.graph.*;
import it.univr.di.labeledvalue.ALabelAlphabet;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class STNUTest {

	/**
	 *
	 */
	static final String fileNameSTNUBasic = "src/test/resources/testGraphML.stnu";
	static final String fileNameSTNUWithInducedVCEdgeByRigidComponent = "src/test/resources/1000_025OK.stnu";
	static final String fileNameSTNUWithComplicatedRigidComponent = "src/test/resources/1000_004OK.stnu";
	static final String fileNameSTNUWithRCInducedByMaxMinEdge = "src/test/resources/stnuWithRCInducedByMaxMinEdge.stnu";
	static final String fileNameSTNUnotDC002 = "src/test/resources/notDC002.stnu";
	static final String fileNameSTNUnotDC020 = "src/test/resources/notDC020.stnu";
	static final String fileNameSTNUnotDC033 = "src/test/resources/notDC033.stnu";


	/**
	 *
	 */
	TNGraphMLReader<STNUEdge> readerSTNU;

	/**
	 *
	 */
	TNGraph<STNUEdge> stnuGraph;

	/**
	 *
	 */
	STNU stnu;

	private LabeledNode X, Ω, Y, Z;

	/**
	 * @throws java.lang.Exception nope
	 */
	@Before
	public void setUp() throws Exception {
		this.readerSTNU = new TNGraphMLReader<>();
		this.stnuGraph = this.readerSTNU.readGraph(new File(fileNameSTNUBasic), STNUEdgeInt.class);
		this.X = this.stnuGraph.getNode("X");
		this.Ω = this.stnuGraph.getNode("Ω");
		this.Y = this.stnuGraph.getNode("Y");
		this.Z = this.stnuGraph.getZ();
		this.stnuGraph.addEdge(new STNUEdgeInt("ΩY", -4), this.Ω, this.Y);
		this.stnuGraph.addEdge(new STNUEdgeInt("XZ", -4), this.X, this.Z);
		this.stnuGraph.addEdge(new STNUEdgeInt("YZ", -7), this.Y, this.Z);
		this.stnuGraph.addEdge(new STNUEdgeInt("XΩ", 10), this.X, this.Ω);
		this.stnuGraph.addEdge(new STNUEdgeInt("ZΩ", 11), this.Z, this.Ω);
		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	@Test
	public void testBellmanFordOL() {
		this.testGraph();
		Object2IntMap<LabeledNode> potential = STNU.GET_SSSP_BellmanFordOL(this.stnu.getG(), this.stnu.getCheckStatus());
		final Object2IntMap<LabeledNode> expected = new Object2IntOpenHashMap<>();

		expected.put(this.Y, 7);
		expected.put(this.Ω, 11);
		expected.put(this.Z, 0);
		expected.put(this.X, 5);
		assertEquals("BellmanFord", 4, expected.size());
		assertEquals("BellmanFord", expected, potential);

		this.stnuGraph.addEdge(new STNUEdgeInt("ΩX", -8), this.Ω, this.X);
		potential = STNU.GET_SSSP_BellmanFordOL(this.stnu.getG(), this.stnu.getCheckStatus());
		assertNull("BellmanFord", potential);
	}

	/**
	 * Test case for FD
	 */
	@Test
	public void testFDWithNegativeAddedToAWait() {
		this.makeSTNUFig8AFD_STNUPaper();
		//add a negative edge between Y and A
		STNUEdge YA = this.stnuGraph.findEdge("Y", "A");
		if (YA == null) {
			YA = this.stnuGraph.makeNewEdge("Y-A", Edge.ConstraintType.requirement);
			this.stnuGraph.addEdge(YA, "Y", "A");
		}
		YA.setValue(-2);

		this.stnu.applyFastDispatchSTNU(true);
		final String result = """
		                      %TNGraph: Fig8AFD_STNUPaper
		                      %Nodes:
		                      ❮A❯
		                      ❮C❯
		                      ❮X❯
		                      ❮Y❯
		                      ❮Z❯
		                      %Edges:
		                      ❮A❯--❮A_Z; derived; -6; ❯-->❮Z❯
		                      ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                      ❮C❯--❮C-Y; derived; 1; ❯-->❮Y❯
		                      ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                      ❮C❯--❮CX; requirement; 3; ❯-->❮X❯
		                      ❮X❯--❮XY; requirement; -2; ❯-->❮Y❯
		                      ❮Y❯--❮Y-A; requirement; -2; UC(C):-9❯-->❮A❯
		                      ❮Y❯--❮YC; requirement; 1; ❯-->❮C❯
		                      """;
		assertEquals(result, this.stnuGraph.toString());
	}

	/**
	 * Test FD_STNU algorithm
	 */
	@Test
	public void testFD_STNU() {
		this.makeSTNUFig8AFD_STNUPaper();
		this.stnu.applyFastDispatchSTNU(true);
//		LabeledNode A = this.stnuGraph.getNode("A");
//		LabeledNode C = this.stnuGraph.getNode("C");
//		assertEquals("❮AC; contingent; LC(C):1❯",this.stnuGraph.findEdge(A, C).toString());
//		assertEquals("❮A-Z; derived; -6; ❯", this.stnuGraph.findEdge(A, Z).toString());
//		assertEquals("❮CA; contingent; UC(C):-10❯", this.stnuGraph.findEdge(C, A).toString());
//		assertEquals("❮C-Y; derived; 1; ❯", this.stnuGraph.findEdge(C, Y).toString());
//		assertEquals("❮CX; requirement; 3; ❯", this.stnuGraph.findEdge(C, X).toString());
//		assertEquals("❮CZ; requirement; -7; ❯", this.stnuGraph.findEdge(C, Z).toString());
//		assertEquals("❮CZ; requirement; -7; ❯", this.stnuGraph.findEdge(C, Z).toString());
		assertEquals("""
		             %TNGraph: Fig8AFD_STNUPaper
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             %Edges:
		             ❮A❯--❮A_Z; derived; -6; ❯-->❮Z❯
		             ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		             ❮C❯--❮C-Y; derived; 1; ❯-->❮Y❯
		             ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		             ❮C❯--❮CX; requirement; 3; ❯-->❮X❯
		             ❮C❯--❮CZ; requirement; -7; ❯-->❮Z❯
		             ❮X❯--❮XY; requirement; -2; ❯-->❮Y❯
		             ❮Y❯--❮Y-A; derived; UC(C):-9❯-->❮A❯
		             ❮Y❯--❮Y_Z; derived; -6; ❯-->❮Z❯
		             ❮Y❯--❮YC; requirement; 1; ❯-->❮C❯
		             """, this.stnuGraph.toString());
		//			❮X❯--❮X-A; derived; UC(C):-11❯-->❮A❯ is removed if the applyFastDispatchSTNU is optimized.
	}

	/**
	 * Test case for Morris2014Dispatchable
	 */
	@Test
	public void testFD_STNUWithNegativeAddedToAWait() {
		this.makeSTNUFig8AFD_STNUPaper();
		//add a negative edge between Y and A
		final LabeledNode W = new LabeledNode("W");
		this.stnuGraph.addVertex(W);

		final STNUEdge WC = this.stnuGraph.makeNewEdge("W-C", Edge.ConstraintType.requirement);
		this.stnuGraph.addEdge(WC, "W", "C");
		WC.setValue(0);

		this.stnu.applyFastDispatchSTNU(true);
		final String result = """
		                      %TNGraph: Fig8AFD_STNUPaper
		                      %Nodes:
		                      ❮A❯
		                      ❮C❯
		                      ❮W❯
		                      ❮X❯
		                      ❮Y❯
		                      ❮Z❯
		                      %Edges:
		                      ❮A❯--❮A_Z; derived; -6; ❯-->❮Z❯
		                      ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                      ❮C❯--❮C-Y; derived; 1; ❯-->❮Y❯
		                      ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                      ❮C❯--❮CX; requirement; 3; ❯-->❮X❯
		                      ❮C❯--❮CZ; requirement; -7; ❯-->❮Z❯
		                      ❮W❯--❮W-A; derived; UC(C):-10❯-->❮A❯
		                      ❮W❯--❮W-C; requirement; 0; ❯-->❮C❯
		                      ❮W❯--❮W-Z; derived; -7; ❯-->❮Z❯
		                      ❮X❯--❮XY; requirement; -2; ❯-->❮Y❯
		                      ❮Y❯--❮Y-A; derived; UC(C):-9❯-->❮A❯
		                      ❮Y❯--❮Y_Z; derived; -6; ❯-->❮Z❯
		                      ❮Y❯--❮YC; requirement; 1; ❯-->❮C❯
		                      """;
		assertEquals(result, this.stnuGraph.toString());
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.algorithms.STNU#getActivationNodeMap()}.
	 */
	@Test
	public void testGetActivationNode() {
		this.testGraph();
		assert this.stnu.getActivationNodeMap() != null;
		final LabeledNode x = this.stnu.getActivationNodeMap().get(this.Y);
		assertEquals("Activation  node", this.X, x);
		assertNull("Activation  node", this.stnu.getActivationNodeMap().get(this.X));
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.algorithms.STNU#getLowerCaseEdgesMap()}.
	 */
	@Test
	public void testGetLowerContingentEdge() {
		this.testGraph();
		assert this.stnu.getLowerCaseEdgesMap() != null;
		final STNUEdge lce = this.stnu.getLowerCaseEdgesMap().get(this.Y);
		assertEquals("Lower case edge", "❮XY; contingent; LC(Y):2❯", lce.toString());
		assertEquals("Lower case edge", 1, this.stnu.getLowerCaseEdgesMap().size());
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.algorithms.STNU#getUpperCaseEdgesMap()}.
	 */
	@Test
	public void testGetUpperContingentEdge() {
		this.testGraph();
		assert this.stnu.getUpperCaseEdgesMap() != null;
		final STNUEdge lce = this.stnu.getUpperCaseEdgesMap().get(this.Y);
		assertEquals("Upper case edge", "❮YX; contingent; UC(Y):-5❯", lce.toString());
		assert this.stnu.getLowerCaseEdgesMap() != null;
		assertEquals("Upper case edge", 1, this.stnu.getLowerCaseEdgesMap().size());
	}

	/**
	 *
	 */
	@Test
	public void testGraph() {
		final String graph = """
		                     %TNGraph: testGraphML
		                     %Nodes:
		                     ❮X❯
		                     ❮Y❯
		                     ❮Z❯
		                     ❮Ω❯
		                     %Edges:
		                     ❮X❯--❮XY; contingent; LC(Y):2❯-->❮Y❯
		                     ❮X❯--❮XZ; requirement; -4; ❯-->❮Z❯
		                     ❮X❯--❮XΩ; requirement; 10; ❯-->❮Ω❯
		                     ❮Y❯--❮YX; contingent; UC(Y):-5❯-->❮X❯
		                     ❮Y❯--❮YZ; requirement; -7; ❯-->❮Z❯
		                     ❮Z❯--❮ZΩ; requirement; 11; ❯-->❮Ω❯
		                     ❮Ω❯--❮Ω_Z; derived; 0; ❯-->❮Z❯
		                     ❮Ω❯--❮ΩY; requirement; -4; ❯-->❮Y❯
		                     """;
		assertEquals(graph, this.stnuGraph.toString());
	}

	/**
	 * (W,0,C) + (C,C:-10,A) generates a wait:  (W,C:-10,A) and we talked about two options: (1) keep the wait and discard the 0 edge into C (2) keep the zero
	 * edge and discard the wait both seem reasonable except that option 2 violates the vee-path completeness property.  In this simple example if we discard
	 * the wait, then there is no vee-path from W to A. so, I think option 1 is the only viable option.  But that means that the condition for removing a wait
	 * must be that there is a negative edge or path from W to C.
	 */
	@Test
	public void testLuke20240102About0EdgetoC() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("XC", 0), this.X, C);
		e = new STNUEdgeInt("XA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		this.stnuGraph.addEdge(e, X, A);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyFastDispatchSTNU(true);//Morris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Simple
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Z❯
		             %Edges:
		             ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		             ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		             ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		             ❮X❯--❮XA; requirement; UC(C):-10❯-->❮A❯
		             """, this.stnuGraph.toString());

//	        ❮X❯--❮X-A; derived; -1; ❯-->❮A❯ THESE two can replace  ❮X❯--❮XA; requirement; UC(C):-10❯-->❮A❯
//			❮X❯--❮XC; requirement; 0; ❯-->❮C❯
	}

	/**
	 * Test case for FD
	 */
	@Test
	public void testMinDispESTNUWithNegativeAddedToAWait() {
		this.makeSTNUFig8AFD_STNUPaper();
		//add a negative edge between Y and A
		STNUEdge YA = this.stnuGraph.findEdge("Y", "A");
		if (YA == null) {
			YA = this.stnuGraph.makeNewEdge("Y-A", Edge.ConstraintType.requirement);
			this.stnuGraph.addEdge(YA, "Y", "A");
		}
		YA.setValue(-2);
		final String result = """
		                      %TNGraph: Fig8AFD_STNUPaper
		                      %Nodes:
		                      ❮A❯
		                      ❮C❯
		                      ❮X❯
		                      ❮Y❯
		                      ❮Z❯
		                      %Edges:
		                      ❮A❯--❮A_Z; derived; -6; ❯-->❮Z❯
		                      ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                      ❮C❯--❮C-Y; derived; 1; ❯-->❮Y❯
		                      ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                      ❮C❯--❮CX; requirement; 3; ❯-->❮X❯
		                      ❮X❯--❮XY; requirement; -2; ❯-->❮Y❯
		                      ❮Y❯--❮Y-A; requirement; -2; UC(C):-9❯-->❮A❯
		                      ❮Y❯--❮YC; requirement; 1; ❯-->❮C❯
		                      """;
		//FD
		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();

		//Morris
		this.makeSTNUFig8AFD_STNUPaper();
		//add a negative edge between Y and A
		YA = this.stnuGraph.findEdge("Y", "A");
		if (YA == null) {
			YA = this.stnuGraph.makeNewEdge("Y-A", Edge.ConstraintType.requirement);
			this.stnuGraph.addEdge(YA, "Y", "A");
		}
		YA.setValue(-2);
		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();

		assertEquals(result, this.stnuGraph.toString());
	}

	/**
	 * Test minDispESTNU algorithm
	 */
	@Test
	public void testMinDistESTNU() {
		this.makeSTNUFig8AFD_STNUPaper();
		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Fig8AFD_STNUPaper
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             %Edges:
		             ❮A❯--❮A_Z; derived; -6; ❯-->❮Z❯
		             ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		             ❮C❯--❮C-Y; derived; 1; ❯-->❮Y❯
		             ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		             ❮C❯--❮CX; requirement; 3; ❯-->❮X❯
		             ❮X❯--❮XY; requirement; -2; ❯-->❮Y❯
		             ❮Y❯--❮Y-A; derived; UC(C):-9❯-->❮A❯
		             """, this.stnuGraph.toString());
	}

	/**
	 * Test minDispESTNU algorithm
	 */
	@Test
	public void testMinDistESTNU20231219() {
		final String expected = """
		                        %TNGraph: STNULuke20231219
		                        %Nodes:
		                        ❮A❯
		                        ❮A1❯
		                        ❮C❯
		                        ❮C1❯
		                        ❮Z❯
		                        %Edges:
		                        ❮A1❯--❮A1-A; derived; UC(C):-6❯-->❮A❯
		                        ❮A1❯--❮A1C1; contingent; LC(C1):1❯-->❮C1❯
		                        ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		                        ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                        ❮C1❯--❮C1A1; contingent; UC(C1):-10❯-->❮A1❯
		                        ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                        """;
		this.makeSTNULuke20231219();
		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());
		this.makeSTNULuke20231219();
		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNU20231220() {
		this.makeSTNULuke20231220();
		assertEquals("""
		             %TNGraph: STNULuke20231220
		             %Nodes:
		             ❮A❯
		             ❮A1❯
		             ❮A2❯
		             ❮C❯
		             ❮C1❯
		             ❮C2❯
		             ❮W❯
		             ❮Y❯
		             ❮Z❯
		             %Edges:
		             ❮A1❯--❮A1_Z; derived; 0; ❯-->❮Z❯
		             ❮A1❯--❮A1C1; contingent; LC(C1):1❯-->❮C1❯
		             ❮A2❯--❮A2_Z; derived; 0; ❯-->❮Z❯
		             ❮A2❯--❮A2A1; requirement; 1; ❯-->❮A1❯
		             ❮A2❯--❮A2C2; contingent; LC(C2):1❯-->❮C2❯
		             ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		             ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		             ❮C1❯--❮C1_Z; derived; 0; ❯-->❮Z❯
		             ❮C1❯--❮C1A1; contingent; UC(C1):-10❯-->❮A1❯
		             ❮C1❯--❮C1C; requirement; 3; ❯-->❮C❯
		             ❮C2❯--❮C2_Z; derived; 0; ❯-->❮Z❯
		             ❮C2❯--❮C2A2; contingent; UC(C2):-10❯-->❮A2❯
		             ❮C2❯--❮C2Y; requirement; 6; ❯-->❮Y❯
		             ❮C❯--❮C_Z; derived; 0; ❯-->❮Z❯
		             ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		             ❮C❯--❮CY; requirement; 8; ❯-->❮Y❯
		             ❮W❯--❮W_Z; derived; 0; ❯-->❮Z❯
		             ❮W❯--❮WC2; requirement; 4; ❯-->❮C2❯
		             ❮W❯--❮WY; requirement; 8; ❯-->❮Y❯
		             ❮Y❯--❮Y_Z; derived; 0; ❯-->❮Z❯
		             """, this.stnuGraph.toString());

		final String expected = """
		                        %TNGraph: STNULuke20231220
		                        %Nodes:
		                        ❮A❯
		                        ❮A1❯
		                        ❮A2❯
		                        ❮C❯
		                        ❮C1❯
		                        ❮C2❯
		                        ❮W❯
		                        ❮Y❯
		                        ❮Z❯
		                        %Edges:
		                        ❮A1❯--❮A1-A; derived; UC(C):-6❯-->❮A❯
		                        ❮A1❯--❮A1C1; contingent; LC(C1):1❯-->❮C1❯
		                        ❮A2❯--❮A2-A; derived; UC(C):-5❯-->❮A❯
		                        ❮A2❯--❮A2A1; requirement; 1; ❯-->❮A1❯
		                        ❮A2❯--❮A2C2; contingent; LC(C2):1❯-->❮C2❯
		                        ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		                        ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                        ❮C1❯--❮C1A1; contingent; UC(C1):-10❯-->❮A1❯
		                        ❮C2❯--❮C2A2; contingent; UC(C2):-10❯-->❮A2❯
		                        ❮C2❯--❮C2Y; requirement; 6; ❯-->❮Y❯
		                        ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                        ❮C❯--❮CY; requirement; 8; ❯-->❮Y❯
		                        ❮W❯--❮W-A2; derived; UC(C2):-6❯-->❮A2❯
		                        ❮Y❯--❮Y_Z; derived; 0; ❯-->❮Z❯
		                        """;
		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(1, this.stnu.getCheckStatus().maxMinConstraint);

		assertEquals(expected, this.stnuGraph.toString());

		this.makeSTNULuke20231220();
		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(1, this.stnu.getCheckStatus().maxMinConstraint);

		assertEquals(expected, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNUSimple() {
		this.makeMinDistESTNUSimple();
		final String expected = """
		                        %TNGraph: Simple
		                        %Nodes:
		                        ❮A❯
		                        ❮C❯
		                        ❮X❯
		                        ❮Y❯
		                        ❮Z❯
		                        %Edges:
		                        ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		                        ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                        ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                        ❮X❯--❮X-A; derived; UC(C):-5❯-->❮A❯
		                        ❮Y❯--❮Y-A; derived; 90; ❯-->❮A❯
		                        ❮Y❯--❮Y_Z; derived; 0; ❯-->❮Z❯
		                        """;
		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());

		this.makeMinDistESTNUSimple();
		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());
	}

	/**
	 * If X--0-->C, then we decided that the wait X--C-10-->A must be present.
	 */
	@Test
	public void testMinDistESTNUSimpleX02C() {
		this.makeMinDistESTNUX02C();
		final String expected = """
		                        %TNGraph: Simple
		                        %Nodes:
		                        ❮A❯
		                        ❮C❯
		                        ❮X❯
		                        ❮Z❯
		                        %Edges:
		                        ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		                        ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                        ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                        ❮X❯--❮X-A; derived; UC(C):-10❯-->❮A❯
		                        """;
//				❮X❯--❮X-A; derived; -1; ❯-->❮A❯ These two constraints can replace ❮X❯--❮X-A; derived; UC(C):-10❯-->❮A❯
//				❮X❯--❮XC; requirement; 0; ❯-->❮C❯

		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());

		this.makeMinDistESTNUX02C();
		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNUWithActivationInRigidComponent0DistanceMorris() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.stnuGraph.addVertex(this.Y);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("XA", 0), this.X, A);
		this.stnuGraph.addEdge(new STNUEdgeInt("AX", 0), A, this.X);
		this.stnuGraph.addEdge(new STNUEdgeInt("YC", 5), this.Y, C);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Simple
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             %Edges:
		             ❮A❯--❮A-X; derived; 0; ❯-->❮X❯
		             ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		             ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		             ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		             ❮X❯--❮X-A; derived; 0; ❯-->❮A❯
		             ❮Y❯--❮Y-A; derived; UC(C):-5❯-->❮A❯
		             """, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNUWithActivationInRigidComponent0distanceFD() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.stnuGraph.addVertex(this.Y);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("XA", 0), this.X, A);
		this.stnuGraph.addEdge(new STNUEdgeInt("AX", 0), A, this.X);
		this.stnuGraph.addEdge(new STNUEdgeInt("YC", 5), this.Y, C);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Simple
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             %Edges:
		             ❮A❯--❮A-X; derived; 0; ❯-->❮X❯
		             ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		             ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		             ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		             ❮X❯--❮X-A; derived; 0; ❯-->❮A❯
		             ❮Y❯--❮Y-A; derived; UC(C):-5❯-->❮A❯
		             """, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNUWithActivationInRigidComponentFD() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.stnuGraph.addVertex(this.Y);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("XA", 10), this.X, A);
		this.stnuGraph.addEdge(new STNUEdgeInt("AX", -10), A, this.X);
		this.stnuGraph.addEdge(new STNUEdgeInt("YC", 5), this.Y, C);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Simple
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             %Edges:
		             ❮A❯--❮A-X; derived; -10; ❯-->❮X❯
		             ❮C❯--❮C-X; contingent; UC(C):-20❯-->❮X❯
		             ❮X❯--❮X-A; derived; 10; ❯-->❮A❯
		             ❮X❯--❮X-C; contingent; LC(C):11❯-->❮C❯
		             ❮X❯--❮X_Z; derived; 0; ❯-->❮Z❯
		             ❮Y❯--❮Y-X; derived; UC(C):-15❯-->❮X❯
		             """, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNUWithActivationInRigidComponentFD1() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.stnuGraph.addVertex(this.Y);
		this.stnuGraph.addVertex(this.Ω);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("XA", 2), this.X, A);//rigid component
		this.stnuGraph.addEdge(new STNUEdgeInt("AX", -2), A, this.X);
		this.stnuGraph.addEdge(new STNUEdgeInt("YC", 6), this.Y, C);
		this.stnuGraph.addEdge(new STNUEdgeInt("ΩC", 8), this.Ω, C);
		this.stnuGraph.addEdge(new STNUEdgeInt("CX", -3), C, X);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Simple
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             ❮Ω❯
		             %Edges:
		             ❮A❯--❮A-X; derived; -2; ❯-->❮X❯
		             ❮C❯--❮CX; contingent; UC(C):-12❯-->❮X❯
		             ❮X❯--❮X-A; derived; 2; ❯-->❮A❯
		             ❮X❯--❮X-C; contingent; LC(C):3❯-->❮C❯
		             ❮X❯--❮X_Z; derived; 0; ❯-->❮Z❯
		             ❮Y❯--❮Y-X; derived; UC(C):-6❯-->❮X❯
		             ❮Ω❯--❮Ω-X; derived; UC(C):-4❯-->❮X❯
		             """, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNUWithActivationInRigidComponentMorris() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.stnuGraph.addVertex(this.Y);
		this.stnuGraph.addVertex(this.Ω);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("XA", 2), this.X, A);//rigid component
		this.stnuGraph.addEdge(new STNUEdgeInt("AX", -2), A, this.X);
		this.stnuGraph.addEdge(new STNUEdgeInt("YC", 6), this.Y, C);
		this.stnuGraph.addEdge(new STNUEdgeInt("ΩC", 8), this.Ω, C);
		this.stnuGraph.addEdge(new STNUEdgeInt("CX", -3), C, X);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Simple
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             ❮Ω❯
		             %Edges:
		             ❮A❯--❮A-X; derived; -2; ❯-->❮X❯
		             ❮C❯--❮CX; contingent; UC(C):-12❯-->❮X❯
		             ❮X❯--❮X-A; derived; 2; ❯-->❮A❯
		             ❮X❯--❮X-C; contingent; LC(C):3❯-->❮C❯
		             ❮X❯--❮X_Z; derived; 0; ❯-->❮Z❯
		             ❮Y❯--❮Y-X; derived; UC(C):-6❯-->❮X❯
		             ❮Ω❯--❮Ω-X; derived; UC(C):-4❯-->❮X❯
		             """, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNUWithActivationInRigidComponentMorris1() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.stnuGraph.addVertex(this.Y);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("XA", 10), this.X, A);
		this.stnuGraph.addEdge(new STNUEdgeInt("AX", -10), A, this.X);
		this.stnuGraph.addEdge(new STNUEdgeInt("YC", 5), this.Y, C);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Simple
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             %Edges:
		             ❮A❯--❮A-X; derived; -10; ❯-->❮X❯
		             ❮C❯--❮C-X; contingent; UC(C):-20❯-->❮X❯
		             ❮X❯--❮X-A; derived; 10; ❯-->❮A❯
		             ❮X❯--❮X-C; contingent; LC(C):11❯-->❮C❯
		             ❮X❯--❮X_Z; derived; 0; ❯-->❮Z❯
		             ❮Y❯--❮Y-X; derived; UC(C):-15❯-->❮X❯
		             """, this.stnuGraph.toString());
	}

	/**
	 * MorrisDispatchable makes a weak wait (V, C:-v, A) and companion edge (V,C) that must be overwritten by a rigid component collapsing. A wrong collapsing
	 * management would break the final result
	 *
	 * @throws IOException                  if the external file is not readable.
	 * @throws ParserConfigurationException other exception
	 * @throws SAXException                 other exception
	 */
	@Test
	public void testMinDistESTNUWithComplicatedRigidComponent() throws Exception {
		this.stnuGraph = this.readerSTNU.readGraph(new File(fileNameSTNUWithComplicatedRigidComponent), STNUEdgeInt.class);
		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final String expected = """
		                        %TNGraph: 1000_004_7.stnu
		                        %Nodes:
		                        ❮A44❯
		                        ❮A64❯
		                        ❮C44❯
		                        ❮C64❯
		                        ❮N347❯
		                        ❮N348❯
		                        ❮N349❯
		                        ❮N507❯
		                        ❮N508❯
		                        ❮N509❯
		                        ❮N667❯
		                        ❮N668❯
		                        ❮Z❯
		                        %Edges:
		                        ❮A44❯--❮A44-N347; derived; -107; ❯-->❮N347❯
		                        ❮A44❯--❮A44-N348; requirement; 0; ❯-->❮N348❯
		                        ❮A64❯--❮A64_Z; derived; 0; ❯-->❮Z❯
		                        ❮C44❯--❮C44-N349; requirement; 116; ❯-->❮N349❯
		                        ❮C64❯--❮C64-N667; derived; 35; ❯-->❮N667❯
		                        ❮A44❯--❮EA44-C44; contingent; LC(C44):1❯-->❮C44❯
		                        ❮A64❯--❮EA64-C64; contingent; LC(C64):14❯-->❮C64❯
		                        ❮C44❯--❮EC44-A44; contingent; UC(C44):-2❯-->❮A44❯
		                        ❮C64❯--❮EC64-A64; contingent; UC(C64):-16❯-->❮A64❯
		                        ❮N347❯--❮N347-N348; requirement; 110; ❯-->❮N348❯
		                        ❮N347❯--❮N347_Z; derived; 0; ❯-->❮Z❯
		                        ❮N348❯--❮N348-A44; requirement; 142; ❯-->❮A44❯
		                        ❮N348❯--❮N348-N347; requirement; -107; ❯-->❮N347❯
		                        ❮N348❯--❮N348-N667; derived; 312; ❯-->❮N667❯
		                        ❮N349❯--❮N349-C44; requirement; -25; ❯-->❮C44❯
		                        ❮N507❯--❮N507-N667; derived; -89; ❯-->❮N667❯
		                        ❮N508❯--❮N508-N667; derived; -229; ❯-->❮N667❯
		                        ❮N509❯--❮N509-N667; derived; -236; ❯-->❮N667❯
		                        ❮N667❯--❮N667-A44; derived; 207; ❯-->❮A44❯
		                        ❮N667❯--❮N667-A64; derived; 2; ❯-->❮A64❯
		                        ❮N667❯--❮N667-N347; derived; 100; ❯-->❮N347❯
		                        ❮N667❯--❮N667-N349; derived; 234; ❯-->❮N349❯
		                        ❮N667❯--❮N667-N507; derived; 89; ❯-->❮N507❯
		                        ❮N667❯--❮N667-N508; derived; 229; ❯-->❮N508❯
		                        ❮N667❯--❮N667-N509; derived; 353; ❯-->❮N509❯
		                        ❮N667❯--❮N667-N668; derived; 86; ❯-->❮N668❯
		                        ❮N667❯--❮N667_Z; derived; 0; ❯-->❮Z❯
		                        ❮N668❯--❮N668-N667; derived; -86; ❯-->❮N667❯
		                        """;

		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());

		this.stnuGraph = this.readerSTNU.readGraph(new File(fileNameSTNUWithComplicatedRigidComponent), STNUEdgeInt.class);
		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());
	}

	/**
	 * MorrisDispatchable makes a weak wait (V, C:-v, A) and companion edge (V,C) that must be overwritten by a rigid component collapsing. A wrong collapsing
	 * management would break the final result
	 *
	 * @throws IOException                  if the external file is not readable.
	 * @throws ParserConfigurationException other exception
	 * @throws SAXException                 other exception
	 */
	@Test
	public void testMinDistESTNUWithConstraintVCInducedByRigidComponent() throws Exception {
		this.stnuGraph = this.readerSTNU.readGraph(new File(fileNameSTNUWithInducedVCEdgeByRigidComponent), STNUEdgeInt.class);
		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: 1000_025_1.stnu
		             %Nodes:
		             ❮A64❯
		             ❮C64❯
		             ❮N34❯
		             ❮N507❯
		             ❮X1❯
		             ❮Z❯
		             %Edges:
		             ❮A64❯--❮A64_Z; derived; 0; ❯-->❮Z❯
		             ❮C64❯--❮C64-N507; derived; 106; ❯-->❮N507❯
		             ❮A64❯--❮EA64-C64; contingent; LC(C64):10❯-->❮C64❯
		             ❮C64❯--❮EC64-A64; contingent; UC(C64):-17❯-->❮A64❯
		             ❮N34❯--❮eN34-X1; requirement; 4; ❯-->❮X1❯
		             ❮X1❯--❮eX1-C64; requirement; -2; ❯-->❮C64❯
		             ❮N34❯--❮N34-C64; derived; -166; ❯-->❮C64❯
		             ❮N507❯--❮N507-C64; derived; -106; ❯-->❮C64❯
		             """, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testMinDistESTNUWithOLengthRigidComponent() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.stnuGraph.addVertex(this.Y);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);


		this.stnuGraph.addEdge(new STNUEdgeInt("XC", 5), this.X, C);
		this.stnuGraph.addEdge(new STNUEdgeInt("XY", 0), this.Y, this.X);
		this.stnuGraph.addEdge(new STNUEdgeInt("YX", 0), this.X, this.Y);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}

		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals("""
		             %TNGraph: Simple
		             %Nodes:
		             ❮A❯
		             ❮C❯
		             ❮X❯
		             ❮Y❯
		             ❮Z❯
		             %Edges:
		             ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		             ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		             ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		             ❮X❯--❮X-A; derived; UC(C):-5❯-->❮A❯
		             ❮X❯--❮X-Y; derived; 0; ❯-->❮Y❯
		             ❮Y❯--❮Y-X; derived; 0; ❯-->❮X❯
		             """, this.stnuGraph.toString());
	}

	/**
	 * @throws IOException                  if the external file is not readable.
	 * @throws ParserConfigurationException other exception
	 * @throws SAXException                 other exception
	 */
	@Test
	public void testMinDistESTNUWithSTNUWithRCInducedByMaxMinEdge() throws Exception {
		this.stnuGraph = this.readerSTNU.readGraph(new File(fileNameSTNUWithRCInducedByMaxMinEdge), STNUEdgeInt.class);
		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final String expected = """
		                        %TNGraph: lukeExample20231229.stnu
		                        %Nodes:
		                        ❮A❯
		                        ❮C❯
		                        ❮V❯
		                        ❮W❯
		                        ❮Z❯
		                        %Edges:
		                        ❮A❯--❮A-V; derived; 6; ❯-->❮V❯
		                        ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		                        ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                        ❮C❯--❮C-V; derived; 0; ❯-->❮V❯
		                        ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                        ❮V❯--❮V-A; derived; UC(C):-6❯-->❮A❯
		                        ❮V❯--❮V-W; derived; 5; ❯-->❮W❯
		                        ❮W❯--❮W-V; derived; -5; ❯-->❮V❯
		                        """;

		this.stnu.applyMorris2014Dispatchable();
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());

		this.stnuGraph = this.readerSTNU.readGraph(new File(fileNameSTNUWithRCInducedByMaxMinEdge), STNUEdgeInt.class);
		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		this.stnu.applyFastDispatchSTNU(true);
		this.stnu.applyMinDispatchableESTNU();
		assertEquals(expected, this.stnuGraph.toString());
	}

	/**
	 * Test case for Morris2014Dispatchable
	 */
	@Test
	public void testMorris2014DispatchableWithNegativeAddedToAWait() {
		this.makeSTNUFig8AFD_STNUPaper();
		//add a negative edge between Y and A
		STNUEdge YA = this.stnuGraph.findEdge("Y", "A");
		if (YA == null) {
			YA = this.stnuGraph.makeNewEdge("Y-A", Edge.ConstraintType.requirement);
			this.stnuGraph.addEdge(YA, "Y", "A");
		}
		YA.setValue(-2);

		this.stnu.applyMorris2014Dispatchable();
		final String result = """
		                      %TNGraph: Fig8AFD_STNUPaper
		                      %Nodes:
		                      ❮A❯
		                      ❮C❯
		                      ❮X❯
		                      ❮Y❯
		                      ❮Z❯
		                      %Edges:
		                      ❮A❯--❮A_Z; derived; -6; ❯-->❮Z❯
		                      ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                      ❮C❯--❮C-Y; derived; 1; ❯-->❮Y❯
		                      ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                      ❮C❯--❮CX; requirement; 3; ❯-->❮X❯
		                      ❮C❯--❮CZ; requirement; -7; ❯-->❮Z❯
		                      ❮X❯--❮X_Z; derived; 0; ❯-->❮Z❯
		                      ❮X❯--❮XY; requirement; -2; ❯-->❮Y❯
		                      ❮Y❯--❮Y-A; requirement; -2; UC(C):-9❯-->❮A❯
		                      ❮Y❯--❮Y_Z; derived; -6; ❯-->❮Z❯
		                      ❮Y❯--❮YC; requirement; 1; ❯-->❮C❯
		                      """;
		assertEquals(result, this.stnuGraph.toString());
	}

	/**
	 * Test case for Morris2014Dispatchable
	 */
	@Test
	public void testMorris2014DispatchableWithOToC() {
		this.makeSTNUFig8AFD_STNUPaper();
		//add a negative edge between Y and A
		final LabeledNode W = new LabeledNode("W");
		this.stnuGraph.addVertex(W);

		final STNUEdge WC = this.stnuGraph.makeNewEdge("W-C", Edge.ConstraintType.requirement);
		this.stnuGraph.addEdge(WC, "W", "C");
		WC.setValue(0);

		this.stnu.applyMorris2014Dispatchable();
		final String result = """
		                      %TNGraph: Fig8AFD_STNUPaper
		                      %Nodes:
		                      ❮A❯
		                      ❮C❯
		                      ❮W❯
		                      ❮X❯
		                      ❮Y❯
		                      ❮Z❯
		                      %Edges:
		                      ❮A❯--❮A_Z; derived; -6; ❯-->❮Z❯
		                      ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                      ❮C❯--❮C-Y; derived; 1; ❯-->❮Y❯
		                      ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                      ❮C❯--❮CX; requirement; 3; ❯-->❮X❯
		                      ❮C❯--❮CZ; requirement; -7; ❯-->❮Z❯
		                      ❮W❯--❮W-A; derived; UC(C):-10❯-->❮A❯
		                      ❮W❯--❮W-C; requirement; 0; ❯-->❮C❯
		                      ❮W❯--❮W-Z; derived; -7; ❯-->❮Z❯
		                      ❮X❯--❮X_Z; derived; 0; ❯-->❮Z❯
		                      ❮X❯--❮XY; requirement; -2; ❯-->❮Y❯
		                      ❮Y❯--❮Y-A; derived; UC(C):-9❯-->❮A❯
		                      ❮Y❯--❮Y_Z; derived; -6; ❯-->❮Z❯
		                      ❮Y❯--❮YC; requirement; 1; ❯-->❮C❯
		                      """;
		assertEquals(result, this.stnuGraph.toString());
	}

	/**
	 *
	 */
	@Test
	public void testNormalForm() {
		this.testGraph();
		this.stnuGraph.addEdge(new STNUEdgeInt("ΩX", -1), this.Ω, this.X);
		this.stnu.applyNormalForm();
		final String graph = """
		                     %TNGraph: testGraphML
		                     %Nodes:
		                     ❮aux_X❯
		                     ❮X❯
		                     ❮Y❯
		                     ❮Z❯
		                     ❮Ω❯
		                     %Edges:
		                     ❮aux_X❯--❮aux_X-X; derived; 2; ❯-->❮X❯
		                     ❮aux_X❯--❮aux_X-Z; requirement; -4; ❯-->❮Z❯
		                     ❮aux_X❯--❮aux_X-Ω; requirement; 10; ❯-->❮Ω❯
		                     ❮X❯--❮X-aux_X; derived; -2; ❯-->❮aux_X❯
		                     ❮X❯--❮XY; contingent; LC(Y):0❯-->❮Y❯
		                     ❮Y❯--❮YX; contingent; UC(Y):-3❯-->❮X❯
		                     ❮Y❯--❮YZ; requirement; -7; ❯-->❮Z❯
		                     ❮Z❯--❮ZΩ; requirement; 11; ❯-->❮Ω❯
		                     ❮Ω❯--❮Ω-aux_X; requirement; -1; ❯-->❮aux_X❯
		                     ❮Ω❯--❮Ω_Z; derived; 0; ❯-->❮Z❯
		                     ❮Ω❯--❮ΩY; requirement; -4; ❯-->❮Y❯
		                     """;
		assertEquals(graph, this.stnuGraph.toString());
	}

	/**
	 * Test predecessor
	 */
	@Test
	public void testPredecessor() {
		this.stnuGraph = new TNGraph<>("", STNUEdgeInt.class);
		this.stnuGraph.addEdge(new STNUEdgeInt("xz", -1), "X", "Z");
		this.stnuGraph.addEdge(new STNUEdgeInt("x1x", -1), "X1", "X");
		this.stnuGraph.addEdge(new STNUEdgeInt("x2x1", -1), "X2", "X1");
		this.stnuGraph.addEdge(new STNUEdgeInt("x3x2", -1), "X3", "X2");
		this.stnuGraph.addEdge(new STNUEdgeInt("x3x7", -1), "X3", "X7");
		this.stnuGraph.addEdge(new STNUEdgeInt("x7x6", -2), "X7", "X6");
		this.stnuGraph.addEdge(new STNUEdgeInt("x6x5", -2), "X6", "X5");
		this.stnuGraph.addEdge(new STNUEdgeInt("x5x4", -2), "X5", "X4");
		this.stnuGraph.addEdge(new STNUEdgeInt("x4z", -2), "X4", "Z");
		this.stnuGraph.addEdge(new STNUEdgeInt("zx3", 10), "Z", "X3");
		// assertEquals(graph, this.stnuGraph.toString());

		this.stnu = new STNU(this.stnuGraph);
		final Object2IntMap<LabeledNode> potential = STNU.GET_SSSP_BellmanFordOL(this.stnuGraph, this.stnu.getCheckStatus());
		final Object2IntMap<LabeledNode> expected = new Object2IntOpenHashMap<>();
		expected.put(this.stnuGraph.getNode("X1"), 2);
		expected.put(this.stnuGraph.getNode("X6"), 6);
		expected.put(this.stnuGraph.getNode("X5"), 4);
		expected.put(this.stnuGraph.getNode("X7"), 8);
		expected.put(this.stnuGraph.getNode("Z"), 0);
		expected.put(this.stnuGraph.getNode("X3"), 9);
		expected.put(this.stnuGraph.getNode("X"), 1);
		expected.put(this.stnuGraph.getNode("X2"), 3);
		expected.put(this.stnuGraph.getNode("X4"), 2);

		assertEquals(expected, potential);
		final Object2IntMap<LabeledNode> distFromSource = new Object2IntOpenHashMap<>();

		final LabeledNode X1 = this.stnuGraph.getNode("X1");
		assert potential != null;
		assert X1 != null;
		final TNPredecessorGraph<STNUEdge> predecessor = STN.GET_STN_PRECEDESSOR_SUBGRAPH_OPTIMIZED(this.stnuGraph, X1, potential, distFromSource, null);
		expected.clear();
		expected.put(this.stnuGraph.getNode("X1"), 0);
		expected.put(this.stnuGraph.getNode("X2"), 7);
		expected.put(this.stnuGraph.getNode("X3"), 8);
		expected.put(this.stnuGraph.getNode("X4"), 1);
		expected.put(this.stnuGraph.getNode("X5"), 3);
		expected.put(this.stnuGraph.getNode("X6"), 5);
		expected.put(this.stnuGraph.getNode("X7"), 7);
		expected.put(this.stnuGraph.getNode("Z"), -2);
		expected.put(this.stnuGraph.getNode("X"), -1);
		assertEquals(expected, distFromSource);

		// ❮X1❯=>[<❮X❯,❮x1x; requirement; -1; ❯>],

		if (predecessor == null) {
			fail("Problem with predecessor.");
		}
		final ObjectList<ObjectObjectImmutablePair<LabeledNode, STNUEdge>> X1successor = predecessor.getSuccessors(X1);
		final ObjectList<ObjectObjectImmutablePair<LabeledNode, STNUEdge>> X1expected = new ObjectArrayList<>();
		X1expected.add(new ObjectObjectImmutablePair<>(this.stnuGraph.getNode("X"), this.stnuGraph.getEdge("x1x")));
		assertEquals(X1expected, X1successor);

		// assertEquals("Predecessors: {❮X3❯=>[<❮Z❯,❮zx3; requirement; 10; ❯>], ❮X2❯=>[<❮X3❯,❮x3x2; requirement; -1; ❯>], ❮X4❯=>[<❮X5❯,❮x5x4; requirement; -2;
		// ❯>], ❮X6❯=>[<❮X7❯,❮x7x6; requirement; -2; ❯>], ❮X5❯=>[<❮X6❯,❮x6x5; requirement; -2; ❯>], ❮X7❯=>[<❮X3❯,❮x3x7; requirement; -1; ❯>], ❮Z❯=>[<❮X❯,❮xz;
		// requirement; -1; ❯>], ❮X❯=>[<❮X1❯,❮x1x; requirement; -1; ❯>]}\n"
		// + "Successors: {❮X1❯=>[<❮X❯,❮x1x; requirement; -1; ❯>], ❮X3❯=>[<❮X7❯,❮x3x7; requirement; -1; ❯>, <❮X2❯,❮x3x2; requirement; -1; ❯>],
		// ❮X6❯=>[<❮X5❯,❮x6x5; requirement; -2; ❯>], ❮X5❯=>[<❮X4❯,❮x5x4; requirement; -2; ❯>], ❮X7❯=>[<❮X6❯,❮x7x6; requirement; -2; ❯>], ❮Z❯=>[<❮X3❯,❮zx3;
		// requirement; 10; ❯>], ❮X❯=>[<❮Z❯,❮xz; requirement; -1; ❯>]}",
		// predecessor.toString());
	}

	/**
	 * Interesting instance for determining rigid components.
	 *
	 * @throws IOException                  if the external file is not readable.
	 * @throws ParserConfigurationException other exception
	 * @throws SAXException                 other exception
	 */
	@Test
	public void testRigidComponents() throws Exception {
		this.stnuGraph = this.readerSTNU.readGraph(new File(fileNameSTNUWithComplicatedRigidComponent), STNUEdgeInt.class);
		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final String expected = "[[❮N508❯, ❮N507❯, ❮N668❯, ❮N667❯]]";

		this.stnu.applyFastDispatchSTNU(true);
		final LabeledNode fakeSource = new LabeledNode("_FAKE_" + System.currentTimeMillis());
		this.stnuGraph.addVertex(fakeSource);
		STN.MAKE_NODES_REACHABLE_BY(this.stnuGraph, fakeSource, 0, "P%d".formatted(System.currentTimeMillis()));
		//
		// 1) Determine a solution that will be used by the following phases.
		//
		final Object2IntMap<LabeledNode> nodePotential = STN.GET_SSSP_BellmanFord(this.stnuGraph, fakeSource, null);
		assertNotNull(nodePotential);
		//
		// 2) Using the solution and the predecessor graph of fakeSource, remove all possible rigid components.
		//
		final Object2IntMap<LabeledNode> distanceFromSource = new Object2IntOpenHashMap<>();
		final TNGraph<STNUEdge> fakeSourcePredecessorGraph =
			STN.GET_STN_PREDECESSOR_SUBGRAPH(this.stnuGraph, fakeSource, nodePotential, distanceFromSource, null);
		assertNotNull(fakeSourcePredecessorGraph);
		final ObjectList<ObjectList<LabeledNode>> rigidComponents = STN.GET_STRONG_CONNECTED_COMPONENTS(fakeSourcePredecessorGraph, fakeSource);

		assertEquals(expected, rigidComponents.toString());
	}


	/**
	 * Test a case from benchmark
	 */
//	@Test
	public void testSRNCFinderNotDC002() {
		final TNGraphMLReader<STNUEdge> reader = new TNGraphMLReader<>();
		try {
			this.stnuGraph = reader.readGraph(new File(fileNameSTNUnotDC002), STNUEdgeInt.class);
		} catch (IOException | ParserConfigurationException | SAXException ex) {
			throw new RuntimeException(ex);
		}

		this.stnu = new STNU(this.stnuGraph, 3600);
		try {
//			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final STNU.STNUCheckStatus.SRNCInfo SRNCInfo = this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(true, this.stnuGraph);
		assert SRNCInfo != null;
		final ObjectList<STNUEdge> srnc = SRNCInfo.srnc();

		assertEquals(
			"[❮A33-N258; requirement; -18; ❯, ❮N258-N257; requirement; -32; ❯, ❮N257-N256; requirement; -121; ❯, "
			+ "❮N256-N255; requirement; -59; ❯, ❮N255-N254; requirement; -63; ❯, ❮N254-N253; requirement; -6; ❯, "
			+ "❮N253-N252; requirement; -124; ❯, ❮N252-N251; requirement; -21; ❯, ❮N251-N333; requirement; 86; ❯, "
			+ "❮N333-N334; requirement; 105; ❯, ❮N334-N335; requirement; 136; ❯, ❮N335-N336; requirement; 50; ❯, "
			+ "❮N336-N337; requirement; 24; ❯, ❮N337-A43; requirement; 33; ❯, ❮EA43-C43; contingent; LC(C43):11❯, ❮C43-A33; derived; -5; ❯]",
			srnc.toString());

		assertEquals(-4, SRNCInfo.value());
		assertFalse(SRNCInfo.simple());
		assertEquals(16, SRNCInfo.length());
		assertEquals(STNU.STNUCheckStatus.SRNCEdges.all, SRNCInfo.edgeType());
		assertEquals(
			"[❮A33-N258; requirement; -18; ❯, ❮N258-N257; requirement; -32; ❯, ❮N257-N256; requirement; -121; ❯, "
			+ "❮N256-N255; requirement; -59; ❯, ❮N255-N254; requirement; -63; ❯, ❮N254-N253; requirement; -6; ❯, "
			+ "❮N253-N252; requirement; -124; ❯, ❮N252-N251; requirement; -21; ❯, ❮N251-N333; requirement; 86; ❯, "
			+ "❮N333-N334; requirement; 105; ❯, ❮N334-N335; requirement; 136; ❯, ❮N335-N336; requirement; 50; ❯, "
			+ "❮N336-N337; requirement; 24; ❯, ❮N337-A43; requirement; 33; ❯, ❮EA43-C43; contingent; LC(C43):11❯, "
			+ "❮C43-N338; requirement; 58; ❯, ❮N338-N260; requirement; 102; ❯, ❮N260-N259; requirement; -26; ❯, "
			+ "❮N259-C33; requirement; -120; ❯, ❮EC33-A33; contingent; UC(C33):-19❯]",
			SRNCInfo.srnExpanded().toString());
		assertEquals(20, SRNCInfo.srnExpanded().size());
		assertEquals(1, SRNCInfo.maxEdgeRepetition());
	}

	/**
	 * Test a case from benchmark
	 */
//	@Test
	public void testSRNCFinderNotDC020() {
		final TNGraphMLReader<STNUEdge> reader = new TNGraphMLReader<>();
		try {
			this.stnuGraph = reader.readGraph(new File(fileNameSTNUnotDC020), STNUEdgeInt.class);
		} catch (IOException | ParserConfigurationException | SAXException ex) {
			throw new RuntimeException(ex);
		}

		this.stnu = new STNU(this.stnuGraph, 3600);
		try {
//			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final STNU.STNUCheckStatus.SRNCInfo SRNCInfo = this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(true, this.stnuGraph);
		assert SRNCInfo != null;
		final ObjectList<STNUEdge> srnc = SRNCInfo.srnc();

		assertEquals(
			"[❮A18-N141; requirement; -70; ❯, ❮N141-N140; requirement; -144; ❯, ❮N140-N139; requirement; -47; ❯, ❮N139-N60; requirement; 134; ❯, ❮N60-N61; requirement; 110; ❯, ❮N61-A18; requirement; 13; ❯]",
			srnc.toString());

		assertEquals(-4, SRNCInfo.value());
		assertFalse(SRNCInfo.simple());
		assertEquals(6, SRNCInfo.length());
		assertEquals(STNU.STNUCheckStatus.SRNCEdges.all, SRNCInfo.edgeType());
		assertEquals(
			"[❮A18-N141; requirement; -70; ❯, ❮N141-N140; requirement; -144; ❯, ❮N140-N139; requirement; -47; ❯, ❮N139-N60; requirement; 134; ❯, ❮N60-N61; requirement; 110; ❯, ❮N61-N62; requirement; 24; ❯, ❮N62-A8; requirement; 131; ❯, ❮EA8-C8; contingent; LC(C8):1❯, ❮C8-N63; requirement; 10; ❯, ❮N63-N64; requirement; 146; ❯, ❮N64-N65; requirement; 81; ❯, ❮N65-N66; requirement; 27; ❯, ❮N66-N67; requirement; 100; ❯, ❮N67-N148; requirement; 96; ❯, ❮N148-N147; requirement; -106; ❯, ❮N147-N146; requirement; -93; ❯, ❮N146-N145; requirement; -114; ❯, ❮N145-N144; requirement; -62; ❯, ❮N144-N143; requirement; -54; ❯, ❮N143-N142; requirement; -42; ❯, ❮N142-C18; requirement; -121; ❯, ❮EC18-A18; contingent; UC(C18):-11❯]",
			SRNCInfo.srnExpanded().toString());
		assertEquals(22, SRNCInfo.srnExpanded().size());
		int sum = 0;
		for (final STNUEdge e : SRNCInfo.srnExpanded()) {
			sum += (e.isOrdinaryEdge()) ? e.getValue() : e.getLabeledValue();
		}
		assertEquals(-4, sum);
		assertEquals(1, SRNCInfo.maxEdgeRepetition());
		assertEquals(1, SRNCInfo.lowerCaseCount().getInt(this.stnuGraph.getNode("C8")));
		assertEquals(1, SRNCInfo.upperCaseCount().getInt(this.stnuGraph.getNode("C18")));
	}

	/**
	 * Test a case from benchmark
	 */
//	@Test
	public void testSRNCFinderNotDC033() {
		final TNGraphMLReader<STNUEdge> reader = new TNGraphMLReader<>();
		try {
			this.stnuGraph = reader.readGraph(new File(fileNameSTNUnotDC033), STNUEdgeInt.class);
		} catch (IOException | ParserConfigurationException | SAXException ex) {
			throw new RuntimeException(ex);
		}

		this.stnu = new STNU(this.stnuGraph, 3600);
		try {
//			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final STNU.STNUCheckStatus.SRNCInfo SRNCInfo = this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(true, this.stnuGraph);
		assert SRNCInfo != null;
		final ObjectList<STNUEdge> srnc = SRNCInfo.srnc();

		assertEquals(
			"[❮A38-N298; requirement; -119; ❯, ❮N298-N297; requirement; -30; ❯, ❮N297-N59; requirement; 97; ❯, ❮N59-N58; requirement; -30; ❯, ❮N58-N57; requirement; -61; ❯, ❮N57-N56; requirement; -108; ❯, ❮N56-N55; requirement; -96; ❯, ❮N55-N376; requirement; 104; ❯, ❮N376-A48; requirement; 71; ❯, ❮EA48-C48; contingent; LC(C48):10❯, ❮C48-N139; requirement; 162; ❯, ❮N139-N138; requirement; -47; ❯, ❮N138-A38; derived; 31; ❯]",
			srnc.toString());

		assertEquals(-16, SRNCInfo.value());
		assertFalse(SRNCInfo.simple());
		assertEquals(13, SRNCInfo.length());
		assertEquals(STNU.STNUCheckStatus.SRNCEdges.all, SRNCInfo.edgeType());
		assertEquals(
			"[❮A38-N298; requirement; -119; ❯, ❮N298-N297; requirement; -30; ❯, ❮N297-N59; requirement; 97; ❯, ❮N59-N58; requirement; -30; ❯, ❮N58-N57; requirement; -61; ❯, ❮N57-N56; requirement; -108; ❯, ❮N56-N55; requirement; -96; ❯, ❮N55-N376; requirement; 104; ❯, ❮N376-A48; requirement; 71; ❯, ❮EA48-C48; contingent; LC(C48):10❯, ❮C48-N139; requirement; 162; ❯, ❮N139-N138; requirement; -47; ❯, ❮N138-A28; requirement; 133; ❯, ❮EA28-C28; contingent; LC(C28):9❯, ❮C28-N220; requirement; 119; ❯, ❮N220-N302; requirement; 84; ❯, ❮N302-N301; requirement; -122; ❯, ❮N301-N300; requirement; -117; ❯, ❮N300-N299; requirement; -42; ❯, ❮N299-C38; requirement; -27; ❯, ❮EC38-A38; contingent; UC(C38):-6❯]",
			SRNCInfo.srnExpanded().toString());
		assertEquals(21, SRNCInfo.srnExpanded().size());
		assertEquals(1, SRNCInfo.maxEdgeRepetition());
	}


	/**
	 * Test magicLoop for srnCycleFinder. The network is not DC and the semi-reducible-cycle is C2 A2 C1 A1 C3 A3 C2
	 */
	@Test
	public void testSrnCycleFinderMagicLoop() {
		this.stnuGraph = new TNGraph<>("cycleInterruptions", STNUEdgeInt.class);
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		final LabeledNode A2 = new LabeledNode("A2");
		final LabeledNode C2 = new LabeledNode("C2");
		final LabeledNode A3 = new LabeledNode("A3");
		final LabeledNode C3 = new LabeledNode("C3");
		final LabeledNode U = new LabeledNode("X");
		C1.setContingent(true);
		C2.setContingent(true);
		C3.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);
		this.stnuGraph.addVertex(A2);
		this.stnuGraph.addVertex(C2);
		this.stnuGraph.addVertex(A3);
		this.stnuGraph.addVertex(C3);
		this.stnuGraph.addVertex(U);

		STNUEdgeInt e = new STNUEdgeInt("A3C3");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C3.getName()), 1, false);
		this.stnuGraph.addEdge(e, A3, C3);
		e = new STNUEdgeInt("C3A3");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C3.getName()), -36, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C3, A3);

		e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 1, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -3, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		e = new STNUEdgeInt("A2C2");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), 1, false);
		this.stnuGraph.addEdge(e, A2, C2);
		e = new STNUEdgeInt("C2A2");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C2, A2);

		this.stnuGraph.addEdge(new STNUEdgeInt("C1C2", -1), C1, C2);
		this.stnuGraph.addEdge(new STNUEdgeInt("C2C1", 8), C2, C1);
		this.stnuGraph.addEdge(new STNUEdgeInt("C1C3", -7), C1, C3);
		this.stnuGraph.addEdge(new STNUEdgeInt("C3C1", 34), C3, C1);
		this.stnuGraph.addEdge(new STNUEdgeInt("C1X", -29), C1, X);
		this.stnuGraph.addEdge(new STNUEdgeInt("XC1", 48), X, C1);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final TNGraphMLWriter write = new TNGraphMLWriter(null);
		try {
			write.save(this.stnuGraph, new File("srnCycleFinderMagicLoop.stnu"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		STNU.STNUCheckStatus.SRNCInfo SRNCInfo = this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(false, this.stnuGraph);
		assert SRNCInfo != null;
		final ObjectList<STNUEdge> srnc = SRNCInfo.srnc();

		assertEquals(
			"[❮A3C3; contingent; LC(C3):1❯, ❮C3-A2; derived; 21; ❯, ❮A2C2; contingent; LC(C2):1❯, ❮C2-A1; derived; 5; ❯, ❮A1C1; contingent; LC(C1):1❯, ❮C1X; requirement; -29; ❯, ❮X-A3; derived; -1; ❯]",
			srnc.toString());

		assertEquals(-1, SRNCInfo.value());
		assertEquals(7, SRNCInfo.length());
		assertFalse(SRNCInfo.simple());
		assertEquals(STNU.STNUCheckStatus.SRNCEdges.ordinaryLC, SRNCInfo.edgeType());
		SRNCInfo = this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(true, this.stnuGraph);
		assert SRNCInfo != null;
		final ObjectList<STNUEdge> srncWithOriginalEdge = SRNCInfo.srnExpanded();
		assertEquals(
			"""
			[❮A3C3; contingent; LC(C3):1❯, ❮C3C1; requirement; 34; ❯, ❮C1A1; contingent; UC(C1):-3❯, ❮A1C1; contingent; LC(C1):1❯, ❮C1C2; requirement; -1; ❯,\
			 ❮C2A2; contingent; UC(C2):-10❯, ❮A2C2; contingent; LC(C2):1❯, ❮C2C1; requirement; 8; ❯, ❮C1A1; contingent; UC(C1):-3❯, ❮A1C1; contingent; LC(C1):1❯,\
			 ❮C1X; requirement; -29; ❯, ❮XC1; requirement; 48; ❯, ❮C1A1; contingent; UC(C1):-3❯, ❮A1C1; contingent; LC(C1):1❯, ❮C1C2; requirement; -1; ❯,\
			 ❮C2A2; contingent; UC(C2):-10❯, ❮A2C2; contingent; LC(C2):1❯, ❮C2C1; requirement; 8; ❯, ❮C1A1; contingent; UC(C1):-3❯, ❮A1C1; contingent; LC(C1):1❯,\
			 ❮C1C3; requirement; -7; ❯, ❮C3A3; contingent; UC(C3):-36❯]""",
			srncWithOriginalEdge.toString());

		assertEquals(4, SRNCInfo.maxEdgeRepetition());
		assertEquals(STNU.STNUCheckStatus.SRNCEdges.all, SRNCInfo.edgeType());

	}

	/**
	 * Test BFCT in srnCycleFinder. The network is not DC and the semi-reducible-cycle is A3 C3 A2 C2 A3
	 */
	@Test
	public void testSrnCycleFinder_BFCT() {
		this.stnuGraph = new TNGraph<>("cycleInterruptions", STNUEdgeInt.class);
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		final LabeledNode A2 = new LabeledNode("A2");
		final LabeledNode C2 = new LabeledNode("C2");
		final LabeledNode A3 = new LabeledNode("A3");
		final LabeledNode C3 = new LabeledNode("C3");
		final LabeledNode U = new LabeledNode("U");
		final LabeledNode V = new LabeledNode("V");
		final LabeledNode W = new LabeledNode("W");
		C1.setContingent(true);
		C2.setContingent(true);
		C3.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);
		this.stnuGraph.addVertex(A2);
		this.stnuGraph.addVertex(C2);
		this.stnuGraph.addVertex(A3);
		this.stnuGraph.addVertex(C3);
		this.stnuGraph.addVertex(U);
		this.stnuGraph.addVertex(V);
		this.stnuGraph.addVertex(W);

		STNUEdgeInt e = new STNUEdgeInt("A3C3");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C3.getName()), 1, false);
		this.stnuGraph.addEdge(e, A3, C3);
		e = new STNUEdgeInt("C3A3");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C3.getName()), -7, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C3, A3);

		e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 1, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -9, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		e = new STNUEdgeInt("A2C2");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), 1, false);
		this.stnuGraph.addEdge(e, A2, C2);
		e = new STNUEdgeInt("C2A2");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), -8, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C2, A2);

		this.stnuGraph.addEdge(new STNUEdgeInt("UC1", -1), U, C1);
		this.stnuGraph.addEdge(new STNUEdgeInt("A1W", -1), A1, W);
		this.stnuGraph.addEdge(new STNUEdgeInt("WC3", -1), W, C3);
		this.stnuGraph.addEdge(new STNUEdgeInt("A3V", -1), A3, V);
		this.stnuGraph.addEdge(new STNUEdgeInt("VC2", 3), V, C2);
		this.stnuGraph.addEdge(new STNUEdgeInt("A2U", 2), A2, U);

		//create a LO negative loop
		this.stnuGraph.addEdge(new STNUEdgeInt("C2A3", -2), C2, A3);
		this.stnuGraph.addEdge(new STNUEdgeInt("C3A2", -1), C3, A2);
//		this.stnuGraph.addEdge(new STNUEdgeInt("C1U", 1), C1, U);
//		this.stnuGraph.addEdge(new STNUEdgeInt("UA2", -2), U, A2);


		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final TNGraphMLWriter write = new TNGraphMLWriter(null);
		try {
			write.save(this.stnuGraph, new File("srnCycleFinderFig2.stnu"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		assertEquals(
			"[❮A3C3; contingent; LC(C3):1❯, ❮C3A2; requirement; -1; ❯, ❮A2C2; contingent; LC(C2):1❯, ❮C2A3; requirement; -2; ❯]",
			Objects.requireNonNull(this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(false, this.stnuGraph)).srnc().toString());
	}

	/**
	 * Test if the path of W is adjusted
	 */
	@Test
	public void testSrnCycleFinder_adjustmentPath4W() {
		this.stnuGraph = new TNGraph<>("adjustWpath", STNUEdgeInt.class);
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		final LabeledNode A2 = new LabeledNode("A2");
		final LabeledNode C2 = new LabeledNode("C2");
		final LabeledNode A3 = new LabeledNode("A3");
		final LabeledNode C3 = new LabeledNode("C3");
		final LabeledNode Q = new LabeledNode("Q");
		final LabeledNode V = new LabeledNode("V");
		final LabeledNode W = new LabeledNode("W");
		C1.setContingent(true);
		C2.setContingent(true);
		C3.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);
		this.stnuGraph.addVertex(A2);
		this.stnuGraph.addVertex(C2);
		this.stnuGraph.addVertex(Q);
		this.stnuGraph.addVertex(V);
		this.stnuGraph.addVertex(W);

		STNUEdge e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 1, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		e = new STNUEdgeInt("A2C2");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), 1, false);
		this.stnuGraph.addEdge(e, A2, C2);
		e = new STNUEdgeInt("C2A2");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C2, A2);

		e = new STNUEdgeInt("A3C3");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C3.getName()), 1, false);
		this.stnuGraph.addEdge(e, A3, C3);
		e = new STNUEdgeInt("C3A3");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C3.getName()), -7, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C3, A3);

		this.stnuGraph.addEdge(new STNUEdgeInt("A2C1", 4), A2, C1);
		this.stnuGraph.addEdge(new STNUEdgeInt("QC2", 12), Q, C2);
		this.stnuGraph.addEdge(new STNUEdgeInt("WQ", -3), W, Q);
		this.stnuGraph.addEdge(new STNUEdgeInt("WC1", 4), W, C1);
		this.stnuGraph.addEdge(new STNUEdgeInt("A3W", 1), A3, W);
		this.stnuGraph.addEdge(new STNUEdgeInt("VC3", 12), V, C3);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
			this.stnu.setDefaultControllabilityCheckAlg(STNU.CheckAlgorithm.SRNCycleFinder);
			this.stnu.dynamicControllabilityCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final TNGraphMLWriter write = new TNGraphMLWriter(null);
		try {
			write.save(this.stnuGraph, new File("srnCycleWPathAdjust.stnu"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		final STNU.STNUCheckStatus.SRNCInfo currentSRNC = this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(false, this.stnuGraph);
		assertNull(currentSRNC);
	}

	/**
	 * est ccLoop DC for srnCycleFinder. The network is DC and the semi-reducible-cycle is null
	 */
	@Test
	public void testSrnCycleFinder_ccLoopDC() {
		this.stnuGraph = new TNGraph<>("ccLoopNOTDC", STNUEdgeInt.class);
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		final LabeledNode W = new LabeledNode("W");
		C1.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);
		this.stnuGraph.addVertex(W);
		this.stnuGraph.addVertex(X);

		STNUEdge e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 1, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -9, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		this.stnuGraph.addEdge(new STNUEdgeInt("C1W", 4), C1, W);
		this.stnuGraph.addEdge(new STNUEdgeInt("WX", 1), W, X);
		this.stnuGraph.addEdge(new STNUEdgeInt("XC1", -3), X, C1);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final TNGraphMLWriter write = new TNGraphMLWriter(null);
		try {
			write.save(this.stnuGraph, new File("srnCycleFinderFig3a.stnu"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		assertNull(this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(false, this.stnuGraph));
		assertTrue(this.stnu.getCheckStatus().isControllable());
	}

	/**
	 * Test ccLoop NOT DC for srnCycleFinder. The network is not DC and the semi-reducible-cycle is A1 C1 W X C1 A1
	 */
	@Test
	public void testSrnCycleFinder_ccLoopNOTDC() {
		this.stnuGraph = new TNGraph<>("ccLoopNOTDC", STNUEdgeInt.class);
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		final LabeledNode W = new LabeledNode("W");
		C1.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);
		this.stnuGraph.addVertex(W);
		this.stnuGraph.addVertex(X);

		STNUEdge e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 1, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -9, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		this.stnuGraph.addEdge(new STNUEdgeInt("C1W", 1), C1, W);
		this.stnuGraph.addEdge(new STNUEdgeInt("WX", -3), W, X);
		this.stnuGraph.addEdge(new STNUEdgeInt("XC1", 4), X, C1);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final TNGraphMLWriter write = new TNGraphMLWriter(null);
		try {
			write.save(this.stnuGraph, new File("srnCycleFinderFig3a.stnu"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		assertEquals("[❮A1C1; contingent; LC(C1):1❯, ❮C1W; requirement; 1; ❯, ❮WX; requirement; -3; ❯, ❮XC1; requirement; 4; ❯, ❮C1A1; contingent; UC(C1):-9❯]",
		             Objects.requireNonNull(this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(false, this.stnuGraph)).srnc().toString());
	}

	/**
	 * Test ccLoop NOT DC for srnCycleFinder. It presents a loop on A1
	 */
	@Test
	public void testSrnCycleFinder_ccLoopNotDCOnA() {
		this.stnuGraph = new TNGraph<>("ccLoopNOTDC", STNUEdgeInt.class);
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		final LabeledNode A2 = new LabeledNode("A2");
		final LabeledNode C2 = new LabeledNode("C2");
		C1.setContingent(true);
		C2.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);
		this.stnuGraph.addVertex(A2);
		this.stnuGraph.addVertex(C2);

		STNUEdge e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 20, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -30, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		e = new STNUEdgeInt("A2C2");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), 5, false);
		this.stnuGraph.addEdge(e, A2, C2);
		e = new STNUEdgeInt("C2A2");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), -30, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C2, A2);

		this.stnuGraph.addEdge(new STNUEdgeInt("A1A2", 11), A1, A2);
		this.stnuGraph.addEdge(new STNUEdgeInt("C2C1", 1), C2, C1);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final TNGraphMLWriter write = new TNGraphMLWriter(null);
		try {
			write.save(this.stnuGraph, new File("srnCycleFinderLoopOnA.stnu"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		assertEquals("[❮A1-A1; derived; -13; ❯]",
		             Objects.requireNonNull(this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(false, this.stnuGraph)).srnc().toString());
	}

	/**
	 * Test cycle of interruptions for srnCycleFinder. The network is not DC and the semi-reducible-cycle is C2 A2 C1 A1 C3 A3 C2
	 */
	@Test
	public void testSrnCycleFinder_cycleInterruptions() {
		this.stnuGraph = new TNGraph<>("cycleInterruptions", STNUEdgeInt.class);
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		final LabeledNode A2 = new LabeledNode("A2");
		final LabeledNode C2 = new LabeledNode("C2");
		final LabeledNode A3 = new LabeledNode("A3");
		final LabeledNode C3 = new LabeledNode("C3");
		final LabeledNode U = new LabeledNode("U");
		final LabeledNode V = new LabeledNode("V");
		final LabeledNode W = new LabeledNode("W");
		C1.setContingent(true);
		C2.setContingent(true);
		C3.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);
		this.stnuGraph.addVertex(A2);
		this.stnuGraph.addVertex(C2);
		this.stnuGraph.addVertex(A3);
		this.stnuGraph.addVertex(C3);
		this.stnuGraph.addVertex(U);
		this.stnuGraph.addVertex(V);
		this.stnuGraph.addVertex(W);

		STNUEdgeInt e = new STNUEdgeInt("A3C3");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C3.getName()), 1, false);
		this.stnuGraph.addEdge(e, A3, C3);
		e = new STNUEdgeInt("C3A3");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C3.getName()), -7, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C3, A3);

		e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 1, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -9, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		e = new STNUEdgeInt("A2C2");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), 1, false);
		this.stnuGraph.addEdge(e, A2, C2);
		e = new STNUEdgeInt("C2A2");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), -8, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C2, A2);

		this.stnuGraph.addEdge(new STNUEdgeInt("UC1", -1), U, C1);
		this.stnuGraph.addEdge(new STNUEdgeInt("A1W", -1), A1, W);
		this.stnuGraph.addEdge(new STNUEdgeInt("WC3", -1), W, C3);
		this.stnuGraph.addEdge(new STNUEdgeInt("A3V", -1), A3, V);
		this.stnuGraph.addEdge(new STNUEdgeInt("VC2", 3), V, C2);
		this.stnuGraph.addEdge(new STNUEdgeInt("A2U", 2), A2, U);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
			this.stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.SRNCycleFinder);
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final TNGraphMLWriter write = new TNGraphMLWriter(null);
		try {
			write.save(this.stnuGraph, new File("srnCycleFinderFig2.stnu"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		final STNU.STNUCheckStatus.SRNCInfo currentSRNC = this.stnu.getCheckStatus().getNegativeSTNUCycleInfo(false, this.stnuGraph);
		assert currentSRNC != null;
		assertEquals(-23, currentSRNC.value());
	}

	private void makeMinDistESTNUSimple() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.stnuGraph.addVertex(this.Y);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);
		this.stnuGraph.addEdge(new STNUEdgeInt("XC", 5), this.X, C);
		this.stnuGraph.addEdge(new STNUEdgeInt("XY", 100), this.Y, C);
		this.stnu = new STNU(this.stnuGraph);

		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Case where a node C is a 0 distance to C
	 */
	private void makeMinDistESTNUX02C() {
		this.stnuGraph = new TNGraph<>("Simple", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("XC", 0), this.X, C);
		this.stnu = new STNU(this.stnuGraph);

		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Builds in this.stnu the network of Fig. 8a of the paper L. Hunsberger and R. Posenato, “A Faster Algorithm for Converting Simple Temporal Networks with
	 * Uncertainty into Dispatchable Form,” Information and Computation, vol. 293, p. 105063, Jun. 2023, doi: 10.1016/j.ic.2023.105063.
	 */
	private void makeSTNUFig8AFD_STNUPaper() {
		this.stnuGraph = new TNGraph<>("Fig8AFD_STNUPaper", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		C.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(this.X);
		this.X.setContingent(false);
		this.stnuGraph.addVertex(this.Y);
		this.Y.setContingent(false);

		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		this.stnuGraph.addEdge(new STNUEdgeInt("CZ", -7), C, Z);
		this.stnuGraph.addEdge(new STNUEdgeInt("CX", 3), C, X);
		this.stnuGraph.addEdge(new STNUEdgeInt("XY", -2), X, Y);
		this.stnuGraph.addEdge(new STNUEdgeInt("YC", 1), Y, C);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Builds in this.stnu the network of email 19/12/2023 by Luke Hunsberger
	 */
	private void makeSTNULuke20231219() {
		this.stnuGraph = new TNGraph<>("STNULuke20231219", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		C.setContingent(true);
		C1.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);

		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 1, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		this.stnuGraph.addEdge(new STNUEdgeInt("C1C", 3), C1, C);
		this.stnuGraph.addEdge(new STNUEdgeInt("A1C", 5), A1, C);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * DC network with this.stnuGraph.addEdge(new STNUEdgeInt("WY", 8), W, Y); redundant.
	 */
	private void makeSTNULuke20231220() {
		this.stnuGraph = new TNGraph<>("STNULuke20231220", STNUEdgeInt.class);
		final LabeledNode A = new LabeledNode("A");
		final LabeledNode C = new LabeledNode("C");
		final LabeledNode A1 = new LabeledNode("A1");
		final LabeledNode C1 = new LabeledNode("C1");
		final LabeledNode A2 = new LabeledNode("A2");
		final LabeledNode C2 = new LabeledNode("C2");
		final LabeledNode W = new LabeledNode("W");
		C.setContingent(true);
		C1.setContingent(true);
		C2.setContingent(true);
		this.stnuGraph.addVertex(this.Z);
		this.stnuGraph.addVertex(A);
		this.stnuGraph.addVertex(C);
		this.stnuGraph.addVertex(A1);
		this.stnuGraph.addVertex(C1);
		this.stnuGraph.addVertex(A2);
		this.stnuGraph.addVertex(C2);
		this.stnuGraph.addVertex(this.Y);
		this.stnuGraph.addVertex(W);

		STNUEdgeInt e = new STNUEdgeInt("AC");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), 1, false);
		this.stnuGraph.addEdge(e, A, C);
		e = new STNUEdgeInt("CA");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C, A);

		e = new STNUEdgeInt("A1C1");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), 1, false);
		this.stnuGraph.addEdge(e, A1, C1);
		e = new STNUEdgeInt("C1A1");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C1.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C1, A1);

		e = new STNUEdgeInt("A2C2");
		e.setConstraintType(Edge.ConstraintType.contingent);
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), 1, false);
		this.stnuGraph.addEdge(e, A2, C2);
		e = new STNUEdgeInt("C2A2");
		e.setLabeledValue(new ALabelAlphabet.ALetter(C2.getName()), -10, true);
		e.setConstraintType(Edge.ConstraintType.contingent);
		this.stnuGraph.addEdge(e, C2, A2);

		this.stnuGraph.addEdge(new STNUEdgeInt("CY", 8), C, Y);
		this.stnuGraph.addEdge(new STNUEdgeInt("C1C", 3), C1, C);
		this.stnuGraph.addEdge(new STNUEdgeInt("A2A1", 1), A2, A1);
		this.stnuGraph.addEdge(new STNUEdgeInt("C2Y", 6), C2, Y);
		this.stnuGraph.addEdge(new STNUEdgeInt("WC2", 4), W, C2);
		this.stnuGraph.addEdge(new STNUEdgeInt("WY", 8), W, Y);

		this.stnu = new STNU(this.stnuGraph);
		try {
			this.stnu.initAndCheck();
		} catch (WellDefinitionException e1) {
			e1.printStackTrace();
		}
		final TNGraphMLWriter write = new TNGraphMLWriter(null);
		try {
			write.save(this.stnuGraph, new File("lukeExample20231220"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

}
