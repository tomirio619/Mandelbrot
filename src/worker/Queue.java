package worker;

import java.util.LinkedList;
import mandelbrot.Point;

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
    private final LinkedList<Job> queue;

    /**
     *
     * Contains jobs that have been calculated
     */
    protected final LinkedList<Job> ready;

    /**
     *
     * Total number of jobs
     */
    private int totalJobs = 0;

    /**
     *
     * If the last calculated job is added to the ready queue this boolean will
     * be set to True. This is determined by looking at the at jobsCompleted
     * when addCompletedJob() is called.
     */
    public boolean allJobsAdded = false;

    /**
     *
     * Indicates if there are still jobs in the queue
     */
    public boolean jobsAvailable = true;

    /**
     *
     * Number of completed jobs added to the ready queue
     */
    private int jobsCompleted = 0;

    /**
     *
     * Size of the blocks that will be used to split the grid
     */
    private static final int chunkSIZE = 5;

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
     * @param coordinates All the points from the grid that have to be
     * calculated
     * @param gridW The width of the grid
     * @param gridH The height of the grid
     */
    public Queue(Point[][] coordinates, int gridW, int gridH) {
        this.coordinates = coordinates;
        queue = new LinkedList<>();
        ready = new LinkedList<>();
        this.gridW = gridW;
        this.gridH = gridH;
        makeJobs();
    }

    /**
     * Create the jobs
     */
    private void makeJobs() {
        for (int width = 0; width <= gridW - chunkSIZE; width += chunkSIZE) {
            for (int height = 0; height <= gridH - chunkSIZE; height += chunkSIZE) {
                Job job = new Job(width, height, new Point[chunkSIZE][chunkSIZE], chunkSIZE, chunkSIZE, this);
                queue.push(job);
                totalJobs++;
            }
        }
    }

    /**
     *
     * @return The last job from the queue
     */
    public synchronized Job getJob() {
        while (queue.size() == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        Job job = queue.pop();
        if (queue.size() == 0) {
            //we just popped the last job from the queue
            jobsAvailable = false;
            notifyAll();
            return job;
        } else {
            notifyAll();
            return job;
        }
    }

    /**
     *
     * @param completedJob The calculated job that will be added to the ready
     * queue
     */
    public synchronized void addCompletedJob(Job completedJob) {
        if (jobsCompleted == totalJobs - 1) {
            // this is the last calculated job that will be added to the ready queue
            ready.push(completedJob);
            jobsCompleted++;
            allJobsAdded = true;
            notifyAll();
        } else {
            ready.push(completedJob);
            jobsCompleted++;
            notifyAll();
        }
    }

    /**
     *
     * @return The last calculated job from the ready queue
     */
    public synchronized Job getCompletedJob() {
        while (ready.size() == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        Job job = ready.pop();
        notifyAll();
        return job;
    }
}
