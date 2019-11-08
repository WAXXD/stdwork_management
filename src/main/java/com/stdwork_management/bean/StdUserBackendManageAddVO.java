package com.stdwork_management.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class StdUserBackendManageAddVO {

    @ApiModelProperty("学生姓名不能为空")
    @NotBlank(message = "name")
    private String name;

    @ApiModelProperty("学号不能为空")
    @NotBlank(message = "stdNo不能为空")
    private String stdNo;

    @ApiModelProperty("密码")
    @NotBlank(message = "password不能为空")
    private String password;

    @ApiModelProperty("毕业时间不能为空 yyyy-MM-dd")
    @NotNull(message = "毕业时间不能为空")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date graduationTime;

    @ApiModelProperty("是否毕业， 1已毕业， 0未毕业")
    @NotNull(message = "是否毕业，1已毕业， 0未毕业")
    private Byte graduated;

}
