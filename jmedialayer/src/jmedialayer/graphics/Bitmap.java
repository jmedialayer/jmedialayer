package jmedialayer.graphics;

public class Bitmap {
    public final int width;
	public final int height;
	public final int area;

    public Bitmap(int width, int height) {
        this.width = width;
        this.height = height;
		this.area = width * height;
    }

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
}
