package com.stdwork_management.mapper;

import com.stdwork_management.bean.StdAccountPO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.base.delete.DeleteMapper;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@Repository
public interface StdAccountMapper extends DeleteMapper<StdAccountPO>, InsertListMapper<StdAccountPO>, Mapper<StdAccountPO> {
}
