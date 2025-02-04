package it.univr.di.cstnu.algorithms;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.univr.di.cstnu.algorithms.CSTNU.CSTNUCheckStatus;
import it.univr.di.cstnu.graph.*;
import it.univr.di.cstnu.graph.Edge.ConstraintType;
import it.univr.di.labeledvalue.*;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class CSTNPSUTest {

	/**
	 * Default implementation class for CSTNEdge
	 */
	static final Class<? extends CSTNPSUEdge> EDGE_IMPL_CLASS = CSTNPSUEdgePluggable.class;
	/**
	 *
	 */
	static File fileCSTNPSU = new File("src/test/resources/template_parent_006-021.cstnpsu");
	/**
	 *
	 */
	CSTNPSU cstnpsu;

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
	 * @throws java.lang.Exception nope
	 */
	@Before
	public void setUp() throws Exception {
		this.alphabet = new ALabelAlphabet();
		this.g = new TNGraph<>("TestGraph",EDGE_IMPL_CLASS, this.alphabet);
		this.Z = LabeledNodeSupplier.get("Z");
		this.g.setZ(this.Z);
		this.cstnpsu = new CSTNPSU(this.g);
	}

	/**
	 * @param g1 input graph
	 */
	private void wellDefinition(TNGraph<CSTNPSUEdge> g1) {
		this.cstnpsu.setG(g1);
		try {
			this.cstnpsu.checkWellDefinitionProperties();
		} catch (WellDefinitionException e) {
			fail("TNGraph not well-formed: " + e.getMessage());
		}
	}

	/**
	 * @throws WellDefinitionException nope
	 */
	@Test
	public final void testCaseLabelRemovalRule() throws WellDefinitionException {
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode B = LabeledNodeSupplier.get("B");
		final ALabel aLabel4B = new ALabel(B.getName(), this.alphabet);
		B.setALabel(aLabel4B);
		final LabeledNode C = LabeledNodeSupplier.get("C");

		final LabeledNode a = LabeledNodeSupplier.get("A?");
		a.setObservable('a');
		this.g.addVertex(a);
		final LabeledNode b = LabeledNodeSupplier.get("B?");
		b.setObservable('b');
		this.g.addVertex(b);

		final CSTNPSUEdge AB = this.g.getEdgeFactory().get("AB");
		AB.mergeLowerCaseValue(Label.emptyLabel, aLabel4B, 13);
		AB.mergeLabeledValue(Label.emptyLabel, 30);
		AB.setConstraintType(ConstraintType.contingent);

		final CSTNPSUEdge BA = this.g.getEdgeFactory().get("BA");
		BA.setConstraintType(ConstraintType.contingent);
		BA.mergeLabeledValue(Label.emptyLabel, -2);
		BA.mergeUpperCaseValue(Label.emptyLabel, aLabel4B, -20);

		final CSTNPSUEdge cz = this.g.getEdgeFactory().get("CZ");
		cz.mergeUpperCaseValue(Label.parse("ab"), aLabel4B, -4);
		cz.mergeUpperCaseValue(Label.parse("a"), aLabel4B, -3);
		cz.mergeUpperCaseValue(Label.parse("¬b"), aLabel4B, -3);
		cz.mergeUpperCaseValue(Label.parse("¬ab"), aLabel4B, -15);

		final CSTNPSUEdge az = this.g.getEdgeFactory().get("AZ");
		az.mergeLabeledValue(Label.parse("ab"), -1);

		this.g.addEdge(AB, A, B);
		this.g.addEdge(BA, B, A);
		this.g.addEdge(cz, C, this.Z);
		this.g.addEdge(az, A, this.Z);

		this.cstnpsu.initAndCheck();

		this.cstnpsu.rG4(C, cz);

		final CSTNPSUEdge CZOk = this.g.getEdgeFactory().get("CZ");
		// remember that CZ contains also (0,emptyLabel)
		CZOk.mergeLabeledValue(Label.emptyLabel, -2);
		CZOk.mergeLabeledValue(Label.parse("ab"), -3);
		CZOk.mergeUpperCaseValue(Label.parse("ab"), aLabel4B, -4);
		CZOk.mergeUpperCaseValue(Label.parse("a"), aLabel4B, -3);
		CZOk.mergeUpperCaseValue(Label.parse("¬b"), aLabel4B, -3);
		CZOk.mergeUpperCaseValue(Label.parse("¬ab"), aLabel4B, -15);

		assertEquals("Upper Case values:", CZOk.toString(), cz.toString());
	}

	/**
	 * Test method for
	 *
	 * <pre>
	 * Z &lt;--- -3,D,b¬c--- C &lt;-----3,c,ab---- A
	 * </pre>
	 */

	@Test
	public final void testCrossCaseRule() {
		// System.out.printf("CROSS CASE\n");
		final LabeledNode C = LabeledNodeSupplier.get("C");
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final CSTNPSUEdge ac = this.g.getEdgeFactory().get("AC");
		ac.setConstraintType(ConstraintType.contingent);
		final CSTNPSUEdge ca = this.g.getEdgeFactory().get("CA");
		ca.setConstraintType(ConstraintType.contingent);
		final CSTNPSUEdge cz = this.g.getEdgeFactory().get("CZ");
		final CSTNPSUEdge az = this.g.getEdgeFactory().get("AZ");
		this.g.addEdge(ac, A, C);
		this.g.addEdge(ca, C, A);
		this.g.addEdge(cz, C, this.Z);
		this.g.addEdge(az, A, this.Z);
		this.g.addVertex(D);
		ac.mergeLowerCaseValue(Label.parse("ab"), new ALabel(C.getName(), this.alphabet), 3);
		ca.mergeUpperCaseValue(Label.parse("ab"), new ALabel(C.getName(), this.alphabet), -10);

		cz.mergeUpperCaseValue(Label.parse("b¬c"), new ALabel(D.getName(), this.alphabet), -4);
		cz.mergeUpperCaseValue(Label.parse("b¬f"), new ALabel(D.getName(), this.alphabet), 3);
		cz.mergeUpperCaseValue(Label.parse("¬b"), new ALabel(D.getName(), this.alphabet), -4);
		cz.mergeUpperCaseValue(Label.parse("ab"), new ALabel(C.getName(), this.alphabet), -4);

		try {
			this.cstnpsu.initAndCheck();
		} catch (WellDefinitionException e) {
			e.printStackTrace();
		}
		this.cstnpsu.rG2(A, C, this.Z, ac, cz, az);

		final CSTNPSUEdge daOk = this.g.getEdgeFactory().get("AZ");
		daOk.mergeUpperCaseValue(Label.parse("ab¬c"), new ALabel(D.getName(), this.alphabet), -1);
		daOk.mergeUpperCaseValue(Label.parse("a¿b"), new ALabel(D.getName(), this.alphabet), -1);

		assertEquals("Upper Case values:", daOk.getUpperCaseValueMap(), az.getUpperCaseValueMap());
	}

	/**
	 * Test method for
	 *
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
		final CSTNPSUEdge pz = this.g.getEdgeFactory().get("PX");
		pz.mergeLabeledValue(Label.parse("abpq"), -10);// ok but p has to be removed
		pz.mergeLabeledValue(Label.parse("ab¬p"), 0);// it is removed during init by (0, ⊡)
		pz.mergeLabeledValue(Label.parse("c¬p"), 1);// verrà cancellato in fase di 'init'!
		pz.mergeLabeledValue(Label.parse("¬c¬pa"), -1);// ok but ¬c¬p has to be removed
		pz.mergeUpperCaseValue(Label.parse("ab¬p"), new ALabel("C", this.alphabet), -11);// ok
		this.g.addEdge(pz, P, this.Z);
		this.g.addVertex(Q);
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(C);

		this.cstnpsu.initAndCheck();// from nov 2017, it doesn't clear odd values
		this.cstnpsu.rM1(P, pz);

		final CSTNPSUEdge pxOK = this.g.getEdgeFactory().get("XY");
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
		pxOK.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", this.alphabet), -11);

		assertEquals("R0: p?X labeled values.", pxOK.getLabeledValueMap(), pz.getLabeledValueMap());
		assertEquals("R0: px upper case labeled values.", pxOK.getUpperCaseValueMap(), pz.getUpperCaseValueMap());
	}

	/**
	 * Test method for
	 *
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
		final CSTNPSUEdge pz = this.g.getEdgeFactory().get("PZ");
		pz.mergeLabeledValue(Label.parse("abp¿q"), -10);
		pz.mergeLabeledValue(Label.parse("ab¬p"), -1);
		pz.mergeLabeledValue(Label.parse("¬a¬c¬p"), -2);
		pz.mergeLabeledValue(Label.parse("¬acp"), -2);
		pz.mergeLabeledValue(Label.parse("¿c¿p"), 1);
		pz.mergeUpperCaseValue(Label.parse("¬a¬p"), new ALabel("C", this.alphabet), -2);
		this.g.addEdge(pz, P, this.Z);
		this.g.addVertex(Q);
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(C);
		this.g.setZ(this.Z);
		this.cstnpsu.initAndCheck();
		this.cstnpsu.rM1(P, pz);

		final CSTNPSUEdge pxOK = this.g.getEdgeFactory().get("XY");
		pxOK.mergeLabeledValue(Label.emptyLabel, 0);
		pxOK.mergeLabeledValue(Label.parse("ab"), -1);
		// pxOK.mergeLabeledValue(Label.parse("ab¿q"), -10);// if it were streamlined
		pxOK.mergeLabeledValue(Label.parse("ab"), -10);// if it is not streamlined
		pxOK.mergeLabeledValue(Label.parse("¬a"), -2);
		// pxOK.mergeLabeledValue(Label.parse("¿c¿p"), 1);// NO!
		pxOK.mergeUpperCaseValue(Label.parse("¬a¬p"), new ALabel("C", this.alphabet), -2);

		assertEquals("R0: P?Z labeled values.", pxOK.getLabeledValueMap(), pz.getLabeledValueMap());
		// 2018-12-18 Trying to make a-label simplification faster... loosing some optimization
		// asserEquals when full optimization is activated.
		assertNotEquals("R0: PZ upper case labeled values.", pxOK.getUpperCaseValueMap(), pz.getUpperCaseValueMap());
	}

	/**
	 * Test method for
	 *
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
		this.g.addVertex(P);
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(G);
		this.g.addVertex(C);
		this.g.addVertex(X);
		this.g.addVertex(Y);
		this.g.setZ(this.Z);
		final CSTNPSUEdge gp = this.g.getEdgeFactory().get("GP");
		gp.mergeLabeledValue(Label.parse("p"), -4);
		this.g.addEdge(gp, G, P);

		final CSTNPSUEdge px = this.g.getEdgeFactory().get("PX");
		px.mergeLabeledValue(Label.parse("¬b"), 0);
		px.mergeLabeledValue(Label.parse("ab"), -10);
		px.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", this.alphabet), -11);

		final CSTNPSUEdge yx = this.g.getEdgeFactory().get("YX");
		yx.mergeLabeledValue(Label.parse("bgp"), -4);
		yx.mergeLabeledValue(Label.parse("cp"), -10);
		yx.mergeLabeledValue(Label.parse("c¬p"), 11);
		yx.mergeUpperCaseValue(Label.parse("bgp"), new ALabel("C", this.alphabet), -7);

		this.g.addEdge(px, P, X);
		this.g.addEdge(yx, Y, X);

		wellDefinition(this.g);

		this.cstnpsu.rM2(Y, yx);

		// <YX, normal, Y, X, L:{(¬ABGp, -4) (ABG, -4) }, LL:{}, UL:{(¬ABG¬p,C:-4) (¬ABGp,C:-7) (ABG,C:-7) }>
		final CSTNPSUEdge yxOK = this.g.getEdgeFactory().get("YX");
		// yxOK.mergeLabeledValue(Label.parse("¬abgp"), -4);
		// yxOK.mergeLabeledValue(Label.parse("ab"), -4); from 20171017 R3 is only qR3*
		// yxOK.mergeLabeledValue(Label.parse("abc"), -10); from 20171017 R3 is only qR3*
		// yxOK.mergeLabeledValue(Label.parse("¬bc"), 0);// R5 rule. from 20171017 R3 is only qR3*
		yxOK.mergeLabeledValue(Label.parse("bgp"), -4);// original
		yxOK.mergeLabeledValue(Label.parse("c¬p"), 11);// original
		yxOK.mergeLabeledValue(Label.parse("cp"), -10);// original

		// yxOK.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", this.alpha), -7);from 20171017 R3 is only qR3*
		yxOK.mergeUpperCaseValue(Label.parse("bgp"), new ALabel("C", this.alphabet), -7);// original

		assertEquals("R3: yx labeled values.", yxOK.getLabeledValueMap(), yx.getLabeledValueMap());
		assertEquals("R3: yx upper case labeled values.", yxOK.getUpperCaseValueMap(), yx.getUpperCaseValueMap());
	}

	/**
	 *
	 */
	@Test
	public final void testLabelModificationR3bis() {
		this.alphabet.clear();

		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode N8 = LabeledNodeSupplier.get("n8");
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(N8);
		this.g.setZ(this.Z);

		final CSTNPSUEdge eN8Z = this.g.getEdgeFactory().get("n8_Z");
		LabeledIntMap map = AbstractLabeledIntMap.parse("{(-20, ab) (-∞, ¿ab) (-8, ¬b) (-17, b) }");
		eN8Z.setLabeledValueMap(map);
		LabeledALabelIntTreeMap map1 = LabeledALabelIntTreeMap
			.parse(
				"{(D, -∞, ¿ab) (D, -30, ab) (D, -∞, a¿b) (D, -4, ¿b) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) (F, -19, ¬ab) (F, -∞, ¿ab) }",
				this.alphabet, null);
		eN8Z.setUpperCaseValueMap(map1);
		this.g.addEdge(eN8Z, N8, this.Z);

		// {❮B?_Z; derived; {(0, ⊡) }; ❯, ❮A?_Z; derived; {(-2, ⊡) (-4, b) }; ❯}
		final CSTNPSUEdge eAZ = this.g.getEdgeFactory().get("A?_Z");
		map = AbstractLabeledIntMap.parse("{(-2, ⊡) (-4, b) }");
		eAZ.setLabeledValueMap(map);
		eAZ.setUpperCaseValueMap(map1);
		this.g.addEdge(eAZ, A, this.Z);

		// System.out.println(eN8Z);
		this.cstnpsu.rM2(N8, eN8Z);

		final CSTNPSUEdge eN8ZOK = this.g.getEdgeFactory().get("n8_Z");
		map = AbstractLabeledIntMap.parse("{(-20, ab) (-∞, ¿ab) (-8, ¬b) (-17, b) }");
		eN8ZOK.setLabeledValueMap(map);
		map1 = LabeledALabelIntTreeMap
			.parse(
				"{(D, -∞, ¿ab) (D, -30, ab) (D, -∞, a¿b) (D, -4, ¿b) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) (F, -19, ¬ab) (F, -∞, ¿ab) }",
				this.alphabet, null);
		eN8ZOK.setUpperCaseValueMap(map1);

		assertEquals("R3: eN8Z labeled values.", eN8ZOK.getLabeledValueMap(), eN8Z.getLabeledValueMap());
		assertEquals("R3: eN8ZOK upper case labeled values.", eN8ZOK.getUpperCaseValueMap(),
		             eN8Z.getUpperCaseValueMap());

		this.cstnpsu.rM2(N8, eN8Z);

		assertEquals("R3: eN8Z labeled values.", eN8ZOK.getLabeledValueMap(), eN8Z.getLabeledValueMap());
		assertEquals("R3: eN8ZOK upper case labeled values.", eN8ZOK.getUpperCaseValueMap(),
		             eN8Z.getUpperCaseValueMap());

	}

	/**
	 * Test method for
	 *
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
		this.g.addVertex(P);
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(G);
		this.g.addVertex(C);
		this.g.addVertex(Y);
		this.g.setZ(this.Z);
		final CSTNPSUEdge gp = this.g.getEdgeFactory().get("GP");
		gp.mergeLabeledValue(Label.parse("p"), -4);
		this.g.addEdge(gp, G, P);

		final CSTNPSUEdge pz = this.g.getEdgeFactory().get("PZ");
		pz.mergeLabeledValue(Label.parse("¬b"), 0);
		pz.mergeLabeledValue(Label.parse("ab"), -10);
		pz.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", this.alphabet), -11);

		final CSTNPSUEdge yz = this.g.getEdgeFactory().get("YZ");
		yz.mergeLabeledValue(Label.parse("bg¿p"), -4);
		yz.mergeLabeledValue(Label.parse("cp"), -10);
		yz.mergeLabeledValue(Label.parse("¿cp"), -11);
		yz.mergeUpperCaseValue(Label.parse("bgp"), new ALabel("C", this.alphabet), -7);

		this.g.addEdge(pz, P, this.Z);
		this.g.addEdge(yz, Y, this.Z);

		wellDefinition(this.g);

		this.cstnpsu.rM2(Y, yz);

		// <YX, normal, Y, X, L:{(¬ABGp, -4) (ABG, -4) }, LL:{}, UL:{(¬ABG¬p,C:-4) (¬ABGp,C:-7) (ABG,C:-7) }>
		final CSTNPSUEdge yxOK = this.g.getEdgeFactory().get("YX");
		// yxOK.mergeLabeledValue(Label.parse("¬abgp"), -4);
		// yxOK.mergeUpperCaseValue(Label.parse("abg"), new ALabel("C", this.alpha), -7);// if streamlined
		yxOK.mergeUpperCaseValue(Label.parse("ab"), new ALabel("C", this.alphabet), -7);// if not streamlined
		yxOK.mergeUpperCaseValue(Label.parse("abc"), new ALabel("C", this.alphabet),
		                         -10);// it could not be present because there is the labeled value
		// (-10,abc)... it depends in which
		// order it is inserted.
		yxOK.mergeUpperCaseValue(Label.parse("bgp"), new ALabel("C", this.alphabet), -7);//
		yxOK.mergeUpperCaseValue(Label.parse("ab¿c"), new ALabel("C", this.alphabet), -11);//

		yxOK.mergeLabeledValue(Label.parse("ab"), -4);// if not streamlined
		// yxOK.mergeLabeledValue(Label.parse("abg"), -4);// if streamlined
		yxOK.mergeLabeledValue(Label.parse("cp"), -10);
		yxOK.mergeLabeledValue(Label.parse("¿cp"), -11);
		yxOK.mergeLabeledValue(Label.parse("abc"), -10);
		yxOK.mergeLabeledValue(Label.parse("bg¿p"), -4);

		assertEquals("R3: yx labeled values.", yxOK.getLabeledValueMap(), yz.getLabeledValueMap());
		assertEquals("R3: yx upper case labeled values.", yxOK.getUpperCaseValueMap(), yz.getUpperCaseValueMap());
	}

	/*
	 * Test method for
	 *
	 * <pre>
	 * A &lt;----[1,,b][0,,c][-11,,d]--- C &lt;---[3,c,ab]--- D
	 * </pre>
	 *
	 * @Test
	 *       public final void testLowerCaseRule() {
	 *       // System.out.printf("LOWER CASE\n");
	 *       TNGraph g = new TNGraph(this.LABELED_VALUE_MAP_IMPL_CLASS);
	 *       LabeledIntEdgePlugged ble dc = this.g.getEdgeFactory().get("DC");
	 *       CSTNPSUEdge ca = this.g.getEdgeFactory().get("CA");
	 *       CSTNPSUEdge da = this.g.getEdgeFactory().get("DA");
	 *       dc.mergeLowerCaseValue(Label.parse("ab"), new ALabel("c", this.alpha), 3);
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
	 *       CSTNPSUEdge daOk = this.g.getEdgeFactory().get("DA");
	 *       daOk.mergeLabeledValue(Label.parse("abc"), 3);
	 *       daOk.mergeLabeledValue(Label.parse("abd"), -8);
	 *       assertEquals("Lower Case values:", daOk.getLabeledValueMap(), da.getLabeledValueMap());
	 *       // System.out.printf("G.hasSameEdge(G1): %s\n", g.hasAllEdgesOf(g1));
	 *       // System.out.printf("G: %s\n", g1);
	 *       }
	 */

	/*
	 * Test method for
	 *
	 * <pre>
	 * Z &lt;----[1,,b][0,,c][-11,,¬b]--- C &lt;---[3,c,ab]--- D
	 * </pre>
	 *
	 * @Test
	 *       public final void testQLowerCaseRule() {
	 *       // System.out.printf("LOWER CASE\n");
	 *       TNGraph g = new TNGraph(this.LABELED_VALUE_MAP_IMPL_CLASS);
	 *       CSTNPSUEdge dc = this.g.getEdgeFactory().get("DC");
	 *       CSTNPSUEdge ca = this.g.getEdgeFactory().get("CA");
	 *       CSTNPSUEdge da = this.g.getEdgeFactory().get("DA");
	 *       dc.mergeLowerCaseValue(Label.parse("ab"), new ALabel("c", this.alpha), 3);
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
	 *       CSTNPSUEdge daOk = this.g.getEdgeFactory().get("DA");
	 *       daOk.mergeLabeledValue(Label.parse("abc"), 3);
	 *       daOk.mergeLabeledValue(Label.parse("a¿b"), -8);
	 *       assertEquals("Lower Case values:", daOk.getLabeledValueMap(), da.getLabeledValueMap());
	 *       // System.out.printf("G.hasSameEdge(G1): %s\n", g.hasAllEdgesOf(g1));
	 *       // System.out.printf("G: %s\n", g1);
	 *       }
	 */

	/**
	 * Test method for Z &lt;---[3,B,ab]--- C &lt;----[-13,,b][11,,c]----D
	 */

	@Test
	public final void testUpperCaseRule() {
		// System.out.printf("UPPER CASE\n");
		final CSTNPSUEdge dc = this.g.getEdgeFactory().get("dc");
		final CSTNPSUEdge cz = this.g.getEdgeFactory().get("cz");
		final CSTNPSUEdge dz = this.g.getEdgeFactory().get("dz");
		cz.mergeUpperCaseValue(Label.parse("ab"), new ALabel("B", this.alphabet), 3);
		dc.mergeLabeledValue(Label.parse("b"), -13);
		dc.mergeLabeledValue(Label.parse("c"), 11);
		final LabeledNode C = LabeledNodeSupplier.get("C");
		final LabeledNode D = LabeledNodeSupplier.get("D");
		this.g.addEdge(dc, D, C);
		this.g.addEdge(cz, C, this.Z);
		this.g.addEdge(dz, D, this.Z);
		this.cstnpsu.rG1G3(D, C, this.Z, dc, cz, dz);

		final CSTNPSUEdge daOk = this.g.getEdgeFactory().get("DZ");
		daOk.mergeUpperCaseValue(Label.parse("abc"), new ALabel("B", this.alphabet), 14);
		daOk.mergeUpperCaseValue(Label.parse("ab"), new ALabel("B", this.alphabet), -10);

		assertEquals("Upper Case values:", daOk.getUpperCaseValueMap(), dz.getUpperCaseValueMap());
	}

	/**
	 *
	 */
	@Test
	public final void test_lncRule() {
		final LabeledNode W = LabeledNodeSupplier.get("W", 'w');
		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		W.setLabel(Label.parse("a"));
		this.g.addVertex(W);
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(X);
		this.g.addVertex(Y);
		this.g.setZ(this.Z);

		final CSTNPSUEdge XY = this.g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.emptyLabel, 2);
		XY.mergeLabeledValue(Label.parse("¬a"), -2);
		XY.mergeLabeledValue(Label.parse("b"), 1);
		XY.mergeLabeledValue(Label.parse("a¬b"), -1);
		// System.out.println(XY);

		final CSTNPSUEdge YZ = this.g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.parse("a"), 2);
		YZ.mergeLabeledValue(Label.parse("ab"), 1);
		YZ.mergeLabeledValue(Label.parse("a¬b"), -1);
		// System.out.println(YW);

		final CSTNPSUEdge XZ = this.g.getEdgeFactory().get("XZ");

		this.g.addEdge(XY, X, Y);
		this.g.addEdge(YZ, Y, this.Z);
		this.g.addEdge(XZ, X, this.Z);

		wellDefinition(this.g);
		// System.out.println(XW);

		this.cstnpsu.rG1G3(X, Y, this.Z, XY, YZ, XZ);
		// System.out.println(XW);

		final CSTNPSUEdge xwOK = this.g.getEdgeFactory().get("XZ");
		// xwOK.mergeLabeledValue(Label.parse("a"), 4);if no positive edge are propagated
		xwOK.mergeLabeledValue(Label.parse("a¬b"), -2);// if not streamlined
		xwOK.mergeLabeledValue(Label.parse("¿a¬b"), -3);
		xwOK.mergeLabeledValue(Label.parse("¿a"), -1);

		assertEquals("No case test.", xwOK.getLabeledValueMap(), XZ.getLabeledValueMap());
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
		this.g.addVertex(W);
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(X);
		this.g.addVertex(Y);
		this.g.setZ(this.Z);

		final ALabel aLabel = new ALabel(A.getName(), this.alphabet);
		final ALabel bLabel = new ALabel(B.getName(), this.alphabet);

		final CSTNPSUEdge XY = this.g.getEdgeFactory().get("XY");
		XY.mergeLabeledValue(Label.emptyLabel, 2);
		XY.mergeLabeledValue(Label.parse("¬a"), 1);
		XY.mergeLabeledValue(Label.parse("b"), 1);
		XY.mergeLabeledValue(Label.parse("a¬b"), -1);
		// System.out.println(XY);

		final CSTNPSUEdge YZ = this.g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.parse("a"), -1);
		YZ.mergeLabeledValue(Label.parse("a¬b"), -2);
		YZ.mergeUpperCaseValue(Label.parse("a"), aLabel, -2);
		YZ.mergeUpperCaseValue(Label.parse("ab"), aLabel, -3);
		YZ.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -4);
		// System.out.println(YW);

		final CSTNPSUEdge XZ = this.g.getEdgeFactory().get("XZ");

		this.g.addEdge(XY, X, Y);
		this.g.addEdge(YZ, Y, this.Z);
		this.g.addEdge(XZ, X, this.Z);

		wellDefinition(this.g);
		// System.out.println(XW);

		this.cstnpsu.rG1G3(X, Y, this.Z, XY, YZ, XZ);
		// System.out.println(XW);

		final CSTNPSUEdge xwOK = this.g.getEdgeFactory().get("XW");
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

		assertEquals("No case: XZ labeled values.", xwOK.getLabeledValueMap(), XZ.getLabeledValueMap());
		// 2018-12-18 Trying to make a-label simplification faster... loosing some optimization
		// asserEquals when full optimization is activated.
		assertNotEquals("No case: XZ upper case labeled values.", xwOK.getUpperCaseValueMap(),
		                XZ.getUpperCaseValueMap());
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
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(X);
		this.g.addVertex(Y);
		this.g.setZ(this.Z);

		final ALabel aLabel = new ALabel(A.getName(), this.alphabet);
		final ALabel bLabel = new ALabel(B.getName(), this.alphabet);

		final CSTNPSUEdge XY = this.g.getEdgeFactory().get("XY");
		XY.mergeUpperCaseValue(Label.emptyLabel, aLabel, 1);
		XY.mergeUpperCaseValue(Label.parse("¬a"), aLabel, -3);
		XY.mergeUpperCaseValue(Label.parse("b"), aLabel, -4);
		XY.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -2);
		// System.out.println(XY);

		final CSTNPSUEdge YZ = this.g.getEdgeFactory().get("YZ");
		YZ.mergeLabeledValue(Label.parse("a"), 2);
		YZ.mergeLabeledValue(Label.parse("ab"), 1);
		YZ.mergeLabeledValue(Label.parse("a¬b"), -1);

		final CSTNPSUEdge XZ = this.g.getEdgeFactory().get("XZ");

		this.g.addEdge(XY, X, Y);
		this.g.addEdge(YZ, Y, this.Z);
		this.g.addEdge(XZ, X, this.Z);

		wellDefinition(this.g);
		// System.out.println(XW);

		this.cstnpsu.rG1G3(X, Y, this.Z, XY, YZ, XZ);
		// System.out.println(XW);

		final CSTNPSUEdge xzOK = this.g.getEdgeFactory().get("XW");
		// FLUC and LCUC change upper case values!
		// xzOK.mergeUpperCaseValue(Label.parse("¿a"), aLabel, -1); From 20171010 unknown literal are not more propagated
		// xzOK.mergeUpperCaseValue(Label.parse("¿a¬b"), aLabel, -4);
		// xzOK.mergeUpperCaseValue(Label.parse("ab"), aLabel, -3);// X<>A?, so rule cannot be applied
		// xzOK.mergeUpperCaseValue(Label.parse("a¿b"), aLabel, -5);

		assertEquals("No case: XZ labeled values.", xzOK.getLabeledValueMap(), XZ.getLabeledValueMap());
		assertEquals("No case: XZ upper case labeled values.", xzOK.getUpperCaseValueMap(), XZ.getUpperCaseValueMap());
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
		this.g.addVertex(A);
		this.g.addVertex(B);
		this.g.addVertex(X);
		this.g.addVertex(Y);
		this.g.setZ(this.Z);

		final ALabel aLabel = new ALabel(A.getName(), this.alphabet);
		final ALabel bLabel = new ALabel(B.getName(), this.alphabet);

		final CSTNPSUEdge XY = this.g.getEdgeFactory().get("XY");
		XY.mergeUpperCaseValue(Label.emptyLabel, aLabel, 1);
		XY.mergeUpperCaseValue(Label.parse("¬a"), aLabel, -3);
		XY.mergeUpperCaseValue(Label.parse("b"), aLabel, -4);
		XY.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -2);
		// System.out.println(XY);

		final CSTNPSUEdge YZ = this.g.getEdgeFactory().get("YZ");
		YZ.mergeUpperCaseValue(Label.parse("a"), aLabel, 1);
		YZ.mergeUpperCaseValue(Label.parse("ab"), aLabel, 0);
		YZ.mergeUpperCaseValue(Label.parse("a¬b"), aLabel.conjunction(bLabel), -2);

		final CSTNPSUEdge XZ = this.g.getEdgeFactory().get("XZ");

		this.g.addEdge(XY, X, Y);
		this.g.addEdge(YZ, Y, this.Z);
		this.g.addEdge(XZ, X, this.Z);

		wellDefinition(this.g);
		// System.out.println(XW);

		this.cstnpsu.rG1G3(X, Y, this.Z, XY, YZ, XZ);
		// System.out.println(XW);

		final CSTNPSUEdge xzOK = this.g.getEdgeFactory().get("XZ");
		// <{¿a=A?->-2, ab=A?->-4, ¿a¬b=A?∙B?->-5, a¿b=A?∙B?->-6}>
		// xwOK.mergeUpperCaseValue(Label.parse("¿a"), aLabel, -2);From 20171010 unknown literal are not more propagated
		// xwOK.mergeUpperCaseValue(Label.parse("ab"), aLabel, -4);
		// xwOK.mergeUpperCaseValue(Label.parse("¿a¬b"), aLabel.conjunction(bLabel), -5);
		// xwOK.mergeUpperCaseValue(Label.parse("a¿b"), aLabel.conjunction(bLabel), -6);

		assertEquals("No case: XZ labeled values.", xzOK.getLabeledValueMap(), XZ.getLabeledValueMap());
		assertEquals("No case: XZ upper case labeled values.", xzOK.getUpperCaseValueMap(), XZ.getUpperCaseValueMap());
	}

	/**
	 * @throws WellDefinitionException nope
	 */
	@Test
	public final void test_llcRule() throws WellDefinitionException {
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode C = LabeledNodeSupplier.get("C");
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final LabeledNode a = LabeledNodeSupplier.get("a?", 'a');
		final LabeledNode b = LabeledNodeSupplier.get("b?", 'b');

		this.g.addVertex(A);
		this.g.addVertex(a);
		this.g.addVertex(b);
		this.g.addVertex(C);
		this.g.addVertex(D);
		this.g.setZ(this.Z);

		final ALabel CaLabel = new ALabel(C.getName(), this.alphabet);

		final CSTNPSUEdge CA = this.g.getEdgeFactory().get("CA");
		CA.mergeUpperCaseValue(Label.parse("a"), CaLabel, -6);
		CA.mergeLabeledValue(Label.parse("a"), -1);
		CA.setConstraintType(ConstraintType.contingent);
		final CSTNPSUEdge AC = this.g.getEdgeFactory().get("AC");
		AC.mergeLowerCaseValue(Label.parse("a"), CaLabel, 3);
		AC.mergeLabeledValue(Label.parse("a"), 10);
		AC.setConstraintType(ConstraintType.contingent);
		final CSTNPSUEdge CD = this.g.getEdgeFactory().get("CD");
		CD.mergeLabeledValue(Label.parse("b"), 0);

		final CSTNPSUEdge DA = this.g.getEdgeFactory().get("DA");
		DA.mergeLabeledValue(Label.parse("b"), -4);

		this.g.addEdge(AC, A, C);
		this.g.addEdge(CA, C, A);
		this.g.addEdge(CD, C, D);
		this.g.addEdge(DA, D, A);

		this.cstnpsu.setG(this.g);

		final CSTNUCheckStatus status = this.cstnpsu.dynamicControllabilityCheck();

		assertFalse(status.consistency);
	}


	/**
	 * @throws WellDefinitionException if there is a definition error
	 */
	@Test
	public final void testPrototypalLink() throws WellDefinitionException {
		final LabeledNode A = LabeledNodeSupplier.get("A");
		final LabeledNode C = LabeledNodeSupplier.get("C");
		final LabeledNode Ω = LabeledNodeSupplier.get("Ω");
		this.g.addVertex(A);
		this.g.addVertex(C);
		this.g.addVertex(Ω);
		this.g.setZ(this.Z);

		final ALabel CaLabel = new ALabel(C.getName(), this.alphabet);

		final CSTNPSUEdge CA = this.g.getEdgeFactory().get("CA");
		CA.mergeUpperCaseValue(Label.emptyLabel, CaLabel, -6);
		CA.mergeLabeledValue(Label.emptyLabel, -1);
		CA.setConstraintType(ConstraintType.contingent);

		final CSTNPSUEdge AC = this.g.getEdgeFactory().get("AC");
		AC.mergeLowerCaseValue(Label.emptyLabel, CaLabel, 3);
		AC.mergeLabeledValue(Label.emptyLabel, 10);
		AC.setConstraintType(ConstraintType.contingent);

		final CSTNPSUEdge ΩC = this.g.getEdgeFactory().get("ΩC");
		ΩC.mergeLabeledValue(Label.emptyLabel, -1);

		final CSTNPSUEdge CΩ = this.g.getEdgeFactory().get("CΩ");
		CΩ.mergeLabeledValue(Label.emptyLabel, 10);

		final CSTNPSUEdge ZA = this.g.getEdgeFactory().get("ZA");
		ZA.mergeLabeledValue(Label.emptyLabel, 0);

		this.g.addEdge(AC, A, C);
		this.g.addEdge(CA, C, A);
		this.g.addEdge(ΩC, Ω, C);
		this.g.addEdge(CΩ, C, Ω);
//		this.g.addEdge(ZA, this.Z, A);

		this.cstnpsu.setG(this.g);

		final CSTNUCheckStatus status = this.cstnpsu.dynamicControllabilityCheck();
		assertTrue(status.consistency);

		final CSTNPSU.PrototypalLink prototypalLink = this.cstnpsu.getPrototypalLink();
		assert prototypalLink != null;
		assertEquals(2, prototypalLink.getLowerBound());
		assertEquals(30, prototypalLink.getLowerGuard());
		assertEquals(7, prototypalLink.getUpperGuard());
		assertEquals(30, prototypalLink.getUpperBound());
		assertEquals(0, prototypalLink.getContingency());
	}

	/**
	 * Test for configureSubNetworks
	 *
	 * @throws WellDefinitionException      in network is not well defined.
	 * @throws IOException                  if any read error
	 * @throws ParserConfigurationException if any parser error
	 * @throws SAXException                 if any parse error
	 */
	@Test
	public final void testConfigureSubNetworks() throws Exception {
		final TNGraphMLReader<CSTNPSUEdge> readerCSTNPSU = new TNGraphMLReader<>();
		final TNGraph<CSTNPSUEdge> cstnpsuGraph = readerCSTNPSU.readGraph(fileCSTNPSU, CSTNPSUEdgePluggable.class);
		this.cstnpsu = new CSTNPSU(cstnpsuGraph);

		final ObjectList<CSTNPSU.PrototypalLink> links = new ObjectArrayList<>(2);
		links.add(new CSTNPSU.PrototypalLink("p1_Z", 119, 818, 230, 1070, 0, "p1_Ω"));
		links.add(new CSTNPSU.PrototypalLink("p2_Z", 92, 856, 219, 1033, 0, "p2_Ω"));

		final ObjectList<ObjectList<CSTNPSU.PrototypalLink>> solutions = this.cstnpsu.configureSubNetworks(links, 2);

//		int i=1;
//		for(ObjectList<CSTNPSU.PrototypalLink> solution : solutions) {
//			System.out.println("\nSoluzione "+(i++));
//			for(CSTNPSU.PrototypalLink link : solution) {
//				System.out.print(link +", ");
//			}
//		}
		assert solutions != null;
		CSTNPSU.PrototypalLink sol0 = solutions.getFirst().getFirst();
		final CSTNPSU.PrototypalLink sol1;
		if ("p2_Z".equals(sol0.getStartingNodeName())) {
			sol1 = solutions.getFirst().get(1);
		} else {
			sol1 = sol0;
			sol0 = solutions.getFirst().get(1);
		}

		assertEquals(new CSTNPSU.PrototypalLink("p2_Z", 92, 219, 219, 267, 0, "p2_Ω"),
		             sol0);
		assertEquals(new CSTNPSU.PrototypalLink("p1_Z", 119, 230, 230, 278, 0, "p1_Ω"),
		             sol1);

		sol0 = solutions.get(1).getFirst();
		if (!"p2_Z".equals(sol0.getStartingNodeName())) {
//			sol1 = sol0;
			sol0 = solutions.get(1).get(1);
		}
		assertEquals(new CSTNPSU.PrototypalLink("p2_Z", 92, 220, 220, 267, 0, "p2_Ω"),
		             sol0);
	}
}
