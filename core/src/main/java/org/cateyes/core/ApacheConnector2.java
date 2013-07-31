package org.cateyes.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.cateyes.core.util.CommonUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ApacheConnector2 {
	final DefaultHttpClient client;
	ExecutorService executor;
	static Logger logger = LoggerFactory.getLogger(ApacheConnector2.class);

	public ApacheConnector2() {
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

	public <T> T getPage(URI uri, ResponseHandler<T> handler) {
		HttpGet request = new HttpGet(uri);
		try {
			return client.execute(request, handler);
		} catch (Exception e1) {
			logger.error(e1.getMessage());
		}
		return null;
	}

	public String getContentAsXpath(String address, String expres)
			throws XPathExpressionException {
		URI uri = URI.create(address);

		javax.xml.xpath.XPathFactory factory = javax.xml.xpath.XPathFactory
				.newInstance();
		javax.xml.xpath.XPath xpath = factory.newXPath();
		final javax.xml.xpath.XPathExpression expression = xpath
				.compile(expres);
		ResponseHandler<String> handler = new ResponseHandler<String>() {

			public String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				HttpEntity entity = response.getEntity();
				try {
					InputStream stream = entity.getContent();
					return expression.evaluate(new InputSource(stream));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					EntityUtils.consume(entity);
				}
				return null;
			}
		};
		return getPage(uri, handler);
	}

	public org.w3c.dom.Document getPageAsDoc(String addr) {
		URI uri = URI.create(addr);
		ResponseHandler<org.w3c.dom.Document> handler = new ResponseHandler<org.w3c.dom.Document>() {

			public org.w3c.dom.Document handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				HttpEntity entity = response.getEntity();
				try {
					InputStream stream = entity.getContent();
					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();
					// Turn on validation, and turn off namespaces
					factory.setValidating(false);
					factory.setNamespaceAware(false);
					DocumentBuilder builder = factory.newDocumentBuilder();
					return builder.parse(stream);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					EntityUtils.consume(entity);
				}
				return null;
			}
		};
		return getPage(uri, handler);
	}

	public org.jsoup.nodes.Document getPageAsJsoup(String addr) {
		return getPageAsJsoup(URI.create(addr));
	}

	public org.jsoup.nodes.Document getPageAsJsoup(URI uri) {
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
				org.jsoup.nodes.Document document = Jsoup.parse(
						entity.getContent(), "UTF-8", uri.toString());
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

		public synchronized void setContent(long content) {
			len = content;
			logger.debug("current length {} ", len);
		}

		int timer = 0;

		public synchronized void addContent(long increase) {
			len += increase;
			if (timer-- < 0) {
				logger.debug("current length {} ", len);
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
