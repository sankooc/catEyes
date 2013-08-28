package org.cateyes.core.flv;

import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;

public class ContainerBox extends Box {
	public ContainerBox(FileChannel fc, long size) {
		super(fc, size);
	}

	List<String> containers = Arrays.asList(
            "moov",
            "trak",
            "mdia",
            "minf",
            "udta",
            "stbl"
    );

	@Override
	public void accept(BoxVisitor visitor) {
		// TODO Auto-generated method stub
		super.accept(visitor);
	}

}
