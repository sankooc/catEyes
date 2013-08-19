package org.cateyes.core.flv;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

public class FlvOutputStream extends DataOutputStream {

	long counter;
	public FlvOutputStream(OutputStream out) throws IOException {
		super(out);
		write('F');
		write('L');
		write('V');
		write(1);
		write(5);
		writeInt(9);
		counter  = 9;
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
