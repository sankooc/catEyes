package org.cateyes.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ApacheConnector {
	final DefaultHttpClient client;
	ExecutorService executor;

	public ApacheConnector() {
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(20);
		cm.setDefaultMaxPerRoute(10);
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000000);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 3000000);
		params.setParameter(CoreConnectionPNames.TCP_NODELAY, false);

		client = new DefaultHttpClient(cm, params);

		HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= 5) {
					// 超过重试次数
					return false;
				}
				if (exception instanceof ConnectionPoolTimeoutException) {
					return false;
				}
				if (exception instanceof SSLHandshakeException) {
					return false;
				}
				return true;
			}
		};
		client.setHttpRequestRetryHandler(retryHandler);
		executor = Executors.newCachedThreadPool();
	}

	public byte[] doGet(URI uri) {
		HttpGet request = new HttpGet(uri);
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(entity.getContent(), baos);
				request.abort();
				EntityUtils.consume(entity);
				return baos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document getPage(String addr) {
		return getPage(URI.create(addr));
	}

	public Document getPage(URI uri) {
		HttpGet request = new HttpGet(uri);
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		HttpEntity entity = response.getEntity();
		try {
			if (null != entity) {
				Document document = Jsoup.parse(entity.getContent(), "UTF-8",
						uri.toString());
				return document;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			request.abort();
			try {
				EntityUtils.consume(entity);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public void doGet(final URI uri, final ContentComsumer consumer) {
		executor.execute(new Runnable() {
			public void run() {
				HttpGet request = new HttpGet(uri);
				try {
					HttpResponse response = client.execute(request);
					HttpEntity entity = response.getEntity();
					if (null != entity) {
						consumer.consume(entity.getContent());
						request.abort();
						EntityUtils.consume(entity);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public interface ContentComsumer {
		public void consume(InputStream content) throws Exception;
	}
}
