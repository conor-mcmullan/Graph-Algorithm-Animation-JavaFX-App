package root.interfaces;

import root.Edge;
import root.Vertex;
import root.scenes.AnimationData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * The interface Graph int.
 */
public interface GraphInt {

    /**
     * Dijkstra.
     *
     * @param from the from
     */
//void linkVerticies();
    void dijkstra(String from);

    /**
     * Gets edges.
     *
     * @return the edges
     */
    List<Edge> getEdges();

    /**
     * Gets vertices.
     *
     * @return the vertices
     */
    List<Vertex> getVertices();

    /**
     * Gets viewable edges.
     *
     * @param includeWeights the include weights
     * @return the viewable edges
     */
    String getViewableEdges(boolean includeWeights);

    /**
     * Gets vertex id list.
     *
     * @return the vertex id list
     */
    ArrayList<String> getVertexIDList();

    /**
     * Gets vertex names.
     *
     * @return the vertex names
     */
    Set<String> getVertexNames();

    /**
     * Dijkstra animation animation data.
     *
     * @param from the from
     * @return the animation data
     */
    AnimationData dijkstraANIMATION(String from);

    /**
     * Find your vertex vertex.
     *
     * @param vertexName the vertex name
     * @return the vertex
     */
    Vertex findYourVertex(String vertexName);

    /**
     * Gets adjacency matrix map.
     *
     * @return the adjacency matrix map
     */
    LinkedHashMap<String, LinkedHashMap<String, Integer>> getAdjacencyMatrixMap();

    /**
     * Gets adjacency matrix.
     *
     * @return the adjacency matrix
     */
    String getAdjacencyMatrix();

    /**
     * Gets positional string.
     *
     * @return the positional string
     */
    String getPositionalString();

    /**
     * View connectivity.
     */
    void viewConnectivity();

    /**
     * View connectivity.
     *
     * @param vert the vert
     */
    void viewConnectivity(Vertex vert);

}
