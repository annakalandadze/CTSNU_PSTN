/**
 *
 */
package it.univr.di.labeledvalue;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.*;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.Serial;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author posenato
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
public class LabeledIntHierarchyMapTest {


	/**
	 *
	 */
	LabeledIntMapSupplier<LabeledIntHierarchyMap> factory = new LabeledIntMapSupplier<>(LabeledIntHierarchyMap.class);

	/**
	 *
	 */
	LabeledIntHierarchyMap actual = factory.get();

	/**
	 *
	 */
	LabeledIntMap expected = factory.get();

	/**
	 *
	 */
	LabeledIntHierarchyMap actual1 = new LabeledIntHierarchyMap();

	/**
	 * Check if the management of the base is correct.
	 */
	@SuppressWarnings("SpellCheckingInspection")
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
		// E,25 a,24 ¬ab,23

		expected.clear();
		expected.put(Label.emptyLabel, 25);
		expected.put(Label.parse("a"), 24);
		expected.put(Label.parse("¬ab"), 23);

		// System.out.println("map:"+map);
		// System.out.println("result:"+result);
		// assertEquals(
		// "Rimuovo componenti maggiori con elementi consistenti della base:\nexpected:" + expected + ".size: " + expected.size() + "\nactual" + actual
		// + ".size: " + actual.size()
		// + "\n",
		// expected, actual);
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
	public final void caso20160108() {
		actual.clear();
		// SEVERE: Put history: (A, -40) (A, -56) (¬a¬b¬c¬DE, -46) (¬a¬c¬DE, -4) (-20, ¬a¬b) (⊡, -16)
		final Label a = Label.parse("a");
		final Label NaNbNcNde = Label.parse("¬a¬b¬c¬de");
		final Label NaNcNde = Label.parse("¬a¬c¬de");
		final Label NaNb = Label.parse("¬a¬b");

		actual.put(a, -40);
		actual.put(a, -56);
		actual.put(NaNbNcNde, -46);
		actual.put(NaNcNde, -4);
		actual.put(NaNb, -20);
		actual.put(Label.emptyLabel, -16);

		assertEquals("{(-16, ⊡) (-56, a) (-20, ¬a¬b) (-46, ¬a¬b¬c¬de) }", actual.toString());
		// System.out.println("actual: " + actual);

	}

	/**
	 * stub class just to have a different implementation of the interface.
	 *
	 * @author posenato
	 */
	private static class LabeledIntMapStub implements LabeledIntMap {

		@Serial
		private static final long serialVersionUID = 1L;
		/**
		 *
		 */
		Object2IntRBTreeMap<Label> map;

		/**
		 *
		 */
		LabeledIntMapStub() {
			map = new Object2IntRBTreeMap<>();
		}

		@Override
		public boolean alreadyRepresents(Label newLabel, int newValue) {
			//  Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			map.clear();
		}

		@Override
		public ObjectSet<Entry<Label>> entrySet(@Nonnull ObjectSet<Entry<Label>> setToReuse) {
			//  Auto-generated method stub
			return null;
		}

		@Override
		public ObjectSortedSet<Entry<Label>> entrySet() {
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
			//  Auto-generated method stub
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
			//  Auto-generated method stub
			return 0;
		}

		@Override
		public int hashCode() {
			return map.hashCode();
		}

		@Override
		public boolean isEmpty() {
			//  Auto-generated method stub
			return false;
		}

		@Override
		public ObjectSet<Label> keySet() {
			return map.keySet();
		}

		@Override
		public ObjectSet<Label> keySet(ObjectSet<Label> setToReuse) {
			//  Auto-generated method stub
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
		public LabeledIntMap newInstance() {
			//  Auto-generated method stub
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
			//  Auto-generated method stub
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
			//  Auto-generated method stub
			return null;
		}

		@Override
		public IntSet values() {
			return null;
		}
	}

