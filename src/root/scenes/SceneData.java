package root.scenes;


import java.util.HashMap;

/**
 * The type Scene data.
 *
 * @param <T> the type parameter
 */
public class SceneData<T> {

    /**
     * The T.
     */
    private T t;
    /**
     * The T map.
     */
    private HashMap<String, T> tMap;

    /**
     * Instantiates a new Scene data.
     */
    public SceneData() {
        tMap = new HashMap<>();
    }

    /**
     * Set.
     *
     * @param t the t
     */
    public void set(T t) {
        this.t = t;
    }

    /**
     * Get t.
     *
     * @return the t
     */
    public T get() {
        return t;
    }

    /**
     * Gets all.
     *
     * @return the all
     */
    public HashMap<String, T> getAll() {
        return this.tMap;
    }

    /**
     * Add.
     *
     * @param key the key
     * @param t   the t
     */
    public void add(String key, T t) {
        tMap.put(key, t);
    }

}
