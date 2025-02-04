package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.algorithms.CSTNU.CSTNUCheckStatus;
import it.univr.di.cstnu.graph.*;
import it.univr.di.cstnu.graph.Edge.ConstraintType;
import it.univr.di.labeledvalue.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class CSTNUTest {

	/**
	 * Default implementation class for CSTNEdge
	 */
	static final Class<? extends CSTNUEdge> EDGE_IMPL_CLASS = CSTNUEdgePluggable.class;

	/**
	 *
	 */
	CSTNU cstnu;

	/**
	 *
	 */
	LabeledNode Z;

	/**
	 *
	 */
	ALabelAlphabet alpha;

	/**
	 *
	 */
	TNGraph<CSTNUEdge> g;

	/**
	 * @throws java.lang.Exception nope
	 */
	@Before
	public void setUp() throws Exception {
		alpha = new ALabelAlphabet();
		g = new TNGraph<>("no name",EDGE_IMPL_CLASS, alpha);
		Z = LabeledNodeSupplier.get("Z");
		g.setZ(Z);
		cstnu = new CSTNU(g);
	}

	/**
	 * @throws WellDefinitionException nope
	 */
	@Test
	public final void testCaseLabelRemovalRule() throws WellDefinitionException {
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode B = LabeledNodeSupplier.get("B");
		final ALabel aLabel4B = new ALabel(B.getName(), alpha);
		B.setALabel(aLabel4B);
		final LabeledNode C = LabeledNodeSupplier.get("C");

		final LabeledNode a = LabeledNodeSupplier.get("A?");
		a.setObservable('a');
		g.addVertex(a);
		final LabeledNode b = LabeledNodeSupplier.get("B?");
		b.setObservable('b');
		g.addVertex(b);

		final CSTNUEdge AB = g.getEdgeFactory().get("AB");
		AB.setLowerCaseValue(Label.emptyLabel, aLabel4B, 13);
		AB.setConstraintType(ConstraintType.contingent);

		final CSTNUEdge BA = g.getEdgeFactory().get("BA");
		BA.setConstraintType(ConstraintType.contingent);
		BA.mergeUpperCaseValue(Label.emptyLabel, aLabel4B, -20);

		final CSTNUEdge cz = g.getEdgeFactory().get("CZ");
		cz.mergeUpperCaseValue(Label.parse("ab"), aLabel4B, -4);
		cz.mergeUpperCaseValue(Label.parse("a"), aLabel4B, -3);
		cz.mergeUpperCaseValue(Label.parse("¬b"), aLabel4B, -3);
		cz.mergeUpperCaseValue(Label.parse("¬ab"), aLabel4B, -15);

		final CSTNUEdge az = g.getEdgeFactory().get("AZ");
		az.mergeLabeledValue(Label.parse("ab"), -1);

		g.addEdge(AB, A, B);
		g.addEdge(BA, B, A);
		g.addEdge(cz, C, Z);
		g.addEdge(az, A, Z);

		cstnu.initAndCheck();

		cstnu.zLabeledLetterRemovalRule(C, cz);

		final CSTNUEdge CZOk = g.getEdgeFactory().get("CZ");
		// remember that CZ contains also (0,emptyLabel)
		CZOk.mergeLabeledValue(Label.emptyLabel, 0);
		CZOk.mergeLabeledValue(Label.parse("a"), -3);
		CZOk.mergeLabeledValue(Label.parse("¬b"), -3);
		CZOk.mergeLabeledValue(Label.parse("ab"), -4);
		CZOk.mergeLabeledValue(Label.parse("¬ab"), -13);
		CZOk.mergeLabeledValue(Label.parse("¿ab"), -14);

		assertEquals("Upper Case values:", CZOk.getLabeledValueMap(), cz.getLabeledValueMap());
	}

	/**
	 * Test method for
	 *
	 * <pre>
	 * A &lt;---(-3,D,b¬c)--- C &lt;-----(3,c,ab)---- D
	 * </pre>
	 */
	@Test
	public final void testCrossCaseRule() {
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode C = LabeledNodeSupplier.get("C");
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final CSTNUEdge dc = g.getEdgeFactory().get("DC");
		final CSTNUEdge ca = g.getEdgeFactory().get("CA");
		final CSTNUEdge da = g.getEdgeFactory().get("DA");
		g.addEdge(dc, D, C);
		g.addEdge(ca, C, A);
		g.addEdge(da, D, A);
		dc.setLowerCaseValue(Label.parse("ab"), new ALabel(C.getName(), alpha), 3);

		ca.mergeUpperCaseValue(Label.parse("b¬c"), new ALabel(D.getName(), alpha), -3);
		ca.mergeUpperCaseValue(Label.parse("b¬f"), new ALabel(D.getName(), alpha), 3);
		ca.mergeUpperCaseValue(Label.parse("¬b"), new ALabel(D.getName(), alpha), -4);
		ca.mergeUpperCaseValue(Label.parse("ab"), new ALabel(C.getName(), alpha), -4);

		cstnu.labeledCrossLowerCaseRule(D, C, A, dc, ca, da);

		final CSTNUEdge daOk = g.getEdgeFactory().get("DA");
		daOk.mergeUpperCaseValue(Label.parse("ab¬c"), new ALabel("D", alpha), 0);

		assertEquals("Cross Case values:", daOk.getUpperCaseValueMap(), da.getUpperCaseValueMap());
	}

	/**
	 * @throws WellDefinitionException nope
	 */
	@Test
	public final void testLabelModificationQR0() throws WellDefinitionException {
		final LabeledNode P = LabeledNodeSupplier.get("P?", 'p');
		final LabeledNode Q = LabeledNodeSupplier.get("Q?", 'q');
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		final LabeledNode C = LabeledNodeSupplier.get("C?", 'c');
		Q.setLabel(Label.parse("p"));
		// C.setLabel(Label.parse("p"));
		final CSTNUEdge pz = g.getEdgeFactory().get("PZ");
		pz.mergeLabeledValue(Label.parse("abp¿q"), -10);
		pz.mergeLabeledValue(Label.parse("ab¬p"), -1);
		pz.mergeLabeledValue(Label.parse("¬a¬c¬p"), -2);
		pz.mergeLabeledValue(Label.parse("¬acp"), -2);
		pz.mergeLabeledValue(Label.parse("¿c¿p"), 1);
		pz.mergeUpperCaseValue(Label.parse("¬a¬p"), new ALabel("C", alpha), -2);
		g.addEdge(pz, P, Z);
		g.addVertex(Q);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(C);
		g.setZ(Z);
		cstnu.initAndCheck();
		cstnu.labelModificationqR0(P, pz);

		final CSTNUEdge pxOK = g.getEdgeFactory().get("XY");
		pxOK.mergeLabeledValue(Label.emptyLabel, 0);
		pxOK.mergeLabeledValue(Label.parse("ab"), -1);
		// pxOK.mergeLabeledValue(Label.parse("ab¿q"), -10);// if it were streamlined
		pxOK.mergeLabeledValue(Label.parse("ab"), -10);// if it is not streamlined
		pxOK.mergeLabeledValue(Label.parse("¬a"), -2);
		// pxOK.mergeLabeledValue(Label.parse("¿c¿p"), 1);// NO!
		pxOK.mergeUpperCaseValue(Label.parse("¬a¬p"), new ALabel("C", alpha), -2);

		assertEquals("R0: P?Z labeled values.", pxOK.getLabeledValueMap(), pz.getLabeledValueMap());
		// 2018-12-18 Trying to make a-label simplification faster... loosing some optimization
		// asserEquals when full optimization is activated.
		assertNotEquals("R0: PZ upper case labedled values.", pxOK.getUpperCaseValueMap(), pz.getUpperCaseValueMap());
	}

	/**
	 * <pre>
	 * P ----[0, ,¬b][-10, ,ab][-11,C,ab]--&gt; Z &lt;--------- Y
	 * </pre>
	 */
	@Test
	public final void testLabelModificationQR3() {
		// System.out.printf("R1-R3 CASE\n");
		final LabeledNode P = LabeledNodeSupplier.get("P", 'p');
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		final LabeledNode C = LabeledNodeSupplier.get("C?", 'c');
		final LabeledNode G = LabeledNodeSupplier.get("G?", 'g');
		G.setLabel(Label.parse("p"));
		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(G);
		g.addVertex(C);
		g.addVertex(Y);
		g.setZ(Z);
		final CSTNUEdge gp = g.getEdgeFactory().get("GP");
		gp.mergeLabeledValue(Label.parse("p"), -4);
		g.addEdge(gp, G, P);

		final CSTNUEdge pz = g.getEdgeFactory().get("PZ");
		pz.mergeLabeledValue(Label.parse("¬b"), 0);
		pz.mergeLabeledValue(Label.parse("ab"), -10);
		pz.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", alpha), -11);

		final CSTNUEdge yz = g.getEdgeFactory().get("YZ");
		yz.mergeLabeledValue(Label.parse("bg¿p"), -4);
		yz.mergeLabeledValue(Label.parse("cp"), -10);
		yz.mergeLabeledValue(Label.parse("¿cp"), -11);
		yz.mergeUpperCaseValue(Label.parse("bgp"), new ALabel("C", alpha), -7);

		g.addEdge(pz, P, Z);
		g.addEdge(yz, Y, Z);

		wellDefinition(g);

		cstnu.labelModificationqR3(Y, yz);

		// <YX, normal, Y, X, L:{(¬ABGp, -4) (ABG, -4) }, LL:{}, UL:{(¬ABG¬p,C:-4) (¬ABGp,C:-7) (ABG,C:-7) }>
		final CSTNUEdge yxOK = g.getEdgeFactory().get("YX");
		// yxOK.mergeLabeledValue(Label.parse("¬abgp"), -4);
		// yxOK.mergeUpperCaseValue(Label.parse("abg"), new ALabel("C", this.alpha), -7);// if streamlined
		yxOK.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", alpha), -7);// if not streamlined
		yxOK.mergeUpperCaseValue(Label.parse("abc"), new ALabel("C", alpha), -10);// it could not be present because there is the labeled value
		// (-10,abc)... it depends in which
		// order it is inserted.
		yxOK.mergeUpperCaseValue(Label.parse("bgp"), new ALabel("C", alpha), -7);//
		yxOK.mergeUpperCaseValue(Label.parse("ab¿c"), new ALabel("C", alpha), -11);//

		yxOK.mergeLabeledValue(Label.parse("ab"), -4);// if not streamlined
		// yxOK.mergeLabeledValue(Label.parse("abg"), -4);// if streamlined
		yxOK.mergeLabeledValue(Label.parse("cp"), -10);
		yxOK.mergeLabeledValue(Label.parse("¿cp"), -11);
		yxOK.mergeLabeledValue(Label.parse("abc"), -10);
		yxOK.mergeLabeledValue(Label.parse("bg¿p"), -4);

		assertEquals("R3: yx labeled values.", yxOK.getLabeledValueMap(), yz.getLabeledValueMap());
		assertEquals("R3: yx upper case labedled values.", yxOK.getUpperCaseValueMap(), yz.getUpperCaseValueMap());
	}

	/**
	 * @throws WellDefinitionException nope
	 */
	@Test
	public final void testLabelModificationR0() throws WellDefinitionException {
		final LabeledNode P = LabeledNodeSupplier.get("P", 'p');
		// LabeledNode X = this.g.getNodeFactory().get("X");
		final LabeledNode Q = LabeledNodeSupplier.get("Q?", 'q');
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		final LabeledNode C = LabeledNodeSupplier.get("C?", 'c');
		Q.setLabel(Label.parse("p"));
		C.setLabel(Label.parse("¬p"));
		final CSTNUEdge pz = g.getEdgeFactory().get("PX");
		pz.mergeLabeledValue(Label.parse("abpq"), -10);// ok but p has to be removed
		pz.mergeLabeledValue(Label.parse("ab¬p"), 0);// it is removed during init by (0, ⊡)
		pz.mergeLabeledValue(Label.parse("c¬p"), 1);// verrà cancellato in fase d'init!
		pz.mergeLabeledValue(Label.parse("¬c¬pa"), -1);// ok but ¬c¬p has to be removed
		pz.mergeUpperCaseValue(Label.parse("ab¬p"), new ALabel("C", alpha), -11);// ok
		g.addEdge(pz, P, Z);
		g.addVertex(Q);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(C);

		cstnu.initAndCheck();// from nov 2017, it doesn't clear odd values
		cstnu.labelModificationqR0(P, pz);

		final CSTNUEdge pxOK = g.getEdgeFactory().get("XY");
		// if R0 is applied!
		// pxOK.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", this.alpha), -11);
		// pxOK.mergeLabeledValue(Label.parse("ab"), -10);
		// pxOK.mergeLabeledValue(Label.parse("a"), -1);
		// pxOK.mergeLabeledValue(Label.parse("c¬p"), 1);
		// if only qR0 is applied!
		pxOK.mergeLabeledValue(Label.emptyLabel, 0);// ok
		// pxOK.mergeLabeledValue(Label.parse("abq"), -10);// ok if it is streamlined
		pxOK.mergeLabeledValue(Label.parse("ab"), -10);// ok if it is NOT streamlined
		pxOK.mergeLabeledValue(Label.parse("ab¬p"), 0); // quindi non è memorizzato
		pxOK.mergeLabeledValue(Label.parse("c¬p"), 1);// viene cancellato dallo 0
		// pxOK.mergeLabeledValue(Label.parse("a¬c"), -1);// ok if it is streamlined
		pxOK.mergeLabeledValue(Label.parse("a"), -1);// ok if it is NOT streamlined
		pxOK.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", alpha), -11);

		assertEquals("R0: p?X labeled values.", pxOK.getLabeledValueMap(), pz.getLabeledValueMap());
		assertEquals("R0: px upper case labedled values.", pxOK.getUpperCaseValueMap(), pz.getUpperCaseValueMap());
	}

	/**
	 * <pre>
	 * P ----[0, ,¬b][-10, ,ab][-11,C,ab]--&gt; X &lt;--------- Y
	 * </pre>
	 */
	@Test
	public final void testLabelModificationR3() {
		// System.out.printf("R1-R3 CASE\n");
		final LabeledNode P = LabeledNodeSupplier.get("P", 'p');
		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		final LabeledNode C = LabeledNodeSupplier.get("C?", 'c');
		final LabeledNode G = LabeledNodeSupplier.get("G?", 'g');
		G.setLabel(Label.parse("p"));
		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(G);
		g.addVertex(C);
		g.addVertex(X);
		g.addVertex(Y);
		g.setZ(Z);
		final CSTNUEdge gp = g.getEdgeFactory().get("GP");
		gp.mergeLabeledValue(Label.parse("p"), -4);
		g.addEdge(gp, G, P);

		final CSTNUEdge px = g.getEdgeFactory().get("PX");
		px.mergeLabeledValue(Label.parse("¬b"), 0);
		px.mergeLabeledValue(Label.parse("ab"), -10);
		px.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", alpha), -11);

		final CSTNUEdge yx = g.getEdgeFactory().get("YX");
		yx.mergeLabeledValue(Label.parse("bgp"), -4);
		yx.mergeLabeledValue(Label.parse("cp"), -10);
		yx.mergeLabeledValue(Label.parse("c¬p"), 11);
		yx.mergeUpperCaseValue(Label.parse("bgp"), new ALabel("C", alpha), -7);

		g.addEdge(px, P, X);
		g.addEdge(yx, Y, X);

		wellDefinition(g);

		cstnu.labelModificationqR3(Y, yx);

		// <YX, normal, Y, X, L:{(¬ABGp, -4) (ABG, -4) }, LL:{}, UL:{(¬ABG¬p,C:-4) (¬ABGp,C:-7) (ABG,C:-7) }>
		final CSTNUEdge yxOK = g.getEdgeFactory().get("YX");
		// yxOK.mergeLabeledValue(Label.parse("¬abgp"), -4);
		// yxOK.mergeLabeledValue(Label.parse("ab"), -4); from 20171017 R3 is only qR3*
		// yxOK.mergeLabeledValue(Label.parse("abc"), -10); from 20171017 R3 is only qR3*
		// yxOK.mergeLabeledValue(Label.parse("¬bc"), 0);// R5 rule. from 20171017 R3 is only qR3*
		yxOK.mergeLabeledValue(Label.parse("bgp"), -4);// original
		yxOK.mergeLabeledValue(Label.parse("c¬p"), 11);// original
		yxOK.mergeLabeledValue(Label.parse("cp"), -10);// original

		// yxOK.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", this.alpha), -7);from 20171017 R3 is only qR3*
		yxOK.mergeUpperCaseValue(Label.parse("bgp"), new ALabel("C", alpha), -7);// original

		assertEquals("R3: yx labeled values.", yxOK.getLabeledValueMap(), yx.getLabeledValueMap());
		assertEquals("R3: yx upper case labedled values.", yxOK.getUpperCaseValueMap(), yx.getUpperCaseValueMap());
	}

	/**
	 * @param g1 graph to check
	 */
	private final void wellDefinition(TNGraph<CSTNUEdge> g1) {
		cstnu.setG(g1);
		try {
			cstnu.checkWellDefinitionProperties();
		} catch (WellDefinitionException e) {
			fail("TNGraph not well-formed: " + e.getMessage());
		}
	}

	/**
	 *
	 */
	@Test
	public final void testLabelModificationR3bis() {
		alpha.clear();

		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode N8 = LabeledNodeSupplier.get("n8");
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(N8);
		g.setZ(Z);

		final CSTNUEdge eN8Z = g.getEdgeFactory().get("n8_Z");
		LabeledIntMap map = AbstractLabeledIntMap.parse("{(-20, ab) (-∞, ¿ab) (-8, ¬b) (-17, b) }");
		eN8Z.setLabeledValueMap(map);
		LabeledALabelIntTreeMap map1 = LabeledALabelIntTreeMap
			.parse("{(D, -∞, ¿ab) (D, -30, ab) (D, -∞, a¿b) (D, -4, ¿b) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) " +
			       "(F, -19, ¬ab) (F, -∞, ¿ab) }", alpha, null);
		eN8Z.setUpperCaseValueMap(map1);
		g.addEdge(eN8Z, N8, Z);

		// {❮B?_Z; derived; {(0, ⊡) }; ❯, ❮A?_Z; derived; {(-2, ⊡) (-4, b) }; ❯}
		final CSTNUEdge eAZ = g.getEdgeFactory().get("A?_Z");
		map = AbstractLabeledIntMap.parse("{(-2, ⊡) (-4, b) }");
		eAZ.setLabeledValueMap(map);
		eAZ.setUpperCaseValueMap(map1);
		g.addEdge(eAZ, A, Z);

		// System.out.println(eN8Z);
		cstnu.labelModificationqR3(N8, eN8Z);

		final CSTNUEdge eN8ZOK = g.getEdgeFactory().get("n8_Z");
		map = AbstractLabeledIntMap.parse("{(-20, ab) (-∞, ¿ab) (-8, ¬b) (-17, b) }");
		eN8ZOK.setLabeledValueMap(map);
		map1 = LabeledALabelIntTreeMap
			.parse("{(D, -∞, ¿ab) (D, -30, ab) (D, -∞, a¿b) (D, -4, ¿b) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) " +
			       "(F, -19, ¬ab) (F, -∞, ¿ab) }", alpha, null);
		eN8ZOK.setUpperCaseValueMap(map1);

		assertEquals("R3: eN8Z labeled values.", eN8ZOK.getLabeledValueMap(), eN8Z.getLabeledValueMap());
		assertEquals("R3: eN8ZOK upper case labedled values.", eN8ZOK.getUpperCaseValueMap(), eN8Z.getUpperCaseValueMap());

		cstnu.labelModificationqR3(N8, eN8Z);

		assertEquals("R3: eN8Z labeled values.", eN8ZOK.getLabeledValueMap(), eN8Z.getLabeledValueMap());
		assertEquals("R3: eN8ZOK upper case labedled values.", eN8ZOK.getUpperCaseValueMap(), eN8Z.getUpperCaseValueMap());

	}

	/*
	 * Test method for {@link it.univr.di.attic.CSTNU_NodeSet#lowerCaseRule(TNGraph, TNGraph, CSTNUCheckStatus)}.
	 *
	 * <pre>
	 * A &lt;----[1,,b][0,,c][-11,,d]--- C &lt;---[3,c,ab]--- D
	 * </pre>
	 *
	 * @SuppressWarnings("javadoc")
	 *
	 * @Test
	 *       public final void testLowerCaseRule() {
	 *       // System.out.printf("LOWER CASE\n");
	 *       TNGraph g = new TNGraph(this.LABELED_VALUE_MAP_IMPL_CLASS);
	 *       LabeledIntEdgePlugga ble dc = this.g.getEdgeFactory().get("DC");
	 *       CSTNUEdge ca = this.g.getEdgeFactory().get("CA");
	 *       CSTNUEdge da = this.g.getEdgeFactory().get("DA");
	 *       dc.setLowerCaseValue(Label.parse("ab"), new ALabel("c", this.alpha), 3);
	 *       ca.mergeLabeledValue(Label.parse("b"), 1);
	 *       ca.mergeLabeledValue(Label.parse("c"), 0);
	 *       ca.mergeLabeledValue(Label.parse("d"), -11);
	 *       LabeledNode A = this.g.getNodeFactory().get("A");
	 *       LabeledNode C = this.g.getNodeFactory().get("C");
	 *       LabeledNode D = this.g.getNodeFactory().get("D");
	 *       LabeledNode OA = this.g.getNodeFactory().get("A?", 'a');
	 *       LabeledNode OB = this.g.getNodeFactory().get("B?", 'b');
	 *       LabeledNode OC = this.g.getNodeFactory().get("C?", 'c');
	 *       LabeledNode OD = this.g.getNodeFactory().get("D?", 'd');
	 *       g.addVertex(OA);
	 *       g.addVertex(OB);
	 *       g.addVertex(OC);
	 *       g.addVertex(OD);
	 *       g.addEdge(dc, D, C);
	 *       g.addEdge(ca, C, A);
	 *       g.addEdge(da, D, A);
	 *       g.setZ(this.Z);
	 *       wellDefinition(g);
	 *       this.cstnu.labeledLowerCaseRule(D, C, A, this.Z, dc, ca, da);
	 *       CSTNUEdge daOk = this.g.getEdgeFactory().get("DA");
	 *       daOk.mergeLabeledValue(Label.parse("abc"), 3);
	 *       daOk.mergeLabeledValue(Label.parse("abd"), -8);
	 *       assertEquals("Lower Case values:", daOk.getLabeledValueMap(), da.getLabeledValueMap());
	 *       // System.out.printf("G.hasSameEdge(G1): %s\n", g.hasAllEdgesOf(g1));
	 *       // System.out.printf("G: %s\n", g1);
	 *       }
	 */

	/*
	 * Test method for {@link it.univr.di.attic.CSTNU_NodeSet#lowerCaseRule(TNGraph, TNGraph, CSTNUCheckStatus)}.
	 *
	 * <pre>
	 * Z &lt;----[1,,b][0,,c][-11,,¬b]--- C &lt;---[3,c,ab]--- D
	 * </pre>
	 *
	 * @SuppressWarnings("javadoc")
	 *
	 * @Test
	 *       public final void testQLowerCaseRule() {
	 *       // System.out.printf("LOWER CASE\n");
	 *       TNGraph g = new TNGraph(this.LABELED_VALUE_MAP_IMPL_CLASS);
	 *       CSTNUEdge dc = this.g.getEdgeFactory().get("DC");
	 *       CSTNUEdge ca = this.g.getEdgeFactory().get("CA");
	 *       CSTNUEdge da = this.g.getEdgeFactory().get("DA");
	 *       dc.setLowerCaseValue(Label.parse("ab"), new ALabel("c", this.alpha), 3);
	 *       ca.mergeLabeledValue(Label.parse("b"), 1);
	 *       ca.mergeLabeledValue(Label.parse("c"), 0);
	 *       ca.mergeLabeledValue(Label.parse("¬b"), -11);
	 *       LabeledNode A = this.g.getNodeFactory().get("A");
	 *       LabeledNode C = this.g.getNodeFactory().get("C");
	 *       LabeledNode D = this.g.getNodeFactory().get("D");
	 *       LabeledNode OA = this.g.getNodeFactory().get("A?", 'a');
	 *       LabeledNode OB = this.g.getNodeFactory().get("B?", 'b');
	 *       LabeledNode OC = this.g.getNodeFactory().get("C?", 'c');
	 *       LabeledNode OD = this.g.getNodeFactory().get("D?", 'd');
	 *       g.addVertex(OA);
	 *       g.addVertex(OB);
	 *       g.addVertex(OC);
	 *       g.addVertex(OD);
	 *       g.addEdge(dc, D, C);
	 *       g.addEdge(ca, C, A);
	 *       g.addEdge(da, D, A);
	 *       g.setZ(A);
	 *       wellDefinition(g);
	 *       this.cstnu.labeledLowerCaseRule(D, C, A, A, dc, ca, da);
	 *       CSTNUEdge daOk = this.g.getEdgeFactory().get("DA");
	 *       daOk.mergeLabeledValue(Label.parse("abc"), 3);
	 *       daOk.mergeLabeledValue(Label.parse("a¿b"), -8);
	 *       assertEquals("Lower Case values:", daOk.getLabeledValueMap(), da.getLabeledValueMap());
	 *       // System.out.printf("G.hasSameEdge(G1): %s\n", g.hasAllEdgesOf(g1));
	 *       // System.out.printf("G: %s\n", g1);
	 *       }
	 */

	/**
	 * A &lt;---[3,B,ab]--- C &lt;----[-13,,b][11,,c]----D
	 */
	@Test
	public final void testUpperCaseRule() {
		// System.out.printf("UPPER CASE\n");
		final CSTNUEdge dc = g.getEdgeFactory().get("dc");
		final CSTNUEdge ca = g.getEdgeFactory().get("ca");
		final CSTNUEdge da = g.getEdgeFactory().get("da");
		ca.mergeUpperCaseValue(Label.parse("ab"), new ALabel("B", alpha), 3);
		dc.mergeLabeledValue(Label.parse("b"), -13);
		dc.mergeLabeledValue(Label.parse("c"), 11);
		final LabeledNode C = LabeledNodeSupplier.get("C");
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode D = LabeledNodeSupplier.get("D");
		g.addEdge(dc, D, C);
		g.addEdge(ca, C, A);
		g.addEdge(da, D, A);
		cstnu.labelPropagation(D, C, A, dc, ca, da);

		final CSTNUEdge daOk = g.getEdgeFactory().get("DA");
		daOk.mergeUpperCaseValue(Label.parse("abc"), new ALabel("B", alpha), 14);
		daOk.mergeUpperCaseValue(Label.parse("ab"), new ALabel("B", alpha), -10);

		assertEquals("Upper Case values:", daOk.getUpperCaseValueMap(), da.getUpperCaseValueMap());
	}

	/**
	 * Test method for
	 */
	@Test
	public final void test_flucRule() {
		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(X);
		g.addVertex(Y);
		g.setZ(Z);

		final ALabel aLabel = new ALabel(A.getName(), alpha);
		final ALabel bLabel = new ALabel(B.getName(), alpha);

		final CSTNUEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeUpperCaseValue(Label.emptyLabel, aLabel, 1);
		XY.mergeUpperCaseValue(Label.parse("¬a"), aLabel, -3);
		XY.mergeUpperCaseValue(Label.parse("b"), aLabel, -4);
		XY.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -2);
		// System.out.println(XY);

		final CSTNUEdge YZ = g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.parse("a"), 2);
		YZ.mergeLabeledValue(Label.parse("ab"), 1);
		YZ.mergeLabeledValue(Label.parse("a¬b"), -1);

		final CSTNUEdge XZ = g.getEdgeFactory().get("XZ");

		g.addEdge(XY, X, Y);
		g.addEdge(YZ, Y, Z);
		g.addEdge(XZ, X, Z);

		wellDefinition(g);
		// System.out.println(XW);

		cstnu.labelPropagation(X, Y, Z, XY, YZ, XZ);
		// System.out.println(XW);

		final CSTNUEdge xzOK = g.getEdgeFactory().get("XW");
		// FLUC and LCUC change upper case values!
		// xzOK.mergeUpperCaseValue(Label.parse("¿a"), aLabel, -1); From 20171010 unknown literal are not more propagated
		// xzOK.mergeUpperCaseValue(Label.parse("¿a¬b"), aLabel, -4);
		// xzOK.mergeUpperCaseValue(Label.parse("ab"), aLabel, -3);// X<>A?, so rule cannot be applied
		// xzOK.mergeUpperCaseValue(Label.parse("a¿b"), aLabel, -5);

		assertEquals("No case: XZ labeled values.", xzOK.getLabeledValueMap(), XZ.getLabeledValueMap());
		assertEquals("No case: XZ upper case labedled values.", xzOK.getUpperCaseValueMap(), XZ.getUpperCaseValueMap());
	}

	/**
	 * Test method for
	 */
	@Test
	public final void test_lcucRule() {
		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(X);
		g.addVertex(Y);
		g.setZ(Z);

		final ALabel aLabel = new ALabel(A.getName(), alpha);
		final ALabel bLabel = new ALabel(B.getName(), alpha);

		final CSTNUEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeUpperCaseValue(Label.emptyLabel, aLabel, 1);
		XY.mergeUpperCaseValue(Label.parse("¬a"), aLabel, -3);
		XY.mergeUpperCaseValue(Label.parse("b"), aLabel, -4);
		XY.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -2);
		// System.out.println(XY);

		final CSTNUEdge YZ = g.getEdgeFactory().get("YZ");
		YZ.mergeUpperCaseValue(Label.parse("a"), aLabel, 1);
		YZ.mergeUpperCaseValue(Label.parse("ab"), aLabel, 0);
		YZ.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -2);

		final CSTNUEdge XZ = g.getEdgeFactory().get("XZ");

		g.addEdge(XY, X, Y);
		g.addEdge(YZ, Y, Z);
		g.addEdge(XZ, X, Z);

		wellDefinition(g);
		// System.out.println(XW);

		cstnu.labelPropagation(X, Y, Z, XY, YZ, XZ);
		// System.out.println(XW);

		final CSTNUEdge xzOK = g.getEdgeFactory().get("XZ");
		// <{¿a=A?->-2, ab=A?->-4, ¿a¬b=A?∙B?->-5, a¿b=A?∙B?->-6}>
		// xwOK.mergeUpperCaseValue(Label.parse("¿a"), aLabel, -2);From 20171010 unknown literal are not more propagated
		// xwOK.mergeUpperCaseValue(Label.parse("ab"), aLabel, -4);
		// xwOK.mergeUpperCaseValue(Label.parse("¿a¬b"), aLabel.conjunction(bLabel), -5);
		// xwOK.mergeUpperCaseValue(Label.parse("a¿b"), aLabel.conjunction(bLabel), -6);

		assertEquals("No case: XZ labeled values.", xzOK.getLabeledValueMap(), XZ.getLabeledValueMap());
		assertEquals("No case: XZ upper case labedled values.", xzOK.getUpperCaseValueMap(), XZ.getUpperCaseValueMap());
	}

	/**
	 * @throws WellDefinitionException nope
	 */
	@Test
	public final void test_llcRule() throws WellDefinitionException {
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode C = LabeledNodeSupplier.get("C");
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final LabeledNode a = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode b = LabeledNodeSupplier.get("B?", 'b');

		g.addVertex(A);
		g.addVertex(a);
		g.addVertex(b);
		g.addVertex(C);
		g.addVertex(D);
		g.setZ(Z);

		final ALabel CaLabel = new ALabel(C.getName(), alpha);

		final CSTNUEdge CA = g.getEdgeFactory().get("CA");
		CA.mergeUpperCaseValue(Label.parse("a"), CaLabel, -6);
		CA.setConstraintType(ConstraintType.contingent);
		final CSTNUEdge AC = g.getEdgeFactory().get("AC");
		AC.setLowerCaseValue(Label.parse("a"), CaLabel, 3);
		AC.setConstraintType(ConstraintType.contingent);
		final CSTNUEdge CD = g.getEdgeFactory().get("CD");
		CD.mergeLabeledValue(Label.parse("b"), 0);

		final CSTNUEdge DA = g.getEdgeFactory().get("DA");
		DA.mergeLabeledValue(Label.parse("b"), -4);

		g.addEdge(AC, A, C);
		g.addEdge(CA, C, A);
		g.addEdge(CD, C, D);
		g.addEdge(DA, D, A);

		cstnu.setG(g);
		cstnu.initAndCheck();

		final CSTNUCheckStatus status = cstnu.dynamicControllabilityCheck();

		assertFalse(status.consistency);
	}

	/**
	 */
	@Test
	public final void test_lncRule() {
		final LabeledNode W = LabeledNodeSupplier.get("W", 'w');
		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		W.setLabel(Label.parse("a"));
		g.addVertex(W);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(X);
		g.addVertex(Y);
		g.setZ(Z);

		final CSTNUEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.emptyLabel, 2);
		XY.mergeLabeledValue(Label.parse("¬a"), -2);
		XY.mergeLabeledValue(Label.parse("b"), 1);
		XY.mergeLabeledValue(Label.parse("a¬b"), -1);
		// System.out.println(XY);

		final CSTNUEdge YW = g.getEdgeFactory().get("YW");
		YW.mergeLabeledValue(Label.parse("a"), 2);
		YW.mergeLabeledValue(Label.parse("ab"), 1);
		YW.mergeLabeledValue(Label.parse("a¬b"), -1);
		// System.out.println(YW);

		final CSTNUEdge XW = g.getEdgeFactory().get("XW");

		g.addEdge(XY, X, Y);
		g.addEdge(YW, Y, W);
		g.addEdge(XW, X, W);

		wellDefinition(g);
		// System.out.println(XW);

		cstnu.labelPropagation(X, Y, W, XY, YW, XW);
		// System.out.println(XW);

		final CSTNUEdge xwOK = g.getEdgeFactory().get("XW");
		// xwOK.mergeLabeledValue(Label.parse("a"), 4);if no positive edge are propagated
		xwOK.mergeLabeledValue(Label.parse("a¬b"), -2);// if not streamlined
		xwOK.mergeLabeledValue(Label.parse("ab"), 2);//
		// xwOK.mergeLabeledValue(Label.parse("¿a"), 0); From 20171010 unknown literal are not more propagated
		// xwOK.mergeLabeledValue(Label.parse("¿a¬b"), -3);
		// xwOK.mergeLabeledValue(Label.parse("¿ab"), -1);

		assertEquals("No case: XW labeled values.", xwOK.getLabeledValueMap(), XW.getLabeledValueMap());
	}

	/**
	 * Test method for
	 */
	@Test
	public final void test_lucRule() {
		final LabeledNode W = LabeledNodeSupplier.get("W", 'w');
		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		W.setLabel(Label.parse("a"));
		g.addVertex(W);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(X);
		g.addVertex(Y);
		g.setZ(Z);

		final ALabel aLabel = new ALabel(A.getName(), alpha);
		final ALabel bLabel = new ALabel(B.getName(), alpha);

		final CSTNUEdge XY = g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.emptyLabel, 2);
		XY.mergeLabeledValue(Label.parse("¬a"), 1);
		XY.mergeLabeledValue(Label.parse("b"), 1);
		XY.mergeLabeledValue(Label.parse("a¬b"), -1);
		// System.out.println(XY);

		final CSTNUEdge YW = g.getEdgeFactory().get("YW");
		YW.mergeLabeledValue(Label.parse("a"), -1);
		YW.mergeLabeledValue(Label.parse("a¬b"), -2);
		YW.mergeUpperCaseValue(Label.parse("a"), aLabel, -2);
		YW.mergeUpperCaseValue(Label.parse("ab"), aLabel, -3);
		YW.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -4);
		// System.out.println(YW);

		final CSTNUEdge XW = g.getEdgeFactory().get("XW");

		g.addEdge(XY, X, Y);
		g.addEdge(YW, Y, W);
		g.addEdge(XW, X, W);

		wellDefinition(g);
		// System.out.println(XW);

		cstnu.labelPropagation(X, Y, W, XY, YW, XW);
		// System.out.println(XW);

		final CSTNUEdge xwOK = g.getEdgeFactory().get("XW");
		xwOK.mergeLabeledValue(Label.parse("a"), 0);
		// xwOK.mergeLabeledValue(Label.parse("¿a¬b"), -3);From 20171010 unknown literal are not more propagated
		xwOK.mergeLabeledValue(Label.parse("ab"), 2);
		xwOK.mergeLabeledValue(Label.parse("a¬b"), -3);
		xwOK.mergeUpperCaseValue(Label.parse("a"), aLabel, 0);// not stored because is 0
		xwOK.mergeUpperCaseValue(Label.parse("ab"), aLabel, -2);
		// xwOK.mergeUpperCaseValue(Label.parse("a¿b"), aLabel, -4);
		// xwOK.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -5);//not allowed because rule does not back propagate conjuncted UC from
		// node != Z

		// assertEquals("No case: XW ", xwOK.toString(), XW.toString());

		assertEquals("No case: XW labeled values.", xwOK.getLabeledValueMap(), XW.getLabeledValueMap());
		// 2018-12-18 Trying to make a-label simplification faster... loosing some optimization
		// asserEquals when full optimization is activated.
		assertNotEquals("No case: XW upper case labedled values.", xwOK.getUpperCaseValueMap(), XW.getUpperCaseValueMap());
	}

}
