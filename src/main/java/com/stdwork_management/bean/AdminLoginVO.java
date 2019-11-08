package com.stdwork_management.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
@ApiModel(description = "后台管理员登录")
@Data
public class AdminLoginVO {

    @ApiModelProperty(value = "管理员名称")
    @NotNull(message = "管理员名称不能为空")
    private String adminName;

    @ApiModelProperty(value = "管理员密码")
    @NotNull(message = "管理员密码不能为空")
    private String password;
}
