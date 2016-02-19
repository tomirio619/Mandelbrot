package mandelbrot;

import worker.*;
import java.util.Observable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
    public static final int MAX_NR_OF_THREADS = Runtime.getRuntime().availableProcessors();
    ;

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
    }

    /**
     * http://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorCompletionService.html
     * http://stackoverflow.com/questions/4912228/when-should-i-use-a-completionservice-over-an-executorservice
     * http://www.nurkiewicz.com/2014/11/executorservice-10-tips-and-tricks.html
     * http://java-buddy.blogspot.nl/2013/01/implement-javafxconcurrenttask-to-draw.html
     */
    public void startThreads() {
        Queue queue = new Queue(coordinates, gridW, gridH, this);
        ExecutorService pool = Executors.newFixedThreadPool(MAX_NR_OF_THREADS);
        ExecutorCompletionService service = new ExecutorCompletionService(pool);

        while (queue.iterator.hasNext()) {
            service.submit(queue.iterator.next());
        }
        pool.execute(new Task<Void>() {
            @Override
            public Void call() {
                for (int i = 0; i < queue.getTotalJobs(); i++) {
                    try {
                        Future<Job> result = service.take();
                        Job j = result.get();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                j.getQueue().incrementCompletedJobs();
                                paintJob(j);
                            }

                        });
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WritableGrid.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(WritableGrid.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        });
        pool.shutdown();
    }

    /**
     * Notifies the View that this job can be painted to the screen.
     *
     * @param j the job
     */
    public void paintJob(Job j) {
        setChanged();
        notifyObservers(j);
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
