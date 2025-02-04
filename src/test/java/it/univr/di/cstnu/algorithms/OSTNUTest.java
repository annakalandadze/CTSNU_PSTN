package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.graph.*;
import it.univr.di.labeledvalue.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Test
 */
@SuppressWarnings("DataFlowIssue")
public class OSTNUTest {

	/**
	 * Default implementation class for CSTNEdge
	 */
	static final Class<? extends OSTNUEdgePluggable> EDGE_IMPL_CLASS = OSTNUEdgePluggable.class;
	/**
	 *
	 */
	OSTNU ostnu;

	/**
	 * nodes
	 */
	LabeledNode Z;
	LabeledNode A;
	LabeledNode C;
	LabeledNode X;
	LabeledNode O;
	/**
	 * edges
	 */
	OSTNUEdgePluggable AC;
	OSTNUEdgePluggable CA;
	OSTNUEdgePluggable CO;

	/**
	 *
	 */
	ALabelAlphabet alphabet;

	/**
	 *
	 */
	TNGraph<OSTNUEdgePluggable> g;

	/**
	 * @throws java.lang.Exception nope
	 */
	@Before
	public void setUp() throws Exception {
		alphabet = new ALabelAlphabet();
		g = new TNGraph<>("no name", EDGE_IMPL_CLASS, alphabet);
		Z = LabeledNodeSupplier.get("Z");
		g.setZ(Z);
		A = new LabeledNode("A");
		C = new LabeledNode("C");
		X = new LabeledNode("X");
		O = new LabeledNode("OC");
		O.setObservable('C');
		this.g.addVertex(A);
		this.g.addVertex(C);
		this.g.addVertex(X);
		this.g.addVertex(O);
		this.AC = this.g.makeNewEdge("A-C", Edge.ConstraintType.contingent);
		this.AC.setLowerCaseValue(Label.emptyLabel, ALabel.parse("C", this.alphabet), 1);
		this.g.addEdge(AC, A, C);
		CA = this.g.makeNewEdge("C-A", Edge.ConstraintType.contingent);
		CA.mergeUpperCaseValue(Label.emptyLabel, ALabel.parse("C", this.alphabet), -10);
		this.g.addEdge(CA, C, A);

		ostnu = new OSTNU(g, Constants.INT_POS_INFINITE);
		ostnu.initAndCheck();
		CO = this.g.findEdge(C, O);
	}

	/**
	 * Test figure2 paper BPMDS
	 * <pre>
	 * A ==[1,10]==≥ C ---(-3)---≥ X -- (4)--≥ D
	 * ^                                       |
	 * |--------------(-10)--------------------
	 * </pre>
	 * It is NOT AC!
	 */
	@Test
	public void test2AgileControllabilityCheck() {
		//nodes
		final LabeledNode D = LabeledNodeSupplier.get("D");
		//edges
		final OSTNUEdgePluggable CX = g.getEdgeFactory().get("C-X");
		CX.mergeLabeledValue(Label.emptyLabel, -3);
		g.addEdge(CX, C, X);
		final OSTNUEdgePluggable XD = g.getEdgeFactory().get("X-D");
		XD.mergeLabeledValue(Label.emptyLabel, 4);
		g.addEdge(XD, X, D);
		final OSTNUEdgePluggable DA = g.getEdgeFactory().get("D-A");
		DA.mergeLabeledValue(Label.emptyLabel, -10);
		g.addEdge(DA, D, A);

		ostnu.checkStatus.initialized = false;
		try {
			ostnu.agileControllabilityCheck();
		} catch (WellDefinitionException e) {
			throw new RuntimeException(e);
		}

		assertFalse(ostnu.checkStatus.isControllable());
	}

	/**
	 * Test method for
	 *
	 * <pre>
	 * A ←---(-3,E,¬E)--- C ←-----(3,c,⊡)---- D
	 * </pre>
	 */
	@Test
	public final void test2LabeledCrossCaseRule() {
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final OSTNUEdgePluggable dc = g.getEdgeFactory().get("DC");
		final OSTNUEdgePluggable ca = g.getEdgeFactory().get("CA");
		final OSTNUEdgePluggable da = g.getEdgeFactory().get("DA");
		g.addEdge(dc, D, C);
		g.addEdge(da, D, A);
		dc.setLowerCaseValue(Label.emptyLabel, new ALabel(C.getName(), alphabet), 3);

		ca.mergeUpperCaseValue(Label.parse("¬e"), new ALabel("E", alphabet), -3);
		ostnu.labeledCrossCaseRule(D, C, A, dc, ca, da);

		final OSTNUEdgePluggable daOk = g.getEdgeFactory().get("DA");
		daOk.mergeUpperCaseValue(Label.parse("¬e"), new ALabel("E", alphabet), 0);

		assertEquals("Upper Case values:", daOk.getUpperCaseValueMap(), da.getUpperCaseValueMap());
	}

