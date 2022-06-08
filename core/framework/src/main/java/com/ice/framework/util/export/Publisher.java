package com.ice.framework.util.export;

/**
 * @author Vick
 * @since 2021/10/18
 */
public interface Publisher {
    /**
     * 发送消息
     *
     * @param msg msg
     */
    void send(String msg);
}
