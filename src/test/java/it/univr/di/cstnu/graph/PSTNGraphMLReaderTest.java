package it.univr.di.cstnu.graph;

import it.univr.di.cstnu.algorithms.PSTN;
import it.univr.di.cstnu.algorithms.STNU;
import it.univr.di.labeledvalue.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Allows to read a TNGraph from a file written in GraphML format.<br> GraphML format allows the definition of different
 * attributes for the tNGraph, vertices and edges.<br> All attributes are defined in the first part of a GraphML file.
 * Examples of GraphML file that can read by this class are given in the Instances directory under CstnuTool one.
 *
 * @author posenato
 * @version $Id: $Id
 */
public class PSTNGraphMLReaderTest {

    /**
     *
     */
    static File filePSTN = new File("src/test/resources/testGraphML.pstn");
    /**
     *
     */
    TNGraphMLReader<STNUEdge> readerPSTN;

    /**
     *
     */
    TNGraph<STNUEdge> pstn;

    PSTN myPSTN;

    /**
     * @throws IOException                  none
     * @throws ParserConfigurationException none
     * @throws SAXException                 none
     */
    @Test
    public void testPSTN() throws Exception {
        this.readerPSTN = new TNGraphMLReader<>();
        this.pstn = this.readerPSTN.readGraph(filePSTN, STNUEdgeInt.class);
        Assert.assertEquals(-5, this.pstn.getEdge("YX").getLabeledValue());
        Assert.assertEquals(2, this.pstn.getEdge("XY").getLabeledValue());
        final TNGraphMLWriter writer = new TNGraphMLWriter(null);
        final String graphXML = writer.save(this.pstn);
        this.readerPSTN = new TNGraphMLReader<>();
        this.pstn = this.readerPSTN.readGraph(graphXML, STNUEdgeInt.class);
        myPSTN = new PSTN(this.pstn);
        STNU approx = myPSTN.buildApproxSTNU().approximatingSTNU;
        Assert.assertNotNull(approx);
        Assert.assertEquals(-5, approx.getG().getEdge("YX").getLabeledValue());
        Assert.assertEquals(-5, this.pstn.getEdge("YX").getLabeledValue());
        Assert.assertEquals(2, this.pstn.getEdge("XY").getLabeledValue());
    }

    /**
     * @throws IOException                  none
     * @throws ParserConfigurationException none
     * @throws SAXException                 none
     */
    @Test
    public void testSTNU1() throws Exception {
        this.readerPSTN = new TNGraphMLReader<>();
        this.pstn = this.readerPSTN.readGraph(filePSTN, STNUEdgeInt.class);
        Assert.assertEquals(2, this.pstn.getEdgeCount());
    }

    /**
     * @throws IOException                  none
     * @throws ParserConfigurationException none
     * @throws SAXException                 none
     */
    @Test
    public void testSTNU33() throws Exception {
        this.readerPSTN = new TNGraphMLReader<>();
        this.pstn = this.readerPSTN.readGraph(filePSTN, STNUEdgeInt.class);
        Assert.assertEquals(Constants.INT_NULL, this.pstn.getEdge("YX").getValue());
        Assert.assertEquals(Constants.INT_NULL, this.pstn.getEdge("XY").getValue());
    }

    /**
     * @throws Exception if the file cannot read.
     */
    @Test
    public void testGraphAttribute() throws Exception {
        this.readerPSTN = new TNGraphMLReader<>();
        this.pstn = this.readerPSTN.readGraph(filePSTN, STNUEdgeInt.class);
        Assert.assertEquals("testGraphML", this.pstn.getName());
    }
}
