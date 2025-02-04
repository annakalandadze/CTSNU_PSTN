package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.graph.*;
import it.univr.di.cstnu.graph.Edge.ConstraintType;
import it.univr.di.labeledvalue.ALabel;
import it.univr.di.labeledvalue.ALabelAlphabet;
import it.univr.di.labeledvalue.Label;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author posenato
 */
public class FTNUUTest {

	/**
	 * Default implementation class for CSTNEdge
	 */
	static final Class<? extends CSTNPSUEdge> EDGE_IMPL_CLASS = CSTNPSUEdgePluggable.class;

	/**
	 *
	 */
	FTNU ftnu;

	/**
	 *
	 */
	LabeledNode Z;

	/**
	 *
	 */
	ALabelAlphabet alphabet;

	/**
	 *
	 */
	TNGraph<CSTNPSUEdge> g;

	/**
	 * @throws Exception  nope
	 */
	@Before
	public void setUp() throws Exception {
		alphabet = new ALabelAlphabet();
		g = new TNGraph<>("no name",EDGE_IMPL_CLASS, alphabet);
		Z = LabeledNodeSupplier.get("Z");
		g.setZ(Z);
		ftnu = new FTNU(g);
	}

	/**
	 *
	 * @throws WellDefinitionException if there is a definition error
	 */
	@Test
	public final void testPrototypalLink() throws WellDefinitionException {
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode C = LabeledNodeSupplier.get("C");
		final LabeledNode Ω = LabeledNodeSupplier.get("Ω");
		g.addVertex(A);
		g.addVertex(C);
		g.addVertex(Ω);
		g.setZ(Z);

		final ALabel CaLabel = new ALabel(C.getName(), alphabet);

		final CSTNPSUEdge CA = g.getEdgeFactory().get("CA");
		CA.mergeUpperCaseValue(Label.emptyLabel, CaLabel, -6);
		CA.mergeLabeledValue(Label.emptyLabel, -1);
		CA.setConstraintType(ConstraintType.contingent);

		final CSTNPSUEdge AC = g.getEdgeFactory().get("AC");
		AC.mergeLowerCaseValue(Label.emptyLabel, CaLabel, 3);
		AC.mergeLabeledValue(Label.emptyLabel, 10);
		AC.setConstraintType(ConstraintType.contingent);

		final CSTNPSUEdge ΩC = g.getEdgeFactory().get("ΩC");
		ΩC.mergeLabeledValue(Label.emptyLabel, -1);

		final CSTNPSUEdge CΩ = g.getEdgeFactory().get("CΩ");
		CΩ.mergeLabeledValue(Label.emptyLabel, 10);

		final CSTNPSUEdge ZA = g.getEdgeFactory().get("ZA");
		ZA.mergeLabeledValue(Label.emptyLabel, 0);

		g.addEdge(AC, A, C);
		g.addEdge(CA, C, A);
		g.addEdge(ΩC, Ω, C);
		g.addEdge(CΩ, C, Ω);
//		this.g.addEdge(ZA, this.Z, A);

		ftnu.setG(g);

//		CSTNUCheckStatus status = this.ftnu.dynamicControllabilityCheck();
//		assertTrue(status.consistency);

		final CSTNPSU.PrototypalLink prototypalLink = ftnu.getPrototypalLink();
		assertEquals(2, prototypalLink.getLowerBound());
		assertEquals(30, prototypalLink.getLowerGuard());
		assertEquals(7, prototypalLink.getUpperGuard());
		assertEquals(30, prototypalLink.getUpperBound());
		assertEquals(0, prototypalLink.getContingency());
	}
}