	/**
	 *
	 */
	@Test
	public final void caso20160109c() {
		actual.clear();
		final Label l = Label.parse("¬p¬q");
		actual.put(Label.parse("¬p¿q"), -13);
		actual.put(l, -13);
		actual.put(Label.parse("¿p"), -15);
		actual.put(Label.parse("¿p¬q"), -22);
		actual.put(Label.emptyLabel, -10);
		actual.put(Label.parse("¿p¿q"), Constants.INT_NEG_INFINITE);

		// System.out.println(actual);
		actual.put(Label.emptyLabel, -22);
		// System.out.println(actual);
		assertEquals("{(-22, ⊡) }", actual.toString());
	}

	/**
	 *
	 */
	@Test
	public final void caso20160109d() {
		actual.clear();
		final Label l = Label.parse("¬a¬b¬d¬e");
		final Label l1 = Label.parse("¬a¬d¬e");

		actual.put(Label.parse("¬c¬de"), 184);
		actual.put(Label.parse("¬a¬b"), 608);
		actual.put(Label.parse("¬ab¬d¬e"), 577);
		actual.put(Label.parse("¬a"), 625);
		actual.put(Label.parse("a¬d¬e"), 470);
		actual.put(Label.emptyLabel, 672);
		actual.put(Label.parse("¬a¬bd"), 288);
		actual.put(Label.parse("¬ad"), 295);
		actual.put(Label.parse("d"), 297);
		actual.put(l, 399);
		actual.put(l1, 406);
		actual.put(Label.parse("¬d¬e"), 408);
		actual.put(Label.parse("¬de"), 415);
		actual.put(Label.parse("¬d¬e"), 306);
		// System.out.println(actual);
		actual.put(Label.emptyLabel, -22);
		// System.out.println(actual);
		assertEquals("{(-22, ⊡) }", actual.toString());
	}

	/**
	 *
	 */
	@Test
	public final void caso20160110() {
		actual.clear();
		final Label l1 = Label.parse("¬d¬e");

		actual.put(Label.parse("¬ab¬d¬e"), -143);
		// actual.put(Label.parse("a¬c¬d¿e"), -137);
		// actual.put(Label.parse("¬c¬d¿e"), -99);
		actual.put(Label.parse("¿ab¬d¬e"), -164);
		// actual.put(Label.parse("¬a¬b¬d¬e"), 454);
		actual.put(Label.parse("a¬d¬e"), 275);
		actual.put(l1, -130);
		actual.put(l1, -189);
		// System.out.println(actual.entrySet());
		assertEquals("{(-189, ¬d¬e) }", actual.toString());
	}

	/**
	 *
	 */
	@Test
	public final void caso20160111() {
		// valori arco 13S_X5E?
		// currentGraph: ❮13S_X5E?; derived; {(-86, ⊡) (-87, ¬c) (C, -87) (CD, -95) (¿C, -93) }; ❯
		// nextGraph: ❮13S_X5E?; derived; {(-86, ⊡) (-87, ¬c) (C, -87) (CD, -95) (¿C, -93) (¿C¬D, -71) }; ❯

		actual.clear();

		actual.put(Label.parse("C¬D"), -50);
		actual.put(Label.parse("c"), 27);
		actual.put(Label.parse("c"), -2);
		actual.put(Label.parse("¬c"), -32);
		actual.put(Label.parse("c"), -17);
		actual.put(Label.parse("¬c"), -68);
		actual.put(Label.parse("¬ac"), -23);
		actual.put(Label.parse("¬ac"), -25);
		actual.put(Label.parse("¬b¬c"), -71);
		actual.put(Label.parse("¬bc"), -20);
		actual.put(Label.parse("¿c¬d"), -71);
		actual.put(Label.parse("c"), -20);
		actual.put(Label.parse("¬c"), -71);
		actual.put(Label.parse("c"), -28);
		actual.put(Label.parse("¬c"), -79);
		actual.put(Label.parse("¬c"), -80);
		actual.put(Label.emptyLabel, 0);
		actual.put(Label.emptyLabel, -10);
		actual.put(Label.parse("c¬d"), -51);
		actual.put(Label.parse("c¬d"), -53);
		actual.put(Label.parse("cd"), -72);
		actual.put(Label.parse("cd"), -84);
		actual.put(Label.parse("ac"), -43);
		actual.put(Label.parse("a"), -36);
		actual.put(Label.parse("¬ac"), -29);
		actual.put(Label.parse("¬a"), -22);
		actual.put(Label.parse("c¬d"), -54);
		actual.put(Label.parse("cd"), -85);
		// System.out.println("actual:" + actual);
		actual1 = new LabeledIntHierarchyMap(actual);
		// System.out.println("actual1:" + actual1);
		actual = actual1;

		actual.put(Label.parse("c"), -76);
		actual.put(Label.emptyLabel, -75);
		actual.put(Label.parse("¿cd"), -95);
		actual.put(Label.parse("¿c"), -83);
		actual.put(Label.parse("¿c"), -93);
		actual.put(Label.parse("c"), -83);
		actual.put(Label.parse("c"), -87);
		actual.put(Label.parse("¬c"), -87);
		actual.put(Label.emptyLabel, -86);
		// System.out.println("actual:" + actual);
		actual1 = new LabeledIntHierarchyMap(actual);
		// System.out.println("actual1:" + actual1);
		actual = actual1;
		actual.put(Label.parse("cd"), -95);

		// System.out.println("actual:" + actual);
		actual1 = new LabeledIntHierarchyMap(actual);
		// System.out.println("actual1:" + actual1);
		actual = actual1;

		// System.out.println(actual);
		expected = AbstractLabeledIntMap.parse("{(-93, ¿c) (-95, cd) (-87, c) (-87, ¬c) (-86, ⊡) }");
		assertEquals(expected.toString(), actual.toString()); // se ottimizzato
		// assertEquals("{(-86, ⊡) (-87, ¬c) (C, -87) (CD, -95) (¿C, -93) }", actual.toString());//se non ottimizzato
	}

