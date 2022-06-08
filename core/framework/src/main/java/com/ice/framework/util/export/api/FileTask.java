package com.ice.framework.util.export.api;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSON;
import com.ice.framework.exception.MgException;
import com.ice.framework.util.export.*;
import com.ice.framework.redis.BaseRedisDao;
import com.ice.framework.response.ResponseErrorCodeEnum;
import com.ice.framework.response.ResponseModel;
import com.ice.framework.response.ResponseUtil;
import com.ice.framework.util.DateUtil;
import com.ice.framework.util.ObjectUtils;
import com.mg.framework.util.export.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.ContentType;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Lazy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vick
 * @since 2021/10/18
 */
@Slf4j
@Component
public class FileTask implements InitializingBean {
    private final Publisher publisher;
    private final FileOperate fileOperate;
    private BaseRedisDao<String, Object> baseRedisDao;
    private static FileTask fileTask;
    /**
     * 项目所在目录
     */
    private String projectDir;

    /**
     * Publisher和FileOperate都需要实现类,没有的话系统启动不了,但是并不是所有服务都需要导入导出,所以加@Lazy,在使用时才加载这两个类的实现
     */
    public FileTask(@Lazy Publisher publisher, @Lazy FileOperate fileOperate, BaseRedisDao<String, Object> baseRedisDao) {
        this.publisher = publisher;
        this.fileOperate = fileOperate;
        this.baseRedisDao = baseRedisDao;
    }

    @Override
    public void afterPropertiesSet() {
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        //处理本地调试的情况
        if (ObjectUtils.isNotEmpty(jarF)) {
            projectDir = jarF.getParentFile().toString();
        }
        fileTask = this;
        fileTask.baseRedisDao = this.baseRedisDao;
    }

    /**
     * 异步导入并生成导入文件
     *
     * @param fileName     生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param fileBytes    file bytes
     * @param webLoginUser CompanySysUserModel
     * @param importable   Importable
     */
    @Async
    public void imports(String fileName, byte[] fileBytes, CompanySysUserModel webLoginUser, Importable importable, Object param) {
        imports(fileName, fileBytes, webLoginUser, (uploadFile, fileInfoId) -> processImport(webLoginUser, importable, uploadFile, fileInfoId, param));
    }

    /**
     * 通过反射调用异步导入并生成导入文件
     *
     * @param fileName     生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param webLoginUser CompanySysUserModel
     * @param target       查询数据方法的执行者
     * @param method       查询数据的方法
     * @param param        参数
     */
    @Async
    public void imports(String fileName, byte[] fileBytes, CompanySysUserModel webLoginUser, Object target, String method, Object param) {
        imports(fileName, fileBytes, webLoginUser, (uploadFile, fileInfoId) -> processImport(webLoginUser, target, method, uploadFile, fileInfoId, param));
    }

    public void imports(String fileName, byte[] fileBytes, CompanySysUserModel webLoginUser, Processor processor) {
        long beginTime = System.currentTimeMillis();
        assert fileBytes != null;
        MultipartFile file = getUpLoadFile(fileName, fileBytes);

        //先保存文件信息，再处理
        Long fileInfoId = getFileId(String.valueOf(webLoginUser.getId()), fileName, FileInfoType.IMPORT);
        //转base64
        String outputStreamToBase64 = new String(Base64.encodeBase64(fileBytes));
        //异步处理文件状态
        saveFile(fileInfoId, outputStreamToBase64);
        //处理文件数据
        ResponseModel<Void> resultModel = processor.process(file, fileInfoId);
        //导入完更新文件信息状态
        updateFileStatus(fileInfoId, resultModel);
        long endTime = System.currentTimeMillis();
        log.info("导入" + fileName + "异步任务总耗时：{}秒", (endTime - beginTime) / 1000);
    }

    /**
     * 同步生成导出文件
     *
     * @param optUserId 操作人ID
     * @param fileName  生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param title     表格名称
     * @param sheetName sheetName
     * @param list      Excel对象数据List
     * @param pojoClass Excel对象Class
     */
    public void export(String optUserId, String fileName, String title, String sheetName, List<?> list, Class<?> pojoClass) {
        export(optUserId, fileName, ".xls", () -> ExcelAsyncUtils.exportExcel(title, sheetName, list, pojoClass));
    }

