package me.sfeer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import me.sfeer.domain.Result;
import me.sfeer.domain.Topology;
import me.sfeer.scheduler.DynamicTask;
import me.sfeer.service.RssService;
import me.sfeer.service.TopologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/drp")
public class DrpController {
    private static final Logger log = LoggerFactory.getLogger(DrpController.class);

    @Resource
    private RssService rssService;

    @Resource
    private DynamicTask dynamicTask;

    @Resource
    private TopologyService topologyService;

    // 资源树结构
    @GetMapping("/rss/tree")
    public JSONArray cateList() {
        return rssService.cateTree();
    }

    // 资源相关的拓扑图列表
    @GetMapping("/rss/{uuid}/topos")
    public List<JSONObject> listByRss(@PathVariable String uuid) {
        return topologyService.findTopologyByRss(uuid);
    }

    // 动态修改调度任务
    @PutMapping("/task/{id}")
    public Result updateDrpShellCron(@PathVariable String id, @RequestBody Map<String, String> param) {
        return dynamicTask.updateCronEexpress(id, param.get("cron"));
    }

    // 拓扑图列表（带分页）
    @GetMapping("/topos")
    public Map<String, Object> list(@RequestParam Map<String, String> param) throws UnsupportedEncodingException {
        int pageNum = param.containsKey("pageNum") ? Integer.parseInt(param.get("pageNum")) : 1;
        int pageSize = param.containsKey("pageSize") ? Integer.parseInt(param.get("pageSize")) : 0;
        String name = param.containsKey("name") ? URLDecoder.decode(param.get("name"), "utf-8") : "";
        PageHelper.startPage(pageNum, pageSize, true, null, true);
        List<JSONObject> list = topologyService.findTopology(name);
        PageInfo<JSONObject> page = new PageInfo<>(list);
        Map<String, Object> res = new HashMap<>();
        res.put("total", page.getTotal());
        res.put("data", list);
        return res;
    }

    @GetMapping("/topo/{id}")
    public Topology get(@PathVariable String id) {
        return topologyService.findTopology(Long.parseLong(id));
    }

    @PostMapping("/topo")
    public Result create(@RequestBody Map<String, String> param) {
        Topology topo = new Topology();
        topo.setName(param.get("name"));
        topo.setGroup(param.get("group"));
        topo.setNodes(param.get("nodes"));
        topo.setLinks(param.get("links"));
        topo.setAreas(param.get("areas"));
        return topologyService.createTopology(topo);
    }

    @PutMapping("/topo/{id}")
    public Result update(@PathVariable String id, @RequestBody Map<String, String> param) {
        Topology topo = new Topology();
        topo.setId(Long.parseLong(id));
        topo.setName(param.get("name"));
        topo.setGroup(param.get("group"));
        topo.setNodes(param.get("nodes"));
        topo.setLinks(param.get("links"));
        topo.setAreas(param.get("areas"));
        return topologyService.modifyTopology(topo);
    }

    // 拓扑图关联
    @PutMapping("/topo/rel")
    public Result relation(@RequestBody Map<String, String> param) {
        return topologyService.relateRss(param.get("uuid"), param.get("ids"));
    }

    // 首页概览信息
    @GetMapping("/main")
    public JSONObject getMainInfo() {
        JSONObject main = new JSONObject();
        // 基础设施
        main.put("facility", rssService.overviewFacility());
        // 设备
        main.put("device", rssService.overviewDevice());
        // 资源池
        main.put("pool", rssService.overviewPool());
        // 节点
        main.put("node", rssService.overviewNode());
        // 服务
        main.put("service", rssService.overviewService());
        // 应用
        main.put("app", rssService.overviewApp());
        return main;
    }

    // 应用实时状态
    @GetMapping("/appinfo")
    public JSONArray getAppInfo() {
        return rssService.overviewApp();
    }

    // 根据资源池ID查询所属节点信息
    @GetMapping("/pool/{uuid}/nodes")
    public List<JSONObject> getNodesByPool(@PathVariable String uuid) {
        return rssService.getNodesByPool(uuid);
    }

    // 根据数据库服务查找关联数据库监控hostid
    @GetMapping("/db/{uuid}/monitor")
    public List<JSONObject> getHostIdsByDb(@PathVariable String uuid) {
        return rssService.getHostIdsByDb(uuid);
    }

    // 根据中间件服务查找关联节点监控hostid
    @GetMapping("/mw/{uuid}/monitor")
    public List<JSONObject> getHostIdsByMid(@PathVariable String uuid) {
        return rssService.getHostIdsByMw(uuid);
    }

}
