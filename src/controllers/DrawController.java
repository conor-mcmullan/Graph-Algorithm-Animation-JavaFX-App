package controllers;


import com.sun.javafx.perf.PerformanceTracker;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import redis.clients.jedis.Jedis;
import root.Edge;
import root.Graph;
import root.Vertex;
import root.customDrawObjects.EdgeLine;
import root.customDrawObjects.VertexCircle;
import root.scenes.AnimationData;
import root.scenes.AnimationState;
import root.scenes.AnimationStep;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;


/**The type Draw controller.*/
@SuppressWarnings("unchecked")
public class DrawController extends AbstractController {

    // drawOLD.fxml [ANIMATION CONTROLS]
    /**The H box animation button bar.*/
    @FXML private HBox hBoxAnimationButtonBar;
    /**The Pane drawing all objects pane.*/
    @FXML private Pane paneDrawingAllObjectsPane;
    @FXML private BorderPane bpBackgroundForAll;
    /**The Txt area adjacency matrix.*/
    @FXML private TextArea txtAreaAdjacencyMatrix;
    /**The Txt area algorithm steps.*/
    @FXML private TextArea txtAreaAlgorithmSteps;
    /**The Txt area travel cost.*/
    @FXML private TextArea txtAreaTravelCost;
    /**The Txt area console.*/
    @FXML private TextArea txtAreaConsole;
    /**The Txt area system info.*/
    @FXML private TextArea txtAreaSystemInfo;
    /**The Txt area algorithm output.*/
    @FXML private TextArea txtAreaAlgorithmOutput;
    /**The G area pane.*/
    @FXML private Pane gAreaPane;
    /**The Anchor pane graph altering buttons.*/
    @FXML private AnchorPane anchorPaneGraphAlteringButtons;
    /**The Radio btn add vertex.*/
    @FXML private RadioButton radioBtnAdd_Vertex;
    /** The Radio btn add edge.*/
    @FXML private RadioButton radioBtnAdd_Edge;
    /**The Radio btn remove object.*/
    @FXML private RadioButton radioBtnRemove_Object;
    /**The C box algo.*/
    @FXML private ChoiceBox<String> cBoxAlgo;
    /**The Toggle fps.*/
    @FXML private ToggleButton toggleFPS;
    /**The Img runnable.*/
    @FXML private ImageView imgRunnable;
    /**The Txt field fps value.*/
    @FXML private TextField txtFieldFPSValue;
    /**The C box animation speed.*/
    @FXML private ChoiceBox<Double> cBoxAnimationSpeed;
    /**The C box starting node.*/
    @FXML private ChoiceBox<String> cBoxStartingNode;
    /**The C box default controls.*/
    @FXML private ChoiceBox<String> cBoxDefaultControls;
    /**The Lbl animation counter max value.*/
    @FXML private Label lblAnimationCounterMaxValue;
    /**The Lbl animation counter current value.*/
    @FXML private Label lblAnimationCounterCurrentValue;
    /**The Lbl animation counter remaining value.*/
    @FXML private Label lblAnimationCounterRemainingValue;

    // Animation State Variables
    /**The Current state.*/
    private AnimationState CURRENT_STATE = AnimationState.STOPPED;
    /**The Step.*/
    private AnimationStep STEP = AnimationStep.BEGIN;
    /**The Animation starting node.*/
    private String animationStartingNode = "";
    /**The Default algorithm cb string.*/
    private final String defaultAlgorithmCBString = "--Select Algorithm--";

    private boolean CHANGE_IN_STEP_VALUE = false;

    /**The Current animation step.*/
    private int CURRENT_ANIMATION_STEP = 0;
    /**The Max animation steps.*/
    private int MAX_ANIMATION_STEPS = 0;
    /**The Counter.*/
    private int counter = 0;
    /**The Tracker.*/
    private PerformanceTracker tracker;
    /**The Frame rate meter.*/
    private AnimationTimer frameRateMeter;
    /**The Timeline.*/
    private Timeline timeline;
    /**The Spare timeline.*/
    private Timeline spareTimeline;
    /**The constant radius.*/
    private static final int radius = 30;

    // Scene State Variables
    /**The Example.*/
    private Boolean EXAMPLE = false;
    /**The Add vertex.*/
    private Boolean ADD_VERTEX = false;
    /**The Add edge.*/
    private Boolean ADD_EDGE = false;
    /**The Remove object.*/
    private Boolean REMOVE_OBJECT = false;
    /**The Translation.*/
    private double translation = 200;

    // Scene + Animation + Graph: Container Objects
    /**The Graph.*/
    private Graph graph;
    /**The Edges.*/
    private ArrayList<Edge> edges = new ArrayList<>();
    /**The Vertexes.*/
    private ArrayList<Vertex> vertexes = new ArrayList<>();
    /**The Clicked nodes.*/
    private ArrayList<StackPane> clickedNodes = new ArrayList<>();
    /**The Runable images map.*/
    private HashMap<Boolean, Image> runableImagesMap = new HashMap<>();
    /**The Travel cost.*/
    private HashMap<String, Double> travelCost = new HashMap<>();
    /**The Animation steps.*/
    private LinkedList<LinkedHashMap<String, String>> animationSteps = new LinkedList<>();

    // The Color Objects used by the Scene
    /**The Node normal.*/
    private final Color nodeNormal = Color.WHITE;
    /**The Line normal.*/
    private final Color lineNormal = Color.BLACK;
    /**The Using.*/
    private final Color using = Color.ORANGE;
    /**The Complete.*/
    private final Color complete = Color.CORNFLOWERBLUE;

    /**
     * The Jedis.
     * Redis: Small in memory Key Value Store
     */
    final Jedis jedis = new Jedis("localhost");

