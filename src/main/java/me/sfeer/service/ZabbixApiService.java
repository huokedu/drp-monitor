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
import java.util.Date;

@Service
public class ZabbixApiService {
    @Resource
    private ZabbixApiMapper zabbixApiMapper;

    @Value("${zabbix.url}")
    private String url;

    @Value("${zabbix.username}")
    private String username;

    @Value("${zabbix.password}")
    private String password;

    private ZabbixApi zabbixApi;

    private static final Logger log = LoggerFactory.getLogger(ZabbixApiService.class);

    private void initZabbixApi() {
        zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();
        zabbixApi.login(username, password);
    }

    public Result createHost(Host host) {
        initZabbixApi();
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
        return new Result();
    }

    public JSONObject getHostData(String id) {
        JSONObject items = new JSONObject();
        for (JSONObject item : zabbixApiMapper.selectItemByHostId(id))
            items.put(item.getString("key"), item.getString("value"));
        return items;
    }

    public JSONArray getTemplates(String name) {
        initZabbixApi();
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("template.get")
                .paramEntry("output", new String[]{"templateid", "name"})
                .paramEntry("selectItems", "count")
                .paramEntry("selectHosts", new String[]{"hostid", "name", "status"})
                .paramEntry("sortfield", "name");

        if (name != null && !name.equals(""))
            req.paramEntry("search", JSONObject.parse("{\"name\":\"" + name + "\"}"));

        return zabbixApi.call(req.build()).getJSONArray("result");
    }

    public JSONArray getTemplateList() {
        initZabbixApi();
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("template.get")
                .paramEntry("output", new String[]{"templateid", "name"})
                .paramEntry("sortfield", "name");
        return zabbixApi.call(req.build()).getJSONArray("result");
    }

    public JSONArray getHosts(String name) {
        initZabbixApi();
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("host.get")
                .paramEntry("output", new String[]{"hostid", "name", "status"})
                .paramEntry("selectInterfaces", new String[]{"ip", "port"})
                .paramEntry("selectItems", "count")
                .paramEntry("selectParentTemplates", new String[]{"templateid", "name"})
                .paramEntry("sortfield", "name");

        if (name != null && !name.equals(""))
            req.paramEntry("search", JSONObject.parse("{\"name\":\"" + name + "\"}"));

        return zabbixApi.call(req.build()).getJSONArray("result");
    }

    public JSONArray getHistoryData(String id, Integer begin, Integer end) {
        initZabbixApi();
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("history.get")
                .paramEntry("itemids", id)
                .paramEntry("history", 0)
                //.paramEntry("limit", 1000)
                .paramEntry("sortorder", "DESC")
                .paramEntry("sortfield", "clock");

        if (begin != null)
            req.paramEntry("time_from", begin);

        if (end != null)
            req.paramEntry("time_till", end);

        return zabbixApi.call(req.build()).getJSONArray("result");
    }

    public JSONArray getHostGroupList() {
        initZabbixApi();
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("hostgroup.get")
                .paramEntry("output", new String[]{"groupid", "name"})
                .paramEntry("sortfield", "name");
        return zabbixApi.call(req.build()).getJSONArray("result");
    }
}
