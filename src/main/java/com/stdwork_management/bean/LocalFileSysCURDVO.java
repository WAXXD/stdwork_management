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
 * @date 2019-11-05
 **/
@Data
@ApiModel(description = "实验模块新增,数据示例\n{'filename':'变压吸附装置'}表示一级目录" +
        "\n{'filename':'变压吸附装置-PSA1','parentPath':'变压吸附装置'}表示在'变压吸附装置'下的目录'变压吸附装置-PSA1'")
public class LocalFileSysCURDVO {

    @ApiModelProperty(value = "在工作空间下的模块名称，为文件夹, eg: 变压吸附装置")
    @NotNull(message = "模块名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件夹路径如果是第一级这里为空，如果是第二级这里为上级文件名称")
    private String parentName;

    @ApiModelProperty(value = "modelAdd接口不传此参数，文件路径或者文件夹路径")
    private String path;

}
