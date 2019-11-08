package com.stdwork_management.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-09-09
 **/
@Slf4j
@Component
public class UploadFileUtil {


    private static String filePath;

    static {
        filePath = PropertiesUtils.getValue("config.properties", "server.workpath");
    }

    public static String upload(MultipartFile file){
        if(file == null){
            return null;
        }
        return storeFile(file, "");
    }

    public static List<String> multiUpload(HttpServletRequest request, String dest){
        try {
            File file = new File(filePath + dest + "/temp/");
            if (file.exists()) {
                FileUtils.cleanDirectory(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, MultipartFile> fileMap = ((MultipartHttpServletRequest) request).getFileMap();
        List<String> fileNames = new ArrayList<>();

        Iterator<Map.Entry<String, MultipartFile>> iterator = fileMap.entrySet().iterator();
        while (iterator.hasNext()){
            MultipartFile file = iterator.next().getValue();
            if(file.isEmpty()){
                return fileNames;
            }
            String uploadRes = storeFile(file, dest);
            if(StringUtils.equals("上传失败",uploadRes)){
                return fileNames;
            }
            fileNames.add(uploadRes);
        }
        return fileNames;
    }

    private static String storeFile(MultipartFile file, String dest){
        File dirPath = new File(filePath + dest + "/temp/");
        if(!dirPath.exists()){
            dirPath.mkdirs();
        }
        File target = new File(filePath + dest + "/temp/" + file.getOriginalFilename());
        try{
            file.transferTo(target);
            log.info("上传成功");
            return target.getName() + "-上传成功";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target.getName() + "-上传失败";
    }



    public static void main(String[] args) throws IOException {
        FileUtils.cleanDirectory(new File("E:/z00/temp/"));
    }

}
