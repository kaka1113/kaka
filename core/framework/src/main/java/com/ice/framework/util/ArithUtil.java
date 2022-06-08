package com.ice.framework.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author hubo
 * @since 2020/3/6
 */
public class ArithUtil {

    /**
     * DecimalFormat 线程不安全，这里使用ThreadLocal规避
     */
    private static final ThreadLocal<DecimalFormat> ZERO_FORMAT = ThreadLocal.withInitial(() ->
            new DecimalFormat("#0.00")
    );

    /**
     * DecimalFormat 线程不安全，这里使用ThreadLocal规避
     */
    private static final ThreadLocal<DecimalFormat> ZERO_FORMAT_FIVE = ThreadLocal.withInitial(() ->
            new DecimalFormat("#0.00000")
    );

    /**
     * 保留两位小数 (位数不足填充零 eg: 0.00)
     *
     * @param obj
     * @return
     */
    public static String formatTowScale(BigDecimal obj) {
        return ZERO_FORMAT.get().format(obj);
    }

    /**
     * 保留五位小数 (位数不足填充零 eg: 0.00000)
     *
     * @param obj
     * @return
     */
    public static String formatFiveScale(BigDecimal obj) {
        return ZERO_FORMAT_FIVE.get().format(obj);
    }

    /**
     * @Author: qiang.su
     * @since: 2020/4/9 12:59
     * @Desc:前面补充0
     */
    public static String fillString(Long num, int digit) {
        /**
         * 0：表示前面补0
         * digit：表示保留数字位数
         * ArithUtil.fillString(123,8) -> 00000123
         */
        return String.format("%0" + digit + "d", num);
    }

}
