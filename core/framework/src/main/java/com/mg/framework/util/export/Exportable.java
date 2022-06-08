package com.mg.framework.util.export;

import com.mg.framework.response.ResponseModel;

import java.util.List;

/**
 * @author Vick
 * @since 2021/10/19
 */
public interface Exportable<T> {
    /**
     * 查询导出列表数据
     *
     * @param param Object
     * @return ResultModel<List < T>>
     */
    ResponseModel<List<T>> selectListForExcelExport(Object param);
}
