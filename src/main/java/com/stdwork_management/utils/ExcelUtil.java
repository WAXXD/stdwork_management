package com.stdwork_management.utils;

import com.stdwork_management.base.annotation.ExcelColumn;
import com.stdwork_management.bean.StdAccountPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * description:
 *
 * @author waxxd
 * @version 1.0
 * @date 2019-10-29
 **/
@Slf4j
public class ExcelUtil {


    public static <T> List<T> readExecel(Class<T> tClass, String filename, InputStream is){
        if(!filename.matches("^.+\\.(xls|xlsx)$")){
            log.error("请选择正确的excel文件格式");
        }
        Workbook workbook = null;
        List<T> dataList = new ArrayList<>();
        try{
            if (StringUtils.endsWithIgnoreCase(filename, "xlsx")){
                workbook = new XSSFWorkbook(is);
            } else {
                workbook = new HSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(workbook != null){
            Map<String, List<Field>> clsMap = new HashMap<>();
            List<Field> fields = Arrays.asList(tClass.getDeclaredFields());
            fields.forEach( field -> {
                ExcelColumn declaredAnnotation = field.getDeclaredAnnotation(ExcelColumn.class);
                if(declaredAnnotation != null){
                    String value = declaredAnnotation.value();
                    if(StringUtils.isNotBlank(value)){
                        if(!clsMap.containsKey(value)){
                            clsMap.put(value, new ArrayList<>());
                        }
                        field.setAccessible(true);
                        clsMap.get(value).add(field);
                    }
                }
            });

            Map<Integer, List<Field>> reflectionMap = new HashMap<>();
            Sheet sheet = workbook.getSheetAt(0);

            boolean firstRow = true;

            int lastRowNum = sheet.getLastRowNum();
            for(int i = sheet.getFirstRowNum(); i <= lastRowNum; i++){
                Row row = sheet.getRow(i);
                if(firstRow){
                    for(int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++){
                        Cell cell = row.getCell(j);
                        String value = cell.getStringCellValue();
                        if(clsMap.containsKey(value)){
                            reflectionMap.put(j, clsMap.get(value));
                        }
                    }
                    firstRow = false;
                } else {
                    if( row == null){
                        continue;
                    }
                    boolean blank = true;
                    try {
                        T t = tClass.newInstance();

                        for(int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                            if(reflectionMap.containsKey(j)){
                                Cell cell = row.getCell(j);
                                Object value = getCellValue(cell);
                                if(!StringUtils.isBlank((String) value)){
                                    blank = false;
                                }
                                List<Field> fieldList = reflectionMap.get(j);
                                for (Field field : fieldList){
                                    if (StringUtils.equals(field.getType().getSimpleName(), "Date")) {
                                        field.set(t, new Date(Long.valueOf((String) value)));
                                    } else {
                                        field.set(t, value);
                                    }
                                }
                            }
                        }

                        if(!blank){
                            dataList.add(t);
                        } else {
                            log.info("row {} is blank, ignore!", i);
                        }
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return dataList;
    }

    public static <T> List<T> readExecel(Class<T> tClass, String path, File file){
        String filename = file.getName();
        if(!filename.matches("^.+\\.(xls|xlsx)$")){
            log.error("请选择正确的excel文件格式");
        }
        InputStream is = null;
        List<T> dataList = null;
        try {
            is = new FileInputStream(file);
            dataList = readExecel(tClass, filename, is);
        }  catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;
    }

    private static String getCellValue(Cell cell){
        if(cell == null) {
            return "";
        }
        CellType cellType = cell.getCellType();
        if (cellType == CellType.NUMERIC) {
            if(HSSFDateUtil.isCellDateFormatted(cell)){
                Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                return String.valueOf(date.getTime());
            } else {
                return new BigDecimal(cell.getNumericCellValue()).toString();
            }
        } else if(cellType ==  CellType.STRING){
            return StringUtils.trimToEmpty(cell.getStringCellValue());
        } else if (cell.getCellType() == CellType.ERROR) {
            return "ERROR";
        } else {
            return cell.toString().trim();
        }
    }

    public static void main(String[] args) {
        File file = new File("E:\\idea_workspace\\stdwork_management\\src\\main\\resources\\学生目录.xlsx");
        List<StdAccountPO> stdAccountPOS = readExecel(StdAccountPO.class, null, file);
        stdAccountPOS.forEach(System.out::println);

    }
}
