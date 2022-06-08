package com.ice.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: qiang.su
 * Date: 2020/4/7
 * Msg: 为了更好查日志 这里写一个日志包装方法
 */
public class ExceptionUtil {

    private static Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);

    public static String processException(Exception e) {
        String timeStr = "网络有点忙，";
        if (null != e) {
            logger.error(timeStr, e);
        }
        return timeStr;
    }

    public static String processException(String msg, Exception e) {
        String timeStr = "网络有点忙，" + msg;
        //String timeStr = "哎呀，系统好像开小差了～（" + System.currentTimeMillis() + ")";
        if (null != e) {
            logger.error(timeStr, e);
        }
        return timeStr;
    }


}
