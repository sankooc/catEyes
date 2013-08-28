package org.cateyes.core.flv;

import java.nio.channels.FileChannel;

public class Box {

	FileChannel fc;
	long size;

	public Box(FileChannel fc, long size) {
		this.fc = fc;
		this.size = size;
	}

	public void accept(BoxVisitor visitor) {
		
	}
}