	/**
	 * Second test for Lower Case
	 * <pre>A ---(c:1,⊡)--≥ C ---(-3)---≥ D
	 *                        ≤---10----
	 * </pre>
	 * determines nothing because Oracle rule must be call.
	 * <pre>A---(empty)---≥ D</pre>
	 */
	@Test
	public void test2LabeledLowerCaseRule() {
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final OSTNUEdgePluggable cd = g.getEdgeFactory().get("CD");
		final OSTNUEdgePluggable dc = g.getEdgeFactory().get("DC");
		final OSTNUEdgePluggable ad = g.getEdgeFactory().get("AD");
		g.addEdge(cd, C, D);
		g.addEdge(dc, D, C);
		g.addEdge(ad, A, D);

		cd.mergeLabeledValue(Label.emptyLabel, -3);
		dc.mergeLabeledValue(Label.emptyLabel, 10);

		ostnu.labeledLowerCaseRule(A, C, D, AC, cd, ad);

		final OSTNUEdgePluggable daOk = g.getEdgeFactory().get("AD");
		assertEquals("Lower Case value:", daOk.getLabeledValueMap(), ad.getLabeledValueMap());

	}

	/**
	 * Test figure2 paper BPMDS
	 * <pre>
	 * A ==[1,10]==≥ C ---(-3)---≥ X -- (-10)--≥ D
	 * ^                                       |
	 * |--------------(4)--------------------
	 * </pre>
	 * It is NOT AC!
	 */
	@Test
	public void test3AgileControllabilityCheck() {
		//nodes
		final LabeledNode D = LabeledNodeSupplier.get("D");
		//edges
		final OSTNUEdgePluggable CX = g.getEdgeFactory().get("C-X");
		CX.mergeLabeledValue(Label.emptyLabel, -3);
		g.addEdge(CX, C, X);
		final OSTNUEdgePluggable XD = g.getEdgeFactory().get("X-D");
		XD.mergeLabeledValue(Label.emptyLabel, -10);
		g.addEdge(XD, X, D);
		final OSTNUEdgePluggable DA = g.getEdgeFactory().get("D-A");
		DA.mergeLabeledValue(Label.emptyLabel, 4);
		g.addEdge(DA, D, A);

		ostnu.checkStatus.initialized = false;
		try {
			ostnu.agileControllabilityCheck();
		} catch (WellDefinitionException e) {
			throw new RuntimeException(e);
		}

		assertFalse(ostnu.checkStatus.isControllable());
	}

	/**
	 * Test figure2 paper BPMDS
	 * <pre>
	 * Z≤--(0)---A ==[1,2]==≥ C ≤------(0)-----A3≤-----O_D             B3
	 *                                        |        ↑              |
	 *                                      (0)        -(0)-------    [1,2]
	 *                                      ↓                    |  ↓--|
	 *                                      A4 ====[480,720]==≥  D
	 * </pre>
	 */
	@Test
	public void testAgileControllabilityCheck() {
		//nodes
		final LabeledNode A3 = LabeledNodeSupplier.get("A3");
		final LabeledNode B3 = LabeledNodeSupplier.get("B3");
		final LabeledNode A4 = LabeledNodeSupplier.get("A4");
		final LabeledNode D = LabeledNodeSupplier.get("D");
		D.setALabel(new ALabel("D", alphabet));
		final LabeledNode OD = LabeledNodeSupplier.get("OD");
		OD.setObservable('D');
		//edges
		CA.mergeUpperCaseValue(Label.emptyLabel, C.getALabel(), -2);
		final OSTNUEdgePluggable A3C = g.getEdgeFactory().get("A3-C");
		A3C.mergeLabeledValue(Label.emptyLabel, 0);
		g.addEdge(A3C, A3, C);
		final OSTNUEdgePluggable ODA3 = g.getEdgeFactory().get("OD-A3");
		ODA3.mergeLabeledValue(Label.emptyLabel, 0);
		g.addEdge(ODA3, OD, A3);
		final OSTNUEdgePluggable A3A4 = g.getEdgeFactory().get("A3-A4");
		A3A4.mergeLabeledValue(Label.emptyLabel, 0);
		g.addEdge(A3A4, A3, A4);
		final OSTNUEdgePluggable A4D = g.getEdgeFactory().get("A4-D");
		A4D.setConstraintType(Edge.ConstraintType.contingent);
		A4D.setLowerCaseValue(Label.emptyLabel, D.getALabel(), 480);
		g.addEdge(A4D, A4, D);
		final OSTNUEdgePluggable DA4 = g.getEdgeFactory().get("D-A4");
		DA4.setConstraintType(Edge.ConstraintType.contingent);
		DA4.mergeUpperCaseValue(Label.emptyLabel, D.getALabel(), -720);
		g.addEdge(DA4, D, A4);
		final OSTNUEdgePluggable B3D = g.getEdgeFactory().get("B3-D");
		B3D.mergeLabeledValue(Label.emptyLabel, 2);
		g.addEdge(B3D, B3, D);
		final OSTNUEdgePluggable DB3 = g.getEdgeFactory().get("D-B3");
		DB3.mergeLabeledValue(Label.emptyLabel, -1);
		g.addEdge(DB3, D, B3);
		this.g.removeVertex(X);

//		TNGraphMLWriter writer = new TNGraphMLWriter(null);
//		try {
//			writer.save(this.g, new File("bpmdsFig2.osntu"));
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
		ostnu.checkStatus.initialized = false;
		try {
			ostnu.agileControllabilityCheck();
		} catch (WellDefinitionException e) {
			throw new RuntimeException(e);
		}
		assertTrue(ostnu.checkStatus.isControllable());
		assertEquals("Negative scenarios", "{}", ostnu.checkStatus.getNegativeScenarios().toString());
	}

