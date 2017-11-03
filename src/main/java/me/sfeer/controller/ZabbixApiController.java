package me.sfeer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    private JSONObject pageHelper(JSONArray s, int num, int size) {
        JSONObject res = new JSONObject();
        int from = size * (num - 1);
        int to = size * num;
        int total = s.size();
        res.put("total", total);
        if (from < total)
            if (to > total)
                res.put("data", s.subList(from, total));
            else
                res.put("data", s.subList(from, to));
        else
            res.put("data", new JSONArray());
        return res;
    }

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
    public JSONObject getHostData(@RequestParam("hostid") String id) {
        return zabbixApiService.getHostData(id);
    }

    // 模版列表
    @GetMapping("/templates")
    public JSONObject getTemplates(@RequestParam Map<String, String> param) {
        return pageHelper(zabbixApiService.getTemplates(param.get("name")),
                Integer.parseInt(param.get("pageNum")),
                Integer.parseInt(param.get("pageSize")));
    }

    // 模版下拉
    @GetMapping("/template/list")
    public JSONArray getTemplateList() {
        return zabbixApiService.getTemplateList();
    }

    @GetMapping("/group/list")
    public JSONArray getHostGroupList() {
        return zabbixApiService.getHostGroupList();
    }

    // 主机列表
    @GetMapping("/hosts")
    public JSONObject getHosts(@RequestParam Map<String, String> param) {
        return pageHelper(zabbixApiService.getHosts(param.get("name")),
                Integer.parseInt(param.get("pageNum")),
                Integer.parseInt(param.get("pageSize")));
    }

    // 历史数据
    // http://127.0.0.1:9000/zabbix/history?itemid=23295&begin=2016-10-01%2000:00:00
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
