package me.sfeer.service;

import me.sfeer.domain.Topology;
import me.sfeer.domain.Result;
import me.sfeer.mapper.TopologyMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TopologyService {

    @Resource
    private TopologyMapper topologyMapper;

    public List<Topology> findTopology() {
        return topologyMapper.selectTopology();
    }

    public List<Topology> findTopologyByRss(String uuid) {
        return topologyMapper.selectTopologyByUuid(uuid);
    }

    public Topology findTopology(Long id) {
        return topologyMapper.selectTopologyById(id);
    }

    public Result createTopology(Topology map) {
        topologyMapper.insertTopology(map);
        return new Result();
    }

    public Result modifyTopology(Topology map) {
        topologyMapper.updateTopology(map);
        return new Result();
    }
}
