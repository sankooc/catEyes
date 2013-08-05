package org.cateyes.core;

import java.util.Map;

import junit.framework.Assert;

import org.cateyes.core.blibli.BlibliResolver;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnFactory;
import org.cateyes.core.iqiyi.IqiyiResolver;
import org.cateyes.core.sohu.SohuResolver;
import org.cateyes.core.tudou.TudouResolver;
import org.cateyes.core.wuliu.WulResolver;
import org.cateyes.core.youku.YoukuResolver;
import org.junit.Test;

public class VolumnTest {
	ApacheConnector connector = ConnectorProvider.getCommonConnector();

	protected void test(Resolver resolver, String url) {
		try {
			Volumn volumn = resolver.createVolumn(url);
			Assert.assertNotNull(volumn);
			Map<String, Long> set = volumn.getUrlSet();
			Assert.assertFalse(set.isEmpty());
			for (String uri : set.keySet()) {
				long size = set.get(uri);
				long ret = connector.getResourceLength(uri);
//				System.out.println(size + ":" + ret + " =" + (size == ret));
				 Assert.assertEquals(ret, size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test56(){
		String uri = "http://www.56.com/u69/v_ODg5MTIzNTQ.html";
		Resolver resolver = new WulResolver();
		
		test(resolver,uri);
		
	}
	
	
	// @Test
	public void testyouku() {
		String uri = "http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html";
		Resolver resolver = new YoukuResolver();
		test(resolver, uri);
	}

	// @Test
	public void testtudou() {
		String uri = "http://www.tudou.com/listplay/8Jr659zJxA4/Dyhg3Ucl1mQ.html";
		Resolver resolver = new TudouResolver();
		test(resolver, uri);
	}

//	@Test
	public void testiqiyi() {
		String uri = "http://www.iqiyi.com/dongman/20120416/77770ccdf98f2322.html";
		Resolver resolver = new IqiyiResolver();
		test(resolver, uri);
	}

//	@Test
	public void testsohu() {
		String uri = "http://tv.sohu.com/20120726/n349111647.shtml";
		Resolver resolver = new SohuResolver();
		test(resolver, uri);
	}

//	@Test
	public void blibli(){
		String uri = "http://www.bilibili.tv/video/av685174/";
		Resolver resolver = new BlibliResolver();
		test(resolver,uri);
	}
	
	
	
}
