package com.ice.framework.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import io.netty.util.internal.StringUtil;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liaojianjie
 * @since 2020/8/14
 */
public class MyStringUtil {


    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     * @author liaojianjie
     */
    public static String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim().toUpperCase();
    }

    public static String getCustomerId(String province) {
        //如果长度超过三个，只取前三个
        province = province.length() >= 3 ? province.substring(0, 3) : province;
        //地区首字母拼音
        String provincePinYin = getFirstSpell(province);
        //时间字符
        LocalDateTime localDateTime = LocalDateTime.now();
        String dateStr = DateTimeFormatter.ofPattern(DateUtil.DATE_PATTERN2).format(localDateTime);
        //4位随机数字
        int no = (int) (Math.random() * 8998) + 1000 + 1;
        return provincePinYin + dateStr + no;
    }


    /**
     * 隐藏中间4位手机字符
     *
     * @param phone 手机字符
     * @author liaojianjie
     */
    public static String scdPhone(String phone) {
        if (phone == null) {
            return null;
        }
        if (phone.length() != 11) {
            return phone;
        }
        String csdPhone = phone.replaceAll("(\\d{4})\\d{4}(\\d{3})", "$1****$2");
        return csdPhone;
    }

    /**
     * 补零
     *
     * @param number
     * @author liaojianjie
     */
    public static String zerofill(Long number, Integer length) {
        String formatStr = "%0" + length + "d";
        String code = String.format(formatStr, number);
        return code;
    }

    /**
     * 解析规格信息并以字符串形式返回规格的值列表
     *
     * @param spec {"key1":"val1","key2":"val2"...}
     * @return {val1,val2...}
     * @author Vick
     * @since 2020/10/18
     */
    public static String parseSkuSpec(String spec) {
        JSONObject specJsonObj = JSONObject.parseObject(spec, Feature.OrderedField);
        StringBuilder specValBuilder = new StringBuilder();
        for (Object value : specJsonObj.values()) {
            specValBuilder.append(value).append(",");
        }
        specValBuilder.deleteCharAt(specValBuilder.length() - 1);
        return specValBuilder.toString();
    }

    public static String join(String... str) {
        StringBuilder sb = new StringBuilder();
        for (String s : str) {
            if (!StringUtil.isNullOrEmpty(s)) {
                sb.append(s);
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    /**
     * @Author: qiang.su
     * @since: 2021/5/26 14:01
     * @Desc: 根据正则表达式获取符合的字符部分
     */
    public static List<String> getFromPatter(String str, String reg) {
        List<String> result = new ArrayList<>();

        Pattern p = Pattern.compile(reg);//把正则封装成对象
        Matcher m = p.matcher(str);//让正则与要作用的字符串进行匹配

        while (m.find())//按规则作用于字符串，并进行查找
        {
            result.add(m.group());//获取匹配后的结果
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(getFromPatter("a:b:{user.id}:{sku.id}", "\\{+.*\\}"));
    }
}
