package org.cateye.server;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.http.client.utils.URLEncodedUtils;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnFactory;

public class CatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	private void deal(HttpServletRequest req, HttpServletResponse resp){
		String url = req.getParameter("target");
		try {
			new URL(url);
			Volumn volum = VolumnFactory.createVolumn(url);
			JSONObject object = JSONObject.fromObject(volum);
			String content = object.toString();
			content = URLEncoder.encode(content);
			resp.getOutputStream().write(content.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(501);
		}
//		String uri;
//		Volumn volum = VolumnFactory.createVolumn(uri);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		deal(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		deal(req, resp);
	}
	

}
