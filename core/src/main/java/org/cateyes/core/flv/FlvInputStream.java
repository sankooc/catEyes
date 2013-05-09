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
//		assert read() == 'F' && read() == 'L' && read() == 'V';
//		assert 0x05 != readUnsignedByte();
//		int typeFlags =readUnsignedByte();
//		logger.debug(Integer.toBinaryString(typeFlags));
//		assert 9 == readInt();
	}

	public void readMetadata() throws IOException {
		assert read() == 'F' && read() == 'L' && read() == 'V';
		assert 0x05 != readUnsignedByte();
		int typeFlags =readUnsignedByte();
		logger.debug(Integer.toBinaryString(typeFlags));
		assert 9 == readInt();
		
		DataStreamUtils.readUInt32(this);
		assert 0x12 == read();
		int dataSize = DataStreamUtils.readUInt24(this); // body length
		DataStreamUtils.readUInt32(this); // timestamp
		DataStreamUtils.readUInt24(this); // streamid
		byte[] data = new byte[dataSize];
		read(data);
		AMFInputStream ais = new AMFInputStream(new ByteArrayInputStream(data));
		Object obj = ais.getNextObject();
		logger.info(obj.toString());
	}

}
