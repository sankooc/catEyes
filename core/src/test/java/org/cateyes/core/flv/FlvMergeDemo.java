package org.cateyes.core.flv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class FlvMergeDemo {
	
//	@Test
	public void merge() throws Exception{
		File[] files = new File[2];
		files[0] = new File("d:/test/test-01.flv");
		files[1] = new File("d:/test/test-02.flv");
//		files[2] = new File("d:/test/test-02.flv");
//		files[3] = new File("d:/test/test-03.flv");
//		files[4] = new File("d:/test/test.flv");
		File target = new File("target/tmp.flv");
		FlvUtil.mergeFlv(files, target);
	}
	
	@Test
	public void resolv() throws FileNotFoundException, IOException{
		File file =  new File("target/youku/test-04.flv");
		FlvUtil.test(file);
		
	}
}
