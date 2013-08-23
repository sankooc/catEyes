package org.cateyes.core.flv;

/**
 * @author sankooc
 */
public class FLVTag {
	public enum TagType {
		VIDEO, AUDIO, SCRIPTDATA
	}
	private int type;
	private byte[] data;
	private long time;
	private long position;
	long presize;
	public FLVTag(long presize ,int type,long time,byte[] data,long position) {
		this.presize = presize;
		this.time = time;
		this.data = data;
		this.position = position;
		this.type = type;
	}
	
//	public FLVTag(int type,long time,byte[] data,long position) {
//		this.time = time;
//		this.data = data;
//		this.position = position;
//		this.type = type;
////		switch (type) {
////		case 0x09:
////			this.type = TagType.VIDEO;
////			break;
////		case 0x08:
////			this.type = TagType.AUDIO;
////			break;
////		case 0x12:
////			this.type = TagType.SCRIPTDATA;
////			break;
////		}
//	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
}
