package com.ice.framework.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * SimpleDateFormat线程不安全使用ThreadLocal避免
 *
 * @author hubo
 * @since 2020/2/25
 */
public class DateUtil {

    private static final Log log = LogFactory.getLog(DateUtil.class);

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN1 = "yyyy/MM/dd";
    public static final String DATE_PATTERN2 = "yyyyMMdd";
    public static final String DATE_PATTERN3 = "yyyy-MM-dd";
    public static final String DATE_PATTERN4 = "yyyy-MM-dd HH:00:00";
    public static final String DATE_PATTERN5 = "yyyyMMddHHmmss";
    public static final String DATE_PATTERN6 = "yyyy-MM-dd 00:00:00";
    public static final String DATE_PATTERN7 = "yyyyMM";
    public static final String DATE_PATTERN8 = "yyyyMM01";
    public static final String DATE_PATTERN9 = "yyyy-MM-dd_HH:mm:ss";
    public static final String DATE_PATTERN10 = "yyyy-MM-dd 23:59:59";
    public static final String DATE_PATTERN11 = "yyyy-MM";
    public static final String DATE_PATTERN12 = "yyyy/MM";

    public static final String TIME_PATTERN1 = "HH:mm:ss";
    public static final String TIME_PATTERN2 = "HHmmss";
    public static final String TIME_PATTERN3 = "MMdd";
    public static final String TIME_PATTERN4 = "HHmmssSSS";


    public static final String HOUR_PATTERN = "HH";

    public static final SimpleDateFormat DATE_FORMAT_S = new SimpleDateFormat(DATE_PATTERN3);

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN3) {{
                setLenient(false);
            }}
    );

    private static final ThreadLocal<SimpleDateFormat> HH_MM_DATE_FORMAT = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("HH:mm") {{
                setLenient(false);
            }}
    );

    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMAT = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN10) {{
                setLenient(false);
            }}
    );

    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT1 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN1) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT2 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN2) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT3 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN3) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT4 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN4) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT5 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN5) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT6 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN6) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT7 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN7) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT8 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN8) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT9 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN9) {{
                setLenient(false);
            }}
    );
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT10 = ThreadLocal.withInitial(() ->
            new SimpleDateFormat(DATE_PATTERN10) {{
                setLenient(false);
            }}
    );

    /**
     * 指定类型-获取当前日期
     *
     * @param pattern
     * @return
     */
    public static String getDate(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        String nowDate = dateFormat.format(new Date());
        return nowDate;
    }

    /**
     * 指定类型-获取当前日期
     *
     * @param pattern
     * @return
     */
    public static Date getDateByDateStr(String dateForStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN3);
        try {
            Date date = dateFormat.parse(dateForStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取前月份
     *
     * @return
     */
    public static String getLastOfMth(int mth) {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(mth);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_PATTERN11);
        return formatters.format(today);
    }


    /**
     * 获取当前时间指定N分钟后的时间（yyyyMMddHHmmss）
     *
     * @param time
     * @param min
     * @return
     */
    public static String afterNMin(String time, int min) {
        Date date = null;
        try {
            date = new SimpleDateFormat(DATE_PATTERN5).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MINUTE, min);
        Date afterNMin = now.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN5);
        String result = format.format(afterNMin);
        return result;
    }

    /*
     * 当前时间偏移运算
     * skipDay 偏移天数 支持负数
     */
    public static String getTimeStr(int skipDay) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(GregorianCalendar.DAY_OF_MONTH, skipDay);
        return DATE_FORMAT_S.format(cal.getTime());
    }

    /*
     * 指定时间的偏移运算
     * skipDay 偏移天数 支持负数
     */
    public static String getTimeStr(String timeStr, int skipDay) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(parseTime(timeStr));
        cal.add(GregorianCalendar.DAY_OF_MONTH, skipDay);
        return DATE_FORMAT_S.format(cal.getTime());
    }

    /*
     * 解析时间字符串
     */
    public static Date parseTime(String timeStr) {
        if (null == timeStr || timeStr.equals("")) {
            return null;
        }
        Date date = null;
        try {
            date = DATE_FORMAT_S.parse(timeStr);
        } catch (ParseException e) {
            log.error("-- DateUtil Parse Error --", e);
        }
        return date;
    }

    /**
     * @Author: qiang.su
     * @since: 2021/9/24 10:55
     * @Desc: 计算两个时间的时间差。注意正负数。
     */
    public static Long getDiffValueAbs(LocalDateTime startTime, LocalDateTime endTime, TimeUnit unit) {
        return getDiffValue(startTime, endTime, unit, true);
    }

    public static Long getDiffValue(LocalDateTime startTime, LocalDateTime endTime, TimeUnit unit, boolean abs) {
        java.time.Duration duration = java.time.Duration.between(startTime, endTime);
        Long value = 0L;
        switch (unit) {
            case SECONDS:
                value = duration.getSeconds();//秒数
                break;
            case MILLISECONDS:
                value = duration.toMillis();//毫秒
                break;
            case MINUTES:
                value = duration.toMinutes(); //分钟
                break;
            case HOURS:
                value = duration.toHours();//小时
                break;
            case DAYS:
                value = duration.toDays();//天数
                break;
            case NANOSECONDS:
                value = duration.toNanos();//纳秒
                break;
            default:
                throw new RuntimeException("不支持的时间格式。");
        }
        return abs ? Math.abs(value) : value;
    }

}
