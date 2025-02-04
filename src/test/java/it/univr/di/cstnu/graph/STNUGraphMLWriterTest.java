package it.univr.di.cstnu.graph;

import it.univr.di.cstnu.graph.Edge.ConstraintType;
import it.univr.di.labeledvalue.ALabelAlphabet.ALetter;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author posenato
 */
@SuppressWarnings("FieldMayBeStatic")
public class STNUGraphMLWriterTest {

	private final String fileName = "src/test/resources/testGraphML.stnu";

	private TNGraph<STNUEdge> g;

	private final String fileOk = """
	                              <?xml version="1.0" encoding="UTF-8"?>
	                              <graphml xmlns="http://graphml.graphdrawing.org/xmlns/graphml"
	                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \s
	                              xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/graphml">
	                              <key id="nContingent" for="graph">
	                              <desc>Number of contingents in the graph</desc>
	                              <default>0</default>
	                              </key>
	                              <key id="NetworkType" for="graph">
	                              <desc>Network Type</desc>
	                              <default>CSTNU</default>
	                              </key>
	                              <key id="nEdges" for="graph">
	                              <desc>Number of edges in the graph</desc>
	                              <default>0</default>
	                              </key>
	                              <key id="nVertices" for="graph">
	                              <desc>Number of vertices in the graph</desc>
	                              <default>0</default>
	                              </key>
	                              <key id="Name" for="graph">
	                              <desc>Graph Name</desc>
	                              <default></default>
	                              </key>
	                              <key id="x" for="node">
	                              <desc>The x coordinate for the visualization. A positive value.</desc>
	                              <default>0</default>
	                              </key>
	                              <key id="y" for="node">
	                              <desc>The y coordinate for the visualization. A positive value.</desc>
	                              <default>0</default>
	                              </key>
	                              <key id="Type" for="edge">
	                              <desc>Type: Possible values: contingent|requirement|derived|internal.</desc>
	                              <default>requirement</default>
	                              </key>
	                              <key id="Value" for="edge">
	                              <desc>Value for STN edge. Format: 'integer'</desc>
	                              <default></default>
	                              </key>
	                              <key id="LabeledValue" for="edge">
	                              <desc>Case Value. Format: 'LC(NodeName):integer' or 'UC(NodeName):integer'</desc>
	                              <default></default>
	                              </key>
	                              <graph edgedefault="directed">
	                              <data key="nContingent">1</data>
	                              <data key="NetworkType">STNU</data>
	                              <data key="nEdges">2</data>
	                              <data key="nVertices">4</data>
	                              <data key="Name">testGraphML</data>
	                              <node id="Z">
	                              <data key="x">0.0</data>
	                              <data key="y">0.0</data>
	                              </node>
	                              <node id="X">
	                              <data key="x">0.0</data>
	                              <data key="y">0.0</data>
	                              </node>
	                              <node id="Ω">
	                              <data key="x">0.0</data>
	                              <data key="y">0.0</data>
	                              </node>
	                              <node id="Y">
	                              <data key="x">0.0</data>
	                              <data key="y">0.0</data>
	                              </node>
	                              <edge id="YX" source="Y" target="X">
	                              <data key="Type">contingent</data>
	                              <data key="LabeledValue">UC(Y):-5</data>
	                              </edge>
	                              <edge id="XY" source="X" target="Y">
	                              <data key="Type">contingent</data>
	                              <data key="LabeledValue">LC(Y):2</data>
	                              </edge>
	                              </graph>
	                              </graphml>""";

	/**
	 * @throws Exception nope
	 */
	@Before
	public void setUp() throws Exception {
		g = new TNGraph<>("testGraphML", STNUEdgeInt.class);
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		final LabeledNode Ω = LabeledNodeSupplier.get("Ω");
		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final STNUEdge xy = new STNUEdgeInt("XY");
		xy.setLabeledValue(new ALetter("Y"), 2, false);
		xy.setConstraintType(ConstraintType.contingent);
		final STNUEdge yx = new STNUEdgeInt("YX");
		yx.setLabeledValue(new ALetter("Y"), -5, true);
		yx.setConstraintType(ConstraintType.contingent);

		g.addVertex(Z);
		g.addVertex(Ω);
		g.addVertex(X);
		g.addVertex(Y);
		g.addEdge(xy, X, Y);
		g.addEdge(yx, Y, X);
	}

	/**
	 * nope
	 */
	@Test
	public void testGraphMLStringWriter() {
		final TNGraphMLWriter graphWriter = new TNGraphMLWriter(null);
		final String graphXML = graphWriter.save(g).trim();
		assertEquals(fileOk, graphXML);
	}

	/**
	 * @throws IOException nope
	 */
	@Test
	public void testGraphMLWriterAbstractLayoutOfLabeledNodeLabeledIntEdge() throws IOException {
		final TNGraphMLWriter graphWriter = new TNGraphMLWriter(null);
		graphWriter.save(g, new File(fileName));
		try (final BufferedReader input = new BufferedReader(
			new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) { // don't use new
			// FileReader(this.fileName) because for Java 8 it does not accept "UTF-8"

			final char[] fileAsChar = new char[4200];
			if (input.read(fileAsChar) == -1) {
				fail("Problem reading " + fileName);
			}
			final String fileAsString = new String(fileAsChar);
			input.close();
			assertEquals(fileOk, fileAsString.trim());
		}
	}
}
