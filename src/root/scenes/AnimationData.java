package root.scenes;

import root.interfaces.AnimationDataInt;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * The type Animation data.
 */
@SuppressWarnings("CanBeFinal")
public class AnimationData implements AnimationDataInt {
    /**
     * The Lv.
     */
    private HashMap<String, Double> lv;
    /**
     * The Sb.
     */
    private StringBuilder sb;
    /**
     * The Travesals.
     */
    private LinkedList<LinkedHashMap<String, String>> travesals;

    /**
     * Instantiates a new Animation data.
     */
    public AnimationData() {
        this.sb = new StringBuilder();
        this.travesals = new LinkedList<>();
        this.lv = new HashMap<>();
    }

    // Getters
    public void addLogInfo(String stdout){
        this.sb.append(String.format("%s", stdout));
    }

    public void addTravesal(String to, String from, Double cost){
        LinkedHashMap<String, String> innerTravesals = new LinkedHashMap<>();
        innerTravesals.put("to", String.valueOf(to));
        innerTravesals.put("from", String.valueOf(from));
        innerTravesals.put("cost", String.valueOf(cost));
        if (!this.travesals.contains(innerTravesals)){
            this.travesals.add(innerTravesals);
        }
    }

    public void setTravesalValues(HashMap<String, Double> costMap){
        this.lv = new HashMap<>(new TreeMap<String, Double>(costMap));
    }

    // Setters
    public StringBuilder getLogInfo(){
        return this.sb;
    }

    public LinkedList<LinkedHashMap<String, String>> getTravesals(){
        return this.travesals;
    }

    public HashMap<String, Double> getTravesalValues(){
        return this.lv;
    }

}
