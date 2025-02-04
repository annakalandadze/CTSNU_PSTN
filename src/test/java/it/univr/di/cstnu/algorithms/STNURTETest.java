package it.univr.di.cstnu.algorithms;

import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import it.univr.di.cstnu.graph.*;
import it.univr.di.labeledvalue.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Simple test
 */
@SuppressWarnings("FieldCanBeLocal")
public class STNURTETest {

	private LabeledNode A, B, C, D, X1, X2, X3, Z;
	private TNGraph<STNUEdge> g;
	private STNU stnu;
	private STNURTE rte;

	/**
	 * Early execution strategy
	 *
	 * @throws WellDefinitionException does not occur
	 */
	@Test
	public void rteEarly() throws WellDefinitionException {
		final STNU.STNUCheckStatus status =
			stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);

		assertTrue(status.isControllable());
		final String expectedG = """
		                         %TNGraph: g
		                         %Nodes:
		                         ❮A❯
		                         ❮B❯
		                         ❮C❯
		                         ❮D❯
		                         ❮X1❯
		                         ❮X2❯
		                         ❮X3❯
		                         ❮Z❯
		                         %Edges:
		                         ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                         ❮A❯--❮AZ; requirement; -1; ❯-->❮Z❯
		                         ❮B❯--❮B_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮C_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                         ❮D❯--❮D_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1C; requirement; -1; ❯-->❮C❯
		                         ❮X2❯--❮X2-A; derived; UC(C):-6❯-->❮A❯
		                         ❮X2❯--❮X2_Z; derived; 0; ❯-->❮Z❯
		                         ❮X2❯--❮X2C; requirement; 4; ❯-->❮C❯
		                         ❮X3❯--❮X3_Z; derived; 0; ❯-->❮Z❯
		                         """;
		final TNGraph<STNUEdge> dispatchableG = stnu.getGChecked();
		assertEquals(expectedG, dispatchableG.toString());

