package org.cateyes.core.util;

import org.jsoup.select.Elements;

public class JsoupUtils {
	public static boolean isEmpty(Elements eles) {
		if (null == eles || eles.isEmpty()) {
			return true;
		}
		return false;
	}
}
