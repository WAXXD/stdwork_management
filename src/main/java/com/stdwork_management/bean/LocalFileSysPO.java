package com.stdwork_management.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@Table(name = "local_file_sys")
@Data
public class LocalFileSysPO {

    @Id
    private String id;

    private String pid;

    private String filename;

    private Byte level;

    private String path;

    private String parentPath;

    private Byte type;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
