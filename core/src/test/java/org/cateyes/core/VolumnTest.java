package org.cateyes.core;

import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.cateyes.core.conn.ApacheConnector;
import org.cateyes.core.conn.ConnectorProvider;
import org.cateyes.core.conn.ApacheConnector.VideoInfo;
import org.cateyes.core.resolver.Resolver;
import org.cateyes.core.resolver.blibli.BlibliResolver;
import org.cateyes.core.resolver.cntv.CntvResolver;
import org.cateyes.core.resolver.feng.FengResolver;
import org.cateyes.core.resolver.iqiyi.IqiyiResolver;
import org.cateyes.core.resolver.ku6.Ku6Resolver;
import org.cateyes.core.resolver.lesh.LeshResolver;
import org.cateyes.core.resolver.pps.PPSResolver;
import org.cateyes.core.resolver.sina.SinaResolver;
import org.cateyes.core.resolver.sohu.SohuResolver;
import org.cateyes.core.resolver.tencent.TencentResolver;
import org.cateyes.core.resolver.tudou.TudouResolver;
import org.cateyes.core.resolver.wuliu.WulResolver;
import org.cateyes.core.resolver.youku.YoukuResolver;
import org.cateyes.core.resolver.yyt.YinyuetaiResolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.VolumnFactory;
import org.junit.Test;

public class VolumnTest {
	ApacheConnector connector = ConnectorProvider.getCommonConnector();

	static Collection<Resolver> resolvers = VolumnFactory.getResolvers();

	protected void test(Resolver resolver, String url) {
		try {

			Assert.assertTrue(resolver.isPrefer(url));
			Volumn volumn = resolver.createVolumn(url);
			Assert.assertNotNull(volumn);
			Map<String, Long> set = volumn.getUrlSet();
			Assert.assertFalse(set.isEmpty());
			for (String uri : set.keySet()) {
				long size = set.get(uri);
				VideoInfo info = connector.getVideoInfo(uri);
				long ret  = info.getSize();
				System.out.println(info.getType());
				if(size <1){
					continue;
				}
//				long ret = connector.getResourceLength(uri);
				// System.out.println(size + ":" + ret + " =" + (size == ret));
				Assert.assertEquals(ret, size);
			}
			for (Resolver r : resolvers) {
				if (r.getClass().equals(resolver.getClass())) {
					return;
				}
			}
			Assert.fail("add resolver to factory");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testYyt() {
		String uri = "http://www.yinyuetai.com/video/731011";
		Resolver resolver = new YinyuetaiResolver();
		test(resolver, uri);
	}

//	@Test
	public void testCntv() {
		String uri = "http://tv.cntv.cn/vodplay/56fb13e1e624474584a95be530234841/860010-1105010100";
		Resolver resolver = new CntvResolver();
		test(resolver, uri);
	}

//	@Test
	public void testKu6() {
		String uri = "http://v.ku6.com/show/Gahx_fVJ5bFGzG3eD1cLDw...html";
		Resolver resolver = new Ku6Resolver();
		test(resolver, uri);
	}

	@Test
	public void testTencent() {
		String uri = "http://v.qq.com/cover/q/qk8vyb5drwnn174.html";
		Resolver resolver = new TencentResolver();
		test(resolver, uri);
	}

//	@Test
	public void testPps() {
		String uri = "http://v.pps.tv/play_36ASVO.html#from_www";
		Resolver resolver = new PPSResolver();
		test(resolver, uri);
	}

//	@Test
	public void testLeshi() {
		String uri = "http://www.letv.com/ptv/vplay/2074193.html";
		Resolver resolver = new LeshResolver();
		test(resolver, uri);
	}

//	@Test
	public void test56() {
		String uri = "http://www.56.com/u69/v_ODg5MTIzNTQ.html";
		Resolver resolver = new WulResolver();
		test(resolver, uri);

	}

//	@Test
	public void testIFeng() {
		String uri = "http://v.ifeng.com/mil/arms/201308/09e7f40c-c591-46e8-8df0-3f926043e7e9.shtml";
		Resolver resolver = new FengResolver();
		test(resolver, uri);
	}

//	@Test
	public void testyouku() {
		String uri = "http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html";
		Resolver resolver = new YoukuResolver();
		test(resolver, uri);
	}

//	@Test
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
	public void testsohu() {//TODO 
		String uri = "http://tv.sohu.com/20120726/n349111647.shtml";
		Resolver resolver = new SohuResolver();
		test(resolver, uri);
	}

	// @Test
	public void testblibli() {
		String uri = "http://www.bilibili.tv/video/av685174/";
		Resolver resolver = new BlibliResolver();
		test(resolver, uri);
	}

	public void testSina() {
		// String uri = "http://video.weibo.com/v/weishipin/t_zQac7Z4.htm";
		String uri = "http://video.sina.com.cn/v/b/111599121-1348174564.html";
		Resolver resolver = new SinaResolver();
		test(resolver, uri);
	}
}
