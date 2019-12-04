package com.stdwork_management.utils;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-21
 **/

import com.stdwork_management.bean.AdminPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class ZipFilesUtil {

    private static final String zipBasePath;

    private static String unzipBasePath;

    private static String unzipDestPath;

    static {
        zipBasePath = PropertiesUtils.getValue("config.properties", "server.workpath");
        unzipBasePath = PropertiesUtils.getValue("config.properties", "server.workpath");
        unzipDestPath = PropertiesUtils.getValue("config.properties", "server.workpath");
    }


    private static void compress(File f, String baseDir, ZipOutputStream zos) {
        if (!f.exists()) {
            log.info("待压缩的文件目录或文件" + f.getName() + "不存在");
            return;
        }

        File[] fs = f.listFiles();
        BufferedInputStream bis = null;
        //ZipOutputStream zos = null;
        byte[] bufs = new byte[1024 * 10];
        FileInputStream fis = null;

        try {
            //zos = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i < fs.length; i++) {
                String fName = fs[i].getName();
                log.info("zip -> {}", baseDir + fName);
                if(StringUtils.equals(fName, "备份文件待下载区") || StringUtils.equals(fName, "temp")) continue;
                if (fs[i].isFile()) {
                    ZipEntry zipEntry = new ZipEntry(baseDir + fName);//
                    zos.putNextEntry(zipEntry);

                    fis = new FileInputStream(fs[i]);
                    bis = new BufferedInputStream(fis, 1024 * 10);
                    int read = 0;
                    while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                        zos.write(bufs, 0, read);
                    }
                    //如果需要删除源文件，则需要执行下面2句
                    //fis.close();
                    //fs[i].delete();
                } else if (fs[i].isDirectory()) {
                    compress(fs[i], baseDir + fName + "/", zos);
                }
            }//end for
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (null != bis)
                    bis.close();
                //if(null!=zos)
                //zos.close();
                if (null != fis)
                    fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void toZip(String zipName) throws FileNotFoundException {
        File sourceDir = new File(zipBasePath);
        String zipPath = zipBasePath + "/备份文件待下载区/";
        File file = new File(zipPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File zipFile = new File(zipPath + zipName + ".zip");
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            String baseDir = "";
            compress(sourceDir, baseDir, zos);
        } finally {
            if (zos != null)
                try {
                    zos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }

    public static void unZip(String unZipFileName, String dest) throws RuntimeException {
        boolean isDir = StringUtils.contains(unZipFileName, "$$") ? false : true;
        long start = System.currentTimeMillis();
        File srcFile = new File(unzipBasePath + dest + "/temp/" + unZipFileName);
//        if (!srcFile.exists()) {
//            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
//        }
//        String fileName = srcFile.getName();
//        String currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        String destPath = unzipDestPath + dest + "/" + fileName.substring(0, fileName.lastIndexOf(".")) + "-" + currentDay;
//        File file = new File(destPath);
//        try {
//            FileUtils.deleteDirectory(file);
//            file.mkdirs();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String destPath = "";
        String currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if(isDir){
            destPath = unzipDestPath + dest + "/" + unZipFileName.substring(0, unZipFileName.lastIndexOf(".")) + "-" + currentDay;
        } else {
            destPath = unzipDestPath + dest + "/";
        }

        ZipFile zipFile = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                log.info("解压 {}", entry.getName());
                String dirPath;
                if(isDir){
                    dirPath = destPath + "/" + entry.getName();
                } else {
//                    String tempName = StringUtils.replace(unZipFileName, "$$.zip", "");
//                    String[] split = tempName.split("\\.");
//                    dirPath = destPath + "/" + split[0] + "-" + currentDay + "." + split[1];

                    String tempName = StringUtils.replace(unZipFileName, "$$.zip", "");
                    String fn = tempName.substring(0, tempName.lastIndexOf("."));
                    String suffix = tempName.substring(tempName.lastIndexOf("."));
//                    destPath = unzipDestPath + dest + "/" + fn + "-" + currentDay + suffix;
                    dirPath = destPath + "/" + fn + "-" + currentDay + suffix;
                }

                if (entry.isDirectory()) {
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {

                    File targetFile = new File(dirPath);
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    is = zipFile.getInputStream(entry);
                    fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = is.read(buf)) != -1) {

                        fos.write(buf, 0, len);
                    }
                    fos.close();
                    is.close();
                }
            }


            long end = System.currentTimeMillis();
            log.info("unzip {} time -> {}", unZipFileName,end - start);
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String createUnzipDir(String unZipFileName, String dest){
        boolean isDir = StringUtils.contains(unZipFileName, "$$") ? false : true;
//        new File(unzipDestPath + dest).listFiles( f -> {
//            String filename = f.getName();
//            if(!StringUtils.equals(filename, "temp") &&
//                    !StringUtils.equals(filename, ".init") &&
//                    StringUtils.equals(filename.substring(0, filename.length() - 9), unZipFileName.substring(0, unZipFileName.lastIndexOf(".")))){
//                try {
//                    FileUtils.deleteDirectory(f);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//            return false;
//        });
        File srcFile = new File(unzipBasePath + dest + "/temp/" + unZipFileName);
//        if (!srcFile.exists()) {
//            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
//        }
        String fileName = srcFile.getName();
        String currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String destPath;

        if(!isDir){
            String tempName = StringUtils.replace(unZipFileName, "$$.zip", "");
            String fn = tempName.substring(0, tempName.lastIndexOf("."));
            String suffix = tempName.substring(tempName.lastIndexOf("."));
            destPath = unzipDestPath + dest + "/" + fn + "-" + currentDay + suffix;
        } else {
            destPath = unzipDestPath + dest + "/" + fileName.substring(0, fileName.lastIndexOf(".")) + "-" + currentDay;
        }
        File file = new File(destPath);
        try {
            if(isDir){
                FileUtils.deleteDirectory(file);
                file.mkdirs();
            } else {
                if(file.exists()){
                    FileUtils.forceDelete(file);
                }
                file.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return destPath;
    }



    public static void main(String[] args) throws FileNotFoundException {
//        createUnzipDir("00042901普华达11.27.xlsx", "");

        AdminPO adminPO = new AdminPO();
        adminPO.setId("1113");
        System.out.println(adminPO.hashCode());
        System.out.println(false);
//        try {
//            FileUtils.forceDeleteOnExit(new File("M:\\z01\\新建文本文档 - 副本 (2).txt"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Integer integer = new Integer(0xee);
//        System.out.println(Integer.toString(0xee, 10));
//        System.out.println(Integer.valueOf("ee", 16));
//        System.out.println(integer);

    }



}

