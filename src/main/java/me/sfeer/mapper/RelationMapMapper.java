package me.sfeer.mapper;

import me.sfeer.domain.RelationMap;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RelationMapMapper {
    @Select("select * from drp_relation_map")
    List<RelationMap> findRelMap();

    @Insert("insert into drp_relation_map(name,`group`,nodes,links,areas) values (#{name},#{group},#{nodes},#{links},#{areas})")
    Long createRelMap(RelationMap map);

    @Update("update drp_relation_map set name=#{name},`group`=#{group},nodes=#{nodes},links=#{links},areas=#{areas} where id=#{id}")
    Long updateRelMap(RelationMap map);
}
