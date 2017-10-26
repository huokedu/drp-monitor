package me.sfeer.mapper;

import me.sfeer.domain.Topology;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TopologyMapper {
    @Select("select * from drp_rm_topology")
    List<Topology> selectTopology();

    @Select("select * from drp_rm_topology where rss_uuid=#{uuid}")
    List<Topology> selectTopologyByUuid(@Param("uuid") String uuid);

    @Select("select * from drp_rm_topology where id=#{id}")
    Topology selectTopologyById(@Param("id") Long id);

    @Insert("insert into drp_relation_map(rss_uuid,name,`group`,nodes,links,areas) values (#{rss_uuid},#{name},#{group},#{nodes},#{links},#{areas})")
    Long insertTopology(Topology topo);

    @Update("update drp_relation_map set rss_uuid=#{rss_uuid},name=#{name},`group`=#{group},nodes=#{nodes},links=#{links},areas=#{areas} where id=#{id}")
    Long updateTopology(Topology topo);
}
