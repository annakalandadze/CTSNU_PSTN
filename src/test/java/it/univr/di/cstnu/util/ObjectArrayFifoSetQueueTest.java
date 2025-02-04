/**
 *
 */
package it.univr.di.cstnu.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author posenato
 *
 */
public class ObjectArrayFifoSetQueueTest {

	/**
	 *
	 */
	public ObjectArrayFIFOSetQueue<String> queue;

	/**
	 * @throws java.lang.Exception  none
	 */
	@Before
	public void setUp() throws Exception {
		queue = new ObjectArrayFIFOSetQueue<>();
		queue.enqueue("A");
		queue.enqueue("B");
		queue.enqueue("C");
		queue.enqueue("D");
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#clear()}.
	 */
	@Test
	public void testClear() {
		queue.clear();
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#comparator()}.
	 */
	@Test
	public void testComparator() {
		assertNull("Compararotor", queue.comparator());

	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#contains(java.lang.Object)}.
	 */
	@Test
	public void testContains() {
		assertTrue(queue.contains("C"));
		assertFalse(queue.contains("c"));
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#dequeue()}.
	 */
	@Test
	public void testDequeue() {
		queue.dequeue();
		assertEquals("[B, C, D]", queue.toString());
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#dequeueLast()}.
	 */
	@Test
	public void testDequeueLast() {
		queue.dequeueLast();
		assertEquals("[A, B, C]", queue.toString());
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#enqueue(java.lang.Object)}.
	 */
	@Test
	public void testEnqueue() {
		queue.enqueue("E");
		assertEquals("[A, B, C, D, E]", queue.toString());
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#enqueueFirst(java.lang.Object)}.
	 */
	@Test
	public void testEnqueueFirst() {
		queue.enqueueFirst("E");
		assertEquals("[E, A, B, C, D]", queue.toString());
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#first()}.
	 */
	@Test
	public void testFirst() {
		assertEquals("A", queue.first());
		assertEquals("[A, B, C, D]", queue.toString());
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#isEmpty()}.
	 */
	@Test
	public void testIsEmpty() {
		assertFalse(queue.isEmpty());
		queue.remove("D");
		queue.remove("C");
		queue.remove("B");
		queue.remove("A");
		assertTrue(queue.isEmpty());
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#last()}.
	 */
	@Test
	public void testLast() {
		assertEquals("D", queue.last());
		assertEquals("[A, B, C, D]", queue.toString());
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#size()}.
	 */
	@Test
	public void testSize() {
		assertEquals(4, queue.size());
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#toArray()}.
	 */
	@Test
	public void testToArray() {
		final String[] s = queue.toArray(new String[0]);
		assertEquals("[A, B, C, D]", Arrays.toString(s));
	}

	/**
	 * Test method for {@link ObjectArrayFIFOSetQueue#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("[A, B, C, D]", queue.toString());
	}

}
