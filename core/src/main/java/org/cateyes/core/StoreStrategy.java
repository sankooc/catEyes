package org.cateyes.core;

import java.util.Collection;

import org.cateyes.core.entity.Volumn;

public interface StoreStrategy {
	Collection<Volumn> findAll();

	void attachVolumn(Volumn volumn);
}
