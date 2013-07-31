package org.cateyes.core.comics;

public class Manga {
	private int id;
	private String author;
	private String name;
	private String cover;
	private Volumn[] episode;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public Volumn[] getEpisode() {
		return episode;
	}
	public void setEpisode(Volumn[] episode) {
		this.episode = episode;
	}
	
}
