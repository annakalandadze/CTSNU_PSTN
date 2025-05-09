/**
 *
 */
package it.univr.di.cstnu.graph;

import it.univr.di.cstnu.graph.Edge.ConstraintType;
import it.univr.di.labeledvalue.ALabelAlphabet.ALetter;
import it.univr.di.labeledvalue.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class STNUEdgeTest {

	/**
	 *
	 */
	static EdgeSupplier<STNUEdgeInt> edgeFactory = new EdgeSupplier<>(STNUEdgeInt.class);

	/**
	 * Simple edge
	 */
	STNUEdge e;

	/**
	 * @throws java.lang.Exception  none
	 */
	@Before
	public void setUp() throws Exception {
		this.e = edgeFactory.get("e");
	}


	/**
	 * test constructor
	 */
	@Test
	public final void testConstructor() {
		this.e = new STNUEdgeInt();
		assertEquals(ConstraintType.requirement, this.e.getConstraintType());
		assertEquals(Constants.INT_NULL, this.e.getLabeledValue());
		this.e = new STNUEdgeInt((Edge) null);
		assertEquals(ConstraintType.requirement, this.e.getConstraintType());
		assertEquals(Constants.INT_NULL, this.e.getLabeledValue());
		this.e.setLabeledValue(new ALetter("A"), -1, true);
		this.e = new STNUEdgeInt(this.e);
		assertEquals(-1, this.e.getLabeledValue());
		assertEquals(ConstraintType.requirement, this.e.getConstraintType());
		this.e.resetLabeledValue();
		assertEquals(ConstraintType.requirement, this.e.getConstraintType());
	}
	/**
	 * Test method for 2 methods.
	 */
	@Test
	public final void testIsEmptyClear() {
		this.e.setValue(1);
		assertFalse(this.e.isEmpty());
		this.e.clear();
		assertTrue(this.e.isEmpty());
		assertEquals("❮e; requirement; ❯", this.e.toString());
		assertTrue(this.e.isEmpty());
		assertFalse(this.e.isContingentEdge());
		this.e.setConstraintType(ConstraintType.contingent);
		assertTrue(this.e.isContingentEdge());
		assertTrue(this.e.isSTNUEdge());
		assertFalse(this.e.isCSTNUEdge());
		assertFalse(this.e.isRequirementEdge());
		this.e.setConstraintType(ConstraintType.requirement);
		assertTrue(this.e.isRequirementEdge());
		assertFalse(this.e.isSTNEdge());
		assertFalse(this.e.isUpperCase());
		assertFalse(this.e.isLowerCase());
	}

	/**
	 */
	@Test
	public final void testLowerCase() {
		this.e.setConstraintType(ConstraintType.contingent);
		this.e.setLabeledValue(new ALetter("Caa"), 0, false);
		assertEquals(0, this.e.getLabeledValue());
		assertEquals("Caa", this.e.getCaseLabel().left().name);
		assertEquals("LC(Caa):0", this.e.getLabeledValueFormatted());
		assertEquals("❮e; contingent; LC(Caa):0❯", this.e.toString());
		assertFalse(this.e.isUpperCase());
		assertTrue(this.e.isLowerCase());
	}

	/**
	 */
	@Test
	public final void testLowerCaseNonValid() {
		final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> this.e.setLabeledValue(new ALetter("Caa"), -1, false));
		assertEquals("A lower-case value cannot be negative. Details: Caa: -1.", ex.getMessage());
	}

	/**
	 */
	@Test
	public final void testUpperCase() {
		this.e.setConstraintType(ConstraintType.contingent);
		this.e.setValue(-1);
		this.e.setLabeledValue(new ALetter("Caa"), 0, true);
		assertEquals("❮e; contingent; -1; UC(Caa):0❯", this.e.toString());
		assertTrue(this.e.isUpperCase());
		assertFalse(this.e.isLowerCase());
	}

	/**
	 */
	@Test
	public final void testParserUpperCase() {
		this.e.setConstraintType(ConstraintType.contingent);
		// this.e.setValue(-1);
		final String uc = "UC(Caa):-1234";
		this.e.setLabeledValue(uc);
		assertEquals("❮e; contingent; UC(Caa):-1234❯", this.e.toString());
		assertTrue(this.e.isUpperCase());
		assertFalse(this.e.isLowerCase());
	}

}
