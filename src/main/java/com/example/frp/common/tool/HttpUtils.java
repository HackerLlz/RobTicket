package com.example.frp.common.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.sun.deploy.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-14 17:25
 */
public class HttpUtils {
    public static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    public static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(1000)
            .setSocketTimeout(5000).build();

    /**
     * Get请求
     * @param url
     * @param paramsJson
     * @param withCookie
     * @return
     */
    public static String doGet(String url, String paramsJson, boolean withCookie) {
        logger.info("开始GET请求，入参：url：{}；paramsJson：{}；withCookie：{}", url, paramsJson, withCookie);
        // 设置证书
//        System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.8.0_181\\bin\\12306d.keystore");
//        ystem.setProperty("javax.net.ssl.trustStorePassword","12306java");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 发送时httpclient会自动编码
        HttpGet httpGet = new HttpGet(buildGetUrl(url, paramsJson));
        String result = send(httpGet, httpClient, withCookie);
        return result;
    }

    /**
     * Post请求，Form格式
     * @param url
     * @param data
     * @param withCookie
     * @return
     */
    public static String doPostForm(String url, String data, boolean withCookie){
        logger.info("开始Post请求，入参：url：{}；data：{}；withCookie：{}", url, data, withCookie);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        addPostForm(httpPost, data);
        String result = send(httpPost, httpClient, withCookie);
        return result;
    }

    /**
     * Post请求，Str格式
     * @param url
     * @param data
     * @param withCookie
     * @return
     */
    public static String doPostStr(String url, String data, boolean withCookie){
        logger.info("开始Post请求，入参：url：{}；data：{}；withCookie：{}", url, data, withCookie);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        addPostStr(httpPost, data);
        String result = send(httpPost, httpClient, withCookie);
        return result;
    }

