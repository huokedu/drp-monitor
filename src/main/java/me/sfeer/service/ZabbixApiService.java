package me.sfeer.service;

import me.sfeer.mapper.ZabbixApiMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZabbixApiService {
    @Resource
    private ZabbixApiMapper zabbixApiMapper;

    private static final Logger log = LoggerFactory.getLogger(ZabbixApiService.class);

    public Map<String, String> getHostData(String id) {
        Map<String, String> items = new HashMap<>();
        for (Map<String, Object> item : zabbixApiMapper.selectItemByHostId(id))
            items.put(item.get("key").toString(), item.get("value").toString());
        return items;
    }
}
