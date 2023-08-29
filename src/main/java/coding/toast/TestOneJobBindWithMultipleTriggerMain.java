package coding.toast;

import coding.toast.job.SampleJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.core.jmx.JobDataMapSupport.newJobDataMap;


public class TestOneJobBindWithMultipleTriggerMain {
    public static void main(String[] args) {

        Scheduler scheduler = null;
        try {

            // http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/ 참고
            Properties properties = new Properties();
            properties.setProperty("org.quartz.scheduler.instanceName", "CodingToast_Scheduler");
            properties.setProperty("org.quartz.threadPool.threadCount", "4"); // thread pool size
            properties.setProperty("org.quartz.threadPool.threadPriority", "4");
            properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");

            // 스케줄러를 생성합니다. 참고로 생성했다고 스케줄러가 실제 동작 상태에 들어간 게 아닙니다.
            scheduler = new StdSchedulerFactory(properties).getScheduler();

            // 스케줄러가 수행해야 할 일(= JobDetail instance)에서 사용할 DataMap 을 생성합니다.
            JobDataMap jobDataMap = newJobDataMap(Map.of("param1", "value1"));


            //하나의 Job 에 여러 Trigger 걸기
            JobDetail jobDetail = newJob()
                    .ofType(SampleJob.class)
                    .withIdentity("jobDetail-1")
                    .setJobData(jobDataMap)
                    .build();


            // 1 초에 한번
            Trigger trigger = newTrigger()
                    .withIdentity("trigger-1-sec", "triggerGroup")
                    .withSchedule(cronSchedule("0/1 * * * * ? *"))
                    .build();

            // 3 초에 한번
            Trigger trigger2 = newTrigger()
                    .withIdentity("trigger-3-sec", "triggerGroup")
                    .withSchedule(cronSchedule("0/3 * * * * ? *"))
                    .build();


            // 하나의 Job 에 Trigger 를 여러개 넣을 수 있는 방법들
            // scheduler.scheduleJob(jobDetail, Set.of(trigger, trigger2), false); // 방법 1
            scheduler.scheduleJobs(Map.of(jobDetail, Set.of(trigger, trigger2)), false); // 방법 2

            // 스케줄러 시작
            scheduler.start();
            Thread.sleep(9000);

        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (scheduler != null) try {scheduler.shutdown();} catch (SchedulerException e) {e.printStackTrace();}
        }
    }
}