	/**
	 * Build a label from the name of a contingent
	 */
	@Test
	public void testGetLabel4ValueInvolvingContingent() {
		Label cNeg = ostnu.getLabel4ValueInvolvingContingent(A, X, false);
		assertEquals("⊡", cNeg.toString());
		final LabeledNode oracle = new LabeledNode("Oracle");
		oracle.setObservable(A.getName().charAt(0));
		ostnu.oracleNode.put(A, oracle);
		cNeg = ostnu.getLabel4ValueInvolvingContingent(A, X, false);
		assertEquals("¬a", cNeg.toString());
	}

	/**
	 * First initAndCheckTest
	 */
	@Test
	public void testInitAndCheck() {
		OSTNUEdgePluggable e = this.g.makeNewEdge("XC", Edge.ConstraintType.requirement);
		e.mergeLabeledValue(Label.emptyLabel, 5);
		this.g.addEdge(e, X, C);
		e = this.g.makeNewEdge("CX", Edge.ConstraintType.requirement);
		e.mergeLabeledValue(Label.emptyLabel, -4);
		this.g.addEdge(e, C, X);

		try {
			ostnu.initAndCheck();
		} catch (WellDefinitionException ex) {
			throw new RuntimeException(ex);
		}

		assertNotNull(CO);
		assertEquals(A, ostnu.activationNode.get(C));
		assertEquals(O, ostnu.oracleNode.get(C));
	}

	/**
	 * First test for isInNegativeScenariosTest
	 */
	@Test
	public void testIsInNegativeScenarios() {
		OSTNUEdgePluggable e = this.g.makeNewEdge("XC", Edge.ConstraintType.requirement);
		e.mergeLabeledValue(Label.emptyLabel, 5);
		this.g.addEdge(e, X, C);
		e = this.g.makeNewEdge("CX", Edge.ConstraintType.requirement);
		e.mergeLabeledValue(Label.emptyLabel, -6);
		this.g.addEdge(e, C, X);

		Label c = Label.parse("C");
		assert c != null;
		ostnu.checkAndManageIfNewLabeledValueIsANegativeLoop(-1, c, X, X, e);
		assertTrue(ostnu.checkStatus.isInNegativeScenarios(c));

		c = Label.parse("¬C");
		assert c != null;
		ostnu.checkAndManageIfNewLabeledValueIsANegativeLoop(-2, c, X, X, e);
		assertTrue(ostnu.checkStatus.isInNegativeScenarios(c));
	}

	/**
	 * First test for Cross Case
	 * <pre>A ---(c:3,⊡)--≥ C ---(B:-3,¬b)---≥ D</pre>
	 * determines
	 * <pre>A---(B:-2,¬b)---≥ D</pre>
	 */
	@Test
	public void testLabeledCrossCaseRule() {
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final OSTNUEdgePluggable cd = g.getEdgeFactory().get("CD");
		final OSTNUEdgePluggable ad = g.getEdgeFactory().get("AD");
		g.addEdge(cd, C, D);
		g.addEdge(ad, A, D);

		cd.mergeUpperCaseValue(Label.parse("¬b"), new ALabel("B", alphabet), -3);

		ostnu.labeledCrossCaseRule(A, C, D, AC, cd, ad);

		final OSTNUEdgePluggable daOk = g.getEdgeFactory().get("AD");
		daOk.mergeUpperCaseValue(Label.parse("¬b"), new ALabel("B", alphabet), -2);

		assertEquals("Cross Case value:", daOk.getUpperCaseValueMap(), ad.getUpperCaseValueMap());
	}

