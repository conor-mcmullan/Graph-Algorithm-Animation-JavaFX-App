package root;

import root.interfaces.EdgeInt;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Edge.
 */
public class Edge implements EdgeInt {

    /**
     * The Start.
     */
    public Vertex start, /**
     * The End.
     */
    end;
    /**
     * The Weight.
     */
    public double weight;
    /**
     * The Edge verticies.
     */
    public List<Vertex> edgeVerticies = new ArrayList<Vertex>();

    /**
     * Instantiates a new Edge.
     *
     * @param start  the start
     * @param end    the end
     * @param weight the weight
     */
    public Edge(Vertex start, Vertex end, double weight){
        this.start = start;
        this.end = end;
        this.weight = weight;
        edgeVerticies.add(this.start);
        edgeVerticies.add(this.end);
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public double getWeight(){
        return this.weight;
    }

    public Vertex getStart(){
        return this.start;
    }

    public Vertex getEnd(){
        return this.end;
    }

    public List<Vertex> getVerticies(){
        return this.edgeVerticies;
    }

    public List<String> getStringOveriew(boolean includeWeight){
        List<String> overview = new ArrayList<String>();
        for(Vertex v: this.edgeVerticies) {
            overview.add(v.getName());
        }
        if (includeWeight){
            overview.add(String.valueOf(this.getWeight()));
        }
        return overview;
    }

}
