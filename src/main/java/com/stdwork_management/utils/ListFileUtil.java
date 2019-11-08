package com.stdwork_management.utils;

import com.stdwork_management.bean.LocalFileSysVO;
import com.stdwork_management.exception.UserDefinedException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-25
 **/
public class ListFileUtil {

    private final static String baseDir = PropertiesUtils.getValue("config.properties", "server.workpath");

    public static List<LocalFileSysVO> getDirJson(String path){
        File file = new File(baseDir + path);
        LocalFileSysVO localFileSysVO = new LocalFileSysVO();
        localFileSysVO.setPath(baseDir + path);
        localFileSysVO.setType((byte) (file.isDirectory() ? 0 : 1));
        localFileSysVO.setFilename(file.getName());

        if(!file.exists()){
            throw new UserDefinedException(9999, file.getName() + "文件夹不存在");
        }
        List<LocalFileSysVO> localFileSysVOS = new ArrayList<>();
        getDir(file, localFileSysVOS);
        return localFileSysVOS;
    }
    private static void getDir(File file, List<LocalFileSysVO> localFileSysVOS) {

        File[] files = file.listFiles();
        for(File f : files){

            if (f.isDirectory()){
                getDir(f, localFileSysVOS);
            } else {
                LocalFileSysVO sysVO = new LocalFileSysVO();
                sysVO.setFilename(f.getName());
                sysVO.setType((byte) 1);
                sysVO.setPath(StringUtils.replace(f.getPath(), "\\", "/").substring(baseDir.length()));
                localFileSysVOS.add(sysVO);
            }
        }

    }

    public static void main(String[] args) {
//        FileDirBean test = getDirJson("-2019-01-01");
//        Gson gson = new Gson();
//        System.out.println(gson.toJson(test));
        String s = "E:/z04/乙苯脱氢反应装置/乙苯脱氢反应装置-sa3";
        File file = new File("E:\\z04\\乙苯脱氢反应装置");
        System.out.println(getDirJson("乙苯脱氢反应装置/乙苯脱氢反应装置-EDR2/waxxd-2019-01-01-20191106"));
    }
}

