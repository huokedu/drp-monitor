package me.sfeer.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.sfeer.mapper.RssMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RssService {

    @Resource
    private RssMapper rssMapper;

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
}
