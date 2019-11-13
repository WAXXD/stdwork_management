package com.stdwork_management.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.stdwork_management.base.Result;
import com.stdwork_management.base.annotation.Token;
import com.stdwork_management.bean.LocalFileSysSearchVO;
import com.stdwork_management.bean.StdAccountPO;
import com.stdwork_management.bean.StdUserChangePWDVO;
import com.stdwork_management.bean.StdUserVO;
import com.stdwork_management.exception.UserDefinedException;
import com.stdwork_management.service.AppService;
import com.stdwork_management.service.BackEndService;
import com.stdwork_management.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-11-04
 **/
@RestController
@RequestMapping("app")
@Api(tags = "3 app API")
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class AppController {

    @Autowired
    private AppService appService;

    @Autowired
    private BackEndService backEndService;

    @Autowired
    private String workPath;

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("test")
    public Result test(HttpServletRequest request){

        Properties properties = PropertiesUtils.getProperties("config.properties");
        System.out.println(properties.get("test.test"));
        System.out.println(workPath);
        System.out.println(request.getSession().getAttribute("user"));
        return new Result().setData(request.getSession().getAttribute("user"));
    }

    @PostMapping("login")
    @ApiOperation(value = "用户登录")
    public Result login(HttpServletRequest request, @ApiParam("登录参数") @Valid StdUserVO stdUserVO,
                        BindingResult bindingResult){
        BindingResultUtil.checkInputParams(bindingResult);
        stdUserVO.setPassword(MD5Util.getMD5(stdUserVO.getPassword()));
        List<StdAccountPO> stdAccountPOS = appService.login(stdUserVO);
        if (stdAccountPOS == null || stdAccountPOS.size() < 1){
            log.info("登录失败用户名或密码错误");
            throw new UserDefinedException(9999, "登录失败用户名或密码错误");
        }
        if(stdAccountPOS.get(0).getGraduated() == 1) {
            log.info("已毕业此账号不能使用");
            throw new UserDefinedException(9999, "已毕业此账号不能使用");
        }
        log.info("{}同学登录到系统", stdAccountPOS.get(0).getName());
        String token = MD5Util.getMD5(stdAccountPOS.get(0).toString() + new Date().getTime());
//        request.getSession().setAttribute("token",token);
//        request.getSession().setAttribute("user", stdAccountPOS.get(0));
        redisUtils.set(token, token, 30 * 60, TimeUnit.SECONDS);
        redisUtils.set(token + "_user", stdAccountPOS.get(0), 30 * 60, TimeUnit.SECONDS);
        request.getSession().setMaxInactiveInterval(30 * 60);
        return new Result().setData(token);
    }

    @PostMapping("changePwd")
    @ApiOperation(value = "修改密码")
    @Token
    public Result changePassword(@ApiParam("修改密码") @Valid StdUserChangePWDVO stdUserChangePWDVO,
                                 BindingResult bindingResult){
        BindingResultUtil.checkInputParams(bindingResult);
        StdAccountPO user = (StdAccountPO)ThreadLocalUtil.get("user");
        if (!StringUtils.equals(stdUserChangePWDVO.getPassword(), stdUserChangePWDVO.getR_password())) {
            throw new UserDefinedException(9999, "两次输入密码不一致");
        }
        if (!StringUtils.equals(user.getPassword(), MD5Util.getMD5(stdUserChangePWDVO.getOrdPassword()))) {
            throw new UserDefinedException(9999, "原密码输入有误");
        }
        stdUserChangePWDVO.setPassword(MD5Util.getMD5(stdUserChangePWDVO.getPassword()));
        appService.changePwd(stdUserChangePWDVO);
        log.info("{}同学修改了密码", user.getName());
        return new Result().setData("密码修改成功");

    }

    @Token
    @GetMapping("fileList")
    @ApiOperation(value = "上传文件管理,浏览和搜索")
    public Result fileList(@ApiParam(value = "test") LocalFileSysSearchVO localFileSysSearchVO){
        PageHelper.startPage(localFileSysSearchVO.getPageNum(), localFileSysSearchVO.getPageSize());
        PageInfo pageInfo = backEndService.fileList(localFileSysSearchVO);
        return new Result().setData(pageInfo);
    }

    @Token
    @GetMapping("download")
    @ApiOperation("文件下载接口，如果需要下载文件夹下面的所有文件，则需要获取到所有文件路径,路径使用此接口downloadPath获取，遍历发送请求")
    public void download(@ApiParam(value = "下载的文件路径，取fileList接口返回数据中的path参数，注此参数需要经过url编码再发送") @RequestParam(value = "path") String path, HttpServletResponse response){
        Path file = Paths.get(workPath + path);
        StdAccountPO user = (StdAccountPO)ThreadLocalUtil.get("user");
        if (Files.exists(file) && !Files.isDirectory(file)){
            try {
                File downloadFile = new File(workPath + path);
                response.addHeader("Content-Disposition", "attachment; filename=" + new String(downloadFile.getName().getBytes("gbk"), "iso8859-1"));
                response.setContentType("application/x-gzip");
                Files.copy(file, response.getOutputStream());
                log.info("app用户{}下载了文件{}", user.getName(), file.getFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            log.info("app用户{}下载的文件{}文件不存在", user.getName(), file.getFileName());
            throw new UserDefinedException(9999, "文件不存在或者不是可供下载文件");
        }
    }

    @GetMapping("downloadPath")
    @Token
    @ApiOperation("如果需要下载文件夹里面的所有文件，请先使用此接口获得文件路径")
    public Result downloadPath(@ApiParam(value = "要下载的文件夹路径") @RequestParam String path){
        if (StringUtils.countMatches(path, "/") < 2){
            throw new UserDefinedException(9999, "不能下载模块，只能下载上传文件夹");
        }
        return new Result().setData(ListFileUtil.getDirJson(path));
    }





}
