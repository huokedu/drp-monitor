package me.sfeer.controller;

import me.sfeer.service.ZabbixApiService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/zabbix")
public class ZabbixApiController {

    @Resource
    private ZabbixApiService zabbixApiService;


    @GetMapping("/latestdata")
    public Map<String, String> getHostData(@RequestParam("hostid") String id) {
        return zabbixApiService.getHostData(id);
    }
}