    /**
     * 同步生成导出文件
     *
     * @param optUserId 操作人ID
     * @param fileName  生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param sheetName sheetName
     * @param list      Excel对象数据List
     * @param pojoClass Excel对象Class
     */
    public void export(String optUserId, String fileName, String sheetName, List<?> list, Class<?> pojoClass) {
        export(optUserId, fileName, null, sheetName, list, pojoClass);
    }

    /**
     * 同步生成导出文件
     *
     * @param optUserId  操作人ID
     * @param fileName   生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param sheetName  sheetName
     * @param columnList Map对象列表
     * @param exportList Excel对象数据List
     */
    public void export(String optUserId, String fileName, String sheetName, List<ExcelExportEntity> columnList, List<Map<String, Object>> exportList) {
        export(optUserId, fileName, ".xls", () -> ExcelAsyncUtils.exportExcel(null, sheetName, columnList, exportList));
    }

    /**
     * 同步生成导出文件
     * 根据Map创建对应的Excel(一个excel 创建多个sheet)
     *
     * @param optUserId 操作人ID
     * @param fileName  文件名
     * @param list      多个Map key title 对应表格Title key entity 对应表格对应实体 key data
     *                  Collection 数据
     */
    public void export(String optUserId, String fileName, List<Map<String, Object>> list, ExcelType type) {
        export(optUserId, fileName, (type == null || type == ExcelType.HSSF) ? ".xls" : ".xlsx", () -> ExcelAsyncUtils.exportExcel(list, type == null ? ExcelType.HSSF : type));
    }

//    /**
//     * 异步生成导出文件
//     * 大数据量导出
//     *
//     * @param optUserId          操作人
//     * @param fileName           文件名
//     * @param pojoClass          Excel对象Class
//     * @param excelExportService 查询数据的接口
//     * @param queryParams        查询数据的参数
//     * @param <T>                Excel对象Class类型
//     */
//    @Async
//    public <T> void export(String optUserId, String fileName, Class<T> pojoClass, IExcelExportServer excelExportService, Object queryParams) {
//        export(optUserId, fileName, ".xlsx", () -> {
//            ExportParams exportParams = new ExportParams(null, fileName);
//            exportParams.setType(ExcelType.XSSF);
//            return ExcelAsyncUtils.exportBigExcel(exportParams, pojoClass, excelExportService, queryParams);
//        });
//    }

    /**
     * 异步生成导出文件
     * 大数据量导出
     *
     * @param optUserId          操作人
     * @param fileName           生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param pojoClass          Excel对象Class
     * @param excelExportService 查询数据的接口
     * @param queryParams        查询数据的参数
     * @param <T>                Excel对象Class类型
     */
    @Async
    public <T> void export(String optUserId, String fileName, Class<T> pojoClass, Exportable<T> excelExportService, Object queryParams) {
        export(optUserId, fileName, ".xls", () -> {
            List<T> exportList = getExportList(fileName, excelExportService, queryParams);
            return ExcelAsyncUtils.exportExcel(fileName, fileName, exportList, pojoClass);
        });
    }

    /**
     * 异步生成导出文件
     * 大数据量导出
     *
     * @param optUserId   操作人
     * @param fileName    生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param pojoClass   Excel对象Class
     * @param target      查询数据方法的执行者
     * @param method      查询数据的方法
     * @param queryParams 查询数据的参数
     * @param <T>         Excel对象Class类型
     */
    @Async
    public <T> void export(String optUserId, String fileName, Class<T> pojoClass, Object target, String method, Object queryParams) {
        export(optUserId, fileName, ".xls", () -> {
            List<T> exportList = getExportList(fileName, target, method, queryParams);
            return ExcelAsyncUtils.exportExcel(fileName, fileName, exportList, pojoClass);
        });
    }

