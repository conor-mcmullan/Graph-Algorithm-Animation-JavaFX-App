package root.interfaces;

import root.Graph;
import root.Vertex;
import root.scenes.AnimationData;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * The interface Algorithms int.
 */
public interface AlgorithmsInt {
    /**
     * Gets traversal map.
     *
     * @param g        the g
     * @param thisVert the this vert
     * @return the traversal map
     */
    HashMap<String, HashMap<String, Double>> getTraversalMap(Graph g, Vertex thisVert);

    /**
     * Dijkstra.
     *
     * @param g          the g
     * @param fromVertex the from vertex
     */
// List<String> nextKey(HashMap<String, Double> innerTMap, ArrayList<String> avoidStings);
    void dijkstra(Graph g, String fromVertex);

    /**
     * Display transversed values.
     *
     * @param lv   the lv
     * @param from the from
     */
    void displayTransversedValues(HashMap<String, Double> lv, Vertex from);

    /**
     * Gets transversed values.
     *
     * @param lv   the lv
     * @param from the from
     * @return the transversed values
     */
    LinkedList<String> getTransversedValues(HashMap<String, Double> lv, Vertex from);

    /**
     * Gets dijkstra animation log info.
     *
     * @param animationData the animation data
     * @return the dijkstra animation log info
     */
    AnimationData getDijkstraAnimationLogInfo(AnimationData animationData);
    // AnimationData dijkstraANIMATION(Graph g, String fromVertex);
}
