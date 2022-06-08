package com.ice.framework.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;

/**
 * java8时间工具类
 * @author : tjq
 * @version V1.0
 * @since : 2021年07月20日 8:52
 */
public class LocalDateTimeUtil {


    public static final String TIME_TO_ZERO = "yyyy/MM/dd 00:00:00";
    /**
     * 构造时间
     *
     * @param amountToSubtract
     * @param unit
     * @return
     */
//    public static LocalDateTime of(long amountToSubtract, TemporalUnit unit) {
//        return LocalDateTime.of(2019, Month.SEPTEMBER, 10, 14, 46, 56);
//    }
    public static final String MinTime = "T00:00:00";
    public static final String MaxTime = "T23:59:59.999999999";

    /**
     * 获取当前时间
     *
     * @return
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 时间相加
     *
     * @param amountToAdd
     * @param unit
     * @return
     */
    public static LocalDateTime plus(long amountToAdd, TemporalUnit unit) {
//        ChronoUnit.DAYS.
        return LocalDateTime.now().plus(amountToAdd, unit);
    }

    /**
     * 时间相减
     *
     * @param amountToSubtract
     * @param unit
     * @return
     */
    @Deprecated
    public static LocalDateTime mins(long amountToSubtract, TemporalUnit unit) {
        return LocalDateTime.now().minus(amountToSubtract, unit);
    }

    /**
     * 时间相减
     */
    public static LocalDateTime minus(long amountToSubtract, TemporalUnit unit) {
        return LocalDateTime.now().minus(amountToSubtract, unit);
    }

    /**
     * LocalDate a = LocalDateTime.of(2012, 6, 30, 12, 00);
     * LocalDate b = LocalDateTime.of(2012, 7, 1, 12, 00);
     * a.isBefore(b) == true
     * a.isBefore(a) == false
     * b.isBefore(a) == false
     *
     * @param beforeTime
     * @param afterTime
     * @return
     */
    public static boolean before(LocalDateTime beforeTime, LocalDateTime afterTime) {
        return beforeTime.isBefore(afterTime);
    }

    /**
     * LocalDate a = LocalDateTime.of(2012, 6, 30, 12, 00);
     * LocalDate b = LocalDateTime.of(2012, 7, 1, 12, 00);
     * a.isAfter(b) == false
     * a.isAfter(a) == false
     * b.isAfter(a) == true
     *
     * @param beforeTime
     * @param afterTime
     * @return
     */
    public static boolean after(LocalDateTime beforeTime, LocalDateTime afterTime) {
        return beforeTime.isAfter(afterTime);
    }

    /**
     * 转化时间
     *
     * @return
     */
    public static LocalDateTime format(LocalDateTime localDateTime, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(df.format(localDateTime));
    }

    /**
     * @Description:当天的开始时间
     * @Param: [today, isFirst: true 表示开始时间，false表示结束时间]
     */
    public static LocalDateTime getStartOrEndDayOfDay(LocalDate today, Boolean isFirst) {
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        if (isFirst) {
            return LocalDateTime.of(today, LocalTime.MIN);
        } else {
            return LocalDateTime.of(today, LocalTime.MAX);
        }
    }

    /**
     * @Description:本周的开始时间
     * @Param: [today, isFirst: true 表示开始时间，false表示结束时间]
     */
    public static LocalDateTime getStartOrEndDayOfWeek(LocalDate today, Boolean isFirst) {
        String time = MinTime;
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        DayOfWeek week = today.getDayOfWeek();
        int value = week.getValue();
        if (isFirst) {
            resDate = today.minusDays(value - 1);
        } else {
            resDate = today.plusDays(7 - value);
            time = MaxTime;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(resDate + time);
        return localDateTime;
    }

    /**
     * @Description:本月的开始时间
     * @Param: [today, isFirst: true 表示开始时间，false表示结束时间]
     */
    public static LocalDateTime getStartOrEndDayOfMonth(LocalDate today, Boolean isFirst) {
        String time = MinTime;
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        int length = month.length(today.isLeapYear());
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), month, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), month, length);
            time = MinTime;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(resDate + time);
        return localDateTime;
    }

    /**
     * @Description:本季度的开始时间
     * @Param: [today, isFirst: true 表示开始时间，false表示结束时间]
     */
    public static LocalDateTime getStartOrEndDayOfQuarter(LocalDate today, Boolean isFirst) {
        String time = MinTime;
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
            time = MaxTime;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(resDate + time);
        return localDateTime;
    }

    /**
     * @Description:本年度的开始时间
     * @Param: [today, isFirst: true 表示开始时间，false表示结束时间]
     */
    public static LocalDateTime getStartOrEndDayOfYear(LocalDate today, Boolean isFirst) {
        String time = MinTime;
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), Month.JANUARY, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), Month.DECEMBER, Month.DECEMBER.length(today.isLeapYear()));
            time = MaxTime;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(resDate + time);
        return localDateTime;
    }

}
