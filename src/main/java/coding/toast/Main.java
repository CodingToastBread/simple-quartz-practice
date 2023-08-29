package coding.toast;

import coding.toast.job.SampleJob;
import org.quartz.*;
import org.quartz.core.jmx.JobDataMapSupport;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


public class Main {
    public static void main(String[] args) {

        Scheduler scheduler = null;
        try {

            // Scheduler 의 설정값으로 사용할 Properties 를 생성합니다.
            // http://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/ 참고
            Properties properties = new Properties();
            properties.setProperty("org.quartz.scheduler.instanceName", "CodingToast_Scheduler");
            properties.setProperty("org.quartz.threadPool.threadCount", "15");
            properties.setProperty("org.quartz.threadPool.threadPriority", "4");
            properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");

            // 스케줄러를 생성합니다. 참고로 생성했다고 스케줄러가 실제 동작 상태에 들어간 게 아닙니다.
            scheduler = new StdSchedulerFactory(properties).getScheduler();

            // 스케줄러가 수행해야 할 일(= JobDetail instance)에서 사용할 DataMap 을 생성합니다.
            JobDataMap jobDataMap = JobDataMapSupport
                    .newJobDataMap(Map.of(
                            "param1", "value1",
                            "param2", "value2"
                    ));

            // JobBuilder 를 통해서 스케줄러에 줄 파리미터인 JobDetail 을 생성합니다.
            JobDetail jobDetail = JobBuilder.newJob()
                    .ofType(SampleJob.class) // 반드시 세팅해줘야 한다!
                    .withIdentity("jobDetail1", "jobGroup")  // 스케줄러가 JobDetail 을 다른 JobDetail 들과 구별하기 위한 일종의 아이디를 제공합니다.
                    .setJobData(jobDataMap) // 앞서 생성한 DataMap 을 넘겨줍니다.
                    .usingJobData("param3", "value3") // DataMap 에 추가적인 값들을 넣어줍니다.
                    .withDescription("a simple quartz job") // 이 일(JobDetail)에 대한 설명을 작성해줍니다.
                    .build();


            // 상세하게 "어떤 것"(= JobDetail) 을 할지를 지정했습니다만,
            // 정작 중요한 "언제할 지" 가 결정되지 않은 상태입니다.
            // 이를 위해서 Trigger 인스턴스를 생성합니다.
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "triggerGroup")
                    // .usingJobData(jobDataMap) // JobDetail 과 마찬가지로 DataMap 을 사용할 수 있습니다.
                    // .withPriority() // 만약에 같은 시간에 수행해야될 Trigger 가 존재한다면, 그때 우선순위를 어떻게 할지 지정합니다.
                    .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ? *"))
                    // .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    // .withIntervalInSeconds(1).repeatForever())
                    .build();



            /*try(InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("config/quartz-config.properties")) {
                Properties prop = new Properties();
                prop.load(resourceAsStream);
                System.out.println(prop);
            } catch (IOException e) {
                e.printStackTrace();
            }*/





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