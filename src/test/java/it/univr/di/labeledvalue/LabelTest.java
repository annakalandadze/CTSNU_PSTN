/**
 *
 */
package it.univr.di.labeledvalue;

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
@SuppressWarnings("MethodMayBeStatic")
public class LabelTest {

	/**
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testAllComponentsOfBaseGenerator() {
		final Label ab = Label.parse("a¬b");
		final Label[] abV = Label.allComponentsOfBaseGenerator(ab.getPropositions());
		assertArrayEquals(new Label[] { Label.parse("ab"), Label.parse("¬ab"), Label.parse("a¬b"), Label.parse("¬a¬b") }, abV);
	}

	/**
	 *
	 */
	@SuppressWarnings({"static-method", "UnnecessaryLocalVariable"})
	@Test
	public final void cloneEmptyLabel() {
		final Label a = Label.emptyLabel;
		final Label b = a;

		// for(char c='A'; c< 'ù'; c++)
		// System.out.println("Char "+c+" to int:"+Character.hashCode(c));
		assertEquals(a, b);
		assertEquals(0, b.size());

	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#conjunction(it.univr.di.labeledvalue.Literal)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testConjunct() {
		Label ab = Label.parse("a¬b");
		assertEquals("ab.size=" + ab.size(), 2, ab.size());
		ab = ab.conjunction(Literal.valueOf('c'));
		assertEquals(Label.parse("a¬bc"), ab);
		assertEquals("ab.size=" + ab.size(), 3, ab.size());
		ab = ab.conjunction(Literal.valueOf('d', Literal.UNKNOWN));
		assertNull(ab);
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#conjunctionExtended(it.univr.di.labeledvalue.Literal)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testConjunctExtended() {
		Label ab = Label.parse("a¬b");
		ab = ab.conjunctionExtended(Literal.valueOf('b', Literal.UNKNOWN));
		// System.out.println(ab);
		assertEquals(Literal.UNKNOWN, ab.getStateLiteralWithSameName('b'));

		ab = ab.conjunctionExtended(Literal.valueOf('d', Literal.UNKNOWN));
		assertEquals(Literal.UNKNOWN, ab.getStateLiteralWithSameName('d'));
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void compareTo() {
		final Label empty = Label.emptyLabel;
		Label a = Label.valueOf('a', Literal.STRAIGHT);
		assertTrue(empty.compareTo(a) < 0);

		final Label notA = Label.valueOf('a', Literal.NEGATED);
		assertTrue(notA.compareTo(a) > 0);

		Label b = Label.valueOf('b', Literal.NEGATED);
		assertTrue(notA.compareTo(b) < 0);

		a = Label.parse("¬a¬b");
		b = Label.parse("¬d¬e");
		assertTrue(a.compareTo(b) < 0);

		a = Label.parse("¬a¬b");
		b = Label.parse("¬b¬e");
		assertTrue(a.compareTo(b) < 0);

		a = Label.parse("¬a¬b");
		b = Label.parse("¬b");
		assertTrue(a.compareTo(b) > 0);

		a = Label.parse("¬b");
		b = Label.parse("¬bc");
		assertTrue(a.compareTo(b) < 0);

		a = Label.parse("¬b¬c");
		b = Label.parse("¬bc");
		assertTrue(a.compareTo(b) > 0);

		a = Label.parse("¬b¬c");
		b = Label.parse("¬b¬c");
		assertEquals(0, a.compareTo(b));

		a = Label.parse("ac");
		b = Label.parse("ab");
		assertTrue(a.compareTo(b) > 0);

	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#conjunctionExtended(it.univr.di.labeledvalue.Label)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testConjunctionExtended() {
		final Label ab = Label.parse("ab");
		final Label aNb = Label.parse("a¬b");
		// System.out.println(ab.conjunctionExtended(aNb));
		assertTrue(ab.conjunctionExtended(aNb).contains(Literal.valueOf('b', Literal.UNKNOWN)));
		assertFalse(ab.conjunctionExtended(aNb).contains(Literal.valueOf('b', Literal.STRAIGHT)));
		assertFalse(ab.conjunctionExtended(aNb).contains(Literal.valueOf('b', Literal.NEGATED)));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#contains(it.univr.di.labeledvalue.Literal)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testContains() {
		final Label ab = Label.parse("ab");
		final Label aNb = Label.parse("a¬b");
		assertTrue(ab.contains(Literal.valueOf('a')));
		assertTrue(aNb.contains(Literal.parse("¬b")));
		// ¿literals
		assertFalse(ab.contains(Literal.valueOf('a', Literal.UNKNOWN)));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#equals(java.lang.Object)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testEqualsObject() {
		final Label ab = Label.parse("a¬b");
		assertEquals(ab, Label.parse("a¬b"));
	}

	/**
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testGetAllStraight() {
		Label ab = Label.parse("a¬b");
		ab = ab.conjunctionExtended(Literal.valueOf('c', Literal.UNKNOWN));
		final char[] expected = Label.parse("abc").getPropositions();
		final char[] obtained = ab.getPropositions();
		assertArrayEquals(expected, obtained);
	}

	/**
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testGetLiteralWithSameName() {
		Label ab = Label.parse("a¬b");
		ab = ab.conjunctionExtended(Literal.valueOf('c', Literal.UNKNOWN));

		assertEquals(Literal.STRAIGHT, ab.getStateLiteralWithSameName('a'));

		assertEquals(Literal.UNKNOWN, ab.getStateLiteralWithSameName('c'));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#getUniqueDifferentLiteral(it.univr.di.labeledvalue.Label)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testGetUniqueDifferentLiteral() {
		final Label ab = Label.parse("a¬b");

		assertEquals(Literal.parse("a"), ab.getUniqueDifferentLiteral(Label.parse("¬a¬b")));
		assertNull(ab.getUniqueDifferentLiteral(Label.parse("¬ab")));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#isConsistentWith(it.univr.di.labeledvalue.Label)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testIsConsistentWithLabel() {
		Label ab = Label.parse("a¬b");

		assertTrue(ab.isConsistentWith(Label.parse("a¬bc")));
		assertTrue(ab.isConsistentWith(Label.parse("a¬b¬c")));
		assertTrue(ab.isConsistentWith(Label.parse("a¬b¿c")));
		assertTrue(Label.parse("a¬bc").isConsistentWith(ab));
		assertTrue(Label.parse("a¬b¬c").isConsistentWith(ab));
		assertTrue(Label.parse("a¬b¿c").isConsistentWith(ab));

		assertTrue(ab.isConsistentWith(Label.parse("a¬b")));
		assertFalse(ab.isConsistentWith(Label.parse("ab")));
		ab = ab.conjunctionExtended(Literal.valueOf('c', Literal.UNKNOWN));
		assertTrue(ab.isConsistentWith(Label.parse("a¬b")));
		assertFalse(ab.isConsistentWith(Label.parse("¬a¬b")));
		ab = ab.conjunctionExtended(Literal.valueOf('a', Literal.UNKNOWN));
		assertTrue(ab.isConsistentWith(Label.parse("a¬b")));
		assertTrue(ab.isConsistentWith(Label.parse("¬a¬b")));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#isConsistentWith(it.univr.di.labeledvalue.Literal)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testIsConsistentWithLiteral() {
		Label ab = Label.parse("a¬b");

		assertFalse(ab.isConsistentWith(Literal.index('b'), Literal.UNKNOWN));
		assertFalse(ab.isConsistentWith(Literal.index('b'), Literal.STRAIGHT));
		ab = ab.conjunctionExtended(Literal.valueOf('a', Literal.UNKNOWN));
		assertFalse(ab.isConsistentWith(Literal.index('a'), Literal.STRAIGHT));
		assertTrue(ab.isConsistentWith(Literal.index('a'), Literal.UNKNOWN));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#negation()}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testNegation() {
		Label ab = Label.parse("a¬b");
		Literal[] litA = ab.negation();

		assertArrayEquals(litA, new Literal[] { Literal.parse("¬a"), Literal.valueOf('b') });

		ab = ab.conjunctionExtended(Literal.valueOf('c', Literal.UNKNOWN));
		litA = ab.negation();
		assertArrayEquals(litA, new Literal[] { Literal.parse("¬a"), Literal.valueOf('b'), null });

	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#parse(java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testParse() {
		final Label ab = Label.parse("a¬b");

		Label ab1 = Label.parse(" a ¬b");
		assertEquals(ab, ab1);

		ab1 = Label.parse(" a¬ b");
		assertEquals(ab, ab1);

		ab1 = Label.parse(" a! b");
		assertNull(ab1);

		ab1 = Label.parse(" a¿ 		b");
		assertEquals("a¿b", ab1.toString());

		ab1 = Label.parse(Constants.EMPTY_LABELstring);
		assertEquals(0, ab1.size());

	}

	/**
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testRemoveLiteralBoolean() {
		Label abc = Label.parse("a¬bc");

		abc = abc.remove('b');
		assertEquals(Label.parse("ac"), abc);
		assertEquals(2, abc.size());

		abc = abc.remove(Literal.parse("¬a"));
		assertEquals(Label.parse("ac"), abc);
		assertEquals(2, abc.size());

		abc = abc.remove(Literal.parse("a"));
		assertEquals(Label.parse("c"), abc);
		assertEquals(1, abc.size());

		abc = abc.remove(Literal.parse("c"));
		assertEquals(Label.emptyLabel, abc);
		assertEquals(0, abc.size());
	}

	/**
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testRemoveLabel() {
		Label abc = Label.parse("a¬bc");

		abc = abc.remove(Label.parse("b"));
		assertEquals(Label.parse("ac"), abc);
		assertEquals(2, abc.size());

		abc = abc.remove(Label.parse("¬a"));
		assertEquals(Label.parse("c"), abc);
		assertEquals(1, abc.size());

		abc = abc.remove(Label.emptyLabel);
		assertEquals(Label.parse("c"), abc);
		assertEquals(1, abc.size());

		abc = abc.remove(Label.parse("c"));
		assertEquals(Label.emptyLabel, abc);
		assertEquals(0, abc.size());

		abc = abc.remove(Label.parse("c"));
		assertEquals(Label.emptyLabel, abc);
		assertEquals(0, abc.size());
	}

	/*
	 * Test method for {@link it.univr.di.labeledvalue.Label#removeAllLiteralsWithSameName(it.univr.di.labeledvalue.Literal)}.
	 */
	// @SuppressWarnings("static-method")
	// @Test
	// public final void testRemove() {
	// Label abc = Label.parse("ac");
	//
	// abc.remove('c');
	// assertEquals(0, abc.maxIndex);
	//
	// abc = Label.parse("abc");
	// abc.remove('b');
	// assertEquals(2, abc.maxIndex);
	//
	// abc = Label.parse("abc");
	// abc.remove('c');
	// assertEquals(1, abc.maxIndex);
	//
	// }

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#getSubLabelIn(it.univr.di.labeledvalue.Label, boolean, boolean)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testSubLabelIn() {
		final Label abc = Label.parse("a¬bc");
		final Label abcUd = Label.parse("a¬bc¿d");

		Label sub1 = abc.getSubLabelIn(Label.parse("ac"), true, true);
		assertEquals(Label.parse("ac"), sub1);
		assertEquals(2, sub1.size());

		sub1 = abc.getSubLabelIn(Label.parse("ac"), false, true);
		assertEquals(Label.parse("¬b"), sub1);
		assertEquals(1, sub1.size());

		sub1 = abcUd.getSubLabelIn(Label.parse("ab"), true, true);
		assertEquals(Label.parse("a"), sub1);
		assertEquals(1, sub1.size());

		sub1 = abcUd.getSubLabelIn(Label.parse("ab"), true, false);
		assertEquals(Label.parse("a¿b"), sub1);
		assertEquals(2, sub1.size());

		Label result = Label.parse("¬b¿d");// ¬b¿d
		// System.out.println(abc);
		sub1 = abcUd.getSubLabelIn(Label.parse("ac"), false, true);
		assertEquals(result, sub1);
		assertEquals(2, sub1.size());

		result = Label.parse("¬b");
		sub1 = abcUd.getSubLabelIn(Label.parse("¬bd"), true, true);
		assertEquals(result, sub1);
		assertEquals(1, sub1.size());
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#subsumes(it.univr.di.labeledvalue.Label)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testSubsumes() {
		final Label ab = Label.parse("a¬b");
		final Label abc = Label.parse("a¬bc");

		assertTrue(ab.subsumes(ab));
		assertTrue(abc.subsumes(ab));

		// ¿literals
		final Label abUc = Label.parse("a¬b¿c");
		assertEquals("a¬b¿c", abUc.toString());
		assertTrue(abUc.subsumes(Label.parse("a¬bc")));
		assertFalse(Label.parse("a¬bc").subsumes(abUc));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#toString()}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testToString() {
		Label ab = Label.parse("¬b¬a¬d¬c");

		assertEquals("¬a¬b¬c¬d", ab.toString());
		ab = ab.conjunctionExtended(Literal.valueOf('c', Literal.UNKNOWN));
		assertEquals("¬a¬b" + Constants.UNKNOWN + "c¬d", ab.toString());

	}

