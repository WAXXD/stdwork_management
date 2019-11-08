package com.stdwork_management.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-28
 **/
@Data
@Accessors(chain = true)
public class DirVO {
    private String dirName;
    private Integer id;
}
