package org.cateyes.core.flv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.cateyes.core.util.CommonUtils;
import org.junit.Test;

public class FlvMergeDemo {
	
//	@Test
	public void merge() throws Exception{
		File[] files = new File[2];
		files[0] = new File("d:/test/test-00.flv");
		files[1] = new File("d:/test/test-01.flv");
//		files[2] = new File("d:/test/test-02.flv");
//		files[3] = new File("d:/test/test-03.flv");
//		files[4] = new File("d:/test/test.flv");
		File target = new File("target/tmp.flv");
		CommonUtils.mergeFlv(files, target);
	}
	
	@Test
	public void resolv() throws FileNotFoundException, IOException{
		File file =  new File("d:/test/test-00.flv");
		CommonUtils.resolve(file);
	}
	
//	@Test
	public void compute(){
		double ret = 362.46666666666664+390.4667573696145+363.06721088435376+343.7946485260771;
		System.out.println(ret);
		//1459.789
	}
}
