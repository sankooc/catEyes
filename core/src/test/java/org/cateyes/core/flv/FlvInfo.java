package org.cateyes.core.flv;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class FlvInfo {
	final FMetadata metadata;
	final FLVTag vedeohead;
	final FLVTag audeohead;
	long tagsize;
	double duration;
	private LinkedList<FlvInputStream> list = new LinkedList<FlvInputStream>();
	public FlvInfo(File file) throws IOException{
		FlvInputStream fis = new FlvInputStream(file);
		this.metadata = fis.readMetadata2();
		vedeohead = fis.readTag();
		audeohead = fis.readTag();
		long offset = fis.getCursor();
		
		metadata.resetPos(offset);//fix remove 0
		this.duration = metadata.getduration();
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
		metadata.resetPos(offset);
		metadata.resetTimes(this.duration);
		
		this.duration += metadata.getduration();
		this.tagsize += tagsize;
		this.metadata.append(metadata);
	}
	
	public void write(File target) throws IOException{
		target.getParentFile().mkdirs();
		
		FlvOutputStream fos = new FlvOutputStream(target);
		
		metadata.resetPos(-tagsize);
		
		fos.writeMetadata(metadata);
		
		fos.writeTag(vedeohead);
		fos.writeTag(audeohead);
		
		for(FlvInputStream fis : list){
			FLVTag tag = fis.readTag();
			fos.writeTag(tag);
		}
		fos.flush();
		fos.close();
	}
	
}
