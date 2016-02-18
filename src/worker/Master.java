package worker;

import mandelbrot.WritableGrid;

/**
 *
 * @author Tom
 */
public class Master implements Runnable {

    /**
     *
     * The queue
     */
    private final Queue queue;

    /**
     *
     * The grid
     */
    private final WritableGrid grid;

    /**
     *
     * @param queue The queue
     * @param grid The grid
     */
    public Master(Queue queue, WritableGrid grid) {
        this.queue = queue;
        this.grid = grid;
    }

    /**
     *
     * Master pops jobs from the ready queue and calls updateJob to write the
     * results to the screen.
     */
    @Override
    public void run() {
        while (!queue.allJobsAdded) {
            Job j = queue.getCompletedJob();
            grid.updateJob(j);
        }
        //All jobs are in the ready queue
        while (queue.ready.size() > 0) {
            Job j = queue.getCompletedJob();
            grid.updateJob(j);
        }
    }

}