    @Override
    public void runLater() {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            // FPS Counter Code
            if (tracker!=null){
                if (frameRateMeter==null) {
                    frameRateMeter=new AnimationTimer() {
                        @Override
                        public void handle(long now) {
                            if (toggleFPS.isSelected()) {
                                // Original 0ms
                                txtFieldFPSValue.setText(String.format("%.1f fps", getFPS()));
                            }
                        }
                    };
                    frameRateMeter.start();
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // get system level info and place in system info tab text area
        try {
            this.init_system_level_info();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // this will stop these images loading constantly
        this.init_runnable_images_map();

        // load algorithm options into the choicebox
        this.init_algorithm_choices();

        // load the animation speeds for automation
        this.init_animation_speed_choices();

        // load animation control options into choicebox
        this.init_default_control_options();

        // set the file chooser with some filters
        this.init_file_chooser_options();

        Platform.runLater(() -> {
            if (this.sd.getAll().size() > 0) {
                this.loadExample();
            }
            // Scene only initialised when runLater closes
            // [drawOLD.fxml]
            // this.tracker = PerformanceTracker.getSceneTracker(this.paneDrawingAllObjectsPane.getScene());

            // [draw.fxml] bpBackgroundForAll
            this.tracker = PerformanceTracker.getSceneTracker(this.bpBackgroundForAll.getScene());
        });
    }

    /**
     * Alphabet map hash map.
     * returns a mapping of where a character falls in the alphabet numerically.
     * @return the hash map
     */
    private HashMap<Integer, String> alphabetMap(){
        HashMap<Integer, String> alphabetPositional = new HashMap<>();
        for (int i=0; i < 26; i++) {
            alphabetPositional.put(i+1, String.valueOf((char)('A' + i)));
        }
        return alphabetPositional;
    }

    /**
     * Gets fps.
     * A float to represent the current tick rate of the JavaFX Scene,
     * we can see impact on increaseing system load by more objects.
     * @return the fps
     */
    private float getFPS () {
        float fps = tracker.getAverageFPS();
        tracker.resetAverageFPS();
        return fps;
    }

    /**
     * Sys log.
     * Appedns Strings to a textArea.
     * @param sysString the sys string
     */
    private void sysLog(String sysString){
        this.txtAreaSystemInfo.appendText(sysString);
    }

    /**
     * Format size string.
     * System Bytes in use becomes the expected correct measurement.
     * @param v the v
     * @return the string
     */
    public String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    /**
     * Init animation speed choices.
     */
    private void init_animation_speed_choices(){
        this.cBoxAnimationSpeed.setValue(1.0);
        this.cBoxAnimationSpeed.getItems().addAll(0.50, 1.00, 1.50, 2.00);
    }

    /**
     * Init system level info.
     * @throws IOException the io exception
     */
    private void init_system_level_info() throws IOException {
        try {
            com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean)
                    ManagementFactory.getOperatingSystemMXBean();
            this.sysLog("\n\n[JAVA]");
            this.sysLog(String.format("\nJava Version\t\t | \t%s", System.getProperty("java.version")));
            this.sysLog("\n\n[OS]");
            this.sysLog(String.format("\nOS Name\t\t\t | \t%s", osBean.getName()));
            this.sysLog(String.format("\nVersion\t\t\t | \t%s", osBean.getVersion()));
            this.sysLog(String.format("\nArchitecture\t\t | \t%s", osBean.getArch()));
            this.sysLog("\n\n[CPU]");
            this.sysLog(String.format("\nAvailable Processors\t | \t%s", osBean.getAvailableProcessors()));
            this.sysLog(String.format("\nSystem Cpu Load\t\t | \t%s", osBean.getSystemCpuLoad()));
            this.sysLog(String.format("\nProcess Cpu Load\t\t | \t%s", osBean.getProcessCpuLoad()));
            this.sysLog(String.format("\nProcess Cpu Time\t\t | \t%s", osBean.getProcessCpuTime()));
            this.sysLog("\n\n[SWAP MEMORY]");
            this.sysLog(String.format("\nFree Swap Space Size\t\t | \t%s", this.formatSize(osBean.getFreeSwapSpaceSize())));
            this.sysLog(String.format("\nTotal Swap Space Size\t\t | \t%s", this.formatSize(osBean.getTotalSwapSpaceSize())));
            this.sysLog("\n\n[SYSTEM MEMORY]");
            this.sysLog(String.format("\nCommitted Virtual Memory Size\t\t | \t%s", this.formatSize(osBean.getCommittedVirtualMemorySize())));
            this.sysLog(String.format("\nTotal Physical Memory Size\t\t\t | \t%s", this.formatSize(osBean.getTotalPhysicalMemorySize())));
            this.sysLog("\n\n[SYSTEM LOAD]");
            this.sysLog(String.format("\nSystem Load Average\t\t | \t%s", osBean.getSystemLoadAverage()));
        }catch (Exception e){
            this.log(Arrays.toString(e.getStackTrace()));
            throw new IOException("Possible JAVA Bean Error Obtain System Info");
        }

    }

    /**
     * Init runnable images map.
     */
    private void init_runnable_images_map(){
        this.runableImagesMap.put(true, new Image("images/runGood.png"));
        this.runableImagesMap.put(false, new Image("images/runBad.png"));
    }

    /**
     * Init default control options.
     */
    private void init_default_control_options(){
        this.cBoxDefaultControls.setValue("manual");
        this.cBoxDefaultControls.getItems().addAll("manual", "automated");
    }

    /**
     * Init file chooser options.
     */
    private void init_file_chooser_options(){
        // only text files wil be accepted
        this.fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("text file", "*.txt"));
    }

    /**
     * Init algorithm choices.
     */
    private void init_algorithm_choices() {
        // load the values of the algorithm functions we have coded into here
        this.cBoxAlgo.setValue(this.defaultAlgorithmCBString);
        this.cBoxAlgo.getItems().add(this.defaultAlgorithmCBString);
        this.cBoxAlgo.getItems().add("dijkstra");
    }

    /**
     * Update graph altering states.
     */
    private void updateGraphAlteringStates() {
        this.ADD_VERTEX = this.radioBtnAdd_Vertex.isSelected();
        this.REMOVE_OBJECT = this.radioBtnRemove_Object.isSelected();
        this.ADD_EDGE = this.radioBtnAdd_Edge.isSelected();
    }

    /**
     * Get char value string.
     * Gets the least alphabetical vertex and get the alphabetical value for this char.
     * @param idx the idx
     * @return the string
     */
    private String getCharValue(int idx){
        return this.alphabetMap().get(idx);
    }

    /**
     * Get char value int.
     *  return (int)c - Integer.valueOf('A') + 1;
     *  The next integer value for a character is returned base on the param c
     * @param c the c
     * @return the int
     */
    private int getCharValue(char c){
        return (new ArrayList<>(this.alphabetMap().values())).indexOf(String.valueOf(c))+1;
    }

    /**
     * Next vertex id string.
     * Ensuring the Vertex IDs don't duplicate and that the next ID is always next insequence.
     * @return the string
     */
    public String nextVertexID(){
        if (this.graph != null) {
            if (!this.graph.getVertexIDList().isEmpty()) {

                // sort the list alphabetically ascending
                ArrayList<String> vs = new ArrayList<>(this.graph.getVertexIDList());
                vs.sort(String::compareToIgnoreCase);

                // if the list has been sorted the very last item should be the one to increase further
                String lowestOrdered = vs.get(vs.size()-1);

                // store the char values individually
                ArrayList<Integer> charValuesList = new ArrayList<>();

                // split the string by each char and iterate
                for (char c: lowestOrdered.toCharArray()){
                    charValuesList.add(this.getCharValue(c));
                }

                // get the last char value
                int lastCharValue = charValuesList.get(charValuesList.size()-1);
                int nextCharValue = lastCharValue;
                if (lastCharValue<26){
                    nextCharValue++;
                }
                else {
                    // A=1 ... Z=26
                    nextCharValue=1;
                }

                String x = getCharValue(nextCharValue);
                if (lowestOrdered.length()>1 || lowestOrdered.equalsIgnoreCase("Z")){
                    // if the last char if Z add this char on else replace with the new char
                    if (lowestOrdered.length()>1){
                        char[] cArr = lowestOrdered.toCharArray();
                        int lastIndex = cArr.length - 1;
                        char lastChar = cArr[lastIndex];
                        int posIdx = this.getCharValue(lastChar);
                        if (posIdx<26){
                            cArr[lastIndex] = x.charAt(0);
                        }
                        else if (posIdx == 26){
                            return String.valueOf(cArr) + "A";
                        }
                        return String.valueOf(cArr);
                    }

                    if (lowestOrdered.equalsIgnoreCase("Z")){
                        return lowestOrdered + x;
                    }

                    return lowestOrdered + x;
                }
                return x;
            }
        }
        // more user friendly than 'Z'
        return "A";
    }

    /**
     * Update graph from scene objects.
     * All scene manipiulations occur within this function to keep everything looking good.
     */
    public void updateGraphFromSceneObjects(){
        HashMap<String, Vertex> holdingMapper = new HashMap<>();
        ArrayList<HashMap<String, String>> edgeInformation = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.vertexes = new ArrayList<>();
        for (Object obj: gAreaPane.getChildren()){
            StackPane spObj = (StackPane) obj;
            if (spObj.getChildren().get(0) instanceof VertexCircle){
                // Vertex - index 0 = VertexCircle
                Text vcTxt = (Text) spObj.getChildren().get(1);
                Vertex v = new Vertex(vcTxt.getText(),vcTxt.getX(),vcTxt.getY());
                this.vertexes.add(v);
                holdingMapper.put(vcTxt.getText(), v);
            }
            else if (spObj.getChildren().get(0) instanceof EdgeLine){
                // Edge
                EdgeLine el = (EdgeLine) spObj.getChildren().get(0);
                Text elTxt = (Text)((StackPane) spObj.getChildren().get(1)).getChildren().get(0);
                // Connectivity Between Edges + Weighting of the Edge
                HashMap<String, String> edgeHashMap = new HashMap<>();
                edgeHashMap.put("id", el.getId());
                edgeHashMap.put("weight", elTxt.getText());
                edgeInformation.add(edgeHashMap);
            }
        }

        for (HashMap<String, String> hm: edgeInformation){
            double weight = Double.parseDouble(hm.get("weight"));
            ArrayList<String> vertexList = new ArrayList<>();
            String connectivityNodes = hm.get("id").replace("edgeLine", "");
            for (String node: connectivityNodes.split("stack")){
                if (!node.isEmpty()){
                    vertexList.add(node);
                }
            }

            Vertex from = holdingMapper.get(vertexList.get(0));
            Vertex to = holdingMapper.get(vertexList.get(1));
            Edge e = new Edge(from, to, weight);
            this.edges.add(e);
        }

        // the graph object is reset/updated
        this.graph = new Graph(this.vertexes, this.edges);
        this.setStartingNodeOptions();
        this.txtAreaAdjacencyMatrix.setText(this.graph.getAdjacencyMatrix());
        this.updateGraphToSceneData();
        this.updateRunnableImage(this.graph.getVertexNames().size()>0);
    }

    /**
     * Sets starting node options.
     * Populates the options using the Graph g Vertexes
     */
    private void setStartingNodeOptions() {
        if (this.graph!=null){
            // get the graph vertexes
            Set<String> dropDownOptionsSet = new LinkedHashSet<>(this.graph.getVertexIDList());
            ArrayList<String> dropDownOptions = new ArrayList<>(dropDownOptionsSet);
            Collections.sort(dropDownOptions);
            this.cBoxStartingNode.getItems().clear();
            if(!dropDownOptions.isEmpty()){
                this.cBoxStartingNode.setValue(dropDownOptions.get(0));
                this.cBoxStartingNode.getItems().addAll(dropDownOptions);
            }
        }
    }

    /**
     * Add new edge.
     * Checks if the user has selected 2 Vetexes and will open an dialog window to continue.
     * @throws IOException the io exception
     */
    private void addNewEdge() throws IOException {
        double weight = 0;
        if(this.clickedNodes.size()==2){
            // StackPane: To & From
            VertexCircle to = (VertexCircle) this.clickedNodes.get(0).getChildren().get(0);
            VertexCircle from = (VertexCircle) this.clickedNodes.get(1).getChildren().get(0);

            // check for object / graph connectivity
            if(this.graph==null){
                // at this point there is at least going to be 2 object/vertex to connect with possibly
                this.updateGraphFromSceneObjects();
            }
            else{
                Vertex toVertex = this.graph.findYourVertex(to.getId().replace("vertex", ""));
                Vertex fromVertex = this.graph.findYourVertex(from.getId().replace("vertex", ""));

                // List A: for comparing equal Vertex inner Objects
                ArrayList<Vertex> myVertexList = new ArrayList<>(){{add(toVertex);add(fromVertex);}};

                // Iterate Over all of the Graph Edge Objects to find existence of Edge
                for(Edge e: this.graph.getEdges()){

                    // List B: for comparing equal Vertex inner Objects
                    List<Vertex> graphVertexList = e.getVerticies();

                    // An Existing Edge
                    if(myVertexList.containsAll(graphVertexList) && graphVertexList.containsAll(myVertexList)){
                        weight = e.getWeight();
                        break;
                    }
                }

                // opening the edge editor
                this.edgeEditor(weight, this.jedis);
            }
        }
    }

    /**
     * Is string double boolean.
     * A Parser tesing function for reading double (number) values out of a String
     * @param numberString the number string
     * @return the boolean
     */
    private boolean isStringDouble(String numberString){
        boolean isCastable = true;
        try {
            Double.parseDouble(numberString);
        } catch (NumberFormatException e) {
            isCastable = false;
        }
        return isCastable;
    }

    /**
     * Edge editor.
     * A special function that opens a new JavaFX FXML File in a window as a modal.
     * The modal gets SceneData but cant return data, the selected data is validated
     * and placed into memory cache store Redis/Jedis and a global Cache Variable
     * 'Success' is set True if it was valid. When the global value of 'Success'
     * is deemed True the remaining key values are extracted and applied to the
     * Scene Objects matching the Vertex and Edge Names.
     * @param weight the weight
     * @param jedis  the jedis
     * @throws IOException the io exception
     */
    @SuppressWarnings("unchecked")
    private void edgeEditor(double weight, Jedis jedis) throws IOException {
        String path = "../resources/~.fxml".replace("~", "edge_editor");
        String defaultWarningText = "Ensure Vertex are unique and Weight is numeric";
        AnchorPane root = FXMLLoader.load(getClass().getResource(path));
        // default failure set to determine method of window onCloseRequest action
        jedis.set("SUCCESS", "FALSE");
        for (Object o: ((Pane) root.getChildren().get(0)).getChildren()) {
            if (o instanceof GridPane){
                for (Object objs: ((GridPane) o).getChildren()) {
                    // load the vertexes into the choice box
                    if (objs instanceof ChoiceBox) {
                        ((ChoiceBox) objs).getItems().addAll(this.graph.getVertexNames());
                        ((ChoiceBox) objs).setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                // get the id of the object to update
                                String objectIDToUpdate = (((ChoiceBox) objs).getId().equalsIgnoreCase("cBoxVertex1")) ? "textVChoice1" : "textVChoice2";
                                for (Object objects: ((GridPane) o).getChildren()) {
                                    if (objects instanceof TextField) {
                                        if (((TextField) objects).getId().equalsIgnoreCase(objectIDToUpdate)) {
                                            ((TextField) objects).setText(((ChoiceBox) objs).getValue().toString());
                                        }
                                    }
                                }

                            }
                        });
                    }
                    // load the data from the existing edge into the TextFields on the popup window
                    if (objs instanceof TextField) {
                        if (((TextField) objs).getId().equalsIgnoreCase("textVChoice1")) {
                            ((TextField) objs).setText(this.clickedNodes.get(0).getId().replace("stack", ""));
                        }
                        if (((TextField) objs).getId().equalsIgnoreCase("textVChoice2")) {
                            ((TextField) objs).setText(this.clickedNodes.get(1).getId().replace("stack", ""));
                        }
                        // get the existing edge weight - else it will start at 0.0
                        if (((TextField) objs).getId().equalsIgnoreCase("textWChoice")) {
                            ((TextField) objs).setText(String.valueOf(weight));
                        }
                    }
                }
            }
            else{
                // load default label warning
                if (o instanceof Label){
                    if (((Label) o).getId().equalsIgnoreCase("lblWarningsLabel")) {
                        ((Label) o).setText(defaultWarningText);
                    }
                }

                // setting button event handlers
                if (o instanceof Button) {

                    // event handler for CancelButton
                    if (((Button) o).getId().equalsIgnoreCase("btnCancelEdgeEdit")){
                        ((Button) o).setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                                //window.close();
                                Event.fireEvent(window, new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));

                            }
                        });
                    }

                    // event handler for SaveButton
                    if (((Button) o).getId().equalsIgnoreCase("btnSaveEdge")){
                        ((Button) o).setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                ArrayList<String> strVertexInEdgeEditList = new ArrayList<>();
                                String strWeighValue = String.valueOf(0.0);
                                boolean areVertexesAcceptable = false;
                                boolean isCastable = false;
                                String invalidWeightText = "WARNING: weight is not acceptable numeric value";
                                String sameVertexErrorString = "WARNING: Vertex Must Be Unique !";
                                // casting the weight value and updating the warnings based on weight
                                for (Object o: ((Pane) root.getChildren().get(0)).getChildren()) {
                                    if (o instanceof GridPane) {
                                        for (Object objs : ((GridPane) o).getChildren()) {
                                            if (objs instanceof TextField) {
                                                if (((TextField) objs).getId().equalsIgnoreCase("textVChoice1") ||
                                                   ((TextField) objs).getId().equalsIgnoreCase("textVChoice2")) {
                                                    strVertexInEdgeEditList.add(((TextField) objs).getText());
                                                }
                                                else if (((TextField) objs).getId().equalsIgnoreCase("textWChoice")){
                                                    // validation cast code here
                                                    strWeighValue = ((TextField) objs).getText();
                                                    isCastable = isStringDouble(strWeighValue);
                                                    // lblWarningsLabel - update
                                                    for (Object objects: ((Pane) root.getChildren().get(0)).getChildren()) {
                                                        if (objects instanceof Label) {
                                                            if (((Label) objects).getId().equalsIgnoreCase("lblWarningsLabel")) {
                                                                if (isCastable) {
                                                                    ((Label) objects).setText(defaultWarningText);
                                                                }
                                                                else{
                                                                    ((Label) objects).setText(invalidWeightText);
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                for (Object objects: ((Pane) root.getChildren().get(0)).getChildren()) {
                                    if (objects instanceof Label) {
                                        if (((Label) objects).getId().equalsIgnoreCase("lblWarningsLabel")) {

                                            // check if vertex 1 & vertex 2 are the same
                                            areVertexesAcceptable = !(strVertexInEdgeEditList.get(0).equalsIgnoreCase(strVertexInEdgeEditList.get(1)));
                                            String warningsToSet = defaultWarningText;

                                            if (!areVertexesAcceptable){
                                                if (!isCastable){
                                                    warningsToSet = invalidWeightText + "\n" + sameVertexErrorString;
                                                }
                                                else {
                                                    warningsToSet = sameVertexErrorString;
                                                }
                                                ((Label) objects).setText(warningsToSet);
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (isCastable && areVertexesAcceptable){
                                    jedis.set("SUCCESS", "TRUE");
                                    jedis.set("vertex1", strVertexInEdgeEditList.get(0));
                                    jedis.set("vertex2", strVertexInEdgeEditList.get(1));
                                    jedis.set("edgeWeight", strWeighValue);

                                    // CLOSE THE SCENE
                                    Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                                    //window.close();
                                    Event.fireEvent(window, new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
                                }
                            }
                        });
                    }
                }
            }
        }

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Edge Editor");
        stage.show();

        // the window on close action
        stage.setOnCloseRequest(e -> {
            edgeManipulation();
        });
    }

    /**
     * Clicked text area.
     * The EventHandler Applied to all objects appearing on the scene.
     * @param mouseEvent the mouse event
     */
    public void clickedTextArea(MouseEvent mouseEvent) {
        this.updateGraphAlteringStates();
        // check if gAreaPane has any items - if so update graph object
        if (!this.gAreaPane.getChildren().isEmpty()){
            this.updateGraphFromSceneObjects();
        }
        if (this.ADD_VERTEX) {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            String nextID = this.nextVertexID();
            VertexCircle vc = new VertexCircle(nextID, x, y, radius, this.nodeNormal, this.lineNormal);
            StackPane sp = vc.getStack();

            sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    String clickedStackObjectID = ((StackPane)mouseEvent.getSource()).getId();
                    if (REMOVE_OBJECT && !ADD_VERTEX && !ADD_EDGE){
                        ArrayList<StackPane> removeSPs = new ArrayList<>();
                        StackPane clkOnSP = (StackPane)mouseEvent.getSource();
                        for (Object childObject: gAreaPane.getChildren()){
                            // the objects should always be StackPane
                            StackPane childSP = (StackPane) childObject;
                            if (childSP.getId().replace("stackEdgeLine", "").contains(clickedStackObjectID)){
                                removeSPs.add(childSP);
                            }
                        }
                        // add the clicked on stackpane to be removed
                        removeSPs.add(clkOnSP);
                        // if there are objects to be removed remove them then
                        if (!removeSPs.isEmpty()){
                            for (StackPane sp: removeSPs){
                                gAreaPane.getChildren().remove(sp);
                            }
                        }
                        // update the graph
                        updateGraphFromSceneObjects();
                    }

                    if (ADD_EDGE && !REMOVE_OBJECT && !ADD_VERTEX){
                        if (!sp.getChildren().get(0).getStyleClass().contains("selected")){
                            sp.getChildren().get(0).getStyleClass().add("selected");
                        }
                        internalAddEdge(sp);
                    }
                }
            });
            gAreaPane.getChildren().add(sp);
        }
        this.updateGraphFromSceneObjects();
        this.setStartingNodeOptions();
    }

    /**
     * Internal add edge.
     * Ensures there is not a double click event on same
     * node prior to opening the common dialog window.
     * @param stkPane the stk pane
     */
    private void internalAddEdge(StackPane stkPane){
        if (clickedNodes.size()==2){
            clickedNodes.clear();
            clickedNodes.add(stkPane);
        }
        else {
            // not a double click event on same node
            if(!clickedNodes.contains(stkPane)) {
                clickedNodes.add(stkPane);
            }
        }
        try {
            addNewEdge();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Load example.
     * The ExampleController will have opened and parsed a file into SceneData Memory,
     * once read the move_window() function detecting the data will push to this function.
     * This function will unpack the data and create a Graph on the Scene and a Graph Object
     * in the Background with corresponding Edge and Vertex Objects.
     */
    private void loadExample() {
        this.EXAMPLE = true;
        if (this.sd.getAll().size() > 0) {
            ArrayList<StackPane> stacks = new ArrayList<>();
            HashMap<String, HashMap<String, Double>> posMap = new HashMap<>();
            // draws the circles
            for (String pos: (ArrayList<String>) this.sd.getAll().get("positions")) {
                String[] splitPosition = pos.split(",");
                String name = String.valueOf(splitPosition[0]);
                double x = Double.parseDouble(splitPosition[1]);
                double y = Double.parseDouble(splitPosition[2]);
                VertexCircle vc = new VertexCircle(name, x, y, radius, this.nodeNormal, this.lineNormal);
                StackPane sp = vc.getStack();
                stacks.add(sp);
                HashMap<String, Double> stackItems = new HashMap<>();
                stackItems.put("x", x);
                stackItems.put("y", y);
                posMap.put(sp.getId(), stackItems);
            }

            HashMap<Integer, HashMap<StackPane, StackPane>> maps = new HashMap<>();
            HashMap<String, StackPane> stckMap = new HashMap<>();
            for (StackPane stkP: stacks) { stckMap.put(stkP.getId(), stkP); }

            // tracks undrawn objects
            HashSet<String> preDrawn = new HashSet<>();

            // loads connectivity of graph
            ArrayList<String> connectivity = (ArrayList<String>) this.sd.getAll().get("connectivity");
            for (String connStr: connectivity){
                String[] splitPosition = connStr.split(",");
                String paneTo = String.format("stack%s", splitPosition[0]);
                String paneFrom = String.format("stack%s", splitPosition[1]);
                preDrawn.add(paneTo);
                preDrawn.add(paneFrom);
                StackPane sPto = stckMap.get(paneTo);
                StackPane sPfrom = stckMap.get(paneFrom);
                HashMap<StackPane, StackPane> spHm = new HashMap<>();
                spHm.put(sPto, sPfrom);
                int size = maps.size()+1;
                maps.put(size, spHm);
            }

            // Draws - Unconnected Vertex
            HashSet<String> hs = new HashSet<>(posMap.keySet());
            hs.removeAll(preDrawn);
            if (!hs.isEmpty()){
                for(String stkName: hs){
                    StackPane to = stckMap.get(stkName);
                    to.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            String clickedStackObjectID = ((StackPane)mouseEvent.getSource()).getId();
                            if (REMOVE_OBJECT && !ADD_VERTEX && !ADD_EDGE){
                                StackPane clkOnSP = (StackPane)mouseEvent.getSource();
                                gAreaPane.getChildren().remove(clkOnSP);
                                ArrayList<StackPane> removeSPs = new ArrayList<>();
                                for (Object childObject: gAreaPane.getChildren()){
                                    // the objects should always be StackPane
                                    StackPane childSP = (StackPane) childObject;
                                    if (childSP.getId().replace("stackEdgeLine", "").contains(clickedStackObjectID)){
                                        removeSPs.add(childSP);
                                    }
                                }
                                if (!removeSPs.isEmpty()){
                                    for (StackPane sp: removeSPs){
                                        gAreaPane.getChildren().remove(sp);
                                    }
                                }
                            }
                            if (ADD_EDGE && !REMOVE_OBJECT && !ADD_VERTEX){
                                if (!to.getChildren().get(0).getStyleClass().contains("selected")){
                                    to.getChildren().get(0).getStyleClass().add("selected");
                                }
                                internalAddEdge(to);
                            }
                        }
                    });
                    gAreaPane.getChildren().add(to);
                }
            }

            ArrayList<String> existsyet = new ArrayList<>();
            for (Integer i: maps.keySet()) {
                HashMap<StackPane, StackPane> stackLookup = maps.get(i);

                StackPane to = stackLookup.keySet().iterator().next();
                to.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        String clickedStackObjectID = ((StackPane)mouseEvent.getSource()).getId();
                        if (REMOVE_OBJECT && !ADD_VERTEX && !ADD_EDGE){
                            StackPane clkOnSP = (StackPane)mouseEvent.getSource();
                            gAreaPane.getChildren().remove(clkOnSP);
                            ArrayList<StackPane> removeSPs = new ArrayList<>();
                            for (Object childObject: gAreaPane.getChildren()){
                                // the objects should always be StackPane
                                StackPane childSP = (StackPane) childObject;
                                if (childSP.getId().replace("stackEdgeLine", "").contains(clickedStackObjectID)){
                                    removeSPs.add(childSP);
                                }
                            }
                            if (!removeSPs.isEmpty()){
                                for (StackPane sp: removeSPs){
                                    gAreaPane.getChildren().remove(sp);
                                }
                            }
                        }
                        if (ADD_EDGE && !REMOVE_OBJECT && !ADD_VERTEX){
                            if (!to.getChildren().get(0).getStyleClass().contains("selected")){
                                to.getChildren().get(0).getStyleClass().add("selected");
                            }
                            internalAddEdge(to);
                        }
                    }
                });

                StackPane from = stackLookup.values().iterator().next();
                from.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        String clickedStackObjectID = ((StackPane)mouseEvent.getSource()).getId();
                        if (REMOVE_OBJECT && !ADD_VERTEX && !ADD_EDGE){
                            StackPane clkOnSP = (StackPane)mouseEvent.getSource();
                            gAreaPane.getChildren().remove(clkOnSP);
                            ArrayList<StackPane> removeSPs = new ArrayList<>();
                            for (Object childObject: gAreaPane.getChildren()){
                                // the objects should always be StackPane
                                StackPane childSP = (StackPane) childObject;
                                if (childSP.getId().replace("stackEdgeLine", "").contains(clickedStackObjectID)){
                                    removeSPs.add(childSP);
                                }
                            }
                            if (!removeSPs.isEmpty()){
                                for (StackPane sp: removeSPs){
                                    gAreaPane.getChildren().remove(sp);
                                }
                            }
                        }
                        if (ADD_EDGE && !REMOVE_OBJECT && !ADD_VERTEX){
                            if (!from.getChildren().get(0).getStyleClass().contains("selected")){
                                from.getChildren().get(0).getStyleClass().add("selected");
                            }
                            internalAddEdge(from);
                        }
                    }
                });

                HashMap<String, Double> stacksPositions1 = posMap.get(to.getId());
                double x1 = stacksPositions1.get("x");
                double y1 = stacksPositions1.get("y");

                HashMap<String, Double> stacksPositions2 = posMap.get(from.getId());
                double x2 = stacksPositions2.get("x");
                double y2 = stacksPositions2.get("y");
                double radius = ((VertexCircle)to.getChildren().get(0)).getRadius();

                // new object naming id
                String n = to.getId()+from.getId();

                // Gets the actual weight of the edge betwen 2 nodes
                String displayText = String.valueOf(0.0);
                for (String connStr: connectivity){
                    ArrayList<String> splitPos = new ArrayList<>(Arrays.asList(connStr.split(",")));
                    double w = Double.parseDouble(splitPos.remove(2));
                    ArrayList<String> nSplit = new ArrayList<>(Arrays.asList(n.split("stack")));
                    nSplit.remove(0);
                    if(nSplit.containsAll(splitPos) && splitPos.containsAll(nSplit)){
                        displayText = String.valueOf(w);
                        break;
                    }
                }

                double lineWidth = 2;
                double textSize = 12;

                EdgeLine el = new EdgeLine(n, displayText, radius, x1, y1, x2, y2,
                        lineWidth, textSize, this.lineNormal, this.lineNormal);
                StackPane connL = el.getStack();

                connL.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (REMOVE_OBJECT && !ADD_EDGE && !ADD_VERTEX){
                            log("REMOVING EDGE");
                            gAreaPane.getChildren().remove(connL);
                        }
                    }
                });

                if (!existsyet.contains(connL.getId())) gAreaPane.getChildren().add(connL);
                existsyet.add(connL.getId());

                // manipulating the scene drawing order
                if (!existsyet.contains(to.getId())) {
                    gAreaPane.getChildren().add(to);
                    existsyet.add(to.getId());
                }
                else{
                    gAreaPane.getChildren().remove(to);
                    gAreaPane.getChildren().add(to);
                }

                if (!existsyet.contains(from.getId())) {
                    gAreaPane.getChildren().add(from);
                    existsyet.add(from.getId());
                }
                else{
                    gAreaPane.getChildren().remove(from);
                    gAreaPane.getChildren().add(from);
                }
            }
        }
        this.updateGraphFromSceneObjects();
        this.setStartingNodeOptions();
    }

    /**
     * Change status add vertex.
     */
    public void changeStatusAddVertex(){
        this.ADD_VERTEX = this.radioBtnAdd_Vertex.isSelected();
    }

    /**
     * Change status remove object.
     */
    public void changeStatusRemoveObject(){
        this.REMOVE_OBJECT = this.radioBtnRemove_Object.isSelected();
    }

    /**
     * Change status add edge.
     */
    public void changeStatusAddEdge(){
        this.ADD_EDGE = this.radioBtnAdd_Edge.isSelected();}

    /**
     * Gets my editing edge.
     * Simple access to Edge and EdgeLine that needs updated.
     * Matches on Both Objects in the List as the only requirement.
     * @param interestedVertexObjects the interested vertex objects
     * @return the my editing edge
     */
    private Edge getMyEditingEdge(ArrayList<Vertex> interestedVertexObjects) {
        for (Edge e : this.graph.getEdges()) {
            if (e.getVerticies().containsAll(interestedVertexObjects)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Get vertex list in edge edit array list.
     * Will add Objects from the Graph Object to a temp container list when matched on.
     * @param vertex1 the vertex 1
     * @param vertex2 the vertex 2
     * @return the array list
     */
    private ArrayList<Vertex> getVertexListInEdgeEdit(String vertex1, String vertex2){
        ArrayList<Vertex> interestedVertexObjects = new ArrayList<>();
        for (Vertex v: this.graph.getVertices()) {
            if (interestedVertexObjects.size() == 2){
                // we should leave as we may have grown larger in no. vertex count
                return interestedVertexObjects;
            }
            if (v.getName().equalsIgnoreCase(vertex1) || v.getName().equalsIgnoreCase(vertex2)){
                // found the Vertex Object
                interestedVertexObjects.add(v);
            }
        }
        return interestedVertexObjects;
    }

    /**
     * Updating edge weight.
     * Using the Vertexes used in editing an Edge,
     * this new double param will be set to the new Object Weight Value.
     * @param vertex1       the vertex 1
     * @param vertex2       the vertex 2
     * @param newEdgeWeight the new edge weight
     */
    private void updatingEdgeWeight(String vertex1, String vertex2, double newEdgeWeight){
        String eLID1 = String.format("edgeLinestack%sstack%s", vertex1, vertex2);
        String eLID2 = String.format("edgeLinestack%sstack%s", vertex2, vertex1);
        for (Object obj: gAreaPane.getChildren()) {
            StackPane spObj = (StackPane) obj;
            if (spObj.getChildren().get(0) instanceof EdgeLine) {
                String edgeLineID = spObj.getChildren().get(0).getId();
                if (edgeLineID.equalsIgnoreCase(eLID1) || edgeLineID.equalsIgnoreCase(eLID2)){
                    StackPane e = ((EdgeLine) spObj.getChildren().get(0)).getStack();
                    for (Object o: e.getChildren()) {
                        if (o instanceof StackPane){
                            for (Object obelsp: ((StackPane) o).getChildren()) {
                                ((Text) obelsp).setText(String.valueOf(newEdgeWeight));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Edge manipulation.
     * The function checking for 'Success' after the EdgeEditor Window has been closed.
     */
    private void edgeManipulation() {
        // get casted isSucces to boolean
        if (Boolean.parseBoolean(jedis.get("SUCCESS").toLowerCase())){
            // the variables reetreived from redis/jedis
            String vertex1 = jedis.get("vertex1");
            String vertex2 = jedis.get("vertex2");
            double edgeWeight = Double.parseDouble(jedis.get("edgeWeight"));

            // the vertex objects we clicked on
            ArrayList<Vertex> interestedVertexObjects = getVertexListInEdgeEdit(vertex1, vertex2);

            // the edge object in question for edit
            Edge e = getMyEditingEdge(interestedVertexObjects);

            if (e == null){
                creatingNewEdge(vertex1, vertex2, edgeWeight);
            }else{
                // detect if there is a change as this may have been nothing but a save
                if (edgeWeight != e.getWeight()) {
                    // update existing edge weight
                    updatingEdgeWeight(vertex1, vertex2, edgeWeight);
                }
            }
        }

        if (!clickedNodes.isEmpty()){
            for (StackPane sp: clickedNodes){
                sp.getChildren().get(0).getStyleClass().remove("selected");
            }
        }

        // update the graph from gAreaPane
        this.updateGraphFromSceneObjects();
    }

    /**
     * Get clicked vertex circles array list.
     * @param vertex1 the vertex 1
     * @param vertex2 the vertex 2
     * @return the array list
     */
    private ArrayList<VertexCircle> getClickedVertexCircles(String vertex1, String vertex2){
        ArrayList<VertexCircle> clickedVCs = new ArrayList<>();
        for (Object obj: gAreaPane.getChildren()){
            StackPane spObj = (StackPane) obj;
            if (spObj.getChildren().get(0) instanceof VertexCircle){
                // Vertex Circle
                VertexCircle vc = (VertexCircle) spObj.getChildren().get(0);
                if (vc.getId().replace("vertex", "").equalsIgnoreCase(vertex1) ||
                    vc.getId().replace("vertex", "").equalsIgnoreCase(vertex2)){
                    clickedVCs.add(vc);
                }
            }
        }

        // this needs to be returned in the order the use has wished perhaps the first time
        if (!clickedVCs.get(0).getId().replace("vertex", "").equalsIgnoreCase(vertex1)){
            // NOT in user clicked order
            // add the first element copy to last and size = 3 then remove index 0 to make it in correct order
            clickedVCs.add(clickedVCs.get(0));
            clickedVCs.remove(0);
        }
        return clickedVCs;
    }

    /**
     * Creating new edge.
     * @param vertex1    the vertex 1
     * @param vertex2    the vertex 2
     * @param edgeWeight the edge weight
     */
    private void creatingNewEdge(String vertex1, String vertex2, double edgeWeight) {
        // get the VertexCircle from the background selection
        ArrayList<VertexCircle> vcClicked = getClickedVertexCircles(vertex1, vertex2);
        String n = vcClicked.get(0).getStack().getId()+vcClicked.get(1).getStack().getId();
        int r = radius;
        EdgeLine el = new EdgeLine(n,
                                    String.valueOf(edgeWeight),
                                    Math.abs(r),
                                    Math.abs(vcClicked.get(0).getCenterX())+r,
                                    Math.abs(vcClicked.get(0).getLayoutY())+(r*2),
                                    Math.abs(vcClicked.get(1).getLayoutX())+(r*2),
                                    Math.abs(vcClicked.get(1).getLayoutY())+(r*2),
                                    2, 12,
                                    this.lineNormal, this.lineNormal);
        StackPane connL = el.getStack();
        connL.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                log("connecting edge has been clicked");
                if (REMOVE_OBJECT){
                    gAreaPane.getChildren().remove(connL);
                }
            }
        });

        // adding the line to the screen
        gAreaPane.getChildren().add(connL);

        // re draws the objects on the screen so you never see the true line ends
        this.reDrawSceneObjects();
    }

    /**
     * Re draw scene objects.
     * The way objects get drawn to the scene need to be in a
     * certain order to ensure they are visible to the user.
     */
    public void reDrawSceneObjects(){
        // list to hold indexes of items to remove and re-draw
        ArrayList<StackPane> stackObjects = new ArrayList<StackPane>();
        for (Object o: gAreaPane.getChildren()) {
            StackPane sp = (StackPane) o;
            if (!sp.getId().contains("stackEdgeLine")){
                stackObjects.add(sp);
            }
        }
        // if there are items to re-draw on the screen
        if (!stackObjects.isEmpty()){
            for (StackPane sp: stackObjects) {
                gAreaPane.getChildren().remove(sp);
                gAreaPane.getChildren().add(sp);
            }
        }
    }

    private LinkedList<LinkedHashMap<String, String>> parseAnimationData(AnimationData animationData){
        LinkedList<LinkedHashMap<String, String>> animations = animationData.getTravesals();
        LinkedList<LinkedHashMap<String, String>> newAnimations = new LinkedList<>();
        HashSet<String> container = new HashSet<>();
        for(LinkedHashMap<String, String> lhm: animations){
            String nodeTo = lhm.get("to");
            String nodeFrom = lhm.get("from");
            String costOf = lhm.get("cost");
            String x = String.format("%s|%s|%s", nodeTo, nodeFrom, costOf);
            String y = String.format("%s|%s|%s", nodeFrom, nodeTo, costOf);
            if(!container.contains(x) && !container.contains(y)){
                container.add(x);
                container.add(y);
                if (animationData.getTravesalValues().get(nodeFrom) == Double.parseDouble(costOf)){
                    newAnimations.add(lhm);
                }
            }
        }
        System.out.println(newAnimations);

        return newAnimations;
    }


    /**
     * Dijkstra animation steps linked list.
     * The Wrapper function to Execute the selected algorithm,
     * and will return the data in a nicely formatted mannner
     * @param from the from
     * @return the linked list
     */
    private LinkedList<LinkedHashMap<String, String>> dijkstraAnimationSteps(String from){
        AnimationData animationData = this.graph.dijkstraANIMATION(from);
        LinkedList<LinkedHashMap<String, String>> animations = this.parseAnimationData(animationData);

        // getting the costings out
        this.travelCost = new HashMap<>(animationData.getTravesalValues());

        // Setting: txtAreaTravelCost
        this.txtAreaTravelCost.clear();
        for(String v: this.graph.getVertexIDList()){
            this.txtAreaTravelCost.appendText(
            String.format("\n|\t%s\t|\t%s\t|\t%s\t\t|",
                    from, v, animationData.getTravesalValues().get(v)));
        }

        // we can use this to create animation steps for un connected nodes
        LinkedHashSet<String> animatedNodes = new LinkedHashSet<>();

        // Setting: txtAreaAlgorithmOutput
        this.txtAreaAlgorithmOutput.clear();
        for(LinkedHashMap<String, String> lhm: animations){
            String nodeTo =  lhm.get("to");
            String nodeFrom =  lhm.get("from");
            this.txtAreaAlgorithmOutput.appendText(String.format("\n%s\t->\t%s\t=\t%s", nodeTo, nodeFrom, lhm.get("cost")));
            // add the nodes to the list
            animatedNodes.add(nodeTo);
            animatedNodes.add(nodeFrom);
        }

        LinkedHashSet<String> diff = new LinkedHashSet<>(this.graph.getVertexIDList());

        // Completion of Animation Steps
        if (!animatedNodes.isEmpty()){
            diff.removeAll(animatedNodes);
        }else{
            // the diff/the graph only contains unconnected nodes
            log("NO ANIMATED NODES");
        }
        log("\nDifference:\t"+diff.toString());
        if (!diff.isEmpty()){
            for (String node: diff){
                LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
                lhm.put("to", node);
                lhm.put("from", node);
                lhm.put("cost", String.valueOf(0));
                animations.add(lhm);
                this.txtAreaAlgorithmOutput.appendText(String.format("\n%s\t->\t%s\t=\t%s", node, node, "0"));
            }
        }

        this.txtAreaAlgorithmSteps.setText(animationData.getLogInfo().toString());

        return animations;
    }

    /**
     * Update runnable image.
     * The Graph Object will decide if the algorithm can even be executed.
     * @param runnable the runnable
     */
    private void updateRunnableImage(boolean runnable){
        this.imgRunnable.setImage(this.runableImagesMap.get(runnable));
    }

    /**
     * Display all animation controls.
     */
    private void displayAllAnimationControls(){
        this.hBoxAnimationButtonBar.setTranslateX(0);
        for (Object o: this.hBoxAnimationButtonBar.getChildren()) {
            ((VBox) o).setVisible(true);
        }
    }

    /**
     * Reset animation details.
     */
    private void resetAnimationDetails(){
        this.lblAnimationCounterMaxValue.setText("0");
        this.lblAnimationCounterCurrentValue.setText("0");
        this.lblAnimationCounterRemainingValue.setText("0");
        this.CURRENT_ANIMATION_STEP = 0;
        this.CURRENT_STATE = AnimationState.STOPPED;
        this.STEP = AnimationStep.BEGIN;
        this.animationSteps.clear();
        this.txtAreaAlgorithmOutput.clear();
        this.txtAreaTravelCost.clear();
        this.txtAreaAlgorithmSteps.clear();
    }

    /**
     * Reset animation.
     */
    private void resetAnimation(){
        this.createVisibleTravelCostLabels("");
        this.displayAllAnimationControls();
        this.resetAnimationDetails();
        this.setGraphAlteringControls(false);
        this.colorReseting(true);
    }

    /**
     * Color reseting.
     * @param reset the reset
     */
    private void colorReseting(boolean reset){
        for (Object obj: gAreaPane.getChildren()) {
            StackPane sp = (StackPane) obj;
            if (sp.getId().contains("stackEdgeLine")){
                if (reset){ ((Line)sp.getChildren().get(0)).setStroke(this.lineNormal); }
                else{ ((Line)sp.getChildren().get(0)).setStroke(this.using); }
            }
            else{
                if (reset){ ((Circle)sp.getChildren().get(0)).setFill(this.nodeNormal); }
                else{ ((Circle)sp.getChildren().get(0)).setFill(this.using); }
            }
        }
    }

    /**
     * Update animation step count labels.
     */
    private void updateAnimationStepCountLabels(){
        this.lblAnimationCounterMaxValue.setText(String.valueOf(
                this.MAX_ANIMATION_STEPS));
        this.lblAnimationCounterCurrentValue.setText(String.valueOf(
                Math.min(this.CURRENT_ANIMATION_STEP+1,
                        Math.min(this.CURRENT_ANIMATION_STEP+2,
                                this.MAX_ANIMATION_STEPS))));
        this.lblAnimationCounterRemainingValue.setText(String.valueOf(
                Math.abs(this.MAX_ANIMATION_STEPS -
                        Math.min(this.CURRENT_ANIMATION_STEP+1,
                                Math.min(this.CURRENT_ANIMATION_STEP+2,
                                        this.MAX_ANIMATION_STEPS)))));
    }

    /**
     * Automated animation steps.
     * Not as simple as a Loop over the manual controls due to
     * the need to 'complete' execution within an EventHandler.
     * Will work out animation steps and tick to the next step
     * based on a Timer and TimerTask.
     * @throws InterruptedException the interrupted exception
     */
    private void automatedAnimationSteps() throws InterruptedException {
        this.updateAnimationStepCountLabels();
        LinkedList<Integer> objIndexes = new LinkedList<>();
        for (int i = 0; i < this.animationSteps.size(); i++) {
            String to = this.animationSteps.get(i).get("to");
            String from = this.animationSteps.get(i).get("from");
            String cost = this.animationSteps.get(i).get("cost");
            String toID = String.format("stack%s", to);
            String fromID = String.format("stack%s", from);
            String edgeLineToFrom = String.format("stackEdgeLine%s%s", toID, fromID);
            String edgeLineFromTo = String.format("stackEdgeLine%s%s", fromID, toID);
            // [NODES] - indexes of StackPanes
            int idxTo = this.getBackgroundObjectIndex(toID);
            int idxFrom = this.getBackgroundObjectIndex(fromID);
            // [EDGE LINE] - indexes of EdgeLines
            int idxEdgeLine = Math.max(this.getBackgroundObjectIndex(edgeLineToFrom), this.getBackgroundObjectIndex(edgeLineFromTo));
            objIndexes.add(idxTo);
            objIndexes.add(idxFrom);
            objIndexes.add(idxEdgeLine);
        }

        // HOW LONG THE STEP TAKES
        int stepTime = 2;
        // HOW MANY TIME THS STEP REPEATS
        int numStepRepeats = objIndexes.size();

        this.counter = 0;
        this.spareTimeline = new Timeline(new KeyFrame(Duration.seconds(stepTime), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean multiple = (counter+1)%3!=0;
                // non multiples of thre
                if (multiple) {
                    Color existingColor = (Color) ((VertexCircle) ((StackPane) gAreaPane.getChildren().get(objIndexes.get(counter))).getChildren().get(0)).getFill();
                    if(existingColor.equals(nodeNormal) || existingColor.equals(using)){
                        ((VertexCircle) ((StackPane) gAreaPane.getChildren().get(objIndexes.get(counter))).getChildren().get(0)).setFill(complete);
                    }
                }
                else{
                    // multiple of three
                    if (counter>numStepRepeats){
                        counter--;
                    }
                    if (objIndexes.get(counter)>-1) {
                        Color existingLineColor = (Color) ((EdgeLine) ((StackPane) gAreaPane.getChildren().get(objIndexes.get(counter))).getChildren().get(0)).getStroke();
                        if(existingLineColor.equals(lineNormal) || existingLineColor.equals(using)) {
                            ((EdgeLine) ((StackPane) gAreaPane.getChildren().get(objIndexes.get(counter))).getChildren().get(0)).setStroke(complete);
                        }
                    }

                    // needs to stop
                    Timer tmr = new Timer();
                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {
                            ArrayList<String> ids = new ArrayList<>();
                            if (objIndexes.get(counter)>-1) {
                                ids.add(gAreaPane.getChildren().get(objIndexes.get(counter)).getId());
                            }
                            if (counter>0) {
                                if (objIndexes.get(counter - 1) > -1) {
                                    ids.add(gAreaPane.getChildren().get(objIndexes.get(counter - 1)).getId());
                                }
                            }
                            if (counter>1) {
                                if (objIndexes.get(counter - 2) > -1) {
                                    ids.add(gAreaPane.getChildren().get(objIndexes.get(counter - 2)).getId());
                                }
                            }
                            for (String id: ids){
                                int indexOfId = getBackgroundObjectIndex(id);
                                if (id.contains("stackEdgeLine")){
                                    ((EdgeLine) ((StackPane) gAreaPane.getChildren().get(indexOfId)).getChildren().get(0)).setStroke(using);
                                }
                                else{
                                    ((VertexCircle) ((StackPane) gAreaPane.getChildren().get(indexOfId)).getChildren().get(0)).setFill(using);
                                }
                            }

                            if (CURRENT_ANIMATION_STEP==MAX_ANIMATION_STEPS){CURRENT_ANIMATION_STEP--;}
                            String from = animationSteps.get(CURRENT_ANIMATION_STEP).get("from");
                            String cost = animationSteps.get(CURRENT_ANIMATION_STEP).get("cost");
                            String fromID = String.format("stack%s", from);
                            int idxFrom = getBackgroundObjectIndex(fromID);
                            updateNodeCostLabel(idxFrom, cost);
                        }
                    };
                    int selectedDelay = (int) getAnimationSpeedDelay(3000);
                    tmr.schedule(tt, selectedDelay);
                    tmr.cancel();
                    tt.run();
                    updateAnimationStepCountLabels();
                    if (CURRENT_ANIMATION_STEP!=MAX_ANIMATION_STEPS){ CURRENT_ANIMATION_STEP++; }
                }
                counter++;
            }
        }));
        this.spareTimeline.setCycleCount(numStepRepeats);
        this.spareTimeline.play();

        // WHEN THE TIMELINE ENDS ITS RUN
        this.spareTimeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // reset counter index keeper
                counter=0;
            }
        });
    }

    private double getAnimationSpeedDelay(int defaultValue){
        Double value = cBoxAnimationSpeed.getValue();
        if(value<1.0) {
            value=1.0/value;
            return (defaultValue*value);
        }
        else{
            return (defaultValue/value);
        }
    }

    private void redoUpToStep(LinkedList<LinkedHashMap<String, String>> undoUpToSteps){
        // reset the color and cost label for all nodes & edges
        this.createVisibleTravelCostLabels("∞");
        this.colorReseting(true);
        // redo the animation but immediatelty up to a given level/step
        if (!undoUpToSteps.isEmpty()) {
            // iterate over the steps
            for (LinkedHashMap<String, String> step: undoUpToSteps){
                String to = String.format("stack%s", step.get("to"));
                String from = String.format("stack%s", step.get("from"));
                String cost = step.get("cost");
                int indexTo = this.getBackgroundObjectIndex(to);
                int indexFrom = this.getBackgroundObjectIndex(from);
                int indexEdgeLine = -1;
                if(indexTo!=indexFrom){
                    indexEdgeLine = Math.max(
                            this.getBackgroundObjectIndex(String.format("stackEdgeLine%s%s", to, from)),
                            this.getBackgroundObjectIndex(String.format("stackEdgeLine%s%s", from, to))
                            );
                }
                // set the cost label under the from node
                StackPane sp = (StackPane) gAreaPane.getChildren().get(indexFrom);
                VertexCircle vc = (VertexCircle) sp.getChildren().get(0);
                VertexCircle newVC = new VertexCircle(vc.getId(),
                        vc.getCenterX(), vc.getCenterY(), vc.getRadius(),
                        (Color) vc.getFill(), (Color) vc.getStroke());
                newVC.resetingStack();
                newVC.setCost(cost);
                newVC.addTravelCostLabeling();
                sp.getChildren().set(2, newVC.getStack().getChildren().get(2));
                // set the colors of all the possible objects
                ((VertexCircle) ((StackPane) gAreaPane.getChildren().get(indexTo)).getChildren().get(0)).setFill(using);
                ((VertexCircle) ((StackPane) gAreaPane.getChildren().get(indexFrom)).getChildren().get(0)).setFill(using);
                if(indexEdgeLine>-1) {
                    ((EdgeLine) ((StackPane) gAreaPane.getChildren().get(indexEdgeLine)).getChildren().get(0)).setStroke(using);
                }
            }
        }
    }

    /**
     * Run algorithm.
     * Using selected algorithm ChoiceBox and Enum State Management the Steps,
     * for a selected Algorithm are loading into a LinkedList of LinkedHashMaps,
     * this maintains order and allows traversal left (back) and right (forward).
     * @param actionEvent the action event
     * @throws Exception the exception
     */
    public void runAlgorithm(ActionEvent actionEvent) throws Exception {
        log("\n\n[RUN] --- Running Graph Algorithm ---");
        boolean continueAsNormal = true;
        if (this.graph!=null){
            String btnId = ((Node)actionEvent.getSource()).getId().toLowerCase().replace("btnAnimation".toLowerCase(), "");
            String selectedAlgo = this.cBoxAlgo.getSelectionModel().getSelectedItem();
            // STARTING
            if (this.CURRENT_STATE.equals(AnimationState.STOPPED)) {
                // ensure the choice if not the default shown value in index 0
                if (!selectedAlgo.equalsIgnoreCase(defaultAlgorithmCBString)) {
                    if (btnId.contains("run")) {
                        // System.out.println("STARTING");
                        this.CURRENT_STATE = AnimationState.RUNNING;
                        this.startToRunAlgorithm();
                    }
                }
            }
            // ALREADY RUNNING
            else if (this.CURRENT_STATE.equals(AnimationState.RUNNING)){
                if (!selectedAlgo.equalsIgnoreCase(defaultAlgorithmCBString)){
                    if (btnId.contains("run")){
                        continueAsNormal=this.restartToRunAlgorithm();
                    }
                }
            }
            if(continueAsNormal) {
                // SETTING - STATE LOGIC - WHEN RUNNING
                if (this.CURRENT_STATE.equals(AnimationState.RUNNING)) {
                    switch (btnId) {
                        case "start":
                            this.STEP = AnimationStep.TO_START;
                            break;
                        case "prev":
                            this.STEP = AnimationStep.TO_PREV;
                            break;
                        case "next":
                            this.STEP = AnimationStep.TO_NEXT;
                            break;
                        case "end":
                            this.STEP = AnimationStep.TO_END;
                            break;
                        case "autoplay":
                            this.STEP = AnimationStep.AUTOPLAY;
                            break;
                        case "reset":
                            this.resetAnimation();
                            break;
                    }
                }
                // GET ANIMATION STEPS
                if (this.CURRENT_STATE.equals(AnimationState.RUNNING)) {
                    // LOAD ANIMATION DATA - IF NOT LOADED
                    if (this.animationSteps.isEmpty()) {
                        this.animationStartingNode = this.cBoxStartingNode.getValue();
                        // returns the order that 'Nodes' + 'Edges' Should be highlighted
                        this.animationSteps = new LinkedList<LinkedHashMap<String, String>>(this.dijkstraAnimationSteps(this.animationStartingNode));
                        // set animation counters max steps
                        this.MAX_ANIMATION_STEPS = this.animationSteps.size();
                        // no longer empty
                        if (!this.animationSteps.isEmpty()){
                            this.createVisibleTravelCostLabels("∞");
                            this.displayCorrectAnimationControls();
                            this.setGraphAlteringControls(true);
                        }
                    }
                    // ANIMATION STEPS EFFECT LOGIC
                    if (!this.animationStartingNode.isEmpty()) {
                        int previous = this.CURRENT_ANIMATION_STEP;
                        // VIEWING THE STATES
                        log("\n\n\n--------");
                        log(String.format("[STEP]:\t%s", this.STEP.name()));
                        log(String.format("[STATE]:\t%s", this.CURRENT_STATE.name()));
                        log(String.format("[MAX ANIMATION STEPS]:\t%s", this.MAX_ANIMATION_STEPS));
                        log(String.format("[CURRENT ANIMATION STEP]:\t%s", this.CURRENT_ANIMATION_STEP));
                        log(String.format("[REMAINING ANIMATION STEP]:\t%s", Math.abs(this.MAX_ANIMATION_STEPS - this.CURRENT_ANIMATION_STEP)));
                        if (this.STEP.equals(AnimationStep.TO_START)) {
                            this.animationSteps.clear();
                            this.STEP = AnimationStep.BEGIN;
                            this.CURRENT_ANIMATION_STEP = 0;
                            this.createVisibleTravelCostLabels("∞");
                            this.colorReseting(true);
                        }
                        if (this.STEP.equals(AnimationStep.TO_END)) {
                            this.CURRENT_ANIMATION_STEP = this.MAX_ANIMATION_STEPS;
                        }
                        if (this.STEP.equals(AnimationStep.TO_NEXT)) {
                            // if the current next step is below the max amount return the new amount else max step always
                            this.CURRENT_ANIMATION_STEP = Math.min(this.CURRENT_ANIMATION_STEP + 1, this.MAX_ANIMATION_STEPS);
                        }
                        if (this.STEP.equals(AnimationStep.TO_PREV)) {
                            if (this.CURRENT_ANIMATION_STEP>-1) {
                                //this.CURRENT_ANIMATION_STEP = Math.max(this.CURRENT_ANIMATION_STEP - 1, 0);
                                LinkedHashMap<String, String> undoStep = this.animationSteps.get(this.CURRENT_ANIMATION_STEP);
                                LinkedList<LinkedHashMap<String, String>> undoUpToSteps = new LinkedList<>();
                                for (LinkedHashMap<String, String> step : this.animationSteps) {
                                    if (!step.equals(undoStep)) {
                                        undoUpToSteps.add(step);
                                    } else {
                                        break;
                                    }
                                }
                                // redo up to the steps before the last step executed
                                this.redoUpToStep(undoUpToSteps);
                            }
                            // cant dip under starting state of index -1 as next will pump it to 0 aka 1
                            this.CURRENT_ANIMATION_STEP = Math.max(this.CURRENT_ANIMATION_STEP - 1, -1);
                        }
                        if (this.STEP.equals(AnimationStep.AUTOPLAY)){
                            this.CURRENT_ANIMATION_STEP = 0;
                            this.createVisibleTravelCostLabels("∞");
                            this.colorReseting(true);
                        }
                        log(String.format("[CURRENT ANIMATION STEP]:\t%s", this.CURRENT_ANIMATION_STEP));
                        log(String.format("[REMAINING ANIMATION STEP]:\t%s", Math.abs(this.MAX_ANIMATION_STEPS - this.CURRENT_ANIMATION_STEP)));
                        this.updateAnimationStepCountLabels();
                        if (this.CURRENT_ANIMATION_STEP==this.MAX_ANIMATION_STEPS){
                            this.CURRENT_ANIMATION_STEP--;
                        }
                        // set has there been a change since last move
                        this.CHANGE_IN_STEP_VALUE = previous!=this.CURRENT_ANIMATION_STEP;
                        if (!this.STEP.equals(AnimationStep.BEGIN)){
                            if (this.STEP.equals(AnimationStep.TO_END)){
                                this.endAnimation();
                            }
                            else if (this.STEP.equals(AnimationStep.AUTOPLAY)){
                                // automated
                                this.automatedAnimationSteps();
                            }
                            else{
                                if (!this.STEP.equals(AnimationStep.TO_PREV)){
                                    if(this.CHANGE_IN_STEP_VALUE){
                                        // manual
                                        this.animateSteps();
                                    }
                                }
                            }
                        }
                        else {
                            log("[STARTING NODES IN GRAPH WITH MULTIPLE CONNECTIONS]");
                            this.CURRENT_ANIMATION_STEP--;
                        }
                    }
                }
            }
        }
        else{
            log("INVALID GRAPH");
            // warning if there is no graph
            this.failToRunAlgorithm();
            // clear the animation states
            this.animationSteps.clear();
            // reset the animation step and current state to default
            this.CURRENT_STATE = AnimationState.STOPPED;
            this.STEP = AnimationStep.BEGIN;
        }
    }

    /**
     * Finalise node cost strings.
     */
    private void finaliseNodeCostStrings(){
        for(LinkedHashMap<String, String> a: this.animationSteps){
            this.updateNodeCostLabel(this.getBackgroundObjectIndex(String.format("stack%s", a.get("from"))), a.get("cost"));
        }
    }

    /**
     * End animation.
     */
    private void endAnimation(){
        this.colorReseting(false);
        // this.finaliseNodeCostStrings();
        this.redoUpToStep(this.animationSteps);

    }

    /**
     * Set graph altering controls.
     * @param enabled the enabled
     */
    private void setGraphAlteringControls(boolean enabled){
        this.radioBtnAdd_Vertex.setSelected(!enabled);
        this.radioBtnRemove_Object.setSelected(!enabled);
        this.radioBtnAdd_Edge.setSelected(!enabled);
        this.radioBtnAdd_Vertex.setDisable(enabled);
        this.radioBtnAdd_Edge.setDisable(enabled);
        this.radioBtnRemove_Object.setDisable(enabled);
    }

    /**
     * Display correct animation controls.
     */
    private void displayCorrectAnimationControls(){
        this.translation = 200;
        boolean manualControls = this.cBoxDefaultControls.getValue().equalsIgnoreCase("manual");
        for (Object o: this.hBoxAnimationButtonBar.getChildren()) {
            VBox vb = (VBox) o;
            if (!vb.getId().equalsIgnoreCase("vboxReset")){
                vb.setVisible(false);
                if (manualControls && vb.getId().contains("vboxM")){ vb.setVisible(true); }
                if (!manualControls && vb.getId().contains("vboxA")){ vb.setVisible(true); }
            }
        }
        if(!manualControls){this.translation *=-1;}
        this.hBoxAnimationButtonBar.setTranslateX(this.translation);
    }

    /**
     * Create visible travel cost labels.
     * Replaces an internal Scene Objects internal label of cost.
     * @param costString the cost string
     */
    private void createVisibleTravelCostLabels(String costString){
        for (Object obj: gAreaPane.getChildren()){
            StackPane sp = (StackPane) obj;
            if (!sp.getId().contains("stackEdge")){
                VertexCircle vc = (VertexCircle) sp.getChildren().get(0);
                VertexCircle newVC = new VertexCircle(vc.getId(),
                        vc.getCenterX(), vc.getCenterY(), vc.getRadius(),
                        (Color) vc.getFill(), (Color) vc.getStroke());
                newVC.resetingStack();
                newVC.setCost(costString);
                newVC.addTravelCostLabeling();
                sp.getChildren().set(2, newVC.getStack().getChildren().get(2));
            }
        }
    }

    /**
     * Get background object index int.
     * @param objectID the object id
     * @return the int
     */
    private int getBackgroundObjectIndex(String objectID){
        for (Object obj: gAreaPane.getChildren()){
            StackPane sp = (StackPane) obj;
            if (sp.getId().equalsIgnoreCase(objectID)){
               return gAreaPane.getChildren().indexOf(sp);
            }
        }
        return -1;
    }

    /**
     * Update node cost label boolean.
     * @param index the index
     * @param cost  the cost
     * @return the boolean
     * @throws IllegalStateException the illegal state exception
     */
    private boolean updateNodeCostLabel(int index, String cost) throws IllegalStateException{
        try {
            StackPane sp = (StackPane) gAreaPane.getChildren().get(index);
            Double costing = this.travelCost.get(sp.getId().replace("stack", ""));
            // TODO - this doesnt amazingly work out when the finalised value is actually larger it goes down then up ...
            // doesnt look right
            Double parsedCost = Double.parseDouble(cost);
            if (parsedCost <= costing) {
                if (!sp.getId().contains("stackEdge")
                    //&& !sp.getId().equalsIgnoreCase(String.format("stack%s", this.animationStartingNode))
                )
                {
                    if(parsedCost==Double.MAX_VALUE){
                        cost = "∞";
                    }
                    VertexCircle vc = (VertexCircle) sp.getChildren().get(0);
                    VertexCircle newVC = new VertexCircle(vc.getId(),
                            vc.getCenterX(), vc.getCenterY(), vc.getRadius(),
                            (Color) vc.getFill(), (Color) vc.getStroke());
                    newVC.resetingStack();
                    newVC.setCost(cost);
                    newVC.addTravelCostLabeling();
                    sp.getChildren().set(2, newVC.getStack().getChildren().get(2));
                }
            } else {
                System.out.println("This cost was greater than the cost that it took from that starting node !");
                return false;
            }
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        return true;
    }

    /**
     * Animate steps.
     */
    private void animateSteps(){
        // NEED TO DETECT IF THIS IS AUTOMATED OR MANUALLY CONTROLLED
        String to = this.animationSteps.get(this.CURRENT_ANIMATION_STEP).get("to");
        String from = this.animationSteps.get(this.CURRENT_ANIMATION_STEP).get("from");
        String cost = this.animationSteps.get(this.CURRENT_ANIMATION_STEP).get("cost");
        String toID = String.format("stack%s", to);
        String fromID = String.format("stack%s", from);
        String edgeLineToFrom = String.format("stackEdgeLine%s%s", toID, fromID);
        String edgeLineFromTo = String.format("stackEdgeLine%s%s", fromID, toID);

        // [NODES] - indexes of StackPanes
        int idxTo = this.getBackgroundObjectIndex(toID);
        int idxFrom = this.getBackgroundObjectIndex(fromID);

        // TODO - consider moving position
        this.updateNodeCostLabel(idxFrom, cost);

        // [EDGE LINE] - indexes of EdgeLines
        int idxEdgeLine = Math.max(this.getBackgroundObjectIndex(edgeLineToFrom), this.getBackgroundObjectIndex(edgeLineFromTo));

        LinkedList<Integer> objIndexes = new LinkedList<>();
        objIndexes.add(0, idxTo);
        objIndexes.add(1, idxFrom);
        objIndexes.add(2, idxEdgeLine);

        // HOW LONG THE STEP TAKES
        int stepTime = 2;
        // HOW MANY TIME THS STEP REPEATS
        int numStepRepeats = objIndexes.size();

        this.counter = 0;
        this.spareTimeline = new Timeline(new KeyFrame(Duration.seconds(stepTime), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (objIndexes.get(counter) > -1) {
                    StackPane stestp = (StackPane) gAreaPane.getChildren().get(objIndexes.get(counter));
                    if (counter < 2) {
                        Color existingColor = (Color) ((VertexCircle) ((StackPane) gAreaPane.getChildren().get(objIndexes.get(counter))).getChildren().get(0)).getFill();
                        if (existingColor.equals(nodeNormal) || existingColor.equals(complete)) {
                            ((VertexCircle) ((StackPane) gAreaPane.getChildren().get(objIndexes.get(counter))).getChildren().get(0)).setFill(using);
                        }
                    } else {
                        if (counter > objIndexes.size() - 1) {
                            counter--;
                        }
                        if (objIndexes.get(counter) > -1) {
                            Color existingLineColor = (Color) ((EdgeLine) ((StackPane) gAreaPane.getChildren().get(objIndexes.get(counter))).getChildren().get(0)).getStroke();
                            if (existingLineColor.equals(lineNormal) || existingLineColor.equals(complete)) {
                                ((EdgeLine) ((StackPane) gAreaPane.getChildren().get(objIndexes.get(counter))).getChildren().get(0)).setStroke(using);
                            }
                        }
                    }
                    //counter++;
                }
                counter++;
            }
        }));
        this.spareTimeline.setCycleCount(numStepRepeats);
        this.spareTimeline.play();

        // WHEN THE TIMELINE ENDS ITS RUN
        this.spareTimeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                counter=0;
                Timer tmr = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {}
                };
                tmr.schedule(tt, 3000);
                tmr.cancel();
                tt.run();
            }
        });


       for (int i: objIndexes){
            if (i>-1){
                StackPane stestp = (StackPane) gAreaPane.getChildren().get(i);
                if (stestp.getId().contains("stackEdgeLine")){
                    ((EdgeLine) ((StackPane) gAreaPane.getChildren().get(i)).getChildren().get(0)).setStroke(complete);
                }
                else {
                    ((VertexCircle) ((StackPane) gAreaPane.getChildren().get(i)).getChildren().get(0)).setFill(complete);
                }
            }
       }

    }

    /**
     * Start to run algorithm.
     */
    private void startToRunAlgorithm(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.setTitle("Starting");
        alert.setHeaderText("Animation of Algorithm:\tAnimation Start Selected");
        alert.setContentText("Automated Observation:\n\tClick 'Auto Play' & adjust your 'Animation Speed'" +
                             "\nManual Observation:\n\tClick Buttons: [Start, Prev, Next, End]" +
                             "\nOptions:\n\tOK = Continue");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            System.out.println("\nSTARTING ALGORITHM RUN");
        }
    }

    /**
     * Restart to run algorithm boolean.
     * Warning/Alert dialog window telling user of a poteintial loss of data.
     * @return the boolean
     */
    private boolean restartToRunAlgorithm(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Restart");
        alert.setHeaderText("Animation of Algorithm:\tAnimation Restart Selected");
        alert.setContentText("The Animation of Algorithm will restart." +
                             "\nReason: Animation was runnning prior to click event on 'Run' Button." +
                             "\nOptions:\n\tOK = Animation Restart\n\tCancel = Ignore Re-Start");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            System.out.println("RESTARTING ALGORITHM RUN");
            this.animationSteps.clear();
            this.txtAreaAlgorithmOutput.clear();
            this.txtAreaTravelCost.clear();
            this.txtAreaAlgorithmSteps.clear();
            //this.STEP = AnimationStep.TO_START;
            this.STEP = AnimationStep.BEGIN;

            // may need to do
            this.CURRENT_ANIMATION_STEP = 0;

            this.colorReseting(true);
        }
        // return the opposite of this
        return!(alert.getResult()==ButtonType.CANCEL);
    }

    /**
     * Fail to run algorithm.
     * Warning/Alert dialog window telling user of an error.
     */
    private void failToRunAlgorithm(){
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        alert.setTitle("Warning");
        alert.setHeaderText("Graph Not Found:\tAnimate Algorithm Run");
        alert.setContentText("The Graph and Scene Objects cannot be found, please " +
                "however over 'Graph Altering Options' for further help.");
        alert.showAndWait();
    }

    /**
     * Save graph.
     */
    public void saveGraph() {
        log("[SAVING] --- Save Graph & Animation Steps --- ");
        this.fileChooser.setTitle("Save Graph File");
        this.fileChooser.setInitialFileName("graph");
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null){
            String saveAsString = selectedFile.getAbsolutePath();
            log(String.format("\nSave As:\t%s", saveAsString));
            fh.graphToFile(this.graph, saveAsString);
        }
    }

    /**
     * Save graph adjacency matrix.
     */
    public void saveGraphAdjacencyMatrix() {
        log("[SAVING] --- Save Graph Adjacency Matrix --- ");
        this.fileChooser.setTitle("Save Graph Adjacency Matrix File");
        this.fileChooser.setInitialFileName("graphAdjacencyMatrix");
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile!=null){
            String saveAsString = selectedFile.getAbsolutePath();
            log(String.format("\nSave As:\t%s", saveAsString));
            String adjMatrix = txtAreaAdjacencyMatrix.getText();
            fh.writeFile(adjMatrix, saveAsString);
        }
    }

    /**
     * Clear all scene contents.
     * Removes all data from the Scene.
     * @param actionEvent the action event
     * @throws IOException the io exception
     */
    public void clearAllSceneContents(ActionEvent actionEvent) throws IOException {
        log("[CLEAR ALL] --- CLEAR ALL CONTENT ---");
        this.sd.getAll().clear();
        this.move_window(actionEvent);
    }

    /**
     * Log.
     * @param s the s
     */
    private void log(String s){
        String apString = String.format("\n%s",s);
        this.txtAreaConsole.appendText(apString);
    }

    /**
     * Update graph to scene data.
     * Update graph to scene data.
     */
    @SuppressWarnings({"unchecked"})
    private void updateGraphToSceneData(){
        // this function only ever occurs if there is an update to the scene/graph

        // HashMap - storing all graph details needed
        HashMap<String, ArrayList<String>> graphToSceneDataMap = new HashMap<>();

        // radius of the circle alters the centre point of where the object draws so this is an 'undo'
        double r = new VertexCircle().radius;

        // Positional String
        ArrayList<String> positions = new ArrayList<>();
        for (Vertex v: this.graph.getVertices()){
            positions.add(String.format("%s,%s,%s", v.getName(), v.getX() + r, v.getY() +r));
        }

        // Connectivity String
        ArrayList<String> connectivity = new ArrayList<>();
        for (Edge e: this.graph.getEdges()){
            connectivity.add(String.format("%s,%s,%s", e.getStart().getName(), e.getEnd().getName(), e.getWeight()));
        }

        // Adding Lists of Strings to the Hashmap
        graphToSceneDataMap.put("connectivity", connectivity);
        graphToSceneDataMap.put("positions", positions);
        graphToSceneDataMap.put("type", new ArrayList<>(){{add("graph");}});
        graphToSceneDataMap.put("verticies", new ArrayList<>(this.graph.getVertexNames()));

        // clear out the existing data as this wont match the new data
        this.sd.getAll().clear();

        // update the scene existing data
        for (String k: graphToSceneDataMap.keySet()){
            this.sd.add(k, graphToSceneDataMap.get(k));
        }

        // if the graph is actually empty remove any scene data as it may have been static
        if(this.graph.getVertexNames().size()==0){
            this.sd.getAll().clear();
        }

    }

}
