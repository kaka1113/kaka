package com.ice.framework.util.export;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Vick
 * @since 2021/10/18
 */
public interface FileOperate {
    /**
     * 新增文件信息
     *
     * @param request FileInfoAddRequest
     * @return Long
     */
    @PostMapping(value = "/fileInfo-provide/add")
    Long add(@RequestBody FileInfoAddRequest request);

    /**
     * 修改文件信息
     *
     * @param request FileInfoUpdateRequest
     */
    @PostMapping(value = "/fileInfo-provide/update")
    void update(@RequestBody FileInfoUpdateRequest request);
}
