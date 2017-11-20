package me.sfeer.mapper;

import com.alibaba.fastjson.JSONObject;
import me.sfeer.domain.Topology;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TopologyMapper {
    @Select("select id,name,`group`,ctime from drp_rm_topology")
    List<JSONObject> selectTopology();

    @Select("select a.id,a.name,a.`group`,a.ctime from drp_rm_topology a, drp_rm_rss_topology b where a.id=b.topoid and b.rss_uuid=#{uuid}")
    List<JSONObject> selectTopologyByRss(@Param("uuid") String uuid);

    @Select("select * from drp_rm_topology where id=#{id}")
    Topology selectTopologyById(@Param("id") Long id);

    @Insert("insert into drp_rm_topology(name,`group`,nodes,links,areas,ctime) values (#{name},#{group},#{nodes},#{links},#{areas},now())")
    void insertTopology(Topology topo);

    @Update("update drp_rm_topology set name=#{name},`group`=#{group},nodes=#{nodes},links=#{links},areas=#{areas} where id=#{id}")
    void updateTopology(Topology topo);

    @Delete("delete from drp_rm_rss_topology where rss_uuid=#{uuid}")
    void deleteRssTopo(String uuid);

    @Insert("insert into drp_rm_rss_topology(rss_uuid,topoid) values (#{uuid},#{id})")
    void insertRssTopo(@Param("uuid") String uuid, @Param("id") Long id);
}