	/**
	 * Test conjunction of empty label
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void unmodifiable() {
		final Label a = Label.parse("");
		final Label b = Label.emptyLabel;

		assertEquals(0, b.size());

		final Label c = a.conjunction(b);
		assertEquals(0, c.size());

		assertEquals(Label.emptyLabel, a);
		assertEquals(Label.emptyLabel, c);
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void caso20141128() {
		final Label bgp = Label.parse("bgp");
		final Label cp = Label.parse("cp");

		assertFalse(bgp.subsumes(cp));
	}

	/**
	 *
	 */
	@SuppressWarnings({"static-method", "ShiftOutOfRange"})
	public final void longTest() {
		System.out.println("-2: " + Long.toBinaryString(1L << 63));
		System.out.println("0: " + Long.toBinaryString(0));
		System.out.println("1: " + Long.toBinaryString(1));
		System.out.println("-1*2+1: " + Long.toUnsignedString((-1 << 1) + 1, 2));
		System.out.println("0: " + Long.toUnsignedString(0, 2));
		System.out.println("1: " + Long.toUnsignedString(1, 2));
		System.out.println(Long.toString(1 << 62) + ": " + Long.toUnsignedString(1 << 62));
		System.out.println(Long.compareUnsigned(-1, 1 << 60));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#compareTo(it.univr.di.labeledvalue.Label)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testCompareTo() {
		Label aNotB = Label.parse("a¬b");
		final Literal uC = Literal.valueOf('c', Literal.UNKNOWN);
		Label aNotB1 = aNotB;
		assertEquals(0, aNotB.compareTo(aNotB1));

		assert uC != null;
		aNotB = aNotB.conjunctionExtended(uC.getNegated());
		aNotB1 = aNotB1.conjunctionExtended(uC);
		assertTrue(aNotB.compareTo(aNotB1) < 0);
		assertEquals("ab1.size:" + aNotB1.size(), 3, aNotB1.size());

		aNotB1 = aNotB1.remove(uC).conjunctionExtended(uC.getStraight());
		// aNotB=a¬b¬c aNotB1=a¬bc
		assertTrue(aNotB.compareTo(aNotB1) > 0);

		aNotB = aNotB.remove(uC.getNegated()).conjunctionExtended(uC);
		// aNotB=a¬b¿c aNotB1=a¬bc
		assertTrue(aNotB.compareTo(aNotB1) > 0);
	}

