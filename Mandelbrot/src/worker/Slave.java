package worker;

/**
 *
 * @author Tom
 */
public class Slave implements Runnable {

    /**
     * The queue
     */
    private final Queue queue;

    /**
     *
     * @param queue The queue
     */
    public Slave(Queue queue) {
        this.queue = queue;
    }

    /**
     * Run method of the Slave, while there are jobs available, get them from
     * the queue and calculate them. Add Calculated jobs to the ready queue.
     */
    @Override
    public void run() {
        while (queue.jobsAvailable) {
            Job job = queue.getJob();
            job.doJob();
            queue.addCompletedJob(job);
        }
    }

}
