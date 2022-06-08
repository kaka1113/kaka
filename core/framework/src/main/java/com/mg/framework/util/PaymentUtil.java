package com.mg.framework.util;

import com.mg.framework.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * @author zyk
 * @since 2022/3/11
 */
@Component
@Slf4j
public class PaymentUtil {


    /**
     * 生成微信回调返回
     *
     * @param returnCode
     * @param response
     * @return
     */
    public String generateWxCallBackReturn(String returnCode, HttpServletResponse response) {
        String resXml = "<xml>\n" +
                "  <return_code><![CDATA[" + returnCode + "]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();

        } catch (Exception ex) {
            ExceptionUtil.processException(ex);
        } finally {

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ExceptionUtil.processException(ex);
            }

        }
        return resXml;
    }


}

