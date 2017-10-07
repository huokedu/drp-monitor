package me.sfeer.service;

import me.sfeer.domain.RelationMap;
import me.sfeer.domain.Result;
import me.sfeer.mapper.RelationMapMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RelationMapService {

    @Resource
    private RelationMapMapper relationMapMapper;

    public List<RelationMap> findRelMap() {
        return relationMapMapper.findRelMap();
    }

    public Result createRelMap(RelationMap map) {
        relationMapMapper.createRelMap(map);
        return new Result();
    }

    public Result updateRelMap(RelationMap map) {
        relationMapMapper.updateRelMap(map);
        return new Result();
    }
}