	/**
	 *
	 */
	@Test
	public final void caso20160112() {
		// valori arco 34E_5E
		// currentGraph: ❮34E_5E; derived; {(-98, ⊡) (-121, ¬a¬b) (A¬bC, -140) (-115, ¬b) (BC, -177) (C, -125) }; ❯
		// nextGraph: ❮34E_5E; derived; {(-98, ⊡) (-121, ¬a¬b) (A¬bC, -140) (-115, ¬b) (BC, -177) (¿BC, -145) (C, -125) }; ❯

		actual.clear();

		actual.put(Label.parse("¬bc"), -89);
		actual.put(Label.parse("¬bc"), -111);
		actual.put(Label.parse("¬a¬b"), 125);
		actual.put(Label.parse("¬b"), 131);
		actual.put(Label.parse("¬b"), 125);
		actual.put(Label.parse("¬a¬b"), -111);
		actual.put(Label.parse("¬b"), -111);
		actual.put(Label.parse("¿bc"), -145);
		actual.put(Label.parse("a¬bc"), -140);
		actual.put(Label.parse("¬bc"), -119);
		actual.put(Label.parse("¬a¬bc"), -124);
		actual.put(Label.parse("¬a¿b"), -121);
		actual.put(Label.parse("¿a¬b"), -121);
		actual.put(Label.parse("¬a¬b"), -121);
		actual.put(Label.parse("¿b"), -115);
		actual.put(Label.parse("a¬b"), -115);
		actual.put(Label.parse("ac"), -107);
		actual.put(Label.parse("bc"), -150);
		actual.put(Label.parse("¬a"), -88);
		actual.put(Label.emptyLabel, -86);
		actual.put(Label.parse("bc"), -177);
		actual.put(Label.emptyLabel, -98);
		actual.put(Label.parse("¬a¬bc"), -125);
		// System.out.println(actual);

		expected = AbstractLabeledIntMap.parse("{(-98, ⊡) (-121, ¬a¬b) (-140, a¬bc) (-115, ¬b) (-177, bc) (-125, c) }");
		assertEquals(expected.toString(), actual.toString()); // se ottimizzato
		// assertEquals("{(-86, ⊡) (-87, ¬c) (C, -87) (CD, -95) (¿C, -93) }", actual.toString());//se non ottimizzato
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
		// E,30 ¬a,25 b,24 ab,23
		expected.clear();
		expected.put(Label.emptyLabel, 30);
		expected.put(Label.parse("¬a"), 25);
		expected.put(Label.parse("ab"), 23);
		expected.put(Label.parse("b"), 24);

		// System.out.printf("map:"+map);
		assertEquals("Base con valori differenti:\n", expected, actual); // Se ottimizzato
		// assertEquals("{("+Label.emptyLabel+", 31) (25, ¬a) (24, ¬ab) (30, a) (23, ab) }", actual.toString());//Non ottimizzato
	}

