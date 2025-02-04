/**
 *
 */
package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.graph.CSTNUEdge;
import it.univr.di.cstnu.graph.CSTNUEdgePluggable;
import it.univr.di.cstnu.graph.TNGraph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author posenato
 */
public class CSTNU2CSTNTest {
	/**
	 *
	 */
	static final Class<? extends CSTNUEdge> edgeImplClass = CSTNUEdgePluggable.class;
	/**
	 *
	 */
	TNGraph<CSTNUEdge> g;

	/**
	 * @throws java.lang.Exception  none
	 */
	@Before
	public void setUp() throws Exception {
		this.g = new TNGraph<>("",edgeImplClass);

	}

	/**
	 */
	@Test
	public void testCSTNU2CSTNLabeledIntGraphInt() {

		final CSTNU2CSTN checker = new CSTNU2CSTN(this.g, 100);

		assertEquals(checker.timeOut, 100);
	}

}
