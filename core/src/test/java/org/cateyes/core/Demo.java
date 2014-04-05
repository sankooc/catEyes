/**
 * 
 */
package org.cateyes.core;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.cateyes.core.media.MediaMerger;
import org.cateyes.core.resolver.youku.YoukuResolver;
import org.cateyes.core.volumn.Volumn;
import org.cateyes.core.volumn.Volumn.VolumnDownloadResult;
import org.cateyes.core.volumn.VolumnFactory;
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
			volum.writeLowQuality(new File("target/sohu"),null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void ifengtest() {
		String uri = "http://v.ifeng.com/mil/arms/201308/09e7f40c-c591-46e8-8df0-3f926043e7e9.shtml";
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.writeLowQuality(new File("target/ifeng"),null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	ThreadPoolExecutor downExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	ThreadPoolExecutor mergeExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
	
	@Test
	public synchronized void youkuTest() throws Exception{
		
		final ArrayList<String> list = new ArrayList<String>();
//		list.add("XNTUxNzEzNDQw");//2
//		list.add("XNTU1MDk2MjI0");//3
		
//		list.add("XNTU4MzkyNjA0");//4
//		list.add("XNTYxODA1MTUy");//5
		list.add("XNTY0NjkzNzU2");//6
		list.add("XNTY3OTQ2NDg0");//7
		list.add("XNTcwOTA1MDUy");//8
		//list.add("XNTczODU2MTc2");//9
		//ist.add("XNTc2NjcxMjY4");//10
		//list.add("XNTc5NDY4NTEy");//11
		//list.add("XNTgyMjcwODg0");//12
		for(final String sid : list){
//			downExecutor.execute(new Runnable(){
//				@Override
//				public void run() {
					try {
						Volumn volumn = YoukuResolver.createVolumnFromVid(sid);
						final VolumnDownloadResult result =volumn.writeHighQuality(new File("/Users/sankooc/genes"), null);
						if(!result.isComplete()){
							continue;
						}
						mergeExecutor.execute(new Runnable(){
							@Override
							public void run() {
								MediaMerger.merge(result.getSource(), result.getFolder(), result.getTitle());
								System.err.println("download"+result.getTitle()+"complete");
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					
//				}
//				
//			});	
		}
		wait();
//		try {
//			Volumn volumn = YoukuResolver.createVolumnFromVid("XNTQ4NjA2NTA4");
//			VolumnDownloadResult result =volumn.writeHighQuality(new File("/Users/sankooc/genes"), "the_g");
//			MediaMerger.merge(result.getSource(), result.getFolder(), result.getTitle());
//			
////			Volumn volum = VolumnFactory.createVolumn("http://v.youku.com/v_show/id_XNjE1Mjk2MjM2.html");
////			volum.writeLowQuality(new File("target/youku"),null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	
//	@Test
	public void tudouDemo(){
		String uri = "http://www.tudou.com/listplay/8Jr659zJxA4/Dyhg3Ucl1mQ.html";
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.writeLowQuality(new File("target/tudou"),null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void iqiyiDemo() {
		String uri = "http://www.iqiyi.com/dongman/20120416/77770ccdf98f2322.html";
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.writeLowQuality(new File("target/iqiyi"),null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void download(String uri,String path){
		try {
			Volumn volum = VolumnFactory.createVolumn(uri);
			volum.writeLowQuality(new File(path),null);
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
	
//	@Test
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