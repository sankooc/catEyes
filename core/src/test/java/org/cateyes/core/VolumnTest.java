package org.cateyes.core;

import java.util.Collection;

import junit.framework.Assert;

import org.cateyes.core.conn.ConnectorProvider;
import org.cateyes.core.conn.HttpConnector;
import org.cateyes.core.conn.HttpConnector.ResponseInfo;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnFactory;
import org.junit.Test;

public class VolumnTest {
	HttpConnector connector = ConnectorProvider.getCommonConnector();

	static Collection<Resolver> resolvers = VolumnFactory.getResolvers();

	protected void test(String url) {
		try {
			Resolver resolver = VolumnFactory.getResolver(url);
			Assert.assertNotNull(resolver);
			Volumn volumn = resolver.createVolumn(url);
			Assert.assertNotNull(volumn);
			System.out.println("-------" + resolver.getClass().getSimpleName()
					+ " testing-------");
			Assert.assertNotNull(volumn.getTitle());
			System.out.println("title is :" + volumn.getTitle());
			int count = volumn.getQualityCount();
			System.out.println("the volumn contain:" + count);
			for (int i = 0; i < count; i++) {
				Collection<String> urls = volumn.getFragmentURL(i);
				Assert.assertFalse(urls.isEmpty());
				for (String uri : urls) {
					ResponseInfo info = connector.getVideoInfo(uri);
					System.out.println("uri:" + uri);
					System.out.println("video fragment size:" + info.getSize()
							+ " type:" + info.getType());
				}
			}
			System.out.println("\r\r\r");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testtudou() {
		String uri = "http://www.tudou.com/albumplay/4X8TTOvk_rw/uuC7X1JuEgQ.html";
		test(uri);
	}
	
	@Test
	public void testsohu() {
		String uri = "http://tv.sohu.com/20120726/n349111647.shtml";
		test(uri);
	}

	@Test
	public void testTencent() {
		String uri = "http://v.qq.com/cover/q/qk8vyb5drwnn174.html";
		test(uri);
	}

	@Test
	public void test56() {
		String uri = "http://www.56.com/u69/v_ODg5MTIzNTQ.html";
		test(uri);
	}

	@Test
	public void testyouku() {
		String uri = "http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html";
		test(uri);
	}

	@Test
	public void testYyt() {
		String uri = "http://www.yinyuetai.com/video/731011";
		test(uri);
	}

	@Test
	public void testCntv() {
		String uri = "http://tv.cntv.cn/video/C39683/7ea80f4b17d7400ba216827e599fdc7f";
		test(uri);
	}

//	@Test
	public void testPps() {
		String uri = "http://v.pps.tv/play_36ASVO.html#from_www";
		test(uri);
	}

	@Test
	public void testLeshi() {
		String uri = "http://www.letv.com/ptv/vplay/2074193.html";
		test(uri);
	}

	@Test
	public void testKu6() {
		String uri = "http://v.ku6.com/show/Gahx_fVJ5bFGzG3eD1cLDw...html";
		test(uri);
	}

	// @Test
	public void testiqiyi() {
		String uri = "http://www.iqiyi.com/dongman/20120416/77770ccdf98f2322.html";
		test(uri);
	}

	// @Test
	public void testGT() {
		String uri = "http://www.gametrailers.com/videos/e1lo05/the-legend-of-zelda--the-wind-waker-hd-nintendo-direct--remake-update";
		test(uri);

	}

	@Test
	public void testIFeng() {
		String uri = "http://v.ifeng.com/mil/arms/201308/09e7f40c-c591-46e8-8df0-3f926043e7e9.shtml";
		test(uri);
	}

	// @Test
	public void testblibli() {
		String uri = "http://www.bilibili.tv/video/av685174/";
		test(uri);
	}

	public void testSina() {
		String uri = "http://video.sina.com.cn/v/b/111599121-1348174564.html";
		test(uri);
	}
}
