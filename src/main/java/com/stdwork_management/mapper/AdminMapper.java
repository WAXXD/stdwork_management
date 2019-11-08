package com.stdwork_management.mapper;

import com.stdwork_management.bean.AdminPO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
@Repository
public interface AdminMapper extends MySqlMapper<AdminPO>, Mapper<AdminPO> {
}
