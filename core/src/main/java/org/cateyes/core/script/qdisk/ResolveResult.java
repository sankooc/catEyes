package org.cateyes.core.script.qdisk;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ResolveResult {
	public enum TYPE {
		FLV, MP4;
	}

	private TYPE type;
	private List<URI> list;

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public ResolveResult(TYPE type) {
		super();
		this.type = type;
	}

	public void addAll(Collection<URI> uris) {
		if (null == uris || uris.isEmpty()) {
			return;
		}
		if (this.list == null) {
			list = new LinkedList<URI>();
		}
		list.addAll(uris);
	}

	public void add(URI uri) {
		if (null == uri) {
			return;
		}
		if (this.list == null) {
			list = new LinkedList<URI>();
		}
		list.add(uri);
	}

	public URI get(int index) {
		if (null == list || list.size() <= index) {
			return null;
		}
		return list.get(index);
	}
}
