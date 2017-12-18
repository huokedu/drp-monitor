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
        // TODO 国家局链路传输速率，行业单位链路数量（网络链路速率从主交换机端口获取监控数据）
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
        res.put("physics.aix", 0);
        res.put("physics.windows", 0);
        res.put("virtual.aix", 0);
        res.put("virtual.windows", 0);
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

        // RPA链路复制情况
        Map<String, JSONObject> copy = new HashMap<>();
        for (JSONObject o : rssMapper.appRpaCopyInfo()) {
            String group = o.getString("group");
            String name = o.getString("name");
            String value = o.getString("value");
            JSONObject x = copy.containsKey(group) ? copy.get(group) : new JSONObject();
            x.put(name, value);
            copy.put(group, x);
        }

        // 应用链路监控概览
        Map<String, JSONObject> appInfo = new HashMap<>();
        for (JSONObject o : rssMapper.appRpaLinkInfo()) {
            String[] groups = o.getString("group").split(",");
            o.put("active", "03"); // 初始化03
            boolean active = true;
            int speed = 0;
            for (String s : groups) {
                if (copy.containsKey(s)) {
                    JSONObject x = copy.get(s);
                    int sp = Integer.parseInt(x.getString("WAN traffic").split(" ")[0]);
                    boolean ac = "ACTIVE".equals(x.getString("Data Transfer"));
                    speed += sp;
                    o.put("speed", speed);
                    active = active && ac;
                    o.put("active", active ? "01" : "02");
                }
            }
            appInfo.put(o.getString("app"), o);
        }

        // 应用运行状况监控概览
        Set<String> mHost = new HashSet<>(); // 中间件监控主机
        Map<String, String> appMHost = new HashMap<>();
        Set<String> nHost = new HashSet<>(); // 中间件所属节点监控主机
        Map<String, String> appNHost = new HashMap<>();
        for (JSONObject o : rssMapper.appMonitorInfo()) {
            String app = o.getString("app"); // 应用uuid
            String host = o.getString("host"); // 监控主机id
            String name = o.getString("name");
            String type = o.getString("type"); // 应用类型
            String flag = o.getString("flag");
            if (host != null) {
                if ("midware".equals(flag)) {
                    if (appMHost.containsKey(app)) {
                        appMHost.put(app, appMHost.get(app) + ',' + host);
                    } else {
                        appMHost.put(app, host);
                    }
                    mHost.add(host);
                } else if ("node".equals(flag)) {
                    if (appNHost.containsKey(app)) {
                        appNHost.put(app, appNHost.get(app) + ',' + host);
                    } else {
                        appNHost.put(app, host);
                    }
                    nHost.add(host);
                }
            }
            if (!appInfo.containsKey(app)) {
                JSONObject x = new JSONObject();
                x.put("app", app);
                x.put("name", name);
                x.put("type", type);
                x.put("status", "03");
                appInfo.put(app, x);
            }
        }

        // 根据hostid查询运行状态
        Map<String, JSONObject> hostInfo = new HashMap<>();
        JSONArray items = zabbixApi.getHostsMidwareStatus(mHost.toArray(new String[mHost.size()]));
        for (int i = 0; i < items.size(); i++) {
            JSONObject o = items.getJSONObject(i);
            String key = o.getString("key_");
            String hostid = o.getString("hostid");
            String lastvalue = o.getString("lastvalue");
            JSONObject info = hostInfo.containsKey(hostid) ? hostInfo.get(hostid) : new JSONObject();
            if ("net.tcp.service[http,{$WAS_HOST},{$WAS_PORT}]".equals(key))
                info.put("status", lastvalue);
            hostInfo.put(hostid, info);
        }

        // 根据hostid查询cpu/memory
        items = zabbixApi.getHostsPerformance(nHost.toArray(new String[nHost.size()]));
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

            // 应用对应的中间件监控主机
            if (appMHost.containsKey(app)) {
                String[] arr = appMHost.get(app).split(",");
                if (arr.length > 1) {
                    // 多节点
                    boolean ok = true;
                    for (String s : arr) {
                        if (hostInfo.containsKey(s)) {
                            JSONObject hh = hostInfo.get(s);
                            ok = ok & hh.getIntValue("status") != 0;
                        }
                    }
                    info.put("status", ok ? "01" : "02");
                } else if (arr.length == 1) {
                    // 单节点
                    if (hostInfo.containsKey(arr[0])) {
                        JSONObject hh = hostInfo.get(arr[0]);
                        info.put("status", hh.getIntValue("status") == 0 ? "02" : "01");
                    }
                }
            }

            // 应用对应的中间件所属节点监控主机
            if (appNHost.containsKey(app)) {
                String[] arr = appNHost.get(app).split(",");
                if (arr.length > 1) {
                    // 多节点, 取平均值
                    float a = 0, b = 0, c = 0, d = 0;
                    for (String s : arr) {
                        if (hostInfo.containsKey(s)) {
                            JSONObject hh = hostInfo.get(s);
                            if (hh.getFloatValue("cpu") > 0) {
                                a += hh.getFloatValue("cpu");
                                b += 100;
                            }
                            if (hh.getFloatValue("total.memory") > 0) {
                                c += hh.getFloatValue("free.memory");
                                d += hh.getFloatValue("total.memory");
                            }
                        }
                    }
                    info.put("cpu", a > 0 ? (float) (Math.round((100 - a * 100 / b) * 100)) / 100 : 0.00f);
                    info.put("memory", c > 0 ? (float) (Math.round((100 - c * 100 / d) * 100)) / 100 : 0.00f);
                } else if (arr.length == 1) {
                    // 单节点
                    if (hostInfo.containsKey(arr[0])) {
                        JSONObject hh = hostInfo.get(arr[0]);
                        float a = hh.getFloatValue("cpu");
                        float c = hh.getFloatValue("free.memory");
                        float d = hh.getFloatValue("total.memory");
                        info.put("cpu", a > 0 ? (float) (Math.round((100 - a) * 100)) / 100 : 0.00f);
                        info.put("memory", c > 0 ? (float) (Math.round((100 - c * 100 / d) * 100)) / 100 : 0.00f);
                    }
                }
            }

            res.add(info);
        }

        return res;
    }

    public List<JSONObject> getNodesByPool(String uuid) {
        return rssMapper.getNodesByPool(uuid);
    }

    public List<JSONObject> getHostIdsByDb(String uuid) {
        return rssMapper.getHostIdsByDb(uuid);
    }

    public List<JSONObject> getHostIdsByMid(String uuid) {
        return rssMapper.getHostIdsByMid(uuid);
    }
}
