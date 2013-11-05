/**
 * 
 */
package org.cateyes.core.comics;

import java.io.File;

import org.cateyes.core.comics.webtoon.Naver;
/**
 * @author sankooc
 *
 */
public class NaverWebtoon {

//	@Test
	public void test() throws Exception{
		Naver naver = new Naver();
		naver.download("22045", new File("d:/comics/mams"), 1, 5);
	}
}
