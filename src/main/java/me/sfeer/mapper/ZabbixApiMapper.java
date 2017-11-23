package me.sfeer.mapper;

import com.alibaba.fastjson.JSONArray;
import me.sfeer.domain.Host;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface ZabbixApiMapper {
    @Insert("insert into drp_rm_monitor(rss_uuid,hostid) values (#{rssId},#{id})")
    void insertRssRelation(Host host);

    @Select("select hostid from drp_rm_monitor where rss_uuid=#{uuid}")
    String getHostByRss(@Param("uuid") String uuid);
}
