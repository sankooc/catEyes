package org.cateyes.core.flv;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class FlvInfo {
	final FMetadata metadata;
	final FLVTag vedeohead;
	final FLVTag audeohead;
	long tagsize;
	LinkedList<FlvInputStream> list = new LinkedList<FlvInputStream>();
	public FlvInfo(File file) throws IOException{
		FlvInputStream fis = new FlvInputStream(file);
		this.metadata = fis.readMetadata2();
		vedeohead = fis.readTag();
		audeohead = fis.readTag();
		long offset = fis.getCursor();
		
		metadata.decrease(offset);//fix remove 0
		
		tagsize = file.length()- offset;
	}
	
	public void append(){
		
	}
	
}
