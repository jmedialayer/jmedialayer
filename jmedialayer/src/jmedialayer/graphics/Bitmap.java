package jmedialayer.graphics;

public class Bitmap {
    private final int width;
    private final int height;

    public Bitmap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
