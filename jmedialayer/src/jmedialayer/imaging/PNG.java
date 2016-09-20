package jmedialayer.imaging;

import com.jtransc.io.JTranscIoTools;
import com.jtransc.io.ra.RAByteArray;
import com.jtransc.io.ra.RAStream;
import jmedialayer.graphics.Bitmap;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.Bitmap8;
import jmedialayer.graphics.RGBA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.InflaterInputStream;

@SuppressWarnings({"PointlessBitwiseExpression", "PointlessArithmeticExpression"})
public class PNG extends ImageFormat {
	static private final int MAGIC1 = 0x89504E47;
	static private final int MAGIC2 = 0x0D0A1A0A;

	static private final int COLOR_GRAYSCALE = 0;
	static private final int COLOR_RGB = 2;
	static private final int COLOR_INDEXED = 3;
	static private final int COLOR_GRAYSCALE_ALPHA = 4;
	static private final int COLOR_RGBA = 6;

	@Override
	public boolean check(byte[] data) {
		RAStream s = new RAByteArray(data);
		int m1 = s.readS32_BE();
		int m2 = s.readS32_BE();
		return (m1 == MAGIC1) && (m2 == MAGIC2);
	}

	static private Chunk readChunk(RAStream s) {
		int len = s.readS32_BE();
		String type = new String(s.readBytes(4L));
		byte[] data = s.readBytes(len);
		int crc = s.readS32_BE();
		return new Chunk(type, data);
	}

	@Override
	public Bitmap read(byte[] data) throws IOException {
		RAStream s = new RAByteArray(data);
		s.skip(8L);
		Header h = new Header();
		int[] palette = new int[0];

		ByteArrayOutputStream compressed = new ByteArrayOutputStream();

		//System.out.println("---");
		mainloop:
		while (s.getAvailable() > 0L) {
			Chunk chunk = readChunk(s);
			RAStream ss = new RAByteArray(chunk.data);
			//System.out.println(chunk.type);
			switch (chunk.type) {
				case "IHDR": {
					h.width = ss.readS32_BE();
					h.height = ss.readS32_BE();
					h.bits = ss.readU8_BE();
					h.colorspace = ss.readU8_BE();
					h.compressionmethod = ss.readU8_BE();
					h.filtermethod = ss.readU8_BE();
					h.interlacemethod = ss.readU8_BE();
					break;
				}
				case "PLTE": {
					palette = Arrays.copyOf(palette, (int)ss.length() / 3);
					for (int n = 0; n < palette.length; n++) {
						int r = ss.readU8_LE();
						int g = ss.readU8_LE();
						int b = ss.readU8_LE();
						int a = 0xFF;
						palette[n] = (r << 0) | (g << 8) | (b << 16) | (a << 24);
					}
					break;
				}
				case "tRNS": {
					palette = Arrays.copyOf(palette, (int)ss.length() / 1);
					for (int n = 0; n < palette.length; n++) {
						palette[n] = (palette[n] & 0xFFFFFF) | (ss.readU8_BE() << 24);
					}
					break;
				}
				case "IDAT": {
					compressed.write(ss.readAvailableBytes());
					break;
				}
				case "IEND": {
					break mainloop;
				}
			}
		}
		return decodeImage(h, compressed.toByteArray(), palette);
	}

	private Bitmap decodeImage(Header h, byte[] compressedData, int[] palette) throws IOException {
		final int BPP = h.getBytes();
		final int width = h.width;
		final int height = h.height;
		final int area = width * height;
		final int stride = BPP * width;
		byte[] lastRow = new byte[stride];
		byte[] currentRow = new byte[stride];
		final InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(compressedData));
		final byte[] databytes = new byte[(1 + BPP * h.width) * h.height];
		JTranscIoTools.readFully(iis, databytes, 0, databytes.length);
		final RAByteArray data = new RAByteArray(databytes);

		Bitmap8 bmp8 = null;
		Bitmap32 bmp32 = null;
		if (BPP == 1) {
			bmp8 = new Bitmap8(width, height);
			bmp8.palette = palette;
		} else {
			bmp32 = new Bitmap32(width, height);
		}

		for (int y = 0; y < height; y++) {
			int filter = data.readU8_LE();
			data.read(currentRow, 0, stride);
			if (filter != 0) applyFilter(filter, lastRow, currentRow, BPP);

			if (bmp8 != null) {
				int m = 0;
				for (int x = 0; x < width; x++) {
					int v = currentRow[m++] & 0xFF;
					bmp8.set(x, y, v);
				}
			} else {
				switch (BPP) {
					case 3: {
						int m = 0;
						for (int x = 0; x < width; x++) {
							int r = currentRow[m++] & 0xFF;
							int g = currentRow[m++] & 0xFF;
							int b = currentRow[m++] & 0xFF;
							bmp32.set(x, y, RGBA.packRGBA_Fast(r, g, b, 0xFF));
						}
						break;
					}
					case 4: {
						int m = 0;
						for (int x = 0; x < width; x++) {
							int r = currentRow[m++] & 0xFF;
							int g = currentRow[m++] & 0xFF;
							int b = currentRow[m++] & 0xFF;
							int a = currentRow[m++] & 0xFF;
							bmp32.set(x, y, RGBA.packRGBA_Fast(r, g, b, a));
						}
						break;
					}
				}
			}

			// Flip rows
			byte[] temp = currentRow;
			currentRow = lastRow;
			lastRow = temp;
		}

		return (bmp8 != null) ? bmp8 : bmp32;
	}

	static private void applyFilter(int filter, byte[] p, byte[] c, int bpp) {
		switch (filter) {
			case 0: {
				break;
			}
			case 1: {
				for (int n = bpp; n < c.length; n++) c[n] += (c[n - bpp] & 0xFF);
				break;
			}
			case 2: {
				for (int n = 0; n < c.length; n++) c[n] += (p[n] & 0xFF);
				break;
			}
			case 3: {
				for (int n = 0; n < bpp; n++) c[n] += (p[n] & 0xFF) / 2;
				for (int n = bpp; n < c.length; n++) c[n] += ((c[n - bpp] & 0xFF) + (p[n] & 0xFF)) / 2;
				break;
			}
			case 4: {
				for (int n = 0; n < bpp; n++) c[n] += (p[n] & 0xFF);
				for (int n = bpp; n < c.length; n++)
					c[n] += paethPredictor((c[n - bpp] & 0xFF), (p[n] & 0xFF), (p[n - bpp] & 0xFF));
				break;
			}
			default:
				throw new RuntimeException("Unimplemented png filter: " + filter);
		}
	}

	static private int paethPredictor(int a, int b, int c) {
		final int p = a + b - c;
		final int pa = Math.abs(p - a);
		final int pb = Math.abs(p - b);
		final int pc = Math.abs(p - c);
		if ((pa <= pb) && (pa <= pc)) return a;
		if (pb <= pc) return b;
		return c;
	}

	static private class Header {
		public int width;
		public int height;
		public int bits;
		public int colorspace;
		public int compressionmethod;
		public int filtermethod;
		public int interlacemethod;

		public int getBytes() {
			switch (colorspace) {
				case COLOR_GRAYSCALE:
					return 1;
				case COLOR_INDEXED:
					return 1;
				case COLOR_GRAYSCALE_ALPHA:
					return 2;
				case COLOR_RGB:
					return 3;
				case COLOR_RGBA:
					return 4;
			}
			return 1;
		}
	}

	static private class Chunk {
		public final String type;
		public final byte[] data;

		public Chunk(String type, byte[] data) {
			this.type = type;
			this.data = data;
		}
	}
}
