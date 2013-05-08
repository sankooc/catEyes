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
import org.cateyes.core.youku.YoukuResolver;
import org.junit.Test;

/**
 * @author sankooc
 *
 */
public class ApacheConnectTest {
	ApacheConnector connect = new ApacheConnector();
	@Test
	public void continueContent(){
		String[] s =  YoukuResolver.getReadUriFromYID("XNTQ2OTc0OTAw", VideoType.FLV);
		String uriStr = s[0];
		URI uri = URI.create(uriStr);
		try {
			File tmp = new File("target/youku/test.flv");
			OutputStream out = new FileOutputStream(tmp,true);
			connect.download(uri, out,tmp.length(),null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}