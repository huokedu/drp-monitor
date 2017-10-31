package me.sfeer.controller;

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

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam Map<String, String> param) {
        PageHelper.startPage(Integer.parseInt(param.get("pageNum")), Integer.parseInt(param.get("pageSize")));
        List<Topology> list = topologyService.findTopology();
        PageInfo<Topology> page = new PageInfo<>(list);
        Map<String, Object> res = new HashMap<>();
        res.put("total", page.getTotal());
        res.put("data", list);
        return res;
    }

    // TODO 需要修改成多对多
    @GetMapping("/list/{uuid}")
    public List<Topology> listByRss(@PathVariable String uuid) {
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
