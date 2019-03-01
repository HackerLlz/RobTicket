package com.duriamuk.robartifact.common.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-14 17:25
 * 1.支持有无携带cookie的浏览器代理；
 * 2.支持模拟浏览器提交过程：
 * 1.存在request和response时，提供public方法进行手动更新cookie，
 * 2.反之，会从ThreadLocal中自动更新cookie；
 */
public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final int CONNECT_TIMEOUT;
    public static final int CONNECTION_REQUEST_TIMEOUT;
    public static final int SOCKET_TIMEOUT;
    public static final int MAX_TOTAL;
    public static final int DEFAULT_MAX_PER_ROUTE;
    public static final int MAX_IDLE_TIME;
    public static final int TIME_TO_LIVE;
    public static final boolean IS_MANAGER_SHARED;
    // 超时信息配置
    private static final RequestConfig requestConfig;
    // 连接池管理对象
    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    // 单例客户端
    private static final CloseableHttpClient httpClient;

    private static final Properties properties = new Properties();

    static {
        InputStream in = HttpUtils.class.getClassLoader().getResourceAsStream("META-INF/conf/httpClient.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CONNECT_TIMEOUT = Integer.valueOf(properties.getProperty("connectTimeout"));
        CONNECTION_REQUEST_TIMEOUT = Integer.valueOf(properties.getProperty("connectionRequestTimeout"));
        SOCKET_TIMEOUT = Integer.valueOf(properties.getProperty("socketTimeout"));
        MAX_TOTAL = Integer.valueOf(properties.getProperty("maxTotal"));
        DEFAULT_MAX_PER_ROUTE = Integer.valueOf(properties.getProperty("defaultMaxPerRoute"));
        MAX_IDLE_TIME = Integer.valueOf(properties.getProperty("maxIdleTime"));
        TIME_TO_LIVE = Integer.valueOf(properties.getProperty("timeToLive"));
        IS_MANAGER_SHARED = Boolean.valueOf(properties.getProperty("isManagerShared"));
    }

    static {
        // 设置最大连接数
        connManager.setMaxTotal(MAX_TOTAL);
        // 设置每个连接的路由数
        connManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        //设置到某个路由的最大连接数，会覆盖defaultMaxPerRoute
        connManager.setMaxPerRoute(new HttpRoute(new HttpHost("kyfw.12306.cn", 80)), MAX_TOTAL);

        requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT).build();

        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig) // 设置超时信息
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)) // 设置重试次数，默认3次 此处禁用
                .setConnectionManager(connManager) // 设置连接池管理对象
                .setConnectionManagerShared(IS_MANAGER_SHARED) // 连接池是否共享模式
                .evictIdleConnections(MAX_IDLE_TIME, TimeUnit.SECONDS) // 定期回收空闲连接
                .evictExpiredConnections() // 定期回收过期连接
                .setConnectionTimeToLive(TIME_TO_LIVE, TimeUnit.SECONDS) // 连接存活时间，如果不设置，则根据长连接信息决定，没信息则会 -1代表永久
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE) // 连接重用策略 是否能keepAlive
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE) // 长连接配置，即获取长连接生产多长时间
                .build();
    }

    /**
     * Get请求
     *
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
//        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpClient httpClient = getHttpClient();
        // 发送时httpclient会自动编码
        HttpGet httpGet = new HttpGet(buildGetUrl(url, paramsJson));
        String result = send(httpGet, httpClient, withCookie);
        return result;
    }

    /**
     * Post请求，Form格式
     *
     * @param url
     * @param data
     * @param withCookie
     * @return
     */
    public static String doPostForm(String url, String data, boolean withCookie) {
        logger.info("开始Post请求，入参：url：{}；data：{}；withCookie：{}", url, data, withCookie);
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        addPostForm(httpPost, data);
        String result = send(httpPost, httpClient, withCookie);
        return result;
    }

    /**
     * Post请求，Str格式
     *
     * @param url
     * @param data
     * @param withCookie
     * @return
     */
    public static String doPostString(String url, String data, boolean withCookie) {
        logger.info("开始Post请求，入参：url：{}；data：{}；withCookie：{}", url, data, withCookie);
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        addPostStr(httpPost, data);
        String result = send(httpPost, httpClient, withCookie);
        return result;
    }

    /**
     * Post请求，发送流文件
     *
     * @param url
     * @param name
     * @param stream
     * @param withCookie
     * @return
     */
    public static String doPostStream(String url, String name, InputStream stream, boolean withCookie) {
        logger.info("开始Post请求，入参：url：{}；stream：{}；withCookie：{}", url, stream, withCookie);
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        addPostStream(httpPost, name, stream);
        String result = send(httpPost, httpClient, withCookie);
        return result;
    }

    /**
     * 获得request
     *
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
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletResponse response = servletRequestAttributes.getResponse();
            return response;
        }
        return null;
    }

    /**
     * 将response的setCookie添加到request的cookie中，模拟浏览器cookie传递
     * 修改request 的 setAttribute, HttpUtils 先取 getAttribute中的cookie
     */
    public static void addResponseSetCookieToRequestCookie() {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        if (!ObjectUtils.isEmpty(request) && !ObjectUtils.isEmpty(response)) {
            String cookie = (String) request.getAttribute(COOKIE);
            cookie = !StringUtils.isEmpty(cookie) ? cookie : request.getHeader(COOKIE);
            for (String setCookie : response.getHeaders(SET_COOKIE)) {
                String name = StrUtils.reverseFindVlaue("=", "", 0, null, setCookie);
                String value = StrUtils.findVlaue("=", "", 0, ";", setCookie);
                cookie = CookieUtils.updateCookieStr(cookie, name, value);
            }
            request.setAttribute(COOKIE, cookie);
            logger.info("已完成添加Set-Cookie到Cookie：{}", request.getAttribute(COOKIE));
        }
    }

    /**
     * 更改request中的cookie,
     * 设置request 的 setAttribute，HttpUtils 先取 getAttribute中的cookie
     *
     * @param name
     * @param value
     */
    public static void updateRequestCookie(String name, String value) {
        HttpServletRequest request = getRequest();
        if (!ObjectUtils.isEmpty(request)) {
            String cookie = (String) request.getAttribute(COOKIE);
            cookie = !StringUtils.isEmpty(cookie) ? cookie : request.getHeader(COOKIE);
            cookie = CookieUtils.updateCookieStr(cookie, name, value);
            request.setAttribute(COOKIE, cookie);
            logger.info("已完成添加cookie：{}", request.getAttribute(COOKIE));
        }
    }

    /**
     * 清空request中的cookie
     */
    public static void clearRequestCookie() {
        HttpServletRequest request = getRequest();
        if (!ObjectUtils.isEmpty(request)) {
            request.setAttribute(COOKIE, "cookie=null");
            logger.info("已清空cookie");
        }
    }

    private static CloseableHttpClient getHttpClient() {
        // 返回的单例HttpClient线程安全
        return httpClient;
    }

    private static String send(HttpRequestBase httpRequestBase, CloseableHttpClient httpClient, boolean withCookie) {
        if (withCookie) {
            // 添加cookie头
            addHeaders(httpRequestBase, buildCookieJson());
            return sendRequestWithCookie(httpRequestBase, httpClient);
        }
        return sendRequest(httpRequestBase, httpClient);
    }

    private static String sendRequest(HttpRequestBase httpRequestBase, CloseableHttpClient httpClient) {
        InputStream is = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        try {
            httpResponse = httpClient.execute(httpRequestBase);
            // 获取返回结果
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                is = httpEntity.getContent();
                result = IOUtils.inputStreamToString(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(is, httpResponse);
        }
        logger.info("完成请求，result：{}", result.startsWith("{") ? result : "html页面");
        return result;
    }

    private static String sendRequestWithCookie(HttpRequestBase httpRequestBase, CloseableHttpClient httpClient) {
        InputStream is = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 获取HttpClient上下文
        HttpClientContext context = getContextWithCookieStore();
        try {
            // 发送请求，将返回的cookie存在上下文中的cookieStore中
            httpResponse = httpClient.execute(httpRequestBase, context);
            // 重新添加Set-Cookie, 使set-cookie路径为/
            addSetCookie(context.getCookieStore());
//            Header[] setCookies = httpResponse.getHeaders("Set-Cookie");     // 该方法不好改变cookie的Path，使得前端无法再带回来
            // 获取返回结果
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                is = httpEntity.getContent();
                result = IOUtils.inputStreamToString(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(is, httpResponse);
        }
        logger.info("完成请求，result：{}", result.startsWith("{") ? result : "html页面");
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

    private static void addPostStream(HttpPost httpPost, String name, InputStream stream) {
        HttpEntity entity = MultipartEntityBuilder
                .create()
                .setCharset(Charset.forName("UTF-8"))
                .addBinaryBody(name, stream)
                .build();
        httpPost.setEntity(entity);
    }

    private static void addPostFormByJson(HttpPost httpPost, String json) {
        // 建立一个NameValuePair数组，用于存储欲传送的参数
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
                // 添加post数据体
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
        // 设置为form表单数据体
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        // 添加post数据体
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
        // 设置为json数据体(字符串应该也适用)
        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
        // 添加post数据体
        httpPost.setEntity(strEntity);
        try {
            logger.info("已添加PostStr参数 str：{}", URLEncoder.encode(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void addHeaders(HttpRequestBase httpRequestBase, String json) {
        if (!ObjectUtils.isEmpty(json)) {
            JSONObject jsonObject = JSON.parseObject(json);
            if (!ObjectUtils.isEmpty(jsonObject)) {
                for (Map.Entry<String, Object> o : jsonObject.entrySet()) {
                    if (o.getValue() instanceof String) {
                        httpRequestBase.setHeader(o.getKey(), (String) o.getValue());
                        logger.info("已添加头参数 {}：{}", o.getKey(), o.getValue());
                    }
                }
            }
        }
    }

    private static String buildGetUrl(String url, String json) {
        if (!ObjectUtils.isEmpty(json)) {
            // Feature.OrderedField用来保证json转化成对象时的字段顺序(应该是使Hash值有序)，12306的url查询需要顺序一样才能查。。
            // HashSet底层是HashMap实现的，只是不能重复，输出顺序是根据HashCode所以是固定的，之前说的无序指的是添加顺序和输出顺序不一样
            JSONObject jsonObject = JSON.parseObject(json, Feature.OrderedField);
            if (!ObjectUtils.isEmpty(jsonObject)) {
                StringBuffer sb = new StringBuffer(url);
                boolean first = true;
                for (Map.Entry<String, Object> o : jsonObject.entrySet()) {
                    if (o.getValue() instanceof String) {
                        String value = null;
                        try {
                            value = URLEncoder.encode((String) o.getValue(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (first) {
                            sb.append("?").append(o.getKey()).append("=").append(value);
                            first = false;
                        } else {
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
            // request不能设置Header，只能设置Attribute，
            // 所以先设置 Attribute，再来更改HttpRequestBase的Header
            String cookieInAttribute = (String) request.getAttribute(COOKIE);
            if (!StringUtils.isEmpty(cookieInAttribute)) {
                cookie = cookieInAttribute;
                logger.info("从Attribute中获得cookie：{}", cookie);
            } else {
                cookie = request.getHeader(COOKIE);
                logger.info("从Header中获得cookie：{}", cookie);
            }
        } else {
            cookie = ThreadLocalUtils.get();
            logger.info("从ThreadLocal中获得cookie：{}", cookie);
        }
        cookie = "{\"" + COOKIE + "\":\"" + cookie + "\"}";
        return cookie;
    }

    private static void addSetCookie(CookieStore cookieStore) {
        HttpServletResponse response = getResponse();
        if (!ObjectUtils.isEmpty(response)) {
            setCookieInResponse(cookieStore);
        } else {
            setCookieInThreadLocal(cookieStore);
        }
    }

    private static void setCookieInResponse(CookieStore cookieStore) {
        for (Cookie c : cookieStore.getCookies()) {
            CookieUtils.setCookie(c.getName(), c.getValue());
        }
    }

    private static void setCookieInThreadLocal(CookieStore cookieStore) {
        String cookie = ThreadLocalUtils.get();
        for (Cookie c : cookieStore.getCookies()) {
            cookie = CookieUtils.updateCookieStr(cookie, c.getName(), c.getValue());
        }
        ThreadLocalUtils.set(cookie);
    }

    private static HttpClientContext getContextWithCookieStore() {
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();
        // 加入cookie store的本地实例
        CookieStore cookieStore = new BasicCookieStore();
        context.setCookieStore(cookieStore);
        return context;
    }

    private static void closeResources(InputStream is, CloseableHttpResponse httpResponse) {
        try {
            // 不关闭httpRequestBase，连接池重用
            if (is != null) {
                is.close();
            }
            if (httpResponse != null) {
                httpResponse.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