	/**
	 * Test labeledLetterRemovalRule
	 */
	@Test
	public void testLabeledLetterRemovalRule() {
		final LabeledNode B = LabeledNodeSupplier.get("B");
		g.addVertex(B);
		final ALabel aLabel4B = new ALabel(B.getName(), alphabet);
		B.setALabel(aLabel4B);

		final LabeledNode b = LabeledNodeSupplier.get("O_B");
		b.setObservable('B');
		g.addVertex(b);

		final OSTNUEdgePluggable AB = g.getEdgeFactory().get("AB");
		AB.setLowerCaseValue(Label.emptyLabel, aLabel4B, 13);
		AB.setConstraintType(Edge.ConstraintType.contingent);

		final OSTNUEdgePluggable BA = g.getEdgeFactory().get("BA");
		BA.setConstraintType(Edge.ConstraintType.contingent);
		BA.mergeUpperCaseValue(Label.emptyLabel, aLabel4B, -20);

		final OSTNUEdgePluggable ca = g.getEdgeFactory().get("CA");
		ca.mergeUpperCaseValue(Label.parse("¬b"), aLabel4B, -4);
		ca.mergeUpperCaseValue(Label.parse("¬b"), aLabel4B, -3);

		g.addEdge(AB, A, B);
		g.addEdge(BA, B, A);

		try {
			ostnu.initAndCheck();
		} catch (WellDefinitionException e) {
			throw new RuntimeException(e);
		}

		ostnu.labeledLetterRemovalRule(C, A, ca);

		final OSTNUEdgePluggable CAOk = g.getEdgeFactory().get("CA");
		CAOk.mergeLabeledValue(Label.parse("¬b"), -4);

		assertEquals("Label Letter Removal Case:", CAOk.getLabeledValueMap(), ca.getLabeledValueMap());
	}

	/**
	 * First test for Lower Case
	 * <pre>A ---(c:1,⊡)--≥ C ---(-3)---≥ D
	 *                        ≤---13----
	 * </pre>
	 * determines
	 * <pre>A---(-2,¬C)---≥ D</pre>
	 */
	@Test
	public void testLabeledLowerCaseRule() {
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final OSTNUEdgePluggable cd = g.getEdgeFactory().get("CD");
		final OSTNUEdgePluggable dc = g.getEdgeFactory().get("DC");
		final OSTNUEdgePluggable ad = g.getEdgeFactory().get("AD");
		g.addEdge(cd, C, D);
		g.addEdge(dc, D, C);
		g.addEdge(ad, A, D);

		cd.mergeLabeledValue(Label.emptyLabel, -3);
		dc.mergeLabeledValue(Label.emptyLabel, 13);

		ostnu.labeledLowerCaseRule(A, C, D, AC, cd, ad);

		final OSTNUEdgePluggable daOk = g.getEdgeFactory().get("AD");
		daOk.mergeLabeledValue(Label.parse("¬a"), -2);

		assertEquals("Lower Case value:", daOk.getLabeledValueMap(), ad.getLabeledValueMap());
	}

