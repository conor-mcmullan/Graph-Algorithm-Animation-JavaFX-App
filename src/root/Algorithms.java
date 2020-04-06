package root;

import root.interfaces.AlgorithmsInt;
import root.scenes.AnimationData;

import java.util.*;

/**
 * The type Algorithms.
 */
public class Algorithms implements AlgorithmsInt {

    /**
     * Instantiates a new Algorithms.
     */
    public Algorithms() {}

    public HashMap<String, HashMap<String, Double>> getTraversalMap(Graph g, Vertex thisVert) {
        // step 1
        /*
            for (all w ∈ V )
                if (w == u)
                    L(w) = 0;
                else if (uw ∈ E)
                    L(w) = W(uw);
                else
                    L(w) = ∞;
        * */

        HashMap<String, HashMap<String, Double>> travelMap = new HashMap<>();
        for (Vertex vertex : g.getVertices()) {
            HashMap<String, Double> tm = new HashMap<>();
            for (Vertex v : g.getVertices()) {
                double value = Double.POSITIVE_INFINITY;
                if (thisVert.getName().equalsIgnoreCase(v.getName())) {
                    value = 0;
                }
                tm.put(v.getName(), value);
            }
            travelMap.put(vertex.getName(), tm);
        }
        return travelMap;
    }

    /**
     * Next key list.
     *
     * @param innerTMap   the inner t map
     * @param avoidStings the avoid stings
     * @return the list
     */
    public List<String> nextKey(HashMap<String, Double> innerTMap, ArrayList<String> avoidStings){
        double min = Double.POSITIVE_INFINITY;
        List<String> minKeys = new ArrayList<> ();
        HashMap<String, Double> newInnerTMap = new HashMap<String, Double> ();
        // for ease remove what is not good or will not be accepted ie. what is in the visited list
        for (String k: innerTMap.keySet()){
            if (!avoidStings.contains(k)){
                newInnerTMap.put(k, innerTMap.get(k));
            }
        }
        // using the pairings of string double as a weighting get key where keys value == min value of the set
        for(Map.Entry<String, Double> entry : newInnerTMap.entrySet()) {
            if(entry.getValue() < min) {
                min = entry.getValue();
                minKeys.clear();
            }
            if(entry.getValue() == min) {
                minKeys.add(entry.getKey());
            }
        }

        return minKeys;
    }

    public void dijkstra(Graph g, String fromVertex) {

        // get the vertex objects already created in the graph
        Vertex from = g.findYourVertex(fromVertex);

        // step 1
        HashMap<String, HashMap<String, Double>> travelMap = this.getTraversalMap(g, from);

        // step 2
        /*
            TV = {};
        */
        ArrayList<String> Tv = new ArrayList<>(){};
        List<Vertex> graphVerticies = g.getVertices();

        // set the  starting vertex
        Vertex currentVertex = from;

        // step 3
        /*
            while (TV ̸= V )
            {
                find y ∈ V − TV such that
                    L(y) = min{L(x)|x ∈ (V − TV )}
                TV = TV ∪ {y};
                for (all x ∈ (V − TV ))
                    if (yx ∈ E && L(x) > L(y) + W(yx))
                        L(x) = L(y) + W(yx);
            }
         */
        // will continue until the maximum set of vertex has been looped over
        while (Tv.size() != graphVerticies.size()) {

            // if and only if on where your leaving from
            if (currentVertex.getName().equalsIgnoreCase(from.getName())) {
                Tv.add(from.getName());
            }

            // get the connected root.Vertex of the current vertex
            List<Edge> connectingEdges = currentVertex.getConnectedEdges();
            for (Edge connE : connectingEdges) {

                // get the vertex that is not the vertex we are in current for
                Vertex altV = connE.getStart().getName().equals(currentVertex.getName()) ? connE.getEnd() : connE.getStart();

                // this will be basically the first iteration
                if (currentVertex.getName().equalsIgnoreCase(from.getName())) {
                    travelMap.get(from.getName()).put(altV.getName(), connE.getWeight());
                }
                else{
                    if (!altV.getName().equalsIgnoreCase(from.getName())) {
                        // get value of from the current value to the new value
                        double currentJourneyCost = travelMap.get(currentVertex.getName()).get(currentVertex.getName());
                        double additionalCost = connE.getWeight();
                        double costFromStartToConnectedVertex = travelMap.get(currentVertex.getName()).get(altV.getName());
                        double totalExpectedCost = currentJourneyCost + additionalCost;
                        if (totalExpectedCost < costFromStartToConnectedVertex){
                            travelMap.get(currentVertex.getName()).put(altV.getName(), totalExpectedCost);
                        }

                    }
                }

            }

            // lets get the new current value
            HashMap<String, Double> innerTMap = travelMap.get(currentVertex.getName());

            // lets get the minimum value and if its key not in Tv then
            List<String> minimumKeys = nextKey(innerTMap, Tv);

            // lets just take the 1st value
            String minKey = minimumKeys.get(0);
            travelMap.replace(minKey, innerTMap);

            // this cant be a new vertex it must be the example_graphs.one we just had
            currentVertex = g.findYourVertex(minKey);

            // when mapping values made while
            if (!Tv.contains(currentVertex.getName())) {
                Tv.add(currentVertex.getName());
            }

        }

        final HashMap<String, Double> journeyCosts = travelMap.get(currentVertex.getName());
        displayTransversedValues(journeyCosts, from);

    }

