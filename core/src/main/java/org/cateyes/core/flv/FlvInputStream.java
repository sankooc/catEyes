package org.cateyes.core.flv;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.cateyes.core.util.DataStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author sankooc
 */
public class FlvInputStream extends DataInputStream {
	int offSet;
	static Logger logger = LoggerFactory.getLogger(FlvInputStream.class);

	public FlvInputStream(InputStream in) throws IOException {
		super(in);
		assert read() == 'F' && read() == 'L' && read() == 'V';
		assert 0x01 == readUnsignedByte();
		readUnsignedByte();
		assert 9 == readInt();
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
		int type = read();
		if (type == -1) {
			return null;
		}
		assert type / 2 == 4;
		int dataSize = DataStreamUtils.readUInt24(this);
		long time = DataStreamUtils.readTime(this);
//		System.out.println("time:" + time);
		DataStreamUtils.readUInt24(this);
		byte[] data = new byte[dataSize];
		read(data);
		return new FLVTag(type, time,data);
	}

	@SuppressWarnings("unchecked")
	public EcmaArray<String, Object> readMetadata() throws IOException {
		DataStreamUtils.readUInt32(this);
		assert 0x12 == read();
		int dataSize = DataStreamUtils.readUInt24(this); // body length
		// System.out.println(dataSize);
		DataStreamUtils.readUInt32(this); // timestamp
		DataStreamUtils.readUInt24(this); // streamid
		byte[] data = new byte[dataSize];
		read(data);
		AMFInputStream ais = new AMFInputStream(new ByteArrayInputStream(data));
		Object obj = ais.getNextObject();
		assert "onMetaData".equals(obj);
		return (EcmaArray<String, Object>) ais.getNextObject();
	}

}
