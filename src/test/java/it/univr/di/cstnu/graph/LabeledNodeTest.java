/**
 *
 */
package it.univr.di.cstnu.graph;

import org.junit.Before;
import org.junit.Test;

import it.univr.di.labeledvalue.ALabelAlphabet;
import it.univr.di.labeledvalue.Label;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class LabeledNodeTest {

	/**
	 *
	 */
	static LabeledNodeSupplier nodeFactory = new LabeledNodeSupplier();

	/**
	 *
	 */
	ALabelAlphabet alphabet;

	/**
	 *
	 */
	LabeledNode a;

	/**
	 * @throws java.lang.Exception  none
	 */
	@Before
	public void setUp() throws Exception {
//		this.alphabet = new ALabelAlphabet();
		this.a = LabeledNodeSupplier.get("A");
		this.a.setLabel(Label.emptyLabel);
	}

	/**
	 */
	@Test
	public final void testEquals() {
		final LabeledNode aa = LabeledNodeSupplier.get("A");
		aa.setLabel(Label.emptyLabel);

		assertTrue(this.a.equalsByName(aa));
		assertNotEquals(this.a, aa);
		assertNotSame(this.a, aa);
	}

	/**
	 */
	@Test
	public final void potentialPut1() {
		this.a.putLabeledPotential(Label.parse("b"), -1);
		this.a.putLabeledPotential(Label.parse("b"), -1);// ignored
		this.a.putLabeledPotential(Label.parse("a"), -1);

		assertEquals("{b->-1, a->-1}", this.a.getLabeledPotential().entrySet().toString());

		this.a.putLabeledPotential(Label.parse("¬b"), -1);
		assertEquals("{⊡->-1}", this.a.getLabeledPotential().entrySet().toString());
	}

}