    /**
     * 异步生成导出文件
     * 大数据量导出
     *
     * @param optUserId          操作人
     * @param fileName           生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param templateName       模板名
     * @param excelExportService 查询数据的接口
     * @param queryParams        查询数据的参数
     * @param <T>                Excel对象Class类型
     */
    @Async
    public <T> void export(String optUserId, String fileName, String templateName, Exportable<T> excelExportService, Object queryParams) {
        List<T> exportList = getExportList(fileName, excelExportService, queryParams);
        export(optUserId, fileName, templateName, exportList);
    }

    /**
     * 同步生成导出文件
     *
     * @param optUserId    操作人ID
     * @param fileName     生成文件名称的前缀[生成的文件名称为:fileName+"_"+"yyyyMMddHHmmss"格式的当前时间)]
     * @param templateName 模板名
     * @param exportList   Excel对象数据List
     */
    public <T> void export(String optUserId, String fileName, String templateName, List<T> exportList) {
        export(optUserId, fileName, ".xls", () -> {
            String path = projectDir + "/export-template/" + templateName;
            TemplateExportParams params = new TemplateExportParams(path);
            Map<String, Object> map = new HashMap<>(16);
            map.put("list", exportList);
            return ExcelAsyncUtils.exportExcel(params, map);
        });
    }

    private ResponseModel<Void> processImport(CompanySysUserModel webLoginUser, Importable importable, MultipartFile file, Long fileInfoId, Object param) {
        ResponseModel<Void> resultModel;
        try {
            resultModel = importable.imports(fileInfoId, file, webLoginUser, param);
        } catch (Exception e) {
            log.info("importable.imports occur error:{}", e.getMessage());
            resultModel = ResponseUtil.fail(e.getMessage() == null ? "没有获取到异常消息,请联系运营人员" : e.getMessage());
        }
        return resultModel;
    }

    @SuppressWarnings("unchecked")
    private ResponseModel<Void> processImport(CompanySysUserModel webLoginUser, Object target, String methodName, MultipartFile file, Long fileInfoId, Object param) {
        ResponseModel<Void> resultModel;
        try {
            Method method = target.getClass().getMethod(methodName, Long.class, MultipartFile.class, CompanySysUserModel.class, Object.class);
            method.setAccessible(true);
            Object invoke = method.invoke(target, fileInfoId, file, webLoginUser, param);
            resultModel = (ResponseModel<Void>) invoke;
        } catch (Exception e) {
            log.info("importable.imports occur error:{}", e.getMessage());
            resultModel = ResponseUtil.fail(e.getMessage() == null ? "没有获取到异常消息,请联系运营人员" : e.getMessage());
        }
        return resultModel;
    }

