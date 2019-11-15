package com.stdwork_management.controller;

import com.stdwork_management.base.Result;
import com.stdwork_management.bean.LocalFileSysPO;
import com.stdwork_management.mapper.LocalFileSysMapper;
import com.stdwork_management.utils.UUIDUtil;
import com.stdwork_management.utils.UploadFileUtil;
import com.stdwork_management.utils.ZipFilesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-21
 **/
@RestController
@Api(tags = "1 处理请求")
@RequestMapping("client")
@ApiIgnore
@Slf4j
@CrossOrigin
public class ClientController {

    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @Autowired
    private String workPath;

    @Autowired
    private LocalFileSysMapper localFileSysMapper;

    private static final String dataPattern = ".+-(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|" +
            "1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))" +
            ")|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)(([0-9]{3}[" +
            "1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9" +
            "]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2" +
            "468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)$";

    @GetMapping("test")
    public Result test(){
        return new Result();
    }

    @GetMapping("dirList")
    public Result dirList(@ApiParam(value = "路径参数，此版本不传", required = false) @RequestParam(value = "path", required = false) String path,
                          @ApiParam(value = "是否包含文件，默认包含, false不包含, 客户端不传此参数", required = false) @RequestParam(value = "flag", required = false) Boolean flag){
        List<String> dirName;
        if (StringUtils.isNotBlank(path)){
            workPath = workPath + path;
            flag = flag == null ? true : false;
            if(!flag){
                dirName = Arrays.stream(new File(workPath).listFiles(f -> f.isDirectory() && Pattern.matches("^.+-[0-9]{8}$", f.getName()))).map(file -> file.getName()).collect(Collectors.toList());
            } else {
                dirName = Arrays.stream(new File(workPath).listFiles()).map(file -> path + "/" + file.getName()).collect(Collectors.toList());
            }
        } else {
            dirName = Arrays.stream(new File(workPath).listFiles(pathname -> {
                if (pathname.isDirectory() && Pattern.matches(dataPattern, pathname.getName())) {
                    return true;
                }
                return false;
            })).map(File::getName).collect(Collectors.toList());
        }

        return new Result().setData(dirName);
    }

    @GetMapping("dirInPath")
    public Result dirInPath( String path){
        Example example = new Example(LocalFileSysPO.class);
        example.and().andEqualTo("level", (byte)3).andEqualTo("parentPath", workPath + path);
        List<String> dirsInPath = localFileSysMapper.selectByExample(example).stream().map(po -> po.getFilename()).collect(Collectors.toList());
        return new Result().setData(dirsInPath);
    }


    @PostMapping("uploadStdFile")
    @ApiIgnore
    public String upload(HttpServletRequest request, String dest){
        MultipartHttpServletRequest multipartHttpServletRequest = null;
        if(request instanceof MultipartHttpServletRequest){
            multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        }
        UploadFileUtil.multiUpload(request, dest);
        Map<String, MultipartFile> fileMap = multipartHttpServletRequest.getFileMap();
        Collection<MultipartFile> values = fileMap.values();

        List<LocalFileSysPO> localFileSysPOList = new ArrayList<>();
        LocalFileSysPO sysPO = new LocalFileSysPO();
        sysPO.setPath(workPath + dest);
        List<LocalFileSysPO> sysPOList = localFileSysMapper.select(sysPO);
        values.stream().forEach(item -> {
            String filename = item.getOriginalFilename();
            String unzipDir = ZipFilesUtil.createUnzipDir(filename, dest);
            boolean isDir = StringUtils.contains(filename, "$$") ? false : true;
            LocalFileSysPO localFileSysPO = new LocalFileSysPO();
            if(isDir){
                localFileSysPO.setType((byte) 0);

            } else {
                localFileSysPO.setType((byte) 1);
            }
            localFileSysPO.setPath(unzipDir);
            localFileSysPO.setFilename(unzipDir.substring(unzipDir.lastIndexOf("/") + 1));
            localFileSysPO.setParentPath(workPath + dest);
            localFileSysPO.setLevel((byte) 3);
            localFileSysPO.setCreateTime(new Date());
            localFileSysPO.setPid(sysPOList.get(0).getId());
            localFileSysPO.setId(UUIDUtil.getUUID());
            localFileSysPOList.add(localFileSysPO);

        });
        Condition condition = new Condition(LocalFileSysPO.class);
        Example.Criteria criteria = condition.or();
        localFileSysPOList.forEach(po -> criteria.orLike("path", po.getPath().substring(0, po.getPath().lastIndexOf("-") + 1) + "%"));
        Optional.ofNullable(localFileSysMapper.selectByCondition(condition)).orElse(new ArrayList<>())
                .forEach( po -> {
                    try {
                        FileUtils.forceDelete(new File(po.getPath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        localFileSysMapper.deleteByCondition(condition);
        localFileSysMapper.insertList(localFileSysPOList);
        asyncServiceExecutor.execute(() -> {
            values.stream().forEach( item -> ZipFilesUtil.unZip(item.getOriginalFilename(), dest));
            try {
                FileUtils.cleanDirectory(new File(workPath + dest + "/temp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        String remoteAddr = request.getRemoteAddr();
        localFileSysPOList.forEach( item -> log.info("{}用户上传了文件{}", remoteAddr, item.getFilename()));
        return "ok";
    }

    @GetMapping("selectDest")
    public Result selectDest(){
        LocalFileSysPO localFileSysPO = new LocalFileSysPO();
        localFileSysPO.setLevel((byte) 2);
        List<String> list = localFileSysMapper.select(localFileSysPO).stream().map(po -> po.getPath().substring(workPath.length())).collect(Collectors.toList());
        return new Result().setData(list);
    }
}
