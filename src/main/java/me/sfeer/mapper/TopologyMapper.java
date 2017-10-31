package me.sfeer.mapper;

import me.sfeer.domain.Topology;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TopologyMapper {
    @Select("select id,name,`group` from drp_rm_topology")
    List<Topology> selectTopology();

    @Select("select id,name,`group` from drp_rm_topology where rss_uuid=#{uuid}")
    List<Topology> selectTopologyByRss(@Param("uuid") String uuid);

    @Select("select * from drp_rm_topology where id=#{id}")
    Topology selectTopologyById(@Param("id") Long id);

    @Insert("insert into drp_rm_topology(name,`group`,nodes,links,areas) values (#{name},#{group},#{nodes},#{links},#{areas})")
    void insertTopology(Topology topo);

    @Update("update drp_rm_topology set name=#{name},`group`=#{group},nodes=#{nodes},links=#{links},areas=#{areas} where id=#{id}")
    void updateTopology(Topology topo);
}
