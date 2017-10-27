package me.sfeer.controller;

import com.alibaba.fastjson.JSONArray;
import me.sfeer.domain.Host;
import me.sfeer.domain.Result;
import me.sfeer.service.ZabbixApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/zabbix")
public class ZabbixApiController {

    @Resource
    private ZabbixApiService zabbixApiService;

    private static final Logger log = LoggerFactory.getLogger(ZabbixApiController.class);

    @PostMapping("/host")
    public Result addHost(@RequestBody Map<String, String> param) {
        Host host = new Host();
        host.setHost(param.get("host"));
        host.setName(param.get("name"));
        host.setType(Integer.parseInt(param.get("type")));
        host.setIp(param.get("ip"));
        host.setGroupId(Long.parseLong(param.get("groupid")));
        host.setPort(param.get("port"));
        host.setTemplateId(Long.parseLong(param.get("templateid")));
        host.setRssId(param.get("rssuuid"));
        return zabbixApiService.createHost(host);
    }

    // 获取主机的最新监控项值
    @GetMapping("/latestdata")
    public Map<String, String> getHostData(@RequestParam("hostid") String id) {
        return zabbixApiService.getHostData(id);
    }

    // 模版列表
    @GetMapping("/templates")
    public JSONArray getTemplateList(@RequestParam(value = "name", required = false) String name) {
        JSONArray s = zabbixApiService.getTemplateList(name);
        log.info("{}", s.size());
        return s;
    }

    // 主机列表
    @GetMapping("/hosts")
    public JSONArray getHostList(@RequestParam(value = "name", required = false) String name) {
        JSONArray s = zabbixApiService.getHostList(name);
        log.info("{}", s.size());
        return s;
    }

    // 历史数据 49170
    @GetMapping("/history")
    public JSONArray getHistoryData(@RequestParam("itemid") String id,
                                    @RequestParam(value = "begin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date begin,
                                    @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end) {
        // TODO begin为空时设置默认查询一天
        log.info("time {}", begin.getTime() / 1000L);
        // TODO 根据查询时间间隔判断查询history还是趋势表
        JSONArray s = zabbixApiService.getHistoryData(id, begin, end);
        log.info("{}", s.size());
        return s;
    }

}
