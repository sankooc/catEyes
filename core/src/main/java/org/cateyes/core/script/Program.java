package org.cateyes.core.script;

import java.net.URI;

public class Program {
	public URI url;
	public URI logo;
	public String name;

	public Program(URI url) {
		this.url = url;
	}

	public URI getUrl() {
		return url;
	}

	public void setUrl(URI url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
