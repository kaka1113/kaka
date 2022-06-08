package com.mg.framework.util.export;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Vick
 * @since 2021/10/18
 */
@Getter
@Setter
public class FileInfoAddRequest implements Serializable {

    private static final long serialVersionUID = -2102120549567565618L;

    /**
     * 类型 IMPORT - 导入  EXPORT - 导出
     */
    private String operateType;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 创建人ID
     */
    private String createUserId;

    /**
     * feign调用时转换,兼容
     */
    public void setType(Object type) {
        if (this.operateType == null && type != null) {
            this.operateType = type.toString();
        }
    }

}
