package me.sfeer.service;

import com.alibaba.fastjson.JSONObject;
import me.sfeer.domain.Topology;
import me.sfeer.domain.Result;
import me.sfeer.mapper.TopologyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TopologyService {

    @Resource
    private TopologyMapper topologyMapper;

    public List<JSONObject> findTopology() {
        return topologyMapper.selectTopology();
    }

    public List<JSONObject> findTopologyByRss(String uuid) {
        return topologyMapper.selectTopologyByRss(uuid);
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

    @Transactional
    public Result relateRss(String uuid, String ids) {
        topologyMapper.deleteRssTopo(uuid);
        for (String d : ids.split(","))
            topologyMapper.insertRssTopo(uuid, Long.parseLong(d));
        return new Result();
    }
}
