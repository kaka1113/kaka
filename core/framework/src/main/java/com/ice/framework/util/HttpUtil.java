package com.ice.framework.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hubo
 * @since 2020/2/25
 */
public class HttpUtil {

    /**
     * HTTP成功状态码
     */
    public static final int HTTP_SUCCESS_STATUS_CODE = 200;

    /**
     * HTTP协议POST请求方法
     */
    public static String httpMethodPost(String url,
                                        String params) {
        return httpMethodPost(url, params, "UTF-8", "application/json;charset=UTF-8");
    }

    /**
     * HTTP协议POST请求方法
     */
    public static String httpMethodPost(String url,
                                        String params, String charsetName, String content_type) {

        StringBuffer sb = new StringBuffer();
        URL urls;
        HttpURLConnection uc = null;
        BufferedReader in = null;
        try {
            urls = new URL(url);
            uc = (HttpURLConnection) urls.openConnection();
            uc.setRequestMethod("POST");
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setUseCaches(false);
            uc.setRequestProperty("Content-Type",
                    content_type);
            uc.connect();
            DataOutputStream out = new DataOutputStream(uc.getOutputStream());
            out.write(params.getBytes(charsetName));
            out.flush();
            out.close();
            in = new BufferedReader(new InputStreamReader(uc.getInputStream(),
                    charsetName));
            String readLine = "";
            while ((readLine = in.readLine()) != null) {
                sb.append(readLine);
            }
            if (in != null) {
                in.close();
            }
            if (uc != null) {
                uc.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (uc != null) {
                uc.disconnect();
            }
        }
        return sb.toString();
    }

    /**
     * 作者：hubo 日期：2017/7/13 16:43 描述：通过post请求发送json数据
     */
    public static String sendPost(String url, Object obj) throws Exception {
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        String result = null;
        //解决中文乱码问题
        StringEntity entity = new StringEntity(JSON.toJSONString(obj), "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        CloseableHttpResponse resp = client.execute(httpPost);
        if (resp.getStatusLine().getStatusCode() == HTTP_SUCCESS_STATUS_CODE) {
            HttpEntity he = resp.getEntity();
            result = EntityUtils.toString(he, "UTF-8");
            EntityUtils.consume(he);
        }
        resp.close();
        return result;
    }

    /**
     * 作者：hubo 日期：2017/7/13 17:32 描述：通过get请求发送json数据
     */
    public static String sendGet(String url) throws Exception {
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建get方式请求对象
        HttpGet httpGet = new HttpGet(url);
        String result = null;
        CloseableHttpResponse resp = client.execute(httpGet);
        if (resp.getStatusLine().getStatusCode() == HTTP_SUCCESS_STATUS_CODE) {
            HttpEntity he = resp.getEntity();
            result = EntityUtils.toString(he, "UTF-8");
            EntityUtils.consume(he);
        }
        resp.close();
        return result;
    }

    /**
     * 作者：hubo 日期：2017/7/13 17:32 描述：通过get请求发送json数据
     */
    public static String sendGetReturnAll(String url) throws Exception {
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建get方式请求对象
        HttpGet httpGet = new HttpGet(url);
        String result = null;
        CloseableHttpResponse resp = client.execute(httpGet);
        HttpEntity he = resp.getEntity();
        result = EntityUtils.toString(he, "UTF-8");
        EntityUtils.consume(he);
        resp.close();
        return result;
    }

    /**
     * 作者：hubo 日期：2017/8/9 14:23 描述：带header的post请求
     */
    public static String sendPost(String url, Object obj, String headerName, String headerValue) throws Exception {
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        String result = null;
        //解决中文乱码问题
        StringEntity entity = new StringEntity(JSON.toJSONString(obj), "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader(headerName, headerValue);
        CloseableHttpResponse resp = client.execute(httpPost);
        if (resp.getStatusLine().getStatusCode() == HTTP_SUCCESS_STATUS_CODE) {
            HttpEntity he = resp.getEntity();
            result = EntityUtils.toString(he, "UTF-8");
            EntityUtils.consume(he);
        }
        resp.close();
        return result;
    }

    /**
     * 作者：hubo 日期：2017/8/9 14:50 描述：带header的get请求
     */
    public static String sendGet(String url, String headerName, String headerValue) throws Exception {
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建get方式请求对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(headerName, headerValue);
        String result = null;
        CloseableHttpResponse resp = client.execute(httpGet);
        if (resp.getStatusLine().getStatusCode() == HTTP_SUCCESS_STATUS_CODE) {
            HttpEntity he = resp.getEntity();
            result = EntityUtils.toString(he, "UTF-8");
            EntityUtils.consume(he);
        }
        resp.close();
        return result;
    }

    /**
     * 普通post(表单方式)
     * 对面@requestparam  或者 实体类接受
     *
     * @return
     */
    public static String doPostMethed(String url, Map<String, String> paramMap) {
        String resp = "";
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        // 创建参数队列(client用request.getParameter或者String XXX都能取到)
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        if (paramMap != null) {
            Set<Map.Entry<String, String>> entries = paramMap.entrySet();
            for (Map.Entry<String, String> entry :
                    entries) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    resp = EntityUtils.toString(entity, "UTF-8");
                }
            } finally {
                response.close();
            }
            return resp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    /**
     * 作者：hubo 日期：2017/8/9 14:23 描述：带header的post请求
     */
    public static String sendPost2(String url, Object obj, String headerName, String headerValue) {
        CloseableHttpResponse resp = null;
        try {
            //创建httpclient对象
            CloseableHttpClient client = HttpClients.createDefault();
            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);
//            httpPost.addHeader("FORCE_LOG","111");
            String result = null;
            //解决中文乱码问题
            StringEntity entity = new StringEntity(JSON.toJSONString(obj), "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader(headerName, headerValue);
            resp = client.execute(httpPost);
//            log.info("HttpUtil resp:{}", resp.getStatusLine().getStatusCode());
            if (resp.getStatusLine().getStatusCode() == HTTP_SUCCESS_STATUS_CODE) {
                HttpEntity he = resp.getEntity();
                result = EntityUtils.toString(he, "UTF-8");
                EntityUtils.consume(he);
            }
            return result;
        } catch (Exception e) {
            String errorMsg = ExceptionUtil.processException(e);
//            LOGGER.error("发送POST请求失败url:{},req:{},ex:{}", url, JSON.toJSONString(obj), errorMsg);
        } finally {
            try {
                if (resp != null) {
                    resp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
