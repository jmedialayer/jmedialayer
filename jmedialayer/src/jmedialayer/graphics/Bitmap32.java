package jmedialayer.graphics;

final public class Bitmap32 extends Bitmap {
    private final int[] data;

    public Bitmap32(int width, int height, int[] data) {
        super(width, height);
        this.data = data;
    }

	public Bitmap32(int width, int height) {
		this(width, height, new int[width * height]);
	}

    public int[] getData() {
		return data;
	}

    @Override
    public int get32(int x, int y) {
        return data[index(x, y)];
    }

    @Override
    public void set32(int x, int y, int value) {
        data[index(x, y)] = value;
    }

    @Override
    public int get(int x, int y) {
        return data[index(x, y)];
    }

    @Override
    public void set(int x, int y, int value) {
        data[index(x, y)] = value;
    }
}
