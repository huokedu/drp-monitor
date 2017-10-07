package me.sfeer.controller;

import me.sfeer.domain.Result;
import me.sfeer.scheduler.DynamicTask;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/shell")
public class DrpShellController {

    @Resource
    private DynamicTask dynamicTasktask;

    @PutMapping("/update/{id}")
    public Result updateDrpShellCron(@PathVariable String id, @RequestBody Map<String, String> param) {
        return dynamicTasktask.updateCronEexpress(id, param.get("cron"));
    }
}
