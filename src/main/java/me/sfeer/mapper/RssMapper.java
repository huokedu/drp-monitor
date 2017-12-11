package me.sfeer.mapper;

import com.alibaba.fastjson.JSONObject;
import me.sfeer.domain.Host;
import org.apache.ibatis.annotations.Insert;
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
            "ORDER BY timestamp DESC")
    List<JSONObject> selectUpsData();

    // 温湿度
    @Select("SELECT\n" +
            "  data_name  AS name,\n" +
            "  data_value AS value\n" +
            "FROM drp_mt_loncom_data\n" +
            "WHERE data_type = '温湿度' AND (data_name LIKE '温度%' OR data_name LIKE '湿度%')\n" +
            "GROUP BY data_name, data_group\n" +
            "ORDER BY timestamp DESC")
    List<JSONObject> selectWSD();

    // 设备概况
    @Select("SELECT\n" +
            "  count(1) AS num,\n" +
            "  category_uuid AS type\n" +
            "FROM drp_rm_rss\n" +
            "WHERE category_uuid IN\n" +
            "      ('cate_device_host_minicomputer', 'cate_device_host_pc', 'cate_device_network_switch_net', 'cate_device_dr_rpa', 'cate_device_dr_falconstor', 'cate_device_network_router')\n" +
            "GROUP BY category_uuid\n" +
            "UNION ALL\n" +
            "SELECT\n" +
            "  count(1) AS num,\n" +
            "  a.parent_uuid AS type\n" +
            "FROM drp_rm_rss_category a, drp_rm_rss b\n" +
            "WHERE a.category_uuid = b.category_uuid AND a.parent_uuid IN ('cate_device_storage', 'cate_device_safety')\n" +
            "GROUP BY a.parent_uuid")
    List<JSONObject> overviewDevice();

    // 资源池概况
    @Select("SELECT\n" +
            "  count(1) AS num,\n" +
            "  category_uuid AS type\n" +
            "FROM drp_rm_rss\n" +
            "WHERE category_uuid IN\n" +
            "      ('cate_pool_compute', 'cate_pool_storage', 'cate_pool_net_ip')\n" +
            "GROUP BY category_uuid")
    List<JSONObject> overviewPool();

    // 资源使用情况
    @Select("SELECT\n" +
            "  sum(attr_value) AS sum,\n" +
            "  attr_uuid AS attr\n" +
            "FROM drp_rm_multi_rss_attr\n" +
            "WHERE attr_uuid IN\n" +
            "      ('attr_pool_computer_cpu_sum', 'attr_pool_computer_cpu_surplus',\n" +
            "       'attr_pool_computer_memory_sum', 'attr_pool_computer_surplus',\n" +
            "       'attr_pool_storage_store_capacity', 'attr_pool_storage_unuse_capacity',\n" +
            "       'attr_pool_net_ip_number', 'attr_pool_net_ip_unuse_ip')\n" +
            "GROUP BY attr_uuid")
    List<JSONObject> selectPoolUsedInfo();

    // 节点概况
    @Select("SELECT\n" +
            "  count(1) AS num,\n" +
            "  brand_uuid AS os,\n" +
            "  category_uuid AS cate\n" +
            "FROM drp_rm_rss\n" +
            "WHERE category_uuid IN ('cate_node_physics', 'cate_node_virtual')\n" +
            "GROUP BY brand_uuid, category_uuid")
    List<JSONObject> overviewNode();

    // 服务概况
    @Select("# 服务\n" +
            "SELECT\n" +
            "  count(1) AS num,\n" +
            "  category_uuid AS type\n" +
            "FROM drp_rm_rss\n" +
            "WHERE category_uuid IN\n" +
            "      ('cate_service_db_db2', 'cate_service_db_oracle', 'cate_service_db_mysql',\n" +
            "       'cate_service_midware_websphere', 'cate_service_midware_weblogic', 'cate_service_midware_tomcat')\n" +
            "GROUP BY category_uuid")
    List<JSONObject> overviewService();

    // 生产、辅助、测试应用的监控概览信息
    @Select("SELECT\n" +
            "  c.rss_uuid        AS node,\n" +
            "  a.rss_uuid        AS midware,\n" +
            "  d.attr_value      AS type,\n" +
            "  a.target_rss_uuid AS app\n" +
            "FROM (drp_rm_multi_rss_relate a, drp_rm_multi_rss_relate b,\n" +
            "  drp_rm_multi_rss_relate c, drp_rm_multi_rss_attr d)\n" +
            "WHERE a.rss_uuid = b.rss_uuid\n" +
            "      AND b.target_rss_uuid = c.target_rss_uuid\n" +
            "      AND a.target_rss_uuid = d.rss_uuid\n" +
            "      AND a.target_category_uuid = 'cate_application_country'\n" +
            "      AND a.category_uuid LIKE 'cate_service_midware_%'\n" +
            "      AND b.target_category_uuid = 'cate_service_midware_node'\n" +
            "      AND c.category_uuid LIKE 'cate_node_%'\n" +
            "      AND d.attr_uuid = 'attr_application_use'\n" +
            "      AND d.attr_value IN ('02', '03', '04', '05')\n" +
            "ORDER BY a.target_rss_uuid")
    List<JSONObject> appMonitorInfo();

    // 容灾复制、容灾转运行的监控概览信息
    @Select("SELECT\n" +
            "  a.rss_uuid AS app,\n" +
            "  a.attr_value AS type,\n" +
            "  b.attr_value AS `group`\n" +
            "FROM drp_rm_multi_rss_attr a, drp_rm_multi_rss_attr b\n" +
            "WHERE a.attr_uuid = 'attr_application_use'\n" +
            "      AND a.attr_value IN ('01', '02')\n" +
            "      AND a.rss_uuid = b.rss_uuid\n" +
            "      AND b.attr_uuid = 'attr_application_volume_group_name'")
    List<JSONObject> appRpaLinkInfo();

    // RPA复制情况
    @Select("SELECT\n" +
            "  data_group AS `group`,\n" +
            "  data_name AS name,\n" +
            "  data_value AS value\n" +
            "FROM drp_mt_loncom_data\n" +
            "WHERE data_name IN ('WAN traffic', 'Data Transfer') AND data_type = 'RPA'\n" +
            "GROUP BY data_group, data_name\n" +
            "ORDER BY timestamp DESC;")
    List<JSONObject> appRpaCopyInfo();


    // 新增资源和监控对象关联
    @Insert("insert into drp_rm_monitor(rss_uuid,hostid) values (#{rssId},#{id})")
    void insertRssRelation(Host host);

    // 查询资源对应监控对象
    @Select("select hostid from drp_rm_monitor where rss_uuid=#{uuid}")
    List<String> getHostByRss(@Param("uuid") String uuid);
}
