package org.cateyes.core.flv;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.cateyes.core.flv.util.DataStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author sankooc
 */
public class FlvInputStream extends DataInputStream {
//	int offSet;
	static Logger logger = LoggerFactory.getLogger(FlvInputStream.class);
	long avi;
	int counter = 0;
	public FlvInputStream(File file) throws IOException {
		super(new FileInputStream(file));
		avi = file.length();
		assert read() == 'F' && read() == 'L' && read() == 'V';
		assert 0x01 == readUnsignedByte();
		readUnsignedByte();
		assert 9 == readInt();
		counter  = 9;
	}
	
	public FlvInputStream(InputStream in) throws IOException {
		super(in);
		assert read() == 'F' && read() == 'L' && read() == 'V';
		assert 0x01 == readUnsignedByte();
		readUnsignedByte();
		assert 9 == readInt();
		counter  = 9;
	}

	public double copyTag(DataOutputStream out, double pretime, double presize)
			throws IOException {
		long pt = (long) (pretime * 100);
		while (true) {
			int pr = readInt();
			if (-1 == presize) {
				presize = pr;
			}
			out.writeInt((int) presize);

			int type = read();
			if (-1 == type) {
				break;
			}
			assert type / 2 == 4;
			out.write(type);
			int dataSize = DataStreamUtils.copyAndReadUInt24(this, out);
			presize = dataSize + TAG_INCREASE;

			long time = DataStreamUtils.readTime(this) + pt;
			DataStreamUtils.writeTime(out, time);

			DataStreamUtils.copyAndReadUInt24(this, out);

			byte[] data = new byte[dataSize];
			read(data);
			out.write(data);
		}
		out.flush();
		return presize;
	}

	public static final int TAG_INCREASE = 11;
	

	public FLVTag readTag() throws IOException {
		long presize = DataStreamUtils.readUInt32(this);
		counter+=4;
		long pos = counter;
		int type = read();
		counter++;
		if (type == -1) {
			return null;
		}
		assert type / 2 == 4;
		int dataSize = DataStreamUtils.readUInt24(this);
		long time = DataStreamUtils.readTime(this);
		counter+=7;
//		System.out.println("time:" + time);
		DataStreamUtils.readUInt24(this);
		counter+=3;
		byte[] data = new byte[dataSize];
		read(data);
		counter+=dataSize;
		return new FLVTag(type, time,data,pos);
	}

	@SuppressWarnings("unchecked")
	public EcmaArray<String, Object> readMetadata() throws IOException {
		DataStreamUtils.readUInt32(this);
		assert 0x12 == read();
		int dataSize = DataStreamUtils.readUInt24(this); // body length
		DataStreamUtils.readUInt32(this); // timestamp
		DataStreamUtils.readUInt24(this); // streamid
		counter+=15;
		byte[] data = new byte[dataSize];
		read(data);
		counter+=dataSize;
		AMFInputStream ais = new AMFInputStream(new ByteArrayInputStream(data));
		Object obj = ais.getNextObject();
		assert "onMetaData".equals(obj);
		return (EcmaArray<String, Object>) ais.getNextObject();
	}

	public FMetadata readMetadata2() throws IOException{
		EcmaArray<String, Object> mta =  readMetadata();
		FMetadata metadata = new FMetadata(mta);
		metadata.setTotleSize(avi);
		return metadata;
	}
	
}