	/**
	 * Second test for Oracle rule
	 * <pre>A ---(c:1,⊡)--≥ C ---(-3)---≥ D
	 *                        ≤---10----
	 * </pre>
	 * determines
	 * <pre>A---(-2,C)---≥ O_C
	 * C---(-3,C)---≥ O_C
	 * D---(0,C)---≥ O_C
	 * A---(7)----≥D
	 * A≤---(9)---D
	 * </pre>
	 */
	@Test
	public void testLabeledOracleRule() {
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final OSTNUEdgePluggable cd = g.getEdgeFactory().get("CD");
		final OSTNUEdgePluggable dc = g.getEdgeFactory().get("DC");
		final OSTNUEdgePluggable ad = g.getEdgeFactory().get("AD");
		g.addEdge(cd, C, D);
		g.addEdge(dc, D, C);
		g.addEdge(ad, A, D);

		cd.mergeLabeledValue(Label.emptyLabel, -3);
		dc.mergeLabeledValue(Label.emptyLabel, 10);

		ostnu.labeledOracleRule(A, C, D, AC, cd, ad);

		final LabeledIntMap ok = (new LabeledIntMapSupplier<>(LabeledIntSimpleMap.class)).get();
		final Label label = Label.parse("a");
		ok.put(label, -2);
		assertEquals("Oracle Case value:", ok, Objects.requireNonNull(this.g.findEdge(A, O)).getLabeledValueMap());
		ok.put(label, -3);
		ok.put(Label.emptyLabel, 0);
		assertEquals("Oracle Case value:", ok, CO.getLabeledValueMap());
		ok.clear();
		ok.put(label, 0);
		assertEquals("Oracle Case value:", ok, Objects.requireNonNull(this.g.findEdge(D, O)).getLabeledValueMap());
		ok.clear();
		ok.put(label, 7);
		assertEquals("Oracle Case value:", ok, Objects.requireNonNull(this.g.findEdge(A, D)).getLabeledValueMap());
		ok.clear();
		ok.put(label, 9);
		assertEquals("Oracle Case value:", ok, Objects.requireNonNull(this.g.findEdge(D, A)).getLabeledValueMap());
	}

	/**
	 * Test for labeledPropagationRule
	 * <pre>A ---(c:1,⊡)--≥ C ---(-3)---≥ D
	 *                        ≤---13----
	 *      A ---------(-2,¬C)----------≥ D
	 * determines
	 *      A  ----(-11, ¬a)--≥C
	 * </pre>
	 */
	@Test
	public void testLabeledPropagationRule() {
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final OSTNUEdgePluggable cd = g.getEdgeFactory().get("CD");
		final OSTNUEdgePluggable dc = g.getEdgeFactory().get("DC");
		final OSTNUEdgePluggable ad = g.getEdgeFactory().get("AD");
		g.addEdge(cd, C, D);
		g.addEdge(dc, D, C);
		g.addEdge(ad, A, D);

		cd.mergeLabeledValue(Label.emptyLabel, -3);
		dc.mergeLabeledValue(Label.emptyLabel, 13);

		ostnu.labeledLowerCaseRule(A, C, D, AC, cd, ad);
		ostnu.labeledPropagationRule(A, D, C, ad, dc, AC);

		final LabeledIntMap ok = (new LabeledIntMapSupplier<>(LabeledIntSimpleMap.class)).get();
		ok.put(Label.emptyLabel, 10);
		ok.put(Label.parse("¬a"), 11);
		assertEquals("Propagation Case value:", ok, AC.getLabeledValueMap());

		final OSTNUEdgePluggable cc = g.getEdgeFactory().get("CC");
		g.addEdge(cc, C, C);
		ostnu.labeledPropagationRule(C, D, C, cd, dc, cc);
		ok.clear();
		assertEquals("Propagation Case value:", ok, cc.getLabeledValueMap());
	}

	/**
	 * First test for Upper Case
	 * <pre>D ---(10,⊡)--≥ C ---(C:-10)---≥ A</pre>
	 * determines
	 * <pre>D---(C:-5,¬C)---≥ A</pre>
	 */
	@Test
	public void testlabeledUpperCaseRule() {
		final LabeledNode D = LabeledNodeSupplier.get("D");
		final OSTNUEdgePluggable dc = g.getEdgeFactory().get("DC");
		final OSTNUEdgePluggable cd = g.getEdgeFactory().get("CD");
		final OSTNUEdgePluggable da = g.getEdgeFactory().get("DA");
		g.addEdge(cd, C, D);
		g.addEdge(dc, D, C);
		g.addEdge(da, D, A);

//		cd.mergeLabeledValue(Label.emptyLabel, 0);
		dc.mergeLabeledValue(Label.emptyLabel, 5);

		ostnu.labeledUpperCaseRule(D, C, A, dc, Objects.requireNonNull(this.g.findEdge(C, A)), da);

		final LabeledALabelIntTreeMap ok = new LabeledALabelIntTreeMap(LabeledIntSimpleMap.class);
		ok.mergeTriple(Label.parse("¬a"), ALabel.parse("C", alphabet), -5);

		assertEquals("Upper Case value:", ok, da.getUpperCaseValueMap());

		dc.removeLabeledValue(Label.emptyLabel);
		dc.mergeLabeledValue(Label.emptyLabel, 11);
		ostnu.labeledUpperCaseRule(D, C, A, dc, Objects.requireNonNull(this.g.findEdge(C, A)), da);
		final LabeledIntMap ok1 = (new LabeledIntMapSupplier<>(LabeledIntSimpleMap.class)).get();
		ok1.put(Label.parse("¬a"), 1);
	}
}
