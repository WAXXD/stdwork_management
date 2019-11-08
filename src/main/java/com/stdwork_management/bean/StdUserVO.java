package com.stdwork_management.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
@Data
@ApiModel(description = "用户登录")
@EqualsAndHashCode(callSuper = false)
public class StdUserVO {

    @ApiModelProperty(value = "学生学号")
    @NotNull(message = "学号不能为空")
    private String stdNo;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空")
    private String password;


}
