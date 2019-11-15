package com.stdwork_management.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.stdwork_management.base.Result;
import com.stdwork_management.base.annotation.Token;
import com.stdwork_management.bean.*;
import com.stdwork_management.exception.UserDefinedException;
import com.stdwork_management.service.BackEndService;
import com.stdwork_management.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@RestController
@RequestMapping("backend")
@Api(tags = "2 后台接口API")
@Slf4j
@CrossOrigin
public class BackEndController {

    @Autowired
    private BackEndService backEndService;

    @Autowired
    private String workPath;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisUtils redisUtils;


    @PostMapping("inputExcel")
    @Token(accountType = "admin")
    public Result inputExcel(MultipartFile file){
        if (file == null) {
            throw new UserDefinedException(9999, "传输文件为空");
        }
        return new Result().setData(backEndService.addExcelInputStdAccount(file));
    }

    @GetMapping("getExcelModel")
    @Token(accountType = "admin")
    @ApiOperation("获取导入学生数据的excel模板")
    public void getExcelModel(HttpServletResponse response){
        AdminPO user1 = (AdminPO)ThreadLocalUtil.get("user");
        File file = null;
        try {
            file = new File(URLDecoder.decode(BackEndController.class.getClassLoader().getResource("学生目录.xlsx").getPath(), "utf-8"));
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment; filename=" + new String(file.getName().getBytes("gbk"), "iso8859-1"));
            FileUtils.copyFile(file, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            log.info("后台用户{}下载模板失败{}",user1.getAdminName(), file.getName());
            throw new UserDefinedException(9999, "下载模板异常");

        }
        log.info("后台用户{}下载模板{}",user1.getAdminName(), file.getName());
    }

    @GetMapping("stdList")
    @Token(accountType = "admin")
    @ApiOperation("获取学生用户列表")
    public Result stdList(@ApiParam(value = "查询参数") StdUserBackendManageVO stdUserBackendManageVO){
        PageHelper.startPage(stdUserBackendManageVO.getPageNum(), stdUserBackendManageVO.getPageSize());
        List<StdAccountPO> list = backEndService.stdList(stdUserBackendManageVO);
        return new Result().setData(new PageInfo<>(list));
    }

    @GetMapping("stdModify")
    @Token(accountType = "admin")
    @ApiOperation("后台修改学生信息")
    public Result stdModify(@ApiParam(value = "修改参数") StdUserBackendManageCURDVO stdUserBackendManageCURDVO){

        backEndService.stdModify(stdUserBackendManageCURDVO);
        return new Result();
    }

    @GetMapping("stdDelete")
    @Token(accountType = "admin")
    @ApiOperation("后台删除学生信息")
    public Result stdDelete(@ApiParam(value = "删除学生的id") String[] ids){
        AdminPO user1 = (AdminPO)ThreadLocalUtil.get("user");
        if( ids == null){
            throw new UserDefinedException(9999, "传入的参数为空");
        }
        log.info("后台管理{}删除了学生{}", user1.getAdminName(), gson.toJson(ids));
        backEndService.stdDelete(ids);
        return new Result();
    }

    @PostMapping("stdCreate")
    @Token(accountType = "admin")
    @ApiOperation("后台创建学生信息")
    public Result stdCreate(@ApiParam(value = "手动创建学生") @Valid StdUserBackendManageAddVO stdVO, BindingResult bindingResult){
        BindingResultUtil.checkInputParams(bindingResult);
        backEndService.stdCreate(stdVO);
        AdminPO user1 = (AdminPO)ThreadLocalUtil.get("user");
        log.info("后台管理{}创建了学生{}", user1.getAdminName(), stdVO.getStdNo());
        return new Result();
    }


    @PostMapping("login")
    @ApiOperation(value = "后台管理登录")
    public Result login(HttpServletRequest request, @ApiParam("登录参数") @Valid AdminLoginVO adminLoginVO,
                        BindingResult bindingResult){
        BindingResultUtil.checkInputParams(bindingResult);
        adminLoginVO.setPassword(MD5Util.getMD5(adminLoginVO.getPassword()));
        List<AdminPO> adminPOS = backEndService.login(adminLoginVO);
        if (adminPOS == null || adminPOS.size() < 1){
            throw new UserDefinedException(9999, "登录失败管理员名称或密码错误");
        }

        log.info("后台用户{}登录到系统", adminLoginVO.getAdminName());
        String token = MD5Util.getMD5(adminPOS.get(0).toString() + new Date().getTime());
        redisUtils.set(token, token, 30 * 60, TimeUnit.SECONDS);
        redisUtils.set( token + "_admin_user", adminPOS.get(0), 30 * 60, TimeUnit.SECONDS);
//        request.getSession().setAttribute("admin_token",token);
//        request.getSession().setAttribute("admin_user", adminPOS.get(0));
//        request.getSession().setMaxInactiveInterval(30 * 60);
        Map<String, String> map = new HashMap<>();
        return new Result().setData(token);
    }

    @PostMapping("changePwd")
    @ApiOperation(value = "修改密码")
    @Token(accountType = "admin")
    public Result changePassword(@ApiParam("修改密码") @Valid AdminChangePWDVO adminChangePWDVO,
                                 BindingResult bindingResult){
        BindingResultUtil.checkInputParams(bindingResult);
        AdminPO user = (AdminPO) ThreadLocalUtil.get("user");
        if (!StringUtils.equals(user.getPassword(), MD5Util.getMD5(adminChangePWDVO.getOrdPassword()))) {
            throw new UserDefinedException(9999, "原密码输入有误");
        }
        if (!StringUtils.equals(adminChangePWDVO.getPassword(), adminChangePWDVO.getR_password())) {
            throw new UserDefinedException(9999, "两次输入密码不一致");
        }
        AdminPO user1 = (AdminPO)ThreadLocalUtil.get("user");
        log.info("后台用户{}修改了密码",user1.getAdminName());
        adminChangePWDVO.setPassword(MD5Util.getMD5(adminChangePWDVO.getPassword()));
        backEndService.changePassword(adminChangePWDVO);
        return new Result().setData("密码修改成功");
    }

    @PostMapping("modelAdd")
    @Token(accountType = "admin")
    @ApiOperation(value = "实验模块文件管理新增模块")
    public Result modelManagementAdd(@ApiParam(value = "新增模块，最多二级目录") LocalFileSysCURDVO localFileSysCURDVO,
                                     BindingResult bindingResult){

        BindingResultUtil.checkInputParams(bindingResult);
        backEndService.creatModel(localFileSysCURDVO);
        AdminPO user1 = (AdminPO)ThreadLocalUtil.get("user");
        log.info("后台用户{}新增了实验模块{}",user1.getAdminName(), localFileSysCURDVO.getFilename());
        return new Result();
    }

    @GetMapping("modelDel")
    @Token(accountType = "admin")
    @ApiOperation(value = "实验模块文件管理删除模块")
    public Result modelManagementDel(@ApiParam(value = "删除模块，删除的模块路径，, path参数来自于modelList()方法")  @RequestParam(required = true) String path){
        if(StringUtils.isBlank(path) || StringUtils.contains(path,"\\") || StringUtils.countMatches(path, "/")  > 1){
            throw new UserDefinedException(9999, "请求参数不能为空或者参数格式有误,不能使用\\路");
        }

        backEndService.delete(workPath + path);
        AdminPO user1 = (AdminPO)ThreadLocalUtil.get("user");
        log.info("后台用户{}删除了实验模块{}",user1.getAdminName(), path);
        return new Result();
    }

    @GetMapping("modelList")
    @ApiOperation(value = "实验模块文件管理, 查询模块")
    @Token(accountType = "admin")
    public Result modelList(@ApiParam(value = "父模块名称，一级模块的父模块为空") @RequestParam(required = false) String filename){
        String path = workPath + Optional.ofNullable(filename).orElse("");
        File file = new File(path);
        List<LocalFileSysVO> list = Arrays.stream(file.listFiles(f -> f.isDirectory()))
                .map( f -> new LocalFileSysVO().setFilename(f.getName()).setType((byte) 0).setPath(f.getPath().replaceAll("\\\\","/").substring(workPath.length())))
                .collect(Collectors.toList());
        return new Result().setCount(list.size()).setData(list);
    }

    @GetMapping("fileList")
    @ApiOperation(value = "上传文件管理,浏览和搜索")
    @Token(accountType = "admin")
//    public Result fileList(@ApiParam(value = "首次请求时为空(调用第一层)，需要打开下级文件夹的时候传入此参数，为点击的那个文件夹路径，此参数为本方法的返回值之一") @RequestParam(required = false) String path,
//                           @ApiParam(value = "首次请求时为空(调用第一层文件夹), 此参数同样是此方法的返回参数，填点击的那个文件的level") @RequestParam(required = false) Byte level,
//                           @ApiParam(value = "搜索关键字，在浏览的时候不传此参数，可以同时传path和搜索关键字") String searchKey){
    public Result fileList(@ApiParam(value = "test") LocalFileSysSearchVO localFileSysSearchVO){
        PageHelper.startPage(localFileSysSearchVO.getPageNum(), localFileSysSearchVO.getPageSize());
        PageInfo pageInfo = backEndService.fileList(localFileSysSearchVO);
        return new Result().setData(pageInfo);
    }

    @GetMapping("del")
    @Token(accountType = "admin")
    @ApiOperation(value = "删除上传文件, 注意此接口不能用来删除模块")
    public Result fileDel(@ApiParam(value = "删除文件，要删除的文件路径") @RequestParam(required = true) String path){
        if(StringUtils.isBlank(path) || StringUtils.contains(path,"\\") || StringUtils.countMatches(path, "/")  <= 1){
            throw new UserDefinedException(9999, "请求参数不能为空或者参数格式有误,不能使用\\路径");
        }
        backEndService.delete(workPath + path);
        AdminPO user1 = (AdminPO)ThreadLocalUtil.get("user");
        log.info("后台用户{}删除了实上传文件{}",user1.getAdminName(), path);
        return new Result();
    }

    @GetMapping("download")
    @Token(accountType = "admin")
    @ApiOperation("后端文件下载接口，如果需要下载文件夹下面的所有文件，则需要获取到所有文件路径,路径使用此接口downloadPath获取，遍历发送请求")
    public void download(@ApiParam(value = "下载的文件路径，取fileList接口返回数据中的path参数，注此参数需要经过url编码再发送") @RequestParam(value = "path") String path, HttpServletResponse response){
        Path file = Paths.get(workPath + path);
        AdminPO user1 = (AdminPO)ThreadLocalUtil.get("user");
        if (Files.exists(file) && !Files.isDirectory(file)){
            try {
                File file1 = new File(workPath + path);
                response.setContentType("application/x-gzip");
                response.addHeader("Content-Disposition", "attachment; filename=" + new String(file1.getName().getBytes("gbk"), "iso8859-1"));
                Files.copy(file, response.getOutputStream());
                log.info("后台用户{}下载了文件{}",user1.getAdminName(), path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("后台用户{}下载文件{}不存在",user1.getAdminName(), path);
            throw new UserDefinedException(9999, "文件不存在或者不是可供下载文件");
        }
    }

    @GetMapping("downloadPath")
    @Token(accountType = "admin")
    @ApiOperation("如果需要下载文件夹里面的所有文件，请先使用此接口获得文件路径")
    public Result downloadPath(@ApiParam(value = "要下载的文件夹路径") @RequestParam String path){
        if (StringUtils.countMatches(path, "/") < 2){
            throw new UserDefinedException(9999, "不能下载模块，只能下载上传文件夹");
        }
        return new Result().setData(ListFileUtil.getDirJson(path));
    }

    @GetMapping("preBackup")
    @ApiOperation("准备导出")
    @Token(accountType = "admin")
    public Result preBackup(HttpServletRequest request) {
        ServletContext servletContext = request.getServletContext();
        if(servletContext.getAttribute("backupState") != null && !(Boolean) servletContext.getAttribute("backupState")) {
            throw new UserDefinedException(9999, "正在准备备份中, 请不要重复尝试");
        }
        servletContext.setAttribute("backupState", false);
        backEndService.preBackup(request);
        return new Result().setMessage("正在备份中...备份完成后即可下载");
    }

    @GetMapping("searchBackupList")
    @Token(accountType = "admin")
    @ApiOperation("获取备份文件列表, 将返回的路径作为path参数传给download接口即可下载")
    public Result searchList(HttpServletRequest request) {
        if(redisUtils.get("backupState") != null && !(Boolean) redisUtils.get("backupState")){
            throw new UserDefinedException(9999, "正在备份中，等待备份完成后获取列表");
        }
        if(redisUtils.get("backupMessage") != null){
            redisUtils.delete("backupMessage");
            throw new UserDefinedException(9999, "上次备份失败请重新备份");
        }
        File file = new File(workPath + "备份文件待下载区");
        List<LocalFileSysVO> path = Arrays.stream(file.listFiles()).map(f -> {
            LocalFileSysVO localFileSysVO = new LocalFileSysVO();
            localFileSysVO.setPath("备份文件待下载区/" + f.getName());
            return localFileSysVO;
        }).collect(Collectors.toList());
        return new Result().setData(path);
    }
}
