/**
 * 
 */
package org.cateyes.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.cateyes.core.VideoConstants.VideoType;
import org.cateyes.core.cntv.CntvResolver;
import org.cateyes.core.deprecated.YoukuResolver;
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnFactory;
import org.cateyes.core.feng.FengResolver;
import org.cateyes.core.ku6.Ku6Resolver;
import org.cateyes.core.lesh.LeshResolver;
import org.cateyes.core.pps.PPSResolver;
import org.cateyes.core.wuliu.WulResolver;
import org.cateyes.core.yyt.YinyuetaiResolver;
import org.junit.Test;

/**
 * @author sankooc
 * 
 */
public class Demo {
	// ApacheConnector connect = new ApacheConnector();

//	@Test
	public void sohuTest(){
		String uri = "http://tv.sohu.com/20120726/n349111647.shtml";
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.write(new File("target/sohu"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void ifengtest() {
		String uri = "http://v.ifeng.com/mil/arms/201308/09e7f40c-c591-46e8-8df0-3f926043e7e9.shtml";
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.write(new File("target/ifeng"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void youkuTest() throws Exception{
		try {
			Volumn volum = VolumnFactory.createVolumn("http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html");
			volum.write(new File("target/youku"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
//	@Test
	public void tudouDemo(){
		String uri = "http://www.tudou.com/listplay/8Jr659zJxA4/Dyhg3Ucl1mQ.html";
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.write(new File("target/tudou"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void iqiyiDemo() {
		String uri = "http://www.iqiyi.com/dongman/20120416/77770ccdf98f2322.html";
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.write(new File("target/iqiyi"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void download(String uri,String path){
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.write(new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void test56() {
		String uri = "http://www.56.com/u69/v_ODg5MTIzNTQ.html";
		download(uri,"target/56");

	}
	
//	@Test
	public void testKu6() {
		String uri = "http://v.ku6.com/show/Gahx_fVJ5bFGzG3eD1cLDw...html";
		download(uri,"target/ku6");
	}
	
	@Test
	public void testPps() {
		String uri = "http://v.pps.tv/play_36ASVO.html#from_www";
		download(uri,"target/pps");
	}
	
	
//	@Test
	public void testYyt() {
		String uri = "http://www.yinyuetai.com/video/731011";
		download(uri,"target/yyt");
	}
	
	
//	@Test
	public void testCntv() {
		String uri = "http://tv.cntv.cn/vodplay/56fb13e1e624474584a95be530234841/860010-1105010100";
		download(uri,"target/cntv");
	}
	
//	@Test
	public void testLeshi() {
		String uri = "http://www.letv.com/ptv/vplay/2074193.html";
		download(uri,"target/leshi");
	}
	
}