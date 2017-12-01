package me.sfeer.mapper;

import me.sfeer.domain.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TaskMapper {
    @Select("SELECT id,name,content,cron FROM drp_scheduler WHERE status='1'")
    List<Task> selectDrpShell();
}
