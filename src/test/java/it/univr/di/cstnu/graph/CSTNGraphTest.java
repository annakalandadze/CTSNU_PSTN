/**
 *
 */
package it.univr.di.cstnu.graph;

import it.univr.di.cstnu.graph.TNGraph.UnmodifiableTNGraph;
import it.univr.di.labeledvalue.Constants;
import it.univr.di.labeledvalue.Label;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * @author posenato
 */
public class CSTNGraphTest {

	/**
	 *
	 */
	static Class<? extends CSTNEdge> edgeImplClass = CSTNEdgePluggable.class;

	/**
	 *
	 */
	static EdgeSupplier<CSTNEdge> edgeFactory = new EdgeSupplier<>(edgeImplClass);

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public void UnmodifiableClearTest() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("prova", edgeImplClass);

		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		g.addVertex(Z);
		g.addVertex(X);
		g.addVertex(LabeledNodeSupplier.get("A3"));
		g.addVertex(LabeledNodeSupplier.get("A4"));
		g.addVertex(LabeledNodeSupplier.get("A5"));
		g.addVertex(LabeledNodeSupplier.get("A6"));
		g.addVertex(LabeledNodeSupplier.get("A7"));
		g.addVertex(LabeledNodeSupplier.get("A8"));
		g.addVertex(LabeledNodeSupplier.get("A9"));
		g.addVertex(LabeledNodeSupplier.get("A10"));
		g.addVertex(LabeledNodeSupplier.get("A11"));
		g.addEdge(edgeFactory.get("ZX"), Z, X);
		g.addEdge(edgeFactory.get("Z3"), "Z", "A3");
		g.addEdge(edgeFactory.get("Z4"), "Z", "A4");
		g.addEdge(edgeFactory.get("A3_11"), "A3", "A11");
		g.addEdge(edgeFactory.get("A11_3"), "A11", "A3");
		g.addEdge(edgeFactory.get("A4_11"), "A4", "A11");
		g.addEdge(edgeFactory.get("A11_4"), "A11", "A4");
		g.addEdge(edgeFactory.get("A11_5"), "A11", "A5");

		final UnmodifiableTNGraph<CSTNEdge> unmodifiableG = TNGraph.unmodifiable(g);
		assertEquals(g.toString(), unmodifiableG.toString());

		unmodifiableG.clear();

		assertEquals("❮A3❯", Objects.requireNonNull(g.getNode("A3")).toString());
		assertNull(unmodifiableG.getNode("A3"));
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public void create() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("prova", edgeImplClass);

		g.addVertex(LabeledNodeSupplier.get("Z"));
		g.addVertex(LabeledNodeSupplier.get("X"));
		g.addEdge(edgeFactory.get("ZX"), "Z", "X");
		g.addEdge(edgeFactory.get("XZ"), "X", "Z");

		assertEquals(g.getVertexCount(), 2);
		assertEquals(g.getEdgeCount(), 2);
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public void removeNode() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("prova", edgeImplClass);

		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		g.addVertex(Z);
		g.addVertex(X);
		g.addEdge(edgeFactory.get("ZX"), Z, X);
		g.addEdge(edgeFactory.get("XZ"), "X", "Z");
		g.removeEdge("XZ");
		g.addEdge(edgeFactory.get("XZ"), X, Z);

		g.removeVertex(X);
		assertEquals(g.getVertexCount(), 1);
		assertEquals(g.getEdgeCount(), 0);

	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test(expected = UnsupportedOperationException.class)
	public void UnmodifiableTest() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("prova", edgeImplClass);

		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		g.addVertex(Z);
		g.addVertex(X);
		g.addVertex(LabeledNodeSupplier.get("A3"));
		g.addVertex(LabeledNodeSupplier.get("A4"));
		g.addVertex(LabeledNodeSupplier.get("A5"));
		g.addVertex(LabeledNodeSupplier.get("A6"));
		g.addVertex(LabeledNodeSupplier.get("A7"));
		g.addVertex(LabeledNodeSupplier.get("A8"));
		g.addVertex(LabeledNodeSupplier.get("A9"));
		g.addVertex(LabeledNodeSupplier.get("A10"));
		g.addVertex(LabeledNodeSupplier.get("A11"));
		g.addEdge(edgeFactory.get("ZX"), Z, X);
		g.addEdge(edgeFactory.get("Z3"), "Z", "A3");
		g.addEdge(edgeFactory.get("Z4"), "Z", "A4");
		g.addEdge(edgeFactory.get("A3_11"), "A3", "A11");
		g.addEdge(edgeFactory.get("A11_3"), "A11", "A3");
		g.addEdge(edgeFactory.get("A4_11"), "A4", "A11");
		g.addEdge(edgeFactory.get("A11_4"), "A11", "A4");
		g.addEdge(edgeFactory.get("A11_5"), "A11", "A5");

