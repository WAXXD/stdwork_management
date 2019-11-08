package com.stdwork_management.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
@Data
@ApiModel(description = "后端管理学生用户")
@EqualsAndHashCode(callSuper = false)
public class StdUserBackendManageCURDVO {

    @ApiModelProperty("学生姓名")
    private String name;

    @ApiModelProperty("学号")
    private String stdNo;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("毕业时间")
    private Date graduationTime;

    @ApiModelProperty("是否毕业， 1已毕业， 0未毕业")
    private Byte graduated;

}
