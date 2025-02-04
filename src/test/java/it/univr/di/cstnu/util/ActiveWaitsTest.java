package it.univr.di.cstnu.util;

import it.univr.di.cstnu.graph.LabeledNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Simple test
 */
public class ActiveWaitsTest {

	ActiveWaits actWait;

	/**
	 * Test for addWait
	 */
	@Test
	public void addWait() {
		final LabeledNode C = new LabeledNode("C");
		this.actWait.addWait(C, 10);
		assertEquals(10, this.actWait.getMaximum());
		this.actWait.addWait(C, 11);
		assertEquals(11, this.actWait.getMaximum());
		this.actWait.addWait(new LabeledNode("D"), 11);
		assertEquals(11, this.actWait.getMaximum());
		this.actWait.addWait(new LabeledNode("E"), 10);
		assertEquals(11, this.actWait.getMaximum());
	}

	/**
	 * Test for remove
	 */
	@Test
	public void remove() {
		final LabeledNode C = new LabeledNode("C");
		this.actWait.addWait(C, 10);
		this.actWait.addWait(C, 11);
		boolean b = this.actWait.remove(C);
		assertEquals(0, this.actWait.size());
		assertTrue(b);
		final LabeledNode D = new LabeledNode("D");
		final LabeledNode E = new LabeledNode("E");
		this.actWait.addWait(D, 11);
		this.actWait.addWait(E, 10);
		this.actWait.remove(D);
		assertEquals(1, this.actWait.size());
		b = this.actWait.remove(D);
		assertFalse(b);
		this.actWait.remove(E);
		this.actWait.remove(E);
		assertFalse(b);
	}

	/**
	 * Setup
	 *
	 * @throws Exception not possible
	 */
	@Before
	public void setUp() throws Exception {
		this.actWait = new ActiveWaits();
	}

	/**
	 * Test for to string
	 */
	@Test
	public void testToString() {
		final LabeledNode C = new LabeledNode("C");
		this.actWait.addWait(C, 10);
		this.actWait.addWait(C, 11);
		this.actWait.addWait(new LabeledNode("D"), 11);
		this.actWait.addWait(new LabeledNode("E"), 1);
		assertEquals("[11->❮C❯, 11->❮D❯, 1->❮E❯]", this.actWait.toString());
	}
}
