/**
 *
 */
package it.univr.di.labeledvalue;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
@SuppressWarnings("ForLoopReplaceableByWhile")
public class LabeledIntTreeMapTest {

	@SuppressWarnings("unused")
	static private final Logger LOG = Logger.getLogger("LabeledIntTreeMapTest");

	/**
	 * stub class just to have a different implementation of the interface.
	 *
	 * @author posenato
	 */
	private static final class LabeledIntMapStub implements LabeledIntMap {

		@Serial
		private static final long serialVersionUID = 1L;
		/**
		 *
		 */
		Object2IntRBTreeMap<Label> map;

		/**
		 *
		 */
		private LabeledIntMapStub() {
			map = new Object2IntRBTreeMap<>();
		}

		@Override
		public boolean alreadyRepresents(Label newLabel, int newValue) {
			return false;
		}

		@Override
		public void clear() {
			map.clear();
		}

		@Override
		public ObjectSet<Entry<Label>> entrySet(@Nonnull ObjectSet<Entry<Label>> setToReuse) {
			return null;
		}

		@Override
		public ObjectSet<Entry<Label>> entrySet() {
			return map.object2IntEntrySet();
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof final LabeledIntMap lvm)) {return false;}
			return entrySet().equals(lvm.entrySet());
		}

		@Override
		public int get(Label l) {
			return 0;
		}

		@Override
		public int getMaxValue() {
			return 0;
		}

		@Override
		public int getMinValue() {
			return 0;
		}

		@Override
		public int getMinValueAmongLabelsWOUnknown() {
			return 0;
		}

		@Override
		public int getMinValueConsistentWith(Label l) {
			return 0;
		}

		@Override
		public int getMinValueSubsumedBy(Label l) {
			return 0;
		}

		@Override
		public int hashCode() {
			return map.hashCode();
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public ObjectSet<Label> keySet() {
			return null;// this.map.keySet();
		}

		@Override
		public ObjectSet<Label> keySet(ObjectSet<Label> setToReuse) {
			return null;
		}

		@Override
		public LabeledIntMap newInstance() {
			return null;
		}

		/**
		 * Factory
		 *
		 * @param lim      an object to clone.
		 * @param optimize true for having the label shortest as possible, false otherwise. For example, the set {(0, ¬C), (1, C)} is represented as {(0, ⊡),
		 *                 (1, C)} if this parameter is true.
		 *
		 * @return an object of type LabeledIntMap.
		 */
		@Override
		public LabeledIntMap newInstance(LabeledIntMap lim, boolean optimize) {
			return null;
		}

		@Override
		public LabeledIntMap newInstance(LabeledIntMap lim) {
			return null;
		}

		/**
		 * Factory
		 *
		 * @param optimize true for having the label shortest as possible, false otherwise. For example, the set {(0, ¬C), (1, C)} is represented as {(0, ⊡),
		 *                 (1, C)} if this parameter is true.
		 *
		 * @return an object of type LabeledIntMap.
		 */
		@Override
		public LabeledIntMap newInstance(boolean optimize) {
			return null;
		}

		@Override
		public boolean put(Label l, int i) {
			map.put(l, i);
			return false;
		}

		@Override
		public void putAll(LabeledIntMap inputMap) {
			// not implemented
		}

		/**
		 * Put the labeled value without any control. It is dangerous, but it can help in some cases.
		 *
		 * @param l a {@link Label} object.
		 * @param i the new value.
		 */
		@Override
		public void putForcibly(Label l, int i) {

		}

		@Override
		public int remove(Label l) {
			return 0;
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("{");
			for (final Entry<Label> e : entrySet()) {// I wanted a sorted print!
				sb.append("(");
				sb.append(e.getKey().toString());
				sb.append(", ");
				final int value = e.getIntValue();
				if ((value == Constants.INT_NEG_INFINITE) || (value == Constants.INT_POS_INFINITE)) {
					if (value < 0) {
						sb.append('-');
					}
					sb.append(Constants.INFINITY_SYMBOL);
				} else {
					sb.append(value);
				}
				sb.append(") ");
			}
			sb.append("}");
			return sb.toString();
		}

		@Override
		public LabeledIntMapView unmodifiable() {
			return null;
		}

		@Override
		public IntSet values() {
			return null;
		}
	}

	/**
	 * Main.
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 */
	static public void main(final String[] args) {
		testRetriveAllElements();
		testMergeSomeValuesAndRetrieveSpecificOnes();
	}

	@SuppressWarnings({"ResultOfMethodCallIgnored", "SameParameterValue"})
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT", justification = "It is a velocity test")
	private static void determineExecutionTimeForRetrievingAllElements(LabeledIntTreeMap map, int nTest, SummaryStatistics entrySetStats,
	                                                                   SummaryStatistics keySetStats) {
		entrySetStats.clear();
		keySetStats.clear();
		Instant startInstant;

		for (int i = 0; i < nTest; i++) {
			startInstant = Instant.now();
			for (final Entry<Label> entry : map.entrySet()) {
				entry.getKey();
				entry.getIntValue();
			}
			final Instant endInstant = Instant.now();
			entrySetStats.addValue(Duration.between(startInstant, endInstant).toNanos() / 10E6);
		}

		for (int i = 0; i < nTest; i++) {
			startInstant = Instant.now();
			for (final Label l : map.keySet()) {
				map.get(l);
			}
			final Instant endInstant = Instant.now();
			keySetStats.addValue(Duration.between(startInstant, endInstant).toNanos() / 10E6);
		}

	}

	/**
	 * Simple test to verify time cost of constructor.
	 */
	@SuppressWarnings({"SingleStatementInBlock", "UnusedAssignment"})
	private static void testMergeSomeValuesAndRetrieveSpecificOnes() {

		final int nTest = (int) 1E3;
		final double msNorm = 1.0E6 * nTest;

		final LabeledIntMap map = new LabeledIntTreeMap();

		final Label l1 = Label.parse("abc¬f");
		final Label l2 = Label.parse("abcdef");
		final Label l3 = Label.parse("a¬bc¬de¬f");
		final Label l4 = Label.parse("¬b¬d¬f");
		final Label l5 = Label.parse("ec");
		final Label l6 = Label.parse("¬fedcba");
		final Label l7 = Label.parse("ae¬f");
		final Label l8 = Label.parse("¬af¿b");
		final Label l9 = Label.parse("¬af¿b");
		final Label l10 = Label.parse("¬ec");
		final Label l11 = Label.parse("abd¿f");
		final Label l12 = Label.parse("a¿d¬f");
		final Label l13 = Label.parse("¬b¿d¿f");
		final Label l14 = Label.parse("b¬df¿e");
		final Label l15 = Label.parse("e¬c");
		final Label l16 = Label.parse("ab¿d¿f");
		final Label l17 = Label.parse("ad¬f");
		final Label l18 = Label.parse("b¿d¿f");
		final Label l19 = Label.parse("¬b¬df¿e");
		final Label l20 = Label.parse("¬e¬c");

		final Label ll1 = Label.parse("gabc¬f");
		final Label ll2 = Label.parse("gabcdef");
		final Label ll3 = Label.parse("ga¬bc¬de¬f");
		final Label ll4 = Label.parse("g¬b¬d¬f");
		final Label ll5 = Label.parse("gec");
		final Label ll6 = Label.parse("g¬fedcba");
		final Label ll7 = Label.parse("gae¬f");
		final Label ll8 = Label.parse("g¬af¿b");
		final Label ll9 = Label.parse("g¬af¿b");
		final Label ll0 = Label.parse("g¬ec");
		final Label ll21 = Label.parse("gabd¿f");
		final Label ll22 = Label.parse("ga¿d¬f");
		final Label ll23 = Label.parse("g¬b¿d¿f");
		final Label ll24 = Label.parse("gb¬df¿e");
		final Label ll25 = Label.parse("ge¬c");
		final Label ll26 = Label.parse("gab¿d¿f");
		final Label ll27 = Label.parse("gad¬f");
		final Label ll28 = Label.parse("gb¿d¿f");
		final Label ll29 = Label.parse("g¬b¬df¿e");
		final Label ll20 = Label.parse("g¬e¬c");

		long startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			map.clear();
			map.put(Label.emptyLabel, 109);
			map.put(l1, 10);
			map.put(l2, 20);
			map.put(l3, 25);
			map.put(l4, 23);
			map.put(l5, 22);
			map.put(l6, 23);
			map.put(l7, 20);
			map.put(l8, 20);
			map.put(l9, 21);
			map.put(l10, 11);
			map.put(l11, 11);
			map.put(l12, 11);
			map.put(l13, 24);
			map.put(l14, 22);
			map.put(l15, 23);
			map.put(l16, 20);
			map.put(l17, 23);
			map.put(l18, 23);
			map.put(l19, 23);
			map.put(l20, 23);
			map.put(ll1, 10);
			map.put(ll2, 20);
			map.put(ll3, 25);
			map.put(ll4, 23);
			map.put(ll5, 22);
			map.put(ll6, 23);
			map.put(ll7, 20);
			map.put(ll8, 20);
			map.put(ll9, 21);
			map.put(ll0, 11);
			map.put(ll21, 11);
			map.put(ll22, 11);
			map.put(ll23, 24);
			map.put(ll24, 22);
			map.put(ll25, 23);
			map.put(ll26, 20);
			map.put(ll27, 23);
			map.put(ll28, 23);
			map.put(ll29, 23);
			map.put(ll20, 23);

		}
		long endTime = System.nanoTime();
		System.out.println("Labeled-value set managed as a tree.\nExecution time for some merge operations (mean over " + nTest + " tests).\nFirst map: " + map
		                   + ".\nTime: (ms): "
		                   + ((endTime - startTime) / msNorm));
		final String rightAnswer = "{(⊡, 23) (c, 22) (c¬e, 11) (a¿d¬f, 11) (ae¬f, 20) (¬a¿bf, 20) (abc¬f, 10) (abd¿f, 11) (b¬d¿ef, 22) (abcdef, 20)}";
		System.out.println("The right final set is " + rightAnswer + ".");
		System.out.println("Is equal? " + AbstractLabeledIntMap.parse(rightAnswer).equals(map));

		startTime = System.nanoTime();
		int min = 1000;
		for (int i = 0; i < nTest; i++) {
			min = map.getMinValue();
		}
		endTime = System.nanoTime();
		System.out.println("Execution time for determining the min value (" + min + ") (mean over " + nTest + " tests). (ms): "
		                   + ((endTime - startTime) / msNorm));

		startTime = System.nanoTime();
		final Label l = Label.parse("abd¿f");
		for (int i = 0; i < nTest; i++) {
			min = map.get(l);
		}
		endTime = System.nanoTime();
		System.out.println("Execution time for retrieving value of label " + l + " (mean over " + nTest + " tests). (ms): "
		                   + ((endTime - startTime) / msNorm));

		startTime = System.nanoTime();
		map.put(Label.parse("c"), 11);
		map.put(Label.parse("¬c"), 11);
		endTime = System.nanoTime();
		System.out.println("After the insertion of (c,11) and (¬c,11) the map becomes: " + map);
		System.out.println("Execution time for simplification (ms): "
		                   + ((endTime - startTime) / 1.0E6));
	}

	/**
	 * Simple test to determine the time cost of retrieving all elements of a labeled int tree map using entrySet() vs. keySet(). On 2017-10-26 it resulted that
	 * there is no appreciable difference between accessing to the map using entrySet() &amp; its get() and accessing to the map using keySet() and, then,
	 * find().
	 */
	@SuppressWarnings({"ForLoopReplaceableByForEach", "StringConcatenationMissingWhitespace"})
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE", justification = "The bug is not present")
	private static void testRetriveAllElements() {
		final int nTest = (int) 1E2;
		final LabeledIntTreeMap map = new LabeledIntTreeMap();
		final SummaryStatistics entrySetStats = new SummaryStatistics();
		final SummaryStatistics keySetStats = new SummaryStatistics();

		final Random rnd = new Random();
		final int[] nOfLabels = {5, 10, 25, 50, 100, 1000};

		//noinspection ArrayLengthInLoopCondition
		for (int k = 0; k < nOfLabels.length; k++) {
			final int nLabel = nOfLabels[k];
			for (int i = 0; i < nLabel; i++) {//build a random label containing 'z'-'a'+1 literals.
				Label label = Label.emptyLabel;
				for (char j = 'a'; j <= 'z'; j++) {
					final char strOrNeg = rnd.nextBoolean() ? Literal.STRAIGHT : Literal.NEGATED;
					assert label != null;
					label = label.conjunction(j, strOrNeg);
				}
//				LOG.info("("+label+", "+i+") ");
				map.put(label, i);
			}
			// System.out.println("Map size: " + map);
			determineExecutionTimeForRetrievingAllElements(map, nTest, entrySetStats, keySetStats);
			System.out.println("Time to retrieve " + map.size() + " elements using entrySet(): " + entrySetStats.getMean() + "ms");
			System.out.println("Time to retrieve " + map.size() + " elements using keySet(): " + keySetStats.getMean() + "ms");
			System.out.println("The difference is " + (entrySetStats.getMean() - keySetStats.getMean()) + " ms. It is better to use: "
			                   + ((entrySetStats.getMean() < keySetStats.getMean()) ? "entrySet()" : "keySet()") + " approach.\n");
		}
	}
	/**
	 *
	 */
	LabeledIntMapSupplier<LabeledIntTreeMap> factory;
	/**
	 *
	 */
	LabeledIntMap actual;
	/**
	 *
	 */
	LabeledIntMap actual1;

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void caso20141021() {
		actual.clear();
		actual.put(Label.parse("¬b"), 29);
		actual.put(Label.parse("a"), 26);
		actual.put(Label.parse("ab"), 24);
		actual.put(Label.emptyLabel, 30);
		actual.put(Label.parse("¬ab"), 28);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.parse("¬ab"), 23);
		actual.put(Label.parse("a¬b"), 24);

		expected.clear();
		// expected.put(Label.emptyLabel, 26);
		// expected.put(Label.parse("¬a"), 25);
		// expected.put(Label.parse("¬ab"), 23);
		// expected.put(Label.parse("a¬b"), 24);
		// expected.put(Label.parse("b"), 24);
		expected.put(Label.parse("ab"), 24);
		expected.put(Label.parse("¬ab"), 23);
		expected.put(Label.parse("a¬b"), 24);
		expected.put(Label.parse("a"), 26);
		expected.put(Label.parse("¬a"), 25);

		// System.out.println("map:"+map);
		// System.out.println("result:"+result);
		assertEquals("Rimuovo componenti maggiori con elementi consistenti della base:\n", expected, actual);
	}

	/**
	 *
	 */
	@Test
	public final void caso20141128() {
		actual.clear();
		actual.put(Label.parse("bgp"), 10);
		actual.put(Label.parse("cp"), -1);
		// System.out.println(map);

		assertEquals("{(-1, cp) (10, bgp) }", actual.toString());
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void casoBaseConValoriDistinti() {
		actual.clear();
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.emptyLabel, 31);
		actual.put(Label.parse("¬ab"), 24);
		actual.put(Label.parse("¬a¬b"), 30);
		actual.put(Label.parse("ab"), 23);

		expected.clear();
		expected.put(Label.parse("a"), 30);
		expected.put(Label.parse("¬a"), 25);
		expected.put(Label.parse("ab"), 23);
		expected.put(Label.parse("¬ab"), 24);
		expected.put(Label.emptyLabel, 31);

		// System.out.printf("map:"+map);
		assertEquals("Base con valori differenti:\n", expected, actual);
	}

	/**
	 * Check the kind of bundle result returned.
	 */
	@SuppressWarnings("ForLoopReplaceableByForEach")
	@Test
	public final void checkIntSet() {
		actual.clear();
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.emptyLabel, 31);
		actual.put(Label.parse("¬ab"), 24);
		actual.put(Label.parse("¬a¬b"), 30);
		actual.put(Label.parse("ab"), 23);

		final ObjectArraySet<Entry<Label>> set = (ObjectArraySet<Entry<Label>>) actual.entrySet();
		// System.out.println(set);
		try {
			for (final Iterator<Entry<Label>> ite = set.iterator(); ite.hasNext(); ) {
				final Entry<Label> e = ite.next();
				e.setValue(1);
			}
		} catch (java.lang.UnsupportedOperationException e) {
			assertNotNull(e);
			return;
		}
		assertEquals(set, actual.entrySet());
		// System.out.println("Map: "+map);
		// System.out.println("Set: "+set);
	}
	/**
	 *
	 */
	LabeledIntMap expected;

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void differentValuesWithBase() {
		actual.clear();
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("b"), 2);
		actual.put(Label.parse("c"), 25);
		actual.put(Label.parse("¬ae"), 4);
		actual.put(Label.parse("¿ac"), 4);
		actual.put(Label.parse("d"), 31);

		assertEquals("{(30, ⊡) (25, ¬a) (2, b) (25, c) (4, ¬ae) (4, ¿ac) }", actual.toString());
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void distruzioneBase() {
		actual.clear();
		actual.put(Label.emptyLabel, 100);
		actual.put(Label.parse("ab"), 50);
		actual.put(Label.parse("a¬b"), 59);
		actual.put(Label.parse("¬ab"), 60);
		actual.put(Label.parse("¬a¬b"), 69);
		actual.put(Label.parse("¬a"), 30);
		actual.put(Label.parse("a"), 30);

		expected.clear();
		expected.put(Label.emptyLabel, 30);
		assertEquals("Test di distruzione base:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void distruzioneBase2() {
		actual.clear();
		actual.put(Label.parse("b"), -12);
		actual.put(Label.parse("¬b"), -1);
		actual.put(Label.parse("¬a"), -10);

		actual.put(Label.emptyLabel, -10);

		expected.clear();
		expected.put(Label.emptyLabel, -10);
		expected.put(Label.parse("b"), -12);
		assertEquals("Test di distruzione base due:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void eliminazione() {
		final Label l1 = Label.parse("¬a¬b");
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(l1, 21);

		actual.remove(l1);
		actual.remove(l1);
		actual.remove(Label.emptyLabel);
		actual.remove(Label.parse("a"));

		expected.clear();

		assertEquals("Test cancellazione ripetuta, di empty label e di una label inesistente:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void equals() {
		actual.clear();
		actual.put(Label.parse("¬a¬b"), 30);
		actual.put(Label.parse("¬ab"), 24);
		actual.put(Label.parse("ab"), 23);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.parse("a"), 30);
		actual.put(Label.emptyLabel, 31);

		expected = new LabeledIntMapStub();
		// in case of algorithm simplifies always two labels that differ for only one opposite literal, then the expected is ⊡, 30, ¬a,25 b,24 ab,23
		// in case of algorithm simplifies two labels that differ for only one opposite literal only when they have same value, then the expected is ¬a,25 ¬ab,
		// `24 a`, `30 ab`, `23`
		expected.put(Label.emptyLabel, 30);
		// expected.put(Label.parse("a"), 30);
		expected.put(Label.parse("¬a"), 25);
		expected.put(Label.parse("ab"), 23);
		expected.put(Label.parse("b"), 24);
		// expected.put(Label.parse("¬ab"), 24);

		// assertEquals("Base con valori differenti: ", "{(30, a) (25, ¬a) (23, ab) (24, ¬ab) }", this.actual.toString());
		assertEquals("Base con valori differenti: ", "{(30, ⊡) (25, ¬a) (24, b) (23, ab) }", actual.toString());
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void equals1() {
		actual.clear();
		actual.put(Label.parse("¬a¬b"), 30);
		actual.put(Label.parse("¬ab"), 24);
		actual.put(Label.parse("ab"), 23);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("c"), 32);
		actual.put(Label.emptyLabel, 31);

		assertEquals("{(30, ⊡) (25, ¬a) (24, b) (23, ab) }", actual.toString());
		// assertEquals("{(30, ⊡) (25, ¬a) (23, ab) (24, ¬ab) }", this.actual.toString());
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void insert0() {
		actual.clear();
		actual.put(Label.emptyLabel, 0);

		expected.clear();
		expected.put(Label.emptyLabel, 0);
		assertEquals("Test su aggiunta di (empty,0):\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void minValue() {
		actual.clear();
		actual.put(Label.parse("a"), 0);
		actual.put(Label.parse("¬a"), 1);

		// System.out.println(map.toString());
		assertEquals("Test min value", 0, actual.getMinValue());
		actual1.clear();

		assertEquals(Constants.INT_NULL, actual1.getMinValue());

		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("ab"), 10);
		actual.put(Label.parse("a"), Constants.INT_POS_INFINITE);
		actual.put(Label.parse("¬b"), 23);
		actual.put(Label.parse("b"), 22);
		actual.put(Label.parse("a¬b"), Constants.INT_NULL);

		assertEquals("Min value: ", 10, actual.getMinValue());

		actual.put(Label.parse("ab"), Constants.INT_NEG_INFINITE);

		assertEquals("Min value: ", Constants.INT_NEG_INFINITE, actual.getMinValue());
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void minValueConsistentWithTest() {
		actual.clear();
		actual.put(Label.parse("a"), 0);
		actual.put(Label.parse("¬a"), 1);

		assertEquals(1, actual.getMinValueConsistentWith(Label.parse("¬a")));
	}

	/**
	 *
	 */
	@Test
	public final void alreadyRepresents() {
		expected.clear();
		expected.put(Label.emptyLabel, 23);
		expected.put(Label.parse("ab"), 20);

		assertTrue(expected.alreadyRepresents(Label.emptyLabel, 24));
		assertTrue(expected.alreadyRepresents(Label.parse("abc"), 20));
		assertFalse(expected.alreadyRepresents(Label.emptyLabel, 21));
		assertFalse(expected.alreadyRepresents(Label.parse("a"), 20));
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void cicloConCancellazione() {
		actual.clear();
		actual.put(Label.parse("a"), 20);
		actual.put(Label.parse("¬b"), 21);
		actual.put(Label.parse("ab"), 10);
		actual.put(Label.parse("a¬b"), 15);
		actual.put(Label.parse("¬ab"), 12);

		LabeledIntTreeMap copy = new LabeledIntTreeMap(actual);

		assertEquals("{(21, ⊡) (15, a) (12, b) (10, ab) }", copy.toString());

		final ObjectSet<Label> keys = copy.keySet();
		// assertEquals("[a, ¬b, ab, a¬b, ¬ab]", Arrays.toString(keys.toArray()));
		assertEquals("[a, b, ab, ⊡]", Arrays.toString(keys.toArray()));

		for (final Label l : copy.keySet()) {
			if ("¬b".equals(l.toString()) || "ab".equals(l.toString())) {
				copy.remove(l);
			}
		}
		// assertEquals("[a, ¬b, ab, a¬b, ¬ab]", Arrays.toString(keys.toArray()));
		assertEquals("[a, b, ab, ⊡]", Arrays.toString(keys.toArray()));
		// assertEquals("{(20, a) (15, a¬b) (12, ¬ab) }", copy.toString());
		assertEquals("{(21, ⊡) (15, a) (12, b) }", copy.toString());

		copy = new LabeledIntTreeMap(actual);

		final StringBuilder sb = new StringBuilder(30);
		for (final Entry<Label> e : copy.entrySet()) {
			if ("¬b".equals(e.getKey().toString()) || "ab".equals(e.getKey().toString())) {
				copy.put(e.getKey(), 15);
			}
			if ("¬b".equals(e.getKey().toString()) || "ab".equals(e.getKey().toString())) {
				sb.append(e.getIntValue()).append(", ");
			}
		}
		// assertEquals("21, 10, ", sb.toString());
		assertEquals("10, ", sb.toString());
		// assertEquals("{(20, a) (15, ¬b) (10, ab) (12, ¬ab) }", copy.toString());
		assertEquals("{(21, ⊡) (15, a) (12, b) (10, ab) }", copy.toString());
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@SuppressWarnings({"ConvertToBasicLatin", "UnnecessaryUnicodeEscape"})
	@Test
	public final void parseTest() {

		assertEquals("patternLabelCharsRE",
		             "\\Q{\\s*\\E(\\Q(\\E((((\u00AC|\u00BF|)[a-zA-F])+|\u22A1)\\s*,\\s*([+\\-])?(∞|[0-9]+)|([+\\-])?(∞|[0-9]+)\\s*,\\s*(((\u00AC|\u00BF|)[a-zA-F])+|\u22A1))\\Q)\\E\\s*)*\\Q}\\E",
		             AbstractLabeledIntMap.labeledValueSetREPattern.toString());
		actual = AbstractLabeledIntMap.parse("{(-30, a) (+25, ¬a) (30, b) (" + Constants.INT_NEG_INFINITE + ", ¬b)}");

		expected.clear();
		expected.put(Label.parse("a"), -30);
		expected.put(Label.parse("¬a"), 25);
		expected.put(Label.parse("b"), 30);
		expected.put(Label.parse("¬b"), Constants.INT_NEG_INFINITE);
		assertEquals("Parse test", expected, actual);


		actual = AbstractLabeledIntMap.parse("{(-30, a) (+25, ¬a) (30, b) (" + Constants.INT_NEG_INFINITE + ",       ¬b)}");
		assertEquals("Parse test", expected, actual);

		actual = AbstractLabeledIntMap.parse("{(-30,a)(+25,¬a)(30,b)(" + Constants.INT_NEG_INFINITE + ",¬b)}");
		assertEquals("Parse test", expected, actual);

		actual = AbstractLabeledIntMap.parse("{(-30, a) (+25, ¬a)          	 (30, b) (" + Constants.INT_NEG_INFINITE + ",       ¬b)}");
		assertEquals("Parse test", expected, actual);

		actual = AbstractLabeledIntMap.parse("{       (-30, a) (+25, ¬a)          	 (30, b) (" + Constants.INT_NEG_INFINITE + ",       ¬b)}");
		assertEquals("Parse test", expected, actual);

		actual = AbstractLabeledIntMap.parse("{       (-30, a) (+25, ¬a)          	 (30, b) (" + Constants.INT_NEG_INFINITE + ",       ¬b)       }");
		assertEquals("Parse test", expected, actual);


	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void removeAllValuesGreaterThan() {
		actual.clear();
		actual.put(Label.emptyLabel, 100);
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("¬ab"), 25);
		actual.put(Label.parse("¬a¬b¬c"), 20);
		actual.put(Label.emptyLabel, 0);

		expected = new LabeledIntTreeMap();
		expected.put(Label.emptyLabel, 0);

		assertEquals("removeAllValuesGreaterThan: ", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazione0Test() {
		final Label b = Label.parse("¬b");
		actual.clear();
		actual.put(Label.parse("a"), -1);
		actual.put(Label.parse("b"), -1);
		actual.put(b, -1);

		expected.clear();
		expected.put(Label.emptyLabel, -1);

		assertEquals(expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazione1Test() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("¬a¬b"), 21);
		actual.put(Label.parse("¬ab"), 21);

		expected.clear();
		expected.put(Label.emptyLabel, 109);
		expected.put(Label.parse("¬a"), 21);

		assertEquals("Test inserimento due label di pari valore con un letterale opposto:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazione20230605Test() {
		final Label ab = Label.parse("¬a¬b");
		final Label abc = Label.parse("¬a¬bc");
		final Label abnc = Label.parse("¬a¬b¬c");
		actual.clear();
		actual.put(ab, 704);
		actual.put(abc, 682);
		actual.put(abnc, 687);

		expected.clear();
		expected.put(ab, 687);
		expected.put(abc, 682);

		assertEquals(expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazione20230606Test() {
		final Label a = Label.parse("¬a");
		final Label c = Label.parse("c");
		final Label nc = Label.parse("¬c");
		final Label uc = Label.parse("¿c");
		final Label nab = Label.parse("¬ab");
		final Label nabc = Label.parse("¬abc");
		final Label nanbnc = Label.parse("¬a¬b¬c");
		final Label nabnc = Label.parse("¬ab¬c");
		final Label nanc = Label.parse("¬a¬c");
		actual.clear();
		actual.put(a, -83);
		actual.put(c, -79);
		actual.put(nab, -93);
		actual.put(a, -85);
		actual.put(uc, -82);
		actual.put(Label.emptyLabel, -78);
		actual.put(nabc, -95);
		actual.put(a, -86);
		actual.put(nc, -84);
		actual.put(nanbnc, -102);
		actual.put(a, -88);
		actual.put(nabnc, -100);
		actual.put(a, -90);

		expected.clear();
		expected.put(Label.emptyLabel, -79);
		expected.put(a, -90);
		expected.put(nc, -84);
		expected.put(nab, -95);
		expected.put(nanc, -100);
		expected.put(nanbnc, -102);
		assertEquals(expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazione3Test() {
		final Label b = Label.parse("¬b");
		actual.clear();
		actual.put(Label.parse("b"), -1);
		actual.put(b, -2);
		actual.put(Label.parse("¿b"), -3);

		expected.clear();
		expected.put(Label.emptyLabel, -1);
		expected.put(b, -2);
		expected.put(Label.parse("¿b"), -3);

		assertEquals(expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazioneBase1Test() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("ab"), 10);
		actual.put(Label.parse("a"), 25);
		actual.put(Label.parse("¬b"), 23);
		actual.put(Label.parse("b"), 22);
		actual.put(Label.parse("a¬b"), 23);
		actual.put(Label.parse("ab"), 20);

		expected.clear();
		// expected.put(Label.emptyLabel, 23);
		expected.put(Label.parse("¬b"), 23);
		expected.put(Label.parse("a"), 25);
		expected.put(Label.parse("b"), 22);
		expected.put(Label.parse("ab"), 10);

		assertEquals("Test su creazione base A INIZIO e gestione semplificazioni:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazioneBase2Test() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("ab"), 10);
		actual.put(Label.parse("a¬b"), 23);
		actual.put(Label.parse("¬b"), 23);
		actual.put(Label.parse("b"), 22);
		actual.put(Label.parse("ab"), 20);

		expected.clear();
		expected.put(Label.emptyLabel, 109);
		expected.put(Label.parse("¬b"), 23);
		expected.put(Label.parse("b"), 22);
		expected.put(Label.parse("ab"), 10);

		assertEquals("Test su creazione base IN MEZZO e gestione semplificazioni:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazioneBase3Test() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("A¬b"), 23);
		actual.put(Label.parse("ab"), 20);
		actual.put(Label.parse("¬b"), 23);
		actual.put(Label.parse("b"), 23);

		expected.clear();
		expected.put(Label.emptyLabel, 23);
		expected.put(Label.parse("ab"), 20);

		assertEquals("Test su creazione base IN FINE e gestione semplificazioni:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazioneBase4Test() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("a¬b"), 19);
		actual.put(Label.parse("¬a¬b"), 19);
		actual.put(Label.parse("¬ab"), 10);
		actual.put(Label.parse("ab"), 10);
		// Map: {⊡, 109 (19, a) (10, b) (19, ¬a¬b) }

		expected.clear();
		expected.put(Label.emptyLabel, 109);
		expected.put(Label.parse("b"), 10);
		expected.put(Label.parse("¬b"), 19);

		assertEquals("Test su creazione e gestione semplificazioni:\n", expected, actual);

		actual.put(Label.parse("¬b"), 10);

		expected.clear();
		expected.put(Label.emptyLabel, 10);

		assertEquals("Test su creazione e gestione semplificazioni:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazioneRicorsivaTest() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("a"), 21);
		actual.put(Label.parse("¬a¬b"), 21);
		actual.put(Label.parse("¬ab"), 21);

		expected.clear();
		expected.put(Label.emptyLabel, 21);

		assertEquals("Test inserimento due label di pari valore con un letterale opposto con ricorsione:\n", expected, actual);
	}

	/**
	 * @throws java.lang.Exception nope
	 */
	@Before
	public void setUp() throws Exception {
		factory = new LabeledIntMapSupplier<>(LabeledIntTreeMap.class);
		actual = factory.get();
		actual1 = factory.get();
		expected = factory.get();
	}

	/**
	 * Check the sum of labeled value.
	 */
	@Test
	public final void simplificationWithInfinite() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("ab"), 10);
		actual.put(Label.parse("a"), Constants.INT_POS_INFINITE);
		actual.put(Label.parse("¬b"), 23);
		actual.put(Label.parse("b"), 22);
		actual.put(Label.parse("a¬b"), Constants.INT_NULL);
		actual.put(Label.parse("ab"), Constants.INT_NEG_INFINITE);

		// System.out.println(map);
		expected = AbstractLabeledIntMap.parse("{(23, ¬b) (22, b) }");
		expected.put(Label.parse("ab"), Constants.INT_NEG_INFINITE);
		expected.put(Label.emptyLabel, 109);
		assertEquals("Test about simplification with infinity numbers:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void putACleaningValueTest() {
		actual.clear();
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.emptyLabel, 0);

		actual1 = new LabeledIntTreeMap(actual);
		actual1.put(Label.emptyLabel, 0);

		assertEquals("Put forcibly with a base", actual, actual1);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void putAllTest() {
		actual.clear();
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("¬a"), 25);

		final LabeledIntMapStub result = new LabeledIntMapStub();
		result.clear();
		result.put(Label.parse("b"), 30);
		result.put(Label.parse("¬b"), 25);

		actual1.clear();
		actual1.put(Label.parse("c"), 30);
		actual1.put(Label.parse("¬c"), 25);

		actual.putAll(result);
		assertEquals("Put all da un oggetto di una classe diversa che implementa la stessa interfaccia",
		             AbstractLabeledIntMap.parse("{(30, a) (25, ¬a) (30, b) (25, ¬b) }"), actual);
		actual.putAll(actual1);
	}

	/**
	 * Check the sum of labeled value.
	 */
	@Test
	public final void simplificationWithUnknown() {
		final Label ab = Label.parse("ab");
		final Label abp = Label.parse("abp");
		final Label abNp = Label.parse("ab¬p");
		final Label abUp = Label.parse("ab¿p");

		actual.clear();
		actual.put(ab, 10);
		actual.put(abUp, 10);
		assertEquals("Test about simplification with unknown labeled numbers:\n", AbstractLabeledIntMap.parse("{(10, ab) }"), actual);

		actual.clear();
		actual.put(abp, 10);
		actual.put(abUp, 10);
		assertEquals("Test about simplification with unknown labeled numbers:\n", AbstractLabeledIntMap.parse("{(10, abp) }"), actual);

		actual.clear();
		actual.put(abp, Constants.INT_NEG_INFINITE);
		actual.put(abUp, Constants.INT_NEG_INFINITE);
		actual1.clear();
		actual1.put(abp, Constants.INT_NEG_INFINITE);
		assertEquals("Test about simplification with unknown labeled numbers:\n", actual1, actual);

		actual.clear();
		actual.put(abp, 10);
		actual.put(abNp, 9);
		actual.put(abUp, 9);
		expected = AbstractLabeledIntMap.parse("{(10, abp) (9, ab¬p) }");
		assertEquals("Test about simplification with unknown labeled numbers:\n", expected, actual);

		actual.clear();
		actual.put(abUp, 9);
		actual.put(abp, 10);
		actual.put(abNp, 9);
		assertEquals("Test about simplification with unknown labeled numbers:\n", AbstractLabeledIntMap.parse("{(10, abp) (9, ab¬p) }"), actual);

		actual.clear();
		actual.put(abp, 10);
		actual.put(abNp, 9);
		actual.put(abUp, 8);
		assertEquals("Test about simplification with unknown labeled numbers:\n", AbstractLabeledIntMap.parse("{(10, abp) (9, ab¬p) (8, ab¿p) }"),
		             actual);
	}

	/**
	 * Check the sum of labeled value.
	 */
	@Test
	public final void simplificationWithUnknown1() {
		final Label Upab = Label.parse("¿p¿ab");
		final Label Upr = Label.parse("¿pr");
		final Label UpNr = Label.parse("¿p¬r");

		actual.clear();
		actual.put(Upab, -24);
		actual.put(Upr, Constants.INT_NEG_INFINITE);
		// System.out.println("Before introduction UpNr: "+ map);
		actual.put(UpNr, Constants.INT_NEG_INFINITE);
		assertEquals("Test about simplification with unknown labeled infinity:\n", "{(-∞, ¿p) }", actual.toString());
	}

	/**
	 *
	 */
	@Test
	public void testEntrySet1() {
		actual.put(Label.emptyLabel, 0);
		actual.put(Label.parse("a"), -1);
		actual.put(Label.parse("¬c"), -1);
		actual.put(Label.parse("b"), -1);

		assertEquals("{(0, ⊡) (-1, a) (-1, b) (-1, ¬c) }", actual.toString());
		final ObjectSet<Entry<Label>> entrySet = actual.entrySet();
		assertEquals("{⊡->0, a->-1, ¬c->-1, b->-1}", entrySet.toString());

		actual.remove(Label.parse("¬c"));
		assertEquals("{⊡->0, a->-1, ¬c->-1, b->-1}", entrySet.toString());
	}

	/**
	 *
	 */
	@Test
	public void testKeySet() {
		actual.put(Label.emptyLabel, 0);
		actual.put(Label.parse("a"), -1);
		actual.put(Label.parse("¬c"), -1);
		actual.put(Label.parse("b"), -1);

		assertEquals("{(0, ⊡) (-1, a) (-1, b) (-1, ¬c) }", actual.toString());
		final ObjectSet<Label> entrySet = actual.keySet();
		assertEquals("{⊡, a, ¬c, b}", entrySet.toString());

		actual.remove(Label.parse("¬c"));
		assertEquals("{⊡, a, ¬c, b}", entrySet.toString());
	}

	/**
	 * Test merge 20160320
	 */
	@Test
	public final void testMerge20160320() {
		actual.clear();
		actual.put(Label.parse("¬b"), 8);
		actual.put(Label.parse("¬ab"), -2);
		actual.put(Label.parse("b"), -1);
		actual.put(Label.parse("¿b"), -11);

		expected.clear();
		assertEquals("{(8, ⊡) (-1, b) (-11, ¿b) (-2, ¬ab) }", actual.toString());
		// assertEquals("{(-1, b) (8, ¬b) (-11, ¿b) (-2, ¬ab) }", this.actual.toString());
	}

	/**
	 * Test merge 20160408
	 */
	@Test
	public final void testMerge20160408() {

		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("abc¬f"), 10);
		actual.put(Label.parse("abcdef"), 20);
		actual.put(Label.parse("a¬bc¬de¬f"), 25);
		assertEquals("{(109, ⊡) (10, abc¬f) (20, abcdef) (25, a¬bc¬de¬f) }", actual.toString());

		actual.put(Label.parse("¬b¬d¬f"), 23);// toglie (25, a¬bc¬de¬f)
		actual.put(Label.parse("ec"), 22);
		actual.put(Label.parse("¬fedcba"), 23);// non serve a nulla
		assertEquals("{(109, ⊡) (22, ce) (23, ¬b¬d¬f) (10, abc¬f) (20, abcdef) }", actual.toString());

		actual.put(Label.parse("ae¬f"), 20);
		actual.put(Label.parse("¬af¿b"), 20);
		actual.put(Label.parse("¬af¿b"), 21);
		assertEquals("{(109, ⊡) (22, ce) (20, ae¬f) (20, ¬a¿bf) (23, ¬b¬d¬f) (10, abc¬f) (20, abcdef) }", actual.toString());

		actual.put(Label.parse("¬ec"), 11);
		actual.put(Label.parse("abd¿f"), 11);
		actual.put(Label.parse("a¿d¬f"), 11);
		// assertEquals("{(109, ⊡) (22, ce) (11, c¬e) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (23, ¬b¬d¬f) (10, abc¬f) (11, abd¿f) (20, abcdef) }",
		// this.actual.toString());
		assertEquals("{(109, ⊡) (22, c) (11, c¬e) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (23, ¬b¬d¬f) (10, abc¬f) (11, abd¿f) (20, abcdef) }",
		             actual.toString());

		actual.put(Label.parse("¬b¿d¿f"), 24);// non inserito perché c'è (23, ¬b¬d¬f)
		actual.put(Label.parse("b¬df¿e"), 22);
		actual.put(Label.parse("e¬c"), 23);
		// assertEquals(
		// "{(109, ⊡) (22, ce) (11, c¬e) (23, ¬ce) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (23, ¬b¬d¬f) (10, abc¬f) (11, abd¿f) (22, b¬d¿ef) (20, abcdef) }",
		// this.actual.toString());
		assertEquals(
			"{(109, ⊡) (22, c) (11, c¬e) (23, ¬ce) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (23, ¬b¬d¬f) (10, abc¬f) (11, abd¿f) (22, b¬d¿ef) (20, abcdef) }",
			actual.toString());

		actual.put(Label.parse("ab¿d¿f"), 20);// non iserito perché c'è (11, abd¿f)
		actual.put(Label.parse("ad¬f"), 23);
		actual.put(Label.parse("b¿d¿f"), 23);
		// assertEquals(
		// "{(109, ⊡) (22, ce) (11, c¬e) (23, ¬ce) (23, ad¬f) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (23, b¿d¿f) (23, ¬b¬d¬f) (10, abc¬f) (11, abd¿f) (22, b¬d¿ef)
		// (20, abcdef) }",
		// this.actual.toString());
		assertEquals(
			"{(109, ⊡) (22, c) (11, c¬e) (23, ¬ce) (23, ad¬f) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (23, b¿d¿f) (23, ¬b¬d¬f) (10, abc¬f) (11, abd¿f) (22, b¬d¿ef) (20, abcdef) }",
			actual.toString());

		actual.put(Label.parse("¬b¬d¿ef"), 23);
		// assertEquals(
		// "{(109, ⊡) (22, ce) (11, c¬e) (23, ¬ce) (23, ad¬f) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (23, b¿d¿f) (23, ¬b¬d¬f) (10, abc¬f) (11, abd¿f) (22, b¬d¿ef)
		// (23, ¬b¬d¿ef) (20, abcdef) }",
		// this.actual.toString());
		assertEquals(
			"{(109, ⊡) (22, c) (11, c¬e) (23, ¬ce) (23, ad¬f) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (23, b¿d¿f) (23, ¬b¬d¬f) (23, ¬d¿ef) (10, abc¬f) (11, abd¿f) (22, b¬d¿ef) (20, abcdef) }",
			actual.toString());

		actual.put(Label.parse("¬e¬c"), 19);

		// assertEquals("{(22, ce) (11, c¬e) (23, ¬ce) (19, ¬c¬e) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (10, abc¬f) (11, abd¿f) (22, b¬d¿ef) (20, abcdef) }",
		// this.actual.toString());
		assertEquals("{(23, ⊡) (22, c) (19, ¬e) (11, c¬e) (11, a¿d¬f) (20, ae¬f) (20, ¬a¿bf) (10, abc¬f) (11, abd¿f) (20, abcdef) }",
		             actual.toString());
	}

	/**
	 *
	 */
	@Test
	public void testRemoveEntrySet() {
		final LabeledIntTreeMap map = new LabeledIntTreeMap(false);
		map.put(Label.emptyLabel, 0);
		map.put(Label.parse("a"), -1);
		map.put(Label.parse("b"), -3);
		map.put(Label.parse("¬b¬a"), -4);

		assertEquals("{(0, ⊡) (-1, a) (-3, b) (-4, ¬a¬b) }", map.toString());
		final ObjectSet<Entry<Label>> entrySet = map.entrySet();
		assertEquals("{⊡->0, a->-1, b->-3, ¬a¬b->-4}", entrySet.toString());

		var i = 0;
		for (final Entry<Label> entry : entrySet) {
			final Label l = entry.getKey();
			map.remove(l);
			if (i == 0) {
				assertEquals("⊡", l.toString());
				assertEquals("{⊡->0, a->-1, b->-3, ¬a¬b->-4}", entrySet.toString());
				assertEquals("{(-1, a) (-3, b) (-4, ¬a¬b) }", map.toString());
				i++;
				continue;
			}
			if (i == 1) {
				assertEquals("a", l.toString());
				assertEquals("{⊡->0, a->-1, b->-3, ¬a¬b->-4}", entrySet.toString());
				assertEquals("{(-3, b) (-4, ¬a¬b) }", map.toString());
				i++;
				continue;
			}
			if (i == 2) {
				assertEquals("b", l.toString());
				assertEquals("{⊡->0, a->-1, b->-3, ¬a¬b->-4}", entrySet.toString());
				assertEquals("{(-4, ¬a¬b) }", map.toString());
				i++;
				continue;
			}
			if (i == 3) {
				assertEquals("¬a¬b", l.toString());
				assertEquals("{⊡->0, a->-1, b->-3, ¬a¬b->-4}", entrySet.toString());
				assertEquals("{}", map.toString());
				i++;
				continue;
			}
			if (i == 4) {
				assertEquals("a", l.toString());
				assertEquals("{⊡->0, a->-1, b->-3, ¬a¬b->-4}", entrySet.toString());
				assertEquals("{}", map.toString());
				i++;
			}
		}
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void unknown() {
		actual.clear();
		actual.put(Label.parse("¬a¬b"), 30);
		actual.put(Label.parse("¬ab"), 24);
		actual.put(Label.parse("¿ab"), 24);
		actual.put(Label.parse("¿a¬b"), 21);
		actual.put(Label.parse("ab"), 23);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.parse("a"), 24);
		actual.put(Label.emptyLabel, 31);

		// assertEquals("Base con valori differenti:\n", "{(24, a) (25, ¬a) (23, ab) (24, ¬ab) (21, ¿a¬b) }", this.actual.toString());
		assertEquals("Base con valori differenti:\n", "{(25, ⊡) (24, a) (24, b) (23, ab) (21, ¿a¬b) }", actual.toString());
	}

}
