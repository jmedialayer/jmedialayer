package jmedialayer.graphics;

import java.util.Arrays;

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

    public int index(int x, int y) {
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

		for (int my = 0; my < height; my++) {
			int row = my + y;
			if (row < 0 || row >= this_height) continue;
			int left = clamp(x, 0, this_width);
			int right = clamp(x + width, 0, this_width);
			fill(index(left, row), index(right, row), color);
		}
	}

    static protected int clamp(int v, int min, int max) {
		if (v < min) return min;
		if (v > max) return max;
		return v;
	}
}
