package org.cateyes.core.flv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.cateyes.core.media.MediaMerger;
import org.cateyes.core.media.io.FlvInputStream;
import org.cateyes.core.media.utils.MediaFileUtils;
import org.junit.Test;

public class FlvMergeDemo {
	
//	@Test
	public void info() throws IOException{
		FlvUtil.infotest(new File[]{
				new File("target/youku/test-01.flv"),
				new File("target/youku/test-02.flv"),
				new File("target/youku/test-03.flv"),
				new File("target/youku/test-04.flv")});
	}
	
	MediaMerger merger = new MediaMerger();
	@Test
	public void multiFile() throws Exception{
		File target = new File("target/tmp/");
		merger.mergeFlv(new File[]{
				new File("target/youku/test-01.flv"),
				new File("target/youku/test-02.flv"),
				new File("target/youku/test-03.flv"),
				new File("target/youku/test-04.flv")}, target,"target");
		FlvInputStream fis = new FlvInputStream(new FileInputStream(target));
		FMetadata metadata = fis.readMetadata();
		MediaFileUtils.checkFile(target, metadata);
	}
	
//	@Test
	public void singleFIle() throws Exception{
		File source = new File("target/youku/test-01.flv");
		File target = new File("target/tmp");
		
		merger.mergeFlv(new File[]{source}, target,"target");
		
		FlvInputStream fis = new FlvInputStream(new FileInputStream(target));
		FMetadata metadata = fis.readMetadata();
		MediaFileUtils.checkFile(target, metadata);
		
	}
	
//	@Test
	public void tag() throws FileNotFoundException, IOException{
		File file =  new File("target/tmp/target.flv");
		FlvUtil.test(file);
		
	}
}
