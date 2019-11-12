package com.stdwork_management.mapper;

import com.stdwork_management.bean.LocalFileSysPO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@Repository
public interface LocalFileSysMapper extends ConditionMapper<LocalFileSysPO>,InsertListMapper<LocalFileSysPO>, Mapper<LocalFileSysPO> {

    List<LocalFileSysPO> fullIndexSearch(String searchKey);
}
