package root.interfaces;

import root.Graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The interface File handler int.
 */
public interface FileHandlerInt {
    /**
     * File to graph graph.
     *
     * @param filepath the filepath
     * @return the graph
     * @throws IOException the io exception
     */
//String removedLastChar(String mystr);
    //String multiRemoveStrings(String mystr, String[] replaceChars);
    //void displayParsedFile(String filepath) throws IOException;
    Graph fileToGraph(String filepath) throws IOException;

    /**
     * Read file array list.
     *
     * @param filepath the filepath
     * @return the array list
     * @throws IOException the io exception
     */
    ArrayList<String> readFile(String filepath) throws IOException;

    /**
     * Read example file hash map.
     *
     * @param filepath the filepath
     * @return the hash map
     * @throws IOException the io exception
     */
    HashMap<String, ArrayList<String>> readExampleFile(String filepath) throws IOException;

    /**
     * Graph to file string.
     *
     * @param g        the g
     * @param filepath the filepath
     * @return the string
     */
    String graphToFile(Graph g, String filepath);

    /**
     * Write file.
     *
     * @param data     the data
     * @param filepath the filepath
     */
    void writeFile(String data, String filepath);
}
