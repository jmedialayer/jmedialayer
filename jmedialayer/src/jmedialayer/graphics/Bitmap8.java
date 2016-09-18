package jmedialayer.graphics;

final public class Bitmap8 extends Bitmap {
	public final byte[] data;
	public int[] palette;

	public Bitmap8(int width, int height, byte[] data, int[] palette) {
		super(width, height);
		this.data = data;
		this.palette = palette;
	}

	public Bitmap8(int width, int height, byte[] data) {
		this(width, height, data, new int[0x100]);
	}

	public Bitmap8(int width, int height) {
		this(width, height, new byte[width * height], new int[0x100]);
	}

	public int locateColor(int color) {
		for (int n = 0; n < palette.length; n++) if (palette[n] == color) return n;
		return 0;
	}

	@Override
	public int get32(int x, int y) {
		return palette[get(x, y)];
	}

	@Override
	public void set32(int x, int y, int value) {
		set(x, y, locateColor(value));
	}

	@Override
	public int get(int x, int y) {
		return data[index(x, y)];
	}

	@Override
	public void set(int x, int y, int value) {
		data[index(x, y)] = (byte) value;
	}
}
