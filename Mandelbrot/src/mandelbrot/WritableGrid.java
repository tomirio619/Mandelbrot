package mandelbrot;

import worker.*;
import java.util.Observable;
import javafx.application.Platform;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 *
 * @author Tom
 */
public final class WritableGrid extends Observable {

    /**
     *
     * The width of the grid
     */
    public final int gridW;

    /**
     *
     * The height of the grid
     */
    public final int gridH;

    /**
     *
     * The PixelWriter of the WritableGrid
     */
    public final PixelWriter writer;

    /**
     *
     * The scale that is currently being used
     */
    public int scalefactor = 124;

    /**
     *
     * All the points that are in the grid
     */
    public final Point[][] coordinates;

    /**
     *
     * The maximum number of threads
     */
    private final int MAX_NR_OF_THREADS;

    /**
     *
     * The grid
     */
    public final WritableImage grid;

    /**
     *
     * @param width The width of the WritableImage
     * @param height The height of the WritableImage
     */
    public WritableGrid(int width, int height) {
        grid = new WritableImage(width, height);
        this.gridW = width;
        this.gridH = height;
        this.writer = grid.getPixelWriter();
        coordinates = new Point[width][height];
        MAX_NR_OF_THREADS = Runtime.getRuntime().availableProcessors();
    }

    /**
     * Starts the Slaves and the Master to respectively compute the jobs and get
     * the results. The master must run on the main javaFX thread. This is done
     * by calling Platform.runLater()
     */
    public void startThreads() {
        Queue queue = new Queue(coordinates, gridW, gridH);
        for (int i = 0; i < MAX_NR_OF_THREADS; i++) {
            Thread slave = new Thread(new Slave(queue));
            slave.setDaemon(true);
            slave.start();
        }
        Platform.runLater(new Master(queue, this));
    }

    /**
     * Notifies the View that this job can be painted to the screen.
     *
     * @param job The finished job that has to be painted to the screen
     */
    public void updateJob(Job job) {
        setChanged();
        notifyObservers(job);
    }

    /**
     * For a given center point (a,b), calculate all of the other points. When
     * this is done call the method startThreads().
     *
     * @param a x coordinate of the center of the grid
     * @param b y coordinate of the center of the grid
     */
    public void calcCoordinates(double a, double b) {
        double stepX = 1.0 / scalefactor;
        double stepY = 1.0 / scalefactor;
        double widthX = stepX * gridW;
        double heightY = stepY * gridH;
        for (int width = 0; width < gridW; width++) {
            for (int height = 0; height < gridH; height++) {
                double newX = a - widthX * 0.5 + width * stepX;
                double newY = b - heightY * 0.5 + height * stepY;
                coordinates[width][height] = new Point(newX, newY);
            }
        }
        startThreads();
    }

}
