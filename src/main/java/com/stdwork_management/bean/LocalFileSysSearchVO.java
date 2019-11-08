package com.stdwork_management.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@Data
@Accessors(chain = true)
@ApiModel(description = "搜索查询")
public class LocalFileSysSearchVO {

    @ApiModelProperty(value = "首次请求时为空(调用第一层)，需要打开下级文件夹的时候传入此参数，为点击的那个文件夹路径，此参数为本方法的返回值之一")
    private String path;

    @ApiModelProperty(value = "搜索关键字，如果是日期日期格式必须为yyyyHHdd，在浏览的时候不传此参数")
    private String searchKey;

    @ApiModelProperty(value = "首次请求时为空(调用第一层文件夹), 此参数同样是此方法的返回参数，填点击的那个文件的level")
    private Byte level;

    @ApiModelProperty(value = "页数")
    private Integer pageNum;

    @ApiModelProperty(value = "每页行数")
    private Integer pageSize;

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
