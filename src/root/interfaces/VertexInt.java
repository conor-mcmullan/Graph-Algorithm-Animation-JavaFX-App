package root.interfaces;

import root.Edge;
import root.Vertex;

import java.util.ArrayList;

/**
 * The interface Vertex int.
 */
public interface VertexInt {
    /**
     * Connect to edge.
     *
     * @param e the e
     */
    void connectToEdge(Edge e);

    /**
     * Connect to vertex.
     *
     * @param v the v
     */
    void connectToVertex(Vertex v);

    /**
     * Gets x.
     *
     * @return the x
     */
    double getX();

    /**
     * Gets y.
     *
     * @return the y
     */
    double getY();

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets connected vertecies.
     *
     * @return the connected vertecies
     */
    ArrayList<Vertex> getConnectedVertecies();

    /**
     * Gets connected edges.
     *
     * @return the connected edges
     */
    ArrayList<Edge> getConnectedEdges();

    /**
     * View connected verticies string.
     *
     * @return the string
     */
    String viewConnectedVerticies();
}
