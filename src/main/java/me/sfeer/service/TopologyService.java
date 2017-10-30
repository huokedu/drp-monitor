package me.sfeer.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.sfeer.domain.Topology;
import me.sfeer.domain.Result;
import me.sfeer.mapper.TopologyMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TopologyService {

    @Resource
    private TopologyMapper topologyMapper;

    public List<Topology> findTopology() {
        return topologyMapper.selectTopology();
    }

    public List<Topology> findTopologyByRss(String uuid) {
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

    private JSONArray loopCate(String parent) {
        JSONArray x = new JSONArray();
        for (Map<String, String> map : topologyMapper.selectNodeCategory(parent)) {
            JSONObject o = JSON.parseObject("{\"id\":\"" + map.get("id") + "\",\"name\":\"" + map.get("name") + "\"}");
            JSONArray x1 = loopCate(map.get("id"));
            if (x1.size() > 0)
                o.put("children", x1);
            x.add(o);
        }
        return x;
    }

    public JSONArray cateTree() {
        return loopCate("-1");
    }
}
