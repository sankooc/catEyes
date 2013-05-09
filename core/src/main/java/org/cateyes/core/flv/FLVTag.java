package org.cateyes.core.flv;

import java.io.DataInputStream;

public class FLVTag {
	public enum TagType {
		VIDEO, AUDIO, SCRIPTDATA
	}
	private TagType type;
//	private FLVTag preTag;
	private long time;
	
//	public static FLVTag read(DataInputStream stream){
//		
//	}
	
	public FLVTag(int type,long time) {
		this.time = time;
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
}
