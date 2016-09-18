package jmedialayer.graphics;

import com.jtransc.annotation.JTranscMethodBody;

import java.util.Arrays;

@SuppressWarnings({"UnnecessaryLocalVariable", "JavacQuirks"})
final public class Bitmap32 extends Bitmap {
	public final int[] data;

	public Bitmap32(int width, int height, int[] data) {
		super(width, height);
		this.data = data;
	}

	public Bitmap32(int width, int height) {
		this(width, height, new int[width * height]);
	}

	@Override
	public Object getRawData() {
		return data;
	}

	// @TODO: This shouldn't be necessary when jtransc is optimized!
	@Override
	@JTranscMethodBody(target = "cpp", value = {
		"int32_t from = p0;",
		"int32_t to = p1;",
		"int32_t value = p2;",
		"int32_t *ptr = (int32_t *)GET_OBJECT(JA_I, this->{% FIELD jmedialayer.graphics.Bitmap32:data %})->getOffsetPtr(0);",
		"for (int n = from; n < to; n++) ptr[n] = value;",
	})
	protected void fill(int from, int to, int value) {
		Arrays.fill(data, from, to, value);
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
