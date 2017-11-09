package me.sfeer.mapper;

import me.sfeer.domain.Host;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface ZabbixApiMapper {
    @Insert("insert into drp_rm_monitor(rss_uuid,hostid) values (#{rssId},#{id})")
    void insertRssRelation(Host host);
}
