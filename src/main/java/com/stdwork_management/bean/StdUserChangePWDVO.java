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
@ApiModel(description = "用户修改密码")
@EqualsAndHashCode(callSuper = false)
public class StdUserChangePWDVO {

    @ApiModelProperty(value = "学生学号")
    @NotNull(message = "学号不能为空")
    private String stdNo;

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
