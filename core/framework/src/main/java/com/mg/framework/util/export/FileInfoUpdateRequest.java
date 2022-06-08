package com.mg.framework.util.export;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Vick
 * @since 2021/10/18
 */
@Getter
@Setter
public class FileInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 2720437111615128561L;

    @ApiModelProperty(value = "文件信息ID")
    private Long id;

    @ApiModelProperty(value = "状态：PROCESSING - 处理中  SUCCESS - 成功  FAILURE - 失败  ")
    private String fileStatus;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "导入文件")
    private String importFile;

    @ApiModelProperty(value = "导出文件")
    private String exportFile;

    @ApiModelProperty(value = "失败原因")
    private String failureReason;

    /**
     * feign调用时转换,兼容
     */
    public void setStatus(Object status) {
        if (this.fileStatus == null && status != null) {
            this.fileStatus = status.toString();
        }
    }
}
