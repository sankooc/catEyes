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
public class ApacheConnectTest {
	ApacheConnector connect = new ApacheConnector();

	@Test
	public void continueContent() {
		try {
			Volumn volum = VolumnFactory.createVolumn(
					"http://v.youku.com/v_show/id_XNTQ2OTc0OTAw.html",
					new File("target/youku"));
			volum.write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}