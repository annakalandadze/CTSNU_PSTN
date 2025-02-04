package it.univr.di.cstnu.util;

import it.univr.di.labeledvalue.Constants;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class TimeIntervalTest {

	/**
	 *
	 */
	@Test
	public void constructor() {
		assertThrows(IllegalArgumentException.class, () -> new TimeInterval(1, 0));
		assertEquals(Constants.INT_POS_INFINITE, (new TimeInterval(0)).getUpper());
	}

	/**
	 *
	 */
	@Test
	public void testIntersect() {
		final TimeInterval i0 = new TimeInterval(0, 10);
		final TimeInterval i1 = new TimeInterval(1, 10);
		final TimeInterval i0i1 = i0.intersect(i1);
		assertNotNull(i0i1);
		assertEquals(1, i0i1.getLower());
		assertEquals(10, i0i1.getUpper());

		final TimeInterval i9 = new TimeInterval(0, 9);
		final TimeInterval i0i9 = i0.intersect(i9);
		assertNotNull(i0i9);
		assertEquals(0, i0i9.getLower());
		assertEquals(9, i0i9.getUpper());

//		final TimeInterval i19 = new TimeInterval(1, 9);
		final TimeInterval i1i9 = i1.intersect(i9);
		assertNotNull(i1i9);
		assertEquals(1, i1i9.getLower());
		assertEquals(9, i1i9.getUpper());

		final TimeInterval i12 = new TimeInterval(11, 12);
		final TimeInterval i0i12 = i0.intersect(i12);
		assertNull(i0i12);
	}

	/**
	 *
	 */
	@Test
	public void set() {
		final TimeInterval i0 = new TimeInterval(0, 10);
		i0.set(2, 9);
		assertEquals(2, i0.getLower());
		assertEquals(9, i0.getUpper());
		assertThrows(IllegalArgumentException.class, () -> i0.set(2, 1));
	}

	/**
	 *
	 */
	@Test
	public void testToString() {
		final TimeInterval i0 = new TimeInterval(0);
		assertEquals("[0, ∞]", i0.toString());
	}
}
