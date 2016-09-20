package jmedialayer.imaging;

import com.jtransc.io.ra.RAByteArray;
import com.jtransc.io.ra.RAStream;
import jmedialayer.graphics.Bitmap;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.Bitmap8;
import jmedialayer.graphics.RGBA;
import jmedialayer.util.FastMemByte;
import jmedialayer.util.FastMemInt;

import java.io.IOException;
import java.util.Objects;

public class BMP extends ImageFormat {
	@Override
	public boolean check(byte[] b) {
		return Objects.equals(new String(new RAByteArray(b).readBytes(2L)), "BM");
	}

	@Override
	public Bitmap read(byte[] data) throws IOException {
		RAStream s = new RAByteArray(data);
		if (!Objects.equals(new String(s.readBytes(2L)), "BM")) throw new RuntimeException("Not a BMP file");

		// FILE HEADER
		int size = s.readS32_LE();
		int reserved1 = s.readS16_LE();
		int reserved2 = s.readS16_LE();
		int offBits = s.readS32_LE();
		// INFO HEADER
		int bsize = s.readS32_LE();
		int width = s.readS32_LE();
		int height = s.readS32_LE();
		int planes = s.readS16_LE();
		int bitcount = s.readS16_LE();
		int compression = s.readS32_LE();
		int sizeImage = s.readS32_LE();
		int pixelsPerMeterX = s.readS32_LE();
		int pixelsPerMeterY = s.readS32_LE();
		int clrUsed = s.readS32_LE();
		int clrImportant = s.readS32_LE();

		int bytecount = bitcount / 8;

		//System.out.println(bitcount);

		byte[] row = new byte[width * bytecount];

		Bitmap out = null;
		Bitmap8 out8 = null;
		Bitmap32 out32 = null;

		if (bitcount == 8) {
			out = out8 = new Bitmap8(width, height);
		} else {
			out = out32 = new Bitmap32(width, height);
			FastMemByte.selectSRC(row);
			FastMemInt.selectDST(out32.data);
		}

		if (bitcount == 8) {
			byte[] paletteData = s.readBytes(256 * 4L);
			int m = 0;
			for (int n = 0; n < 256; n++) {
				int b = paletteData[m++] & 0xFF;
				int g = paletteData[m++] & 0xFF;
				int r = paletteData[m++] & 0xFF;
				int a = paletteData[m++] & 0xFF;
				out8.palette[n] = RGBA.packRGBA_Fast(r, g, b, 0xFF);
			}
		}

		for (int y = 0; y < height; y++) {
			s.read(row, 0, row.length);
			int index = out.index(0, height - y - 1);

			int n = 0;
			switch (bitcount) {
				case 8: {
					System.arraycopy(row, 0, out8.data, index, width);
					break;
				}
				case 24: {
					for (int x = 0; x < width; x++) {
						int b = FastMemByte.getSRC_u(n++);
						int g = FastMemByte.getSRC_u(n++);
						int r = FastMemByte.getSRC_u(n++);
						int a = 0xFF;
						FastMemInt.setDST(index + x, RGBA.packRGBA_Fast(r, g, b, a));
					}
					break;
				}
				case 32: {
					for (int x = 0; x < width; x++) {
						int b = FastMemByte.getSRC_u(n++);
						int g = FastMemByte.getSRC_u(n++);
						int r = FastMemByte.getSRC_u(n++);
						int a = FastMemByte.getSRC_u(n++);
						FastMemInt.setDST(index + x, RGBA.packRGBA_Fast(r, g, b, a));
					}
					break;
				}
				default: {
					throw new RuntimeException("Unsupported BMP BPP: " + bitcount);
				}
			}
		}

		return out;
	}
}
