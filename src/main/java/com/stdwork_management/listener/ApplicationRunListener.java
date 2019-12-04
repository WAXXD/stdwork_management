package com.stdwork_management.listener;

import com.stdwork_management.mapper.LocalFileSysMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@Component
@Slf4j
public class ApplicationRunListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private LocalFileSysMapper localFileSysMapper;

    @Autowired
    private String workPath;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
//            log.info("查看初始化数据是否存在");
//            LocalFileSysPO localFileSysPO = new LocalFileSysPO();
//            localFileSysPO.setId("1");
//            localFileSysPO.setPath(workPath);
//            List<LocalFileSysPO> localFileSysPOList = localFileSysMapper.select(localFileSysPO);
//            if (localFileSysPOList.size() == 0){
//                log.info("不存在，初始化数据库表-> [ {} ]", localFileSysPO);
//                localFileSysMapper.insert(localFileSysPO);
//                log.info("初始化成功");
//            } else {
//                log.info("存在不需要初始化[ {} ] -> [ {} ]",localFileSysPOList.size(), localFileSysPOList.get(0));
//            }
//        }
    }
}
