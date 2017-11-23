package me.sfeer.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import me.sfeer.domain.Host;
import me.sfeer.domain.Result;
import me.sfeer.mapper.ZabbixApiMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ZabbixApiService {
    private static final Logger log = LoggerFactory.getLogger(ZabbixApiService.class);

    @Resource
    private ZabbixApiMapper zabbixApiMapper;

    @Value("${zabbix.url}")
    private String url;

    @Value("${zabbix.username}")
    private String username;

    @Value("${zabbix.password}")
    private String password;

    public Result createHost(Host host) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        JSONObject inter = new JSONObject();
        inter.put("type", host.getType());
        inter.put("main", 1);
        inter.put("useip", 1);
        inter.put("ip", host.getIp());
        inter.put("dns", "");
        inter.put("port", host.getPort());
        JSONArray interfaces = new JSONArray();
        interfaces.add(inter);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("host.create")
                .paramEntry("host", host.getHost())
                .paramEntry("name", host.getName())
                .paramEntry("interfaces", interfaces)
                .paramEntry("groups", JSONArray.parse("[{\"groupid\":\"" + host.getGroupId() + "\"}]"))
                .paramEntry("templates", JSONArray.parse("[{\"templateid\":\"" + host.getTemplateId() + "\"}]"));
        JSONObject res = zabbixApi.call(req.build());
        JSONObject error = res.getJSONObject("error");
        log.info("返回结果：{}", res.getJSONObject("error"));
        if (error != null) {
            return new Result(error.getString("code"), error.getString("data"));
        } else {
            host.setId(Long.parseLong(res.getJSONObject("result").getJSONArray("hostids").get(0).toString()));
            log.info("保存监控对象：{}", host.toString());
            zabbixApiMapper.insertRssRelation(host);
        }
        zabbixApi.destroy();
        return new Result();
    }

    public JSONArray getHostData(String id) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("item.get")
                .paramEntry("output", new String[]{"key_", "lastvalue", "value_type"})
                .paramEntry("hostids", id);
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getTemplates(String name) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("template.get")
                .paramEntry("output", new String[]{"templateid", "name"})
                .paramEntry("selectItems", "count")
                .paramEntry("selectHosts", new String[]{"hostid", "name", "status"})
                .paramEntry("sortfield", "name");
        if (name != null && !name.equals(""))
            req.paramEntry("search", JSONObject.parse("{\"name\":\"" + name + "\"}"));
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getTemplateList() {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("template.get")
                .paramEntry("output", new String[]{"templateid", "name"})
                .paramEntry("sortfield", "name");
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getHosts(String name) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("host.get")
                .paramEntry("output", new String[]{"hostid", "name", "status"})
                .paramEntry("selectInterfaces", new String[]{"ip", "port"})
                .paramEntry("selectItems", "count")
                .paramEntry("selectParentTemplates", new String[]{"templateid", "name"})
                .paramEntry("sortfield", "name");
        if (name != null && !name.equals(""))
            req.paramEntry("search", JSONObject.parse("{\"name\":\"" + name + "\"}"));
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getHistoryData(String ids, Integer type, Integer begin, Integer end) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("history.get")
                .paramEntry("itemids", ids.split(","))
                .paramEntry("history", type);
        if (begin != null)
            req.paramEntry("time_from", begin);
        if (end != null)
            req.paramEntry("time_till", end);
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getTrendData(String ids, Integer type, Integer begin, Integer end) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("trend.get")
                .paramEntry("itemids", ids.split(","))
                .paramEntry("trend", type);
        if (begin != null)
            req.paramEntry("time_from", begin);
        if (end != null)
            req.paramEntry("time_till", end);
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getHostGroupList() {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("hostgroup.get")
                .paramEntry("output", new String[]{"groupid", "name"})
                .paramEntry("sortfield", "name");
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getGraphList(String id) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("graph.get")
                .paramEntry("output", new String[]{"graphid", "name"})
                .paramEntry("hostids", id)
                .paramEntry("sortfield", "name");
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getItemsByGraph(String id) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("item.get")
                .paramEntry("output", new String[]{"itemid", "key_", "value_type", "units"})
                .paramEntry("graphids", id)
                .paramEntry("sortfield", "name");
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public JSONArray getHostList(String id) {
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("host.get")
                .paramEntry("output", new String[]{"hostid", "name"})
                .paramEntry("groupids", id)
                .paramEntry("sortfield", "name");
        JSONArray res = zabbixApi.call(req.build()).getJSONArray("result");
        zabbixApi.destroy();
        return res;
    }

    public Host getHostByRss(String uuid) {
        String hostId = zabbixApiMapper.getHostByRss(uuid);
        ZabbixApi zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("host.get")
                .paramEntry("selectGroups", new String[]{"groupid"})
                .paramEntry("selectInterfaces", new String[]{"ip", "port", "type"})
                .paramEntry("selectParentTemplates", new String[]{"groupid"})
                .paramEntry("output", new String[]{"hostid", "name", "host"})
                .paramEntry("hostids", hostId);
        JSONObject res = zabbixApi.call(req.build()).getJSONArray("result").getJSONObject(0);
        log.error("DADADA: {}", res);
        zabbixApi.destroy();
        Host host = new Host();
        host.setId(res.getLong("hostid"));
        host.setRssId(uuid);
        host.setHost(res.getString("host"));
        host.setName(res.getString("name"));
        host.setTemplateId(res.getJSONArray("parentTemplates").getJSONObject(0).getLong("templateid"));
        host.setGroupId(res.getJSONArray("groups").getJSONObject(0).getLong("groupid"));
        host.setIp(res.getJSONArray("interfaces").getJSONObject(0).getString("ip"));
        host.setPort(res.getJSONArray("interfaces").getJSONObject(0).getString("port"));
        host.setType(res.getJSONArray("interfaces").getJSONObject(0).getInteger("type"));
        return host;
    }
}
