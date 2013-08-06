/**
 * 
 */
package org.cateyes.core;

import java.util.Map;

import junit.framework.Assert;

import org.cateyes.core.entity.Volumn;
import org.cateyes.core.feng.FengResolver;
import org.junit.Test;

/**
 * @author sankooc
 * 
 */
public class ResolverTest {

	ApacheConnector connector = ConnectorProvider.getCommonConnector();

	protected void test(Resolver resolver, String url) {
		try {
			Assert.assertTrue(resolver.isPrefer(url));
			Volumn volumn = resolver.createVolumn(url);
			Assert.assertNotNull(volumn);
			Map<String, Long> set = volumn.getUrlSet();
			Assert.assertFalse(set.isEmpty());
			for (String uri : set.keySet()) {
				long size = set.get(uri);
				long ret = connector.getResourceLength(uri);
				Assert.assertTrue((size < 0) || size == ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test() {
		String uri = "http://v.ifeng.com/mil/arms/201308/09e7f40c-c591-46e8-8df0-3f926043e7e9.shtml";
		Resolver resolver = new FengResolver();
		test(resolver, uri);
	}

}
