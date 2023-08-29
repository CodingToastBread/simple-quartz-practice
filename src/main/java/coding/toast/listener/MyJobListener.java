package coding.toast.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJobListener implements JobListener {
    public static final Logger log = LoggerFactory.getLogger(MyJobListener.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info("============= [JOB Listener] - jobToBeExecuted =============");
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("============= [JOB Listener] - jobExecutionVetoed =============");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.info("============= [JOB Listener] - jobWasExecuted =============");
    }
}