    private MultipartFile getUpLoadFile(String fileName, byte[] fileBytes) {
        InputStream inputStream = new ByteArrayInputStream(fileBytes);
        MultipartFile file;
        try {
            file = new MockMultipartFile(fileName, fileName, ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
        } catch (IOException e) {
            throw new MgException(ResponseErrorCodeEnum.API_ERROR.getCode(), "上传字节流转换文件失败");
        }
        return file;
    }

    public static byte[] getFileBytes(MultipartFile file) {
        byte[] fileBytes;
        try {
            fileBytes = IOUtils.toByteArray(file.getInputStream());
        } catch (IOException e) {
            throw new MgException(ResponseErrorCodeEnum.API_ERROR.getCode(), "获取上传文件字节流失败");
        }
        return fileBytes;
    }

    private Long getFileId(String optUserId, String fileName, String export) {
        FileInfoAddRequest fileInfoAddRequest = new FileInfoAddRequest();
        fileInfoAddRequest.setOperateType(export);
        fileInfoAddRequest.setFileName(fileName);
        fileInfoAddRequest.setCreateUserId(optUserId);
        return fileOperate.add(fileInfoAddRequest);
    }

    private void saveFile(Long fileInfoId, String outputStreamToBase64) {
        FileInfoMqRequest fileInfoMqRequest = new FileInfoMqRequest();
        fileInfoMqRequest.setFileInfoId(fileInfoId);
        fileInfoMqRequest.setBase64FileString(outputStreamToBase64);
        publisher.send(JSON.toJSONString(fileInfoMqRequest));
    }

    private void updateFileStatus(Long fileInfoId, ResponseModel<Void> resultModel) {
        FileInfoUpdateRequest fileInfoUpdateRequest = new FileInfoUpdateRequest();
        fileInfoUpdateRequest.setId(fileInfoId);
        fileInfoUpdateRequest.setFileStatus(FileInfoStatus.SUCCESS);
        if (resultModel.getErrCode() != 0) {
            int msgLengthLimit = 30000;
            String errorMsg = resultModel.getMessage().length() <= msgLengthLimit ? resultModel.getMessage() : resultModel.getMessage().substring(0, msgLengthLimit) + "... ...";

            fileInfoUpdateRequest.setFileStatus(FileInfoStatus.FAILURE);
            fileInfoUpdateRequest.setFailureReason(errorMsg);
        }
        fileOperate.update(fileInfoUpdateRequest);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getExportList(String fileName, Object target, String methodName, Object queryParams) {
        assert target != null;
        assert methodName != null;
        assert queryParams != null;
        //获取workbook
        ExportParams exportParams = new ExportParams(null, fileName);
        exportParams.setType(ExcelType.XSSF);
        List<T> exportList;
        try {
            Method method = target.getClass().getMethod(methodName, queryParams.getClass());
            //设置为true可调用类的私有方法
            method.setAccessible(true);
            Object invoke = method.invoke(target, queryParams);
            exportList = (List<T>) invoke;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Excel导出时获取反射方法出错:", e);
        }
        return exportList;
    }

    private <T> List<T> getExportList(String fileName, Exportable<T> excelExportService, Object queryParams) {
        //获取workbook
        ExportParams exportParams = new ExportParams(null, fileName);
        exportParams.setType(ExcelType.XSSF);
        List<T> exportList = Collections.emptyList();
        ResponseModel<List<T>> resultModel = excelExportService.selectListForExcelExport(queryParams);
        if (ResultEnum.SUCCESS.getKey().equals(String.valueOf(resultModel.getErrCode()))) {
            exportList = resultModel.getData();
        } else {
            log.info("fetch " + fileName + " data while exporting occur error:" + resultModel.getMessage());
        }
        return exportList;
    }

    private void export(String optUserId, String fileName, String fileType, Executor executor) {
        long beginTime = System.currentTimeMillis();
        fileName = fileName + DateUtil.getDate(DateUtil.DATE_PATTERN5) + fileType;
        //先保存文件信息，再处理
        Long fileInfoId = getFileId(optUserId, fileName, FileInfoType.EXPORT);
        //导出文件
        String outputStreamToBase64 = executor.exec();
        //异步处理文件状态
        saveFile(fileInfoId, outputStreamToBase64);
        long endTime = System.currentTimeMillis();
        log.info("导出" + fileName + "异步任务总耗时：{}秒", (endTime - beginTime) / 1000);
    }

    /**
     * 计算处理进度并将之设置到redis
     *
     * @param currentCount current count
     * @param totalCount   total count
     * @param processKey   process key
     */
    public static void calcAndSetProcessProgress(int currentCount, int totalCount, String processKey) {
        BigDecimal currentCountD = new BigDecimal(String.valueOf(currentCount));
        BigDecimal importTotalCountD = new BigDecimal(String.valueOf(totalCount));
        BigDecimal hundredD = new BigDecimal("100");
        String speed = (currentCountD.divide(importTotalCountD, 2, RoundingMode.HALF_UP).multiply(hundredD).stripTrailingZeros().toPlainString()) + "%";
        fileTask.baseRedisDao.set(processKey, speed);
    }

    private interface Executor {

        /**
         * 获取base64编码的Excel文件内容
         *
         * @return base64编码的Excel文件内容
         */
        String exec();
    }

    private interface Processor {

        /**
         * 处理导入逻辑
         *
         * @param flie       上传的文件
         * @param fileInfoId 保存到数据库的文件ID
         * @return 处理结果
         */
        ResponseModel<Void> process(MultipartFile flie, Long fileInfoId);
    }

}
