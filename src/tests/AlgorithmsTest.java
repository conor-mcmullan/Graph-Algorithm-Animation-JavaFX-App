package tests;

import org.junit.jupiter.api.*;
import root.*;
import root.scenes.AnimationData;

import java.io.IOException;
import java.util.*;


class AlgorithmsTest{

    private static void printLine(){ System.out.println("".concat("-".repeat(10))); }
    private static FileHandler fh;
    private static Graph g;
    private static Algorithms a;

    @BeforeAll
    static void init() throws IOException {
        a = new Algorithms();
        fh = new FileHandler();
        g = fh.fileToGraph("src/graphfiles/example_6.txt");
    }

    @BeforeEach
    void setUp() {
        printLine();
    }

    @AfterEach
    void tearDown() { printLine();}

    @Test
    void constructorTest() {
        System.out.println("constructorTest()");
        Algorithms a = new Algorithms();
        boolean validClassObject = false;
        if (a!=null){
            int constructorCount = a.getClass().getConstructors().length;
            int methodCount = a.getClass().getMethods().length;
            System.out.println(String.format("Constructors:\t%s", constructorCount));
            System.out.println(String.format("Method Count:\t%s", methodCount));
            validClassObject = (constructorCount>0 && methodCount>0);
        }
        else{
            System.out.println("Invalid: Class Object is NULL");
        }
        Assertions.assertTrue(validClassObject);
    }

    @Test
    void getTraversalMapTest() {
        System.out.println("getTraversalMapTest()");
        HashMap<String, HashMap<String, Double>> travelMap = a.getTraversalMap(g, new Vertex("a"));
        // keyset and graph vertex list should be equal
        System.out.println("getVertexIDList:\t"+g.getVertexIDList());
        System.out.println("travelMap:\t\t\t"+travelMap.keySet());
        boolean initialMapIsEqual = g.getVertexIDList().equals(new ArrayList<>(travelMap.keySet()));
        System.out.println("Equal:\t"+initialMapIsEqual);
        // all of the values in travelMap should be the same
        HashSet<HashMap<String, Double>> hs = new HashSet<>(travelMap.values());
        boolean valuesEqual = (hs.size() == 1);
        System.out.println("Values:\t"+hs.toString());
        System.out.println("Equal:\t"+valuesEqual);
        Assertions.assertTrue(valuesEqual && initialMapIsEqual);
    }

    @Test
    void dijkstraAnimationCompleteGraphTest() {
        System.out.println("dijkstraAnimationCompleteGraphTest()");
        AnimationData data = a.dijkstraANIMATION(g, "a");
        boolean correctAlgorithm = data.getLogInfo().toString().toLowerCase().contains("dijkstra");
        System.out.println("Expected Algorithm:\tdijkstra");
        System.out.println("Correct Algorithm:\t"+correctAlgorithm);
        System.out.println("Travel Costs:\t"+data.getTravesalValues());
        HashMap<String, Double> expectedCosts = new HashMap<>();
        expectedCosts.put("a", 0.0);
        expectedCosts.put("b", 2.0);
        expectedCosts.put("c", 5.0);
        expectedCosts.put("d", 8.0);
        expectedCosts.put("e", 1.0);
        expectedCosts.put("f", 2.0);
        boolean validCostings = true;
        for (String node: data.getTravesalValues().keySet()){
            if (!expectedCosts.get(node).equals(data.getTravesalValues().get(node))){
                validCostings=false;
                break;
            }
        }
        System.out.println("Valid Costings:\t"+validCostings);
        Assertions.assertTrue(correctAlgorithm && validCostings);
    }

