package org.cateyes.core.flv;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.cateyes.core.util.DataStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlvInputStream extends DataInputStream {
	int offSet;
	static Logger logger = LoggerFactory.getLogger(FlvInputStream.class);

	public FlvInputStream(InputStream in) throws IOException {
		super(in);
		// assert read() == 'F' && read() == 'L' && read() == 'V';
		// assert 0x05 != readUnsignedByte();
		// int typeFlags =readUnsignedByte();
		// logger.debug(Integer.toBinaryString(typeFlags));
		// assert 9 == readInt();
		assert read() == 'F' && read() == 'L' && read() == 'V';
		assert 0x05 != readUnsignedByte();
		int typeFlags = readUnsignedByte();
//		logger.debug(Integer.toBinaryString(typeFlags));
		assert 9 == readInt();
		DataStreamUtils.readUInt32(this);
	}

	
	public void readTag() throws IOException{
		int type = read();
		int dataSize = DataStreamUtils.readUInt24(this);
		long time = DataStreamUtils.readTime(this);
		System.out.println(time);
		DataStreamUtils.readUInt24(this);
		byte[] data = new byte[dataSize];
		read(data);
//		return new FLVTag(type,time);
	}
	
	@SuppressWarnings("unchecked")
	public EcmaArray<String,?> readMetadata() throws IOException {
		assert 0x12 == read();
		int dataSize = DataStreamUtils.readUInt24(this); // body length
//		System.out.println(dataSize);
		DataStreamUtils.readUInt32(this); // timestamp
		DataStreamUtils.readUInt24(this); // streamid
		byte[] data = new byte[dataSize];
		read(data);
		AMFInputStream ais = new AMFInputStream(new ByteArrayInputStream(data));
		Object obj = ais.getNextObject();
		assert "onMetaData".equals(obj);
		return (EcmaArray<String, ?>) ais.getNextObject();
	}

}
