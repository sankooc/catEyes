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
import org.cateyes.core.entity.Volumn;
import org.cateyes.core.entity.VolumnFactory;
import org.cateyes.core.youku.YoukuResolver;
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
	public void youkuTest() throws Exception{
		try {
			Volumn volum = VolumnFactory.createVolumn("http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html");
			volum.write(new File("target/youku"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	@Test
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

}