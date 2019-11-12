package com.stdwork_management.service.impl;

import com.stdwork_management.bean.StdAccountPO;
import com.stdwork_management.bean.StdUserChangePWDVO;
import com.stdwork_management.bean.StdUserVO;
import com.stdwork_management.exception.UserDefinedException;
import com.stdwork_management.mapper.StdAccountMapper;
import com.stdwork_management.service.AppService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private StdAccountMapper stdAccountMapper;

    @Override
    public List<StdAccountPO> login(StdUserVO stdUserVO) {
        StdAccountPO stdAccountPO = new StdAccountPO();
        BeanUtils.copyProperties(stdUserVO, stdAccountPO);
//        stdAccountPO.setGraduated((byte) 0);
        return stdAccountMapper.select(stdAccountPO);
    }

    @Override
    public void changePwd(StdUserChangePWDVO stdUserChangePWDVO) {
        StdAccountPO stdAccountPO = new StdAccountPO();
        stdAccountPO.setStdNo(stdUserChangePWDVO.getStdNo());
        StdAccountPO update = stdAccountMapper.selectOne(stdAccountPO);
        if(update == null){
            throw new UserDefinedException(9999, "用户不存在或者密码错误");
        }
        update.setUpdateTime(new Date());
        update.setPassword(stdUserChangePWDVO.getPassword());
        if(stdAccountMapper.updateByPrimaryKeySelective(update) == 0) {
            throw new UserDefinedException(9999, "系统错误");
        }
    }
}
