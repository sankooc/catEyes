package org.cateyes.core.script;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public interface SiteScript {
	Program getResource(String url) throws Exception;

	class Program {
		private String source;
		private String title;
		private Map<String, Collection<String>> list = new LinkedHashMap<String, Collection<String>>();

		public Program(String source, String title) {
			super();
			this.source = source;
			this.title = title;
		}

		public Map<String, Collection<String>> getList() {
			return list;
		}

		public String getSource() {
			return source;
		}

		public String getTitle() {
			return title;
		}
	}
}
