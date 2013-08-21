package org.cateyes.core.conn;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.cateyes.core.flv.FlvUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class ApacheConnector implements HttpConnector {

	public static class VideoInfo {
		long size;
		String type;

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public VideoInfo(long size, String type) {
			super();
			this.size = size;
			this.type = type;
		}

		public VideoInfo() {
			super();
		}

	}

	final DefaultHttpClient client;
	ExecutorService executor;
	static Logger logger = LoggerFactory.getLogger(ApacheConnector.class);

	private static ApacheConnector INSTANCE = new ApacheConnector();

	public static ApacheConnector getInstance() {
		return INSTANCE;
	}

	private ApacheConnector() {
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
				return true;
			}
		};
		client.setHttpRequestRetryHandler(retryHandler);
		executor = Executors.newCachedThreadPool();
	}

	protected void swap(HttpMessage request) {
		// request.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
		// request.addHeader("User-Agent",
		// "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
		// request.addHeader("Accept-Encoding","gzip,deflate,sdch");
		// request.addHeader("Accept-Language","en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
		// request.addHeader("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		// request.addHeader("Connection","keep-alive");
		// request.addHeader("Cookie","key1=value1; key2=value2");

	}

	public String getPageRegix(String uri, final Pattern pattern)
			throws Exception {
		ResponseHandler<String> handler = new ResponseHandler<String>() {
			public String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				InputStream stream = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(stream));
				while (true) {
					try {
						String line = reader.readLine();
						if (null == line) {
							break;
						}
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							return matcher.group(1);
						}
					} catch (IOException e) {
						logger.error(e.getMessage());
						break;
					}
				}
				return null;
			}
		};
		return doGet(uri, handler);
	}

	public String getPageXpath(String uri, final XPathExpression expression)
			throws Exception {
		ResponseHandler<String> handler = new ResponseHandler<String>() {
			public String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				InputStream stream = response.getEntity().getContent();
				try {
					return (String) expression.evaluate(
							new InputSource(stream), XPathConstants.STRING);
				} catch (XPathExpressionException e) {
					logger.error(e.getMessage(), e);
				}
				return null;
			}
		};

		return doGet(uri, handler);
	}

	public JSONObject getPageAsJson(String uri) throws Exception {
		ResponseHandler<JSONObject> handler = new ResponseHandler<JSONObject>() {
			public JSONObject handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				InputStream stream = response.getEntity().getContent();
				String content = IOUtils.toString(stream);
				return JSONObject.fromObject(content);
				// return null;
			}
		};
		return doGet(uri, handler);
	}

	public VideoInfo getVideoInfo(String uri) throws Exception {
		HttpRequestBase request = createRequest(uri);
		HttpEntity entity = null;
		try {
			HttpResponse response = client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			if (200 != code) {
				logger.error("response error : {}", code);
				return null;
			}
			String type = response.getHeaders("Content-Type")[0].getValue();
			entity = response.getEntity();
			if (null != entity) {
				long size = entity.getContentLength();
				return new VideoInfo(size, type);
			}
			return new VideoInfo(0, type);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				request.abort();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}

	public org.w3c.dom.Document getPageAsDoc(String addr) throws Exception {
		ResponseHandler<org.w3c.dom.Document> handler = new ResponseHandler<org.w3c.dom.Document>() {
			public org.w3c.dom.Document handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				HttpEntity entity = response.getEntity();
				try {
					InputStream stream = entity.getContent();
					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();
					factory.setValidating(false);
					factory.setNamespaceAware(false);
					DocumentBuilder builder = factory.newDocumentBuilder();
					return builder.parse(stream);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				} finally {
					EntityUtils.consume(entity);
				}
				return null;
			}
		};
		return doGet(addr, handler);
	}

	HttpRequestBase createRequest(String uri) {
		HttpGet request = new HttpGet(uri);
		swap(request);
		return request;
	}

	void consume(HttpResponse response) {
		if (null != response) {
			HttpEntity entity = response.getEntity();
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public byte[] doGet(String uri) {
		HttpRequestBase request = createRequest(uri);
		HttpResponse response = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(entity.getContent(), baos);
				return baos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			consume(response);
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

	public void download(final String uri, final long contentSize, File file,
			MResource adaptor) throws Exception {
		long size = 0;
		OutputStream out = null;
		file.getParentFile().mkdirs();
		if (file.exists()) {
			size = file.length();
			out = new FileOutputStream(file, true);
		} else {
			out = new FileOutputStream(file);
		}
		download(uri, out, contentSize, size, adaptor);
		out.close();
	}

	void readHeaders(HttpMessage message) {
		Header[] headers = message.getAllHeaders();
		if (null == headers) {
			return;
		}
		for (Header head : headers) {
			logger.info("header key [{}]  value [{}]", head.getName(),
					head.getValue());
		}
	}

	public long getResourceLength(String uri) {
		HttpRequestBase request = createRequest(uri);
		HttpEntity entity = null;
		try {
			HttpResponse response = client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			if (200 != code) {
				logger.error("response error : {}", code);
				return 0;
			}
			response.getHeaders("Content-Type")[0].getValue();
			entity = response.getEntity();
			if (null != entity) {
				return entity.getContentLength();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				request.abort();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return 0;
	}

	public void download(final String uri, OutputStream out,
			final long contentSize, long size, MResource control)
			throws Exception {
		long totalLenth = contentSize;
		if (contentSize < 1) {
			totalLenth = getResourceLength(uri);
		}
		logger.info("content length {}", totalLenth);
		if (totalLenth == 0) {
			throw new RuntimeException("no resource");
		}
		if (null == control) {
			control = ConsoleOuputer.getInstance().createConsoler("");
		}
		control.init();
		control.setLength(totalLenth);
		long begin = System.currentTimeMillis();

		if (totalLenth == size) {
			logger.info("downloaded");
			control.finish();
			return;
		}
		HttpRequestBase request = createRequest(uri);
		HttpResponse response = null;

		try {
			if (size < 1) {
				control.setContent(0);
			} else {
				control.setContent(size);
				request.addHeader("Range", "bytes=" + size + "-");
			}
			control.start();
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			logger.info("headlength {} ", entity.getContentLength());
			if (null != entity) {
				FlvUtil.copyStream(entity.getContent(), out, control);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			consume(response);
		}
		control.finish();

		long end = System.currentTimeMillis();

		logger.info("spend time {}", (end - begin));
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

	public <T> T doGet(final String uri, final ResponseHandler<T> hander)
			throws Exception {
		HttpRequestBase request = createRequest(uri);
		return client.execute(request, hander);
	}
}
