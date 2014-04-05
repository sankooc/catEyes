package org.cateyes.core.script.qdisk;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.cateyes.core.conn.ApacheConnector;
import org.cateyes.core.conn.HttpConnector;
import org.cateyes.core.script.Program;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class QdiskTest {
	QdiskDocumentResolver resolver = new QdiskDocumentResolver();

	// @Test
	public void testProgramUri() {
		try {
			Document document = Jsoup.parse(new File(
					"target/test-classes/programPage.htm"), "UTF-8");
			String str = resolver.resolveProgram(document);
			Assert.assertEquals(str, "세대공감 토요일");
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	// @Test
	public void testCatalog() {
		try {
			Document document = Jsoup.parse(new File(
					"target/test-classes/catalog.htm"), "UTF-8");
			Collection<Program> programs = resolver
					.getCatalogPrograms(document);
			Assert.assertNotNull(programs);
			displayProgram(programs);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

//	@Test
	public void testResearch(){
		try {
			Document document = Jsoup.parse(new File(
					"target/test-classes/search.htm"), "UTF-8");
			int count = resolver.searchCount(document);
			System.out.println(count);
			Assert.assertEquals(count, 42);
			
			Collection<Program> programs = resolver.search(document);
			Assert.assertNotNull(programs);
			displayProgram(programs);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	HttpConnector connector = ApacheConnector.getInstance();
	
	@Test
	public void testVolumn() throws UnsupportedEncodingException {
		try {
//			Document document = Jsoup.parse(new File(
//					"target/test-classes/programPage.htm"), "UTF-8");
			Document document = connector.getHtmlPage("http://www.1qdisk.com/vod/view.html?idx=24");
			Collection<String> list = resolver.resolveVolumns(document);
			Assert.assertNotNull(list);
			for (String str : list) {
				Collection<String> maps = resolver.getVolumnSid(document, str);
				System.out.print(str + " ");
				if (null != maps) {
					for (String sid : maps) {
						System.out.print(" : [" + sid + "]");
					}
				}
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	// @Test
	public void testRecent() {
		try {
			Document document = Jsoup.parse(new File(
					"target/test-classes/main.htm"), "UTF-8");
			Collection<Program> programs = resolver.getRecentProgram(document);
			Assert.assertNotNull(programs);
			displayProgram(programs);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	void displayProgram(Collection<Program> programs) {
		if (null == programs || programs.isEmpty()) {
			return;
		}
		for (Program program : programs) {
			System.out.println(program.getName());
		}
	}

}
