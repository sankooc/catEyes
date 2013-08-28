package org.cateyes.core.flv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

public class Mega4Media {
	
	@Test
	public void testmege() throws Exception, IOException {
		// LOL SUMMER LESSON SUP-01.mp4

		String[] files = { "target/youku/LOL SUMMER LESSON SUP-01.mp4",
				"target/youku/LOL SUMMER LESSON SUP-02.mp4",
				"target/youku/LOL SUMMER LESSON SUP-03.mp4",
				"target/youku/LOL SUMMER LESSON SUP-04.mp4",
				"target/youku/LOL SUMMER LESSON SUP-05.mp4" };

		List<Track> videoTracks = new LinkedList<Track>();
		List<Track> audioTracks = new LinkedList<Track>();
		for (int i = 0; i < files.length; i++) {
			Movie movie = MovieCreator.build(Channels
					.newChannel(new FileInputStream(files[i])));
			for (Track t : movie.getTracks()) {
				if (t.getHandler().equals("soun")) {
					audioTracks.add(t);
				} else if (t.getHandler().equals("vide")) {
					videoTracks.add(t);
				}
			}
		}

		Movie result = new Movie();
		
        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        IsoFile out = new DefaultMp4Builder().build(result);
		FileOutputStream fos = new FileOutputStream(new File(
				"target/youku/output.mp4"));
		out.getBox(fos.getChannel());
		fos.close();
	}
}
