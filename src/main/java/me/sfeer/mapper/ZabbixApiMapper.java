package me.sfeer.mapper;

import me.sfeer.domain.Host;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface ZabbixApiMapper {

    @Select("call zabbix.latest_data(#{hostid})")
    List<Map<String, Object>> selectItemByHostId(@Param("hostid") String hostid);

    @Insert("insert into drp_rm_monitor(rss_uuid,hostid) values (#{rssId},#{id})")
    void insertRssRelation(Host host);
}
