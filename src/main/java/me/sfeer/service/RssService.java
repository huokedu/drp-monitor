package me.sfeer.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.sfeer.mapper.RssMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class RssService {

    @Resource
    private RssMapper rssMapper;

    private JSONArray loopCate(String parent) {
        JSONArray x = new JSONArray();
        for (Map<String, String> map : rssMapper.selectNodeCategory(parent)) {
            JSONObject o = JSON.parseObject("{\"id\":\"" + map.get("id") + "\",\"name\":\"" + map.get("name") + "\"}");
            JSONArray x1 = loopCate(map.get("id"));
            if (x1.size() > 0)
                o.put("children", x1);
            x.add(o);
        }
        return x;
    }

    public JSONArray cateTree() {
        return loopCate("-1");
    }
}