    @Test
    void dijkstraAnimationMixedCompleteGraphTest() {
        System.out.println("dijkstraAnimationMixedCompleteGraphTest()");

        // Graph Object Container Lists
        List<Vertex> vertices = new ArrayList<Vertex>() {};
        List<Edge> edges = new ArrayList<Edge>() {};

        // create vertex objects
        Vertex nodeA = new Vertex("A");
        Vertex nodeB = new Vertex("B");
        Vertex nodeC = new Vertex("C");
        Vertex nodeD = new Vertex("D");
        // add all of the vertex objects to the list
        vertices.add(nodeA);vertices.add(nodeB);
        vertices.add(nodeC);vertices.add(nodeD);

        // add all of the edges to the list
        edges.add(new Edge(nodeA, nodeB, 4));
        edges.add(new Edge(nodeB, nodeC, 3));
        edges.add(new Edge(nodeC, nodeA, 6));

        // create the graph object
        Graph graph = new Graph(vertices, edges);

        // before the same test but with this graph
        AnimationData data = a.dijkstraANIMATION(graph, "A");
        boolean correctAlgorithm = data.getLogInfo().toString().toLowerCase().contains("dijkstra");
        System.out.println("Expected Algorithm:\tdijkstra");
        System.out.println("Correct Algorithm:\t"+correctAlgorithm);
        System.out.println("Travel Costs:\t"+data.getTravesalValues());
        HashMap<String, Double> expectedCosts = new HashMap<>();
        expectedCosts.put("A", 0.0);
        expectedCosts.put("B", 4.0);
        expectedCosts.put("C", 6.0);
        expectedCosts.put("D", Double.POSITIVE_INFINITY);
        boolean validCostings = true;
        for (String node: data.getTravesalValues().keySet()){
            if (!expectedCosts.get(node).equals(data.getTravesalValues().get(node))){
                validCostings=false;
                break;
            }
        }
        System.out.println("Valid Costings:\t"+validCostings);
        Assertions.assertTrue(correctAlgorithm && validCostings);
    }

    @Test
    void dijkstraAnimationInCompleteGraphTest(){
        System.out.println("dijkstraAnimationMixedCompleteGraphTest()");
        // Graph Object Container Lists
        List<Vertex> vertices = new ArrayList<Vertex>() {};
        List<Edge> edges = new ArrayList<Edge>() {};
        // add all of the vertex objects to the list
        vertices.add(new Vertex("A"));
        vertices.add(new Vertex("B"));
        vertices.add(new Vertex("C"));
        vertices.add(new Vertex("D"));
        // create the graph object
        Graph graph = new Graph(vertices, edges);
        // before the same test but with this graph
        AnimationData data = a.dijkstraANIMATION(graph, "A");
        boolean correctAlgorithm = data.getLogInfo().toString().toLowerCase().contains("dijkstra");
        System.out.println("Expected Algorithm:\tdijkstra");
        System.out.println("Correct Algorithm:\t"+correctAlgorithm);
        System.out.println("Travel Costs:\t"+data.getTravesalValues());
        HashMap<String, Double> expectedCosts = new HashMap<>();
        expectedCosts.put("A", 0.0);
        expectedCosts.put("B", Double.POSITIVE_INFINITY);
        expectedCosts.put("C", Double.POSITIVE_INFINITY);
        expectedCosts.put("D", Double.POSITIVE_INFINITY);
        boolean validCostings = true;
        for (String node: data.getTravesalValues().keySet()){
            if (!expectedCosts.get(node).equals(data.getTravesalValues().get(node))){
                validCostings=false;
                break;
            }
        }
        System.out.println("Valid Costings:\t"+validCostings);
        Assertions.assertTrue(correctAlgorithm && validCostings);
    }

    @Test
    void nextKeyTest(){
        System.out.println("Graph Connectivity");
        g.viewConnectivity();

        HashMap<String, HashMap<String, Double>> travelMap = a.getTraversalMap(g, new Vertex("a"));
        System.out.println("\nInitial Traversal Costings:"+travelMap.toString());

        HashMap<String, Double> innerTMap = travelMap.get("a");
        System.out.println(innerTMap.toString());

        // lets get the minimum value and if its key not in Tv then
        ArrayList<String> tv = new ArrayList<>();
        // add 'a' as we started here
        tv.add("a");

        List<String> minimumKeys = a.nextKey(innerTMap, tv);
        System.out.println(minimumKeys);

        // lets just take the 1st value
        String minKey = minimumKeys.get(0);
        System.out.println(minKey);
        travelMap.replace(minKey, innerTMap);
        innerTMap = travelMap.get("a");
        System.out.println(innerTMap.toString());

        System.out.println("----");
        tv.add(minKey);
        innerTMap = travelMap.get(minKey);
        minimumKeys = a.nextKey(innerTMap, tv);
        System.out.println(minimumKeys);
        //minKey = minimumKeys.get(0);
        System.out.println(minKey);
        travelMap.replace(minKey, innerTMap);
        innerTMap = travelMap.get(minKey);
        System.out.println(innerTMap.toString());

        System.out.println("---");
        System.out.println(Arrays.toString(tv.toArray()));

    }
    /*
    * TEST
    *
    * Next Key - getting the minimum next connected node
    *   nextKey - 1 node graph
    *   nextKey - mixed graph
    *   nextKey - complete graph

    *
    * Log Info
    *   getDijkstraAnimationLogInfo - return and updated AnimationData Object
    *
    * */

}