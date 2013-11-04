package org.cateyes.core.volumn;

import java.io.File;
import java.util.Collection;

import org.cateyes.core.IHeader;
import org.cateyes.core.VideoConstants.Provider;

/**
 * @author sankooc
 */
public interface Volumn extends IHeader {

	class VolumnDownloadResult {
		final boolean complete;
		final Collection<File> source;
		final File folder;
		final String title;

		public VolumnDownloadResult(boolean complete, Collection<File> source, File folder, String title) {
			super();
			this.complete = complete;
			this.source = source;
			this.folder = folder;
			this.title = title;
		}

		public boolean isComplete() {
			return complete;
		}

		public Collection<File> getSource() {
			return source;
		}

		public File getFolder() {
			return folder;
		}

		public String getTitle() {
			return title;
		}

	}

	Provider getProvider();

	void addFragment(int quality, String suffix, String url);

	void addFragment(int quality, String suffix, String url, long size);

	void addFragment(int quality, String url, long size);

	VolumnDownloadResult writeLowQuality(File dir, String title) throws Exception;

	VolumnDownloadResult write(File dir, String title, int quality) throws Exception;

	VolumnDownloadResult writeHighQuality(File dir, String title) throws Exception;

	int getQualityCount();

	Collection<String> getFragmentURL(int quality);

	String getTitle();

}
