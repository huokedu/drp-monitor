package me.sfeer.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import jdk.nashorn.internal.parser.JSONParser;
import me.sfeer.domain.Host;
import me.sfeer.domain.Result;
import me.sfeer.mapper.ZabbixApiMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if(error != null) {
            return new Result(error.getString("code"), error.getString("data"));
        } else {
            host.setId(Long.parseLong(res.getJSONObject("result").getJSONArray("hostids").get(0).toString()));
            log.info("保存监控对象：{}", host.toString());
            zabbixApiMapper.insertRssRelation(host);
        }
        return new Result();
    }

    public Map<String, String> getHostData(String id) {
        Map<String, String> items = new HashMap<>();
        for (Map<String, Object> item : zabbixApiMapper.selectItemByHostId(id))
            items.put(item.get("key").toString(), item.get("value").toString());
        return items;
    }

    public JSONArray getTemplateList(String name) {
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

    public JSONArray getHostList(String name) {
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

    public JSONArray getHistoryData(String id, Date begin, Date end) {
        initZabbixApi();
        RequestBuilder req = RequestBuilder.newBuilder()
                .method("history.get")
                .paramEntry("itemids", id)
                .paramEntry("history", 0)
                .paramEntry("limit", 1000)
                .paramEntry("sortorder", "DESC")
                .paramEntry("sortfield", "clock");

        if (begin != null)
            req.paramEntry("time_from", begin.getTime() / 1000L);

        if (end != null)
            req.paramEntry("time_till", end.getTime() / 1000L);

        return zabbixApi.call(req.build()).getJSONArray("result");
    }
}
