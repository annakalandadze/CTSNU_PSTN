/**
 *
 */
package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.algorithms.AbstractCSTN.CSTNCheckStatus;
import it.univr.di.cstnu.graph.CSTNEdge;
import it.univr.di.cstnu.graph.LabeledNode;
import it.univr.di.cstnu.graph.LabeledNodeSupplier;
import it.univr.di.labeledvalue.AbstractLabeledIntMap;
import it.univr.di.labeledvalue.Label;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class CSTNwoNodeLabelTest extends CSTNTest {

	/**
	 *
	 */
	public CSTNwoNodeLabelTest() {
		cstn = new CSTNwoNodeLabel(g);
	}

	/**
	 * @throws java.lang.Exception  none
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for
	 *
	 * <pre>
	 * P? &lt;--- X &lt;---- Y
	 * ^
	 * |
	 * |
	 * A_p?         B?
	 * </pre>
	 */
	@Override
	@Test
	public final void testAlphaBetaGamaPrime() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		A.setLabel(Label.parse("¬p"));
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		B.setLabel(Label.parse("a¬p"));
		final LabeledNode G = LabeledNodeSupplier.get("G?", 'g');

		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(G);
		g.addVertex(X);
		g.addVertex(Y);

		final CSTNEdge XP = g.getEdgeFactory().get("XP");
		XP.mergeLabeledValue(Label.emptyLabel, 10);
		XP.mergeLabeledValue(Label.parse("¬a¬p"), 8);
		XP.mergeLabeledValue(Label.parse("¬b"), -1);
		XP.mergeLabeledValue(Label.parse("ba¬p"), 9);

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("¬b"), 9);
		YX.mergeLabeledValue(Label.parse("b"), -10);

		final CSTNEdge AP = g.getEdgeFactory().get("AP");
		AP.mergeLabeledValue(Label.parse("¬p"), -1);

		g.addEdge(XP, X, P);
		g.addEdge(YX, Y, X);
		g.addEdge(AP, A, P);

		wellDefinition();

		// CHILDREN ARE NOT CONSIDERED because it is assumed a streamlined CSTN
		assertEquals("¬abg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("¬ab"), Label.parse("pbg")).toString());

		assertEquals("¬abg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("¬ab"), Label.parse("¬pbg")).toString());

		assertEquals("¬abg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("bp¬ag")).toString());

		assertEquals("¬abg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("b¬pg¬a")).toString());

		B.setLabel(Label.parse("a"));
		// now P has only 'a' as child, but it doesn't matter!
		assertEquals("¬a¿bg", // if children are not considered
				cstn.makeBetaGammaDagger4qR3(Y, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("p¬b¬ag")).toString());

		assertEquals("¬abg", // if children are not considered
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("¬pbg¬a")).toString());

		assertEquals("¬a¿bg", // if children are not considered
				cstn.makeBetaGammaDagger4qR3(Y, P, P.getPropositionObserved(), Label.parse("¬b"), Label.parse("b¬apg")).toString());
	}

	/**
	 */
	@Override
	@Test
	public void testLabelModificationR0() {
		final CSTNEdge px = g.getEdgeFactory().get("PX");
		px.mergeLabeledValue(Label.parse("ABp"), -10);
		px.mergeLabeledValue(Label.parse("AB¬p"), 0);
		px.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);
		px.mergeLabeledValue(Label.parse("C¬p"), 1);
		g.addEdge(px, P, X);
		// wellDefinition(g);

		cstn.labelModificationR0qR0(P, X, px);

		final CSTNEdge pxOK = g.getEdgeFactory().get("XY");
		pxOK.mergeLabeledValue(Label.parse("AB"), -10);
		pxOK.mergeLabeledValue(Label.parse("¬A¬B"), 0);// std semantics
		pxOK.mergeLabeledValue(Label.parse("C¬p"), 1);

		assertEquals("R0: p?X labeled values.", pxOK.getLabeledValueMap(), px.getLabeledValueMap());
	}

	/**
	 */
	@Override
	@Test
	public void testLabelModificationR0Z() {
		final CSTNEdge pz = g.getEdgeFactory().get("PZ");
		pz.mergeLabeledValue(Label.parse("ABp"), -10);
		pz.mergeLabeledValue(Label.parse("AB¬p"), 0);
		pz.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);
		g.addEdge(pz, P, Z);
		g.addVertex(LabeledNodeSupplier.get("A", 'A'));
		g.addVertex(LabeledNodeSupplier.get("B", 'B'));

		try {
			cstn.initAndCheck();
		} catch (WellDefinitionException e) {
			fail(e.getMessage());
		}
		cstn.labelModificationR0qR0(P, Z, pz);

		final CSTNEdge pzOK = g.getEdgeFactory().get("PZ");
		pzOK.mergeLabeledValue(Label.parse("AB"), -10);
		pzOK.mergeLabeledValue(Label.emptyLabel, 0);

		assertEquals("R0: P?Z labeled values.", pzOK.getLabeledValueMap(), pz.getLabeledValueMap());
	}

	/**
	 */
	@Override
	@Test
	public final void testLabelModificationR3() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		final LabeledNode C = LabeledNodeSupplier.get("C?", 'c');
		final LabeledNode G = LabeledNodeSupplier.get("G?", 'g');
		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(G);
		g.addVertex(C);
		g.addVertex(X);
		g.addVertex(Y);
		g.setZ(Z);

		final CSTNEdge px = g.getEdgeFactory().get("PX");
		px.mergeLabeledValue(Label.parse("¬b"), 0);
		px.mergeLabeledValue(Label.parse("ab"), -10);

		final CSTNEdge yx = g.getEdgeFactory().get("YX");
		yx.mergeLabeledValue(Label.parse("bgp"), -4);
		yx.mergeLabeledValue(Label.parse("cp"), -10);
		yx.mergeLabeledValue(Label.parse("c¬p"), 11);

		g.addEdge(px, P, X);
		g.addEdge(yx, Y, X);

		wellDefinition();

		// System.out.println(g);

		cstn.labelModificationR3qR3(Y, X, yx);

		final CSTNEdge yxOK = g.getEdgeFactory().get("YX");
		// std semantics
		// yxOK.mergeLabeledValue(Label.parse("¬abgp"), -4);
		yxOK.mergeLabeledValue(Label.parse("abc"), -10);
		yxOK.mergeLabeledValue(Label.parse("abg"), -4);
		yxOK.mergeLabeledValue(Label.parse("¬bc"), 0);// std semantics R3 rule with reaction time
		yxOK.mergeLabeledValue(Label.parse("bgp"), -4);
		yxOK.mergeLabeledValue(Label.parse("c¬p"), 11);
		yxOK.mergeLabeledValue(Label.parse("cp"), -10);

		assertEquals("R3: yx labeled values.", yxOK.getLabeledValueSet(), yx.getLabeledValueSet());
	}

	/**
	 * Test method
	 */
	@Override
	@Test
	public final void testLabelModificationR3withUnkown() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		final LabeledNode C = LabeledNodeSupplier.get("C?", 'c');
		final LabeledNode G = LabeledNodeSupplier.get("G?", 'g');
		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(G);
		g.addVertex(C);
		g.addVertex(X);
		g.addVertex(Y);
		g.setZ(Z);

		final CSTNEdge pz = g.getEdgeFactory().get("PZ");
		pz.mergeLabeledValue(Label.parse("¬b"), -1);
		pz.mergeLabeledValue(Label.parse("ab"), -10);
		pz.mergeLabeledValue(Label.parse("¿c"), -11);

		final CSTNEdge xz = g.getEdgeFactory().get("XZ");
		xz.mergeLabeledValue(Label.parse("abgp"), -4);
		xz.mergeLabeledValue(Label.parse("abcp"), -10);
		xz.mergeLabeledValue(Label.parse("abc¬p"), -11);
		xz.mergeLabeledValue(Label.parse("ab¿p"), -15);

		g.addEdge(pz, P, Z);
		g.addEdge(xz, X, Z);
		// System.out.println(g);
		cstn.labelModificationR3qR3(X, Z, xz);

		assertEquals("R3: yx labeled values.", AbstractLabeledIntMap.parse("{(-11, abc¬p) (-11, ab¿c) (-15, ab¿p) (-10, ab) }"), xz.getLabeledValueMap());
	}

	/**
	 * Test method for
	 */
	@Override
	@Test
	public final void testLabeledPropagation() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(X);
		g.addVertex(Y);

		final CSTNEdge XP = g.getEdgeFactory().get("XP");
		XP.mergeLabeledValue(Label.emptyLabel, 10);
		XP.mergeLabeledValue(Label.parse("¬a"), 8);
		XP.mergeLabeledValue(Label.parse("¬b"), -1);
		XP.mergeLabeledValue(Label.parse("b"), 9);

		final CSTNEdge PY = g.getEdgeFactory().get("PY");
		PY.mergeLabeledValue(Label.parse("¬b"), 9);
		PY.mergeLabeledValue(Label.parse("b"), -10);

		final CSTNEdge XY = g.getEdgeFactory().get("XY");

		g.addEdge(XP, X, P);
		g.addEdge(XY, X, Y);
		g.addEdge(PY, P, Y);

		cstn.setPropagationOnlyToZ(false);

		wellDefinition();

		cstn.labelPropagation(X, P, Y, XP, PY, XY);

		// System.out.println(XP);
		// System.out.println(PY);

		final CSTNEdge XYok = g.getEdgeFactory().get("XY");
		// xyOK.mergeLabeledValue(Label.parse("¬A¬B"), 17);
		// EqLP+
		// XYok.mergeLabeledValue(Label.parse("¬b"), 8);if positive value are not admitted.
		XYok.mergeLabeledValue(Label.parse("¬ab"), -2);
		XYok.mergeLabeledValue(Label.parse("b"), -1);
		XYok.mergeLabeledValue(Label.parse("¿b"), -11);

		assertEquals("No case: XY labeled values.", XYok.getLabeledValueMap(), XY.getLabeledValueMap());

		XP.clear();
		XP.mergeLabeledValue(Label.parse("b"), -1);
		XP.mergeLabeledValue(Label.parse("¬b"), 1);
		XY.clear();
		cstn.labelPropagation(X, P, Y, XP, PY, XY);// Y is Z!!!

		XYok.clear();
		// XYok.mergeLabeledValue(Label.parse("¬b"), 10);if positive value are not admitted.
		XYok.mergeLabeledValue(Label.parse("b"), -11);
		assertEquals("No case: XY labeled values.", XYok.getLabeledValueMap(), XY.getLabeledValueMap());

		XP.clear();
		// System.out.println("xp: " +XP);
		XP.mergeLabeledValue(Label.parse("b"), -1);
		// System.out.println("xp: " +XP);
		XP.mergeLabeledValue(Label.parse("¬b"), -10);
		XY.clear();

		// System.out.println("xp: " +XP);
		// System.out.println("py: " +PY);
		// System.out.println("xy: " +xy);

		cstn.labelPropagation(X, P, Y, XP, PY, XY);// Y is Z!!!

		// System.out.println("xy: " +xy);

		XYok.mergeLabeledValue(Label.parse("¿b"), -20);
		XYok.mergeLabeledValue(Label.parse("¬b"), -1);

		assertEquals("No case: XY labeled values.", XYok.getLabeledValueMap(), XY.getLabeledValueMap());
	}

	/**
	 * Test method for
	 */
	@Override
	@Test
	public final void testLabeledPropagation1() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		g.addVertex(P);
		g.addVertex(X);
		g.addVertex(Y);

		// Ricostruisco i passi di un caso di errore
		final CSTNEdge XZ = g.getEdgeFactory().get("XZ");
		XZ.mergeLabeledValue(Label.parse("p"), -2);
		XZ.mergeLabeledValue(Label.parse("¿p"), -5);
		XZ.mergeLabeledValue(Label.parse("¬p"), -1);
		XZ.mergeLabeledValue(Label.parse("¿p"), -5);

		final CSTNEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.parse("p"), -2);
		XY.mergeLabeledValue(Label.emptyLabel, 0);

		final CSTNEdge YZ = g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.parse("¬p"), -1);
		YZ.mergeLabeledValue(Label.emptyLabel, 0);

		g.addEdge(XY, X, Y);
		g.addEdge(XZ, X, Z);
		g.addEdge(YZ, Y, Z);
		// wellDefinition(g);

		// System.out.println(g);
		// System.out.println(g1);

		cstn.labelPropagation(X, Y, Z, XY, YZ, XZ);
		ok.clear();
		ok.mergeLabeledValue("¬p", -1);
		ok.mergeLabeledValue("p", -2);
		ok.mergeLabeledValue("¿p", -5);
		assertEquals("Label propagation rule with particular values", ok.getLabeledValueMap(), XZ.getLabeledValueMap());
		// (¬p, -1, {X, Y}) nel caso in cui accettiamo la propagazione dei node set con valori di edge positivi

		// ❮XZ; normal; {(¬p, -1) (p, -2) (¿p, -5, {X, Y}) }; ❯
		// assertEquals("Label propagation rule with particular values", "❮XZ; normal; {(¬p, -1) (p, -2) (¿p, -5, {X, Y}) }; ❯", XZ.toString());
	}

	/**
	 * Test method for
	 */
	@Override
	@Test
	public final void testLabeledPropagation2() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		g.addVertex(P);
		g.addVertex(X);
		g.addVertex(Y);

