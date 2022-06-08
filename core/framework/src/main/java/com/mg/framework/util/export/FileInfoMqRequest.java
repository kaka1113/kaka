package com.mg.framework.util.export;

import java.io.Serializable;

/**
 * @author Vick
 * @since 2021/10/18
 */
public class FileInfoMqRequest implements Serializable {

    private static final long serialVersionUID = 5080232549990511889L;

    /**
     * 文件信息ID
     */
    private Long fileInfoId;
    /**
     * 文件base64字符串
     */
    private String base64FileString;

    public Long getFileInfoId() {
        return fileInfoId;
    }

    public void setFileInfoId(Long fileInfoId) {
        this.fileInfoId = fileInfoId;
    }

    public String getBase64FileString() {
        return base64FileString;
    }

    public void setBase64FileString(String base64FileString) {
        this.base64FileString = base64FileString;
    }
}
