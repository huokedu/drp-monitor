package me.sfeer.scheduler;

import me.sfeer.domain.DrpShell;
import me.sfeer.domain.Result;
import me.sfeer.service.DrpShellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class DynamicTask implements SchedulingConfigurer {

    @Resource
    private DrpShellService drpShellService;

    private static final Logger log = LoggerFactory.getLogger(DynamicTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private Map<String, DrpShell> drp_shell = new HashMap<>();

    public Result updateCronEexpress(String key, String cron) {
        DrpShell shell = drp_shell.get(key);
        shell.setCron(cron);
        drp_shell.put(key, shell);
        return new Result();
    }

    private class Rs implements Runnable {

        private DrpShell shell;

        Rs(DrpShell shell) {
            this.shell = shell;
        }

        public void run() {
            log.info("脚本{},当前时间：{}", shell.getContent(), dateFormat.format(new Date()));
        }
    }

    private class Tg implements Trigger {

        private DrpShell shell;

        Tg(DrpShell shell) {
            this.shell = shell;
        }

        public Date nextExecutionTime(TriggerContext triggerContext) {
            CronTrigger trigger = new CronTrigger(shell.getCron());
            return trigger.nextExecutionTime(triggerContext);
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 初始化读取数据库中所有脚本
        for (DrpShell shell : drpShellService.findAllDrpShell())
            drp_shell.put(shell.getId().toString(), shell);
        Map<Runnable, Trigger> tasks = new HashMap<>();
        for (String key : drp_shell.keySet())
            tasks.put(new Rs(drp_shell.get(key)), new Tg(drp_shell.get(key)));
        taskRegistrar.setTriggerTasks(tasks);
    }
}
