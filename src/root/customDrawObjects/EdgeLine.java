package root.customDrawObjects;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * The type Edge line.
 */
public class EdgeLine extends Line {

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
     * The Line color.
     */
    private Color lineColor, /**
     * The Text color.
     */
    textColor;
    /**
     * The Line width.
     */
    private double lineWidth = 1;
    /**
     * The Text size.
     */
    private double textSize = 12;
    /**
     * The Radius.
     */
    private double radius = 0;
    /**
     * The Stack.
     */
    private StackPane stack;

    /**
     * Instantiates a new Edge line.
     *
     * @param name        the name
     * @param displayText the display text
     * @param radius      the radius
     * @param x1          the x 1
     * @param y1          the y 1
     * @param x2          the x 2
     * @param y2          the y 2
     * @param lineWidth   the line width
     * @param textSize    the text size
     * @param lineColor   the line color
     * @param textColor   the text color
     */
    public EdgeLine(String name, String displayText, double radius,
                    double x1, double y1, double x2, double y2,
                    double lineWidth, double textSize,
                    Color lineColor, Color textColor){
        super(x1,y1,x2,y2);
        this.radius = radius;

        // set all object variables
        this.setVariables(name, lineWidth, textSize, lineColor, textColor);

        // line Object
        super.setId(this.getLineObjectId());
        super.setStroke(this.getLineColor());
        super.setStrokeWidth(this.getLineWidth());

        // set the stack object
        this.setStack(displayText);
    }

    /**
     * Set stack.
     *
     * @param displayText the display text
     */
    private void setStack(String displayText){

        // text object created
        Text text = this.LineText(displayText);
        StackPane txt = new StackPane(text);
        txt.setBackground(new Background(
                new BackgroundFill(Color.WHITE, null, new Insets(0))));
        double txtS = text.getFont().getSize();
        txt.setMaxWidth(txtS*2.0);
        txt.setMaxHeight(txtS*1.5);

        // StackPane Object initialised
        this.stack = new StackPane();
        this.stack.setId(this.getStackId());

        // if the boundary/background is to be seen change color
        this.stack.setBackground(new Background(
                new BackgroundFill(Color.TRANSPARENT, null, new Insets(0))));

        double maxW = Math.abs(this.getStartX()-this.getEndX());
        double maxH = Math.abs(this.getStartY()-this.getEndY());
        this.stack.setMinWidth(maxW); this.stack.setMaxWidth(maxW);
        this.stack.setMinHeight(maxH); this.stack.setMaxHeight(maxH);
        this.stack.getChildren().addAll(this, txt);

        // positional vars
        double x = this.getStartX();
        double y = this.getStartY();
        double  x1 = this.getStartX();
        double  y1 = this.getStartY();
        double  x2 = this.getEndX();
        double  y2 = this.getEndY();

        // debug purposes only
        int type = -1;

        // translations for stackpane drawing at top left corner
        if ((x1 < x2) && (y1 > y2)){
            y = y2;
            type=1;
        }
        else if ((x1 < x2) && (y1 < y2)){
            type=2;
        }
        else if ((x1 > x2) && (y1 < y2)){
            x = x2;
            type=3;
        }
        else if ((x1 > x2) && (y1 > y2)){
            x = x2;
            y = y2;
            type=4;
        }
        else if ((x1 == x2) && (y1 != y2)){
            type=5;
        }
        else if ((x1 != x2) && (y1 == y2)){
            type=6;
            // this was a nightmare error
            x = Math.min(x, Math.min(x1, x2));
        }
        else {
            type=7;
        }

        // DEBUGGING
        boolean debug = false;
        if (debug) {
            lineTypeOut(debug, String.valueOf(type));
            this.lineValueOut("x", x);
            this.lineValueOut("y", y);
            this.lineValueOut("x1", x1);
            this.lineValueOut("x2", x2);
            this.lineValueOut("y1", y1);
            this.lineValueOut("y2", y2);
        }
        // actual drawing x,y of top left corner of stack pane
        this.stack.setLayoutX(x);
        this.stack.setLayoutY(y);
    }

    /**
     * Line value out.
     *
     * @param valueName the value name
     * @param value     the value
     */
    private void lineValueOut(String valueName, Double value){
        System.out.println(String.format("%s:\t%s", valueName, value));
    }

    /**
     * Line type out.
     *
     * @param stdout the stdout
     * @param str    the str
     */
    private void lineTypeOut(boolean stdout, String str){
        if (stdout){
            System.out.println(str);
        }
    }

    /**
     * Line text text.
     *
     * @param displayText the display text
     * @return the text
     */
    private Text LineText(String displayText){
        Text txt = new Text(displayText);
        txt.setId(this.getTextObjectId());
        double x = Math.abs(this.getStartX()-this.getEndX());
        double y = Math.abs(this.getStartY()-this.getEndY());
        // set object vitals
        txt.setX(x);
        txt.setY(y);
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
     * @param name      the name
     * @param lineWidth the line width
     * @param textSize  the text size
     * @param lineColor the line color
     * @param textColor the text color
     */
    private void setVariables(String name,
                              double lineWidth, double textSize,
                              Color lineColor, Color textColor){

        // mass variable setting
        this.setLineObjectId(name);
        this.setTextObjectId(name);
        this.setStackId(name);

        // colors
        this.setLineColor(lineColor);
        this.setTextColor(textColor);
        this.setTextSize(textSize);
        this.setLineWidth(lineWidth);
    }

    /**
     * Set line object id.
     *
     * @param name the name
     */
// setters
    private void setLineObjectId(String name){
        this.objectId = String.format("edgeLine%s", name);
    }

    /**
     * Set text object id.
     *
     * @param name the name
     */
    private void setTextObjectId(String name){
        this.textId = String.format("textEdgeLine%s", name);
    }

    /**
     * Set stack id.
     *
     * @param name the name
     */
    private void setStackId(String name){
        this.stackId = String.format("stackEdgeLine%s", name);
    }

    /**
     * Sets line width.
     *
     * @param lineWidth the line width
     */
    private void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * Set line color.
     *
     * @param lineColor the line color
     */
    private void setLineColor(Color lineColor){
        this.lineColor = lineColor;
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
     * Set text size.
     *
     * @param textSize the text size
     */
    private void setTextSize(double textSize){
        this.textSize = textSize;
    }

    /**
     * Get line object id string.
     *
     * @return the string
     */
// getters
    private String getLineObjectId(){
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
     * Get line color color.
     *
     * @return the color
     */
    private Color getLineColor(){
        return this.lineColor;
    }

    /**
     * Gets text color.
     *
     * @return the text color
     */
    private Color getTextColor() {
        return this.textColor;
    }

    /**
     * Get text size double.
     *
     * @return the double
     */
    private double getTextSize(){
        return this.textSize;
    }

    /**
     * Gets line width.
     *
     * @return the line width
     */
    private double getLineWidth() {
        return this.lineWidth;
    }

}

