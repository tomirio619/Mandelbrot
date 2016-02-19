package view;

import controller.ActionEventHandler;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import worker.Job;
import controller.MouseHandler;
import java.util.function.UnaryOperator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import mandelbrot.Point;
import mandelbrot.WritableGrid;

/**
 *
 * @author Tom
 */
public class View implements Observer {

    /**
     *
     * The MouseHandler
     */
    private final MouseHandler mhandler;

    /**
     *
     * The ImageView
     */
    private final ImageView image;

    /**
     *
     * The VBox that contains the ImageView
     */
    private final VBox vbox;

    /**
     *
     * The grid
     */
    private final WritableGrid grid;

    /**
     *
     * The progress bar
     */
    private ProgressBar progressBar;

    /**
     *
     * The scene
     */
    private final Scene scene;

    /**
     *
     * The AnchorPane
     */
    static protected final BorderPane borderPane = new BorderPane();

    /**
     *
     * The Stage
     */
    private final Stage primaryStage;

    /**
     *
     * @param stage The stage
     * @param grid The grid
     * @param width Width of the grid
     * @param height Height of the grid
     */
    public View(Stage stage, WritableGrid grid, int width, int height) {
        primaryStage = stage;
        this.grid = grid;

        vbox = new VBox();
        scene = new Scene(borderPane);
        image = new ImageView();
        mhandler = new MouseHandler(grid);
        progressBar = new ProgressBar(0.0);
        /* set up the view which contains the representation of the mandelbrot 
        set
         */
        image.setImage(grid.grid);
        borderPane.setCenter(image);
        image.addEventHandler(MouseEvent.ANY, mhandler);

        /*
        Create the button, labels, fields and progress bar
        restrict input:
        http://stackoverflow.com/questions/11093326/restricting-jtextfield-input-to-integers
        JFormattedTextField
         */
        Label nrOfThreadsLabel = new Label("Threads:");
        Label maxIterationsLabel = new Label("Max iterations:");
        Label coordinatesLabel = new Label("Coordinates");
        Label xCoordinateLabel = new Label("x:");
        Label yCoordinateLabel = new Label("y:");
        Label blockSizeLabel = new Label("Blocksize:");

        TextField nrOfThreatsField = new TextField();
        TextField blockSizeField = new TextField();
        TextField maxIterationsField = new TextField();
        TextField xCoordinateField = new TextField();
        TextField yCoordinateField = new TextField();

        Button zoomButton = new Button("Zoom");
        zoomButton.setId("zoomButton");
        zoomButton.setOnAction(new ActionEventHandler(grid));

        /*
        Add labels, textfields and progressbar to gridlayout
         */
        GridPane gridPane = new GridPane();
        gridPane.addRow(0, nrOfThreadsLabel, nrOfThreatsField);
        gridPane.addRow(1, maxIterationsLabel, maxIterationsField);
        gridPane.addRow(2, blockSizeLabel, blockSizeField);
        gridPane.addRow(3, coordinatesLabel);
        gridPane.addRow(4, xCoordinateLabel, xCoordinateField);
        gridPane.addRow(5, yCoordinateLabel, yCoordinateField);
        gridPane.addRow(6, zoomButton, progressBar);
        vbox.getChildren().addAll(gridPane);
        BorderPane.setMargin(vbox, new Insets(12, 12, 12, 12));
        borderPane.setRight(vbox);

        /*
       http://stackoverflow.com/questions/31039449/java-8-u40-textformatter-javafx-to-restrict-user-input-only-for-decimal-number
       http://www.javaworld.com/article/2991463/core-java/javafx-improvements-in-java-se-8u40.html?page=4
        Create formatters and filters
         */
        StringConverter<Integer> formatter;
        formatter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                System.out.println("fromString(): string = " + string);
                return Integer.parseInt(string);
            }

        };

        UnaryOperator<TextFormatter.Change> filter;
        filter = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change t) {
                System.out.println(t);
                String text = t.getText();
                for (int i = 0; i < text.length(); i++) {
                    if (!Character.isDigit(text.charAt(i))) {
                        return null;
                    }
                }
                return t;
            }

        };
        maxIterationsField.setTextFormatter(new TextFormatter<>(formatter, Point.maxIter, filter));

        primaryStage.setTitle("Mandelbrot");
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     *
     * @param r The rectangle that has to be drawn on the screen
     */
    public static void drawSelection(Rectangle r) {
        borderPane.getChildren().add(r);
    }

    /**
     *
     * @param r The rectangle that has to be remove from the screen
     */
    public static void removeSelection(Rectangle r) {
        borderPane.getChildren().remove(r);
    }

    /**
     * Paints the calculated job to the WritableGrid
     *
     * @param j The job that has been calculated
     */
    public void paint(Job j) {
        for (int width = 0; width < j.getWidth(); width++) {
            for (int height = 0; height < j.getHeight(); height++) {
                Color c = j.getResult(width, height);
                writePixel(j.getX() + width, j.getY() + height, c);
            }
        }
    }

    /**
     *
     * @param width The width of the pixel
     * @param heigth The height of the pixel
     * @param c The color of the pixel
     */
    public void writePixel(int width, int heigth, Color c) {
        grid.writer.setColor(width, heigth, c);
    }

    /**
     *
     * @param o The object that notified the Observer
     * @param arg The argument passed when the Observer was notified, in this
     * case this is the calculated job
     */
    @Override
    public void update(Observable o, Object arg) {
        Job j = (Job) arg;
        paint(j);
        //System.out.println("Total jobs: " + totalJobs);
        //System.out.println("Completed jobs:" + completedJobs);
        //progressBar.setProgress(completedJobs/totalJobs);
    }

    /**
     *
     * @param a Initial x value for the center of the grid
     * @param b Initial y value for the center of the grid
     */
    public void initiate(double a, double b) {
        grid.calcCoordinates(0.02, -0.01);
    }

}
