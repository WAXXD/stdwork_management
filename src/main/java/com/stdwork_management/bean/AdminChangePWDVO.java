package com.stdwork_management.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
@Data
@ApiModel(value = "管理员修改密码", description = "管理员修改密码")
@EqualsAndHashCode(callSuper = false)
public class AdminChangePWDVO {

    @ApiModelProperty(value = "管理员账号")
    @NotNull(message = "管理员不能为空")
    private String adminName;

    @ApiModelProperty(value = "旧密码")
    @NotNull(message = "旧密码不能为空")
    private String ordPassword;

    @ApiModelProperty(value = "新密码")
    @Length(min = 6, max = 16, message = "密码必需在6-16位之间")
    private String password;

    @ApiModelProperty(value = "再次输入新密码")
    @Length(min = 6, max = 16, message = "密码必需在6-16位之间")
    private String r_password;


}
