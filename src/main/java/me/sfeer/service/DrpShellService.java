package me.sfeer.service;

import me.sfeer.domain.DrpShell;
import me.sfeer.mapper.DrpShellMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DrpShellService {

    @Resource
    private DrpShellMapper drpShellMapper;

    public List<DrpShell> findAllDrpShell() {
        return drpShellMapper.selectDrpShell();
    }
}
