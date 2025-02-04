package it.univr.di.cstnu.graph;

import it.univr.di.cstnu.graph.Edge.ConstraintType;
import it.univr.di.labeledvalue.ALabel;
import it.univr.di.labeledvalue.Label;
import it.univr.di.labeledvalue.LabeledALabelIntTreeMap;
import it.univr.di.labeledvalue.Literal;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
/**
 * test for  CSTNUGraphMLWriter
 * @author posenato
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeStatic"})
public class CSTNUGraphMLWriterTest {

	private final String fileName = "src/test/resources/testGraphML.cstnu";

	private final String fileOk = """
			<?xml version="1.0" encoding="UTF-8"?>
			<graphml xmlns="http://graphml.graphdrawing.org/xmlns/graphml"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \s
			xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/graphml">
			<key id="nContingent" for="graph">
			<desc>Number of contingents in the graph</desc>
			<default>0</default>
			</key>
			<key id="nObservedProposition" for="graph">
			<desc>Number of observed propositions in the graph</desc>
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
			<key id="Obs" for="node">
			<desc>Proposition Observed. Value specification: [a-zA-F]</desc>
			<default></default>
			</key>
			<key id="x" for="node">
			<desc>The x coordinate for the visualization. A positive value.</desc>
			<default>0</default>
			</key>
			<key id="Label" for="node">
			<desc>Label. Format: [¬[a-zA-F]|[a-zA-F]]+|⊡</desc>
			<default>⊡</default>
			</key>
			<key id="y" for="node">
			<desc>The y coordinate for the visualization. A positive value.</desc>
			<default>0</default>
			</key>
			<key id="Potential" for="node">
			<desc>Labeled Potential Values. Format: {[('node name (no case modification)', 'integer', 'label') ]+}|{}</desc>
			<default></default>
			</key>
			<key id="Type" for="edge">
			<desc>Type: Possible values: contingent|requirement|derived|internal.</desc>
			<default>requirement</default>
			</key>
			<key id="LowerCaseLabeledValues" for="edge">
			<desc>Labeled Lower-Case Values. Format: {[('node name (no case modification)', 'integer', 'label') ]+}|{}</desc>
			<default></default>
			</key>
			<key id="UpperCaseLabeledValues" for="edge">
			<desc>Labeled Upper-Case Values. Format: {[('node name (no case modification)', 'integer', 'label') ]+}|{}</desc>
			<default></default>
			</key>
			<key id="LabeledValues" for="edge">
			<desc>Labeled Values. Format: {[('integer', 'label') ]+}|{}</desc>
			<default></default>
			</key>
			<graph edgedefault="directed">
			<data key="nContingent">1</data>
			<data key="nObservedProposition">1</data>
			<data key="NetworkType">CSTNU</data>
			<data key="nEdges">2</data>
			<data key="nVertices">4</data>
			<data key="Name">testGraphML.cstnu</data>
			<node id="Z">
			<data key="x">100.0</data>
			<data key="Label">⊡</data>
			<data key="y">60.0</data>
			</node>
			<node id="X">
			<data key="Obs">p</data>
			<data key="x">100.0</data>
			<data key="Label">⊡</data>
			<data key="y">160.0</data>
			</node>
			<node id="Ω">
			<data key="x">400.0</data>
			<data key="Label">⊡</data>
			<data key="y">60.0</data>
			</node>
			<node id="Y">
			<data key="x">400.0</data>
			<data key="Label">¬p</data>
			<data key="y">160.0</data>
			</node>
			<edge id="YX" source="Y" target="X">
			<data key="Type">contingent</data>
			<data key="UpperCaseLabeledValues">{(Y, -5, ¬p) }</data>
			<data key="LabeledValues">{}</data>
			</edge>
			<edge id="XY" source="X" target="Y">
			<data key="Type">contingent</data>
			<data key="LowerCaseLabeledValues">{(Y, 2, ¬p) }</data>
			<data key="LabeledValues">{}</data>
			</edge>
			</graph>
			</graphml>""";

	/**
	 * testGraphMLWriterAbstractLayoutOfLabeledNodeLabeledIntEdge
	 *
	 * @throws IOException clear
	 */
	@Test
	public void testGraphMLWriterAbstractLayoutOfLabeledNodeLabeledIntEdge() throws IOException {
		final Label p = Label.valueOf('p', Literal.NEGATED);

		final TNGraph<CSTNUEdge> g = new TNGraph<>("testGraphML.cstnu", CSTNUEdgePluggable.class);
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		Z.setX(100);
		Z.setY(60);
		final LabeledNode Ω = LabeledNodeSupplier.get("Ω");
		Ω.setX(400);
		Ω.setY(60);

		final LabeledNode X = LabeledNodeSupplier.get("X");
		X.setX(100);
		X.setY(160);
		X.setObservable('p');

		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		Y.setLabel(p);
		Y.setX(400);
		Y.setY(160);

		final CSTNUEdge xy = new CSTNUEdgePluggable("XY");
		xy.setLowerCaseValue(p, new ALabel("Y", g.getALabelAlphabet()), 2);
		xy.setConstraintType(ConstraintType.contingent);
		final CSTNUEdge yx = new CSTNUEdgePluggable("YX");
		final LabeledALabelIntTreeMap uc = new LabeledALabelIntTreeMap(null);
		uc.mergeTriple(p, new ALabel("Y", g.getALabelAlphabet()), -5);
		yx.setUpperCaseValueMap(uc);
		yx.setConstraintType(ConstraintType.contingent);

		g.addVertex(Z);
		g.addVertex(Ω);
		g.addVertex(X);
		g.addVertex(Y);
		g.addEdge(xy, X, Y);
		g.addEdge(yx, Y, X);

		// A test should not depend on other class! :-)
		// CSTNU cstnu = new CSTNU(g);
		// cstnu.initAndCheck();
		final TNGraphMLWriter graphWriter = new TNGraphMLWriter(null);
		graphWriter.save(g, new File(fileName));
		try (final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) { // don't use new FileReader(this.fileName)
			// because for Java 8 it does not accept "UTF-8"
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
