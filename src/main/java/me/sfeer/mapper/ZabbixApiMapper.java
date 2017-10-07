package me.sfeer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface ZabbixApiMapper {

    @Select("call latest_data(#{hostid})")
    List<Map<String, Object>> selectItemByHostId(@Param("hostid") String hostid);
}
