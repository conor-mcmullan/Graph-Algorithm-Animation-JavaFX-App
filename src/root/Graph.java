package root;

import root.interfaces.GraphInt;
import root.scenes.AnimationData;

import java.util.*;

/**
 * The type Graph.
 */
public class Graph implements GraphInt {

    /**
     * The Vertex map.
     */
    private HashMap<String, Vertex> vertexMap = new HashMap<>();
    /**
     * The Vertices.
     */
    private List<Vertex> vertices;
    /**
     * The Edges.
     */
    private List<Edge> edges;
    /**
     * The A.
     */
    private final Algorithms a = new Algorithms();

    /**
     * Instantiates a new Graph.
     *
     * @param vertices the vertices
     * @param edges    the edges
     */
    public Graph(List<Vertex> vertices, List<Edge> edges){
        this.vertices = vertices;
        this.edges = edges;
        linkVerticies();
    }

    /**
     * Link verticies.
     */
    private void linkVerticies(){
        /*
         * this function will go for each root.Vertex look at all of the edges that exist
         * get the edges that contain the the current vertex and get the alt vertex
         * append this edge and vertex to the the list of vertex connected edges
         * and vertex connected nodes
         */
        for (Vertex v: this.vertices){
            // link the verticies with edges
            for (Edge e: this.edges){
                List<Vertex> edgeVerticies = e.getVerticies();
                if(edgeVerticies.contains(v)){
                    for(Vertex compareToVert: edgeVerticies){
                        if (!compareToVert.getName().equalsIgnoreCase(v.getName())){
                            v.connectToEdge(e);
                            v.connectToVertex(compareToVert);
                        }
                    }
                }
            }
            // link the verticies into a searchable format
            this.vertexMap.put(v.getName(), v);
        }
    }

    public void dijkstra(String from){
        a.dijkstra(this, from);
    }

    public List<Edge> getEdges(){
        return this.edges;
    }

    public List<Vertex> getVertices(){
        return this.vertices;
    }

    public String getViewableEdges(boolean includeWeights){

        ArrayList<String> sb = new ArrayList<>();
        sb.add("{\n");

        for (Edge e: this.edges) {
            sb.add("{");
            for (String str: e.getStringOveriew(includeWeights)){
                sb.add(str);
                sb.add(", ");
            }

            int lastIndex = sb.size() - 1;
            String lastElement = sb.get(lastIndex);
            if (lastElement.equals(", ")){
                sb.remove(lastIndex);
            }
            sb.add("},");


            lastIndex = sb.size() - 1;
            lastElement = sb.get(lastIndex);
            if (lastElement.equals(",")){
                sb.remove(lastIndex);
            }
            sb.add("\n");

        }
        sb.add("}");

        StringBuilder overview = new StringBuilder();
        for (String str: sb){
            overview.append(str);
        }

        return overview.toString();
    }

    public ArrayList<String> getVertexIDList(){
        ArrayList<String> names = new ArrayList<>();
        for (Vertex v: this.vertices){
            names.add(v.getName());
        }
        Collections.sort(names);
        return names;
    }

    public Set<String> getVertexNames(){
        return vertexMap.keySet();
    }

    public AnimationData dijkstraANIMATION(String from) {
        return a.dijkstraANIMATION(this, from);
    }

    public Vertex findYourVertex(String vertexName){
        return this.vertexMap.getOrDefault(vertexName, new Vertex(vertexName));
    }

    public LinkedHashMap<String, LinkedHashMap<String, Integer>> getAdjacencyMatrixMap(){
        LinkedHashMap<String, Integer> base = new LinkedHashMap<>();
        for (String s:  this.getVertexIDList()) {
            base.put(s, 0);
        }

        LinkedHashMap<String, LinkedHashMap<String, Integer>> hm = new LinkedHashMap<>();
        for (String s: this.getVertexIDList()) {
            hm.put(s, new LinkedHashMap<>(base));
        }

        for (String strV: this.getVertexIDList()) {

            Vertex v = this.findYourVertex(strV);
            for (Vertex vConn : v.getConnectedVertecies()) {
                hm.get(v.getName()).replace(vConn.getName(), 1);
            }
        }

        return hm;
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public String getAdjacencyMatrix(){

        // String Manipulation of HashMap
        String adjStr = this.getAdjacencyMatrixMap().toString();
        adjStr = adjStr.replace("},", "\n");
        adjStr = adjStr.replace("={", "\t");
        adjStr = adjStr.replace("}}", "");
        adjStr = adjStr.replace("{", "");
        adjStr = adjStr.replaceAll("[aA-zZ]+=", "");
        adjStr = adjStr.replace("  ", " ");
        adjStr = " " + adjStr;

        // HashMap String to String Array
        String[] sx = adjStr.split("\n");
        for(int i=0; i <sx.length; i++){
            sx[i] = sx[i].trim();
            sx[i] = "|\t"+ sx[i].replace("\t", "\t|\t");
            sx[i] = sx[i].replace(", ", "\t|\t") + "\t|";
        }

        // String Array to String on New Lines
        String x = "";
        for (String s: sx) {
            x += "\n"+s;
        }

        // String Header - Adjacency Matrix
        String header = "|\t" + " " + "\t|\t";
        for (String s: this.getVertexIDList()){
            header += s + "\t|\t";
        }
        x = header + x;

        // Returning String Adjacency Matrix
        x = this.vertices.isEmpty() ? "" : x;
        return x;
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public String getPositionalString(){
        String pos = this.vertices.isEmpty() ? "{}" : "{\n";
        if (!this.vertices.isEmpty()) {
            for (Vertex v : this.vertices) {
                pos += String.format("{%s, %s, %s},\n", v.getName(), v.getX(), v.getY());
            }
            pos += "}";
        }
        return pos;
    }

    @SuppressWarnings("unused")
    public void viewConnectivity(){
        // view the results
        System.out.println("\nConnectivity:\n");
        for (Vertex v: this.vertices){
            System.out.println(v.getName() + ":\t" + v.viewConnectedVerticies());
        }

    }

    @SuppressWarnings("unused")
    public void viewConnectivity(Vertex vert){
        // view the results
        System.out.printf("\nConnectivity of:\t%s", vert.getName());
        for (Vertex v: this.vertices){
            if (v.equals(vert)) {
                System.out.println("\n" + v.getName() + ":\t" + v.viewConnectedVerticies());
            }
        }

    }

}
