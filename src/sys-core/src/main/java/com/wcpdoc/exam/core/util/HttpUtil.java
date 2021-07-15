package com.wcpdoc.exam.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.wcpdoc.exam.core.exception.HttpRequestException;

/**
 * http工具类
 * 
 * v1.0 zhanghc 2019年11月4日下午5:01:44
 */
public class HttpUtil {
	private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();// cpu数量
	private static final int MAX_THREAD_TOTAL = CPU_NUM * 10;// 线程池最大数量
	private static final int MAX_PER_ROUTE = 10;// 最大经过路由数量
	private static final int SOCKET_TIMEOUT = 6 * 1000;// socket超时时间，单位毫秒
	private static final int CONNECT_TIMEOUT = 6 * 1000;// 连接超时时间，单位毫秒
	private static final int CONNECTION_REQUEST_TIMEOUT = 6 * 1000;// 连接请求超时时间，单位毫秒

	private static PoolingHttpClientConnectionManager CONN_MANAGER = null;
	private static RequestConfig REQUEST_CONFIG = null;
	private static CloseableHttpClient HTTP_CLIENT = null;

	static {
		REQUEST_CONFIG = RequestConfig.custom()//
				.setSocketTimeout(SOCKET_TIMEOUT)//
				.setConnectTimeout(CONNECT_TIMEOUT)//
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)//
				.build();

		CONN_MANAGER = new PoolingHttpClientConnectionManager();
		CONN_MANAGER.setMaxTotal(MAX_THREAD_TOTAL);
		CONN_MANAGER.setDefaultMaxPerRoute(MAX_PER_ROUTE);
		HTTP_CLIENT = HttpClients.custom()//
				.setConnectionManager(CONN_MANAGER)//
				.build();
	}

	/**
	 * get请求
	 * 
	 * v1.0 zhanghc 2019年11月4日下午7:05:44
	 * @param url
	 * @param params
	 * @return String
	 * @throws HttpRequestException
	 */
	public static final String get(String url, Map<String, Object> params, String charset) throws HttpRequestException {
		try {
 			URIBuilder uriBuilder = new URIBuilder();
			if (params != null) {
				for (Entry<String, Object> entry : params.entrySet()) {
					if (entry.getValue() == null) {
						uriBuilder.addParameter(entry.getKey(), null);
						continue;
					}
					uriBuilder.addParameter(entry.getKey(), entry.getValue().toString());
				}
			}

			String _params = uriBuilder.build().toString();
			HttpGet httpGet = new HttpGet(url + _params);
			httpGet.setConfig(REQUEST_CONFIG);
			CloseableHttpResponse execute = HTTP_CLIENT.execute(httpGet);
			HttpEntity httpEntity = execute.getEntity();
			return EntityUtils.toString(httpEntity, charset);
		} catch (Exception e) {
			throw new HttpRequestException(e.getMessage());
		}
	}
	
	/**
	 * get请求
	 * 
	 * v1.0 zhanghc 2019年11月4日下午7:05:44
	 * @param url
	 * @param params
	 * @return String
	 * @throws HttpRequestException
	 */
	public static final String get(String url) throws HttpRequestException {
		return get(url, "utf-8");
	}
	
	/**
	 * get请求
	 * 
	 * v1.0 zhanghc 2019年11月4日下午7:05:44
	 * @param url
	 * @param params
	 * @return String
	 * @throws HttpRequestException
	 */
	public static final String get(String url, String charset) throws HttpRequestException {
		return get(url, null, charset);
	}
	
	/**
	 * post请求
	 * 
	 * v1.0 zhanghc 2019年11月4日下午7:08:39
	 * @param url
	 * @param params
	 * @param charset
	 * @return String
	 * @throws HttpRequestException 
	 */
	public static final String post(String url, Map<String, Object> params, String charset) throws HttpRequestException {
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(REQUEST_CONFIG);
			List<BasicNameValuePair> paramList = new ArrayList<BasicNameValuePair>(0);
			if (params != null) {
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					if (entry.getValue() == null) {
						paramList.add(new BasicNameValuePair(entry.getKey(), null));
						continue;
					}
					
					paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
				}
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, charset);
			httpPost.setEntity(entity);
       		CloseableHttpResponse execute = HTTP_CLIENT.execute(httpPost);
			HttpEntity httpEntity = execute.getEntity();
			return EntityUtils.toString(httpEntity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new HttpRequestException(e.getMessage());
		}
	}
	
	public static final String post(String url, String params, String charset) throws HttpRequestException {
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(REQUEST_CONFIG);
			StringEntity entity = new StringEntity(params, charset);
			httpPost.setEntity(entity);
			CloseableHttpResponse execute = HTTP_CLIENT.execute(httpPost);
			HttpEntity httpEntity = execute.getEntity();
			return EntityUtils.toString(httpEntity, charset);
		} catch (Exception e) {
			throw new HttpRequestException(e.getMessage());
		}
	}
	
	public static final byte[] postByte(String url, String params, String charset) throws HttpRequestException {
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(REQUEST_CONFIG);
			StringEntity entity = new StringEntity(params, charset);
			httpPost.setEntity(entity);
			CloseableHttpResponse execute = HTTP_CLIENT.execute(httpPost);
			HttpEntity httpEntity = execute.getEntity();
			return EntityUtils.toByteArray(httpEntity);
		} catch (Exception e) {
			throw new HttpRequestException(e.getMessage());
		}
	}
	
	public static final byte[] getByte(String url) throws HttpRequestException {
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setConfig(REQUEST_CONFIG);
			CloseableHttpResponse execute = HTTP_CLIENT.execute(httpGet);
			HttpEntity httpEntity = execute.getEntity();
			return EntityUtils.toByteArray(httpEntity);
		} catch (Exception e) {
			throw new HttpRequestException(e.getMessage());
		}
	}
}