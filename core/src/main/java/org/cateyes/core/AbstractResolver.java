package org.cateyes.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractResolver implements Resolver {

	abstract protected String[] getRegexStrings();

	private Pattern[] patterns;

	// private ApacheConnector connector;

	protected AbstractResolver() {
		String[] regx = getRegexStrings();
		if (null == regx) {
			patterns = new Pattern[0];
			return;
		}
		patterns = new Pattern[regx.length];
		for (int i = 0; i < regx.length; i++) {
			patterns[i] = Pattern.compile(regx[i]);
		}
	}

	public boolean isPrefer(String uri) {
		for (Pattern pattern : patterns) {
			Matcher matcher = pattern.matcher(uri);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
}
