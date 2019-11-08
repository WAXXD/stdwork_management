package com.stdwork_management.service;

import com.stdwork_management.bean.StdAccountPO;
import com.stdwork_management.bean.StdUserChangePWDVO;
import com.stdwork_management.bean.StdUserVO;

import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
public interface AppService {

    List<StdAccountPO> login(StdUserVO stdUserVO);

    void changePwd(StdUserChangePWDVO stdUserChangePWDVO);
}
