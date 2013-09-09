package org.cateyes.local;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	static String format;
	static {
		InputStream stream = CatsHandler.class.getClassLoader().getResourceAsStream("index.html");
		InputStream stream2 = CatsHandler.class.getClassLoader().getResourceAsStream("content.html");
		try {
			inx = IOUtils.toByteArray(stream);
			format = IOUtils.toString(stream2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();
		String[] tokens = request.getUri().split("\\?");
		if (tokens.length > 1) {
			String path = tokens[0];
			if (!"/cateyes".equals(path)) {
				writeResponse(e, request);
				return;
			}
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

	static String trFormat = "<tr><td><a href=\"%s\">%s</a></td></tr>";

	private void writeResponse(MessageEvent e, HttpRequest request, String url) {
		try {
			Volumn volumn = VolumnFactory.createVolumn(url);
			if (null != volumn) {
				StringBuilder builder = new StringBuilder();
				int inx = 1;
				
//				for (String source : volumn.getFragmentURL(0)) {
//					builder.append(String.format(trFormat, source, volumn.getTitle()+"-" + inx++));
//				}
				String content = String.format(format, volumn.getProvider().name().toLowerCase(), volumn.getTitle(), builder.toString());
				HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
				try {
					response.setContent(ChannelBuffers.copiedBuffer(content, Charset.forName("UTF-8")));
					response.setHeader(CONTENT_TYPE, "text/html;charset=utf-8");
					response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				ChannelFuture future = e.getChannel().write(response);
				future.addListener(ChannelFutureListener.CLOSE);
				return;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		writeResponse(e, request);
	}

	private void writeResponse(MessageEvent e, HttpRequest request) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setContent(ChannelBuffers.copiedBuffer(inx));
		response.setHeader(CONTENT_TYPE, "text/html;charset=utf-8");
		response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
		ChannelFuture future = e.getChannel().write(response);
		future.addListener(ChannelFutureListener.CLOSE);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
