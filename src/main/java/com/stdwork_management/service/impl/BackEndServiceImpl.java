package com.stdwork_management.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.stdwork_management.bean.*;
import com.stdwork_management.exception.UserDefinedException;
import com.stdwork_management.mapper.AdminMapper;
import com.stdwork_management.mapper.LocalFileSysMapper;
import com.stdwork_management.mapper.StdAccountMapper;
import com.stdwork_management.service.BackEndService;
import com.stdwork_management.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-31
 **/
@Slf4j
@Service
public class BackEndServiceImpl implements BackEndService {

    @Autowired
    private StdAccountMapper stdAccountMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private String workPath;

    @Autowired
    private LocalFileSysMapper localFileSysMapper;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String addExcelInputStdAccount(MultipartFile file) {
        List<StdAccountPO> stdAccountPOS;
        try {
            stdAccountPOS = ExcelUtil.readExecel(StdAccountPO.class, file.getOriginalFilename(), file.getInputStream()).stream().map(stdAccountPO -> {
                stdAccountPO.setId(UUIDUtil.getUUID());
                stdAccountPO.setGraduated((byte) 0);
                stdAccountPO.setCreateTime(new Date());
                stdAccountPO.setPassword(MD5Util.getMD5("111111"));
                return stdAccountPO;
            }).collect(Collectors.toList());
            if (stdAccountMapper.insertList(stdAccountPOS) == 0) {
                throw new UserDefinedException(9999, "数据插入错误");
            }
            AdminPO user = (AdminPO)ThreadLocalUtil.get("user");
            log.info("后台用户{}导入了{}学生",  user.getAdminName(), gson.toJson(stdAccountPOS));
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getCause().getMessage();
            if(StringUtils.contains(message, "Duplicate")){
                String[] s = message.split(" ");
                throw new UserDefinedException(9999, "学号:" + s[2] + "已存在，请修改或者删除此学号");
            } else {
                throw new UserDefinedException(9999, e.getCause().getMessage());
            }
        }
        return "数据导入完成";
    }


    @Override
    public List<AdminPO> login(AdminLoginVO adminLoginVO) {
        AdminPO adminPO = new AdminPO();
        BeanUtils.copyProperties(adminLoginVO, adminPO);
        return adminMapper.select(adminPO);
    }

    @Override
    public void changePassword(AdminChangePWDVO adminChangePWDVO) {
        AdminPO adminPO = new AdminPO();
        adminPO.setAdminName(adminChangePWDVO.getAdminName());
        AdminPO update = adminMapper.selectOne(adminPO);
        if(update == null){
            throw new UserDefinedException(9999, "用户不存在或者密码错误");
        }
        update.setUpdateTime(new Date());
        update.setPassword(adminChangePWDVO.getPassword());
        if(adminMapper.updateByPrimaryKeySelective(update) == 0) {
            throw new UserDefinedException(9999, "系统错误, 请稍后再试");
        }

    }

