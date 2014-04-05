package org.cateyes.core.media.utils;

import org.apache.commons.codec.binary.Base64;

public class CommonUtil {
	public static String base64encode(String str){
		Base64 base = new Base64();
		return base.encodeToString(str.getBytes());
	}
}
