package com.littlemonkey.utils.http;

import com.alibaba.fastjson.JSON;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>http连接工具</p>
 */
public class HttpConnections {

    private static final Logger logger = LoggerFactory.getLogger(HttpConnections.class);

    private static ThreadLocal<HttpConnection> httpConnectionThreadLocal = new ThreadLocal<HttpConnection>();

    /**
     * <p>创建http连接</p>
     *
     * @param host 代理设置，null:不使用代理
     * @return
     * @throws Exception
     */
    public static HttpConnection createHttpConnection(HttpHost host) {
        try {
            CookieStore cookieStore = new BasicCookieStore();
            HttpConnection httpConnection = new HttpConnection(HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .setProxy(host)
                    .setHostnameVerifier(new AllowAllHostnameVerifier())
                    .setSslcontext(new SSLContextBuilder()
                            .loadTrustMaterial(null, new TrustStrategy() {
                                public boolean isTrusted(X509Certificate[] arg0, String arg1) {
                                    return Boolean.TRUE;
                                }
                            }).build()).build(), cookieStore);
            httpConnectionThreadLocal.set(httpConnection);
            return httpConnection;
        } catch (Exception e) {
            logger.error("create httpConnection error", e);
        }
        return null;
    }

    /**
     * <p>执行http请求</p>
     *
     * @param httpRequest
     * @return
     */
    public static String execute(HttpRequestBase httpRequest) {
        HttpClient httpClient = httpConnectionThreadLocal.get();
        if (httpClient == null) {
            throw new RuntimeException("before invoke method createHttpConnection.");
        }
        String entityStr = null;
        try {
            httpRequest.getParams().setParameter("http.protocol.allow-circular-redirects", Boolean.TRUE); //允许重定向
            logger.info("request url: {},method: {}", httpRequest.getRequestLine().getUri(), httpRequest.getMethod());
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                entityStr = EntityUtils.toString(entity, Consts.UTF_8);
                if (HttpStatus.SC_OK != statusCode) {
                    logger.error("server response error, code: {},result: {}", statusCode, entityStr);
                } else {
                    logger.info("server response success, code: {},result: {}", statusCode, entityStr);
                }
            }
            return entityStr;
        } catch (Exception e) {
            logger.error("execute httpClient request error", e);
        } finally {
            httpRequest.releaseConnection(); //关闭请求，节省资源
        }
        return entityStr;
    }

    private static void prepareHeaders(HttpRequestBase httpRequest, Map<String, String> headers) {
        for (String header : headers.keySet()) {
            httpRequest.setHeader(header, headers.get(header));
        }
    }

    public static String GET(String url, Map<String, String> param, Map<String, String> headers) {
        HttpGet httpGet = new HttpGet(URLUtils.join(url, param));
        prepareHeaders(httpGet, headers);
        logger.info("request body: {} ", param);
        return execute(httpGet);
    }

    public static String POST(String url, Object reqPayLoad, Map<String, String> headers) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        prepareHeaders(httpPost, headers);
        if (reqPayLoad != null) {
            String jsonStr = JSON.toJSONString(reqPayLoad);
            logger.info("request body: {} ", jsonStr);
            httpPost.setEntity(new StringEntity(jsonStr, ContentType.APPLICATION_JSON));
        }
        return execute(httpPost);
    }

    public String PUT(String url, Object reqPayLoad, Map<String, String> headers) throws Exception {
        HttpPut httpPut = new HttpPut(url);
        prepareHeaders(httpPut, headers);
        if (reqPayLoad != null) {
            String jsonStr = JSON.toJSONString(reqPayLoad);
            logger.info("request body: {} ", jsonStr);
            httpPut.setEntity(new StringEntity(jsonStr, Consts.UTF_8));
        }
        return execute(httpPut);
    }

    public String DELETE(String url, Map<String, String> queryParams, Map<String, String> headers) throws Exception {
        HttpDelete httpDelete = new HttpDelete(URLUtils.join(url, queryParams));
        prepareHeaders(httpDelete, headers);
        logger.info("request body: {} ", queryParams);
        return execute(httpDelete);
    }


    private static class HttpConnection implements HttpClient, CookieStore {
        private HttpClient httpClient;
        private CookieStore cookieStore;

        public HttpConnection(HttpClient httpClient, CookieStore cookieStore) {
            this.httpClient = httpClient;
            this.cookieStore = cookieStore;
        }

        public HttpClient getHttpClient() {
            return httpClient;
        }

        public void setHttpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
        }

        public CookieStore getCookieStore() {
            return cookieStore;
        }

        public void setCookieStore(CookieStore cookieStore) {
            this.cookieStore = cookieStore;
        }

        @Override
        public HttpParams getParams() {
            return httpClient.getParams();
        }

        @Override
        public ClientConnectionManager getConnectionManager() {
            return httpClient.getConnectionManager();
        }

        @Override
        public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
            return httpClient.execute(request);
        }

        @Override
        public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
            return httpClient.execute(request, context);
        }

        @Override
        public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
            return httpClient.execute(target, request);
        }

        @Override
        public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
            return httpClient.execute(target, request, context);
        }

        @Override
        public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
            return httpClient.execute(request, responseHandler);
        }

        @Override
        public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
            return httpClient.execute(request, responseHandler, context);
        }

        @Override
        public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
            return httpClient.execute(target, request, responseHandler);
        }

        @Override
        public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
            return httpClient.execute(target, request, responseHandler, context);
        }

        @Override
        public void addCookie(Cookie cookie) {
            cookieStore.addCookie(cookie);
        }

        @Override
        public List<Cookie> getCookies() {
            return cookieStore.getCookies();
        }

        @Override
        public boolean clearExpired(Date date) {
            return cookieStore.clearExpired(date);
        }

        @Override
        public void clear() {
            cookieStore.clear();
        }
    }
}

