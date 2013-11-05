package org.cateyes.core.conn;

import java.io.File;
import java.io.OutputStream;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpression;

import net.sf.json.JSONObject;

public interface HttpConnector {
	public class ResponseInfo {
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

		public ResponseInfo(long size, String type) {
			super();
			this.size = size;
			this.type = type;
		}

		public ResponseInfo() {
			super();
		}

	}

	boolean checkConnector(String uri, int status, long size, String type);

	byte[] doGet(String uri) throws Exception;

	void download(final String url, File file) throws Exception;

	void download(final String uri, long contentSize, File file, MResource adaptor) throws Exception;

	void download(final String uri, OutputStream out, long contentSize, long size, MResource control) throws Exception;

	org.jsoup.nodes.Document getHtmlPage(String addr) throws Exception;

	org.w3c.dom.Document getPageAsDocument(String addr) throws Exception;

	JSONObject getPageAsJson(String uri) throws Exception;

	String getPageRegix(String uri, Pattern pattern) throws Exception;

	String getPageXpath(String uri, XPathExpression expression) throws Exception;

	long getResourceLength(String uri);

	ResponseInfo getVideoInfo(String uri) throws Exception;

}
