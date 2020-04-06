package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import root.FileHandler;
import root.Graph;
import root.scenes.SceneData;
import root.scenes.SceneSwitcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The type Example controller.
 */
@SuppressWarnings("unchecked")
public class ExampleController  extends AbstractController {

    /**
     * The Btn main.
     */
// my fxml scene objects
    @FXML private Button btn_main, /**
     * The Btn draw.
     */
    btn_draw, /**
     * The Btn load.
     */
    btn_load, /**
     * The Btn exit.
     */
    btn_exit;
    /**
     * The Btn example one.
     */
    @FXML private Button btnExample_One, /**
     * The Btn example two.
     */
    btnExample_Two, /**
     * The Btn example three.
     */
    btnExample_Three, /**
     * The Btn example four.
     */
    btnExample_Four;
    /**
     * The Txt area example one.
     */
    @FXML private TextArea txtAreaExample_One, /**
     * The Txt area example two.
     */
    txtAreaExample_Two, /**
     * The Txt area example three.
     */
    txtAreaExample_Three, /**
     * The Txt area example four.
     */
    txtAreaExample_Four;

    /**
     * The Wm.
     */
    private final SceneSwitcher wm = new SceneSwitcher();
    /**
     * The Fh.
     */
    private final FileHandler fh = new FileHandler();
    /**
     * The Example graphs.
     */
    private HashMap<Button, Graph> exampleGraphs;
    /**
     * The Example map.
     */
    private HashMap<String, HashMap<String, ArrayList<String>>> exampleMap;
    /**
     * The Text areas.
     */
    private TextArea[] textAreas;
    /**
     * The Example.
     */
    @FXML public String example = "";

    /**
     * The Sd.
     */
// data container
    private SceneData sd = new SceneData();

    @Override
    public void initialize() {
        this.exampleMap = new HashMap<>();
        this.textAreas = new TextArea[]{txtAreaExample_One, txtAreaExample_Two,
                                        txtAreaExample_Three};//, txtAreaExample_Four};

        // load examples graph files into text areas and also hashmap
        this.loadExamples();
    }

    /**
     * Load examples.
     */
    private void loadExamples() {
        // lets load the text here to the text areas appropriately
        for (TextArea txtArea: this.textAreas) {
            String exampleTextAreaName = txtArea.getId().replace("txtArea", "").toLowerCase();
            String exampleNumber = exampleTextAreaName.split("_")[1];
            String filepath = String.format("src/example_graphs/%s/%s.txt", exampleNumber, exampleTextAreaName);
            if (Boolean.parseBoolean(System.getenv().getOrDefault("JAR_EXECUTION", "FALSE").toLowerCase())){
                filepath = String.format("\\example_graphs\\%s\\%s.txt", exampleNumber, exampleTextAreaName);
            }
            try {
                HashMap<String, ArrayList<String>> example = this.fh.readExampleFile(filepath);
                this.exampleMap.put(exampleNumber, example);
                String typeString = example.containsKey("type")?example.get("type").get(0):null;
                typeString = typeString!=null?String.format("Graph Type:\t%s", typeString):null;
                if (typeString != null) {
                    txtArea.appendText(typeString);
                }
                String connectivityString = example.containsKey("connectivity")?example.get("connectivity").toString():null;
                if (connectivityString != null) {
                    txtArea.appendText("\n\nConnectivity:\n");
                    for (String connectionString: example.get("connectivity")) {
                        txtArea.appendText(String.format("\n%s", connectionString));
                    }
                }
                txtArea.setEditable(false);
            } catch (IOException e) {
                System.out.println("\n*****\nERROR\n");
                System.out.println(e.getMessage());
                System.out.println("\n*****\n");
            }
        }
    }

    public void move_window(ActionEvent actionEvent) throws IOException {
        this.wm.swithScene(actionEvent, this.sd);
    }

    /**
     * Load example graph.
     *
     * @param actionEvent the action event
     * @throws IOException the io exception
     */
    public void load_example_graph(ActionEvent actionEvent) throws IOException {
        Button btn = (Button) actionEvent.getSource();
        String id = btn.getId().replace("btn","").toLowerCase();
        String key = id.replace("example_", "");
        HashMap<String, ArrayList<String>> exampleMapData = exampleMap.getOrDefault(key,null);
        for (String k: exampleMapData.keySet()) {
            sd.add(k, exampleMapData.get(k));
        }
        this.move_window(actionEvent);
    }

    @Override
    public void init_data(SceneData sd){

        // String s = t.get().toString();
        // txtAreaExample_One.setText(s);
        this.sd = sd;

        String s = (String) this.sd.getAll().get("data");
        txtAreaExample_One.setText(s);

    }

}
