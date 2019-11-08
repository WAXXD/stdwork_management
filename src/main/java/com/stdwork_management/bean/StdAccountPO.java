package com.stdwork_management.bean;

import com.stdwork_management.base.annotation.ExcelColumn;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-29
 **/
@Data
@Table(name = "std_account")
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StdAccountPO {

    @Id
    private String id;

    @ExcelColumn(value = "姓名")
    private String name;

    @ExcelColumn(value = "学号")
    private String stdNo;

    private String password;

    @ExcelColumn(value = "毕业时间")
    private Date graduationTime;

    private Date createTime;

    private Date updateTime;

    private Byte graduated;

}
