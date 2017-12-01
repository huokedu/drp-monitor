package me.sfeer.service;

import me.sfeer.domain.Task;
import me.sfeer.mapper.TaskMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TaskService {

    @Resource
    private TaskMapper taskMapper;

    public List<Task> findAllDrpShell() {
        return taskMapper.selectDrpShell();
    }
}
