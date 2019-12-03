package com.stdwork_management.service;

import com.github.pagehelper.PageInfo;
import com.stdwork_management.bean.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
public interface BackEndService {

    String addExcelInputStdAccount(MultipartFile file);

    List<AdminPO> login(AdminLoginVO adminLoginVO);

    void changePassword(AdminChangePWDVO adminChangePWDVO);

    void creatModel(LocalFileSysCURDVO localFileSysCURDVO);

    void delete(String path);

    PageInfo fileList(LocalFileSysSearchVO localFileSysSearchVO);

    List<StdAccountPO> stdList(StdUserBackendManageVO stdUserBackendManageVO);

    void stdModify(StdUserBackendManageCURDVO stdUserBackendManageCURDVO);

    void stdDelete(String[] ids);

    void stdCreate(StdUserBackendManageAddVO stdUserBackendManageAddVO);

    void preBackup(HttpServletRequest request, String backUpPath);

    void deleteByIds(List<String> ids);
}
