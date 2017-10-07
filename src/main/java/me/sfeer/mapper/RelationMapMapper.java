package me.sfeer.mapper;

import me.sfeer.domain.RelationMap;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RelationMapMapper {
    @Select("select * from drp_relation_map")
    List<RelationMap> findRelMap();

    @Select("select * from drp_relation_map where id=#{id}")
    RelationMap findRelMapById(@Param("id") Long id);

    @Insert("insert into drp_relation_map(name,`group`,nodes,links,areas) values (#{name},#{group},#{nodes},#{links},#{areas})")
    Long createRelMap(RelationMap map);

    @Update("update drp_relation_map set name=#{name},`group`=#{group},nodes=#{nodes},links=#{links},areas=#{areas} where id=#{id}")
    Long updateRelMap(RelationMap map);
}
