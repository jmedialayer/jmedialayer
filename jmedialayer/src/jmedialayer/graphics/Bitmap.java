package jmedialayer.graphics;

import com.jtransc.annotation.JTranscInline;
import jmedialayer.util.FastMemByte;
import jmedialayer.util.FastMemInt;

public abstract class Bitmap {
	public final int width;
	public final int height;
	public final int area;

	public Bitmap(int width, int height) {
		this.width = width;
		this.height = height;
		this.area = width * height;
	}

	abstract public Object getRawData();

	abstract protected void fill(int from, int to, int value);

	@JTranscInline
	final public int index(int x, int y) {
		return y * width + x;
	}

	public int get32(int x, int y) {
		return 0;
	}

	public void set32(int x, int y, int value) {
	}

	public int get(int x, int y) {
		return 0;
	}

	public void set(int x, int y, int value) {
	}

	public void fillrect(int x, int y, int width, int height, int color) {
		final int this_width = this.width;
		final int this_height = this.height;

		int left = clamp(x, 0, this_width);
		int right = clamp(x + width, 0, this_width);
		for (int my = 0; my < height; my++) {
			int row = my + y;
			if (row < 0 || row >= this_height) continue;
			fill(index(left, row), index(right, row), color);
		}
	}

	static protected int clamp(int v, int min, int max) {
		if (v < min) return min;
		if (v > max) return max;
		return v;
	}

	public void clear(int color) {
		fill(0, area, color);
	}

	public Bitmap32 toBitmap32() {
		if (this instanceof Bitmap32) {
			return (Bitmap32) this;
		} else if (this instanceof Bitmap8) {
			Bitmap8 bmp8 = (Bitmap8)this;
			Bitmap32 out = new Bitmap32(width, height);
			int area = out.area;

			FastMemByte.selectSRC(bmp8.data);
			FastMemInt.selectTMP(bmp8.palette);
			FastMemInt.selectDST(out.data);

			for (int n = 0; n < area; n++) FastMemInt.setDST(n, FastMemInt.getTMP(FastMemByte.getSRC(n) & 0xFF));
			return out;
		} else {
			throw new RuntimeException("Don't know how to handle " + this);
		}
	}
}
