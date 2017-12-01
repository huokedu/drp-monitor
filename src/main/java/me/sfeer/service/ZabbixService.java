package me.sfeer.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.sfeer.mapper.ZabbixApi;
import me.sfeer.domain.Host;
import me.sfeer.domain.Result;
import me.sfeer.mapper.RssMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ZabbixService {
    private static final Logger log = LoggerFactory.getLogger(ZabbixService.class);

    @Resource
    private RssMapper rssMapper;

    @Resource
    private ZabbixApi zabbixApi;

    @Value("${zabbix.url}")
    private String url;

    @Value("${zabbix.username}")
    private String username;

    @Value("${zabbix.password}")
    private String password;

    public Result createHost(Host host) {
        JSONObject res = zabbixApi.createHost(host);
        JSONObject error = res.getJSONObject("error");
        log.info("返回结果：{}", res.getJSONObject("error"));
        if (error != null) {
            return new Result(error.getString("code"), error.getString("data"));
        } else {
            host.setId(Long.parseLong(res.getJSONObject("result").getJSONArray("hostids").get(0).toString()));
            log.info("保存监控对象：{}", host.toString());
            rssMapper.insertRssRelation(host);
        }
        return new Result();
    }

    public JSONArray getHostData(String id) {
        return zabbixApi.getHostData(id);
    }

    public JSONArray getTemplates(String name) {
        return zabbixApi.getTemplates(name);
    }

    public JSONArray getTemplateList() {
        return zabbixApi.getTemplateList();
    }

    public JSONArray getHosts(String name) {
        return zabbixApi.getHosts(name);
    }

    public JSONArray getHistoryData(String ids, Integer type, Integer begin, Integer end) {
        return zabbixApi.getHistoryData(ids, type, begin, end);
    }

    public JSONArray getTrendData(String ids, Integer type, Integer begin, Integer end) {
        return zabbixApi.getTrendData(ids, type, begin, end);
    }

    public JSONArray getHostGroupList() {
        return zabbixApi.getHostGroupList();
    }

    public JSONArray getGraphList(String id) {
        return zabbixApi.getGraphList(id);
    }

    public JSONArray getItemsByGraph(String id) {
        return zabbixApi.getItemsByGraph(id);
    }

    public JSONArray getHostList(String id) {
        return zabbixApi.getHostList(id);
    }

    public Host getHostByRss(String uuid) {
        Host host = zabbixApi.getHostByRss(rssMapper.getHostByRss(uuid));
        host.setRssId(uuid);
        return host;
    }
}
