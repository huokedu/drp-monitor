package me.sfeer.mapper;

import me.sfeer.domain.Topology;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TopologyMapper {
    @Select("select * from drp_rm_topology")
    List<Topology> findRelMap();

    @Select("select * from drp_rm_topology where id=#{id}")
    Topology findRelMapById(@Param("id") Long id);

    @Insert("insert into drp_relation_map(name,`group`,nodes,links,areas) values (#{name},#{group},#{nodes},#{links},#{areas})")
    Long createRelMap(Topology topo);

    @Update("update drp_relation_map set name=#{name},`group`=#{group},nodes=#{nodes},links=#{links},areas=#{areas} where id=#{id}")
    Long updateRelMap(Topology topo);
}
