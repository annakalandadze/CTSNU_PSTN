/**
 *
 */
package it.univr.di.cstnu.graph;

import it.univr.di.cstnu.graph.Edge.ConstraintType;
import it.univr.di.labeledvalue.*;
import it.univr.di.labeledvalue.ALabelAlphabet.ALetter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class CSTNUEdgeTest {
	/**
	 *
	 */
	static Class<? extends CSTNUEdge> edgeClass = CSTNUEdgePluggable.class;

	/**
	 *
	 */
	static EdgeSupplier<CSTNUEdge> edgeFactory = new EdgeSupplier<>(edgeClass);
	/**
	 *
	 */
	ALabelAlphabet alphabet;

	/**
	 *
	 */
	CSTNUEdge e;

	/**
	 * @throws Exception  none
	 */
	@Before
	public void setUp() throws Exception {
		this.e = edgeFactory.get("e");
		this.alphabet = new ALabelAlphabet();
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.graph.CSTNUEdgePluggable#clearLowerCaseValues()}.
	 */
	@Test
	public void testClearLowerCaseValue() {
		this.e.mergeLabeledValue(Label.emptyLabel, 1);
		this.e.setLowerCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), 1);

		assertEquals("❮e; contingent; {(1, ⊡) }; LL: {(A, 1, ⊡) }; ❯", this.e.toString());

		this.e.clearLowerCaseValues();
		assertEquals("❮e; requirement; {(1, ⊡) }; ❯", this.e.toString());
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.graph.CSTNUEdgePluggable#hasSameValues(Edge)}.
	 */
	@Test
	public void testEqualsAllLabeledValues() {
		this.e.mergeLabeledValue(Label.emptyLabel, 1);
		this.e.setLowerCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), 1);
		this.e.mergeUpperCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), -1);

		final CSTNUEdge e1 = edgeFactory.get();
		e1.setName("e1");
		e1.setConstraintType(ConstraintType.contingent);
		e1.mergeLabeledValue(Label.emptyLabel, 1);
		e1.setLowerCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), 1);
		e1.mergeUpperCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), -1);

		assertTrue(this.e.hasSameValues(e1));
	}

	/**
	 */
	@Test
	public void testGetAllUpperCaseAndOrdinaryLabeledValuesSet() {
		this.e.mergeLabeledValue(Label.emptyLabel, 1);
		this.e.mergeUpperCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), 0);
		this.e.setLowerCaseValue(Label.emptyLabel, new ALabel("B", this.alphabet), -1);
		// System.out.println(e.getAllUpperCaseAndLabeledValuesMaps().toString());
		assertEquals("{(◇, 1, ⊡) (A, 0, ⊡) }", this.e.getAllUpperCaseAndLabeledValuesMaps().toString());
	}

	/**
	 */
	@Test
	public final void testGetUpperLabelValueLabelString() {
		this.e.mergeLabeledValue(Label.emptyLabel, 1);
		this.e.mergeUpperCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), 0);
		this.e.mergeUpperCaseValue(Label.emptyLabel, new ALabel("B", this.alphabet), -1);

		assertEquals(0, this.e.getUpperCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet)));
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.graph.CSTNUEdgePluggable#clear()}.
	 */
	@Test
	public void testIsEmptyAndClear() {
		this.e.mergeLabeledValue(Label.emptyLabel, 1);
		this.e.mergeUpperCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), 0);
		assertEquals("❮e; requirement; {(1, ⊡) }; UL: {(A, 0, ⊡) }; ❯", this.e.toString());

		assertFalse(this.e.isEmpty());
		this.e.clear();
		assertEquals("❮e; requirement; ❯", this.e.toString());
		assertTrue(this.e.isEmpty());
		assertFalse(this.e.isContingentEdge());
		this.e.setConstraintType(ConstraintType.contingent);
		assertTrue(this.e.isContingentEdge());
		assertTrue(this.e.isCSTNEdge());
		assertTrue(this.e.isCSTNUEdge());
		assertFalse(this.e.isRequirementEdge());
		this.e.setConstraintType(ConstraintType.requirement);
		assertTrue(this.e.isRequirementEdge());
		assertFalse(this.e.isSTNEdge());
	}

	/**
	 *
	 */
	@Test
	public final void testMergeUpperLabeledValues() {
		final ALabel aleph = new ALabel("C1_E", this.alphabet);
		aleph.conjoin(new ALetter("C3_E"));
		this.e.mergeUpperCaseValue(Label.parse("a¿b"), aleph, -2);
		aleph.conjoin(new ALetter("C2_E"));
		// e.putUpperCaseValueToRemovedList(Label.parse("a¿b"), aleph, -2);
		// e.mergeUpperCaseValue(Label.parse("a¿b"), aleph, -2);

		assertEquals("{(C1_E∙C3_E, -2, a¿b) }", this.e.getUpperCaseValueMap().toString());
	}

	/**
	 *
	 */
	@Test
	public final void testMergeUpperLabeledValues1() {
		this.alphabet.clear();
		LabeledIntMap map = AbstractLabeledIntMap.parse("{(-20, ab) }");
		assertEquals(map.toString(), "{(-20, ab) }");

		map = AbstractLabeledIntMap.parse("{(-20, ab) (-∞, ¿ab) }");
		assertEquals(map.toString(), "{(-20, ab) (-∞, ¿ab) }");

		map = AbstractLabeledIntMap.parse("{(-20, ab) (-∞, ¿ab) (-8, ¬b)  }");
		assertEquals("{(-8, ¬b) (-20, ab) (-∞, ¿ab) }", map.toString());

		map = AbstractLabeledIntMap.parse("{(-20, ab) (-∞, ¿ab) (-8, ¬b) (-17, b) }");
		// assertEquals("{(-17, b) (-8, ¬b) (-20, ab) (-∞, ¿ab) }", map.toString());
		assertEquals("{(-8, ⊡) (-17, b) (-20, ab) (-∞, ¿ab) }", map.toString());

		this.e.setLabeledValueMap(map);
		final LabeledALabelIntTreeMap map1 = LabeledALabelIntTreeMap
				.parse("{(D, -∞, ¿ab) (D, -30, ab) (D, -∞, a¿b) (D, -4, ¿b) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) (F, -19, ¬ab) (F, -∞, ¿ab) }",
				       this.alphabet, null);
		this.e.setUpperCaseValueMap(map1);

		// assertEquals(
		// "❮e; requirement; {(-17, b) (-8, ¬b) (-20, ab) (-∞, ¿ab) }; UL: {(D, -4, ¿b) (D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b)
		// (F, -19, ¬ab) (F, -∞, ¿ab) }; ❯",
		// e.toString());
		assertEquals(
				"❮e; requirement; {(-8, ⊡) (-17, b) (-20, ab) (-∞, ¿ab) }; UL: {(D, -4, ¿b) (D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) (F, -19, ¬ab) (F, -∞, ¿ab) }; ❯",
				this.e.toString());

		final CSTNUEdge e1 = edgeFactory.get(this.e);

		// System.out.println(e);
		// System.out.println(e1);

		assertTrue(this.e.hasSameValues(e1));
	}

	/**
	 *
	 */
	@Test
	public final void testMergeUpperLabeledValues2() {
		this.alphabet.clear();
		this.e.mergeLabeledValue(Label.parse("a"), -20);
		this.e.mergeLabeledValue(Label.parse("¬a"), -20);

		this.e.mergeUpperCaseValue(Label.parse("ab"), ALabel.parse("D", this.alphabet), -30);
		assertEquals("❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) }; ❯", this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("a¿b"), ALabel.parse("D", this.alphabet), Constants.INT_NEG_INFINITE);
		assertEquals("❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) }; ❯", this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("¿ab"), ALabel.parse("D", this.alphabet), Constants.INT_NEG_INFINITE);
		assertEquals("❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) }; ❯", this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("¿b"), ALabel.parse("I", this.alphabet), -9);
		assertEquals("❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) }; ❯", this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("¬b"), ALabel.parse("I", this.alphabet), -30);
		assertEquals("❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -30, ¬b) }; ❯", this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("¬b"), ALabel.parse("I" + ALabel.ALABEL_SEPARATORstring + "D", this.alphabet), -60);
		assertEquals(
				"❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -30, ¬b) (D" + ALabel.ALABEL_SEPARATORstring + "I, -60, ¬b) }; ❯",
				this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("¬b"),
				ALabel.parse("I" + ALabel.ALABEL_SEPARATORstring + "D" + ALabel.ALABEL_SEPARATORstring + "F", this.alphabet),
				-60);
		assertEquals(
				"❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -30, ¬b) (D" + ALabel.ALABEL_SEPARATORstring + "I, -60, ¬b) }; ❯",
				this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("¬ba"),
				ALabel.parse("I" + ALabel.ALABEL_SEPARATORstring + "D" + ALabel.ALABEL_SEPARATORstring + "F", this.alphabet),
				-60);
		assertEquals(
				"❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -30, ¬b) (D" + ALabel.ALABEL_SEPARATORstring + "I, -60, ¬b) }; ❯",
				this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("b"), ALabel.parse("I", this.alphabet), -60);
		// assertEquals(
		// "❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -60, b) (I, -30, ¬b) (D" + ALabel.ALABEL_SEPARATORstring
		// + "I, -60, ¬b) }; ❯",
		// e.toString());
		assertEquals(
				"❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -30, ⊡) (I, -60, b) (D" + ALabel.ALABEL_SEPARATORstring
						+ "I, -60, ¬b) }; ❯",
				this.e.toString());

		this.e.mergeUpperCaseValue(Label.parse("¬b"), ALabel.parse("I", this.alphabet), -60);
		assertEquals(
				"❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -60, ⊡) }; ❯",
				this.e.toString());

	}

	/**
	 *
	 */
	@Test
	public final void testSimlificationUpperLabeledValues() {
		this.alphabet.clear();
		final LabeledALabelIntTreeMap mapUC = LabeledALabelIntTreeMap
				.parse("{(D, -∞, ¿ab) (D, -30, ab) (D, -∞, a¿b) (D, -4, ¿b) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) (F, -19, ¬ab) (F, -∞, ¿ab) }",
				       this.alphabet, null);
		final LabeledIntMap map = AbstractLabeledIntMap.parse("{(0, a) (0, ¬a) }");

		this.e.setLabeledValueMap(map);
		this.e.setUpperCaseValueMap(mapUC);

		assertEquals(
				"❮e; requirement; {(0, ⊡) }; UL: {(D, -4, ¿b) (D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) (F, -19, ¬ab) (F, -∞, ¿ab) }; ❯",
				this.e.toString());

		this.e.mergeLabeledValue(Label.parse("¬ab"), -20);
		assertEquals(
				"❮e; requirement; {(0, ⊡) (-20, ¬ab) }; UL: {(D, -4, ¿b) (D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) (F, -∞, ¿ab) }; ❯",
				this.e.toString());

		this.e.mergeLabeledValue(Label.parse("ab"), -20);

		assertEquals(
				"❮e; requirement; {(0, ⊡) (-20, b) }; UL: {(D, -4, ¿b) (D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -9, ¬b) (I, -11, ¿b) (I, -∞, ¿a¿b) (F, -∞, ¿ab) }; ❯",
				this.e.toString());

		this.e.mergeLabeledValue(Label.parse("¬b"), -20);

		assertEquals(
				"❮e; requirement; {(-20, ⊡) }; UL: {(D, -30, ab) (D, -∞, a¿b) (D, -∞, ¿ab) (I, -∞, ¿a¿b) (F, -∞, ¿ab) }; ❯",
				this.e.toString());

		this.e.mergeLabeledValue(Label.parse("a"), -30);

		assertEquals(
				"❮e; requirement; {(-20, ⊡) (-30, a) }; UL: {(D, -∞, a¿b) (D, -∞, ¿ab) (I, -∞, ¿a¿b) (F, -∞, ¿ab) }; ❯",
				this.e.toString());

	}

	/**
	 * Test method for {@link it.univr.di.cstnu.graph.CSTNUEdge#toString()}.
	 */
	@Test
	public final void testToString() {
		this.e.mergeLabeledValue(Label.emptyLabel, 1);
		assertEquals("❮e; requirement; {(1, ⊡) }; ❯", this.e.toString());
		this.e.mergeUpperCaseValue(Label.emptyLabel, new ALabel("A", this.alphabet), 0);
		assertEquals("❮e; requirement; {(1, ⊡) }; UL: {(A, 0, ⊡) }; ❯", this.e.toString());
	}

}
