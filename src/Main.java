import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import redis.clients.jedis.Jedis;
import root.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        String menuPath = "resources/main.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(menuPath));
        Scene scene = new Scene(root);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.setTitle("Graph Algorithm Animation App | Main");
        stage.setScene(scene);
        stage.getIcons().add(new Image("images/appIcon.png"));
        stage.show();
    }

    private static void exampleHardCoded() {
        System.out.println("\nHARD CODED");

        // list of objects
        List<Vertex> vertices = new ArrayList<Vertex>() {
        };
        List<Edge> edges = new ArrayList<Edge>() {
        };

        // create vertex objects
        Vertex a = new Vertex("A");
        Vertex b = new Vertex("B");
        Vertex c = new Vertex("C");
        Vertex d = new Vertex("D");
        Vertex e = new Vertex("E");
        Vertex f = new Vertex("F");

        // add all of the vertex objects to the list
        vertices.add(a);
        vertices.add(b);
        vertices.add(c);
        vertices.add(d);
        vertices.add(e);
        vertices.add(f);

        // create the edges between the vertex
        Edge a_b_2 = new Edge(a, b, 2);
        Edge a_f_2 = new Edge(a, f, 2);
        Edge a_e_1 = new Edge(a, e, 1);
        Edge b_c_3 = new Edge(b, c, 3);
        Edge c_d_3 = new Edge(c, d, 3);
        Edge c_e_2 = new Edge(c, e, 2);
        Edge e_f_4 = new Edge(e, f, 4);

        // add all of the edges to the list
        edges.add(a_b_2);
        edges.add(a_f_2);
        edges.add(a_e_1);
        edges.add(b_c_3);
        edges.add(c_d_3);
        edges.add(c_e_2);
        edges.add(e_f_4);

        // Example root.Graph
        /*
        // {
        //     {"A", "B", 1},
        //     {"B", "C", 1},
        //     {"B", "D", 1},
        //     {"C", "D", 1},
        //     {"E", "D", 1}
        // }
         */

        // create the graph
        Graph g = new Graph(vertices, edges);
        g.dijkstra("A");

    }

    private static void exampleFromFile() throws IOException {
        System.out.println("\nFROM FILE");
        FileHandler fh = new FileHandler();
        Graph fileg = fh.fileToGraph("src/graphfiles/example_6.txt");
        fileg.dijkstra("a");
    }

    private static void exampleGraphToFile() throws IOException {
        System.out.println("\nTo FILE");
        FileHandler fh = new FileHandler();
        Graph fileg = fh.fileToGraph("src/graphfiles/example_6.txt");
        fileg.dijkstra("a");
        fh.graphToFile(fileg, "src/graphfiles/example_written_10.txt");
    }

    private static void demos(){
        // Example root.Graph - Hard Coded
        //exampleHardCoded();

        // Example root.Graph - From File
        //exampleFromFile();

        // Example root.Graph - To File
        //exampleGraphToFile();
    }

    private static void clearJedisCache(){
        try {
            // connect to the Redis instance the script just launched

            String hostString = "redis://localhost:6379";
            Jedis jedis = new Jedis("localhost");

            // deletes from current db (#1)
            jedis.flushDB();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void executeCommands() throws IOException, InterruptedException {
        File tempScript = createTempScript();
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } finally {
            tempScript.delete();
        }
    }

    private static File createTempScript() throws IOException {
        File tempScript = File.createTempFile("script", null);
        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);
        printWriter.println("#!/bin/bash");
        //printWriter.println("echo 'Redis Server: Starting'");
        printWriter.println("redis-server --port 6379 > /dev/null 2>&1 &");
        // `command` > /dev/null 2>&1 (command stdout and stderr are redirected to /dev/null)
        // `&` (will background the running command)
        // printWriter.println("echo 'Redis Server: Running'");
        printWriter.close();
        return tempScript;
    }

    private static String getPathToWindowsExeRedisServer(){
        String path = (new File("")).getAbsolutePath().replace('\\', '/');
        File f = new File(path);
        if (!path.toLowerCase().contains("csc3002-computer-science-project-pk01")){
            path += "/csc3002-computer-science-project-pk01";
            f = new File(path);
        }
        if (f.list().length>0) {
            if (!path.contains("src") && Arrays.asList(f.list()).contains("src")) {
                path += "/src";
            }
        }
        if (path.contains("src")) {
            path = (new File(path)).getAbsolutePath().replace('\\', '/');
            // f = new File(path);
            path += String.format("/windowsRedisExecutables/%sbit/redis-server.exe", OSDetails.getOperationBitSize());
        }
        else{
            System.out.println(String.format("\n*****\n%s\n*****\n",
                    "Error:\t Paths to Redis.exe invalid"));
        }
        return path;
    }

    public static void main(String[] args) throws IOException {
        if (OSDetails.getOperatingSystemName().equalsIgnoreCase("windows")) {
            // Window will not execute the script
            String path = getPathToWindowsExeRedisServer().replace("/", "\\\\");
            Runtime.getRuntime().exec(path, null, new File(String.format("%s:\\program files\\", path.split("")[0])));
        }
        else {
            // MacOs & Linux: will do the same bash server start script
            try {
                // Starting a TMP Bash Script to launch Redis Server
                executeCommands();
            } catch (Exception e) {
                System.out.println(String.format("\nError:\t%s", e));
            }
        }

        // clearing any pre stored key values
        clearJedisCache();

        // Launching Main App
        launch(args);

        // Examples for demos:
        // demos();

    }

}
