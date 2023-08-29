package coding.toast;

import coding.toast.job.SampleJob;
import coding.toast.listener.MyJobListener;
import coding.toast.listener.MyTriggerListener;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.core.jmx.JobDataMapSupport.newJobDataMap;


public class TestJobListenerMain {
    public static void main(String[] args) {

        Scheduler scheduler = null;
        try {

            // Scheduler 의 설정값으로 사용할 Properties 를 생성합니다.
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

           // JobBuilder 를 통해서 스케줄러에 줄 파리미터인 JobDetail 을 생성합니다.
            JobDetail jobDetail = newJob()
                    .ofType(SampleJob.class)
                    .withIdentity("jobDetail-1")
                    .setJobData(jobDataMap)
                    .build();


            JobDetail jobDetail2 = newJob()
                    .ofType(SampleJob.class)
                    .withIdentity("jobDetail-2")
                    .setJobData(jobDataMap)
                    .build();

            // Trigger 생성
            Trigger trigger = newTrigger()
                    .withIdentity("trigger-1", "triggerGroup")
                    .withSchedule(cronSchedule("0/2 * * * * ? *"))
                    .build();

            Trigger trigger2 = newTrigger()
                    .withIdentity("trigger-2", "triggerGroup")
                    .withSchedule(cronSchedule("0/2 * * * * ? *"))
                    // .forJob(jobDetail) // trigger 에 jobkey 를 세팅하는 건데, scheduler.scheduleJob 수행 시에 자동으로 세팅되니 Skip!
                    .build();


            // 하나의 trigger 에 하나의 job 만 매핑할 때는 아래처럼 scheduleJob 메소드 사용
            // 참고로 하나의 Job 에 대하여 여러 Trigger 가 매핑되도록 할 수 있다.
            // 반대로 하나의 Trigger 에 여러 Job 을 매핑하지는 못한다... 상당히 불편함...
            // 이거에 대한 논의가 있었는데 한번쯤 봐두면 좋을 듯하다.
            // https://stackoverflow.com/questions/7999870/why-cant-i-schedule-multiple-jobs-to-the-same-trigger-in-quartz-net
            scheduler.scheduleJob(jobDetail, trigger);
            // scheduler.scheduleJob(jobDetail2, trigger2);



            // ** 리스너 등록합니다!
            ListenerManager listenerManager = scheduler.getListenerManager();

            // 방법1: 특정 Job 에만 종속된 Listener 를 등록한다.
            /*listenerManager.addJobListener(new MyJobListener(),
                    KeyMatcher.keyEquals(JobKey.jobKey("jobDetail-1", "jobGroup")));*/

            // 방법2: job 리스너 특정 그룹에 대해서만 모니터링하도록 합니다.
            listenerManager.addJobListener(new MyJobListener(),
                    GroupMatcher.groupEquals("jobGroup"));


            // Trigger Listener 도 Job Listener 와 마찬가지 방식으로 add 한다.
            listenerManager.addTriggerListener(new MyTriggerListener());

            scheduler.start();
            Thread.sleep(4000);

        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (scheduler != null) try {scheduler.shutdown();} catch (SchedulerException e) {e.printStackTrace();}
        }
    }
}