		rte = new STNURTE(dispatchableG);
		final STNURTE.RTEState state = rte.rte(STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY, STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY);
		assertEquals("{❮A❯=>1, ❮B❯=>0, ❮C❯=>2, ❮D❯=>0, ❮X1❯=>3, ❮X2❯=>2, ❮X3❯=>0, ❮Z❯=>0}"
			, (new Object2IntAVLTreeMap<>(state.schedule)).toString());
	}

	/**
	 * Early strategy for ordinary constraint and Late execution strategy for contingents
	 *
	 * @throws WellDefinitionException does not occur
	 */
	@Test
	public void rteEarlyLate() throws WellDefinitionException {
		final STNU.STNUCheckStatus status =
			stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);

		assertTrue(status.isControllable());
		final String expectedG = """
		                         %TNGraph: g
		                         %Nodes:
		                         ❮A❯
		                         ❮B❯
		                         ❮C❯
		                         ❮D❯
		                         ❮X1❯
		                         ❮X2❯
		                         ❮X3❯
		                         ❮Z❯
		                         %Edges:
		                         ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                         ❮A❯--❮AZ; requirement; -1; ❯-->❮Z❯
		                         ❮B❯--❮B_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮C_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                         ❮D❯--❮D_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1C; requirement; -1; ❯-->❮C❯
		                         ❮X2❯--❮X2-A; derived; UC(C):-6❯-->❮A❯
		                         ❮X2❯--❮X2_Z; derived; 0; ❯-->❮Z❯
		                         ❮X2❯--❮X2C; requirement; 4; ❯-->❮C❯
		                         ❮X3❯--❮X3_Z; derived; 0; ❯-->❮Z❯
		                         """;
		final TNGraph<STNUEdge> dispatchableG = stnu.getGChecked();
		assertEquals(expectedG, dispatchableG.toString());

		rte = new STNURTE(dispatchableG);
		final STNURTE.RTEState state = rte.rte(STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY, STNURTE.StrategyEnum.LATE_EXECUTION_STRATEGY);
		assertEquals("{❮A❯=>1, ❮B❯=>0, ❮C❯=>11, ❮D❯=>0, ❮X1❯=>12, ❮X2❯=>7, ❮X3❯=>0, ❮Z❯=>0}"
			, (new Object2IntAVLTreeMap<>(state.schedule)).toString());
	}


	/**
	 * Early strategy for ordinary constraint and Late execution strategy for contingents with a rigid component.
	 *
	 * @throws WellDefinitionException does not occur
	 */
	@Test
	public void rteEarlyLateRC() throws WellDefinitionException {
		STNUEdgeInt e = new STNUEdgeInt("X2X3", 0);
		g.addEdge(e, X2, X3);
		e = new STNUEdgeInt("X3X2", 0);
		g.addEdge(e, X3, X2);

		final STNU.STNUCheckStatus status =
			stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);

		assertTrue(status.isControllable());
		final String expectedG = """
		                         %TNGraph: g
		                         %Nodes:
		                         ❮A❯
		                         ❮B❯
		                         ❮C❯
		                         ❮D❯
		                         ❮X1❯
		                         ❮X2❯
		                         ❮X3❯
		                         ❮Z❯
		                         %Edges:
		                         ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                         ❮A❯--❮AZ; requirement; -1; ❯-->❮Z❯
		                         ❮B❯--❮B_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮C_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                         ❮D❯--❮D_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1C; requirement; -1; ❯-->❮C❯
		                         ❮X2❯--❮X2-A; derived; UC(C):-6❯-->❮A❯
		                         ❮X2❯--❮X2_Z; derived; 0; ❯-->❮Z❯
		                         ❮X2❯--❮X2C; requirement; 4; ❯-->❮C❯
		                         ❮X2❯--❮X2X3; requirement; 0; ❯-->❮X3❯
		                         ❮X3❯--❮X3-A; derived; UC(C):-6❯-->❮A❯
		                         ❮X3❯--❮X3_Z; derived; 0; ❯-->❮Z❯
		                         ❮X3❯--❮X3X2; requirement; 0; ❯-->❮X2❯
		                         """;
		final TNGraph<STNUEdge> dispatchableG = stnu.getGChecked();
		assertEquals(expectedG, dispatchableG.toString());

		rte = new STNURTE(dispatchableG);
		final STNURTE.RTEState state = rte.rte(STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY, STNURTE.StrategyEnum.LATE_EXECUTION_STRATEGY);
		assertEquals("{❮A❯=>1, ❮B❯=>0, ❮C❯=>11, ❮D❯=>0, ❮X1❯=>12, ❮X2❯=>7, ❮X3❯=>7, ❮Z❯=>0}"
			, (new Object2IntAVLTreeMap<>(state.schedule)).toString());
	}


	/**
	 * Early execution strategy with a rigid component
	 *
	 * @throws WellDefinitionException does not occur
	 */
	@Test
	public void rteEarlyRC() throws WellDefinitionException {
		STNUEdgeInt e = new STNUEdgeInt("X2X3", 0);
		g.addEdge(e, X2, X3);
		e = new STNUEdgeInt("X3X2", 0);
		g.addEdge(e, X3, X2);

		final STNU.STNUCheckStatus status =
			stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);

		assertTrue(status.isControllable());
		final String expectedG = """
		                         %TNGraph: g
		                         %Nodes:
		                         ❮A❯
		                         ❮B❯
		                         ❮C❯
		                         ❮D❯
		                         ❮X1❯
		                         ❮X2❯
		                         ❮X3❯
		                         ❮Z❯
		                         %Edges:
		                         ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                         ❮A❯--❮AZ; requirement; -1; ❯-->❮Z❯
		                         ❮B❯--❮B_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮C_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                         ❮D❯--❮D_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1C; requirement; -1; ❯-->❮C❯
		                         ❮X2❯--❮X2-A; derived; UC(C):-6❯-->❮A❯
		                         ❮X2❯--❮X2_Z; derived; 0; ❯-->❮Z❯
		                         ❮X2❯--❮X2C; requirement; 4; ❯-->❮C❯
		                         ❮X2❯--❮X2X3; requirement; 0; ❯-->❮X3❯
		                         ❮X3❯--❮X3-A; derived; UC(C):-6❯-->❮A❯
		                         ❮X3❯--❮X3_Z; derived; 0; ❯-->❮Z❯
		                         ❮X3❯--❮X3X2; requirement; 0; ❯-->❮X2❯
		                         """;
		final TNGraph<STNUEdge> dispatchableG = stnu.getGChecked();
		assertEquals(expectedG, dispatchableG.toString());

		rte = new STNURTE(dispatchableG);
		final STNURTE.RTEState state = rte.rte(STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY, STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY);
		assertEquals("{❮A❯=>1, ❮B❯=>0, ❮C❯=>2, ❮D❯=>0, ❮X1❯=>3, ❮X2❯=>2, ❮X3❯=>2, ❮Z❯=>0}"
			, (new Object2IntAVLTreeMap<>(state.schedule)).toString());
	}


	/**
	 * Late execution strategy
	 *
	 * @throws WellDefinitionException does not occur
	 */
	@Test
	public void rteLate() throws WellDefinitionException {
		final STNU.STNUCheckStatus status =
			stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);

		assertTrue(status.isControllable());
		final String expectedG = """
		                         %TNGraph: g
		                         %Nodes:
		                         ❮A❯
		                         ❮B❯
		                         ❮C❯
		                         ❮D❯
		                         ❮X1❯
		                         ❮X2❯
		                         ❮X3❯
		                         ❮Z❯
		                         %Edges:
		                         ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                         ❮A❯--❮AZ; requirement; -1; ❯-->❮Z❯
		                         ❮B❯--❮B_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮C_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                         ❮D❯--❮D_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1C; requirement; -1; ❯-->❮C❯
		                         ❮X2❯--❮X2-A; derived; UC(C):-6❯-->❮A❯
		                         ❮X2❯--❮X2_Z; derived; 0; ❯-->❮Z❯
		                         ❮X2❯--❮X2C; requirement; 4; ❯-->❮C❯
		                         ❮X3❯--❮X3_Z; derived; 0; ❯-->❮Z❯
		                         """;
		final TNGraph<STNUEdge> dispatchableG = stnu.getGChecked();
		assertEquals(expectedG, dispatchableG.toString());

		rte = new STNURTE(dispatchableG);
		final STNURTE.RTEState state = rte.rte(STNURTE.StrategyEnum.LATE_EXECUTION_STRATEGY, STNURTE.StrategyEnum.LATE_EXECUTION_STRATEGY);
		//2147483647 = +∞
		assertEquals(
			"{❮A❯=>∞, ❮B❯=>∞, ❮C❯=>∞, ❮D❯=>∞, ❮X1❯=>∞, ❮X2❯=>∞, ❮X3❯=>∞, ❮Z❯=>0}"
			, (new Object2IntAVLTreeMap<>(state.schedule)).toString().replaceAll(
				String.valueOf(Constants.INT_POS_INFINITE),
				Constants.INFINITY_SYMBOLstring));
	}

	/**
	 * Late execution strategy with a global  upper bound
	 *
	 * @throws WellDefinitionException does not occur
	 */
	@Test
	public void rteLateWithUpperBound() throws WellDefinitionException {
		STNUEdge e = new STNUEdgeInt("X3B", 1);
		g.addEdge(e, X3, B);

		e = new STNUEdgeInt("BX1", 1);
		g.addEdge(e, B, X1);

		e = new STNUEdgeInt("DX3", 1);
		g.addEdge(e, D, X3);

		e = new STNUEdgeInt("X2D", 1);
		g.addEdge(e, X2, D);

		e = new STNUEdgeInt("ZX2", 8);
		g.addEdge(e, Z, X2);

		final STNU.STNUCheckStatus status =
			stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);

		assertTrue(status.isControllable());
		final String expectedG = """
		                         %TNGraph: g
		                         %Nodes:
		                         ❮A❯
		                         ❮B❯
		                         ❮C❯
		                         ❮D❯
		                         ❮X1❯
		                         ❮X2❯
		                         ❮X3❯
		                         ❮Z❯
		                         %Edges:
		                         ❮A❯--❮AC; contingent; LC(C):1❯-->❮C❯
		                         ❮A❯--❮AZ; requirement; -1; ❯-->❮Z❯
		                         ❮B❯--❮B-A; derived; UC(C):-10❯-->❮A❯
		                         ❮B❯--❮B-C; derived; 0; ❯-->❮C❯
		                         ❮B❯--❮B_Z; derived; 0; ❯-->❮Z❯
		                         ❮B❯--❮BX1; requirement; 1; ❯-->❮X1❯
		                         ❮C❯--❮C_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮CA; contingent; UC(C):-10❯-->❮A❯
		                         ❮D❯--❮D-A; derived; UC(C):-8❯-->❮A❯
		                         ❮D❯--❮D_Z; derived; 0; ❯-->❮Z❯
		                         ❮D❯--❮DX3; requirement; 1; ❯-->❮X3❯
		                         ❮X1❯--❮X1_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1C; requirement; -1; ❯-->❮C❯
		                         ❮X2❯--❮X2-A; derived; UC(C):-7❯-->❮A❯
		                         ❮X2❯--❮X2_Z; derived; 0; ❯-->❮Z❯
		                         ❮X2❯--❮X2C; requirement; 4; ❯-->❮C❯
		                         ❮X2❯--❮X2D; requirement; 1; ❯-->❮D❯
		                         ❮X3❯--❮X3-A; derived; UC(C):-9❯-->❮A❯
		                         ❮X3❯--❮X3_Z; derived; 0; ❯-->❮Z❯
		                         ❮X3❯--❮X3B; requirement; 1; ❯-->❮B❯
		                         ❮Z❯--❮Z-A; derived; 1; ❯-->❮A❯
		                         ❮Z❯--❮ZX2; requirement; 8; ❯-->❮X2❯
		                         """;
		final TNGraph<STNUEdge> dispatchableG = stnu.getGChecked();
		assertEquals(expectedG, dispatchableG.toString());

		rte = new STNURTE(dispatchableG);
		final STNURTE.RTEState state = rte.rte(STNURTE.StrategyEnum.LATE_EXECUTION_STRATEGY, STNURTE.StrategyEnum.LATE_EXECUTION_STRATEGY);
		//2147483647 = +∞
		assertEquals(
			"{❮A❯=>1, ❮B❯=>11, ❮C❯=>11, ❮D❯=>9, ❮X1❯=>12, ❮X2❯=>8, ❮X3❯=>10, ❮Z❯=>0}"
			, (new Object2IntAVLTreeMap<>(state.schedule)).toString().replaceAll(
				String.valueOf(Constants.INT_POS_INFINITE),
				Constants.INFINITY_SYMBOLstring));
	}

	/**
	 * @throws Exception nothing
	 */
	@Before
	public void setUp() throws Exception {
		A = new LabeledNode("A");
		B = new LabeledNode("B");
		C = new LabeledNode("C");
		D = new LabeledNode("D");
		X1 = new LabeledNode("X1");
		X2 = new LabeledNode("X2");
		X3 = new LabeledNode("X3");
		Z = new LabeledNode("Z");
		g = new TNGraph<>("g", STNUEdgeInt.class);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(C);
		g.addVertex(D);
		g.addVertex(X1);
		g.addVertex(X2);
		g.addVertex(X3);
		g.addVertex(Z);
		STNUEdge e = new STNUEdgeInt("AC", 10);
		e.setConstraintType(Edge.ConstraintType.contingent);
		g.addEdge(e, A, C);

		e = new STNUEdgeInt("CA", -1);
		e.setConstraintType(Edge.ConstraintType.contingent);
		g.addEdge(e, C, A);

		e = new STNUEdgeInt("AZ", -1);
		g.addEdge(e, A, Z);

		e = new STNUEdgeInt("X1C", -1);
		g.addEdge(e, X1, C);

		e = new STNUEdgeInt("X2C", 4);
		g.addEdge(e, X2, C);

		stnu = new STNU(g);
	}


	/**
	 * Early execution strategy test case proposed by Léon Planken.
	 *
	 * @throws WellDefinitionException does not occur
	 */
	@Test
	public void rteEarlyWithOneCtg() throws WellDefinitionException {
		g = new TNGraph<>("g", STNUEdgeInt.class);
		g.addVertex(A);
		g.addVertex(C);
		g.addVertex(X1);
		g.addVertex(X2);
		g.addVertex(Z);

		STNUEdgeInt e = new STNUEdgeInt("A-C", 6);
		e.setConstraintType(Edge.ConstraintType.contingent);
		g.addEdge(e, A, C);

		e = new STNUEdgeInt("C-A", -2);
		e.setConstraintType(Edge.ConstraintType.contingent);
		g.addEdge(e, C, A);

		e = new STNUEdgeInt("X1-A", -9);
		g.addEdge(e, X1, A);

		e = new STNUEdgeInt("X2-X1", 1);
		g.addEdge(e, X2, X1);

		e = new STNUEdgeInt("X2-C", 0);
		g.addEdge(e, X2, C);

		stnu.setG(g);
		final STNU.STNUCheckStatus status =
			stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);

		assertTrue(status.isControllable());
		final String expectedG = """
		                         %TNGraph: g
		                         %Nodes:
		                         ❮A❯
		                         ❮C❯
		                         ❮X1❯
		                         ❮X2❯
		                         ❮Z❯
		                         %Edges:
		                         ❮A❯--❮A-C; contingent; LC(C):2❯-->❮C❯
		                         ❮A❯--❮A_Z; derived; 0; ❯-->❮Z❯
		                         ❮C❯--❮C-A; contingent; UC(C):-6❯-->❮A❯
		                         ❮C❯--❮C_Z; derived; 0; ❯-->❮Z❯
		                         ❮X1❯--❮X1-A; requirement; -9; ❯-->❮A❯
		                         ❮X1❯--❮X1_Z; derived; 0; ❯-->❮Z❯
		                         ❮X2❯--❮X2-A; derived; -8; ❯-->❮A❯
		                         ❮X2❯--❮X2-C; requirement; 0; ❯-->❮C❯
		                         ❮X2❯--❮X2-X1; requirement; 1; ❯-->❮X1❯
		                         ❮X2❯--❮X2_Z; derived; 0; ❯-->❮Z❯
		                         """;
		final TNGraph<STNUEdge> dispatchableG = stnu.getGChecked();
		assertEquals(expectedG, dispatchableG.toString());

		rte = new STNURTE(dispatchableG);
		final STNURTE.RTEState state = rte.rte(STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY, STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY);
		assertEquals("{❮A❯=>0, ❮C❯=>2, ❮X1❯=>9, ❮X2❯=>8, ❮Z❯=>0}"
			, (new Object2IntAVLTreeMap<>(state.schedule)).toString());
	}

}
