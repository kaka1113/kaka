package com.ice.framework.util.export;

import com.ice.framework.response.ResponseModel;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Vick
 * @since 2020/9/8
 */
public interface Importable {

    /**
     * 功能描述描述
     *
     * @return 导入功能描述
     */
    @SuppressWarnings("unused")
    default String getMethodDescription() {
        return "";
    }

    /**
     * 批量导入
     *
     * @param fileInfoId          file info id
     * @param file                MultipartFile
     * @param companySysUserModel CompanySysUserModel
     * @param param               参数
     * @return ResultModel
     */
    ResponseModel<Void> imports(Long fileInfoId, MultipartFile file, CompanySysUserModel companySysUserModel, Object param);

}
