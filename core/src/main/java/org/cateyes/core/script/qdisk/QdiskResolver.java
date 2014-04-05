/**
 * 
 */
package org.cateyes.core.script.qdisk;

import java.net.URI;
import java.util.Collection;

import org.cateyes.core.script.Program;
import org.cateyes.core.script.Chapter;
import org.jsoup.nodes.Document;

/**
 * 
 * @author sankooc
 * 
 */
public class QdiskResolver extends QdiskDocumentResolver {

	public Collection<Program> search(String programName) {
		return null;
	}

	/**
	 * get programs from type
	 * 
	 * @param type
	 * @param page
	 * @return
	 */
	public Collection<Program> getPrograms(ProgramType type, int page) {
		if (null == type) {
			return null;
		}
		String cate = null;
		switch (type) {
		case DRAMA:
			cate = "100000";
			break;
		case MOVIE:
			cate = "200000";
			break;
		case ENTERTAINMENT:
			cate = "300000";
			break;
		case SPORTS:
			cate = "400000";
			break;
		default:
			return null;
		}
		String total = site + "/vod/list.html?cate=" + cate + "&page=" + page;
		try {
			return getCatalogPrograms(connector.getHtmlPage(total));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Collection<Chapter> getVolumns(Program program) {
		return null;
	}

	/**
	 * create program from uri
	 * 
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public Program getProgram(URI uri) throws Exception {
		String name = resolveProgram(connector.getHtmlPage(uri.toString()));
		Program program = new Program(uri);
		program.setName(name);
		return program;
	}

	public org.cateyes.core.script.SiteScript.Program getProgram2(String url) throws Exception {
		Document doc = connector.getHtmlPage(url);
		String title = resolveProgram(doc);
		org.cateyes.core.script.SiteScript.Program program = new org.cateyes.core.script.SiteScript.Program(url, title);

		Collection<String> chapters = resolveVolumns(doc);
		if (null != chapters) {
			for (String name : chapters) {
				Collection<String> urls = getVolumnSid(doc, name);
				program.getList().put(name, urls);
			}
		}
		return program;
	}

	/**
	 * return recent updated programs
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<Program> recentProgram() throws Exception {
		return getRecentProgram(connector.getHtmlPage(site));
	}
}
