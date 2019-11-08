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
public class StdUserBackendManageVO {

    @ApiModelProperty("学生姓名")
    private String name;

    @ApiModelProperty("学号")
    private String stdNo;

    @ApiModelProperty("毕业时间")
    private Date graduationTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("是否毕业， 1已毕业， 0未毕业  默认为0")
    private Byte graduated;

    @ApiModelProperty(value = "页数")
    private Integer pageNum;

    @ApiModelProperty(value = "每页行数")
    private Integer pageSize;

    public Byte getGraduated(){
        if(graduated == null){
            return graduated = 0;
        }
        return graduated;
    }
    public Integer getPageNum() {
        if(pageNum == null){
            pageNum = 1;
        }
        return pageNum;
    }
    public Integer getPageSize() {
        if(pageSize == null){
            pageSize = 10;
        }
        if(pageSize > 10000){
            pageSize = 10000;
        }
        return pageSize;
    }
}