    @Override
    public void creatModel(LocalFileSysCURDVO localFileSysCURDVO) {
        String parentPath = workPath + Optional.ofNullable(localFileSysCURDVO.getParentName()).orElse("");
        File dir = new File(parentPath + "/" + localFileSysCURDVO.getFilename());
        if(dir.exists()){
            throw new UserDefinedException(9999, "模块已存在，创建失败");
        }
        LocalFileSysPO localFileSysPO = new LocalFileSysPO();
        localFileSysPO.setFilename(localFileSysCURDVO.getFilename());
        localFileSysPO.setId(UUIDUtil.getUUID());
        localFileSysPO.setParentPath(parentPath);
        localFileSysPO.setPath(parentPath.equals(workPath) ? parentPath + localFileSysCURDVO.getFilename() : parentPath +  "/" + localFileSysCURDVO.getFilename());
        localFileSysPO.setCreateTime(new Date());
        localFileSysPO.setType((byte) 0);
        Example example = new Example(LocalFileSysPO.class);
//        example.or().orEqualTo("path", parentPath).orEqualTo("path", localFileSysPO.getPath());
        example.or().orEqualTo("path", parentPath);
        List<LocalFileSysPO> localFileSysPOS = localFileSysMapper.selectByExample(example);
        if (localFileSysPOS.size() == 0 ) {
            LocalFileSysPO parent = new LocalFileSysPO();
            parent.setPath(parentPath);
            parent.setType((byte) 0);
            parent.setCreateTime(new Date());
            parent.setId(UUIDUtil.getUUID());
            parent.setPid("1");
            parent.setLevel((byte) 1);
            parent.setParentPath(workPath);
            parent.setFilename(parentPath.substring(parentPath.lastIndexOf("/") + 1));
            if (localFileSysMapper.insertSelective(parent) == 0) {
                throw new UserDefinedException(9999, "本地与数据库数据不符,创建失败1");
            }
            localFileSysPO.setPid(parent.getId());
            localFileSysPO.setLevel((byte) 2);
        } else if(StringUtils.countMatches(localFileSysPOS.get(0).getPath(), "/") >= 3){
            log.info("不允许创建超过两级的模块");
            throw new UserDefinedException(9999, "不允许创建超过两级的模块");
        } else {
            localFileSysPO.setPid(localFileSysPOS.get(0).getId());
            if(localFileSysPOS.get(0).getFilename() == null){
                localFileSysPO.setLevel((byte) 1);
            } else {
                localFileSysPO.setLevel((byte) 2);
            }
        }
        if (localFileSysPOS.stream().filter(po -> localFileSysPO.getPath().equals(po.getPath())).collect(Collectors.toList()).size() == 0
                && localFileSysMapper.insertSelective(localFileSysPO) == 0) {
            throw new UserDefinedException(9999, "本地与数据库数据不符,创建失败2");
        }
        dir.mkdirs();
        File file = new File(localFileSysPO.getPath() + "/.init");
        File file1 = new File(localFileSysPO.getParentPath() + "/.init");
        if (!file.exists() ) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!file1.exists() ) {
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void delete(String path) {
        LocalFileSysPO localFileSysPO = new LocalFileSysPO();
        localFileSysPO.setPath(path);
        if (localFileSysMapper.delete(localFileSysPO) == 0) {
            throw new UserDefinedException(9999, "删除失败");
        }
        File file = new File(path);
        if(file.exists()){
            try {
                log.info("[{}]文件存在", path);
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            log.info("[{}]文件不存在", path);
        }
    }

    @Override
    public PageInfo fileList(LocalFileSysSearchVO localFileSysSearchVO) {
        LocalFileSysPO localFileSysPO = new LocalFileSysPO();
        Example example = new Example(LocalFileSysPO.class);
        List<LocalFileSysPO> localFileSysPOS = null;
        List<LocalFileSysVO> localFileSysVOList = null;
        PageInfo localFileSysPOPageInfo = null;
        String path = localFileSysSearchVO.getPath();
        String searchKey = localFileSysSearchVO.getSearchKey();
        Byte level = localFileSysSearchVO.getLevel();
        if(StringUtils.isNotBlank(searchKey)){
            if(StringUtils.isNotBlank(path)){
                example.and().andEqualTo("parentPath", workPath + path);
            }
            example.setOrderByClause("create_time");
            example.and().andLike("filename", "%" + searchKey + "%");
            localFileSysPOS = localFileSysMapper.selectByExample(example);
            localFileSysPOPageInfo = new PageInfo<>(localFileSysPOS);
//            localFileSysPOS = localFileSysMapper.fullIndexSearch(searchKey);
//            localFileSysPOPageInfo = new PageInfo<>(localFileSysPOS);
        } else if(StringUtils.isBlank(path)){
            localFileSysPO.setLevel((byte) 1);
            example.and().andEqualTo(localFileSysPO);
            example.setOrderByClause("path");
            localFileSysPOS = localFileSysMapper.selectByExample(example);
            localFileSysPOPageInfo = new PageInfo<>(localFileSysPOS);
        } else {
            if (level == null){
                throw new UserDefinedException(9999, "传入参数有误，请参看接口文档");
            }
            if(level < 3){
                example.and().andEqualTo("parentPath", workPath + path);
                example.setOrderByClause("path");
                localFileSysPOS = localFileSysMapper.selectByExample(example);
                localFileSysPOPageInfo = new PageInfo<>(localFileSysPOS);
            } else {
                File file = new File(workPath + path);
                localFileSysVOList = Arrays.stream(file.listFiles(f -> {
                    if(f == null) {
                        return false;
                    }
                    return !f.getName().equals(".init");
                })).map(f -> {
                    LocalFileSysVO fileSysVO = new LocalFileSysVO();
                    fileSysVO.setFilename(f.getName());
    //               fileSysVO.setPath(substring.replaceAll(File.separator, "/"));
                    fileSysVO.setPath(StringUtils.replace(f.getPath().substring(workPath.length()), "\\", "/"));
                    fileSysVO.setType((byte) (f.isDirectory() ? 0 : 1));
                    fileSysVO.setLevel((byte) (StringUtils.countMatches(fileSysVO.getPath(), "/") + 1));
                    return fileSysVO;
                }).collect(Collectors.toList());

                Integer pageNum = localFileSysSearchVO.getPageNum();
                Integer pageSize = localFileSysSearchVO.getPageSize();
                int lastIndex = localFileSysVOList.size() > pageNum * pageSize ? pageNum * pageSize : localFileSysVOList.size();
                int fromIndex = pageNum * pageSize - pageSize;
                int total = localFileSysVOList.size();
                localFileSysVOList = localFileSysVOList.subList(fromIndex, lastIndex);
                localFileSysPOPageInfo = new PageInfo<>(localFileSysVOList);
                localFileSysPOPageInfo.setTotal(total);
                int pages = total % 10 == 0 ? total / 10 : (total / 10) + 1;
                localFileSysPOPageInfo.setPages(pages);
                localFileSysPOPageInfo.setPageNum(pageNum);
                localFileSysPOPageInfo.setPageSize(pageSize);
                localFileSysPOPageInfo.setIsFirstPage(pageNum == 1);
                localFileSysPOPageInfo.setIsLastPage(pageNum == pages);
                localFileSysPOPageInfo.setHasPreviousPage(pageNum != 1);
                localFileSysPOPageInfo.setHasNextPage(pageNum < pages);
            }
        }

        if(localFileSysPOS != null){
            localFileSysVOList = localFileSysPOS.stream().map(po -> {
                LocalFileSysVO localFileSysVO = new LocalFileSysVO();
                po.setPath(po.getPath().substring(workPath.length()));
                BeanUtils.copyProperties(po, localFileSysVO);
                return localFileSysVO;
            }).collect(Collectors.toList());
            localFileSysPOPageInfo.setList(localFileSysVOList);
        }
        return localFileSysPOPageInfo;
    }

    @Override
    public List<StdAccountPO> stdList(StdUserBackendManageVO stdUserBackendManageVO) {
        Example example = new Example(StdAccountPO.class);
        if(stdUserBackendManageVO.getGraduationTime() != null){
            example.and().andLessThan("graduationTime", stdUserBackendManageVO.getGraduationTime());
            stdUserBackendManageVO.setGraduationTime(null);
        }
        StdAccountPO stdAccountPO = new StdAccountPO();
        stdAccountPO.setGraduated(null);
        BeanUtils.copyProperties(stdUserBackendManageVO, stdAccountPO);
        example.and().andEqualTo(stdAccountPO);
        example.setOrderByClause("create_time desc");
        example.selectProperties("id", "name","stdNo", "graduationTime","createTime", "updateTime","graduated");
        List<StdAccountPO> stdAccountPOS;
        stdAccountPOS = stdAccountMapper.selectByExample(example);
        return stdAccountPOS;
    }

    @Override
    public void stdModify(StdUserBackendManageCURDVO stdUserBackendManageCURDVO) {
        StdAccountPO stdAccountPO = new StdAccountPO();
        BeanUtils.copyProperties(stdUserBackendManageCURDVO, stdAccountPO);
        if (StringUtils.isNotBlank(stdAccountPO.getPassword())) {
            stdAccountPO.setPassword(MD5Util.getMD5(stdAccountPO.getPassword()));
        }
        stdAccountPO.setUpdateTime(new Date());
        Example example = new Example(StdAccountPO.class);
        example.and().andEqualTo("stdNo", stdAccountPO.getStdNo());
        List<StdAccountPO> stdNo = stdAccountMapper.selectByExample(example);
        if(stdNo != null && stdNo.size() > 0 ){
            stdAccountPO.setId(stdNo.get(0).getId());
        } else {
            throw new UserDefinedException(9999, "修改学生不存在");
        }
        if (stdAccountMapper.updateByPrimaryKeySelective(stdAccountPO) == 0) {
            new UserDefinedException(9999, "修改失败");
        }
    }

    @Override
    public void stdDelete(String[] ids) {
        Arrays.stream(ids).forEach(id -> stdAccountMapper.deleteByPrimaryKey(id));
    }

    @Override
    public void stdCreate(StdUserBackendManageAddVO stdUserBackendManageAddVO) {
        Example example = new Example(StdAccountPO.class);
        example.and().andEqualTo("stdNo", stdUserBackendManageAddVO.getStdNo());
        List<StdAccountPO> stdAccountPOS = stdAccountMapper.selectByExample(example);
        if ( stdAccountPOS.size() > 0) {
            throw new UserDefinedException(9999, "学号已存在");
        }
        StdAccountPO stdAccountPO = new StdAccountPO();
        BeanUtils.copyProperties(stdUserBackendManageAddVO, stdAccountPO);
        stdAccountPO.setId(UUIDUtil.getUUID());
        stdAccountPO.setCreateTime(new Date());
        stdAccountPO.setPassword(MD5Util.getMD5(stdAccountPO.getPassword()));

        if (stdAccountMapper.insertSelective(stdAccountPO) == 0) {
            throw new UserDefinedException(9999, "创建失败");
        }
    }

    @Override
    @Async("asyncServiceExecutor")
    public void preBackup(HttpServletRequest request, String backUpPath) {
        try {
            log.info("开始压缩备份文件...");
            String zipName = "backup-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            ZipFilesUtil.toZip(zipName);
            redisUtils.set("backupState", true);
            log.info("压缩完成...{}", zipName);
        } catch (FileNotFoundException e) {
            log.info("备份失败...");
            redisUtils.delete("backupState");
            redisUtils.set("backupMessage", "上次备份失败请重新备份");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByIds(List<String> ids) {
        localFileSysMapper.deleteByIds(ids);
    }

}
