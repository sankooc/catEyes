package org.cateyes.core.flv;

import java.util.HashMap;
/**
 * TODO 무엇에 쓰는 물건인고?
 * @author sankooc
 */
public class EcmaArray<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = -2230034371710028690L;
	final int count;

	public EcmaArray(int count) {
		super();
		this.count = count;
	}

	public int getCount() {
		return count;
	}
}
