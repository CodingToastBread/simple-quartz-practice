package coding.toast.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTriggerListener implements TriggerListener {
    public static final Logger log = LoggerFactory.getLogger(MyTriggerListener.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        log.info("==================== [TRIGGER Listener] - triggerFired ====================");
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        log.info("==================== [TRIGGER Listener] - vetoJobExecution ====================");
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        log.info("==================== [TRIGGER Listener] - triggerMisfired ====================");
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        log.info("==================== [TRIGGER Listener] - triggerComplete ====================");
    }
}
