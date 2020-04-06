package root;

import root.interfaces.VertexInt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type Vertex.
 */
public class Vertex implements VertexInt {

    /**
     * The Connected edges.
     */
// Class variables: storing this. Vertex connections
    private ArrayList<Edge> connectedEdges = new ArrayList<Edge>();
    /**
     * The Connected vertecies.
     */
    private ArrayList<Vertex> connectedVertecies = new ArrayList<>();
    /**
     * The Name.
     */
    private String name;
    /**
     * The X.
     */
    private double x, /**
     * The Y.
     */
    y;

    /**
     * Instantiates a new Vertex.
     *
     * @param name the name
     */
// Constructors
    public Vertex(String name){
        this.name = name;
    }

    /**
     * Instantiates a new Vertex.
     *
     * @param name the name
     * @param x    the x
     * @param y    the y
     */
    public Vertex(String name, double x, double y) { this.name = name; this.x = x; this.y = y; }

    // Setters|Connectors
    public void connectToEdge(Edge e){
        connectedEdges.add(e);
    }

    public void connectToVertex(Vertex v){
        connectedVertecies.add(v);
    }

    // Getters
    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<Vertex> getConnectedVertecies(){
        return this.connectedVertecies;
    }

    public ArrayList<Edge> getConnectedEdges(){
        return this.connectedEdges;
    }

    public String viewConnectedVerticies(){
        if (this.connectedVertecies.isEmpty()){
            return "[]";
        }
        else {
            List<String> vertexStrings = new ArrayList<>() {};
            for (Vertex v : this.connectedVertecies) {
                vertexStrings.add(v.getName());
            }
            Set<String> vertexSet = new HashSet<String>(vertexStrings);
            return vertexSet.toString();
        }
    }

}
