package jmedialayer.imaging;

import jmedialayer.graphics.Bitmap;

public class ImageFormats {
	static public ImageFormat[] formats = {
		new PNG(),
		new BMP()
	};

	static public boolean check(byte[] data) {
		for (ImageFormat format : formats) {
			if (format.check(data)) return true;
		}
		return false;

	}

	static public Bitmap read(byte[] data) {
		for (ImageFormat format : formats) {
			try {
				if (format.check(data)) return format.read(data);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		throw new RuntimeException("Can't decode image: Unknown image format");
	}
}
