/**
 *
 */
package it.univr.di.cstnu.graph;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
//import org.xml.sax.SAXException;
import org.xml.sax.SAXException;

/**
 * @author posenato
 *
 */
public class TNGraphTest {


	/**
	 *
	 */
	static File fileSTNU = new File("src/test/resources/dc_500nodes_050ctgs_5lanes_001_SQRT_CTG_DENSE.stnu");
	/**
	 *
	 */
	TNGraphMLReader<STNUEdge> readerSTNU;

	/**
	 *
	 */
	TNGraph<STNUEdge> stnu;



	/**
	 * Check the method getEdgeCount()
	 * @throws IOException if any error
	 * @throws ParserConfigurationException if any error
	 * @throws SAXException if any error.
	 */
	@Test
	public void testSTNUGetEdgeCount() throws Exception {
		this.readerSTNU = new TNGraphMLReader<>();
		this.stnu = this.readerSTNU.readGraph(fileSTNU, STNUEdgeInt.class);
		assertEquals(2254, this.stnu.getEdgeCount());

	}

}
