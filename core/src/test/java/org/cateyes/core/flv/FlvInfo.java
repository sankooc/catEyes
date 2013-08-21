package org.cateyes.core.flv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class FlvInfo {
	final FMetadata metadata;
	final FLVTag vedeohead;
	final FLVTag audeohead;
	long tagsize;
	private LinkedList<FlvInputStream> list = new LinkedList<FlvInputStream>();
	public FlvInfo(File file) throws IOException{
		FlvInputStream fis = new FlvInputStream(file);
		this.metadata = fis.readMetadata2();
		vedeohead = fis.readTag();
		audeohead = fis.readTag();
		long offset = fis.getCursor();
		
		metadata.decrease(offset);//fix remove 0
		
		tagsize = file.length()- offset;
		list.push(fis);
	}
	
	public void append(File file) throws Exception{
		FlvInputStream fis = new FlvInputStream(file);
		FMetadata metadata = fis.readMetadata2();
		fis.readTag();
		fis.readTag();
		long offset = fis.getCursor() ;
		long tagsize = file.length()- offset;
		offset -=  tagsize;
		metadata.decrease(offset);
		this.metadata.append(metadata);
	}
	
	public void write(File target) throws FileNotFoundException{
		target.getParentFile().mkdirs();
		FileOutputStream stream = new FileOutputStream(target);
		//TODO
		// writemetadata
		// set offset
		// write tag from flvinpustream
		//close stream
		
	}
	
}
