package it.univr.di.labeledvalue;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.univr.di.labeledvalue.ALabelAlphabet.ALetter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class LabeledALabelIntTreeMapTest {

	private LabeledALabelIntTreeMap map,
			result;

	/**
	 *
	 */
	ALabelAlphabet alpha;

	/**
	 * Main.
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 */
	static public void main(final String[] args) {
		testEntrySet();
	}

	/**
	 * @param map map
	 * @param nTest number of test
	 * @param entrySetStats statistics
	 * @param keySetStats statistics
	 */
	private static void testEntrySetTime(LabeledALabelIntTreeMap map, int nTest, SummaryStatistics entrySetStats, SummaryStatistics keySetStats) {
		entrySetStats.clear();
		keySetStats.clear();
		Instant startInstant;

		// for (int i = 0; i < nTest; i++) {
		// startInstant = Instant.now();
		// for (Entry<ALabel, LabeledIntTreeMap> entry : map.entrySet()) {
		// entry.getKey();
		// for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Label> entry1 : entry.getValue().entrySet()) {
		// entry1.getKey();
		// entry1.getIntValue();
		// }
		// }
		// Instant endInstant = Instant.now();
		// entrySetStats.addValue(Duration.between(startInstant, endInstant).toNanos() / 10E6);
		// }

		for (int i = 0; i < nTest; i++) {
			startInstant = Instant.now();
			for (final ALabel l : map.keySet()) {
				final LabeledIntMap map1 = map.get(l);
				for (final Label label : map1.keySet()) {
					map1.get(label);
				}
			}
			final Instant endInstant = Instant.now();
			keySetStats.addValue(Duration.between(startInstant, endInstant).toNanos() / 10E6);
		}

	}

	/**
	 *
	 */
	@Test
	public final void canRepresentTest() {
		final ALabel n8 = new ALabel("N8", alpha);
		final ALabel n9 = new ALabel("N9", alpha);
		final ALabel n89 = n8.conjunction(n9);

		map.clear();
		map.mergeTriple(Label.emptyLabel, n8, -16);
		map.mergeTriple(Label.emptyLabel, n9, -17);
		map.mergeTriple(Label.emptyLabel, n89, -9);

		result.clear();
		result.mergeTriple(Label.emptyLabel, n8, -16);
		result.mergeTriple(Label.emptyLabel, n9, -17);

		assertTrue(map.alreadyRepresents(Label.emptyLabel, n89, -4));
		assertTrue(map.alreadyRepresents(Label.parse("a"), n89, -4));
		assertTrue(map.alreadyRepresents(Label.emptyLabel, n89, -16));
		assertFalse(map.alreadyRepresents(Label.parse("a"), n89, -18));
	}

	/**
	 *
	 */
	@Test
	public final void generazioneSet() {
		map.clear();
		map.mergeTriple("¬a¬b", new ALabel("N9", alpha), 13);
		map.mergeTriple("¬a", new ALabel("N9", alpha), 13);
		map.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);
		map.mergeTriple(Label.parse("¬a"), new ALabel("N9", alpha), 14);
		map.mergeTriple(Label.parse("¬a¬b"), new ALabel("N9", alpha), 11);
		map.mergeTriple(Label.parse("¬a¬b"), new ALabel("N8", alpha), 11);
		map.mergeTriple(Label.parse("¬ab"), new ALabel("N7", alpha), 11);
		map.mergeTriple(Label.parse("¬ba"), new ALabel("N9", alpha), 15);
		map.mergeTriple(Label.parse("ab"), new ALabel("N6", alpha), 1);

		final ObjectSet<Object2IntMap.Entry<Entry<Label, ALabel>>> set = new ObjectArraySet<>();
		for (final ALabel aleph : map.keySet()) {
			for (final it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Label> entry : map.get(aleph).entrySet()) {
				set.add(new AbstractObject2IntMap.BasicEntry<>((new SimpleEntry<>(entry.getKey(), aleph)),
						entry.getIntValue()));
			}
		}

		final ObjectSet<Object2IntMap.Entry<Entry<Label, ALabel>>> set1 = new ObjectArraySet<>();

		set1.add(new AbstractObject2IntMap.BasicEntry<>(
				(new SimpleEntry<>(Label.parse("¬ab"), new ALabel("N7", alpha))), 11));
		set1.add(new AbstractObject2IntMap.BasicEntry<>(
				(new SimpleEntry<>(Label.parse("ab"), new ALabel("N6", alpha))), 1));
		set1.add(new AbstractObject2IntMap.BasicEntry<>(
				(new SimpleEntry<>(Label.parse("¬a¬b"), new ALabel("N9", alpha))), 11));
		set1.add(new AbstractObject2IntMap.BasicEntry<>(
				(new SimpleEntry<>(Label.parse("¬a¬b"), new ALabel("N8", alpha))), 11));
		set1.add(new AbstractObject2IntMap.BasicEntry<>(
				(new SimpleEntry<>(Label.emptyLabel, new ALabel("N9", alpha))), 12));

		assertEquals("Generation of set of triple\n", set1, set);
	}

	/**
	 *
	 */
	@Test
	public final void immutable() {
		map.clear();
		map.mergeTriple("¬a¬b", new ALabel("N9", alpha), 13);
		map.mergeTriple("¬a", new ALabel("N9", alpha), 13);

		final LabeledALabelIntTreeMap view = map.unmodifiable();
		view.remove(Label.parse("¬a"), new ALabel("N9", alpha));

		result.clear();
		result.mergeTriple("¬a", new ALabel("N9", alpha), 13);

		assertEquals("Immutable makes read-only", result, view);
		assertEquals(1, map.size());
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione() {
		map.clear();
		map.mergeTriple("¬a¬b", new ALabel("N9", alpha), 13);
		map.mergeTriple("¬a", new ALabel("N9", alpha), 13);

		result.clear();
		result.mergeTriple("¬a", new ALabel("N9", alpha), 13);

		assertEquals("Check of merge with simple simplification", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione1() {
		map.clear();
		map.mergeTriple("¬a¬b", new ALabel("N9", alpha), 13);
		map.mergeTriple("¬a", new ALabel("N9", alpha), 13);
		map.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);

		result.clear();
		result.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);

		assertEquals("Check of merge with double simple simplification", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione10() {
		final ALabel n8 = new ALabel("N8", alpha);
		final ALabel n9 = new ALabel("N9", alpha);
		final ALabel n89 = n8.conjunction(n9);

		map.clear();
		map.mergeTriple("a", n8, -16);
		map.mergeTriple("¬a", n9, -17);
		map.mergeTriple(Label.emptyLabel, n89, -9);
		map.mergeTriple(Label.emptyLabel, ALabel.emptyLabel, -20);

		result.clear();
		result.mergeTriple(Label.emptyLabel, ALabel.emptyLabel, -20);

		assertEquals("Check of merge with two nodes\n", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione2() {
		map.clear();
		map.mergeTriple("¬a¬b", new ALabel("N9", alpha), 13);
		map.mergeTriple("¬a", new ALabel("N9", alpha), 13);
		map.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);
		map.mergeTriple(Label.parse("¬a"), new ALabel("N9", alpha), 14);

		result.clear();
		result.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);

		assertEquals("Check of merge with double simple simplification and add useless value", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione3() {
		map.clear();
		map.mergeTriple("¬a¬b", new ALabel("N9", alpha), 13);
		map.mergeTriple("¬a", new ALabel("N9", alpha), 13);
		map.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);
		map.mergeTriple(Label.parse("¬a"), new ALabel("N9", alpha), 14);
		map.mergeTriple(Label.parse("¬a¬b"), new ALabel("N9", alpha), 11);

		result.clear();
		result.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);
		result.mergeTriple("¬a¬b", new ALabel("N9", alpha), 11);

		assertEquals("Check of merge with a final overwriting value", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione4() {
		map.clear();
		map.mergeTriple("¬a¬b", new ALabel("N9", alpha), 13);
		map.mergeTriple("¬a", new ALabel("N9", alpha), 13);
		map.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);
		map.mergeTriple(Label.parse("¬a"), new ALabel("N9", alpha), 14);
		map.mergeTriple(Label.parse("¬a¬b"), new ALabel("N9", alpha), 11);
		map.mergeTriple(Label.parse("¬a¬b"), new ALabel("N8", alpha), 11);

		result.clear();
		result.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);
		result.mergeTriple("¬a¬b", new ALabel("N9", alpha), 11);
		result.mergeTriple("¬a¬b", new ALabel("N8", alpha), 11);

		assertEquals("Check of merge with two different node\n", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione5() {
		map.clear();
		map.mergeTriple("¬a¬b", new ALabel("N9", alpha), 13);
		map.mergeTriple("¬a", new ALabel("N9", alpha), 13);
		map.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);
		map.mergeTriple(Label.parse("¬a"), new ALabel("N9", alpha), 14);
		map.mergeTriple(Label.parse("¬a¬b"), new ALabel("N9", alpha), 11);
		map.mergeTriple(Label.parse("¬ab"), new ALabel("N9", alpha), 11);
		map.mergeTriple(Label.parse("¬ba"), new ALabel("N9", alpha), 12);
		map.mergeTriple(Label.parse("ab"), new ALabel("N9", alpha), 11);
		map.mergeTriple(Label.parse("¬a¬b"), new ALabel("N8", alpha), 11);

		result.clear();
		result.mergeTriple(Label.emptyLabel, new ALabel("N9", alpha), 12);
		result.mergeTriple("¬a", new ALabel("N9", alpha), 11);
		result.mergeTriple("ab", new ALabel("N9", alpha), 11);
		result.mergeTriple("¬a¬b", new ALabel("N8", alpha), 11);

		assertEquals("Check of merge with two different nodes,\n", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione6() {
		final ALabel n8 = new ALabel("N8", alpha);
		final ALabel n9 = new ALabel("N9", alpha);
		final ALabel n8n9 = n8.conjunction(n9);

		map.clear();
		map.mergeTriple("¬a", n9, 14);
		map.mergeTriple("¬b", n8, 13);
		map.mergeTriple(Label.emptyLabel, n8n9, 12);
		map.mergeTriple("¬a", n9, 12);
		map.mergeTriple("a", n9, 12);

		result.clear();
		result.mergeTriple("¬b", n8, 13);
		result.putTriple(Label.emptyLabel, n8n9, 12);
		// this.result.mergeTriple(Label.emptyLabel, n9, 12);
		// 2018-12-22 Test for evaluating if the extreme optimization worths!
		// I removed the simplification of a-labels when a new value simplifies p-labels.
		// so, the following is still present
		result.mergeTriple("¬a", n9, 12);
		result.mergeTriple("a", n9, 12);

		assertEquals("Check of merge with two concanated nodes\n", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione7() {
		final ALabel n8 = new ALabel("N8", alpha);
		final ALabel n9 = new ALabel("N9", alpha);
		final ALabel n89 = n8.conjunction(n9);

		map.clear();
		map.mergeTriple("a", ALabel.emptyLabel, -16);
		map.mergeTriple("¬a", ALabel.emptyLabel, -17);
		map.mergeTriple(Label.emptyLabel, ALabel.emptyLabel, -9);
		map.mergeTriple(Label.emptyLabel, n89, -9);

		result.clear();
		result.mergeTriple("a", ALabel.emptyLabel, -16);
		result.mergeTriple("¬a", ALabel.emptyLabel, -17);

		assertEquals("Check of merge with two nodes\n", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione8() {
		final ALabel n8 = new ALabel("N8", alpha);
		final ALabel n9 = new ALabel("N9", alpha);
		final ALabel n89 = n8.conjunction(n9);

		map.clear();
		map.mergeTriple(Label.emptyLabel, n8, -16);
		map.mergeTriple(Label.emptyLabel, n9, -17);
		map.mergeTriple(Label.emptyLabel, n89, -9);

		result.clear();
		result.mergeTriple(Label.emptyLabel, n8, -16);
		result.mergeTriple(Label.emptyLabel, n9, -17);

		assertEquals("Check of merge with two nodes\n", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeConSemplificazione9() {
		map.clear();
		map.mergeTriple(Label.emptyLabel, ALabel.emptyLabel, -16);
		map.mergeTriple("a", ALabel.emptyLabel, -17);
		map.mergeTriple("¬a", ALabel.emptyLabel, -17);

		result.clear();
		result.mergeTriple(Label.emptyLabel, ALabel.emptyLabel, -17);

		assertEquals("Check of merge with two nodes\n", result, map);
	}

	/**
	 *
	 */
	@Test
	public final void mergeWOSimplification() {
		final ALabel n8 = new ALabel("N8", alpha);
		final ALabel n9 = new ALabel("N9", alpha);
		final ALabel n89 = n8.conjunction(n9);

		map.clear();
		map.mergeTriple("a", ALabel.emptyLabel, -16, true);
		map.mergeTriple("¬a", ALabel.emptyLabel, -16, true);
		map.mergeTriple(Label.emptyLabel, ALabel.emptyLabel, -9, true);
		map.mergeTriple(Label.emptyLabel, n89, -9, true);

		assertEquals("Check of merge with two nodes\n", "{(◇, -9, ⊡) (◇, -16, a) (◇, -16, ¬a) (N8∙N9, -9, ⊡) }", map.toString());
		assertEquals(4, map.size());
	}

	/**
	 * Simple test to verify time cost of constructor.
	 */
	@SuppressWarnings("unused")
	private static void testConstructor() {
		final int nTest = (int) 1E3;
		final double msNorm = 1.0E6 * nTest;

		final LabeledALabelIntTreeMap map = new LabeledALabelIntTreeMap(null);

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

		final ALabelAlphabet alpha = new ALabelAlphabet();
		final ALabel a = new ALabel(new ALetter("A"), alpha);
		final ALabel b = new ALabel(new ALetter("B"), alpha);
		final ALabel c = new ALabel(new ALetter("C"), alpha);

		long startTime = System.nanoTime();
		for (int i = 0; i < nTest; i++) {
			map.clear();
			map.putTriple(Label.emptyLabel, a, 109);
			map.putTriple(l1, a, 10);
			map.putTriple(l2, a, 20);
			map.putTriple(l3, a, 25);
			map.putTriple(l4, a, 23);
			map.putTriple(l5, a, 22);
			map.putTriple(l6, a, 23);
			map.putTriple(l7, a, 20);
			map.putTriple(l8, a, 20);
			map.putTriple(l9, a, 21);
			map.putTriple(l10, a, 11);
			map.putTriple(l11, a, 11);
			map.putTriple(l12, a, 11);
			map.putTriple(l13, a, 24);
			map.putTriple(l14, a, 22);
			map.putTriple(l15, a, 23);
			map.putTriple(l16, a, 20);
			map.putTriple(l17, a, 23);
			map.putTriple(l18, a, 23);
			map.putTriple(l19, a, 23);
			map.putTriple(l20, a, 23);
			map.putTriple(l1, b, 10);
			map.putTriple(l2, b, 20);
			map.putTriple(l3, b, 25);
			map.putTriple(l4, b, 23);
			map.putTriple(l5, b, 22);
			map.putTriple(l6, b, 23);
			map.putTriple(l7, b, 20);
			map.putTriple(l8, b, 20);
			map.putTriple(l9, b, 21);
			map.putTriple(l10, b, 11);
			map.putTriple(l11, b, 11);
			map.putTriple(l12, b, 11);
			map.putTriple(l13, b, 24);
			map.putTriple(l14, b, 22);
			map.putTriple(l15, b, 23);
			map.putTriple(l16, b, 20);
			map.putTriple(l17, b, 23);
			map.putTriple(l18, b, 23);
			map.putTriple(l19, b, 23);
			map.putTriple(l20, b, 23);
			map.putTriple(l1, c, 10);
			map.putTriple(l2, c, 20);
			map.putTriple(l3, c, 25);
			map.putTriple(l4, c, 23);
			map.putTriple(l5, c, 22);
			map.putTriple(l6, c, 23);
			map.putTriple(l7, c, 20);
			map.putTriple(l8, c, 20);
			map.putTriple(l9, c, 21);
			map.putTriple(l10, c, 11);
			map.putTriple(l11, c, 11);
			map.putTriple(l12, c, 11);
			map.putTriple(l13, c, 24);
			map.putTriple(l14, c, 22);
			map.putTriple(l15, c, 23);
			map.putTriple(l16, c, 20);
			map.putTriple(l17, c, 23);
			map.putTriple(l18, c, 23);
			map.putTriple(l19, c, 23);
			map.putTriple(l20, c, 23);
		}
		long endTime = System.nanoTime();
		// System.out.println("LABELED VALUE SET-TREE MANAGED\nExecution time for some merge operations (mean over " + nTest + " tests).\nFirst map: " + map
		// + ".\nTime: (ms): "
		// + ((endTime - startTime) / msNorm));
		// String rightAnswer = "{(⊡, A, 23) (abc¬f, A, 10) (abd¿f, A, 11) (b¬d¿ef, A, 22) (abcdef, A, 20) (ae¬f, A, 20) (¬a¿bf, A, 20) (a¿d¬f, A, 11) (c¬e, A,
		// 11) (c, A, 22) }";
		// System.out.println("The right final set is " + parse(rightAnswer,alpha) + ".");
		// System.out.println("Is equal? " + parse(rightAnswer,alpha).equals(map));

		startTime = System.nanoTime();
		int min = 1000;
		for (int i = 0; i < nTest; i++) {
			min = map.getMinValue().getValue().getIntValue();
		}
		endTime = System.nanoTime();
		// System.out.println("Execution time for determining the min value (" + min + ") (mean over " + nTest + " tests). (ms): "
		// + ((endTime - startTime) / msNorm));

//		startTime = System.nanoTime();
		final Label l = Label.parse("abd¿f");
		for (int i = 0; i < nTest; i++) {
			min = map.getValue(l, a);
		}
//		endTime = System.nanoTime();
		// System.out.println("Execution time for retrieving value of label " + l + " (mean over " + nTest + " tests). (ms): "
		// + ((endTime - startTime) / msNorm));

		// startTime = System.nanoTime();
		// map.putTriple(Label.parse("c"), a, 11);
		// map.putTriple(Label.parse("¬c"), a, 11);
		// endTime = System.nanoTime();
		// System.out.println("After the insertion of (c,A,11) and (¬c,A,11) the map becomes: " + map);
		// System.out.println("Execution time for simplification (ms): "
		// + ((endTime - startTime) / 1.0E6));

		map.clear();
		map.putTriple(Label.parse("c"), a, 11);
		map.putTriple(Label.parse("c"), a.conjunction(b), 11);
		map.putTriple(Label.parse("c"), a.conjunction(c), 11);
		map.putTriple(Label.parse("c"), a.conjunction(c), 11);
		map.putTriple(Label.parse("c"), a.conjunction(c), 11);
		System.out.println("After the insertion of conjuncted ALabel: " + map);
	}

	/**
	 * Simple test to verify time cost of entrySet() vs. keySet(). On 2017-10-26 it resulted that there is no appreciable difference between accessing to the
	 * map using entrySet() & its get() and accessing to the map using keySet() and, then, find().
	 */
	private static void testEntrySet() {
		final int nTest = (int) 1E2;
		final LabeledALabelIntTreeMap map = new LabeledALabelIntTreeMap(null);
		final SummaryStatistics entrySetStats = new SummaryStatistics();
		final SummaryStatistics keySetStats = new SummaryStatistics();
		final int[] value = {50, 100, 1000, 5000, 10000};
		final int nChar = 'z' - 'a' + 1;
		final char[] chars = new char[nChar];
		for (int j = 'a'; j <= 'z'; j++)
			chars[j - 'a'] = (char) j;

		final ALabelAlphabet alphabet = new ALabelAlphabet();
		for (final int nLabel : value) {
			for (int i = 0; i < nLabel; i++) {
				final Label label = Label.emptyLabel;
				int l = 1;
				for (int j = 0; j < nChar; j++, l = l << 1) {
					label.conjunction(chars[j], ((i & l) != 0 ? Literal.STRAIGHT : Literal.NEGATED));
				}
				final ALabel aleph = new ALabel(Character.toString(chars[i % nChar]), alphabet);
				map.putTriple(label, aleph, i);
			}
			// System.out.println("Map size: " + map);
			testEntrySetTime(map, nTest, entrySetStats, keySetStats);
			// System.out.println("Time to retrieve " + map.size() + " elements using entrySet(): " + entrySetStats.getMean() + "ms");
			System.out.println(
				"Time to retrieve " + map.size() + " elements using keySet(): " + keySetStats.getMean() + "ms");
			// System.out.println("The difference is " + (entrySetStats.getMean() - keySetStats.getMean()) + " ms. It is better to use: "
			// + ((entrySetStats.getMean() < keySetStats.getMean()) ? "entrySet()" : "keySet()") + " approach.\n");
		}
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void minValue() {
		map.clear();
		map = LabeledALabelIntTreeMap.parse("{(¬a, N9, -12) (a, N10, -11) (" + Label.emptyLabel + ", N9, -14) }",
		                                    alpha, null);

		assert map != null;
		assertEquals(-14, map.getMinValue().getValue().getIntValue());
		map.clear();

		assertEquals(Constants.INT_NULL, map.getMinValue().getValue().getIntValue());
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void minValueConsisntenWithTest() {
		map.clear();
		map = LabeledALabelIntTreeMap.parse("{(¬a, N9, -12) (a, N10, -11) (" + Label.emptyLabel + ", N9, -14) }",
		                                    alpha, null);
		// System.out.println(this.map);
		assert map != null;
		assertEquals(-14, map.getMinValue().getValue().getIntValue());
		map.clear();
		assertEquals(Constants.INT_NULL, map.getMinValue().getValue().getIntValue());

		map = LabeledALabelIntTreeMap.parse("{(¬a, N9, -12) (a, N10, -11) (" + Label.emptyLabel + ", N9, -14)}",
		                                    alpha, null);
		// System.out.println(map.getMinValueConsistentWith(Label.parse("¬a"), new ALabel("N9", alpha)) );
		assert map != null;
		assertEquals(-14, map.getMinValueConsistentWith(Label.parse("¬a"), new ALabel("N9", alpha)));
		assertEquals(Constants.INT_NULL, map.getMinValueConsistentWith(Label.parse("¬a"), new ALabel("N11", alpha)));
	}

	/**
	 *
	 */
	@Test
	public final void parse() {

		map = LabeledALabelIntTreeMap
				.parse("{(¬a, N9, -12) (¬a, N9" + ALabel.ALABEL_SEPARATORstring + "N12" + ALabel.ALABEL_SEPARATORstring
				       + "N13, -20) (a, N10, -11) (" + Label.emptyLabel + ", N9, -12)}", alpha, null);

		// System.out.printf("Map da parse: %s\n", map);

		final ALabel n9 = new ALabel("N9", alpha);
		result.clear();
		result.putTriple(Label.emptyLabel, n9, -12);
		result.mergeTriple("a", new ALabel("N10", alpha), -11);
		n9.conjoin(new ALetter("N12"));
		n9.conjoin(new ALetter("N13"));
		result.mergeTriple("¬a", n9, -20);

		assertEquals("Check of parse method", result, map);

		final String mapS = "{(12E, -142, ⊡) (12E, -162, e) (10E∙12E, -185, e) (12E∙2E, -192, e) (12E∙2E, -172, ⊡) (6E∙12E, -178, e) (11E∙12E∙7E, -159, ¬e) (6E∙11E∙12E, -156, ¬e) (11E∙12E∙2E∙7E, -187, ¬e) (12E∙2E∙7E, -161, ¬e) (10E∙12E∙2E, -76, ⊡) (10E∙12E∙2E, -215, e) (6E∙12E∙2E, -208, e) (10E∙6E∙12E, -95, ⊡) (10E∙6E∙12E, -201, e) (12E∙7E, -84, ⊡) (10E∙12E∙7E, -84, ⊡) (6E∙12E∙7E, -84, ⊡) (10E∙12E∙2E∙7E, -114, ⊡) (10E∙11E∙12E∙7E, -69, ⊡) (10E∙6E∙11E∙12E, -69, ⊡) (10E∙11E∙12E∙2E∙7E, -69, ⊡) (10E∙16E∙2E, -15, de) (10E∙6E∙16E, -1, de) }";

		alpha.clear();
		map = LabeledALabelIntTreeMap.parse(mapS, alpha, null);
		// Assert.assertEquals(mapS, this.map.toString());
	}

	/**
	 * @throws Exception  nope
	 */
	@Before
	public void setUp() throws Exception {
		map = new LabeledALabelIntTreeMap(null);
		result = new LabeledALabelIntTreeMap(null);
		alpha = new ALabelAlphabet();
	}

}
