package org.cateyes.core;

import java.net.URI;

public interface Resolver {
	URI[] getResource(URI uri);
}
