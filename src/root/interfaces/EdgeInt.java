package root.interfaces;

import root.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * The interface Edge int.
 */
public interface EdgeInt {

    /**
     * The constant edgeVerticies.
     */
// Vertex start, end;
    // double weight;
    List<Vertex> edgeVerticies = new ArrayList<Vertex>();

    /**
     * Sets weight.
     *
     * @param weight the weight
     */
    void setWeight(double weight);

    /**
     * Gets weight.
     *
     * @return the weight
     */
    double getWeight();

    /**
     * Gets start.
     *
     * @return the start
     */
    Vertex getStart();

    /**
     * Gets end.
     *
     * @return the end
     */
    Vertex getEnd();

    /**
     * Gets verticies.
     *
     * @return the verticies
     */
    List<Vertex> getVerticies();

    /**
     * Gets string overiew.
     *
     * @param includeWeight the include weight
     * @return the string overiew
     */
    List<String> getStringOveriew(boolean includeWeight);
}
