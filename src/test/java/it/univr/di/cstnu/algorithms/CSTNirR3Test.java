/**
 *
 */
package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.algorithms.AbstractCSTN.CSTNCheckStatus;
import it.univr.di.cstnu.graph.CSTNEdge;
import it.univr.di.cstnu.graph.LabeledNode;
import it.univr.di.cstnu.graph.LabeledNodeSupplier;
import it.univr.di.labeledvalue.AbstractLabeledIntMap;
import it.univr.di.labeledvalue.Constants;
import it.univr.di.labeledvalue.Label;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class CSTNirR3Test extends CSTNTest {

	/**
	 *
	 */
	public CSTNirR3Test() {
		cstn = new CSTNIR3R(g);
	}

	/**
	 * @throws java.lang.Exception  nope
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void testLabelModificationR3() {
		// no sense here!
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

		assertEquals("¿bg",
				cstn.makeBetaGammaDagger4qR3(Y, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("p¬b¬ag")).toString());// 'a'
		assertEquals("bg",
				cstn.makeBetaGammaDagger4qR3(Y, P, P.getPropositionObserved(), Label.parse("b"), Label.parse("bg¬a")).toString());
		assertEquals("¿bg",
				cstn.makeBetaGammaDagger4qR3(Y, P, P.getPropositionObserved(), Label.parse("¬b"), Label.parse("b¬ag")).toString());
	}

	/**
	 */
	@Override

	@Test
	public final void testLabelModificationR0() {

		final CSTNEdge pz = g.getEdgeFactory().get("PZ");
		pz.mergeLabeledValue(Label.parse("ABp"), -10);
		pz.mergeLabeledValue(Label.parse("AB¬p"), 0);
		pz.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);
		pz.mergeLabeledValue(Label.parse("C¬p"), 1);
		g.addEdge(pz, P, Z);
		g.setZ(X);

		cstn.labelModificationR0qR0(P, X, pz);

		final CSTNEdge pzOK = g.getEdgeFactory().get("XY");
		// if R0 is applied!
		pzOK.mergeLabeledValue(Label.parse("AB"), -10);
		pzOK.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);// ir semantics
		pzOK.mergeLabeledValue(Label.parse("C¬p"), 1);
		// if only qR0 is applied!
		// pxOK.mergeLabeledValue(Label.parse("ABp"), -10);
		// pxOK.mergeLabeledValue(Label.parse("AB¬p"), 0);
		// pxOK.mergeLabeledValue(Label.parse("¬A¬B¬p"), 0);
		// pxOK.mergeLabeledValue(Label.parse("C¬p"), 1);

		assertEquals("R0: p?X labeled values.", pzOK.getLabeledValueMap(), pz.getLabeledValueMap());
	}

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

	/**
	 * @throws WellDefinitionException  nope
	 */
	@Test
	public final void testLabelModificationqR3() throws WellDefinitionException {

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
		g.addVertex(Y);

		final CSTNEdge pz = g.getEdgeFactory().get("PZ");
		pz.mergeLabeledValue(Label.parse("¬b"), -1);
		pz.mergeLabeledValue(Label.parse("¬cab"), -10);

		final CSTNEdge yz = g.getEdgeFactory().get("YZ");
		yz.mergeLabeledValue(Label.parse("bgp"), -4);
		yz.mergeLabeledValue(Label.parse("cp"), -10);
		yz.mergeLabeledValue(Label.parse("c¬p"), 11);

		g.addEdge(pz, P, Z);
		g.addEdge(yz, Y, Z);

		cstn.initAndCheck();
		cstn.labelModificationR3qR3(Y, Z, yz);

		final CSTNEdge yzOK = g.getEdgeFactory().get("YZ");
		yzOK.mergeLabeledValue(Label.emptyLabel, 0);// ok
		yzOK.mergeLabeledValue(Label.parse("bgp"), -4);// ok
		yzOK.mergeLabeledValue(Label.parse("cp"), -10);// ok
		yzOK.mergeLabeledValue(Label.parse("c¬p"), 11);// ok
		yzOK.mergeLabeledValue(Label.parse("¿bg"), -1);
		yzOK.mergeLabeledValue(Label.parse("¬cabg"), -4);
		yzOK.mergeLabeledValue(Label.parse("c¬b"), -1);
		yzOK.mergeLabeledValue(Label.parse("a¿cb"), -10);
		yzOK.mergeLabeledValue(Label.parse("c¬b"), -1);
		yzOK.mergeLabeledValue(Label.parse("a¿cb"), -10);

		assertEquals("No case: XY labeled values.", yzOK.getLabeledValueMap(), yz.getLabeledValueMap());
	}

	/**
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

		wellDefinition();

		cstn.labelPropagation(X, P, Y, XP, PY, XY);

		// System.out.println(XP);
		// System.out.println(PY);

		final CSTNEdge xyOK = g.getEdgeFactory().get("XY");
		// xyOK.mergeLabeledValue(Label.parse("¬A¬B"), 17);
		// xyOK.mergeLabeledValue(Label.parse("¬b"), 8);if positive value are not admitted.
		xyOK.mergeLabeledValue(Label.parse("¬ab"), -2);
		xyOK.mergeLabeledValue(Label.parse("b"), -1);
		// xyOK.mergeLabeledValue(Label.parse("¿b"), -11);// Propagations to Z does not generate unknown values.
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

		// xyOK.mergeLabeledValue(Label.parse("¿b"), -20);// Propagations to Z does not generate unknown values.
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
	 * .
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
		XY.mergeLabeledValue(Label.parse("¬p"), 3);

		assertEquals("XY: ", AbstractLabeledIntMap.parse("{(3, ¬p) (-2, p) }"), XY.getLabeledValueMap());

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("p"), 3);
		YX.mergeLabeledValue(Label.parse("¬p"), -1);

		g.addEdge(XY, X, Y);
		g.addEdge(YX, Y, X);
		// System.out.println(g);

		try {
			cstn.initAndCheck();
		} catch (WellDefinitionException e) {
			fail(Arrays.toString(e.getStackTrace()));
		}

		final CSTNEdge XX = g.getEdgeFactory().get("XX");
		g.addEdge(XX, X, X);
		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		// Remember that not negative value on self loop are never stored!
		assertEquals("XX: ", "{}", XX.getLabeledValueMap().toString());

		XY.mergeLabeledValue(Label.parse("¬p"), 1);
		// reaction time is 1
		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		assertEquals("XX: ", "{}", XX.getLabeledValueMap().toString());

	}

	/**
	 * Test method for checking that all propagations are done
	 * .
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
	 * .
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
		XY.mergeLabeledValue(Label.parse("p"), 2);

		final CSTNEdge YX = g.getEdgeFactory().get("YX");
		YX.mergeLabeledValue(Label.parse("¬p"), -2);

		final CSTNEdge XX = g.getEdgeFactory().get("XX");

		g.addEdge(XY, X, Y);
		g.addEdge(YX, Y, X);
		g.addEdge(YZ, Y, Z);
		g.addEdge(XZ, X, Z);
		g.addEdge(XX, X, X);

		cstn.labelPropagation(X, Y, Z, XY, YZ, XZ);
		assertEquals("XZ", "{(0, ⊡) }", XZ.getLabeledValueMap().toString());

		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		assertEquals("XX", "{}", XX.getLabeledValueMap().toString());// 2017-10-10: qLabels are not more generated.

		cstn.labelPropagation(Y, X, X, YX, XX, YX);
		assertEquals("", "{(-2, ¬p) }", YX.getLabeledValueMap().toString());
	}

	/**
	 * Test method for
	 */
	@Override

	@Test
	public void testLabeledPropagationBackwardOfInfty1() {
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		g.addVertex(P);
		g.addVertex(X);
		g.addVertex(Y);

		// this.Z.potentialPut(Label.parse("p"), Constants.INT_NEG_INFINITE);
		Z.putLabeledPotential(Label.parse("¿p"), Constants.INT_NEG_INFINITE);
		assertEquals("Z", "{(-∞, ¿p) }", Z.getLabeledPotential().toString());

		final CSTNEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.parse("p"), -2);
		XY.mergeLabeledValue(Label.emptyLabel, -1);
		g.addEdge(XY, X, Y);
		assertEquals("XY", "{(-1, " + Label.emptyLabel + ") (-2, p) }", XY.getLabeledValueMap().toString());

		final CSTNEdge YZ = g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.parse("¬p"), -1);
		g.addEdge(YZ, Y, Z);

		final CSTNEdge XZ = g.getEdgeFactory().get("XZ");
		g.addEdge(XZ, X, Z);

		cstn.labelPropagation(X, Y, Z, XY, YZ, XZ);
		assertEquals("XZ", "{(-2, ¬p) }", XZ.getLabeledValueMap().toString());
		// Z contains a negative loop (forced). At first propagation to X (label p), the
		// method finds the negative loop, stores it, and returns.
		// this.cstn.potentialR3(this.X, this.Z, XZ, null);
		assertTrue("Status", cstn.checkStatus.consistency);
	}

	/**
	 * Test method for
	 * .
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
		XY.mergeLabeledValue(Label.parse("p"), 2);

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
		assertEquals("XZ", "{(0, ⊡) }", XZ.getLabeledValueMap().toString());

		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		assertEquals("XX", "{}", XX.getLabeledValueMap().toString());

		cstn.labelPropagation(X, X, Y, XX, XY, XY);
		assertEquals("XY", "{(2, p) }", XY.getLabeledValueMap().toString());

		cstn.labelPropagation(Y, X, Y, YX, XY, YY);
		assertEquals("", "{}", YY.getLabeledValueMap().toString());

	}

	/**
	 * Test method for
	 * .
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
		XY.mergeLabeledValue(Label.parse("p"), 2);

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
		assertEquals("XZ", "{(0, ⊡) }", XZ.getLabeledValueMap().toString());

		cstn.labelPropagation(X, Y, X, XY, YX, XX);
		// assertTrue(eNew == null);//if only negative value are q-propagate

		// g.addEdge(XX, X, X);
		cstn.labelPropagation(X, X, Y, XX, XY, XY);
		assertEquals("XY", "{(2, p) }", XY.getLabeledValueMap().toString());
		cstn.labelPropagation(Y, X, Y, YX, XY, YY);
		assertEquals("", "{}", YY.getLabeledValueMap().toString());// 2017-10-10: qLabels are not more generated.

		cstn.labelPropagation(Y, Y, X, YY, YX, YX);
		assertEquals("", "{(-2, ¬p) }", YX.getLabeledValueMap().toString());// 2017-10-10: qLabels are not more generated.
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