		final UnmodifiableTNGraph<CSTNEdge> unmodifiableG = TNGraph.unmodifiable(g);
		assertEquals(g.toString(), unmodifiableG.toString());

		unmodifiableG.removeVertex(Objects.requireNonNull(unmodifiableG.getNode("A3")));

	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public void addManyNodes() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("prova", edgeImplClass);

		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		g.addVertex(Z);
		g.addVertex(X);
		g.addVertex(LabeledNodeSupplier.get("A3"));
		g.addVertex(LabeledNodeSupplier.get("A4"));
		g.addVertex(LabeledNodeSupplier.get("A5"));
		g.addVertex(LabeledNodeSupplier.get("A6"));
		g.addVertex(LabeledNodeSupplier.get("A7"));
		g.addVertex(LabeledNodeSupplier.get("A8"));
		g.addVertex(LabeledNodeSupplier.get("A9"));
		g.addVertex(LabeledNodeSupplier.get("A10"));
		g.addVertex(LabeledNodeSupplier.get("A11"));
		g.addEdge(edgeFactory.get("ZX"), Z, X);
		g.addEdge(edgeFactory.get("XZ"), "X", "Z");
		g.addEdge(edgeFactory.get("Z3"), "Z", "A3");
		g.addEdge(edgeFactory.get("Z4"), "Z", "A4");
		g.addEdge(edgeFactory.get("Z5"), "Z", "A5");
		g.addEdge(edgeFactory.get("Z6"), "Z", "A6");
		g.addEdge(edgeFactory.get("Z7"), "Z", "A7");
		g.addEdge(edgeFactory.get("Z8"), "Z", "A8");
		g.addEdge(edgeFactory.get("Z9"), "Z", "A9");
		g.addEdge(edgeFactory.get("Z10"), "Z", "A10");
		g.addEdge(edgeFactory.get("Z11"), "Z", "A11");

