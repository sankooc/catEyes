/**
 * 
 */
package org.cateyes.core.script.qdisk;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.cateyes.core.VideoConstants;
import org.cateyes.core.conn.HttpConnector;
import org.cateyes.core.script.Program;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author sankooc
 * 
 */
public class QdiskDocumentResolver {
	protected final HttpConnector connector = org.cateyes.core.conn.ApacheConnector.getInstance();
	protected final static String site = "http://www.1qdisk.com";

	boolean isEmpty(Elements node) {
		return false;
	}

	protected String resolveProgram(Document doc) {
		if (null == doc) {
			return null;
		}
		Elements eles = doc.getElementsByTag("body");
		if (isEmpty(eles)) {
			return null;
		}
		eles = eles.get(0).getElementsByAttributeValue("id", "contents");
		if (isEmpty(eles)) {
			return null;
		}
		eles = eles.get(0).getElementsByAttributeValue("valign", "top");
		if (isEmpty(eles)) {
			return null;
		}
		eles = eles.get(0).getElementsByAttributeValue("style", "padding-left:10px");
		if (isEmpty(eles)) {
			return null;
		}
		return eles.get(0).text();
	}

	protected Collection<String> resolveVolumns(Document doc) {
		if (null == doc) {
			return null;
		}

		Elements eles = doc.getElementsByAttributeValue("class", "vod_list");
		if (isEmpty(eles)) {
			return null;
		}
		Collection<String> list = new LinkedList<String>();
		for (Element ele : eles) {
			Element span = ele.child(0);
			String name = span.text();
			if (StringUtils.isEmpty(name)) {
				continue;
			}
			list.add(name);
		}
		return list;
	}

	protected Collection<String> getVolumnSid(Document doc, String name) {
		if (null == doc) {
			return null;
		}
		Elements eles = doc.getElementsContainingOwnText(name);
		if (isEmpty(eles)) {
			return null;
		}
		Element vol = eles.get(0).parent();
		Collection<String> list = new LinkedList<String>();
		Element next = vol;
		while (true) {
			next = next.nextElementSibling();
			if (null == next || next.childNodes().isEmpty()) {
				break;
			}
			Element img = next.child(0);
			String pro = null;
			if (null == img || null == (pro = img.attr("onclick"))) {
				continue;
			} else {
				String str = getUrl(pro);
				if (!StringUtils.isEmpty(str)) {
					list.add(str);
				}
			}
		}
		return list;
	}

	protected String getUrl(String event) {
		if (null == event) {
			return null;
		}
		if (event.startsWith("playerTwoddo")) {
			event = event.substring(13, event.length() - 1);
		} else if (event.startsWith("player")) {
			event = event.substring(7, event.length() - 1);
		} else {
			return null;
		}
		event = event.split(",")[0];
		try {
			return URLDecoder.decode(event.substring(1, event.length() - 1), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void addMaps(String event, Map<String, VideoConstants.Provider> maps) {
		if (null == event) {
			return;
		}
		// System.out.println(event);
		// event = event.substring(event.indexOf('('), event.indexOf(')')-1);
		if (event.contains("http://player.youku.com")) {
			String[] split = event.split("/");
			if (split.length > 5) {
				maps.put(split[5], VideoConstants.Provider.YOUKU);
			}
			// Matcher matcher = pattern.matcher(event);
			// if (matcher.find(0)) {
			// String sid = matcher.group();
			// maps.put(sid, FlvProvider.YOUKU);
			// }
		} else if (event.contains("sohu.com")) {
			// String[] split =event.split("%2F");
			// if(split.length >4){
			// maps.put(split[4], FlvProvider.TUDOU);
			// }
		} else if (event.contains("www.tudou.com")) {
			String prefix = "/";
			if (event.contains("%2F")) {
				prefix = "%2F";
			}
			if (event.contains("v.swf")) {
				String[] split = event.split(prefix);
				if (split.length > 4) {
					maps.put(split[4], VideoConstants.Provider.TUDOU);
				}
			} else {
				// String sid = event.substring(tudouPre, event.indexOf(",") -
				// 1);
				// maps.put(sid, FlvProvider.TUDOU);
			}
		}

	}

	protected Collection<Program> getCatalogPrograms(Document doc) {
		if (null == doc) {
			return null;
		}
		Elements eles = doc.getElementsByAttributeValue("class", "main_ztnr");
		if (isEmpty(eles)) {
			return null;
		}
		Collection<Program> list = new ArrayList<Program>(eles.size());
		for (Element ele : eles) {
			Element pe = ele.child(0).child(1).child(0).child(0);
			if (null == pe) {
				continue;
			}
			String prex = pe.attr("href");
			Program program = new Program(URI.create(site + prex));
			program.setName(pe.text());
			list.add(program);
		}
		return list;
	}

	// /http://www.1qdisk.com/vod/list.html?searchFlag=title&searchValue=kbs&page=2

	protected int searchCount(Document document) {
		if (null == document) {
			return 0;
		}
		try {
			Elements eles = document.getElementsByAttributeValue("class", "main-left-02");
			eles = eles.get(0).child(0).child(0).child(0).child(0).child(0).child(0).child(0).getElementsByTag("strong");
			return Integer.parseInt(eles.get(0).text().trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected Collection<Program> search(Document document) {
		return getCatalogPrograms(document);
	}

	protected Collection<Program> getRecentProgram(Document doc) {
		if (null == doc) {
			return null;
		}
		Elements eles = doc.getElementsByAttributeValue("class", "img_list12");
		if (null == eles || eles.isEmpty()) {
			return null;
		}
		Collection<Program> programs = new ArrayList<Program>(eles.size());
		for (int i = 0; i < eles.size(); i++) {
			Element ele = eles.get(i);
			if ("li".equals(ele.tagName())) {
				// Element next = null;
				Program program = null;
				Elements children = ele.children();
				if (isEmpty(children)) {
					continue;
				}
				Element next = children.get(0);
				if ("a".equals(next.tagName())) {
					String path = next.attr("href");
					URI uri = URI.create(site + path);
					program = new Program(uri);
					programs.add(program);
					Element span = next.child(1);
					program.setName(span.text());
				}
			}
		}
		return programs;
	}
}
