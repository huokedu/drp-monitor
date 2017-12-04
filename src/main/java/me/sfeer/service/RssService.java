package me.sfeer.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.sfeer.mapper.RssMapper;
import me.sfeer.mapper.ZabbixApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RssService {
    private static final Logger log = LoggerFactory.getLogger(RssService.class);

    @Resource
    private RssMapper rssMapper;

    @Resource
    private ZabbixApi zabbixApi;

    private JSONArray loopCate(String parent) {
        JSONArray x = new JSONArray();
        for (JSONObject o : rssMapper.selectNodeCategory(parent)) {
            JSONArray x1 = loopCate(o.getString("id"));
            if (x1.size() > 0)
                o.put("children", x1);
            x.add(o);
        }
        return x;
    }

    public JSONArray cateTree() {
        return loopCate("-1");
    }

    // 基础设施概览信息
    public JSONObject overviewFacility() {
        JSONObject res = new JSONObject();

        // 机柜容量
        int total = 0;
        for (JSONObject o : rssMapper.selectCabinetCapacity()) {
            total += o.getInteger("num");
            if ("01".equals(o.getString("usestatus")))
                res.put("cabinet.used", o.getInteger("num"));
        }
        res.put("cabinet.total", total);

        // UPS实时功率
        for (JSONObject o : rssMapper.selectUpsData()) {
            if ("一楼UPS1".equals(o.getString("group")) && "输出视在功率A相".equals(o.getString("name")))
                res.put("UPS1.A", o.getString("value"));
            else if ("一楼UPS1".equals(o.getString("group")) && "输出视在功率B相".equals(o.getString("name")))
                res.put("UPS1.B", o.getString("value"));
            else if ("一楼UPS1".equals(o.getString("group")) && "输出视在功率C相".equals(o.getString("name")))
                res.put("UPS1.C", o.getString("value"));
            else if ("一楼UPS2".equals(o.getString("group")) && "输出视在功率A相".equals(o.getString("name")))
                res.put("UPS2.A", o.getString("value"));
            else if ("一楼UPS2".equals(o.getString("group")) && "输出视在功率B相".equals(o.getString("name")))
                res.put("UPS2.B", o.getString("value"));
            else if ("一楼UPS2".equals(o.getString("group")) && "输出视在功率C相".equals(o.getString("name")))
                res.put("UPS2.C", o.getString("value"));
        }

        // 温湿度
        float wd = 0.0f, sd = 0.0f;
        int num1 = 0, num2 = 0;
        for (JSONObject o : rssMapper.selectWSD()) {
            if (o.getString("name").startsWith("温度")) {
                wd += o.getFloat("value");
                num1++;
            } else if (o.getString("name").startsWith("湿度")) {
                sd += o.getFloat("value");
                num2++;
            }
        }
        res.put("pjwd", String.format("%.2f", wd / num1));
        res.put("pjsd", String.format("%.2f", sd / num2));

        // 链路信息
        // TODO 国家局链路传输速率，行业单位链路数量
        return res;
    }

    // 设备概览信息
    public JSONObject overviewDevice() {
        JSONObject res = new JSONObject();
        res.put("cdp", 0);
        for (JSONObject o : rssMapper.overviewDevice()) {
            if ("cate_device_host_minicomputer".equals(o.getString("type")))
                res.put("minicomputer", o.getIntValue("num"));
            else if ("cate_device_host_pc".equals(o.getString("type")))
                res.put("pc", o.getIntValue("num"));
            else if ("cate_device_network_switch_net".equals(o.getString("type")))
                res.put("switch", o.getIntValue("num"));
            else if ("cate_device_network_router".equals(o.getString("type")))
                res.put("router", o.getIntValue("num"));
            else if ("cate_device_safety".equals(o.getString("type")))
                res.put("safety", o.getIntValue("num"));
            else if ("cate_device_storage".equals(o.getString("type")))
                res.put("storage", o.getIntValue("num"));
            else if ("cate_device_dr_rpa".equals(o.getString("type")))
                res.put("rpa", o.getIntValue("num"));
            else if ("cate_device_dr_falconstor".equals(o.getString("type")))
                res.put("cdp", o.getIntValue("num"));
        }
        return res;
    }

    // 资源池概览信息
    public JSONObject overviewPool() {
        JSONObject res = new JSONObject();
        for (JSONObject o : rssMapper.overviewPool()) {
            if ("cate_pool_compute".equals(o.getString("type")))
                res.put("compute.num", o.getIntValue("num"));
            else if ("cate_pool_storage".equals(o.getString("type")))
                res.put("storage.num", o.getIntValue("num"));
            else if ("cate_pool_net_ip".equals(o.getString("type")))
                res.put("ip.num", o.getIntValue("num"));
        }

        // 资源池使用情况
        for (JSONObject o : rssMapper.selectPoolUsedInfo()) {
            if ("attr_pool_computer_cpu_sum".equals(o.getString("attr")))
                res.put("cpu.total", o.getIntValue("sum"));
            else if ("attr_pool_computer_cpu_surplus".equals(o.getString("attr")))
                res.put("cpu.free", o.getIntValue("sum"));
            else if ("attr_pool_computer_memory_sum".equals(o.getString("attr")))
                res.put("memory.total", o.getIntValue("sum"));
            else if ("attr_pool_computer_surplus".equals(o.getString("attr")))
                res.put("memory.free", o.getIntValue("sum"));
            else if ("attr_pool_net_ip_number".equals(o.getString("attr")))
                res.put("ip.total", o.getIntValue("sum"));
            else if ("attr_pool_net_ip_unuse_ip".equals(o.getString("attr")))
                res.put("ip.free", o.getIntValue("sum"));
            else if ("attr_pool_storage_store_capacity".equals(o.getString("attr")))
                res.put("store.total", o.getIntValue("sum"));
            else if ("attr_pool_storage_unuse_capacity".equals(o.getString("attr")))
                res.put("store.free", o.getIntValue("sum"));
        }
        return res;
    }

    // 节点概览
    public JSONObject overviewNode() {
        JSONObject res = new JSONObject();
        int linux1 = 0, linux2 = 0, other1 = 0, other2 = 0;
        for (JSONObject o : rssMapper.overviewNode()) {
            if ("cate_node_physics".equals(o.getString("cate"))) {
                if (o.getString("os").endsWith("aix"))
                    res.put("physics.aix", o.getIntValue("num"));
                else if (o.getString("os").endsWith("windows"))
                    res.put("physics.windows", o.getIntValue("num"));
                else if (o.getString("os").endsWith("centos") || o.getString("os").endsWith("redhat"))
                    linux1 += o.getIntValue("num");
                else
                    other1 += o.getIntValue("num");
            } else if ("cate_node_virtual".equals(o.getString("cate"))) {
                if (o.getString("os").endsWith("aix"))
                    res.put("virtual.aix", o.getIntValue("num"));
                else if (o.getString("os").endsWith("windows"))
                    res.put("virtual.windows", o.getIntValue("num"));
                else if (o.getString("os").endsWith("centos") || o.getString("os").endsWith("redhat"))
                    linux2 += o.getIntValue("num");
                else
                    other2 += o.getIntValue("num");
            }
        }
        res.put("physics.linux", linux1);
        res.put("physics.other", other1);
        res.put("virtual.linux", linux2);
        res.put("virtual.other", other2);
        return res;
    }

    // 服务概览
    public JSONObject overviewService() {
        JSONObject res = new JSONObject();
        res.put("db2", 0);
        res.put("oracle", 0);
        res.put("mysql", 0);
        res.put("websphere", 0);
        res.put("weblogic", 0);
        res.put("tomcat", 0);
        for (JSONObject o : rssMapper.overviewService()) {
            if ("cate_service_db_db2".equals(o.getString("type")))
                res.put("db2", o.getIntValue("num"));
            else if ("cate_service_db_oracle".equals(o.getString("type")))
                res.put("oracle", o.getIntValue("num"));
            else if ("cate_service_db_mysql".equals(o.getString("type")))
                res.put("mysql", o.getIntValue("num"));
            else if ("cate_service_midware_websphere".equals(o.getString("type")))
                res.put("websphere", o.getIntValue("num"));
            else if ("cate_service_midware_weblogic".equals(o.getString("type")))
                res.put("weblogic", o.getIntValue("num"));
            else if ("cate_service_midware_tomcat".equals(o.getString("type")))
                res.put("tomcat", o.getIntValue("num"));
        }
        return res;
    }

    // 应用概览
    public JSONArray overviewApp() {
        JSONArray res = new JSONArray();

        // 应用链路监控概览
        res.addAll(rssMapper.appRpaLinkInfo());

        // 应用性能监控概览
        Set<String> hostIds = new HashSet<>();
        Map<String, String> appHost = new HashMap<>();
        Map<String, JSONObject> appInfo = new HashMap<>();
        for (JSONObject o : rssMapper.appMonitorInfo()) {
            String app = o.getString("app"); // 应用uuid
            String host = o.getString("host"); // 监控主机id
            String type = o.getString("type"); // 应用类型
            if (host != null) {
                if (appHost.containsKey(app)) {
                    appHost.put(app, appHost.get(app) + ',' + host);
                } else {
                    appHost.put(app, host);
                }
                hostIds.add(host);
            }
            appInfo.put(app, JSONObject.parseObject("{\"type\":\"" + type + "\",\"app\":\"" + app + "\"}"));
        }

        // 根据hostid查询cpu/memory
        Map<String, JSONObject> hostInfo = new HashMap<>();
        JSONArray items = zabbixApi.getHostsPerformance(hostIds.toArray(new String[hostIds.size()]));
        for (int i = 0; i < items.size(); i++) {
            JSONObject o = items.getJSONObject(i);
            String key = o.getString("key_");
            String hostid = o.getString("hostid");
            String lastvalue = o.getString("lastvalue");
            JSONObject info = hostInfo.containsKey(hostid) ? hostInfo.get(hostid) : new JSONObject();
            if ("vm.memory.size[available]".equals(key))
                info.put("free.memory", lastvalue);
            else if ("vm.memory.size[total]".equals(key))
                info.put("total.memory", lastvalue);
            else if ("system.cpu.util[,idle]".equals(key))
                info.put("cpu", lastvalue);
            else if ("system.stat[cpu,id]".equals(key))
                info.put("cpu", lastvalue);
            hostInfo.put(hostid, info);
        }

        for (String app : appInfo.keySet()) {
            JSONObject info = appInfo.get(app);
            // 应用节点对应的监控主机
            if (appHost.containsKey(app)) {
                String[] arr = appHost.get(app).split(",");
                if (arr.length > 1) {
                    // 多节点, 取平均值
                    float a = 0.0f, b = 0.0f;
                    for (int i = 0; i < arr.length; i++) {
                        JSONObject hh = hostInfo.get(arr[i]);
                        a += hh.getFloatValue("cpu");
                        b += hh.getFloatValue("free.memory") / hh.getFloatValue("total.memory") * 100.0;
                    }
                    info.put("cpu", String.format("%.2f", 100 - a / arr.length));
                    info.put("memory", String.format("%.2f", 100 - b / arr.length));
                } else {
                    // 单节点
                    JSONObject hh = hostInfo.get(arr[0]);
                    float a = hh.getFloatValue("cpu");
                    float b = hh.getFloatValue("free.memory");
                    float c = hh.getFloatValue("total.memory");
                    info.put("cpu", String.format("%.2f", 100 - a));
                    info.put("memory", String.format("%.2f", 100 - b / c * 100.0));
                }
                info.put("status", "01");
            } else {
                // 应用未监控
                info.put("status", "03");
            }
            res.add(info);
        }

        return res;
    }
}
