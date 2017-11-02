package me.sfeer.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface RssMapper {

    @Select("select category_uuid id,category_name name from drp_rm_rss_category where canbe_view='01' and parent_uuid=#{uuid} order by category_type desc")
    List<JSONObject> selectNodeCategory(@Param("uuid") String uuid);
}
