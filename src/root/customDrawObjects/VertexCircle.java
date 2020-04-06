package root.customDrawObjects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * The type Vertex circle.
 */
@SuppressWarnings("SameParameterValue")
public class VertexCircle extends Circle {

    /**
     * The Object id.
     */
    private String objectId, /**
     * The Text id.
     */
    textId, /**
     * The Stack id.
     */
    stackId;
    /**
     * The X.
     */
    private double x, /**
     * The Y.
     */
    y;
    /**
     * The Circle color.
     */
    private Color circleColor, /**
     * The Text color.
     */
    textColor;
    /**
     * The Circle stroke px.
     */
    private double circleStrokePx = 2, /**
     * The Text size.
     */
    textSize = 12;
    /**
     * The Stack.
     */
    StackPane stack;
    /**
     * The Radius.
     */
    public double radius;
    /**
     * The Cost.
     */
    public String cost;
    /**
     * The Text.
     */
    Text text;

    /**
     * Instantiates a new Vertex circle.
     */
    public VertexCircle(){super();}

    /**
     * Instantiates a new Vertex circle.
     *
     * @param name        the name
     * @param x           the x
     * @param y           the y
     * @param radius      the radius
     * @param circleColor the circle color
     * @param textColor   the text color
     */
    public VertexCircle(String name, double x, double y, double radius, Color circleColor, Color textColor){
        super(x, y, radius);
        this.circleColor=circleColor;

        if (name.contains("vertex")) {
            name = name.replace("vertex", "");
        }
        else {
            // remove radius distance
            x -= radius;
            y -= radius;
        }

        // set all object variables
        this.setVariables(name, x, y, radius, circleColor, textColor);

        // Circle Object
        super.setId(this.getCircleObjectId());
        super.setCenterX(this.getX());
        super.setCenterY(this.getY());
        super.setRadius(this.getRadius());
        super.setStrokeWidth(this.getCircleStroke());
        super.setFill(this.getCircleColor());

        // Text Object
        this.text = this.VertexText(name);
        this.setStyling("default");

        // StackPane Object
        this.stack = new StackPane();
        this.stack.setId(this.getStackId());

        // if the boundary is to be seen change color
        this.stack.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, null, new Insets(0))));
        this.maxHeight(this.getRadius()*2+this.getStrokeWidth()*2);

        // Adds another element under the node name to show travel cost in djikstra algorithm
        this.addTravelCostLabeling();

        // using center will be more fruitful
        this.stack.setLayoutX(this.getCenterX());
        this.stack.setLayoutY(this.getCenterY());
    }

    /**
     * Set cost.
     *
     * @param cost the cost
     */
    public void setCost(String cost){this.cost = cost;}

    /**
     * Get cost string.
     *
     * @return the string
     */
    private String getCost(){return cost;}

    /**
     * Add travel cost labeling.
     */
    public void addTravelCostLabeling(){
        // we dont want to see anything at the start
        Text travelValue = this.VertexText(this.getCost(), "txtTravelValue");
        StackPane spTravelValue = new StackPane();
        Rectangle rect = new Rectangle();

        // ids are set for later finding and method access
        spTravelValue.setId("spTravelValue");
        rect.setId("rectTravelValue");

        // distance multiplier
        double multH = 1.2;
        double multW = multH;
        if (this.getCost()!=null) {
            if (this.getCost().equalsIgnoreCase("∞")) {
                multW = 1.8;
            }
        }

        // dynamic growth with costing string size increase
        rect.setHeight(this.textSize * multH);
        rect.setWidth(this.textSize/2 * multW * travelValue.getText().length());
        rect.setFill(Paint.valueOf("#26fb62"));


        spTravelValue.setBackground(new Background(
                new BackgroundFill(
                        Color.TRANSPARENT,
                        null, new Insets(0))));
        spTravelValue.getChildren().addAll(rect, travelValue);

        //spTravelValue.autosize();

        spTravelValue.setMaxHeight(rect.getHeight());
        spTravelValue.setMaxWidth(rect.getWidth());

        /*spTravelValue.setMaxHeight(10);
        spTravelValue.setMaxWidth(10);*/


        StackPane.setAlignment(rect, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(travelValue, Pos.BOTTOM_CENTER);
        spTravelValue.setTranslateY(this.radius*.5);
        if (this.stack.getChildren().isEmpty()){
            this.stack.getChildren().addAll(this, this.text, spTravelValue);
        }
        if (!this.stack.getChildren().contains(this)){
            this.stack.getChildren().add(this);
        }
        if (!this.stack.getChildren().contains(this.text)){
            this.stack.getChildren().add(this.text);
        }
        if (!this.stack.getChildren().contains(spTravelValue)){
            this.stack.getChildren().add(spTravelValue);
        }
        StackPane.setAlignment(this.text, Pos.CENTER);
        //System.out.println(this.stack.getChildren().toString());

    }

    /**
     * Reseting stack.
     */
    public void resetingStack(){
        this.stack.getChildren().clear();
    }

    /**
     * Set styling.
     *
     * @param styleClass the style class
     */
    private void setStyling(String styleClass){ this.getStyleClass().add(styleClass); }

    /**
     * Get styling string.
     *
     * @return the string
     */
    private String getStyling(){return this.getStyleClass().toString();}

    /**
     * Vertex text text.
     *
     * @param name the name
     * @return the text
     */
    private Text VertexText(String name){
        Text txt = new Text(name);
        txt.setId(this.getTextObjectId());
        txt.setX(this.getX());
        txt.setY(this.getY());
        txt.setStroke(this.getTextColor());
        txt.setFont(new Font(this.getTextSize()));
        return txt;
    }

    /**
     * Vertex text text.
     *
     * @param name the name
     * @param id   the id
     * @return the text
     */
    private Text VertexText(String name, String id){
        Text txt = new Text(name);
        txt.setId(id);
        txt.setX(this.getX());
        txt.setY(this.getY());
        txt.setStroke(this.getTextColor());
        txt.setFont(new Font(this.getTextSize()));
        return txt;
    }

    /**
     * Get stack stack pane.
     *
     * @return the stack pane
     */
    public StackPane getStack(){
        return this.stack;
    }

    /**
     * Set variables.
     *
     * @param name        the name
     * @param x           the x
     * @param y           the y
     * @param radius      the radius
     * @param circleColor the circle color
     * @param textColor   the text color
     */
    private void setVariables(String name, double x, double y, double radius, Color circleColor, Color textColor){
        // mass variable setting
        this.setCircleObjectId(name);
        this.setTextObjectId(name);
        this.setStackId(name);
        this.setX(x);
        this.setY(y);
        this.setCircleRadius(radius);

        this.setCircleColor(circleColor);
        this.setTextColor(textColor);

        // will initialise with the defaukt values as expected
        this.setCircleStroke(this.getCircleStroke());
        this.setTextSize(this.getTextSize());
    }

    /**
     * Set circle object id.
     *
     * @param name the name
     */
// setters
    private void setCircleObjectId(String name){
        this.objectId = String.format("vertex%s", name);
    }

    /**
     * Set text object id.
     *
     * @param name the name
     */
    private void setTextObjectId(String name){
        this.textId = String.format("text%s", name);
    }

    /**
     * Set stack id.
     *
     * @param name the name
     */
    private void setStackId(String name){
        this.stackId = String.format("stack%s", name);
    }

    /**
     * Set x.
     *
     * @param x the x
     */
    private void setX(double x){
        this.x = x;
    }

    /**
     * Set y.
     *
     * @param y the y
     */
    private void setY(double y){
        this.y = y;
    }

    /**
     * Set circle radius.
     *
     * @param radius the radius
     */
    private void setCircleRadius(double radius){
        this.radius = radius;
    }

    /**
     * Set circle color.
     *
     * @param circleColor the circle color
     */
    private void setCircleColor(Color circleColor){
        this.circleColor = circleColor;
    }

    /**
     * Set text color.
     *
     * @param textColor the text color
     */
    private void setTextColor(Color textColor){
        this.textColor = textColor;
    }

    /**
     * Set circle stroke.
     *
     * @param strokePx the stroke px
     */
    private void setCircleStroke(double strokePx){
        this.circleStrokePx = strokePx;
    }

    /**
     * Set text size.
     *
     * @param textSize the text size
     */
    private void setTextSize(double textSize){
        this.textSize = textSize;
    }

    /**
     * Get circle object id string.
     *
     * @return the string
     */
// getters
    public String getCircleObjectId(){
        return this.objectId;
    }

    /**
     * Get text object id string.
     *
     * @return the string
     */
    private String getTextObjectId(){
        return this.textId;
    }

    /**
     * Get stack id string.
     *
     * @return the string
     */
    private String getStackId(){
        return this.stackId;
    }

    /**
     * Get x double.
     *
     * @return the double
     */
    private double getX(){
        return this.x;
    }

    /**
     * Get y double.
     *
     * @return the double
     */
    private double getY(){
        return this.y;
    }

    /**
     * Get circle radius double.
     *
     * @return the double
     */
    private double getCircleRadius(){
        return this.radius;
    }

    /**
     * Get circle color color.
     *
     * @return the color
     */
    private Color getCircleColor(){
        return this.circleColor;
    }

    /**
     * Get text color color.
     *
     * @return the color
     */
    private Color getTextColor(){
        return this.textColor;
    }

    /**
     * Get circle stroke double.
     *
     * @return the double
     */
    private double getCircleStroke(){
        return this.circleStrokePx;
    }

    /**
     * Get text size double.
     *
     * @return the double
     */
    private double getTextSize(){
        return this.textSize;
    }
}
