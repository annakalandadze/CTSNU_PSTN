/**
 *
 */
package it.univr.di.labeledvalue;

import it.univr.di.labeledvalue.ALabelAlphabet.ALetter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class ALabelTest {

	private ALabel a, b, c, e;
	/**
	 *
	 */
	ALabelAlphabet alpha;

	/**
	 * @throws java.lang.Exception if any error
	 */
	@Before
	public void setUp() throws Exception {
		this.alpha = new ALabelAlphabet();
		this.a = new ALabel(new ALetter("A"), this.alpha);
		this.b = new ALabel(new ALetter("b"), this.alpha);
		this.c = new ALabel(new ALetter("C"), this.alpha);
		this.e = ALabel.emptyLabel;
	}

	/**
	 *
	 */
	@Test
	public void creation() {
		assertEquals("A", this.a.toString());
		assertEquals("b", this.b.toString());
		assertEquals("C", this.c.toString());
		assertEquals("◇", this.e.toString());
	}

	/**
	 *
	 */
	@Test
	public void getALetter() {
		assertEquals("A", this.a.getALetter().toString());
	}

	/**
	 * @param args args
	 */
	public static void main(String[] args) {
		final ALabelAlphabet alpha = new ALabelAlphabet();
		ALabel a = new ALabel(new ALetter("A"), alpha);
		System.out.println("ALabel A: " + a);

		final ALabel b = new ALabel(new ALetter("b"), a.getAlphabet());
		System.out.println("ALabel b: " + b);

		final ALabel c = new ALabel(new ALetter("C"), b.getAlphabet());
		System.out.println("ALabel C: " + c);

		System.out.println("ALabel b: " + b);

		a = a.conjunction(b);
		System.out.println("ALabel dopo conjunction con b [Ab]: " + a);

		final ALabel e = ALabel.emptyLabel;
		System.out.println("Empty label: " + e);

		a = a.conjunction(e);
		System.out.println("ALabel dopo conjunction con empty [Ab]: " + a);

		System.out.println("Empty conjunction con Ab [Ab]: " + e.conjunction(a));

		a = a.conjunction(a);
		System.out.println("ALabel dopo conjunction con a [Ab]: " + a);

		a = a.conjunction(c);
		System.out.println("ALabel dopo conjunction con C [AbC]: " + a);

		a.remove(new ALetter("C"));
		System.out.println("ALabel dopo rimozione C [Ab]: " + a);

		System.out.println("ALabel contiene 'b' [true]: " + a.contains(new ALetter("b")));
		System.out.println("ALabel contiene 'C' [false]: " + a.contains(new ALetter("C")));

		System.out.println("ALabel confronto con 'C' [<0]: " + a.compareTo(c));
		//noinspection EqualsWithItself
		System.out.println("ALabel confronto a [0]: " + a.compareTo(a));
		System.out.println("C confronto con 'A∙b' [>0]: " + c.compareTo(a));
		a.conjoin(new ALetter("C"));
		System.out.println("ALabel A: " + a);
		for (final ALetter l : a) {
			System.out.println("Letter: " + l);
		}

		System.out.println("An intersect with empty: " + a.intersect(ALabel.emptyLabel));
		System.out.println("An intersect with a: " + a.intersect(a));
		System.out.println("An intersect with b: " + a.intersect(b));
		System.out.println("An intersect with c: " + a.intersect(c));
		System.out.println("An intersect with 'A': " + a.intersect(new ALabel("A", a.getAlphabet())));

	}

	/**
	 *
	 */
	@Test
	public void conjunct() {
		this.a.conjoin(new ALetter("x"));
		assertEquals("A∙x", this.a.toString());

		this.a.conjoin(new ALetter("y"));
		assertEquals("A∙x∙y", this.a.toString());
	}

	/**
	 *
	 */
	@Test
	public void remove() {
		this.a = this.a.conjunction(this.b).conjunction(this.c);
		this.a.remove(new ALetter("C"));
		assertEquals("A∙b", this.a.toString());
		this.e.remove((ALetter) null);
		assertEquals("◇", this.e.toString());
	}

	/**
	 *
	 */
	@Test
	public void contains() {
		this.a = this.a.conjunction(this.b);
		assertTrue(this.a.contains(new ALetter("b")));
		assertTrue(this.a.contains(this.b));
		assertTrue(this.a.contains(ALabel.emptyLabel));
		assertTrue(this.a.contains((ALabel) null));
		assertTrue(this.a.contains((ALetter) null));
		assertFalse(this.a.contains(new ALetter("C")));
		assertFalse(this.a.contains(this.c.conjunction(this.b)));

	}

	/**
	 *
	 */
	@SuppressWarnings("EqualsWithItself")
	@Test
	public void compare() {
		assertTrue(this.a.compareTo(this.c) < 0);
		assertEquals(0, this.a.compareTo(this.a));
		assertTrue(this.c.compareTo(this.a) > 0);
	}

	/**
	 *
	 */
	@Test
	public void array() {
		this.a = this.a.conjunction(this.c);
		assertEquals("A∙C", this.a.toString());
		final ALetter[] a1 = new ALetter[3];
		int i = 0;
		for (final ALetter l : this.a) {
			a1[i++] = l;
		}
		assertEquals("C", a1[1].toString());
	}

	/**
	 *
	 */
	@Test
	public void intersect() {
		this.a = this.a.conjunction(this.c);
		assertEquals("◇", this.a.intersect(this.e).toString());
		assertEquals(this.a, this.a.intersect(this.a));
		assertNotEquals(this.b, this.a.intersect(this.b));
		assertEquals(this.c, this.a.intersect(this.c));
		assertEquals("A", this.a.intersect(new ALabel("A", this.a.getAlphabet())).toString());
	}

	/**
	 *
	 */
	@Test
	public final void parse() {
		this.alpha.clear();

		ALabel n9 = ALabel.parse("N9" + ALabel.ALABEL_SEPARATORstring + "N12" + ALabel.ALABEL_SEPARATORstring + "N13", this.alpha);

		// System.out.printf("Map da parse: %s\n", map);

		ALabel n9ok = new ALabel("N9", this.alpha);
		n9ok.conjoin(new ALetter("N12"));
		n9ok.conjoin(new ALetter("N13"));

		assertEquals("Check of parse method", n9ok, n9);

		n9 = ALabel.parse("", this.alpha);
		n9ok = ALabel.emptyLabel;
		assertEquals("Check of parse method", n9ok, n9);

	}

	/**
	 *
	 */
	@Test
	public final void count() {
		this.alpha.clear();

		assertEquals(0, ALabel.emptyLabel.size());

		ALabel n9 = ALabel.parse("N9" + ALabel.ALABEL_SEPARATORstring + "N12" + ALabel.ALABEL_SEPARATORstring + "N13", this.alpha);
		assertEquals(3, n9.size());

		n9.remove(new ALetter("N9"));
		assertEquals(2, n9.size());

		n9 = n9.conjunction(new ALabel("N9", this.alpha));
		assertEquals(3, n9.size());

		n9 = n9.conjunction(new ALabel("N9", this.alpha));
		assertEquals(3, n9.size());

		n9.remove(new ALetter[] { new ALetter("N9"), new ALetter("N12"), new ALetter("N3") });
		assertEquals(1, n9.size());

		n9.remove(new ALetter[] { new ALetter("N9"), new ALetter("N12"), new ALetter("N13") });
		assertEquals(0, n9.size());

	}

	/**
	 *
	 */
	@Test
	public void conjunction() {
		this.a = this.a.conjunction(this.b);
		assertEquals("A∙b", this.a.toString());

		assertEquals("A∙b", this.a.conjunction(this.e).toString());
		assertEquals("A∙b", this.e.conjunction(this.a).toString());
		assertEquals("A∙b", this.a.conjunction(this.a).toString());

		this.a = this.a.conjunction(this.c);
		final String aS = this.a.toString();
		assertEquals("A∙b∙C", this.a.toString());
	}

}
