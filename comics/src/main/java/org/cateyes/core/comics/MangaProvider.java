package org.cateyes.core.comics;

import java.net.URI;

public interface MangaProvider {
	int getTotle();

	URI get(int volumn);

	URI getIcon();
	
	String getTitle();
}
