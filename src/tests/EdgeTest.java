package tests;

import org.junit.jupiter.api.*;
import root.Edge;
import root.Graph;
import root.Vertex;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setWeight() {
        double weight = 2.0;
        List<Vertex> vertices = new ArrayList<Vertex>(){};
        List<Edge> edges = new ArrayList<Edge>(){};
        Vertex a = new Vertex("A");
        Vertex b = new Vertex("B");
        Edge a_b_2 = new Edge(a, b, weight);
        edges.add(a_b_2);
        Graph g = new Graph(vertices, edges);
        // evaluating weight sets for edge object
        assertEquals(weight, a_b_2.getWeight());
        //evaluating weight sets for edge objects added to the graph
        assertEquals(weight, g.getEdges().get(0).getWeight());
    }

    /*
     * TEST
     *
     * Edge
     *      constructor and method access
     *
     * set a edge with a weight
     * set a edge without a weight
     * update a weight
     * get start vertex
     * get end vertex
     * get both vertex in edge with weight
     * get both vertex in edge without weight
     *
     * */


}