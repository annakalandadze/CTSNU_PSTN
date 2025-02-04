/**
 *
 */
package it.univr.di.labeledvalue;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author posenato
 */
public class LabeledIntSimpleMapTest {

	/**
	 *
	 */
	LabeledIntSimpleMap map;

	/**
	 * @throws Exception  nope
	 */
	@Before
	public void setUp() throws Exception {
		map = new LabeledIntSimpleMap();
	}

	/**
	 */
	@Test
	public void testEntrySet() {
		map.put(Label.emptyLabel, 0);
		map.put(Label.parse("a"), -1);
		map.put(Label.parse("¬a"), -1);
		map.put(Label.parse("b"), -1);

		assertEquals("{(0, ⊡) (-1, a) (-1, ¬a) (-1, b) }", map.toString());
		final ObjectSet<Entry<Label>> entrySet = map.entrySet();
		assertEquals("{b=>-1, ¬a=>-1, a=>-1, ⊡=>0}", entrySet.toString());

		map.remove(Label.parse("¬a"));
		assertEquals("{b=>-1, null=>-1, a=>-1, ⊡=>0}", entrySet.toString());

		entrySet.remove(new AbstractObject2IntMap.BasicEntry<>(Label.parse("a"), -1));
		assertEquals("{(0, ⊡) (-1, a) (-1, b) }", map.toString());
		assertEquals("{b=>-1, null=>-1, ⊡=>0}", entrySet.toString());
	}

	/**
	 *
	 */
	@Test
	public void testKeySet() {
		map.put(Label.emptyLabel, 0);
		map.put(Label.parse("a"), -1);
		map.put(Label.parse("¬a"), -1);
		map.put(Label.parse("b"), -1);

		assertEquals("{(0, ⊡) (-1, a) (-1, ¬a) (-1, b) }", map.toString());
		final ObjectSet<Label> entrySet = map.keySet();
		assertEquals("{b, ¬a, a, ⊡}", entrySet.toString());

		map.remove(Label.parse("¬a"));
		assertEquals("{b, ¬a, a, ⊡}", entrySet.toString());
	}

	/**
	 *
	 */
	@Test
	public void testRemoveEntrySet() {
		map.put(Label.emptyLabel, 0);
		map.put(Label.parse("a"), -1);
		map.put(Label.parse("¬a"), -1);
		map.put(Label.parse("b"), -1);
		map.put(Label.parse("¬b"), -2);

		assertEquals("{(0, ⊡) (-1, a) (-1, ¬a) (-1, b) (-2, ¬b) }", map.toString());
		ObjectSet<Entry<Label>> entrySet = map.entrySet();
		assertEquals("{b=>-1, ¬b=>-2, ¬a=>-1, a=>-1, ⊡=>0}", entrySet.toString());

		var i = 0;
		entrySet = map.entrySet();
		for (final Entry<Label> entry : entrySet) {
			final Label l = entry.getKey();
			map.remove(l);
			if (i == 0) {
				assertEquals("b", l.toString());
				assertEquals("{null=>-1, ¬b=>-2, ¬a=>-1, a=>-1, ⊡=>0}", entrySet.toString());
				assertEquals("{(0, ⊡) (-1, a) (-1, ¬a) (-2, ¬b) }", map.toString());
				i++;
				continue;
			}
			if (i == 1) {
				assertEquals("¬b", l.toString());
				assertEquals("{null=>-1, null=>-2, ¬a=>-1, a=>-1, ⊡=>0}", entrySet.toString());
				assertEquals("{(0, ⊡) (-1, a) (-1, ¬a) }", map.toString());
				i++;
				continue;
			}
			if (i == 2) {
				assertEquals("¬a", l.toString());
				assertEquals("{null=>-1, null=>-2, null=>-1, a=>-1, ⊡=>0}", entrySet.toString());
				assertEquals("{(0, ⊡) (-1, a) }", map.toString());
				i++;
				continue;
			}
			if (i == 3) {
				assertEquals("a", l.toString());
				assertEquals("{null=>-1, null=>-2, null=>-1, null=>-1, ⊡=>0}", entrySet.toString());
				assertEquals("{(0, ⊡) }", map.toString());
				i++;
				continue;
			}
			if (i == 4) {
				assertEquals(Constants.EMPTY_LABELstring, l.toString());
				assertEquals("{null=>-1, null=>-2, null=>-1, null=>-1, null=>0}", entrySet.toString());
				assertEquals("{}", map.toString());
				i++;
			}
		}
	}

}
