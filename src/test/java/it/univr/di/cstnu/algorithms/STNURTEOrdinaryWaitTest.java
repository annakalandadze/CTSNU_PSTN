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
public class STNURTEOrdinaryWaitTest {
	private LabeledNode Start23, Finish12, Start12, Start16;

	private TNGraph<STNUEdge> g;
	private STNU stnu;
	private STNURTE rte;

	/**
	 * Early execution strategy
	 *
	 * @throws WellDefinitionException does not occur
	 */
	@Test
	public void rteWait() throws WellDefinitionException {
		final STNU.STNUCheckStatus status =
			stnu.dynamicControllabilityCheck(STNU.CheckAlgorithm.Morris2014Dispatchable);

		assertTrue(status.isControllable());
		final TNGraph<STNUEdge> dispatchableG = stnu.getGChecked();

		rte = new STNURTE(dispatchableG);

		boolean success = true;
		try {
			final STNURTE.RTEState state = rte.rte(STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY, STNURTE.StrategyEnum.EARLY_EXECUTION_STRATEGY);
		} catch (IllegalStateException e) {
			success = false;
		}
		assertTrue(success);
	}


	/**
	 * @throws Exception nothing
	 */
	@Before
	public void setUp() throws Exception {
		Start12 = new LabeledNode("12_Start");
		Start16 = new LabeledNode("16_Start");
		Start23 = new LabeledNode("23_Start");
		Finish12 = new LabeledNode("12_Finish");
		g = new TNGraph<>("g", STNUEdgeInt.class);
		g.addVertex(Start12);
		g.addVertex(Start16);
		g.addVertex(Start23);
		g.addVertex(Finish12);

		STNUEdge e = new STNUEdgeInt("23s-12f", 0);
		g.addEdge(e, Start23, Finish12);

		e = new STNUEdgeInt("23s-16s", 1);
		g.addEdge(e, Start23, Start16);

		e = new STNUEdgeInt("16s-12s", -9);
		g.addEdge(e, Start16, Start12);

		e = new STNUEdgeInt("12s-12f", 6);
		e.setConstraintType(Edge.ConstraintType.contingent);
		g.addEdge(e, Start12, Finish12);

		e = new STNUEdgeInt("12f-12s", -2);
		e.setConstraintType(Edge.ConstraintType.contingent);
		g.addEdge(e, Finish12, Start12);

		stnu = new STNU(g);
	}

}
