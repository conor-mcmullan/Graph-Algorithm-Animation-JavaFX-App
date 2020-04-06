package root;

import com.sun.tools.javac.Main;
import redis.clients.jedis.util.IOUtils;
import root.interfaces.FileHandlerInt;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The type File handler.
 */
public class FileHandler implements FileHandlerInt {

    /**
     * Instantiates a new File handler.
     */
    public FileHandler(){}

    /**
     * Removed last char string.
     *
     * @param mystr the mystr
     * @return the string
     */
    private String removedLastChar(String mystr){
        return mystr.substring(0, mystr.length()-1);
    }

    /**
     * Multi remove strings string.
     *
     * @param mystr        the mystr
     * @param replaceChars the replace chars
     * @return the string
     */
    private String multiRemoveStrings(String mystr, String[] replaceChars){
        for (String str: replaceChars){
            mystr = mystr.replace(str, "");
        }

        if (mystr.endsWith(",")){
            mystr = removedLastChar(mystr);
        }

        return mystr;
    }

    /**
     * Display parsed file.
     *
     * @param filepath the filepath
     * @throws IOException the io exception
     */
    protected void displayParsedFile(String filepath) throws IOException {
        System.out.println();
        for(String str: this.readFile(filepath)){
            System.out.println(str);
        }
    }

    public Graph fileToGraph(String filepath) throws IOException {
        ArrayList<String> fileLines = this.readFile(filepath);
        List<Vertex> vertices = new ArrayList<Vertex>(){};
        HashMap<String, Vertex> vertMap = new HashMap<String, Vertex>();
        List<Edge> edges = new ArrayList<Edge>(){};

        // create the container of vertex objects
        for (String line: fileLines){

            // should be length of 3
            String[] splitLine = line.split(",");
            Vertex from = new Vertex(splitLine[0]);
            String fromName = from.getName();
            Vertex to = new Vertex(splitLine[1]);
            String toName = to.getName();
            double weight = Double.parseDouble(splitLine[2]);

            // create the vertex objects and if they exist they will be over written
            if (vertMap.containsKey(fromName)) {
                from = vertMap.get(fromName);
            }
            else{
                vertMap.put(fromName, from);
                vertices.add(from);
            }

            if (vertMap.containsKey(toName)) {
                to = vertMap.get(toName);
            }
            else{
                vertMap.put(toName, to);
                vertices.add(to);
            }

            // create the connecting edge
            Edge from_to_w = new Edge(from, to, weight);
            edges.add(from_to_w);

        }

        return new Graph(vertices, edges);
    }

    public ArrayList<String> readFile(String filepath) throws IOException {
        File file = new File(filepath);
        BufferedReader csvReader = new BufferedReader(new FileReader(file));
        ArrayList<String> fileLines = new ArrayList<String>();
        String line;
        String[] removeStrings = new String[]{"{", "}", "(", ")", " ", "\"", "'"};

        // read until the last line
        while ((line = csvReader.readLine()) != null) {
            // replace the chars that aren't needed
            line = this.multiRemoveStrings(line, removeStrings);

            // separated by line - ideal file
            if (line.split(",").length == 3) {
                fileLines.add(line);
            }
            else{
                Matcher m = Pattern.compile("(.,){2}(\\d)+").matcher(line);
                while (m.find()) {
                    fileLines.add(m.group());
                }
            }
        }
        csvReader.close();
        return fileLines;
    }

    public HashMap<String, ArrayList<String>> readExampleFile(String filepath) throws IOException {
        filepath = filepath.replace("\\", "/");
        File file;
        BufferedReader csvReader;
        // if executed from a jar the contents of files cannot be read using IO
        String jarExecution = System.getenv().getOrDefault("JAR_EXECUTION", "FALSE");
        if (Boolean.parseBoolean(jarExecution.toLowerCase())) {
            InputStream is = getClass().getResourceAsStream(filepath);
            csvReader = new BufferedReader(new InputStreamReader(is));
        }
        else{
            file = new File(filepath);
            csvReader = new BufferedReader(new FileReader(file));
        }
        String line;
        // read until the last line
        int splitNumber = 0;
        HashMap<Integer, ArrayList<String>> fileCategoryLinesSplitMap = new HashMap<>();
        while ((line = csvReader.readLine()) != null) {
            boolean divisorLine = line.contains("-");
            if (divisorLine) {
                splitNumber++;
            }
            boolean isNewKey = fileCategoryLinesSplitMap.containsKey(splitNumber);
            if (!isNewKey){
                fileCategoryLinesSplitMap.put(splitNumber, new ArrayList<String>());
            }
            if (!divisorLine){
                fileCategoryLinesSplitMap.get(splitNumber).add(line);
            }
        }

        // tidy up the read in file contents
        HashMap<String, ArrayList<String>> categoryMapContents = new HashMap<>();
        for(ArrayList<String> values: fileCategoryLinesSplitMap.values()){
            ArrayList<String> tmpValues = new ArrayList<>();
            if (!values.isEmpty()) {
                String newMapKey = values.remove(0).replace("#", " ").strip();
                // we can alter the values here
                for (String str : values) {
                    String[] removeStrings = new String[]{"{", "}", "(", ")", " ", "\"", "'"};
                    str = this.multiRemoveStrings(str.trim(), removeStrings);
                    if (!str.isEmpty()) {
                        tmpValues.add(str);
                    }
                }
                categoryMapContents.put(newMapKey, tmpValues);
            }
        }

        // not longer used map object
        fileCategoryLinesSplitMap.clear();
        csvReader.close();
        return categoryMapContents;
    }

    public String graphToFile(Graph g, String filepath){
        final String line = "---------------";
        StringBuilder sb = new StringBuilder();
        sb.append(line);
        sb.append("\n# type");
        sb.append("\ngraph\n");
        sb.append(line);
        sb.append("\n# verticies\n");
        sb.append(g.getVertexNames().toString());
        sb.append("\n"+line);
        sb.append("\n# connectivity\n");
        sb.append(g.getViewableEdges(true));
        sb.append("\n"+line);
        sb.append("\n# positions\n");
        sb.append(g.getPositionalString());
        sb.append("\n"+line);
        if(filepath.equalsIgnoreCase("${sd.data.example}")){return sb.toString();}
        else{
            this.writeFile(sb.toString(), filepath);
        }
        return sb.toString();
    }

    public void writeFile(String data, String filepath) {
        File file = new File(filepath);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
