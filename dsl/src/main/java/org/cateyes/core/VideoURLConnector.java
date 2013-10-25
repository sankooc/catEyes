package org.cateyes.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class VideoURLConnector extends URLConnection {

	protected VideoURLConnector(URL url) {
		super(url);
	}

	@Override
	public void connect() throws IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			URLStreamHandler
			URL url = new URL("video:http://www.baidu.com");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
