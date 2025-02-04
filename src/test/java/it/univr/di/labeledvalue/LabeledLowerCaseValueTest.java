package it.univr.di.labeledvalue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author posenato
 */
public class LabeledLowerCaseValueTest {

	/**
	 *
	 */
	ALabelAlphabet alpha;

	/**
	 *
	 */
	@Test
	public final void empty() {
		final LabeledLowerCaseValue empty = LabeledLowerCaseValue.emptyLabeledLowerCaseValue;
		final LabeledLowerCaseValue empty1 = LabeledLowerCaseValue.parse("{}", alpha);
		final LabeledLowerCaseValue lowerValue = LabeledLowerCaseValue.parse("{(¬a, N9, -12) }", alpha);

		Assert.assertEquals("Equals of two empties", empty, empty1);
		Assert.assertNotEquals("Equals of one empty and one not empty", empty, lowerValue);
		Assert.assertEquals("Hash code of empty is 0", 0, empty.hashCode());
		Assert.assertEquals("String of empty is {}", "{}", empty.toString());
	}

	/**
	 *
	 */
	@SuppressWarnings("EqualsWithItself")
	@Test
	public final void parse() {
		LabeledLowerCaseValue lowerValue = LabeledLowerCaseValue.parse("{(¬a, N9, -12) }", alpha);
		Assert.assertEquals(LabeledLowerCaseValue.create(new ALabel("N9", alpha), -12, Label.parse("¬a")), lowerValue);

		lowerValue = LabeledLowerCaseValue.parse("{ }", alpha);
		Assert.assertEquals(LabeledLowerCaseValue.emptyLabeledLowerCaseValue, lowerValue);

		Assert.assertNotEquals(LabeledLowerCaseValue.emptyLabeledLowerCaseValue, null);
		Assert.assertEquals(LabeledLowerCaseValue.emptyLabeledLowerCaseValue, LabeledLowerCaseValue.emptyLabeledLowerCaseValue);

		Assert.assertNotEquals(null, LabeledLowerCaseValue.emptyLabeledLowerCaseValue);
		Assert.assertEquals(LabeledLowerCaseValue.emptyLabeledLowerCaseValue, LabeledLowerCaseValue.parse("{ }", alpha));
		Assert.assertEquals(LabeledLowerCaseValue.emptyLabeledLowerCaseValue, LabeledLowerCaseValue.emptyLabeledLowerCaseValue);

	}

	/**
	 * @throws java.lang.Exception  none
	 */
	@Before
	public void setUp() throws Exception {
		alpha = new ALabelAlphabet();
	}

}
