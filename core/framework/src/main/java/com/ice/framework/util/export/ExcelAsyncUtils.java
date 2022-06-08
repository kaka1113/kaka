package com.ice.framework.util.export;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author hubo
 * @since 2020/3/6
 */
@Slf4j
public class ExcelAsyncUtils {
    /**
     * 获取excel文件内容
     *
     * @param workbook Workbook
     * @return base64编码的Excel文件内容
     */
    private static String getExcelContent(Workbook workbook) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            log.info("downLoadExcel occur error:{}", e.getMessage());
        }
        return new String(Base64.encodeBase64(outputStream.toByteArray()));
    }

    /**
     * 根据参数和实体类导出
     *
     * @param exportParams excel参数设罫
     * @param list         Excel对象数据List
     * @param pojoClass    Excel对象Class
     * @return base64编码的Excel文件内容
     */
    public static String exportExcel(ExportParams exportParams, List<?> list, Class<?> pojoClass) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        return getExcelContent(workbook);
    }

    /**
     * 根据参数和实体类导出
     *
     * @param title     表格名称
     * @param sheetName sheetName
     * @param list      Excel对象数据List
     * @param pojoClass Excel对象Class
     * @return base64编码的Excel文件内容
     */
    public static String exportExcel(String title, String sheetName, List<?> list, Class<?> pojoClass) {
        return exportExcel(new ExportParams(title, sheetName), list, pojoClass);
    }

    /**
     * 用于支持基于模板导出
     *
     * @param params 导出参数类
     * @param map    模板集合
     * @return base64编码的Excel文件内容
     */
    public static String exportExcel(TemplateExportParams params, Map<String, Object> map) {
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        return getExcelContent(workbook);
    }

    /**
     * 根据Map创建对应的Excel
     *
     * @param title      表格名称
     * @param sheetName  sheetName
     * @param entityList Map对象列表
     * @param list       Excel对象数据List
     * @return base64编码的Excel文件内容
     */
    public static String exportExcel(String title, String sheetName, List<ExcelExportEntity> entityList, Collection<?> list) {
        Workbook workbook = ExcelExportUtil.exportExcel(
                new ExportParams(title, sheetName), entityList, list);
        return getExcelContent(workbook);
    }

    /**
     * 根据Map创建对应的Excel(一个excel 创建多个sheet)
     *
     * @param list 多个Map key title 对应表格Title key entity 对应表格对应实体 key data
     *             Collection 数据
     * @return base64编码的Excel文件内容
     */
    public static String exportExcel(List<Map<String, Object>> list, ExcelType type) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, type);
        return getExcelContent(workbook);
    }

//    /**
//     * 大数据量导出
//     *
//     * @param entity      表格标题属性
//     * @param pojoClass   Excel对象Class
//     * @param server      查询数据的接口
//     * @param queryParams 查询数据的参数
//     */
//    public static String exportBigExcel(ExportParams entity, Class<?> pojoClass,
//                                        IExcelExportServer server, Object queryParams) {
//        Workbook workbook = ExcelExportUtil.exportBigExcel(entity, pojoClass, server, queryParams);
//        return getExcelContent(workbook);
//    }

    @Deprecated
    public static String exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
                                     HttpServletResponse response) {
        return exportExcel(title, sheetName, list, pojoClass);
    }

    @Deprecated
    public static String exportExcel(List<?> list, Class<?> pojoClass, String fileName, ExportParams exportParams) {
        return exportExcel(exportParams, list, pojoClass);
    }

}