	/**
	 * Checks if the management of the structure is correct. It checks only if actual.wellFormatCheck is true!
	 */
	@Test
	public final void caso20160108b() {
		actual.clear();
		actual.wellFormatCheck = true;
		// SEVERE: Put history: (841, ¬a¬b¬d¬e) (823, ¬a¬b¬d¬e) (576, ¬a¬b¬d¬e) (¬a¬b¬c¬DE, 517) (552, ¬a¬b) (¬a¬b¬d¬e, 881)
		// (818, ¬ab¬d¬e) (890, ¬a¬d¬e) (¬a¬c¬DE, 896) (916, ¬a) (A¬d¬e, 711) (A¬d¬e, 711) (890, ¬d¬e) (¬c¬DE, 927) (⊡, 964)
		// (890, ¬a¬b¬d¬e) (899, ¬a¬d¬e) (¬a¬b¬c¬DE, 373) (459, ¬a¬b) (¬a¬c¬DE, 752) (775, ¬ab¬d¬e) (A, 642) (A, 642)
		actual.put(Label.parse("¬a¬b¬d¬e"), 841);
		actual.put(Label.parse("¬a¬b¬d¬e"), 823);
		actual.put(Label.parse("¬a¬b¬d¬e"), 576);
		actual.put(Label.parse("¬a¬b¬c¬de"), 517);
		actual.put(Label.parse("¬a¬b"), 552);
		actual.put(Label.parse("¬a¬b¬d¬e"), 881);
		actual.put(Label.parse("¬ab¬d¬e"), 818);
		actual.put(Label.parse("¬a¬d¬e"), 890);
		actual.put(Label.parse("¬a¬c¬de"), 896);
		actual.put(Label.parse("¬a"), 916);
		actual.put(Label.parse("a¬d¬e"), 711);
		actual.put(Label.parse("¬d¬e"), 890);
		actual.put(Label.parse("¬c¬de"), 927);
		actual.put(Label.emptyLabel, 964);
		actual.put(Label.parse("¬a¬b¬d¬e"), 890);
		actual.put(Label.parse("¬a¬d¬e"), 899);// !!!
		actual.put(Label.parse("¬a¬b¬c¬de"), 373);
		actual.put(Label.parse("¬a¬b"), 459);
		actual.put(Label.parse("¬a¬c¬de"), 752);
		actual.put(Label.parse("¬ab¬d¬e"), 775);
		actual.put(Label.parse("a"), 642);
		actual.put(Label.parse("a"), 642);

		assertEquals("{(916, ⊡) (642, a) (459, ¬a¬b) (890, ¬d¬e) (775, ¬ab¬d¬e) (752, ¬a¬c¬de) (373, ¬a¬b¬c¬de) }",
		             actual.toString());
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@SuppressWarnings({"static-method", "AutoBoxing", "AutoUnboxing"})
	@Test
	public final void caso20160109() {

		final ObjectArraySet<Integer> set = new ObjectArraySet<>();

		set.add(1);
		set.add(2);
		final Integer quattro = Integer.valueOf(4);
		set.add(quattro);
		set.add(3);
		final Integer cinque = Integer.valueOf(5);
		set.add(cinque);
		set.add(6);

		final StringBuilder seq = new StringBuilder(30);
		for (final Integer i : set) {// Questo for salta il valore 6!
			if (i == null) {
				continue;
			}
			if (i == 3) {// emulo cancellazioni fatte da procedure chiamate dentro il for
				set.remove(quattro);
				set.remove(cinque);
			}
			seq.append(i);
			seq.append(' ');
		}
		assertEquals("1 2 4 3 ", seq.toString());

		seq.setLength(0);
		set.clear();
		set.add(1);
		set.add(2);
		set.add(quattro);
		set.add(3);
		set.add(cinque);
		set.add(6);
		for (final Iterator<Integer> iT = set.iterator(); iT.hasNext(); ) {// Questo for salta il valore 6!
			final Integer i = iT.next();
			if (i == null) {
				continue;
			}
			if (i == 3) {// emulo cancellazioni fatte da procedure chiamate dentro il for
				set.remove(quattro);
				set.remove(cinque);
			}
			seq.append(i);
			seq.append(' ');
		}
		assertEquals("1 2 4 3 ", seq.toString());

		seq.setLength(0);
		set.clear();
		set.add(1);
		set.add(2);
		set.add(quattro);
		set.add(3);
		set.add(cinque);
		set.add(6);
		Integer[] iA = set.toArray(new Integer[6]);
		for (final Integer i : iA) {// Questo for analizza il 5 che non è più presente!
			if (i == null) {
				continue;
			}
			if (i == 3) {// emulo cancellazioni fatte da procedure chiamate dentro il for
				set.remove(quattro);
				set.remove(cinque);
			}
			seq.append(i);
			seq.append(' ');
		}
		assertEquals("1 2 4 3 5 6 ", seq.toString());

		seq.setLength(0);
		set.clear();
		set.add(1);
		set.add(2);
		set.add(quattro);
		set.add(3);
		set.add(cinque);
		set.add(6);
		iA = set.toArray(new Integer[6]);
		for (final Integer i : iA) {// Questo for è ok!
			if (i == null || !set.contains(i)) {
				continue;
			}
			if (i == 3) {// emulo cancellazioni fatte da procedure chiamate dentro il for
				set.remove(quattro);
				set.remove(cinque);
			}
			seq.append(i);
			seq.append(' ');
		}
		assertEquals("1 2 4 3 6 ", seq.toString());

	}

	/**
	 * Check the kind of bundle result returned.
	 */
	@Test
	public final void checkIntSet() {
		actual.clear();
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.emptyLabel, 31);
		actual.put(Label.parse("¬ab"), 24);
		actual.put(Label.parse("¬a¬b"), 30);
		actual.put(Label.parse("ab"), 23);

		final Set<Entry<Label>> set = actual.entrySet();
		// System.out.println(set);
		for (final Entry<Label> e : set) {
			e.setValue(1);
		}
		assertEquals(set, actual.entrySet());
		// System.out.println("Map: "+map);
		// System.out.println("Set: "+set);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@SuppressWarnings("SpellCheckingInspection")
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
		// assertEquals("Test di distruzione base:\nexpect.size" + expected.size() + "\nactual.size" + actual.size(), expected, actual);//se ottimizzato
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
		assertEquals("Test di distruzione base 2:\n", expected, actual);
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

		// assertEquals("Test cancellazione ripetuta, di empty label e di una label inesistente:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@SuppressWarnings("SpellCheckingInspection")
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
		expected.put(Label.emptyLabel, 30);
		expected.put(Label.parse("¬a"), 25);
		expected.put(Label.parse("ab"), 23);
		expected.put(Label.parse("b"), 24);

		// System.out.printf("map:"+map);
		// assertTrue("Test di equals con un'altra classe che implementa l'interfaccia:\nexpected:" + expected + ".size: " + expected.size() + "\nactual" +
		// actual
		// + ".size: " + actual.size()
		// + "\n", expected.equals(actual));
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@SuppressWarnings({"static-method", "AutoBoxing", "AutoUnboxing"})
	@Test
	public final void caso20160109list() {

		final ObjectArrayList<Integer> list = new ObjectArrayList<>();

		list.add(1);
		list.add(2);
		final Integer quattro = Integer.valueOf(4);
		list.add(quattro);
		list.add(3);
		final Integer cinque = Integer.valueOf(5);
		list.add(cinque);
		list.add(6);

		final StringBuilder seq = new StringBuilder(30);
		for (final Integer i : list) {// Questo for salta il valore 6!
			if (i == null) {
				continue;
			}
			if (i == 3) {// emulo cancellazioni fatte da procedure chiamate dentro il for
				list.remove(quattro);
				list.remove(cinque);
			}
			seq.append(i);
			seq.append(' ');
		}
		assertEquals("1 2 4 3 ", seq.toString());

		seq.setLength(0);
		list.clear();
		list.add(1);
		list.add(2);
		list.add(quattro);
		list.add(3);
		list.add(cinque);
		list.add(6);
		for (final Iterator<Integer> iT = list.iterator(); iT.hasNext(); ) {// Questo for salta il valore 6!
			final Integer i = iT.next();
			if (i == null) {
				continue;
			}
			if (i == 3) {// emulo cancellazioni fatte da procedure chiamate dentro il for
				list.remove(quattro);
				list.remove(cinque);
			}
			seq.append(i);
			seq.append(' ');
		}
		assertEquals("1 2 4 3 ", seq.toString());

		seq.setLength(0);
		list.clear();
		list.add(1);
		list.add(2);
		list.add(quattro);
		list.add(3);
		list.add(cinque);
		list.add(6);
		Integer[] iA = list.toArray(new Integer[6]);
		for (final Integer i : iA) {// Questo for analizza il 5 che non è più presente!
			if (i == null) {
				continue;
			}
			if (i == 3) {// emulo cancellazioni fatte da procedure chiamate dentro il for
				list.remove(quattro);
				list.remove(cinque);
			}
			seq.append(i);
			seq.append(' ');
		}
		assertEquals("1 2 4 3 5 6 ", seq.toString());

		seq.setLength(0);
		list.clear();
		list.add(1);
		list.add(2);
		list.add(quattro);
		list.add(3);
		list.add(cinque);
		list.add(6);
		iA = list.toArray(new Integer[6]);
		for (final Integer i : iA) {// Questo for è ok!
			if (i == null || !list.contains(i)) {
				continue;
			}
			if (i == 3) {// emulo cancellazioni fatte da procedure chiamate dentro il for
				list.remove(quattro);
				list.remove(cinque);
			}
			seq.append(i);
			seq.append(' ');
		}
		assertEquals("1 2 4 3 6 ", seq.toString());

	}

	/**
	 *
	 */
	@Before
	public void init() {
		actual.wellFormatCheck = true;
		actual1.wellFormatCheck = true;
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
	public final void manageTwinDebug() {
		actual.clear();
		actual.put(Label.parse("¬a¬b"), 30);
		actual.put(Label.parse("¬ab"), 24);
		// actual.put(Label.parse("ab"), 23);
		// actual.put(Label.parse("¬a"), 25);
		// actual.put(Label.parse("a"), 30);
		// actual.put(Label.emptyLabel, 31);

		expected.clear();
		// expected.put(Label.emptyLabel, 31);
		// expected.put(Label.parse("a"), 30);
		expected.put(Label.parse("¬a"), 30);
		// expected.put(Label.parse("ab"), 23);
		expected.put(Label.parse("¬ab"), 24);

		// assertTrue("Test di equals:\n", expected.equals(actual));
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
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void putACleaningValueTest() {
		actual.clear();
		actual.put(Label.parse("a"), 30);
		actual.put(Label.parse("¬a"), 25);
		actual.put(Label.emptyLabel, 0);

		actual1 = new LabeledIntHierarchyMap(actual);
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
		// assertEquals("Put all da un oggetto di una classe diversa che implementa la stessa interfaccia",
		// AbstractLabeledIntMap.parse("{(30, a) (25, ¬a) (30, b) (25, ¬b) }"), actual);
		// actual.putAll(actual1);
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

		// assertEquals("Test inserimento due label di pari valore con un letterale opposto:\n", expected, actual);
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
		expected.put(Label.emptyLabel, 23);
		expected.put(Label.parse("b"), 22);
		expected.put(Label.parse("ab"), 10);

		// assertEquals("Test su creazione base A INIZIO e gestione semplificazioni:\n", expected, actual);
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
		expected.put(Label.emptyLabel, 23);
		expected.put(Label.parse("b"), 22);
		expected.put(Label.parse("ab"), 10);

		// assertEquals("Test su creazione base IN MEZZO e gestione semplificazioni:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazioneBase3Test() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("a¬b"), 23);
		actual.put(Label.parse("ab"), 20);
		actual.put(Label.parse("¬b"), 23);
		actual.put(Label.parse("b"), 23);

		expected.clear();
		expected.put(Label.emptyLabel, 23);
		expected.put(Label.parse("ab"), 20);

		// assertEquals("Test su creazione base IN FINE e gestione semplificazioni:\n", expected, actual);
	}

	/**
	 * Check if the management of the base is correct.
	 */
	@Test
	public final void semplificazioneBase4Test() {
		actual.clear();
		actual.put(Label.emptyLabel, 109);
		actual.put(Label.parse("ab"), 10);
		actual.put(Label.parse("a¬b"), 19);
		actual.put(Label.parse("¬ab"), 10);
		actual.put(Label.parse("¬a¬b"), 19);
		// { E, 109 a,19 b,10 –a–b,19 }

		expected.clear();
		expected.put(Label.emptyLabel, 109);
		expected.put(Label.parse("¬b¬a"), 19);
		expected.put(Label.parse("b"), 10);
		expected.put(Label.parse("a"), 19);
		// assertEquals("Test su creazione e gestione semplificazioni:\n", expected, actual);

		actual.put(Label.parse("¬b"), 10);
		expected.clear();
		expected.put(Label.emptyLabel, 10);

		// assertEquals("Test su creazione e gestione semplificazioni:\n", expected, actual);

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

		// assertEquals("Test inserimento due label di pari valore con un letterale opposto con ricorsione:\n", expected, actual);
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
		// assertEquals("Test about simplification with infinity numbers:\n", expected, actual);
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
		actual.put(Label.emptyLabel, 31);

		expected.clear();
		expected.put(Label.emptyLabel, 31);
		expected.put(Label.parse("a"), 30);
		expected.put(Label.parse("¬a"), 25);
		expected.put(Label.parse("ab"), 23);
		expected.put(Label.parse("b"), 24);

		// System.out.printf("map:"+map);
		assertEquals("Test di equals.\nexpected: " + expected
		             + "\nactual: " + actual, expected, actual); // se ottimizzato
		// assertEquals("{("+Label.emptyLabel+", 31) (25, ¬a) (24, ¬ab) (30, a) (23, ab) }", actual.toString());//Non ottimizzato
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
		expected = AbstractLabeledIntMap.parse("{(10, ab) }");
		assertEquals(
			"Test about simplification with unknown labeled numbers:\nexpected:" + expected.size() + "\nactual: " +
			actual.size(),
			expected, actual);

		actual.clear();
		actual.put(abp, 10);
		actual.put(abUp, 10);
		assertEquals("Test about simplification with unknown labeled numbers:\n",
		             AbstractLabeledIntMap.parse("{(10, abp) }"), actual);

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
		// expected = AbstractLabeledIntMap.parse("{(10, abp) (9, ab¬p) }");
		// assertEquals("Test about simplification with unknown labeled numbers:\n", expected, actual);//se ottimizzato
		assertEquals("{(10, ab) (9, ab¬p) }", actual.toString());// Ottimizzato

		actual.clear();
		actual.put(abUp, 9);
		actual.put(abp, 10);
		actual.put(abNp, 9);
		assertEquals("Test about simplification with unknown labeled numbers:\n", "{(10, ab) (9, ab¬p) }",
		             actual.toString());// se ottimizzato
		// assertEquals("{(9, ab¬p) (10, abp) }", actual.toString());//Non ottimizzato

		actual.clear();
		actual.put(abp, 10);
		actual.put(abNp, 9);
		actual.put(abUp, 8);
		assertEquals("Test about simplification with unknown labeled numbers:\n", "{(10, ab) (9, ab¬p) (8, ab¿p) }",
		             actual.toString());// se ottimizzato
		// assertEquals("{(9, ab¬p) (10, abp) (8, ab¿p) }", actual.toString());//Non ottimizzato
	}

	/**
	 * Check the sum of labeled value.
	 */
	@SuppressWarnings("SpellCheckingInspection")
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
		// assertEquals("Test about simplification with unknown labeled infinity:\n", "{(¿p, -∞) }", actual.toString());
	}
}
