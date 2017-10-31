package me.sfeer.controller;

import com.alibaba.fastjson.JSONArray;
import me.sfeer.service.RssService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/rss")
public class RssController {

    @Resource
    private RssService rssService;

    @GetMapping("/ctree")
    public JSONArray cateList() {
        return rssService.cateTree();
    }
}
