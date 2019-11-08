package com.stdwork_management.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@Data
@Accessors(chain = true)
public class LocalFileSysDTO {

    private String id;

    private String pid;

    private String filename;

    private String path;

    private Byte type;

    private List<LocalFileSysDTO> localFileSysDTOList;

}
