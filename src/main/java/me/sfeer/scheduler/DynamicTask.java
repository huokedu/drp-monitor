package me.sfeer.scheduler;

import me.sfeer.domain.DrpShell;
import me.sfeer.domain.Result;
import me.sfeer.service.DrpShellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 初始化读取数据库中所有脚本
        for (DrpShell shell : drpShellService.findAllDrpShell())
            drp_shell.put(shell.getId().toString(), shell);
        Map<Runnable, Trigger> tasks = new HashMap<>();
        for (String key : drp_shell.keySet()) {
            DrpShell shell = drp_shell.get(key);
            tasks.put(
                    () -> log.info("脚本{},当前时间：{}", shell.getContent(), dateFormat.format(new Date())),
                    triggerContext -> {
                        CronTrigger trigger = new CronTrigger(shell.getCron());
                        return trigger.nextExecutionTime(triggerContext);
                    }
            );
        }
        taskRegistrar.setTriggerTasks(tasks);
    }
}
