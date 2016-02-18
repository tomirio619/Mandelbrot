package view;

import java.util.Observable;
import java.util.Observer;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import worker.Job;
import controller.MouseHandler;
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
     * The scene
     */
    private final Scene scene;

    /**
     *
     * The AnchorPane
     */
    static protected final AnchorPane root = new AnchorPane();

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
        scene = new Scene(root, width, height);
        vbox = new VBox();
        image = new ImageView();

        image.setImage(grid.grid);
        vbox.autosize();
        vbox.getChildren().add(image);
        root.getChildren().add(vbox);

        mhandler = new MouseHandler(grid);
        scene.addEventHandler(MouseEvent.ANY, mhandler);

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
        root.getChildren().add(r);
    }

    /**
     *
     * @param r The rectangle that has to be remove from the screen
     */
    public static void removeSelection(Rectangle r) {
        root.getChildren().remove(r);
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
