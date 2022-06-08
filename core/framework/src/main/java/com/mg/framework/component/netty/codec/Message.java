package com.mg.framework.component.netty.codec;

/**
 * @author : tjq
 * @since : 2022-05-12
 */
public class Message {

    /** 消息命令请求信息 */
    private String uri;

    /** 消息类型 */
    private byte type;

    /** 消息流水号 */
    private long serialNumber;

    /** 消息传输数据 */
    private byte[] data;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}



