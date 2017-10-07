package me.sfeer.controller;

import me.sfeer.domain.RelationMap;
import me.sfeer.domain.Result;
import me.sfeer.service.RelationMapService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/map")
public class RelationMapController {

    @Resource
    private RelationMapService relationMapService;

    @GetMapping("/list")
    public List<RelationMap> list() {
        return relationMapService.findRelMap();
    }

    @GetMapping("/get/{id}")
    public RelationMap get(@PathVariable String id) {
        return relationMapService.findRelMap(Long.parseLong(id));
    }

    @PostMapping("/add")
    public Result create(@RequestBody Map<String, String> param) {
        RelationMap map = new RelationMap();
        map.setName(param.get("name"));
        map.setGroup(param.get("group"));
        map.setNodes(param.get("nodes"));
        map.setLinks(param.get("links"));
        map.setAreas(param.get("areas"));
        return relationMapService.createRelMap(map);
    }

    @PutMapping("/modify/{id}")
    public Result update(@PathVariable String id, @RequestBody Map<String, String> param) {
        RelationMap map = new RelationMap();
        map.setId(Long.parseLong(id));
        map.setName(param.get("name"));
        map.setGroup(param.get("group"));
        map.setNodes(param.get("nodes"));
        map.setLinks(param.get("links"));
        map.setAreas(param.get("areas"));
        return relationMapService.updateRelMap(map);
    }
}
