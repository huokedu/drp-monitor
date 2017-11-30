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

    public JSONObject selectDev() {
        JSONObject res = new JSONObject();
        int total = 0;
        for (JSONObject o : rssMapper.selectCabinetCapacity()) {
            total += o.getInteger("num");
            if ("01".equals(o.getString("usestatus")))
                res.put("cabinet.used", o.getInteger("num"));
        }
        res.put("cabinet.total", total);
        for (JSONObject o : rssMapper.selectUpsData()) {
            if ("一楼UPS1".equals(o.getString("group")) && "输出视在功率A相".equals(o.getString("name")))
                res.put("UPS1.A", o.getFloat("value"));
            else if ("一楼UPS1".equals(o.getString("group")) && "输出视在功率B相".equals(o.getString("name")))
                res.put("UPS1.B", o.getFloat("value"));
            else if ("一楼UPS1".equals(o.getString("group")) && "输出视在功率C相".equals(o.getString("name")))
                res.put("UPS1.C", o.getFloat("value"));
            else if ("一楼UPS2".equals(o.getString("group")) && "输出视在功率A相".equals(o.getString("name")))
                res.put("UPS2.A", o.getFloat("value"));
            else if ("一楼UPS2".equals(o.getString("group")) && "输出视在功率B相".equals(o.getString("name")))
                res.put("UPS2.B", o.getFloat("value"));
            else if ("一楼UPS2".equals(o.getString("group")) && "输出视在功率C相".equals(o.getString("name")))
                res.put("UPS2.C", o.getFloat("value"));
        }
        return res;
    }

    public JSONObject selectHost() {
        return null;
    }
}
