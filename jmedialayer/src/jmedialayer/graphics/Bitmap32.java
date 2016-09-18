package jmedialayer.graphics;

import com.jtransc.annotation.JTranscInline;
import com.jtransc.annotation.JTranscMethodBody;
import jmedialayer.util.FastMemInt;

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
	// Improved here: https://github.com/jtransc/jtransc/commit/01a9891cfcfc676ffdb81de30820d6321c50359c
	// Also it is slow probably related to dynamic_cast + using shared_ptr in all fields and not just where required.
	// But at least filling and copying should be fast. And future jtrnasc version will totally avoid those stuff and will be faster too.
	@Override
	//@JTranscMethodBody(target = "cpp", value = {
	//	"int32_t from = p0;",
	//	"int32_t to = p1;",
	//	"int32_t value = p2;",
	//	"int32_t *ptr = (int32_t *)((JA_I *)this->{% FIELD jmedialayer.graphics.Bitmap32:data %}.get())->getOffsetPtr(0);",
	//	"for (int n = from; n < to; n++) ptr[n] = value;",
	//})
	protected void fill(int from, int to, int value) {
		FastMemInt.selectSRC(this.data);
		for (int n = from; n < to; n++) FastMemInt.setSRC(n, value);
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
	//@JTranscMethodBody(target = "cpp", value = "(this->{% FIELD jmedialayer.graphics.Bitmap32:data %}.get())")
	public int get(int x, int y) {
		return data[index(x, y)];
	}

	@Override
	public void set(int x, int y, int value) {
		data[index(x, y)] = value;
	}

	private void putdata(int index, int[] value, int offset, int count) {
		System.arraycopy(value, offset, this.data, index, count);
	}

	private void putdataMixed(int index, int[] value, int offset, int count) {
		setMixed(value, offset, this.data, index, count);
	}

	static private void setMixed(int[] src, int srcOffset, int[] dst, int dstOffset, int count) {
		FastMemInt.selectDST(dst);
		FastMemInt.selectSRC(src);
		for (int n = 0; n < count; n++) {
			FastMemInt.setDST(dstOffset + n, mix(FastMemInt.getDST(dstOffset + n), FastMemInt.getSRC(srcOffset + n)));
			//FastMemInt.setDST(dstOffset + n, 0xFFFFFFFF);
		}
	}

	public void put(int x, int y, Bitmap32 bmp, boolean mix) {
		final int[] bmp_data = bmp.data;
		final int width = bmp.width;
		final int height = bmp.height;
		final int this_width = this.width;
		final int this_height = this.height;

		int left = clamp(x, 0, this_width);
		int right = clamp(x + width, 0, this_width);
		int wcount = right - left;

		for (int my = 0; my < height; my++) {
			int row = my + y;
			if (row < 0 || row >= this_height) continue;
			if (mix) {
				putdataMixed(index(left, row), bmp_data, my * width, wcount);
			} else {
				putdata(index(left, row), bmp_data, my * width, wcount);
			}
		}
	}

	@JTranscInline
	static private int getA(int rgba) {
		return (rgba >>> 24);
	}

	@JTranscInline
	static private int packRGB_A(int rgb, int a) {
		return (rgb & 0xFFFFFF) | (a << 24);
	}

	@JTranscInline
	static private int clampFF(int v) {
		return clamp(v, 0, 0xFF);
	}

	@JTranscInline
	static private int blend(int c1, int c2, int factor) {
		int f1 = 256 - factor;
		return ((((((c1 & 0xFF00FF) * f1) + ((c2 & 0xFF00FF) * factor)) & 0xFF00FF00) | ((((c1 & 0x00FF00) * f1) + ((c2 & 0x00FF00) * factor)) & 0x00FF0000))) >>> 8;
	}

	static private int mix(int dst, int src) {
		int a = getA(src);
		if (a <= 0) return dst;
		if (a >= 0xFF) return src;
		return packRGB_A(
			blend(dst, src, a * 256 / 255),
			clampFF(getA(dst) + getA(src))
		);
	}
}
