package org.cateyes.core.comics;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.google.gson.Gson;

public class ComicsBuilder {
	File file;

	public ComicsBuilder(File file) {
		this.file = file;
	}

	Manga manga;

	public ComicsBuilder(Manga manga) {
		this.manga = manga;
	}

	Gson gson = new Gson();

	public void write(OutputStream out, Charset charset) throws IOException {
		String content = gson.toJson(manga, Manga.class);
		out.write(content.getBytes(charset));
	}

	public static void create(MangaProvider provider,int start,int end) {
		
	}

	public static void createMetadat(File root){
		
	}
	
	
}
