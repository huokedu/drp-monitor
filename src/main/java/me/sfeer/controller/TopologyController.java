package me.sfeer.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import me.sfeer.domain.Topology;
import me.sfeer.domain.Result;
import me.sfeer.service.TopologyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/topo")
public class TopologyController {

    @Resource
    private TopologyService topologyService;

    // 下拉框使用
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam Map<String, String> param) {
        int pageNum = param.get("pageNum") == null ? 0 : Integer.parseInt(param.get("pageNum"));
        int pageSize = param.get("pageSize") == null ? 0 : Integer.parseInt(param.get("pageSize"));
        PageHelper.startPage(pageNum, pageSize, true, null, true);
        List<JSONObject> list = topologyService.findTopology();
        PageInfo<JSONObject> page = new PageInfo<>(list);
        Map<String, Object> res = new HashMap<>();
        res.put("total", page.getTotal());
        res.put("data", list);
        return res;
    }

    @GetMapping("/list/{uuid}")
    public List<JSONObject> listByRss(@PathVariable String uuid) {
        return topologyService.findTopologyByRss(uuid);
    }

    @GetMapping("/get/{id}")
    public Topology get(@PathVariable String id) {
        return topologyService.findTopology(Long.parseLong(id));
    }

    @PostMapping("/add")
    public Result create(@RequestBody Map<String, String> param) {
        Topology topo = new Topology();
        topo.setName(param.get("name"));
        topo.setGroup(param.get("group"));
        topo.setNodes(param.get("nodes"));
        topo.setLinks(param.get("links"));
        topo.setAreas(param.get("areas"));
        return topologyService.createTopology(topo);
    }

    // 拓扑图关联
    @PutMapping("/rel")
    public Result relation(@RequestBody Map<String, String> param) {
        return topologyService.relateRss(param.get("uuid"), param.get("ids"));
    }

    @PutMapping("/modify/{id}")
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
}
