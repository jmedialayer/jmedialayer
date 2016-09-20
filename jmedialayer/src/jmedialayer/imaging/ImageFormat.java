package jmedialayer.imaging;

import jmedialayer.graphics.Bitmap;

import java.io.IOException;

public class ImageFormat {
	public boolean check(byte[] data) {
		throw new RuntimeException();
	}

	public Bitmap read(byte[] data) throws IOException {
		throw new RuntimeException();
	}

	static public class Info {
		public String name;
		public int bpp;
		public int width;
		public int height;
	}
}
