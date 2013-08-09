package org.cateyes.local;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class CatsHandler extends SimpleChannelUpstreamHandler {

	static byte[] inx;
	static {
		InputStream stream = CatsHandler.class.getClassLoader()
				.getResourceAsStream("index.html");
		try {
			inx = IOUtils.toByteArray(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();
		String[] tokens = request.getUri().split("\\?");
		if (tokens.length > 1) {
			String path = tokens[0];
			String que = tokens[1];
			que = URLDecoder.decode(que);
			Pattern pattern = Pattern.compile("target=([^&]+)");
			Matcher matcher = pattern.matcher(que);
			if (matcher.find()) {
				String url = matcher.group(1);
				writeResponse(e, request, url);
				return;
			}
		}

		writeResponse(e, request);
	}

	private void writeResponse(MessageEvent e, HttpRequest request, String url) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setContent(ChannelBuffers.copiedBuffer(inx));
		try {
			Volumn volum = VolumnFactory.createVolumn(url);
			JSONObject object = JSONObject.fromObject(volum);
			String content = object.toString();
			response.setContent(ChannelBuffers.copiedBuffer(content,Charset.forName("UTF-8")));
			response.setHeader(CONTENT_TYPE, "text/json;charset=utf-8");
			response.setHeader(CONTENT_LENGTH, response.getContent()
					.readableBytes());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		ChannelFuture future = e.getChannel().write(response);
		future.addListener(ChannelFutureListener.CLOSE);

	}

	private void writeResponse(MessageEvent e, HttpRequest request) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setContent(ChannelBuffers.copiedBuffer(inx));
		response.setHeader(CONTENT_TYPE, "text/html;charset=utf-8");
		response.setHeader(CONTENT_LENGTH, response.getContent()
				.readableBytes());
		ChannelFuture future = e.getChannel().write(response);
		future.addListener(ChannelFutureListener.CLOSE);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
