package org.cateyes.core.mp4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

public class Mp4Analyser {
	@Test
	public void anlayse() throws IOException{
		File file = new File("target/yyt/hy.mp4");
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		int length = raf.readInt();
		raf.seek(length);
		length = raf.readInt();
		System.out.println();
		
		
		
	}
}
