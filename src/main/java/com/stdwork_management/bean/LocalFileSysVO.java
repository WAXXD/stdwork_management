package com.stdwork_management.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@Data
@Accessors(chain = true)
public class LocalFileSysVO {

    private String filename;

    private String path;

    private Byte type;

    private Byte level;

}
