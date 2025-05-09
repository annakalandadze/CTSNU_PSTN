// SPDX-FileCopyrightText: 2020 Roberto Posenato <roberto.posenato@univr.it>
//
// SPDX-License-Identifier: LGPL-3.0-or-later

package it.univr.di.cstnu.algorithms;

import it.univr.di.cstnu.graph.CSTNEdge;
import it.univr.di.cstnu.graph.TNGraph;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Simple class to represent and DC check Conditional Simple Temporal Network (CSTN) where the edge weight are signed
 * integer. The dynamic consistency check (DC check) is done assuming instantaneous reaction DC semantics (cf. ICAPS
 * 2016 paper, table 1) and using LP, R0, qR0, R3*, and qR3* rules.
 *
 * @author Roberto Posenato
 * @version $Rev$
 */
@SuppressWarnings("ClassWithoutLogger")
public class CSTNIR extends CSTN {

	/**
	 * Version of the class
	 */
	// static public final String VERSIONandDATE = "Version 1.0 - April, 03 2017";// first release i.r.
	static public final String VERSIONandDATE = "Version  1.1 - October, 10 2017";// removed qLabels

	/**
	 * Constructor for CSTN.
	 *
	 * @param g1 the labeled int valued tNGraph to check
	 */
	public CSTNIR(TNGraph<CSTNEdge> g1) {
		super(g1);
		reactionTime = 0;
	}

	/**
	 * @param g1       a {@link it.univr.di.cstnu.graph.TNGraph} object.
	 * @param timeOut1 the timeout for the check in seconds.
	 */
	public CSTNIR(TNGraph<CSTNEdge> g1, int timeOut1) {
		super(g1, timeOut1);
		reactionTime = 0;
	}

	/**
	 * Default constructor.
	 */
	CSTNIR() {
		reactionTime = 0;
	}

	/**
	 * Just for using this class also from a terminal.
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 * @throws java.io.IOException                            if any.
	 * @throws javax.xml.parsers.ParserConfigurationException if any.
	 * @throws org.xml.sax.SAXException                       if any.
	 */
	@SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
	public static void main(final String[] args) throws IOException, ParserConfigurationException, SAXException {
		defaultMain(args, new CSTNIR3R(), "Instantaneous Reaction DC");
	}

	/**
	 *
	 */
	@Override
	boolean lpMustRestricted2ConsistentLabel(final int u, final int v) {
		// Table 1 ICAPS paper for standard DC
		// u must be < 0
		return u >= 0;
	}

	/**
	 *
	 */
	@SuppressWarnings("FinalMethod")
	@Override
	final boolean mainConditionForSkippingInR0qR0(final int w) {
		// Table 1 ICAPS2016 paper for IR semantics
		// w must be < 0.
		return w >= 0;
	}

}
