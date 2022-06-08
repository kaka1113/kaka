package com.mg.framework.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author hubo
 * @since 2020/2/25
 */
public class CommonUtil {

    /**
     * 生成指定位数的随机数
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        int number = 0;
        for (int i = 0; i < length; i++) {
            number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String header1 = "x-forwarded-for";
        String header2 = "Proxy-Client-IP";
        String header3 = "WL-Proxy-Client-IP";
        String ip1 = "unknown";
        String ip2 = "127.0.0.1";
        String ip3 = "0:0:0:0:0:0:0:1";
        String ipAddress = request.getHeader(header1);
        if (ipAddress == null || ipAddress.length() == 0 || ip1.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader(header2);
        }
        if (ipAddress == null || ipAddress.length() == 0 || ip1.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader(header3);
        }
        if (ipAddress == null || ipAddress.length() == 0 || ip1.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ip2.equals(ipAddress) || ip3.equals(ipAddress)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        int i = 15;
        if (ipAddress != null && ipAddress.length() > i) {
            String split = ",";
            if (ipAddress.indexOf(split) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(split));
            }
        }
        return ipAddress;
    }

    //xml解析
    public static Map doXMLParse(String strxml) {
        Map m = new HashMap();
        InputStream in = null;
        try {
            strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
            if ("".equals(strxml)) {
                return null;
            }
            in = new ByteArrayInputStream(strxml.getBytes(StandardCharsets.UTF_8));
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            List list = root.getChildren();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String k = e.getName();
                String v = "";
                List children = e.getChildren();
                if (children.isEmpty()) {
                    v = e.getTextNormalize();
                } else {
                    v = getChildrenText(children);
                }
                m.put(k, v);
            }
            //关闭流
            in.close();
        } catch (Exception ex) {
            ExceptionUtil.processException(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ExceptionUtil.processException(ex);
                }
            }
        }
        return m;
    }

    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

}
