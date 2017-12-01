package me.sfeer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.sfeer.domain.Host;
import me.sfeer.domain.Result;
import me.sfeer.service.ZabbixService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/zabbix")
public class ZabbixController {
    private static final Logger log = LoggerFactory.getLogger(ZabbixController.class);

    @Resource
    private ZabbixService zabbixService;

    private JSONObject pageHelper(JSONArray s, int num, int size) {
        JSONObject res = new JSONObject();
        int from = size * (num - 1);
        int to = size * num;
        int total = s.size();
        res.put("total", total);
        if (from < total && size != 0)
            if (to > total)
                res.put("data", s.subList(from, total));
            else
                res.put("data", s.subList(from, to));
        else
            res.put("data", new JSONArray());
        return res;
    }

    // 添加host
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
        return zabbixService.createHost(host);
    }

    // 根据资源id获取host
    @GetMapping("/host/rss/{uuid}")
    public Host getHostByRss(@PathVariable String uuid) {
        return zabbixService.getHostByRss(uuid);
    }

    // 获取主机的最新监控项值
    @GetMapping("/latestdata")
    public JSONArray getHostData(@RequestParam("hostid") String id) {
        return zabbixService.getHostData(id);
    }

    // 模版列表
    @GetMapping("/templates")
    public JSONObject getTemplates(@RequestParam Map<String, String> param) throws UnsupportedEncodingException {
        int pageNum = param.containsKey("pageNum") ? Integer.parseInt(param.get("pageNum")) : 1;
        int pageSize = param.containsKey("pageSize") ? Integer.parseInt(param.get("pageSize")) : 0;
        String name = param.containsKey("name") ? URLDecoder.decode(param.get("name"), "utf-8") : "";
        return pageHelper(zabbixService.getTemplates(name), pageNum, pageSize);
    }

    // 模版下拉
    @GetMapping("/template/list")
    public JSONArray getTemplateList() {
        return zabbixService.getTemplateList();
    }

    // 分组下拉
    @GetMapping("/group/list")
    public JSONArray getHostGroupList() {
        return zabbixService.getHostGroupList();
    }

    // 主机下拉
    @GetMapping("/host/list")
    public JSONArray getHostList(@RequestParam("groupid") String id) {
        return zabbixService.getHostList(id);
    }

    // 主机列表
    @GetMapping("/hosts")
    public JSONObject getHosts(@RequestParam Map<String, String> param) {
        return pageHelper(zabbixService.getHosts(param.get("name")),
                Integer.parseInt(param.get("pageNum")),
                Integer.parseInt(param.get("pageSize")));
    }

    // 图形下拉
    @GetMapping("/graph/list")
    public JSONArray getGraphList(@RequestParam("hostid") String id) {
        return zabbixService.getGraphList(id);
    }

    // 图形包含监控项
    @GetMapping("/items")
    public JSONArray getItems(@RequestParam("graphid") String id) {
        return zabbixService.getItemsByGraph(id);
    }

    // 历史数据
    @GetMapping("/history")
    public JSONArray getHistoryData(@RequestParam("itemids") String ids,
                                    @RequestParam("type") Integer type,
                                    @RequestParam(value = "begin", required = false) Integer begin,
                                    @RequestParam(value = "end", required = false) Integer end) {
        return zabbixService.getHistoryData(ids, type, begin, end);
    }

    // 趋势数据
    @GetMapping("/trend")
    public JSONArray getTrendData(@RequestParam("itemids") String ids,
                                  @RequestParam("type") Integer type,
                                  @RequestParam(value = "begin", required = false) Integer begin,
                                  @RequestParam(value = "end", required = false) Integer end) {
        return zabbixService.getTrendData(ids, type, begin, end);
    }

}
