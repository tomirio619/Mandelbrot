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
import worker.Queue;

/**
 *
 * @author Tom
 */
public class View implements Observer {

    static int counter = 0;
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
    private static BorderPane borderPane;

    private GridPane gridPane;

    /**
     *
     * The Stage
     */
    private final Stage primaryStage;

    private Label nrOfThreadsLabel;

    private Label maxIterationsLabel;

    private Label coordinatesLabel;

    private Label xCoordinateLabel;

    private Label yCoordinateLabel;

    private Label blockSizeLabel;

    private TextField nrOfThreadsField;

    private TextField blockSizeField;

    private TextField maxIterationsField;

    private TextField xCoordinateField;

    private TextField yCoordinateField;

    private Button zoomButton;

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

        borderPane = new BorderPane();
        vbox = new VBox();
        scene = new Scene(borderPane);
        image = new ImageView();
        mhandler = new MouseHandler(grid);
        progressBar = new ProgressBar(0.0);
        /* set up the view which contains the representation of the mandelbrot 
        set
         */
        BorderPane.setMargin(image, new Insets(12, 12, 12, 12));
        image.setImage(grid.grid);
        borderPane.setCenter(image);
        image.addEventHandler(MouseEvent.ANY, mhandler);

        /*
        Create the button, labels, fields and progress bar
        restrict input:
        http://stackoverflow.com/questions/11093326/restricting-jtextfield-input-to-integers
        JFormattedTextField
         */
        nrOfThreadsLabel = new Label("Threads:");
        maxIterationsLabel = new Label("Max iterations:");
        coordinatesLabel = new Label("Coordinates");
        xCoordinateLabel = new Label("x:");
        yCoordinateLabel = new Label("y:");
        blockSizeLabel = new Label("Blocksize:");

        nrOfThreadsField = new TextField();
        blockSizeField = new TextField();
        maxIterationsField = new TextField();
        xCoordinateField = new TextField();
        yCoordinateField = new TextField();

        zoomButton = new Button("Zoom");
        zoomButton.setId("zoomButton");
        zoomButton.setOnAction(new ActionEventHandler(grid));

        /*
        Set Id's
         */
        nrOfThreadsField.setId("nrOfThreatsField");
        nrOfThreadsField.setDisable(true);
        maxIterationsField.setId("maxIterationsField");
        blockSizeField.setId("blockSizeField");
        xCoordinateField.setId("xCoordinateField");
        yCoordinateField.setId("yCoordinateField");

        /*
        Set text colour in labels to white
         */
        nrOfThreadsLabel.setStyle("-fx-text-fill: white;");
        maxIterationsLabel.setStyle("-fx-text-fill: white;");
        coordinatesLabel.setStyle("-fx-text-fill: white;");
        xCoordinateLabel.setStyle("-fx-text-fill: white;");
        yCoordinateLabel.setStyle("-fx-text-fill: white;");
        blockSizeLabel.setStyle("-fx-text-fill: white;");
        /*
        Add labels, textfields and progressbar to gridlayout
         */
        gridPane = new GridPane();
        gridPane.addRow(0, nrOfThreadsLabel, nrOfThreadsField);
        gridPane.addRow(1, maxIterationsLabel, maxIterationsField);
        gridPane.addRow(2, blockSizeLabel, blockSizeField);
        gridPane.addRow(3, coordinatesLabel);
        gridPane.addRow(4, xCoordinateLabel, xCoordinateField);
        gridPane.addRow(5, yCoordinateLabel, yCoordinateField);
        gridPane.addRow(6, zoomButton, progressBar);
        progressBar.setMaxWidth(Double.MAX_VALUE);
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
        blockSizeField.setTextFormatter(new TextFormatter<>(formatter, Queue.chunkSIZE, filter));
        nrOfThreadsField.setText(Integer.toString(WritableGrid.MAX_NR_OF_THREADS));

        /*
        Verify values on focusloss
        http://www.java2s.com/Code/Java/Event/ValidatingaJTextFieldWhenPermanentlyLosingtheFocus.htm
         */
        //maxIterationsField;
        /*
        Set style of borderpane
         */
        borderPane.setStyle("-fx-background-color: #2B2B2B;");

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
        counter++;
        Job j = (Job) arg;
        paint(j);
        double totalJobs = j.getQueue().getTotalJobs();
        double completedJobs = j.getQueue().getCompletedJobs();
        double ratio = completedJobs / totalJobs;
        //System.out.println("Het totale aantal jobs was " + totalJobs);
        //System.out.println("Het aantal voltooide jobs was " + completedJobs);
        //System.out.println("De ratio is " + ratio);
        //System.out.println("De counter staat op " + counter);
        if (ratio == 1.0) {
            //all jobs completed
            progressBar.setProgress(0.0);
            j.getQueue().resetCompletedJobs();
        } //System.out.println("Total jobs: " + totalJobs);
        //System.out.println("Completed jobs:" + completedJobs);
        else {
            progressBar.setProgress(ratio);
        }

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
