package root.interfaces;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * The interface Animation data int.
 */
public interface AnimationDataInt {
    /**
     * Add log info.
     *
     * @param stdout the stdout
     */
    void addLogInfo(String stdout);

    /**
     * Add travesal.
     *
     * @param to   the to
     * @param from the from
     * @param cost the cost
     */
    void addTravesal(String to, String from, Double cost);

    /**
     * Sets travesal values.
     *
     * @param costMap the cost map
     */
    void setTravesalValues(HashMap<String, Double> costMap);

    /**
     * Gets log info.
     *
     * @return the log info
     */
    StringBuilder getLogInfo();

    /**
     * Gets travesals.
     *
     * @return the travesals
     */
    LinkedList<LinkedHashMap<String, String>> getTravesals();

    /**
     * Gets travesal values.
     *
     * @return the travesal values
     */
    HashMap<String, Double> getTravesalValues();
}