		g.addEdge(edgeFactory.get("A3_11"), "A3", "A11");
		g.addEdge(edgeFactory.get("A11_3"), "A11", "A3");
		g.addEdge(edgeFactory.get("A4_11"), "A4", "A11");
		g.addEdge(edgeFactory.get("A11_4"), "A11", "A4");
		g.addEdge(edgeFactory.get("A5_11"), "A5", "A11");
		g.addEdge(edgeFactory.get("A11_5"), "A11", "A5");
		// System.out.println("G: "+g);
		g.removeVertex(Z);
		@SuppressWarnings("unused") final String expected = """
		                                                    %TNGraph: prova
		                                                    %TNGraph Syntax
		                                                    %LabeledNode: <name, label, proposition observed>
		                                                    %T: <name, type, source node, dest. node, L:{labeled values}, LL:{lower case labeled values}, UL:{upper case labeled values}>
		                                                    Nodes:
		                                                    <A10,	⊡,	null>
		                                                    <A11,	⊡,	null>
		                                                    <A3,	⊡,	null>
		                                                    <A4,	⊡,	null>
		                                                    <A5,	⊡,	null>
		                                                    <A6,	⊡,	null>
		                                                    <A7,	⊡,	null>
		                                                    <A8,	⊡,	null>
		                                                    <A9,	⊡,	null>
		                                                    <X,	⊡,	null>
		                                                    Edges:
		                                                    <A11_3,	normal,	A11,	A3,	L:{}, LL:{}, UL:{}>
		                                                    <A11_4,	normal,	A11,	A4,	L:{}, LL:{}, UL:{}>
		                                                    <A11_5,	normal,	A11,	A5,	L:{}, LL:{}, UL:{}>
		                                                    <A3_11,	normal,	A3,	A11,	L:{}, LL:{}, UL:{}>
		                                                    <A4_11,	normal,	A4,	A11,	L:{}, LL:{}, UL:{}>
		                                                    <A5_11,	normal,	A5,	A11,	L:{}, LL:{}, UL:{}>
		                                                    """;
		assertEquals(g.getVertexCount(), 10);
		assertEquals(g.getEdgeCount(), 6);
	}

	/**
	 *
	 */
	@SuppressWarnings({"static-method", "EqualsWithItself"})
	@Test
	public final void cloneTest() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("", edgeImplClass);
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		final LabeledNode U = LabeledNodeSupplier.get("U");
		g.addVertex(Z);
		g.addVertex(U);
		g.setZ(Z);

		final Label l = Label.parse("¬p¬q");
		final CSTNEdge eUZ = edgeFactory.get("eUZ");
		eUZ.mergeLabeledValue(Label.parse("¬p¿q"), -13);
		eUZ.mergeLabeledValue(l, -13);
		eUZ.mergeLabeledValue(Label.parse("¿p"), -15);
		eUZ.mergeLabeledValue(Label.parse("¿p¬q"), -22);
		eUZ.mergeLabeledValue(Label.emptyLabel, -10);
		eUZ.mergeLabeledValue(Label.parse("¿p¿q"), Constants.INT_NEG_INFINITE);

		g.addEdge(eUZ, U, Z);

		assertEquals(g, g);

		// System.out.println(g);

		TNGraph<CSTNEdge> g1 = new TNGraph<>("", edgeImplClass);
		g1.copy(g);

		// System.out.println(g1);

		assertEquals(g.toString(), g1.toString());
		assertTrue(g1.hasSameEdgesOf(g));

		eUZ.removeLabeledValue(l);
		eUZ.putLabeledValue(Label.emptyLabel, -22);

		// System.out.println(g);
		assertFalse(g1.hasSameEdgesOf(g));

		g1 = new TNGraph<>(g, edgeImplClass);
		assertEquals(g.toString(), g1.toString());
		assertTrue(g1.hasSameEdgesOf(g));
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public void enlarge() {
		final int order = 4;
		int[][] adjacency = new int[order][order];
		int currentSize = adjacency.length;
		for (int i = 0; i < currentSize; i++) {
			for (int j = 0; j < currentSize; j++) {
				adjacency[i][j] = (i + 1) * (j + 1);
			}
		}

		// System.out.println("Adjacency prima di enlarge: " + Arrays.deepToString(adjacency) + "\nlenght:" + adjacency.length);
		assertEquals("[[1, 2, 3, 4], [2, 4, 6, 8], [3, 6, 9, 12], [4, 8, 12, 16]]", Arrays.deepToString(adjacency));

		currentSize = (int) (currentSize * 1.8f);
		// System.out.println("currentSize:" + currentSize);
		assertEquals(7, currentSize);
		adjacency = Arrays.copyOf(adjacency, currentSize);
		for (int j = 0; j < order; j++) {
			adjacency[j] = Arrays.copyOf(adjacency[j], currentSize);
		}
		for (int j = order; j < currentSize; j++) {
			adjacency[j] = new int[currentSize];
		}
		// System.out.println("Adjacency dopo di enlarge: " + Arrays.deepToString(adjacency) + "\nlenght:" + adjacency.length);
		assertEquals(
			"[[1, 2, 3, 4, 0, 0, 0], [2, 4, 6, 8, 0, 0, 0], [3, 6, 9, 12, 0, 0, 0], [4, 8, 12, 16, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0]]",
			Arrays.deepToString(adjacency));
		final int nodeIndexToRem = 1;
		final int last = order - 1;
		for (int i = 0; i < last; i++) {
			if (i == nodeIndexToRem) {
				adjacency[nodeIndexToRem][nodeIndexToRem] = adjacency[last][last];
				adjacency[last][last] = adjacency[nodeIndexToRem][last] = adjacency[last][nodeIndexToRem] = 0;
				continue;
			}
			adjacency[i][nodeIndexToRem] = adjacency[i][last];
			adjacency[i][last] = 0;
			adjacency[nodeIndexToRem][i] = adjacency[last][i];
			adjacency[last][i] = 0;
		}
		// System.out.println("Adjacency dopo cancellazione indice 1: " + Arrays.deepToString(adjacency) + "\nlenght:" + adjacency.length);
		assertEquals(
			"[[1, 4, 3, 0, 0, 0, 0], [4, 16, 12, 0, 0, 0, 0], [3, 12, 9, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0]]",
			Arrays.deepToString(adjacency));
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public void managinDifferentEdges() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("prova", edgeImplClass);

		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		g.addVertex(Z);
		g.addVertex(X);
		g.addVertex(LabeledNodeSupplier.get("A3"));
		g.addVertex(LabeledNodeSupplier.get("A4"));
		g.addVertex(LabeledNodeSupplier.get("A5"));
		g.addVertex(LabeledNodeSupplier.get("A6"));
		g.addVertex(LabeledNodeSupplier.get("A7"));
		g.addVertex(LabeledNodeSupplier.get("A8"));
		g.addVertex(LabeledNodeSupplier.get("A9"));
		g.addVertex(LabeledNodeSupplier.get("A10"));
		g.addVertex(LabeledNodeSupplier.get("A11"));
		g.addEdge(edgeFactory.get("ZX"), Z, X);
		g.addEdge(edgeFactory.get("XZ"), X, Z);
		g.addEdge(edgeFactory.get("Z3"), "Z", "A3");
		g.addEdge(edgeFactory.get("Z4"), "Z", "A4");
		g.addEdge(edgeFactory.get("Z5"), "Z", "A5");
		g.addEdge(edgeFactory.get("Z6"), "Z", "A6");
		g.addEdge(edgeFactory.get("Z7"), "Z", "A7");
		g.addEdge(edgeFactory.get("Z8"), "Z", "A8");
		g.addEdge(edgeFactory.get("Z9"), "Z", "A9");
		g.addEdge(edgeFactory.get("Z10"), "Z", "A10");
		g.addEdge(edgeFactory.get("Z11"), "Z", "A11");
		g.addEdge(edgeFactory.get("A3_11"), "A3", "A11");
		g.addEdge(edgeFactory.get("A11_3"), "A11", "A3");
		g.addEdge(edgeFactory.get("A4_11"), "A4", "A11");
		g.addEdge(edgeFactory.get("A11_4"), "A11", "A4");
		g.addEdge(edgeFactory.get("A5_11"), "A5", "A11");
		g.addEdge(edgeFactory.get("A11_5"), "A11", "A5");
		// System.out.println("G: "+g);
		g.removeVertex(Z);

		CSTNEdge e1;
		for (final CSTNEdge edge : g.getEdges()) {
			e1 = edgeFactory.get(edge.getName() + "new");
			edge.takeIn(e1);
		}
		@SuppressWarnings("unused") final String expected = """
		                                                    %TNGraph: prova
		                                                    %TNGraph Syntax
		                                                    %LabeledNode: <name, label, proposition observed>
		                                                    %T: <name, type, source node, dest. node, L:{labeled values}, LL:{lower case labeled values}, UL:{upper case labeled values}>
		                                                    Nodes:
		                                                    <A10,	⊡,	null>
		                                                    <A11,	⊡,	null>
		                                                    <A3,	⊡,	null>
		                                                    <A4,	⊡,	null>
		                                                    <A5,	⊡,	null>
		                                                    <A6,	⊡,	null>
		                                                    <A7,	⊡,	null>
		                                                    <A8,	⊡,	null>
		                                                    <A9,	⊡,	null>
		                                                    <X,	⊡,	null>
		                                                    Edges:
		                                                    <A11_3,	normal,	A11,	A3,	L:{}, LL:{}, UL:{}>
		                                                    <A11_4,	normal,	A11,	A4,	L:{}, LL:{}, UL:{}>
		                                                    <A11_5,	normal,	A11,	A5,	L:{}, LL:{}, UL:{}>
		                                                    <A3_11,	normal,	A3,	A11,	L:{}, LL:{}, UL:{}>
		                                                    <A4_11,	normal,	A4,	A11,	L:{}, LL:{}, UL:{}>
		                                                    <A5_11,	normal,	A5,	A11,	L:{}, LL:{}, UL:{}>
		                                                    """;
		assertEquals(g.getVertexCount(), 10);
		assertEquals(g.getEdgeCount(), 6);
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public void reverse() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("prova", edgeImplClass);

		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Z = LabeledNodeSupplier.get("Z");
		g.addVertex(Z);
		g.addVertex(X);
		g.addVertex(LabeledNodeSupplier.get("A3"));
		g.addVertex(LabeledNodeSupplier.get("A4"));
		g.addVertex(LabeledNodeSupplier.get("A5"));
		g.addVertex(LabeledNodeSupplier.get("A6"));
		g.addVertex(LabeledNodeSupplier.get("A7"));
		g.addVertex(LabeledNodeSupplier.get("A8"));
		g.addVertex(LabeledNodeSupplier.get("A9"));
		g.addVertex(LabeledNodeSupplier.get("A10"));
		g.addVertex(LabeledNodeSupplier.get("A11"));
		g.addEdge(edgeFactory.get("ZX"), Z, X);
		g.addEdge(edgeFactory.get("Z3"), "Z", "A3");
		g.addEdge(edgeFactory.get("Z4"), "Z", "A4");
		g.addEdge(edgeFactory.get("A3_11"), "A3", "A11");
		g.addEdge(edgeFactory.get("A11_3"), "A11", "A3");
		g.addEdge(edgeFactory.get("A4_11"), "A4", "A11");
		g.addEdge(edgeFactory.get("A11_4"), "A11", "A4");
		g.addEdge(edgeFactory.get("A11_5"), "A11", "A5");
		// System.out.println("G: " + g);

		CSTNEdge e1;
		for (final CSTNEdge edge : g.getEdges()) {
			e1 = edgeFactory.get(edge.getName() + "new");
			edge.takeIn(e1);
		}
		// System.out.println("Gnew: " + g);

		g.reverse();
		// System.out.println("Greversed: " + g);

		assertEquals(11, g.getVertexCount());
		assertEquals(8, g.getEdgeCount());
		final String expected = """
		                        %TNGraph: prova
		                        %Nodes:
		                        ❮A10❯
		                        ❮A11❯
		                        ❮A3❯
		                        ❮A4❯
		                        ❮A5❯
		                        ❮A6❯
		                        ❮A7❯
		                        ❮A8❯
		                        ❮A9❯
		                        ❮X❯
		                        ❮Z❯
		                        %Edges:
		                        ❮A3❯--❮A11_3new; requirement; ❯-->❮A11❯
		                        ❮A4❯--❮A11_4new; requirement; ❯-->❮A11❯
		                        ❮A5❯--❮A11_5new; requirement; ❯-->❮A11❯
		                        ❮A11❯--❮A3_11new; requirement; ❯-->❮A3❯
		                        ❮A11❯--❮A4_11new; requirement; ❯-->❮A4❯
		                        ❮A3❯--❮Z3new; requirement; ❯-->❮Z❯
		                        ❮A4❯--❮Z4new; requirement; ❯-->❮Z❯
		                        ❮X❯--❮ZXnew; requirement; ❯-->❮Z❯
		                        """;
		assertEquals("Reversed tNGraph:", expected, g.toString());
	}

	/**
	 *
	 */
	@SuppressWarnings("static-method")
	@Test
	public final void testGetChildrenOf() {
		final TNGraph<CSTNEdge> g = new TNGraph<>("", edgeImplClass);
		final LabeledNode P = LabeledNodeSupplier.get("P?", 'p');
		final LabeledNode X = LabeledNodeSupplier.get("X");
		final LabeledNode Y = LabeledNodeSupplier.get("Y");
		final LabeledNode A = LabeledNodeSupplier.get("A?", 'a');
		A.setLabel(Label.parse("p"));
		final LabeledNode B = LabeledNodeSupplier.get("B?", 'b');
		B.setLabel(Label.parse("¬pa"));

		g.addVertex(P);
		g.addVertex(A);
		g.addVertex(B);
		g.addVertex(X);
		g.addVertex(Y);

		// wellDefinition(g);

		// System.out.println("Children of A: "+g.getChildrenOf(A));
		assertNull(g.getChildrenOf(B));

		assertEquals("b", Objects.requireNonNull(g.getChildrenOf(A)).toString());

		// System.out.println("Children of P: "+g.getChildrenOf(P));
		assertEquals("ab", Objects.requireNonNull(g.getChildrenOf(P)).toString());

		B.setLabel(Label.emptyLabel);
		// System.out.println("Children of P: "+g.getChildrenOf(P));
		assertEquals("a", Objects.requireNonNull(g.getChildrenOf(P)).toString());
		assertNull(g.getChildrenOf(A));

	}
}
