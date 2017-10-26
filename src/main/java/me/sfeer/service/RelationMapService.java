package me.sfeer.service;

import me.sfeer.domain.Topology;
import me.sfeer.domain.Result;
import me.sfeer.mapper.TopologyMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RelationMapService {

    @Resource
    private TopologyMapper relationMapMapper;

    public List<Topology> findRelMap() {
        return relationMapMapper.findRelMap();
    }

    public Topology findRelMap(Long id) { return relationMapMapper.findRelMapById(id); }

    public Result createRelMap(Topology map) {
        relationMapMapper.createRelMap(map);
        return new Result();
    }

    public Result updateRelMap(Topology map) {
        relationMapMapper.updateRelMap(map);
        return new Result();
    }
}
