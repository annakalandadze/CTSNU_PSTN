/**
 *
 */
package it.univr.di.cstnu.graph;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class EdgeSupplierTest {

	/**
	 *
	 */
	static EdgeSupplier<STNEdge> stnEdgeFactory = new EdgeSupplier<>(STNEdgeInt.class);
	/**
	 *
	 */
	static EdgeSupplier<CSTNEdge> cstnEdgeFactory = new EdgeSupplier<>(CSTNEdgePluggable.class);
	/**
	 *
	 */
	static EdgeSupplier<CSTNUEdge> cstnuEdgeFactory = new EdgeSupplier<>(CSTNUEdgePluggable.class);

	/**
	 *
	 */
	STNEdge stnEdge;
	/**
	 *
	 */
	CSTNEdge cstnEdge;
	/**
	 *
	 */
	CSTNUEdge cstnuEdge;

	/**
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testEdgeSupplier() {
		assertNotNull(stnEdgeFactory.getEdgeImplClass());
		assertNotNull(cstnEdgeFactory.getEdgeImplClass());
		assertNotNull(cstnuEdgeFactory.getEdgeImplClass());
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.graph.EdgeSupplier#get()}.
	 */
	@Test
	public void testGet() {
		stnEdge = stnEdgeFactory.get();
		cstnEdge = cstnEdgeFactory.get();
		cstnuEdge = cstnuEdgeFactory.get();

		assertNotNull(stnEdge);
		assertTrue(stnEdge.isSTNEdge());
		assertFalse(stnEdge.isCSTNEdge());

		assertNotNull(cstnEdge);
		assertTrue(cstnEdge.isCSTNEdge());
		assertFalse(cstnEdge.isCSTNUEdge());
		assertNotNull(cstnuEdge);
		assertTrue(cstnuEdge.isCSTNUEdge());
		assertTrue(cstnuEdge.isCSTNUEdge());
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.graph.EdgeSupplier#get(java.lang.Class, int)}.
	 */
	@SuppressWarnings({ "static-access", "static-method" })
	@Test
	public void testGetClassOfKInt() {

		final STNEdge[] c = stnEdgeFactory.get(STNEdgeInt.class, 4);
		assertEquals(Arrays.toString(c), "[null, null, null, null]");
		c[1] = stnEdgeFactory.get();
		assertTrue(c[1].isSTNEdge());

		final CSTNEdge[] a = cstnEdgeFactory.get(CSTNEdgePluggable.class, 4);
		assertEquals(Arrays.toString(a), "[null, null, null, null]");
		a[1] = cstnEdgeFactory.get();
		assertTrue(a[1].isCSTNEdge());
		final CSTNUEdge[] b = cstnEdgeFactory.get(CSTNUEdgePluggable.class, 4);
		assertNotNull(b);
		b[3] = cstnuEdgeFactory.get();
		assertTrue(b[3].isCSTNEdge());
		a[2] = b[3];
		assertTrue(a[2].isCSTNEdge());
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.graph.EdgeSupplier#get(java.lang.String)}.
	 */
	@Test
	public void testGetString() {
		stnEdge = stnEdgeFactory.get("Pippo");
		cstnEdge = cstnEdgeFactory.get("Pippo");
		cstnuEdge = cstnuEdgeFactory.get("Pippo");

		assertNotNull(stnEdge);
		assertNotNull(cstnEdge);
		assertNotNull(cstnuEdge);
	}

	/**
	 * Test method for {@link it.univr.di.cstnu.graph.EdgeSupplier#get(it.univr.di.cstnu.graph.Edge)}.
	 */
	@Test
	public void testGetT() {
		stnEdge = stnEdgeFactory.get("Pippo");
		cstnEdge = cstnEdgeFactory.get("Pippo");
		cstnuEdge = cstnuEdgeFactory.get("Pippo");

		final STNEdge stnE1 = stnEdgeFactory.get(stnEdge);
		final CSTNEdge cstnE1 = cstnEdgeFactory.get(cstnEdge);
		final CSTNUEdge cstnuE1 = cstnuEdgeFactory.get(cstnuEdge);

		assertNotEquals(stnEdge, stnE1);
		assertNotEquals(cstnEdge, cstnE1);
		assertNotEquals(cstnuEdge, cstnuE1);
	}
}
