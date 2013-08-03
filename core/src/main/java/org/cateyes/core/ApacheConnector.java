package org.cateyes.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.cateyes.core.util.CommonUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApacheConnector {
	final DefaultHttpClient client;
	ExecutorService executor;
	static Logger logger = LoggerFactory.getLogger(ApacheConnector.class);

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

	protected void swap(HttpMessage request){
		request.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
	}
	
	
	public byte[] doGet(URI uri) {
		HttpGet request = new HttpGet(uri);
		try {
			swap(request);
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

	final MResource cont = new MResource() {
		public void init() {
			logger.debug("init");
		}

		public void start() {
			logger.debug("start");

		}

		public void error(String msg) {
			// TODO Auto-generated method stub

		}

		public void finish() {
			logger.debug("finish");

		}

		volatile long len;
		volatile long total;

		public void setLength(long size) {
			logger.debug("total length is {}", size);
			total = size;

		}

		public void display(long content) {
			int m = 0;
			int k = 0;
			int b = 0;
			b = (int) (content & 0x02ffl);
			content = content >> 10;
			if (content > 0) {
				k = (int) (content & 0x02ffl);
				content = content >> 10;
			}
			if (content > 0) {
				m = (int) (content & 0x02ffl);
				content = content >> 10;
			}
			b = (int) (len & 0x02ffl);
			logger.debug("current length {}MB {}KB {}B ", new Object[] { m, k,
					b });
		}

		public synchronized void setContent(long content) {
			len = content;
		}

		int timer = 0;

		public synchronized void addContent(long increase) {
			len += increase;
			if (timer-- < 0) {
				display(len);
				timer = 50;
			}
		}

		public boolean isError() {
			// TODO Auto-generated method stub
			return false;
		}
	};

	public void download(final URI uri, File file, Adaptor adaptor)
			throws Exception {
		long size = 0;
		OutputStream out = null;
		file.getParentFile().mkdirs();
		if (file.exists()) {
			size = file.length();
			out = new FileOutputStream(file, true);
		} else {
			out = new FileOutputStream(file);
		}
		download(uri, out, size, adaptor);
		out.close();
	}

	public void download(final URI uri, OutputStream out, long size,
			Adaptor adaptor) throws Exception {
		long totalLenth = 0;

		MResource control = null;
		if (null != adaptor) {
			control = adaptor.getAdaptor(MResource.class);
		}
		if (null == control) {
			control = cont;
		}
		control.init();
		long begin = System.currentTimeMillis();
		HttpUriRequest request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		if (null != entity) {
			totalLenth = entity.getContentLength();
			control.setLength(totalLenth);
			EntityUtils.consume(entity);
			logger.info("target resource length {}", totalLenth);
		}
		request.abort();
		long end = System.currentTimeMillis();

		logger.info("spend time {}", (end - begin));
		if (totalLenth == 0) {
			control.error("no resource");
			throw new RuntimeException("no resource");
		}

		if (totalLenth == size) {
			logger.info("downloaded");
			control.finish();
			return;
		}
		control.start();
		request = new HttpGet(uri);
		if (size < 1) {
			// control.setPercent(0);
			control.setContent(0);
			response = client.execute(request);
			if (logger.isDebugEnabled()) {
				logger.debug("response code {}", response.getStatusLine()
						.getStatusCode());
				Header[] headers = response.getAllHeaders();
				if (null != headers && headers.length != 0) {
					for (Header head : headers) {
						logger.info("response headers key[{}] - value[{}]",
								head.getName(), head.getValue());
					}
				}
			}
			entity = response.getEntity();
			if (null != entity) {
				CommonUtils.copyStream(entity.getContent(), out, control);
				EntityUtils.consume(entity);
			}
			request.abort();
		} else {
			control.setContent(size);
			swap(request);
			request.addHeader("Range", "bytes=" + size + "-");
			response = client.execute(request);
			if (logger.isDebugEnabled()) {
				logger.debug("response code {}", response.getStatusLine()
						.getStatusCode());
				Header[] headers = response.getAllHeaders();
				if (null != headers && headers.length != 0) {
					for (Header head : headers) {
						logger.info("response headers key[{}] - value[{}]",
								head.getName(), head.getValue());
					}
				}
			}
			entity = response.getEntity();
			logger.info("headlength {} ", entity.getContentLength());
			if (null != entity) {
				CommonUtils.copyStream(entity.getContent(), out, control);
				EntityUtils.consume(entity);
			}
			request.abort();
		}
		control.finish();
	}

	public void download(final HttpUriRequest request, OutputStream out) {
		HttpEntity entity;
		try {
			HttpResponse response = client.execute(request);
			if (logger.isDebugEnabled()) {
				logger.debug("response code {}", response.getStatusLine()
						.getStatusCode());
				Header[] headers = response.getAllHeaders();
				if (null != headers && headers.length != 0) {
					for (Header head : headers) {
						logger.info("response headers key[{}] - value[{}]",
								head.getName(), head.getValue());
					}
				}
			}
			entity = response.getEntity();
			logger.info("headlength {} ", entity.getContentLength());
			if (null != entity) {
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			request.abort();
		}
	}

	// public void downloadFile(final URI uri, File file) {
	// HttpUriRequest request;
	// if (null == file || !file.exists()) {
	// request = new HttpGet(uri);
	// } else {
	// long ext = file.length();
	// request = new HttpGet(uri);
	// request.addHeader("", "");
	// }
	// }

	public <T> T doGet(final URI uri, final ResponseHandler<T> hander) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(uri);
		swap(request);
		return client.execute(request, hander);
	}

	public void doGet(final URI uri, final ContentComsumer consumer) {
		executor.execute(new Runnable() {
			public void run() {
				HttpGet request = new HttpGet(uri);
				swap(request);
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
