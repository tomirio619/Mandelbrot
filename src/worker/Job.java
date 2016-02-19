package worker;

import java.util.concurrent.Callable;
import javafx.scene.paint.Color;
import mandelbrot.Point;

/**
 *
 * @author Tom
 */
public class Job implements Callable<Job> {

    /**
     *
     * The original width of the pixel of point [0][0] in job
     */
    private final int x;

    /**
     *
     * The original height of the pixel of point [0][0] in job
     */
    private final int y;

    /**
     *
     * The width of the job
     */
    private final int WIDTH;

    /**
     *
     * The height of the job
     */
    private final int HEIGHT;

    /**
     *
     * All the points for which the mandelvalue will be calculated
     */
    private final Point[][] job;

    /**
     *
     * The queue, containing all of the jobs that have to be calculated
     */
    private final Queue queue;

    /**
     *
     * The results of the calculation
     */
    private final Color[][] results;

    /**
     *
     * @param a The original width of element [0][0] in the job
     * @param b The original height of element [0][0] in the job
     * @param job Array that contains points for which the mandelvalue will be
     * calculated
     * @param w The width of the job
     * @param h The height of the job
     * @param queue The queue
     */
    public Job(int a, int b, Point[][] job, int w, int h, Queue queue) {
        this.queue = queue;
        x = a;
        y = b;
        WIDTH = w;
        HEIGHT = h;
        this.job = job;
        initializeJob();
        results = new Color[WIDTH][HEIGHT];
    }

    /**
     *
     * Calculate the job: calculate for each point the mandelvalue and put it in
     * the results array.
     */
    public void doJob() {
        for (int width = 0; width < WIDTH; width++) {
            for (int height = 0; height < HEIGHT; height++) {
                Point pnt = job[width][height];
                results[width][height] = pnt.bersteinColouring();
            }
        }
    }

    /**
     *
     * Fills the job with the correct points from the original grid, starting
     * from the original width and height
     */
    private void initializeJob() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Point p = queue.coordinates[x + i][y + j];
                job[i][j] = p;
            }
        }
    }

    /**
     *
     * @return The original width of the pixel of point [0][0] in job
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return The original height of the pixel of point [0][0] in job
     */
    public int getY() {
        return y;
    }

    /**
     *
     * @return the width
     */
    public int getWidth() {
        return WIDTH;
    }

    /**
     *
     * @return the height
     */
    public int getHeight() {
        return HEIGHT;
    }

    /**
     *
     * @param width the width of the result
     * @param height the height of the result
     * @return
     */
    public Color getResult(int width, int height) {
        return results[width][height];
    }

    public Queue getQueue() {
        return queue;
    }

    @Override
    public Job call() throws Exception {
        doJob();
        return this;
    }
}
