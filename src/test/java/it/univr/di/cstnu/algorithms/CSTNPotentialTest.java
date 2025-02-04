/**
 *
 */
package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.algorithms.AbstractCSTN.NodesToCheck;
import it.univr.di.cstnu.graph.*;
import it.univr.di.labeledvalue.Label;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author posenato
 */
public class CSTNPotentialTest {
	/**
	 *
	 */
	static final Class<? extends CSTNEdge> edgeImplClass = CSTNEdgePluggable.class;

	/**
	 *
	 */
	TNGraph<CSTNEdge> g;
	/**
	 *
	 */
	LabeledNode Z;
	/**
	 *
	 */
	LabeledNode P;

	/**
	 *
	 */
	LabeledNode Q;

	/**
	 *
	 */
	LabeledNode X;

	/**
	 *
	 */
	LabeledNode Y;

	/**
	 *
	 */
	CSTNPotential cstnPotential;
	/**
	 *
	 */
	Instant instant;

	/**
	 *
	 */
	@Before
	public void setUp() {
		this.g = new TNGraph<>("TestGraph", edgeImplClass);
		this.Z = LabeledNodeSupplier.get("Z");
		this.P = LabeledNodeSupplier.get("P", 'p');
		this.Q = new LabeledNode("Q?", 'q');
		this.X = LabeledNodeSupplier.get("X");
		this.Y = LabeledNodeSupplier.get("Y");
		this.cstnPotential = new CSTNPotential(this.g);
		this.instant = Instant.now();
		this.instant = this.instant.plus(1, ChronoUnit.HOURS);

		final Label p = Label.parse("p");
		final Label q = Label.parse("q");
		CSTNEdge edge = this.g.getEdgeFactory().get("PX");
		edge.mergeLabeledValue(p, -3);
		this.g.addEdge(edge, this.P, this.X);

		edge = this.g.getEdgeFactory().get("XZ");
		edge.mergeLabeledValue(p, -1);
		this.g.addEdge(edge, this.X, this.Z);

		edge = this.g.getEdgeFactory().get("XY");
		edge.mergeLabeledValue(p, 5);
		this.g.addEdge(edge, this.X, this.Y);

		edge = this.g.getEdgeFactory().get("YX");
		edge.mergeLabeledValue(p, -2);
		this.g.addEdge(edge, this.Y, this.X);

		edge = this.g.getEdgeFactory().get("PZ");
		edge.mergeLabeledValue(q, -5);
		this.g.addEdge(edge, this.P, this.Z);

		edge = this.g.getEdgeFactory().get("QZ");
		edge.mergeLabeledValue(p, -6);
		this.g.addEdge(edge, this.Q, this.Z);
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.algorithms.CSTNPotential#dynamicConsistencyCheckWOInit()}.
	 */
	@Test
	public void testDynamicConsistencyCheckWOInit() {
		this.cstnPotential.setUpperBoundRequested(false);
		this.cstnPotential.setPropagationOnlyToZ(true);

		try {
			this.cstnPotential.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
			fail("WellDefinitionException");
		}
		// remember that qLoopFinder adjusts some edges.
		final String expectedGraph = """
		                             %TNGraph: TestGraph
		                             %Nodes:
		                             ❮P; Obs: p❯
		                             ❮Q?; Obs: q❯
		                             ❮X❯
		                             ❮Y❯
		                             ❮Z❯
		                             %Edges:
		                             ❮P; Obs: p❯--❮PX; requirement; {(-3, ⊡) }; ❯-->❮X❯
		                             ❮P; Obs: p❯--❮PZ; requirement; {(-4, ⊡) (-5, q) }; ❯-->❮Z❯
		                             ❮Q?; Obs: q❯--❮QZ; requirement; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮X❯--❮XY; requirement; {(5, p) }; ❯-->❮Y❯
		                             ❮X❯--❮XZ; requirement; {(0, ⊡) (-1, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮Y_Z; internal; {(0, ⊡) (-3, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮YX; requirement; {(-2, p) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_P; internal; {(24, ⊡) }; ❯-->❮P; Obs: p❯
		                             ❮Z❯--❮Z_Q?; internal; {(24, ⊡) }; ❯-->❮Q?; Obs: q❯
		                             ❮Z❯--❮Z_X; internal; {(24, ⊡) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_Y; internal; {(24, ⊡) }; ❯-->❮Y❯
		                             """;
		assertEquals(expectedGraph, this.g.toString());

		this.cstnPotential.dynamicConsistencyCheckWOInit();

		final String expectedPot = "Z={(0, ⊡) }P={(-5, ⊡) }Q={(-5, ⊡) (-6, p) }X={(-1, ⊡) }Y={(-3, ⊡) }";
		assertEquals(expectedPot, "Z=" + this.Z.getLabeledPotential().toString()
		                          + "P=" + this.P.getLabeledPotential().toString()
		                          + "Q=" + this.Q.getLabeledPotential().toString()
		                          + "X=" + this.X.getLabeledPotential().toString()
		                          + "Y=" + this.Y.getLabeledPotential().toString());

	}

	/**
	 * Test method for {@link it.univr.di.cstnu.algorithms.CSTNPotential#dynamicConsistencyCheckWOInit()}.
	 */
	@Test
	public void testDynamicConsistencyCheckWOInitUpper() {
		this.cstnPotential.setUpperBoundRequested(true);
		CSTNEdge edge = this.g.getEdge("YX");
		edge.mergeLabeledValue(Label.parse("p"), -6);
		edge = this.g.getEdge("XY");
		edge.removeLabeledValue(Label.parse("p"));
		edge.mergeLabeledValue(Label.parse("p"), 6);

		try {
			this.cstnPotential.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
			fail("WellDefinitionException");
		}
		// remember that qLoopFinder adjusts some edges.
		final String expectedGraph = """
		                             %TNGraph: TestGraph
		                             %Nodes:
		                             ❮P; Obs: p❯
		                             ❮Q?; Obs: q❯
		                             ❮X❯
		                             ❮Y❯
		                             ❮Z❯
		                             %Edges:
		                             ❮P; Obs: p❯--❮PX; requirement; {(-3, ⊡) }; ❯-->❮X❯
		                             ❮P; Obs: p❯--❮PZ; requirement; {(-4, ⊡) (-5, q) }; ❯-->❮Z❯
		                             ❮Q?; Obs: q❯--❮QZ; requirement; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮X❯--❮XY; requirement; {(6, p) }; ❯-->❮Y❯
		                             ❮X❯--❮XZ; requirement; {(0, ⊡) (-1, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮Y_Z; internal; {(0, ⊡) (-7, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮YX; requirement; {(-6, p) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_P; internal; {(24, ⊡) }; ❯-->❮P; Obs: p❯
		                             ❮Z❯--❮Z_Q?; internal; {(24, ⊡) }; ❯-->❮Q?; Obs: q❯
		                             ❮Z❯--❮Z_X; internal; {(24, ⊡) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_Y; internal; {(24, ⊡) }; ❯-->❮Y❯
		                             """;
		assertEquals(expectedGraph, this.g.toString());

		this.cstnPotential.dynamicConsistencyCheckWOInit();

		final String expectedPot = "Z={(0, ⊡) }P={(24, ⊡) }Q={(24, ⊡) }X={(21, ⊡) (18, p) }Y={(24, ⊡) }";
		assertEquals(expectedPot, "Z=" + this.Z.getLabeledUpperPotential().toString()
		                          + "P=" + this.P.getLabeledUpperPotential().toString()
		                          + "Q=" + this.Q.getLabeledUpperPotential().toString()
		                          + "X=" + this.X.getLabeledUpperPotential().toString()
		                          + "Y=" + this.Y.getLabeledUpperPotential().toString());
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.algorithms.CSTNPotential#initAndCheck()}.
	 */
	@Test
	public void testInitAndCheck() {

		try {
			this.cstnPotential.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
			fail("WellDefinitionException");
		}

		// remember that qLoopFinder adjusts some edges.
		final String expectedGraph = """
		                             %TNGraph: TestGraph
		                             %Nodes:
		                             ❮P; Obs: p❯
		                             ❮Q?; Obs: q❯
		                             ❮X❯
		                             ❮Y❯
		                             ❮Z❯
		                             %Edges:
		                             ❮P; Obs: p❯--❮PX; requirement; {(-3, ⊡) }; ❯-->❮X❯
		                             ❮P; Obs: p❯--❮PZ; requirement; {(-4, ⊡) (-5, q) }; ❯-->❮Z❯
		                             ❮Q?; Obs: q❯--❮QZ; requirement; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮X❯--❮XY; requirement; {(5, p) }; ❯-->❮Y❯
		                             ❮X❯--❮XZ; requirement; {(0, ⊡) (-1, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮Y_Z; internal; {(0, ⊡) (-3, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮YX; requirement; {(-2, p) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_P; internal; {(24, ⊡) }; ❯-->❮P; Obs: p❯
		                             ❮Z❯--❮Z_Q?; internal; {(24, ⊡) }; ❯-->❮Q?; Obs: q❯
		                             ❮Z❯--❮Z_X; internal; {(24, ⊡) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_Y; internal; {(24, ⊡) }; ❯-->❮Y❯
		                             """;
		assertEquals(expectedGraph, this.g.toString());
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.algorithms.CSTNPotential#initAndCheck()}.
	 */
	@Test
	public void testInitAndCheckWithUpperBounds() {
		this.cstnPotential.setUpperBoundRequested(true);
		try {
			this.cstnPotential.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
			fail("WellDefinitionException");
		}

		// remember that qLoopFinder adjusts some edges.
		final String expectedGraph = """
		                             %TNGraph: TestGraph
		                             %Nodes:
		                             ❮P; Obs: p❯
		                             ❮Q?; Obs: q❯
		                             ❮X❯
		                             ❮Y❯
		                             ❮Z❯
		                             %Edges:
		                             ❮P; Obs: p❯--❮PX; requirement; {(-3, ⊡) }; ❯-->❮X❯
		                             ❮P; Obs: p❯--❮PZ; requirement; {(-4, ⊡) (-5, q) }; ❯-->❮Z❯
		                             ❮Q?; Obs: q❯--❮QZ; requirement; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮X❯--❮XY; requirement; {(5, p) }; ❯-->❮Y❯
		                             ❮X❯--❮XZ; requirement; {(0, ⊡) (-1, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮Y_Z; internal; {(0, ⊡) (-3, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮YX; requirement; {(-2, p) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_P; internal; {(24, ⊡) }; ❯-->❮P; Obs: p❯
		                             ❮Z❯--❮Z_Q?; internal; {(24, ⊡) }; ❯-->❮Q?; Obs: q❯
		                             ❮Z❯--❮Z_X; internal; {(24, ⊡) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_Y; internal; {(24, ⊡) }; ❯-->❮Y❯
		                             """;
		assertEquals(expectedGraph, this.g.toString());
	}

	/**
	 * Test method for
	 * {@link it.univr.di.cstnu.algorithms.CSTNPotential#potentialR3(it.univr.di.cstnu.graph.LabeledNode[],
	 * it.univr.di.cstnu.algorithms.AbstractCSTN.NodesToCheck, java.time.Instant)}.
	 */
	@Test
	public void testPotentialR3() {
		this.cstnPotential.setUpperBoundRequested(false);
		this.cstnPotential.setPropagationOnlyToZ(true);

		try {
			this.cstnPotential.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
			fail("WellDefinitionException");
		}
		// remember that qLoopFinder adjusts some edges.
		final String expectedGraph = """
		                             %TNGraph: TestGraph
		                             %Nodes:
		                             ❮P; Obs: p❯
		                             ❮Q?; Obs: q❯
		                             ❮X❯
		                             ❮Y❯
		                             ❮Z❯
		                             %Edges:
		                             ❮P; Obs: p❯--❮PX; requirement; {(-3, ⊡) }; ❯-->❮X❯
		                             ❮P; Obs: p❯--❮PZ; requirement; {(-4, ⊡) (-5, q) }; ❯-->❮Z❯
		                             ❮Q?; Obs: q❯--❮QZ; requirement; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮X❯--❮XY; requirement; {(5, p) }; ❯-->❮Y❯
		                             ❮X❯--❮XZ; requirement; {(0, ⊡) (-1, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮Y_Z; internal; {(0, ⊡) (-3, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮YX; requirement; {(-2, p) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_P; internal; {(24, ⊡) }; ❯-->❮P; Obs: p❯
		                             ❮Z❯--❮Z_Q?; internal; {(24, ⊡) }; ❯-->❮Q?; Obs: q❯
		                             ❮Z❯--❮Z_X; internal; {(24, ⊡) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_Y; internal; {(24, ⊡) }; ❯-->❮Y❯
		                             """;
		assertEquals(expectedGraph, this.g.toString());

		final NodesToCheck allNodes = new NodesToCheck(this.g.getVertices());
		this.Z.putLabeledPotential(Label.emptyLabel, 0);
		final NodesToCheck obs = this.cstnPotential.singleSinkShortestPaths(allNodes, this.instant);

		String expectedPot = "Z={(0, ⊡) }P={(-4, ⊡) (-5, q) }Q={(-6, p) }X={(-1, p) }Y={(-3, p) }";
		assertEquals(expectedPot, "Z=" + this.Z.getLabeledPotential().toString()
		                          + "P=" + this.P.getLabeledPotential().toString()
		                          + "Q=" + this.Q.getLabeledPotential().toString()
		                          + "X=" + this.X.getLabeledPotential().toString()
		                          + "Y=" + this.Y.getLabeledPotential().toString());

		assertEquals("[❮Q?; Obs: q❯, ❮P; Obs: p❯]", obs.toString());

		expectedPot = "Z={(0, ⊡) }P={(-5, ⊡) }Q={(-5, ⊡) (-6, p) }X={(-1, ⊡) }Y={(-3, ⊡) }";
		this.cstnPotential.potentialR3(this.g.getVerticesArray(), obs, this.instant);
		assertEquals(expectedPot, "Z=" + this.Z.getLabeledPotential().toString()
		                          + "P=" + this.P.getLabeledPotential().toString()
		                          + "Q=" + this.Q.getLabeledPotential().toString()
		                          + "X=" + this.X.getLabeledPotential().toString()
		                          + "Y=" + this.Y.getLabeledPotential().toString());
	}

	/**
	 * Test method for
	 * {@link
	 * it.univr.di.cstnu.algorithms.CSTNPotential#singleSinkShortestPaths(it.univr.di.cstnu.algorithms.AbstractCSTN.NodesToCheck,
	 * java.time.Instant)}.
	 */
	@Test
	public void testSingleSinkShortestPaths() {
		this.cstnPotential.setUpperBoundRequested(false);
		this.cstnPotential.setPropagationOnlyToZ(true);

		try {
			this.cstnPotential.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
			fail("WellDefinitionException");
		}
		// remember that qLoopFinder adjusts some edges.
		final String expectedGraph = """
		                             %TNGraph: TestGraph
		                             %Nodes:
		                             ❮P; Obs: p❯
		                             ❮Q?; Obs: q❯
		                             ❮X❯
		                             ❮Y❯
		                             ❮Z❯
		                             %Edges:
		                             ❮P; Obs: p❯--❮PX; requirement; {(-3, ⊡) }; ❯-->❮X❯
		                             ❮P; Obs: p❯--❮PZ; requirement; {(-4, ⊡) (-5, q) }; ❯-->❮Z❯
		                             ❮Q?; Obs: q❯--❮QZ; requirement; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮X❯--❮XY; requirement; {(5, p) }; ❯-->❮Y❯
		                             ❮X❯--❮XZ; requirement; {(0, ⊡) (-1, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮Y_Z; internal; {(0, ⊡) (-3, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮YX; requirement; {(-2, p) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_P; internal; {(24, ⊡) }; ❯-->❮P; Obs: p❯
		                             ❮Z❯--❮Z_Q?; internal; {(24, ⊡) }; ❯-->❮Q?; Obs: q❯
		                             ❮Z❯--❮Z_X; internal; {(24, ⊡) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_Y; internal; {(24, ⊡) }; ❯-->❮Y❯
		                             """;
		assertEquals(expectedGraph, this.g.toString());

		final NodesToCheck allNodes = new NodesToCheck(this.g.getVertices());
		this.Z.putLabeledPotential(Label.emptyLabel, 0);
		final NodesToCheck obs = this.cstnPotential.singleSinkShortestPaths(allNodes, this.instant);

		final String expectedPot = "Z={(0, ⊡) }P={(-4, ⊡) (-5, q) }Q={(-6, p) }X={(-1, p) }Y={(-3, p) }";
		assertEquals(expectedPot, "Z=" + this.Z.getLabeledPotential().toString()
		                          + "P=" + this.P.getLabeledPotential().toString()
		                          + "Q=" + this.Q.getLabeledPotential().toString()
		                          + "X=" + this.X.getLabeledPotential().toString()
		                          + "Y=" + this.Y.getLabeledPotential().toString());

		assertEquals("[❮Q?; Obs: q❯, ❮P; Obs: p❯]", obs.toString());
	}

	/**
	 * Test method for
	 * {@link
	 * it.univr.di.cstnu.algorithms.CSTNPotential#singleSourceShortestPaths(it.univr.di.cstnu.algorithms.AbstractCSTN.NodesToCheck,
	 * it.univr.di.cstnu.algorithms.AbstractCSTN.NodesToCheck, java.time.Instant)}.
	 */
	@Test
	public void testSingleSourceShortestPaths() {
		this.cstnPotential.setUpperBoundRequested(true);

		try {
			this.cstnPotential.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
			fail("WellDefinitionException");
		}
		// remember that qLoopFinder adjusts some edges.
		final String expectedGraph = """
		                             %TNGraph: TestGraph
		                             %Nodes:
		                             ❮P; Obs: p❯
		                             ❮Q?; Obs: q❯
		                             ❮X❯
		                             ❮Y❯
		                             ❮Z❯
		                             %Edges:
		                             ❮P; Obs: p❯--❮PX; requirement; {(-3, ⊡) }; ❯-->❮X❯
		                             ❮P; Obs: p❯--❮PZ; requirement; {(-4, ⊡) (-5, q) }; ❯-->❮Z❯
		                             ❮Q?; Obs: q❯--❮QZ; requirement; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮X❯--❮XY; requirement; {(5, p) }; ❯-->❮Y❯
		                             ❮X❯--❮XZ; requirement; {(0, ⊡) (-1, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮Y_Z; internal; {(0, ⊡) (-3, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮YX; requirement; {(-2, p) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_P; internal; {(24, ⊡) }; ❯-->❮P; Obs: p❯
		                             ❮Z❯--❮Z_Q?; internal; {(24, ⊡) }; ❯-->❮Q?; Obs: q❯
		                             ❮Z❯--❮Z_X; internal; {(24, ⊡) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_Y; internal; {(24, ⊡) }; ❯-->❮Y❯
		                             							   """;
		assertEquals(expectedGraph, this.g.toString());

		final NodesToCheck allNodes = new NodesToCheck(this.g.getVertices());
		this.Z.putLabeledPotential(Label.emptyLabel, 0);
		this.Z.putLabeledUpperPotential(Label.emptyLabel, 0);
		final NodesToCheck obsInvolved = new NodesToCheck();
		final NodesToCheck nodeModified =
			this.cstnPotential.singleSourceShortestPaths(allNodes, obsInvolved, this.instant);

		final String expectedPot = "Z={(0, ⊡) }P={(24, ⊡) }Q={(24, ⊡) }X={(21, ⊡) }Y={(24, ⊡) }";
		assertEquals(expectedPot, "Z=" + this.Z.getLabeledUpperPotential().toString()
		                          + "P=" + this.P.getLabeledUpperPotential().toString()
		                          + "Q=" + this.Q.getLabeledUpperPotential().toString()
		                          + "X=" + this.X.getLabeledUpperPotential().toString()
		                          + "Y=" + this.Y.getLabeledUpperPotential().toString());

		assertEquals("[]",
		             obsInvolved.toString());// it could be [❮P; Obs: p❯] when X is update to 22 from Y, then to 21 from P?. From Y, p is involved.
		assertEquals("[❮X❯]", nodeModified.toString());
	}

	/**
	 * Test method for
	 * {@link
	 * it.univr.di.cstnu.algorithms.CSTNPotential#upperPotentialR3(it.univr.di.cstnu.algorithms.AbstractCSTN.NodesToCheck,
	 * it.univr.di.cstnu.algorithms.AbstractCSTN.NodesToCheck, java.time.Instant)}.
	 */
	@Test
	public void testUpperPotentialR3() {
		final CSTNEdge edge = this.g.getEdge("YX");
		edge.mergeLabeledValue(Label.parse("p"), -5);
		this.cstnPotential.setUpperBoundRequested(true);

		try {
			this.cstnPotential.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
			fail("WellDefinitionException");
		}
		// remember that qLoopFinder adjusts some edges.
		final String expectedGraph = """
		                             %TNGraph: TestGraph
		                             %Nodes:
		                             ❮P; Obs: p❯
		                             ❮Q?; Obs: q❯
		                             ❮X❯
		                             ❮Y❯
		                             ❮Z❯
		                             %Edges:
		                             ❮P; Obs: p❯--❮PX; requirement; {(-3, ⊡) }; ❯-->❮X❯
		                             ❮P; Obs: p❯--❮PZ; requirement; {(-4, ⊡) (-5, q) }; ❯-->❮Z❯
		                             ❮Q?; Obs: q❯--❮QZ; requirement; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮X❯--❮XY; requirement; {(5, p) }; ❯-->❮Y❯
		                             ❮X❯--❮XZ; requirement; {(0, ⊡) (-1, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮Y_Z; internal; {(0, ⊡) (-6, p) }; ❯-->❮Z❯
		                             ❮Y❯--❮YX; requirement; {(-5, p) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_P; internal; {(24, ⊡) }; ❯-->❮P; Obs: p❯
		                             ❮Z❯--❮Z_Q?; internal; {(24, ⊡) }; ❯-->❮Q?; Obs: q❯
		                             ❮Z❯--❮Z_X; internal; {(24, ⊡) }; ❯-->❮X❯
		                             ❮Z❯--❮Z_Y; internal; {(24, ⊡) }; ❯-->❮Y❯
		                             """;
		assertEquals(expectedGraph, this.g.toString());

		final NodesToCheck allNodes = new NodesToCheck(this.g.getVertices());
		this.Z.putLabeledPotential(Label.emptyLabel, 0);
		this.Z.putLabeledUpperPotential(Label.emptyLabel, 0);
		final NodesToCheck obsInvolved = new NodesToCheck();
		NodesToCheck nodeModified = this.cstnPotential.singleSourceShortestPaths(allNodes, obsInvolved, this.instant);

		String expectedPot = "Z={(0, ⊡) }P={(24, ⊡) }Q={(24, ⊡) }X={(21, ⊡) (19, p) }Y={(24, ⊡) }";
		assertEquals(expectedPot, "Z=" + this.Z.getLabeledUpperPotential().toString()
		                          + "P=" + this.P.getLabeledUpperPotential().toString()
		                          + "Q=" + this.Q.getLabeledUpperPotential().toString()
		                          + "X=" + this.X.getLabeledUpperPotential().toString()
		                          + "Y=" + this.Y.getLabeledUpperPotential().toString());

		assertEquals("[❮P; Obs: p❯]",
		             obsInvolved.toString());// because X is update to 22 from Y, then to 21 from P?. From Y, p is involved.
		assertEquals("[❮X❯]", nodeModified.toString());

		nodeModified = this.cstnPotential.upperPotentialR3(nodeModified, obsInvolved, this.instant);
		expectedPot = "Z={(0, ⊡) }P={(24, ⊡) }Q={(24, ⊡) }X={(21, ⊡) (19, p) }Y={(24, ⊡) }";
		assertEquals(expectedPot, "Z=" + this.Z.getLabeledUpperPotential().toString()
		                          + "P=" + this.P.getLabeledUpperPotential().toString()
		                          + "Q=" + this.Q.getLabeledUpperPotential().toString()
		                          + "X=" + this.X.getLabeledUpperPotential().toString()
		                          + "Y=" + this.Y.getLabeledUpperPotential().toString());
	}

}
