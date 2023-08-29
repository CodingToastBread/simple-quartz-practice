package coding.toast.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(SampleJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("[Job key: {}] / [Trigger Key: {}]",
                context.getJobDetail().getKey(),
                context.getTrigger().getKey());

        context.getMergedJobDataMap()
                .forEach((key, val) -> log.info("key: {} / value: {}", key, val));
    }
}
