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

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class CSTNirTest extends CSTNTest {

	/**
	 *
	 */
	public CSTNirTest() {
		cstn = new CSTNIR(g);
	}

	/**
	 * @throws java.lang.Exception  nope
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
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		A.setLabel(Label.parse("p"));

		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(X);
		g.addVertex(Y);

		final CSTNEdge XP = g.getEdgeFactory().get("XP");
		XP.mergeLabeledValue(Label.emptyLabel, 10);
		XP.mergeLabeledValue(Label.parse("¬ap"), 8);
		XP.mergeLabeledValue(Label.parse("¬b"), -1);
		XP.mergeLabeledValue(Label.parse("bap"), 9);

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("¬b"), 9);
		YX.mergeLabeledValue(Label.parse("b"), -10);

		final CSTNEdge AP = g.getEdgeFactory().get("AP");
		AP.mergeLabeledValue(Label.parse("p"), -1);

		g.addEdge(XP, X, P);
		g.addEdge(YX, Y, X);
		g.addEdge(AP, A, P);

		wellDefinition();

		assertEquals("¬abg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("¬ab"), Label.parse("pbg")).toString());
		assertEquals("¬abg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("¬ab"), Label.parse("¬pbg")).toString());

		assertEquals("bg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("bp¬ag")).toString());
		assertEquals("bg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("b¬pg¬a")).toString());

		assertEquals("¿bg",
				cstn.makeBetaGammaDagger4qR3(Y, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("p¬b¬ag")).toString());// 'a'
																																					// in
																																					// 'ba'
																																					// is
																																					// a
																																					// children
																																					// supposed
																																					// not
																																					// to
																																					// be
																																					// present.
		assertEquals("bg",
				cstn.makeAlphaBetaGammaPrime4R3(Y, X, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("bg¬a")).toString());

		assertEquals("¿bg",
				cstn.makeBetaGammaDagger4qR3(Y, P, P.getPropositionObserved(), Label.parse("¬b"), Label.parse("b¬ag")).toString());
		// assertEquals("bg", this.cstn.makeAlphaBetaGammaPrime(g, Y, X, P, Z, P.getPropositionObserved(), Label.parse("b"), Label.parse("bg¬a")).toString());
	}

	/**
	 *
	 */
	@Override

	@Test
	public final void testLabelModificationR0() {

		final CSTNEdge px = g.getEdgeFactory().get("PX");
		px.mergeLabeledValue(Label.parse("ABp"), -10);
		px.mergeLabeledValue(Label.parse("AB¬p"), 0);
		px.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);
		px.mergeLabeledValue(Label.parse("C¬p"), 1);
		g.addEdge(px, P, X);

		// wellDefinition();

		cstn.labelModificationR0qR0(P, X, px);

		final CSTNEdge pxOK = g.getEdgeFactory().get("XY");
		// if R0 is applied!
		pxOK.mergeLabeledValue(Label.parse("AB"), -10);
		pxOK.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);// ir semantics
		pxOK.mergeLabeledValue(Label.parse("C¬p"), 1);
		// if only qR0 is applied!
		// pxOK.mergeLabeledValue(Label.parse("ABp"), -10);
		// pxOK.mergeLabeledValue(Label.parse("AB¬p"), 0);
		// pxOK.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);
		// pxOK.mergeLabeledValue(Label.parse("C¬p"), 1);

		assertEquals("R0: p?X labeled values.", pxOK.getLabeledValueMap(), px.getLabeledValueMap());
	}

	// /**
	// *
	// CSTNCheckStatus)}.
	// */
	// @SuppressWarnings({ "deprecation", "javadoc" })
	// @Test
	// public final void testLabelModificationR2() {
	// TNGraph g = new TNGraph(labeledIntValueMapClass);
	// LabeledNode P = g.getNodeFactory().get("P?", 'p');
	// LabeledNode A = g.getNodeFactory().get("A?", 'a');
	// LabeledNode B = g.getNodeFactory().get("B?", 'b');
	// LabeledNode C = g.getNodeFactory().get("C?", 'c');
	// LabeledNode X = g.getNodeFactory().get("X");
	// g.addVertex(P);
	// g.addVertex(A);
	// g.addVertex(B);
	// g.addVertex(X);
	// g.addVertex(C);
	//
	// CSTNEdge xp = new CSTNEdge("XP", labeledIntValueMapClass);
	// xp.mergeLabeledValue(Label.parse("ABp"), 10);
	// xp.mergeLabeledValue(Label.parse("AB¬p"), 0);
	// xp.mergeLabeledValue(Label.parse("C¬p"), -1);
	// g.addEdge(xp, X, P);
	//
	// wellDefinition();
	// CSTNCheckStatus status = new CSTNCheckStatus();
	//
	// cstn.labelModificationR2(g, P, X, xp, status);
	//
	// CSTNEdge pxOK = new CSTNEdge("XY", labeledIntValueMapClass);
	// //Reaction time = 0
	// pxOK.mergeLabeledValue(Label.parse("AB"), 0);
	// pxOK.mergeLabeledValue(Label.parse("C¬p"), -1);
	// pxOK.mergeLabeledValue(Label.parse("C"), 0);
	// //Reaction time = 1
	// pxOK = new CSTNEdge("XY", labeledIntValueMapClass);
	// pxOK.mergeLabeledValue(Label.parse("AB"), 1);
	// pxOK.mergeLabeledValue(Label.parse("AB¬p"), 0);
	// pxOK.mergeLabeledValue(Label.parse("C"), 1);
	// pxOK.mergeLabeledValue(Label.parse("C¬p"), -1);
	//
	// assertEquals("R2: XP? labeled values.", pxOK.getLabeledValueMap(), xp.getLabeledValueMap());
	// }

	// /**
	// *
	// CSTNCheckStatus)}.
	// */
	// @SuppressWarnings({ "deprecation", "javadoc" })
	// @Test
	// public final void testLabelModificationR1() {
	// // System.out.printf("R1 CASE\n");
	// TNGraph g = new TNGraph(labeledIntValueMapClass);
	// LabeledNode Z = g.getNodeFactory().get("Z");
	// LabeledNode P = g.getNodeFactory().get("P", 'p');
	// LabeledNode X = g.getNodeFactory().get("X");
	// LabeledNode Y = g.getNodeFactory().get("Y");
	// CSTNEdge px = new CSTNEdge("PX", labeledIntValueMapClass);
	// LabeledNode A = g.getNodeFactory().get("A?", 'a');
	// LabeledNode B = g.getNodeFactory().get("B?", 'b');
	// LabeledNode C = g.getNodeFactory().get("C?", 'c');
	// LabeledNode G = g.getNodeFactory().get("G?", 'g');
	// g.addVertex(Z);
	// g.addVertex(P);
	// g.addVertex(A);
	// g.addVertex(B);
	// g.addVertex(G);
	// g.addVertex(C);
	// g.addVertex(X);
	// g.addVertex(Y);
	//
	// px.mergeLabeledValue(Label.parse("¬b"), 0);
	// px.mergeLabeledValue(Label.parse("ab"), -10);
	//
	// CSTNEdge xy = new CSTNEdge("XY", labeledIntValueMapClass);
	// xy.mergeLabeledValue(Label.parse("bgp"), 10);
	// xy.mergeLabeledValue(Label.parse("cp"), -1);
	//
	// g.addEdge(px, P, X);
	// g.addEdge(xy, X, Y);
	//
	// wellDefinition();
	//
	// CSTN cstn1 = new CSTN(1, true, labeledIntValueMapClass);
	// cstn1.labelModificationR1(g, X, Y, Z, xy, new CSTNCheckStatus());
	//
	// CSTNEdge xyOK = new CSTNEdge("XY", labeledIntValueMapClass);
	// xyOK.putLabeledValue(Label.parse("abc"), -1);
	// xyOK.putLabeledValue(Label.parse("¬bc"), -1);
	// xyOK.putLabeledValue(Label.parse("bgp"), 10);
	// xyOK.putLabeledValue(Label.parse("cp"), -1);
	// // xyOK.putLabeledValue(Label.parse("abg"), 10);// se non è instantaneous!
	//
	// assertEquals("R1: xy labeled values.", xyOK.labeledValueSet(), xy.labeledValueSet());
	//
	// cstn1 = new CSTN(1, true, labeledIntValueMapClass);
	//
	// xyOK.removeLabel(Label.parse("abg"));
	// xy.clear();
	// xy.mergeLabeledValue(Label.parse("bgp"), 10);
	// xy.mergeLabeledValue(Label.parse("cp"), -1);
	// cstn1.labelModificationR1(g, X, Y, xy, new CSTNCheckStatus());// test istantaneous
	//
	// assertEquals("R1: xy labeled values.", xyOK.labeledValueSet(), xy.labeledValueSet());
	// }

	/**
	 *
	 */
	@Override

	@Test
	public final void testLabelModificationR0Z() {

		final CSTNEdge px = g.getEdgeFactory().get("PX");
		px.mergeLabeledValue(Label.parse("ABp"), -10);
		px.mergeLabeledValue(Label.parse("AB¬p"), 0);
		px.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);
		g.addEdge(px, P, X);
		g.setZ(X);
		g.addVertex(LabeledNodeSupplier.get("A", 'A'));
		g.addVertex(LabeledNodeSupplier.get("B", 'B'));

		try {
			cstn.initAndCheck();
		} catch (WellDefinitionException e) {
			fail(Arrays.toString(e.getStackTrace()));
		}
		cstn.labelModificationR0qR0(P, X, px);

		final CSTNEdge pxOK = g.getEdgeFactory().get("XY");
		pxOK.mergeLabeledValue(Label.parse("AB"), -10);
		pxOK.mergeLabeledValue(Label.emptyLabel, 0);

		assertEquals("R0: P?Z labeled values.", pxOK.getLabeledValueMap(), px.getLabeledValueMap());
	}

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
		// if R3 is applied!
		yxOK.mergeLabeledValue(Label.parse("abc"), -10);
		yxOK.mergeLabeledValue(Label.parse("abg"), -4);
		yxOK.mergeLabeledValue(Label.parse("¬bc"), 0);// std semantics R3 rule with reaction time
		yxOK.mergeLabeledValue(Label.parse("bgp"), -4);
		yxOK.mergeLabeledValue(Label.parse("c¬p"), 11);
		yxOK.mergeLabeledValue(Label.parse("cp"), -10);
		// if only qR3* is applied
		// yxOK.mergeLabeledValue(Label.parse("bgp"), -4);
		// yxOK.mergeLabeledValue(Label.parse("cp"), -10);
		// yxOK.mergeLabeledValue(Label.parse("c¬p"), 11);

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

		g.setZ(Z);
		cstn.labelModificationR3qR3(X, Z, xz);

		assertEquals("R3: yx labeled values.", AbstractLabeledIntMap.parse("{(abc¬p, -11) (ab¿c, -11) (ab¿p, -15) (ab, -10) }"), xz.getLabeledValueMap());
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

		final CSTNEdge xyOK = g.getEdgeFactory().get("XY");
		// xyOK.mergeLabeledValue(Label.parse("¬A¬B"), 17);
		// xyOK.mergeLabeledValue(Label.parse("¬b"), 8);if positive value are not admitted.
		xyOK.mergeLabeledValue(Label.parse("¬ab"), -2);
		xyOK.mergeLabeledValue(Label.parse("b"), -1);
		xyOK.mergeLabeledValue(Label.parse("¿b"), -11);

		assertEquals("No case: XY labeled values.", xyOK.getLabeledValueMap(), XY.getLabeledValueMap());

		XP.clear();
		XP.mergeLabeledValue(Label.parse("b"), -1);
		XP.mergeLabeledValue(Label.parse("¬b"), 1);
		XY.clear();
		cstn.labelPropagation(X, P, Y, XP, PY, XY);// Y is Z!!!

		// EqLP+ rule no positive value
		xyOK.clear();
		// xyOK.mergeLabeledValue(Label.parse("¬b"), 10);if positive value are not admitted.
		xyOK.mergeLabeledValue(Label.parse("b"), -11);
		assertEquals("No case: XY labeled values.", xyOK.getLabeledValueMap(), XY.getLabeledValueMap());

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

		xyOK.mergeLabeledValue(Label.parse("¿b"), -20);
		xyOK.mergeLabeledValue(Label.parse("¬b"), -1);

		assertEquals("No case: XY labeled values.", xyOK.getLabeledValueMap(), XY.getLabeledValueMap());
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
		// wellDefinition();

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

		g.addEdge(XY, X, Y);
		g.addEdge(YX, Y, X);
		// System.out.println(g);

		cstn.setPropagationOnlyToZ(false);

		try {
			cstn.initAndCheck();
		} catch (WellDefinitionException e) {
			fail(Arrays.toString(e.getStackTrace()));
		}

		final CSTNEdge XX = g.getEdgeFactory().get("XX");
		g.addEdge(XX, X, X);
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
			fail(Arrays.toString(e1.getStackTrace()));
			return;
		}
		assertFalse("Check the NOT DC qLoop29morning2qL.cstn. If this message appears, then checking wronlgy says CSTN is consistent!", status.consistency);
	}

	/**
	 * Test method for
	 */
	@Override

	@Test
	public final void testLabeledPropagationBackwardOfInfty() {

		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		g.addVertex(P);
		g.addVertex(X);
		g.addVertex(Y);

		final CSTNEdge YZ = g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.emptyLabel, 0);
		final CSTNEdge XZ = g.getEdgeFactory().get("XZ");
		XZ.mergeLabeledValue(Label.emptyLabel, 0);

		final CSTNEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.parse("p"), -2);

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("¬p"), -2);

		final CSTNEdge XX = g.getEdgeFactory().get("XX");

		g.addEdge(XY, X, Y);
		g.addEdge(YX, Y, X);
		g.addEdge(YZ, Y, Z);
		g.addEdge(XZ, X, Z);
		g.addEdge(XX, X, X);

		cstn.setPropagationOnlyToZ(false);
		cstn.labelPropagation(X, Y, Z, XY, YZ, XZ);
		assertEquals("XZ", "{(0, ⊡) (-2, p) }", XZ.getLabeledValueMap().toString());

		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		assertEquals("XX", "{(-∞, ¿p) }", XX.getLabeledValueMap().toString());// 2017-10-10: qLabels are not more generated.

		cstn.labelPropagation(Y, X, X, YX, XX, YX);
		assertEquals("", "{(-2, ¬p) (-∞, ¿p) }", YX.getLabeledValueMap().toString());
	}

	/**
	 * Test method for
	 */
	@Override

	@Test
	public final void testLabeledPropagationForwardOfInfty() {

		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		g.addVertex(P);
		g.addVertex(X);
		g.addVertex(Y);

		final CSTNEdge YZ = g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.emptyLabel, 0);
		final CSTNEdge XZ = g.getEdgeFactory().get("XZ");
		XZ.mergeLabeledValue(Label.emptyLabel, 0);

		final CSTNEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.parse("p"), -2);

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("¬p"), -2);

		final CSTNEdge XX = g.getEdgeFactory().get("XX");
		final CSTNEdge YY = g.getEdgeFactory().get("YY");

		g.addEdge(XY, X, Y);
		g.addEdge(YX, Y, X);
		g.addEdge(YZ, Y, Z);
		g.addEdge(XZ, X, Z);
		g.addEdge(XX, X, X);
		g.addEdge(YY, Y, Y);

		cstn.labelPropagation(X, Y, Z, XY, YZ, XZ);
		assertEquals("XZ", "{(0, ⊡) (-2, p) }", XZ.getLabeledValueMap().toString());

		cstn.setPropagationOnlyToZ(false);
		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		assertEquals("XX", "{(-∞, ¿p) }", XX.getLabeledValueMap().toString());

		cstn.labelPropagation(X, X, Y, XX, XY, XY);
		// 2018-11-28: infinity forward propagation is useless
		// assertEquals("XY", "{(-2, p) }", XY.getLabeledValueMap().toString());

		cstn.labelPropagation(Y, X, Y, YX, XY, YY);
		assertEquals("", "{(-∞, ¿p) }", YY.getLabeledValueMap().toString());

	}

	/**
	 * Test method for
	 */
	@Override

	@Test
	public final void testLabeledPropagationForwardOfInfty1() {

		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		g.addVertex(P);
		g.addVertex(X);
		g.addVertex(Y);

		final CSTNEdge YZ = g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.emptyLabel, 0);
		final CSTNEdge XZ = g.getEdgeFactory().get("XZ");
		XZ.mergeLabeledValue(Label.emptyLabel, 0);

		final CSTNEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.parse("¿p"), -2);

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("¬p"), -2);

		final CSTNEdge XX = g.getEdgeFactory().get("XX");
		final CSTNEdge YY = g.getEdgeFactory().get("YY");

		g.addEdge(XY, X, Y);
		g.addEdge(YX, Y, X);
		g.addEdge(YZ, Y, Z);
		g.addEdge(XZ, X, Z);
		g.addEdge(XX, X, X);
		g.addEdge(YY, Y, Y);

		cstn.setPropagationOnlyToZ(false);
		cstn.labelPropagation(X, Y, Z, XY, YZ, XZ);
		// assertEquals("XZ", "{(0, ⊡) }", eNew.getLabeledValueMap().toString());//if only negative value are q-propagate
		assertEquals("XZ", "{(0, ⊡) (-2, ¿p) }", XZ.getLabeledValueMap().toString());// if negative sum value are q-propagate

		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		// assertTrue(eNew == null);//if only negative value are q-propagate

		// g.addEdge(XX, X, X);
		cstn.labelPropagation(X, X, Y, XX, XY, XY);
		// assertEquals("XY", "{(-2, ¿p) }", eNew.getLabeledValueMap().toString());//if only negative value are q-propagate
		// 2018-11-28: infinity forward propagation is useless
		// assertEquals("XY", "{(-∞, ¿p) }", XY.getLabeledValueMap().toString());// if negative sum value are q-propagate
		cstn.labelPropagation(Y, X, Y, YX, XY, YY);
		assertEquals("", "{(-∞, ¿p) }", YY.getLabeledValueMap().toString());// 2017-10-10: qLabels are not more generated.

		cstn.labelPropagation(Y, Y, X, YY, YX, YX);
		// 2018-11-28: infinity forward propagation is useless
		assertEquals("", "{(-2, ¬p) (-∞, ¿p) }", YX.getLabeledValueMap().toString());// 2017-10-10: qLabels are not more generated.
	}

	/**
	 * Test method to check if a tNGraph requiring only R0-R3 application is checked well. .
	 *
	 * @throws WellDefinitionException  nope
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
}
