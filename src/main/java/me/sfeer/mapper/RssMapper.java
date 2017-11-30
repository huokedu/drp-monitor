package me.sfeer.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface RssMapper {

    @Select("select category_uuid id,category_name name from drp_rm_rss_category where canbe_view='01' and parent_uuid=#{uuid} order by category_type desc")
    List<JSONObject> selectNodeCategory(@Param("uuid") String uuid);

    // 查询机柜容量信息
    @Select("SELECT count(1) as num,c.usestatus " +
            "FROM drp_rm_multi_rss_relate a, drp_rm_rss c " +
            "WHERE a.rss_uuid in(" +
            "SELECT rss_uuid " +
            "FROM drp_rm_rss where category_uuid='cate_basic_cabinet')" +
            "AND a.target_category_uuid='cate_basic_cabinet_space' " +
            "AND a.target_rss_uuid = c.rss_uuid " +
            "GROUP BY c.usestatus")
    List<JSONObject> selectCabinetCapacity();

    // UPS实时功率
    @Select("SELECT data_name as name," +
            "data_value as value," +
            "data_group as `group` " +
            "FROM drp_mt_loncom_data " +
            "WHERE data_type='UPS' AND data_name like '输出视在功率%相' " +
            "GROUP BY data_name,data_group " +
            "ORDER BY timestamp DESC")
    List<JSONObject> selectUpsData();
}
