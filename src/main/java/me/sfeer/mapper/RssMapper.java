package me.sfeer.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RssMapper {

    @Select("select category_uuid id,category_name name from drp_rm_rss_category where canbe_view='01' and parent_uuid=#{uuid} order by category_type desc")
    List<JSONObject> selectNodeCategory(@Param("uuid") String uuid);

    // 查询机柜容量信息
    @Select("SELECT\n" +
            "  count(1) AS num,\n" +
            "  c.usestatus\n" +
            "FROM drp_rm_multi_rss_relate a, drp_rm_rss c\n" +
            "WHERE a.rss_uuid IN (\n" +
            "  SELECT rss_uuid\n" +
            "  FROM drp_rm_rss\n" +
            "  WHERE category_uuid = 'cate_basic_cabinet')\n" +
            "      AND a.target_category_uuid = 'cate_basic_cabinet_space'\n" +
            "      AND a.target_rss_uuid = c.rss_uuid\n" +
            "GROUP BY c.usestatus")
    List<JSONObject> selectCabinetCapacity();

    // UPS实时功率
    @Select("SELECT\n" +
            "  data_name  AS name,\n" +
            "  data_value AS value,\n" +
            "  data_group AS `group`\n" +
            "FROM drp_mt_loncom_data\n" +
            "WHERE data_type = 'UPS' AND data_name LIKE '输出视在功率%相'\n" +
            "GROUP BY data_name, data_group\n" +
            "ORDER BY timestamp DESC;")
    List<JSONObject> selectUpsData();
}
