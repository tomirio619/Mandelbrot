package worker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import mandelbrot.Point;
import mandelbrot.WritableGrid;

/**
 *
 * @author Tom
 */
public class Queue {

    /**
     *
     * The points in the grid
     */
    protected final Point[][] coordinates;

    /**
     *
     * Contains jobs that have to be calculated
     */
    private final ArrayList<Job> queue;

    /**
     *
     * Total number of jobs
     */
    private int totalJobs = 0;

    /**
     *
     * Size of the blocks that will be used to split the grid
     */
    public static final int chunkSIZE = 5;

    /**
     *
     * The list iterator for the queue
     */
    public final Iterator<Job> iterator;

    /**
     *
     * The width of the grid
     */
    private final int gridW;

    /**
     *
     * The height of the grid
     */
    private final int gridH;

    /**
     *
     * The grid
     */
    private final WritableGrid grid;

    /**
     *
     * @param coordinates All the points from the grid that have to be
     * calculated
     * @param gridW The width of the grid
     * @param gridH The height of the grid
     * @param grid
     */
    public Queue(Point[][] coordinates, int gridW, int gridH, WritableGrid grid) {
        this.grid = grid;
        this.coordinates = coordinates;
        queue = new ArrayList<>();
        this.gridW = gridW;
        this.gridH = gridH;
        makeJobs();
        iterator = queue.iterator();
    }

    /**
     * Create the jobs
     */
    private void makeJobs() {
        for (int width = 0; width <= gridW - chunkSIZE; width += chunkSIZE) {
            for (int height = 0; height <= gridH - chunkSIZE; height += chunkSIZE) {
                Job job = new Job(width, height, new Point[chunkSIZE][chunkSIZE], chunkSIZE, chunkSIZE, this);
                queue.add(job);
                totalJobs++;
            }
        }
    }

    /**
     * @return The total number of jobs created
     */
    public int getTotalJobs() {
        return totalJobs;
    }

}
