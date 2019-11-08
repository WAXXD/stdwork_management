package com.stdwork_management.bean;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
@Data
@Table(name = "admin_user")
public class AdminPO {

    @Id
    private String id;

    private String adminName;

    private String password;

    private Date createTime;

    private Date updateTime;
}
