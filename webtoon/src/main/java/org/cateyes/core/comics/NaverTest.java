/**
 * 
 */
package org.cateyes.core.comics;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
/**
 * @author sankooc
 *
 */
public class NaverTest {

	@Test
	public void test() throws InterruptedException, IOException{
		Naver naver = new Naver();
		naver.download("22045", new File("d:/comics/mams"), 1, 20);
	}
}
