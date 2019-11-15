package com.stdwork_management.utils;

import com.stdwork_management.bean.LocalFileSysVO;
import com.stdwork_management.exception.UserDefinedException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    private static final String dataPattern = "-(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|" +
            "1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))" +
            ")|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)(([0-9]{3}[" +
            "1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9" +
            "]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2" +
            "468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)$";

    public static void main(String[] args) {
        System.out.println(Pattern.matches(dataPattern, "-20190109"));
    }
}

