package jmedialayer.graphics;

public class Bitmap8 extends Bitmap {
    private final byte[] data;
    private final int[] palette;

    public Bitmap8(byte[] data, int width, int height, int[] palette) {
        super(width, height);
        this.data = data;
        this.palette = palette;
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
