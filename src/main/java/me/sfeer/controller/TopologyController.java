package me.sfeer.controller;

import me.sfeer.domain.Topology;
import me.sfeer.domain.Result;
import me.sfeer.service.TopologyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/topo")
public class TopologyController {

    @Resource
    private TopologyService topologyService;

    @GetMapping("/list")
    public List<Topology> list() {
        return topologyService.findTopology();
    }

    @GetMapping("/get/{id}")
    public Topology get(@PathVariable String id) {
        return topologyService.findTopology(Long.parseLong(id));
    }

    @PostMapping("/add")
    public Result create(@RequestBody Map<String, String> param) {
        Topology map = new Topology();
        map.setName(param.get("name"));
        map.setGroup(param.get("group"));
        map.setNodes(param.get("nodes"));
        map.setLinks(param.get("links"));
        map.setAreas(param.get("areas"));
        return topologyService.createTopology(map);
    }

    @PutMapping("/modify/{id}")
    public Result update(@PathVariable String id, @RequestBody Map<String, String> param) {
        Topology map = new Topology();
        map.setId(Long.parseLong(id));
        map.setName(param.get("name"));
        map.setGroup(param.get("group"));
        map.setNodes(param.get("nodes"));
        map.setLinks(param.get("links"));
        map.setAreas(param.get("areas"));
        return topologyService.modifyTopology(map);
    }
}
