package it.univr.di.cstnu.util;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.univr.di.cstnu.graph.LabeledNode;
import it.univr.di.labeledvalue.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ExtendedPriorityQueueTest {

	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private ExtendedPriorityQueue<String> minQueue, maxQueue;
	private ExtendedPriorityQueue<LabeledNode> heap;
	private LabeledNode A, B, C;

	/**
	 * @throws Exception not possible
	 */
	@Before
	public void setUp() throws Exception {
		minQueue = new ExtendedPriorityQueue<>();
		maxQueue = new ExtendedPriorityQueue<>(true);
		minQueue.insertOrUpdate("10", 10);
		minQueue.insertOrUpdate("9", 9);
		minQueue.insertOrUpdate("8", 8);
		minQueue.insertOrUpdate("4", 4);
		minQueue.insertOrUpdate("3", 3);
		//
		maxQueue.insertOrUpdate("i1", 1);
		maxQueue.insertOrUpdate("i-1", -1);
		maxQueue.insertOrUpdate("i3", 3);
		maxQueue.insertOrUpdate("i6", 6);
		maxQueue.insertOrUpdate("i10", 10);
		//
		heap = new ExtendedPriorityQueue<>();
		A = new LabeledNode("A");
		B = new LabeledNode("B");
		C = new LabeledNode("C");
		heap.insertOrUpdate(A, 0);
		heap.insertOrUpdate(B, 1);
		heap.insertOrUpdate(C, -1);
	}


	/**
	 *
	 */
	@Test
	public void testInsertAPreviousNode() {
		//remove and reinsert "3" from the min queue
		final String three = minQueue.extractFirst();
		assertEquals("3", three);
		final boolean update = minQueue.insertOrUpdate(three, 4);
		assertTrue(update);
	}

	/**
	 *
	 */
	@Test
	public void clear() {
		minQueue.clear();
		assertEquals(0, minQueue.size());
	}

	/**
	 *
	 */
	@Test
	public void delete() {
		minQueue.delete("8");
		minQueue.delete("8");
		assertEquals("[3->3, 4->4, 9->9, 10->10]", minQueue.toString());
	}

	/**
	 *
	 */
	@Test
	public void extractMin() {
		assertEquals("3", minQueue.extractFirst());
		assertEquals("4", minQueue.extractFirst());
	}

	/**
	 *
	 */
	@Test
	public void extractMinEntry() {
		assertEquals("3", minQueue.extractFirst());
		assertEquals("4", minQueue.extractFirst());

	}

	/**
	 * Simple test
	 */
	@Test
	public void getElements() {
		assertEquals("3", minQueue.extractFirst());
		assertEquals("4", minQueue.extractFirst());
	}

	/**
	 *
	 */
	@SuppressWarnings("DataFlowIssue")
	@Test
	public void getFirst() {
		assertEquals("3", minQueue.getFirstEntry().getValue());
		assertEquals("3", minQueue.getFirstEntry().getValue());
		assertEquals("i10", maxQueue.getFirstEntry().getValue());
	}

	/**
	 *
	 */
	@Test
	public void getStatus() {
		assertEquals(ExtendedPriorityQueue.Status.isPresent, minQueue.getStatus("3"));
		assertEquals(ExtendedPriorityQueue.Status.neverPresent, minQueue.getStatus("3a"));
		assertEquals("3", minQueue.extractFirst());
		assertEquals(ExtendedPriorityQueue.Status.wasPresent, minQueue.getStatus("3"));
	}

	/**
	 *
	 */
	@SuppressWarnings("DataFlowIssue")
	@Test
	public void insertOrUpdate() {
		boolean s;
		assertEquals(3, minQueue.getFirstEntry().getKey().intValue());
		s = minQueue.insertOrUpdate("10", 0);
		assertEquals(0, minQueue.getFirstEntry().getKey().intValue());
		assertTrue(s);

		s = maxQueue.insertOrUpdate("i6", 16);
		assertTrue(s);
		assertEquals(16, maxQueue.getFirstEntry().getKey().intValue());
		s = maxQueue.insertOrUpdate("i6", 16);
		assertEquals(16, maxQueue.getFirstEntry().getKey().intValue());
		assertTrue(s);
	}

	/**
	 *
	 */
	@Test
	public void isEmpty() {
		minQueue.clear();
		assertTrue(minQueue.isEmpty());
	}

	/**
	 *
	 */
	@Test
	public void size() {
		assertEquals(5, minQueue.size());
	}


	/**
	 * Test method for {@link ExtendedPriorityQueue#insertOrUpdate(Object, int)}.
	 */
	@Test
	public void testAdd() {
		heap.insertOrUpdate(A, 0);
		// this.heap.add(this.A, 1);
		// this.heap.add(this.A, 2);
		heap.insertOrUpdate(B, 1);
		heap.insertOrUpdate(C, -1);
		assertEquals("First element:", -1, heap.extractFirstEntry().getIntValue());
		assertEquals("Second element:", 0, heap.extractFirstEntry().getIntValue());
		assertEquals("Third element:", 1, heap.extractFirstEntry().getIntValue());
		assertEquals("Priority of A:", 0, heap.getPriority(A));
		assertEquals("Status of A:", "wasPresent", heap.getStatus(A).toString());
	}

	/**
	 * Test method for {@link ExtendedPriorityQueue#clear()}.
	 */
	@Test
	public void testClear() {
		testAdd();
		heap.clear();
		assertEquals("Cancellazione elementi:", "[]", heap.toString());
		assertEquals("Aggiunta elementi:", "{}", heap.getAllDeterminedPriorities().toString());
		assertEquals("Aggiunta elementi:", "neverPresent", heap.getStatus(A).toString());
	}

	/**
	 * Test method for {@link ExtendedPriorityQueue#extractFirst()}.
	 */
	@Test
	public void testExtractMin() {
		testDecreasePriority();
		final LabeledNode min = heap.extractFirst();
		assertEquals("testExtractMin:", "[-1->❮C❯, 0->❮A❯]", heap.toString());
		assertEquals("min: ", "❮B❯", min.toString());
		assertEquals("Aggiunta elementi:", "wasPresent", heap.getStatus(B).toString());
	}

	/**
	 *
	 */
	@SuppressWarnings("DataFlowIssue")
	@Test
	public void testDecreasePriority() {
		heap.insertOrUpdate(B, -10);
		assertEquals("Decrease priority:", -10, heap.getFirstEntry().getKey().intValue());
		assertEquals("Aggiunta elementi:", "isPresent", heap.getStatus(B).toString());
		assertEquals("Decrease priority:", -10, heap.getPriority(B));
	}

	/**
	 * Test method for {@link ExtendedPriorityQueue#extractFirstEntry()}.
	 */
	@Test
	public void testExtractMinEntry() {
		testDecreasePriority();
		final AbstractObject2IntMap.BasicEntry<LabeledNode> min = heap.extractFirstEntry();
		assertEquals("testExtractMin:", "[-1->❮C❯, 0->❮A❯]", heap.toString());
		assertEquals("min: ", "❮B❯", min.getKey().toString());
		assertEquals("min: ", -10, min.getIntValue());
		assertEquals("Aggiunta elementi:", "wasPresent", heap.getStatus(B).toString());
	}

	/**
	 * Test method for {@link ExtendedPriorityQueue#getAllDeterminedPriorities()}.
	 */
	@Test
	public void testGetAllDeterminedPriorities() {
		assertEquals("testGetPriorities", 0, heap.getAllDeterminedPriorities().getInt(A));
		heap.insertOrUpdate(B, -1);
		heap.extractFirst();
		heap.insertOrUpdate(B, -2);
		heap.extractFirst();
		heap.insertOrUpdate(B, -3);
		assertEquals(-1, heap.getPriority(C));
		assertEquals(0, heap.getPriority(A));
		assertEquals(-2, heap.getPriority(B));
	}

	/**
	 * Test method for {@link ExtendedPriorityQueue#getPriority(Object)}.
	 */
	@Test
	public void testGetPriority() {
		testDecreasePriority();
		int p = heap.getPriority(B);
		assertEquals("priority: ", -10, p);
		assertEquals("Aggiunta elementi:", "isPresent", heap.getStatus(B).toString());
		p = heap.getPriority(new LabeledNode("no"));
		assertEquals("priority di un elemento mai inserito: ", Constants.INT_POS_INFINITE, p);
	}

	/**
	 * Test method for {@link ExtendedPriorityQueue#getStatus(Object)}.
	 */
	@Test
	public void testGetStatus() {
		assertEquals("status", "isPresent", heap.getStatus(A).toString());
		assertEquals("status", "isPresent", heap.getStatus(C).toString());
		heap.extractFirstEntry();
		heap.extractFirstEntry();
		assertEquals("status", "wasPresent", heap.getStatus(A).toString());
		assertEquals("status", "wasPresent", heap.getStatus(C).toString());
	}

	/**
	 * Test method for {@link ExtendedPriorityQueue#isEmpty()}.
	 */
	@Test
	public void testIsEmpty() {
		assertFalse(heap.isEmpty());
		heap.clear();
		assertTrue(heap.isEmpty());
	}

	/**
	 * Test method for {@link ExtendedPriorityQueue#size()}.
	 */
	@Test
	public void testSize() {
		assertEquals("status", 3, heap.size());
		heap.clear();
		assertEquals("status", 0, heap.size());
	}

}
