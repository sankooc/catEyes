package org.cateyes.core.flv;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.cateyes.core.flv.FLVTag.TagType;
import org.cateyes.core.flv.utils.DataStreamUtils;

public class FlvOutputStream extends DataOutputStream {

	File file;
	long counter;
	public FlvOutputStream(File target) throws IOException {
		super(new FileOutputStream(target));
		this.file = target;
		write('F');
		write('L');
		write('V');
		write(1);
		write(5);
		writeInt(9);
		counter  = 9;
	}
	
	public void writeMetadata(FMetadata metadata) throws IOException{
		write(metadata.toBytes2());// metadata
	}
	
	long pretime;
	long presize;
	
	public void writeTag(FLVTag tag){
		
//			int pr = readInt();
//			if (-1 == presize) {
//				presize = pr;
//			}
//			writeInt((int) presize);
//
//			TagType type = tag.getType();
//			
//			if (-1 == type) {
//				break;
//			}
//			assert type / 2 == 4;
//			out.write(type);
//			int dataSize = DataStreamUtils.copyAndReadUInt24(this, out);
//			presize = dataSize + 18;
//
//			long time = DataStreamUtils.readTime(this) + pt;
//			DataStreamUtils.writeTime(this, time);
//
//			DataStreamUtils.copyAndReadUInt24(, this);
//
//			byte[] data = new byte[dataSize];
//			read(data);
//			out.write(data);
		//TODO
	}
	
	public void rebuild(Collection<FlvInputStream> flvs) throws IOException{
		double startTime = 0;
		FMetadata metatdata = null;
		for(FlvInputStream fis : flvs){
			EcmaArray<String, Object> ecma = fis.readMetadata();
			FMetadata mta =  new FMetadata(ecma);
			double duration = mta.getDoubleValue("duration");//time
			if (null == metatdata) {
				metatdata = mta;
			} else {
				metatdata.update(ecma);
			}
			//loop
			startTime +=duration;
		}
	}
	public static final int initMeta = 289;
	void addMetadata(FMetadata metatdata) throws IOException{
		List<Double> tlist = metatdata.getTimes();
		List<Double> plist = metatdata.getPosition();
		int totalsize = tlist.size()*18+initMeta;
		byte[] data = metatdata.toBytes2();
	}
	
	
}
