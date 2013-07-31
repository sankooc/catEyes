package org.cateyes.core;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;

public class Connector2 {
	@Test
	public void xpath(){
		try {
			ApacheConnector2 connector = new ApacheConnector2();
			org.w3c.dom.Document doc = connector.getPageAsDoc("http://www.baidu.com");
			System.out.println(doc.toString());
//			String content = connector.getContentAsXpath("www.baidu.com", "/html/head");
//			System.out.println(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
