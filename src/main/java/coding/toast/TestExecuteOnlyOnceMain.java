package coding.toast;

import coding.toast.job.SampleJob;
import org.quartz.*;
import org.quartz.core.jmx.JobDataMapSupport;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Map;
import java.util.Properties;


public class TestExecuteOnlyOnceMain {
    public static void main(String[] args) {

        Scheduler scheduler = null;
        try {

            // Scheduler 의 설정값으로 사용할 Properties 를 생성합니다.
            // http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/ 참고
            Properties properties = new Properties();
            properties.setProperty("org.quartz.scheduler.instanceName", "coding-toast");
            properties.setProperty("org.quartz.threadPool.threadCount", "15");
            properties.setProperty("org.quartz.threadPool.threadPriority", "4");
            properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");

            // 스케줄러를 생성합니다. 참고로 생성했다고 스케줄러가 실제 동작 상태에 들어간 게 아닙니다.
            scheduler = new StdSchedulerFactory(properties).getScheduler();

            JobDataMap jobDataMap = JobDataMapSupport.newJobDataMap(Map.of("param1", "value1"));

            // JobBuilder 를 통해서 스케줄러에 줄 파리미터인 JobDetail 을 생성합니다.
            JobDetail jobDetail = JobBuilder.newJob()
                    .ofType(SampleJob.class) // 반드시 세팅해줘야 한다!
                    .usingJobData(jobDataMap)
                    .withIdentity("attempt-only-once", "jobGroup")  // 스케줄러가 JobDetail 을 다른 JobDetail 들과 구별하기 위한 일종의 아이디를 제공합니다.
                    .build();

            // 아래처럼 simpleTrigger 를 생성하면 딱 한번만 실행하고 끝납니다.
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "triggerGroup")
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                    .build();


            // "언제(=trigger)", "무엇(=jobDetail)"을 할지를 스케줄러에게 알려줍니다.
            scheduler.scheduleJob(jobDetail, trigger);

            // start 를 호출해야 진짜 스케줄러가 standby mode 에 들어갑니다.
            scheduler.start();

            // 10초 정도만 스케줄러를 실행시키겠습니다.
            Thread.sleep(10000);


        } catch (SchedulerException | InterruptedException e) {
            // 테스트니까 에러는 크게 신경쓰지 않겠습니다.
            e.printStackTrace();
        } finally {
            // scheduler 는 shutdown 해야만 프로그램이 종료됩니다.
            // 참고로 scheduler 는 데몬 쓰레드가 아닌 user 쓰레드이기 때문에 shutdown 을 안 하면 프로그램이 종료되지 않습니다.
            if (scheduler != null) try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
}