    public void displayTransversedValues(HashMap<String, Double> lv, Vertex from){
        for(String s: this.getTransversedValues(lv, from)){
            System.out.println(s);
        }
    }

    public LinkedList<String> getTransversedValues(HashMap<String, Double> lv, Vertex from) {
        LinkedList<String> travelStrings = new LinkedList<>();
        for (String key : lv.keySet()) {
            travelStrings.add(String.format("\n%s --> %s\tcost: %s", from.getName(), key, lv.get(key)));
        }
        return travelStrings;
    }

    public AnimationData getDijkstraAnimationLogInfo(AnimationData animationData) {
        String[] steps = new String[]{
            "[ALGORITHM]:\t" + "DIJKSTRA",
            "\n[STEP 1]",
                "\nfor (all w ∈ V )",
                "\n\tif (w == u)",
                "\n\t\tL(w) = 0;",
                "\n\telse if (uw ∈ E)",
                "\n\t\tL(w) = W(uw);",
                "\n\telse",
                "\n\t\tL(w) = ∞;",
            "\n\n[STEP 2]",
                "\n\t\tTV = {};",
                "\n[STEP 3]",
                "\nwhile (TV ̸= V ){",
                "\n\tfind y ∈ V − TV such that",
                "\n\t\tL(y) = min{L(x)|x ∈ (V − TV )}",
                "\n\tTV = TV ∪ {y};",
                "\n\tfor (all x ∈ (V − TV ))",
                "\n\t\tif (yx ∈ E && L(x) > L(y) + W(yx))",
                "\n\t\tL(x) = L(y) + W(yx);",
                "\n\t}"
        };
        for (String step: steps) {
            animationData.addLogInfo(step);
        }
        return animationData;
    }

    /**
     * Dijkstra animation animation data.
     *
     * @param g          the g
     * @param fromVertex the from vertex
     * @return the animation data
     */
    public AnimationData dijkstraANIMATION(Graph g, String fromVertex) {
        // SETUP: Algorithm
        Vertex from = g.findYourVertex(fromVertex);
        HashMap<String, HashMap<String, Double>> travelMap = this.getTraversalMap(g, from);
        ArrayList<String> Tv = new ArrayList<>(){};
        List<Vertex> graphVerticies = g.getVertices();
        Vertex currentVertex = from;

        // SETUP: Animation
        AnimationData animationData = new AnimationData();
        animationData = this.getDijkstraAnimationLogInfo(animationData);

        // START: Algorithm
        while (Tv.size() != graphVerticies.size()) {
            if (currentVertex.getName().equalsIgnoreCase(from.getName())) {
                animationData.addTravesal(fromVertex, fromVertex, 0.0);
                Tv.add(from.getName());
            }
            List<Edge> connectingEdges = currentVertex.getConnectedEdges();
            for (Edge connE : connectingEdges) {
                Vertex altV = connE.getStart().getName().equals(currentVertex.getName()) ? connE.getEnd() : connE.getStart();
                // [Animation]
                animationData.addTravesal(currentVertex.getName(), altV.getName(), connE.getWeight());
                if (currentVertex.getName().equalsIgnoreCase(from.getName())) {
                    travelMap.get(from.getName()).put(altV.getName(), connE.getWeight());
                    // [Animation]
                    animationData.addTravesal(currentVertex.getName(), altV.getName(), connE.getWeight());
                }
                else{
                    if (!altV.getName().equalsIgnoreCase(from.getName())) {
                        double currentJourneyCost = travelMap.get(currentVertex.getName()).get(currentVertex.getName());
                        double additionalCost = connE.getWeight();
                        double costFromStartToConnectedVertex = travelMap.get(currentVertex.getName()).get(altV.getName());
                        double totalExpectedCost = currentJourneyCost + additionalCost;
                        if (totalExpectedCost < costFromStartToConnectedVertex){
                            // [Animation]
                            animationData.addTravesal(currentVertex.getName(), altV.getName(), totalExpectedCost);
                            travelMap.get(currentVertex.getName()).put(altV.getName(), totalExpectedCost);
                        }
                        // [Animation]
                        animationData.addTravesal(currentVertex.getName(), altV.getName(), totalExpectedCost);

                    }
                }
            }
            HashMap<String, Double> innerTMap = new HashMap<>(travelMap.get(currentVertex.getName()));
            List<String> minimumKeys = nextKey(innerTMap, Tv);

            // handles from 1 to any num unconnected Nodes
            String minKey = currentVertex.getName();
            if (!minimumKeys.isEmpty()){
                minKey = minimumKeys.get(0);
            }

            travelMap.replace(minKey, innerTMap);
            currentVertex = g.findYourVertex(minKey);
            if (!Tv.contains(currentVertex.getName())) {
                Tv.add(currentVertex.getName());
            }
        }
        // END: Algorithm

        // FINALISE: Animation
        animationData.setTravesalValues(travelMap.get(currentVertex.getName()));

        // RETURN: Animation Data
        return animationData;
    }
}
