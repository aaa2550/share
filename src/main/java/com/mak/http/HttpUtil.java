package com.mak.http;

import com.mak.dto.ProxyInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * http工具类
 * @author sxf
 *
 */
public class HttpUtil {

    private final CloseableHttpClient httpclient;
    private static final int SIZE = 1024 * 1024;
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private static class HttpUtilHolder {
        private static final HttpUtil INSTANCE = new HttpUtil();
    }

    public static HttpUtil getIntance() {
        return HttpUtilHolder.INSTANCE;
    }

	private HttpUtil() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		// 将最大连接数增加到300
		cm.setMaxTotal(1000);
		// 将每个路由基础的连接增加到100
		cm.setDefaultMaxPerRoute(300);
		// 链接超时setConnectTimeout ，读取超时setSocketTimeout
		RequestConfig defaultRequestConfig = null;
        defaultRequestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();

		httpclient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(defaultRequestConfig)
				.build();

        new IdleConnectionMonitorThread(cm).start();
	}

    /**
     * 编码默认UTF-8
     * @param url
     * @return
     */
    public String get(String url) {
        return this.get(url, CHARSET_UTF8.toString());
    }

    /**
     * @param url
     * @param code
     * @return
     */
    public String get(String url, final String code) {
        String res = null;

        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, code) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            res = httpclient.execute(httpget, responseHandler);
        } catch (Exception e) {
            logger.error("url:{}",url, e);
            throw new RuntimeException(e.getMessage() + "");
        }
        return res;
    }

    /**
     * @param url
     * @param code
     * @return
     */
    public String proxyGet(String url, final String code, ProxyInfo proxyInfo) throws TimeoutException {
        String res = null;

        try {
            HttpGet httpget = new HttpGet(url);
            HttpHost proxy = new HttpHost(proxyInfo.getIp(), Integer.parseInt(proxyInfo.getPort()));
            RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
            httpget.setConfig(config);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, code) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            res = httpclient.execute(httpget, responseHandler);
        } catch (Exception e) {
            //logger.error("url:{}" + url, e);
            throw new TimeoutException(e.getMessage() + "");
        }
        return res;
    }

    public static String httpsGet(String url) {
        String res = null;
        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, CHARSET_UTF8.toString()) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            res = MySSLSocketFactory.getNewHttpClient().execute(httpget, responseHandler);
        } catch (Exception e) {
            logger.error("url:{}",url, e);
        }
        return res;
    }

    public List<String> getList(String url) {
        List<String> res = null;
        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<List<String>> responseHandler = new ResponseHandler<List<String>>() {
                @Override
                public List<String> handleResponse(final HttpResponse response) throws ClientProtocolException,
                        IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        List<String> result = new ArrayList<String>();
                        HttpEntity entity = response.getEntity();
                        if (entity == null) {
                            return result;
                        }
                        BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()), SIZE);
                        while (true) {
                            String line = in.readLine();
                            if (line == null) {
                                break;
                            } else {
                                result.add(line);
                            }
                        }
                        in.close();
                        return result;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            res = httpclient.execute(httpget, responseHandler);
        } catch (Exception e) {
            logger.error(url, e);
        }
        return res;
    }

    private String post(String url, List<NameValuePair> params, String code) {
        String res = null;
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            if (params != null)
                httpPost.setEntity(new UrlEncodedFormEntity(params, code));
            response = httpclient.execute(httpPost);
            HttpEntity entity2 = response.getEntity();
            res = EntityUtils.toString(entity2, code);
            EntityUtils.consume(entity2);
        } catch (Exception e) {
            logger.error(url, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return res;
    }

    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    public String postJSON(String url, String json, String code) {
      String res = null;
      CloseableHttpResponse response = null;
      try {
          HttpPost httpPost = new HttpPost(url);
          httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
          
          StringEntity se = new StringEntity(json);
          se.setContentType(CONTENT_TYPE_TEXT_JSON);
          se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
          httpPost.setEntity(se);
          response = httpclient.execute(httpPost);
          HttpEntity entity2 = response.getEntity();
          res = EntityUtils.toString(entity2, code);
          EntityUtils.consume(entity2);
      } catch (Exception e) {
          logger.error(url, e);
      } finally {
          if (response != null) {
              try {
                  response.close();
              } catch (IOException e) {
              }
          }
      }
      return res;
  }

    
    
    /**
     * 默认UTF-8
     * @param url
     * @param params
     * @return
     */
    public String post(String url, Map<String, ?> params) {
        return this.post(url, params, CHARSET_UTF8.toString());
    }

    /**
     * @param url{
     * @param params
     * @param code
     * @return
     */
    public String post(String url, Map<String, ?> params, String code) {
        List<NameValuePair> nvps = null;
        if (params != null && params.size() > 0) {
            nvps = new ArrayList<NameValuePair>();
            for (Entry<String, ?> entry : params.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }
        return this.post(url, nvps, code);
    }

    // 监控有异常的链接
    private static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // 关闭失效的连接
                        connMgr.closeExpiredConnections();
                        // 可选的, 关闭30秒内不活动的连接
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void download(String url, String outFile) {
        try {
            InputStream inputStream = getInputStream(url);
            FileCopyUtils.copy(inputStream, new FileOutputStream(outFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream(String url) {
        try {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<InputStream> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? entity.getContent() : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            return httpclient.execute(httpget, responseHandler);
        } catch (Exception e) {
            logger.error("url:{}",url, e);
        }
        return null;
    }

    public String postBody(String url, String body) {
        String res = null;
        try {
            HttpPost httppost = new HttpPost(url);
            if (!StringUtils.isEmpty(body)) {
                httppost.setEntity(new StringEntity(body, CHARSET_UTF8));
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                res = EntityUtils.toString(resEntity, CHARSET_UTF8);
                EntityUtils.consume(resEntity);
            }
        } catch (Exception e) {
            logger.error(url, e);
        }

        return res;
    }
}