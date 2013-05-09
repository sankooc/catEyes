package org.cateyes.core.flv;

import java.io.File;
import java.io.IOException;

import org.cateyes.core.util.CommonUtils;
import org.junit.Test;

public class FlvMergeDemo {
	
	@Test
	public void merge() throws IOException{
		File[] files = new File[2];
		files[0] = new File("d:/test/test-00.flv");
		files[1] = new File("d:/test/test-01.flv");
		File target = new File("target/tmp.flv");
		CommonUtils.mergeFlv(files, target);
	}
}