	/**
	 * Proposes only some execution time estimates about some class methods.
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 */
	public static void main(final String[] args) {
		final int nTest = 1000;
		final double msNorm = 1.0 / (1000000.0 * nTest);

		final Literal d = Literal.valueOf('d'), z = Literal.valueOf('z');

		final Label empty = Label.emptyLabel;

		System.out.println("Empty: " + empty);
		Label result;
		// System.out.println("Empty: " + result);
		result = Label.valueOf('a', Literal.STRAIGHT);
		System.out.println("a: " + result);
		result = Label.valueOf('b', Literal.NEGATED);
		System.out.println("¬b: " + result);
		result = Label.valueOf('a', Literal.ABSENT);
		System.out.println("Null: " + result);

		Label l1 = Label.parse(Constants.NOT + "abcd");
		Label l2 = Label.parse(Constants.NOT + "aejfsd");
		System.out.println("l1: " + l1 + "\nl2: " + l2);
		long startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			result = l1.conjunction(l2);
		}
		long endTime = System.nanoTime();
		System.out.println(
				"Execution time for a simple conjunction of '" + l1 + "' with '" + l2 + "'='" + result + "' (ms): " + ((endTime - startTime) * msNorm));

		l1 = Label.parse(Constants.NOT + "abcd");
		l2 = Label.parse("a¬d¬cejfs");
		startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			result = l1.conjunctionExtended(l2);
		}
		endTime = System.nanoTime();
		System.out.println(
				"Execution time for an extended conjunction of '" + l1 + "' with '" + l2 + "'='" + result + "' (ms): " + ((endTime - startTime) * msNorm));

		startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			l1.isConsistentWith(l2);
		}
		endTime = System.nanoTime();
		System.out.println("Execution time for checking if two (inconsistent) labels are consistent. Details '" + l1 + "' with '" + l2 + "' (ms): "
				+ ((endTime - startTime) * msNorm));

		startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			l1.isConsistentWith(l1);
		}
		endTime = System.nanoTime();
		System.out.println("Execution time for checking if two (consistent) labels are consistent. Details '" + l1 + "' with '" + l1 + "' (ms): "
				+ ((endTime - startTime) * msNorm));

		startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			l1.contains(d);
		}
		endTime = System.nanoTime();
		System.out.println(
				"Execution time for checking if a literal is present in a label (the literal is the last inserted) (ms): " + ((endTime - startTime) * msNorm));

		startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			l1.contains('d');
		}
		endTime = System.nanoTime();
		System.out.println(
				"Execution time for checking if a literal is present in a label (given the name) (ms): " + ((endTime - startTime) * msNorm));

		startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			l1.contains(z);
		}
		endTime = System.nanoTime();
		System.out.println(
				"Execution time for checking if a literal is present in a label (the literal is not present) (ms): " + ((endTime - startTime) * msNorm));

		startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			l1.contains('d');
		}
		endTime = System.nanoTime();
		System.out.println("Execution time for get the literal in the label with the same proposition letter (the literal is present) (ms): "
				+ ((endTime - startTime) * msNorm));

		startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			l1.contains('z');
		}
		endTime = System.nanoTime();
		System.out.println("Execution time for get the literal in the label with the same proposition letter (the literal is not present) (ms): "
				+ ((endTime - startTime) * msNorm));
	}

	/**
	 * Test method for {@link it.univr.di.labeledvalue.Label#conjunction(it.univr.di.labeledvalue.Label)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testConjunction() {
		final Label ab = Label.parse("ab");
		final Label aNb = Label.parse("a¬b");
		assertNull(ab.conjunction(aNb));

		final Label uC = Label.valueOf('b', Literal.UNKNOWN);
		assertNull(ab.conjunction(uC));

		final Label b = Label.parse("¬b");
		final Label b1 = Label.parse("¬b");
		assertEquals("¬b", b.conjunction(b1).toString());
		assertEquals(1, b.conjunction(b1).size());

		assertNull("Empty label conjunct with an unknown is null", Label.emptyLabel.conjunction(Label.parse("¿p")));
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void comparatorOrder() {
		final ObjectSortedSet<Label> order = new ObjectRBTreeSet<>();

		order.add(Label.valueOf('a', Literal.STRAIGHT));
		order.add(Label.valueOf('a', Literal.NEGATED));
		order.add(Label.valueOf('a', Literal.UNKNOWN));
		order.add(Label.valueOf('b', Literal.STRAIGHT));
		order.add(Label.valueOf('b', Literal.NEGATED));
		order.add(Label.valueOf('b', Literal.UNKNOWN));
		order.add(Label.valueOf('c', Literal.STRAIGHT));
		order.add(Label.valueOf('c', Literal.NEGATED));
		order.add(Label.valueOf('c', Literal.UNKNOWN));
		order.add(Label.parse("ab"));
		order.add(Label.parse("a¬b"));
		order.add(Label.parse("¬ab"));
		order.add(Label.parse("¬a¬b"));
		order.add(Label.parse("ac"));
		order.add(Label.parse("¬ac"));
		// System.out.println("Order: " + order);
		assertEquals("{a, ¬a, ¿a, b, ¬b, ¿b, c, ¬c, ¿c, ab, a¬b, ac, ¬ab, ¬a¬b, ¬ac}", order.toString());
		assertEquals(15, order.size());
	}
}
