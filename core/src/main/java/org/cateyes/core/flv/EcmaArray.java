package org.cateyes.core.flv;

import java.util.HashMap;
/**
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