    /**
     * 获得request
     * @return
     */
    public static HttpServletRequest getRequest() {
        // 上传的文件参数获取不到
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            return request;
        }
        return null;
    }

    /**
     * 获得response
     * @return
     */
    public static HttpServletResponse getReponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletResponse response = servletRequestAttributes.getResponse();
            return response;
        }
        return null;
    }

    private static String send(HttpRequestBase httpRequestBase, CloseableHttpClient httpClient, boolean withCookie) {
        // 设置请求超时时间
        httpRequestBase.setConfig(requestConfig);
        if (withCookie) {
            // 添加cookie头
            addHeaders(httpRequestBase, buildCookieJson());
            return sendRequestWithCookie(httpRequestBase, httpClient);
        }
        return sendRequest(httpRequestBase, httpClient);
    }

    private static String sendRequest(HttpRequestBase httpRequestBase, CloseableHttpClient httpClient) {
        InputStream is = null;
        String result = "";
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpRequestBase);
            // 获取返回结果
            HttpEntity httpEntity = httpResponse.getEntity();
            if(httpEntity != null) {
                is = httpEntity.getContent();
                result = IOUtils.inputStreamToString(is);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
        }
        if (result.startsWith("{")) {
            logger.info("完成请求，result：{}", result);
        } else {
//            logger.info("完成请求，result：html页面");
            logger.info("完成请求，result：{}", result);
        }
        return result;
    }

    private static String sendRequestWithCookie(HttpRequestBase httpRequestBase, CloseableHttpClient httpClient) {
        InputStream is = null;
        String result = "";
        // 获取HttpClient上下文
        HttpClientContext context = getContextWithCookieStore();
        try {
            // 发送请求，将返回的cookie存在上下文中的cookieStore中
            CloseableHttpResponse httpResponse = httpClient.execute(httpRequestBase, context);
            // 重新添加Set-Cookie, 使set-cookie路径为/
            addSetCookie(context.getCookieStore());
//            Header[] setCookies = httpResponse.getHeaders("Set-Cookie");     // 该方法不好改变cookie的Path，使得前端无法再带回来
            // 获取返回结果
            HttpEntity httpEntity = httpResponse.getEntity();
            if(httpEntity != null) {
                is = httpEntity.getContent();
                result = IOUtils.inputStreamToString(is);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
        }
        if (result.startsWith("{")) {
            logger.info("完成请求带cookie，result：{}", result);
        } else {
            logger.info("完成请求带cookie，result：html页面");
        }
        return result;
    }

    private static void addPostForm(HttpPost httpPost, String data) {
        if (!ObjectUtils.isEmpty(data)) {
            if (data.startsWith("{")) {
                addPostFormByJson(httpPost, data);
            } else {
                addPostFormByStr(httpPost, data);
            }
        }
    }

    private static void addPostFormByJson(HttpPost httpPost, String json) {
        //建立一个NameValuePair数组，用于存储欲传送的参数
        List<NameValuePair> params = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(json);
        if (!ObjectUtils.isEmpty(jsonObject)) {
            for (Map.Entry<String, Object> o : jsonObject.entrySet()) {
                if (o.getValue() instanceof String) {
                    // 不能编码！！！一般情况没问题，但是如果有","等符号的话，两次编码就会有问题
                    params.add(new BasicNameValuePair(o.getKey(), (String) o.getValue()));
                    logger.info("已添加ValuePair参数 {}：{}", o.getKey(), o.getValue());
                }
            }
            try {
                //添加post数据体
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
                httpPost.setEntity(formEntity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addPostFormByStr(HttpPost httpPost, String str) {
        StringEntity strEntity = null;
        try {
            // 不能编码。不然"="会被编码 会出错。form-url不解码，直接传就行
            // @RequestBody能读json和form-url，区别是json会解码，form-url不解码
            strEntity = new StringEntity(str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //设置为form表单数据体
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        //添加post数据体
        httpPost.setEntity(strEntity);
        logger.info("已添加PostForm参数 str：{}", str);
    }

    private static void addPostStr(HttpPost httpPost, String data) {
        StringEntity strEntity = null;
        try {
            strEntity = new StringEntity(URLEncoder.encode(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //设置为json数据体(字符串应该也适用)
        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
        //添加post数据体
        httpPost.setEntity(strEntity);
        try {
            logger.info("已添加PostStr参数 str：{}", URLEncoder.encode(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void addHeaders(HttpRequestBase httpRequestBase, String json) {
        if (!ObjectUtils.isEmpty(json)){
            JSONObject jsonObject = JSON.parseObject(json);
            if (!ObjectUtils.isEmpty(jsonObject)){
                for (Map.Entry<String, Object> o : jsonObject.entrySet()) {
                    if (o.getValue() instanceof String) {
                        httpRequestBase.setHeader(o.getKey(), (String)o.getValue());
                        logger.info("已添加头参数 {}：{}", o.getKey(), o.getValue());
                    }
                }
            }
        }
    }

    private static String buildGetUrl(String url, String json) {
        if (!ObjectUtils.isEmpty(json)){
            // Feature.OrderedField用来保证json转化成对象时的字段顺序(应该是使Hash值有序)，12306的url查询需要顺序一样才能查。。
            // HashSet底层是HashMap实现的，只是不能重复，输出顺序是根据HashCode所以是固定的，之前说的无序指的是添加顺序和输出顺序不一样
            JSONObject jsonObject = JSON.parseObject(json, Feature.OrderedField);
            if (!ObjectUtils.isEmpty(jsonObject)){
                StringBuffer sb = new StringBuffer(url);
                boolean first = true;
                for (Map.Entry<String, Object> o : jsonObject.entrySet()) {
                    if (o.getValue() instanceof String) {
                        String value = null;
                        try {
                            value = URLEncoder.encode((String)o.getValue(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if(first){
                            sb.append("?").append(o.getKey()).append("=").append(value);
                            first = false;
                        }
                        else {
                            sb.append("&").append(o.getKey()).append("=").append(value);
                        }
                    }
                }
                logger.info("Url组装完成：{}", sb.toString());
                return sb.toString();
            }
        }
        return url;
    }

    private static String buildCookieJson() {
        HttpServletRequest request = getRequest();
        String cookie = null;
        if (!ObjectUtils.isEmpty(request)) {
            cookie = request.getHeader("cookie");
        } else {
            cookie = ThreadLocalUtils.get();
        }
        cookie = "{\"Cookie\":\"" + cookie + "\"}";
        return cookie;
    }

    private static void addSetCookie(CookieStore cookieStore) {
        HttpServletResponse response = getReponse();
        if (!ObjectUtils.isEmpty(response)) {
            for (Cookie c : cookieStore.getCookies()) {
                String setCookie = c.getName() + "=" + c.getValue() + "; Path=/; MaxAge=1800";    // path是必须的
                response.addHeader("Set-Cookie", setCookie);
                logger.info("添加Set-Cookie：{}", setCookie);
            }
        }
    }

    private static HttpClientContext getContextWithCookieStore() {
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();
        // 加入cookie store的本地实例
        CookieStore cookieStore =  new BasicCookieStore();
        context.setCookieStore(cookieStore);
        return context;
    }
}