//		ObjectAVLTreeSet<String> nodeSet = new ObjectAVLTreeSet<>();
//		nodeSet.add(this.X.getName());
//		nodeSet.add(Y.getName());

		// Ricostruisco i passi di un caso di errore
		final CSTNEdge YZ = g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.emptyLabel, 0);

		final CSTNEdge XZ = g.getEdgeFactory().get("XZ");
		XZ.mergeLabeledValue(Label.emptyLabel, 0);

		final CSTNEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.parse("p"), -2);
		XY.mergeLabeledValue(Label.parse("¬p"), -1);
		XY.mergeLabeledValue(Label.emptyLabel, 0);

		g.addEdge(XY, X, Y);
		g.addEdge(YZ, Y, Z);
		g.addEdge(XZ, X, Z);

		cstn.setG(g);
		cstn.labelPropagation(X, Y, Z, XY, YZ, XZ);

		assertEquals("Label propagation rule with particular values", AbstractLabeledIntMap.parse("{(-1, ¬p) (-2, p) }"), XZ.getLabeledValueMap());
	}

	/**
	 * Test method for creating an -infty loop
	 */
	@Override
	@Test
	public final void testLabeledPropagation3() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		g.addVertex(P);
		g.addVertex(X);
		g.addVertex(Y);

		final CSTNEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.parse("p"), -2);
		XY.mergeLabeledValue(Label.parse("¿p"), -5);
		XY.mergeLabeledValue(Label.parse("¬p"), 3);
		XY.mergeLabeledValue(Label.parse("¿p"), -5);

		assertEquals("XY: ", AbstractLabeledIntMap.parse("{(3, ¬p) (-2, p) (-5, ¿p) }"), XY.getLabeledValueMap());

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("p"), 3);
		YX.mergeLabeledValue(Label.parse("¬p"), -1);

		assertEquals("YX: ", AbstractLabeledIntMap.parse("{(-1, ¬p) (3, p) }"), YX.getLabeledValueMap());

		final CSTNEdge XX = g.getEdgeFactory().get("XX");

		g.addEdge(XX, X, X);
		g.addEdge(XY, X, Y);
		g.addEdge(YX, Y, X);
		// System.out.println(g);
		cstn.setPropagationOnlyToZ(false);

		try {
			cstn.initAndCheck();
		} catch (WellDefinitionException e) {
			fail(e.getMessage());
		}
		assertEquals("XY: ", AbstractLabeledIntMap.parse("{(3, ¬p) (-2, p) (-5, ¿p) }"), XY.getLabeledValueMap());
		assertEquals("YX: ", AbstractLabeledIntMap.parse("{(-1, ¬p) (3, p) }"), YX.getLabeledValueMap());
		assertEquals("XX: ", AbstractLabeledIntMap.parse("{}"), XX.getLabeledValueMap());

		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		// Remember that not negative value on self loop are never stored!
		assertEquals("XX: ", "{(-∞, ¿p) }", XX.getLabeledValueMap().toString());

		XY.mergeLabeledValue(Label.parse("¬p"), 1);
		// reaction time is 1
		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		assertEquals("XX: ", "{(-∞, ¿p) }", XX.getLabeledValueMap().toString());
	}

	/**
	 * Test method for checking that all propagations are done
	 */
	@Override
	@Test
	public final void testLabeledPropagation4() {
		final LabeledNode Q = LabeledNodeSupplier.get("Q?", 'q');
		final LabeledNode X2 = LabeledNodeSupplier.get("X2");
		final LabeledNode X3 = LabeledNodeSupplier.get("X3");
		final LabeledNode X4 = LabeledNodeSupplier.get("X4");
		final LabeledNode X5 = LabeledNodeSupplier.get("X5");
		final LabeledNode X6 = LabeledNodeSupplier.get("X6");
		g.addVertex(P);
		g.addVertex(Q);
		g.addVertex(X2);
		g.addVertex(X3);
		g.addVertex(X4);
		g.addVertex(X5);
		g.addVertex(X6);

		CSTNEdge e = g.getEdgeFactory().get("X2X4");
		e.mergeLabeledValue(Label.parse("p"), -2);
		g.addEdge(e, X2, X4);

		e = g.getEdgeFactory().get("X4X3");
		e.mergeLabeledValue(Label.parse("¬p"), -1);
		g.addEdge(e, X4, X3);

		e = g.getEdgeFactory().get("X3Q");
		e.mergeLabeledValue(Label.parse("p"), -2);
		g.addEdge(e, X3, Q);

		e = g.getEdgeFactory().get("QX2");
		e.mergeLabeledValue(Label.parse("¬p"), -3);
		g.addEdge(e, Q, X2);

		e = g.getEdgeFactory().get("X4X5");
		e.mergeLabeledValue(Label.parse("¬q"), -3);
		g.addEdge(e, X4, X5);

		e = g.getEdgeFactory().get("X5P");
		e.mergeLabeledValue(Label.parse("q"), -2);
		g.addEdge(e, X5, P);

		e = g.getEdgeFactory().get("PX6");
		e.mergeLabeledValue(Label.parse("¬q"), -1);
		g.addEdge(e, P, X6);

		e = g.getEdgeFactory().get("X6X4");
		e.mergeLabeledValue(Label.parse("q"), -2);
		g.addEdge(e, X6, X4);

		wellDefinition();

		// System.out.println(g);

		final CSTNCheckStatus status;
		try {
			status = cstn.dynamicConsistencyCheck();
		} catch (WellDefinitionException e1) {
			fail(e1.getMessage());
			return;
		}
		assertFalse("Check the NOT DC qLoop29morning2qL.cstn. If this message appears, then checking wronlgy says CSTN is consistent!", status.consistency);
	}

	/**
	 * Test method to check if a tNGraph requiring only R0-R3 application is checked well. .
	 *
	 * @throws WellDefinitionException  none
	 */
	@Override
	@Test
	public final void testQstar() throws WellDefinitionException {
		final LabeledNode Q = LabeledNodeSupplier.get("Q?", 'q');
		final LabeledNode R = LabeledNodeSupplier.get("R?", 'r');
		g.addVertex(P);
		g.addVertex(Q);
		g.addVertex(R);

		// Ricostruisco i passi di un caso di errore
		final CSTNEdge RZ = g.getEdgeFactory().get("RZ");
		RZ.mergeLabeledValue(Label.parse("p¬q"), -16);

		final CSTNEdge QZ = g.getEdgeFactory().get("QZ");
		QZ.mergeLabeledValue(Label.parse("p¬r"), -14);

		final CSTNEdge PZ = g.getEdgeFactory().get("PZ");
		PZ.mergeLabeledValue(Label.parse("¬q¬r"), -12);

		g.addEdge(RZ, R, Z);
		g.addEdge(QZ, Q, Z);
		g.addEdge(PZ, P, Z);
		cstn.dynamicConsistencyCheck();

		final CSTNEdge RZnew = g.findEdge(R.getName(), Z.getName());
		// Std semantics
		ok.clear();
		ok.mergeLabeledValue("⊡", -12);
		ok.mergeLabeledValue("p", -14);
		ok.mergeLabeledValue("p¬q", -16);
		// assertEquals("Qstar check"okRZ; normal; {(⊡, -12) (p, -14) (p¬q -16) }; ❯", RZnew.toString()); std semantics
		// assertEquals("Qstar check"okRZ; normal; {(⊡, -13) (p, -15) (p¬q -16) }; ❯", RZnew.toString()); epsilon semantics
		assertEquals("Qstar check", ok.getLabeledValueMap(), RZnew.getLabeledValueMap());

		final CSTNEdge QZnew = g.findEdge(Q.getName(), Z.getName());
		ok.clear();
		ok.mergeLabeledValue("⊡", -12);
		ok.mergeLabeledValue("p", -14);
		assertEquals("Qstar check", ok.getLabeledValueMap(), QZnew.getLabeledValueMap());

		final CSTNEdge PZnew = g.findEdge(P.getName(), Z.getName());
		ok.clear();
		ok.mergeLabeledValue("⊡", -12);
		assertEquals("Qstar check", ok.getLabeledValueMap(), PZnew.getLabeledValueMap());
	}

	/*
	 * Test method for
	 * {@link it.univr.di.cstnu.algorithms.CSTN#labeledPropagationRule(LabeledNode, LabeledNode, LabeledNode, CSTNEdge, CSTNEdge, LabeledIntEdge)}
	 * .
	 * * Since in 2017-10-10 -infty qlabeled value have been suppressed, this test is not more necessary!
	 */
	// @SuppressWarnings("javadoc")
	// @Test
	// public final void testLabeledPropagationForwardOfInfty() {
	// TNGraph g = new TNGraph(this.labeledIntValueMapClass);
	// LabeledNode Y = g.getNodeFactory().get("Y");
	// g.addVertex(this.P);
	// g.addVertex(this.X);
	// g.addVertex(Y);
	// g.addVertex(this.Z);
	// g.setZ(this.Z);
	//
	// CSTNEdge YZ = this.g.getEdgeFactory().get("YZ");
	// YZ.mergeLabeledValue(Label.emptyLabel, 0);
	// CSTNEdge XZ = this.g.getEdgeFactory().get("XZ");
	// XZ.mergeLabeledValue(Label.emptyLabel, 0);
	//
	// CSTNEdge XY = this.g.getEdgeFactory().get("XY");
	// XY.mergeLabeledValue(Label.parse("p"), -2);
	//
	// CSTNEdge YX = this.g.getEdgeFactory().get("YX");
	// YX.mergeLabeledValue(Label.parse("¬p"), -2);
	//
	// CSTNEdge XX = this.g.getEdgeFactory().get("XX");
	// CSTNEdge YY = this.g.getEdgeFactory().get("YY");
	//
	// g.addEdge(XY, this.X, Y);
	// g.addEdge(YX, Y, this.X);
	// g.addEdge(YZ, Y, this.Z);
	// g.addEdge(XZ, this.X, this.Z);
	// g.addEdge(XX, this.X, this.X);
	// g.addEdge(YY, Y, Y);
	//
	// this.cstn.setG(g);
	// this.cstn.labeledPropagationRule(this.X, Y, this.Z, XY, YZ, XZ);
	// assertEquals("XZ", "{(0, ⊡) (-2, p) }", XZ.getLabeledValueMap().toString());
	//
	// this.cstn.labeledPropagationRule(this.X, Y, this.X, XY, YX, XX);
	// assertEquals("XX", "{(-∞, ¿p) }", XX.getLabeledValueMap().toString());
	//
	// this.cstn.labeledPropagationRule(this.X, this.X, Y, XX, XY, XY);
	// assertEquals("XY", "{(-2, p) (-∞, ¿p) }", XY.getLabeledValueMap().toString());
	//
	// this.cstn.labeledPropagationRule(Y, this.X, Y, YX, XY, YY);
	// assertEquals("", "{(-∞, ¿p) }", YY.getLabeledValueMap().toString());
	// }

	/*
	 * Test method for
	 * {@link it.univr.di.cstnu.algorithms.CSTN#labeledPropagationRule(LabeledNode, LabeledNode, LabeledNode, CSTNEdge, CSTNEdge, LabeledIntEdge)}
	 * .
	 * * Since in 2017-10-10 -infty qlabeled value have been suppressed, this test is not more necessary!
	 */
	// @SuppressWarnings("javadoc")
	// @Test
	// public final void testLabeledPropagationForwardOfInfty1() {
	// TNGraph g = new TNGraph(this.labeledIntValueMapClass);
	// LabeledNode Y = g.getNodeFactory().get("Y");
	// g.addVertex(this.P);
	// g.addVertex(this.X);
	// g.addVertex(Y);
	// g.addVertex(this.Z);
	// g.setZ(this.Z);
	//
	// CSTNEdge YZ = this.g.getEdgeFactory().get("YZ");
	// YZ.mergeLabeledValue(Label.emptyLabel, 0);
	// CSTNEdge XZ = this.g.getEdgeFactory().get("XZ");
	// XZ.mergeLabeledValue(Label.emptyLabel, 0);
	//
	// CSTNEdge XY = this.g.getEdgeFactory().get("XY");
	// XY.mergeLabeledValue(Label.parse("¿p"), -2);
	//
	// CSTNEdge YX = this.g.getEdgeFactory().get("YX");
	// YX.mergeLabeledValue(Label.parse("¬p"), -2);
	//
	// CSTNEdge XX = this.g.getEdgeFactory().get("XX");
	// CSTNEdge YY = this.g.getEdgeFactory().get("YY");
	//
	// g.addEdge(XY, this.X, Y);
	// g.addEdge(YX, Y, this.X);
	// g.addEdge(YZ, Y, this.Z);
	// g.addEdge(XZ, this.X, this.Z);
	// g.addEdge(XX, this.X, this.X);
	// g.addEdge(YY, Y, Y);
	//
	// this.cstn.setG(g);
	// this.cstn.labeledPropagationRule(this.X, Y, this.Z, XY, YZ, XZ);
	// // assertEquals("XZ", "{(0, ⊡) }", eNew.getLabeledValueMap().toString());//if only negative value are q-propagate
	// assertEquals("XZ", "{(0, ⊡) (-2, ¿p) }", XZ.getLabeledValueMap().toString());// if negative sum value are q-propagate
	//
	// this.cstn.labeledPropagationRule(this.X, Y, this.X, XY, YX, XX);
	// // assertTrue(eNew == null);//if only negative value are q-propagate
	//
	// // g.addEdge(XX, X, X);
	// this.cstn.labeledPropagationRule(this.X, this.X, Y, XX, XY, XY);
	// // assertEquals("XY", "{(-2, ¿p) }", eNew.getLabeledValueMap().toString());//if only negative value are q-propagate
	// assertEquals("XY", "{(-∞, ¿p) }", XY.getLabeledValueMap().toString());// if negative sum value are q-propagate
	//
	// this.cstn.labeledPropagationRule(Y, this.X, Y, YX, XY, YY);
	// assertEquals("", "{(-∞, ¿p) }", YY.getLabeledValueMap().toString());
	//
	// this.cstn.labeledPropagationRule(Y, Y, this.X, YY, YX, YX);
	// assertEquals("", "{(-2, ¬p) (-∞, ¿p) }", YX.getLabeledValueMap().toString());
	// }

	/*
	 * Test method for
	 * {@link it.univr.di.cstnu.algorithms.CSTN#labeledPropagationRule(LabeledNode, LabeledNode, LabeledNode, CSTNEdge, CSTNEdge, LabeledIntEdge)}
	 * .
	 * * Since in 2017-10-10 -infty qlabeled value have been suppressed, this test is not more necessary!
	 */
	// @SuppressWarnings("javadoc")
	// @Test
	// public final void testLabeledPropagationBackwardOfInfty() {
	// TNGraph g = new TNGraph(this.labeledIntValueMapClass);
	// LabeledNode Y = g.getNodeFactory().get("Y");
	// g.addVertex(this.P);
	// g.addVertex(this.X);
	// g.addVertex(Y);
	// g.addVertex(this.Z);
	// g.setZ(this.Z);
	//
	// CSTNEdge YZ = this.g.getEdgeFactory().get("YZ");
	// YZ.mergeLabeledValue(Label.emptyLabel, 0);
	// CSTNEdge XZ = this.g.getEdgeFactory().get("XZ");
	// XZ.mergeLabeledValue(Label.emptyLabel, 0);
	//
	// CSTNEdge XY = this.g.getEdgeFactory().get("XY");
	// XY.mergeLabeledValue(Label.parse("p"), -2);
	//
	// CSTNEdge YX = this.g.getEdgeFactory().get("YX");
	// YX.mergeLabeledValue(Label.parse("¬p"), -2);
	//
	// CSTNEdge XX = this.g.getEdgeFactory().get("XX");
	//
	// g.addEdge(XY, this.X, Y);
	// g.addEdge(YX, Y, this.X);
	// g.addEdge(YZ, Y, this.Z);
	// g.addEdge(XZ, this.X, this.Z);
	// g.addEdge(XX, this.X, this.X);
	// this.cstn.setG(g);
	// this.cstn.labeledPropagationRule(this.X, Y, this.Z, XY, YZ, XZ);
	// assertEquals("XZ", "{(0, ⊡) (-2, p) }", XZ.getLabeledValueMap().toString());
	//
	// this.cstn.labeledPropagationRule(this.X, Y, this.X, XY, YX, XX);
	// assertEquals("XX", "{(-∞, ¿p) }", XX.getLabeledValueMap().toString());
	//
	// this.cstn.labeledPropagationRule(Y, this.X, this.X, YX, XX, YX);
	// assertEquals("", "{(-2, ¬p) (-∞, ¿p) }", YX.getLabeledValueMap().toString());
	// }

	/**
	 * Test method for
	 *
	 * <pre>
	 * P? &lt;--- X &lt;---- Y
	 * ^
	 * |
	 * |
	 * A_p?         B?
	 * </pre>
	 */
	@Override
	@Test
	public final void testRemoveChildren() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		A.setLabel(Label.parse("¬p"));
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		B.setLabel(Label.parse("a¬p"));
		final LabeledNode G = LabeledNodeSupplier.get("G?", 'g');

		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(G);
		g.addVertex(X);
		g.addVertex(Y);

		final CSTNEdge XP = g.getEdgeFactory().get("XP");
		XP.mergeLabeledValue(Label.emptyLabel, 10);
		XP.mergeLabeledValue(Label.parse("¬a¬p"), 8);
		XP.mergeLabeledValue(Label.parse("¬b"), -1);
		XP.mergeLabeledValue(Label.parse("ba¬p"), 9);

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("¬b"), 9);
		YX.mergeLabeledValue(Label.parse("b"), -10);

		final CSTNEdge AP = g.getEdgeFactory().get("AP");
		AP.mergeLabeledValue(Label.parse("¬p"), -1);

		g.addEdge(XP, X, P);
		g.addEdge(YX, Y, X);
		g.addEdge(AP, A, P);

		wellDefinition();

		assertEquals("a¿b", cstn.removeChildrenOfUnknown(Label.parse("a¿b")).toString());
		assertEquals("¿p", cstn.removeChildrenOfUnknown(Label.parse("a¿p")).toString());
		assertEquals("¿p", cstn.removeChildrenOfUnknown(Label.parse("ab¿p")).toString());
		assertEquals("¿ap", cstn.removeChildrenOfUnknown(Label.parse("bp¿a")).toString());
		Label a = Label.parse("a");
		a = a.remove(Label.parse("a"));
		assertEquals(Label.emptyLabel, cstn.removeChildrenOfUnknown(a));
	}
}
