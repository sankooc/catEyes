package org.cateyes.core.flv;

/**
 * @author sankooc
 */
public class FLVTag {
	public enum TagType {
		VIDEO, AUDIO, SCRIPTDATA
	}
	private TagType type;
	private byte[] data;
//	private FLVTag preTag;
	private long time;
	
//	public static FLVTag read(DataInputStream stream){
//		
//	}
	
	public FLVTag(int type,long time,byte[] data) {
		this.time = time;
		this.data = data;
		switch (type) {
		case 0x09:
			this.type = TagType.VIDEO;
			break;
		case 0x08:
			this.type = TagType.AUDIO;
			break;
		case 0x12:
			this.type = TagType.SCRIPTDATA;
			break;
		}
	}

	public TagType getType() {
		return type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
