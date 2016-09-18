package jmedialayer.graphics;

import com.jtransc.annotation.JTranscInline;
import jmedialayer.util.FastMemInt;

@SuppressWarnings({"PointlessBitwiseExpression", "WeakerAccess"})
public final class RGBA {
	@JTranscInline
	static public int getR(int rgba) {
		return (rgba >>> 0) & 0xFF;
	}

	@JTranscInline
	static public int getG(int rgba) {
		return (rgba >>> 8) & 0xFF;
	}

	@JTranscInline
	static public int getB(int rgba) {
		return (rgba >>> 16) & 0xFF;
	}

	@JTranscInline
	static public int getA(int rgba) {
		return (rgba >>> 24) & 0xFF;
	}

	@JTranscInline
	static public int packRGB_A(int rgb, int a) {
		return (rgb & 0xFFFFFF) | (a << 24);
	}

	@JTranscInline
	static public int packRGBA_Fast(int r, int g, int b, int a) {
		return (r << 0) | (g << 8) | (b << 16) | (a << 24);
	}

	@JTranscInline
	static public int clampFF(int v) {
		return clamp(v, 0, 0xFF);
	}

	@JTranscInline
	static public int blend(int c1, int c2, int factor) {
		int f1 = 256 - factor;
		return ((((((c1 & 0xFF00FF) * f1) + ((c2 & 0xFF00FF) * factor)) & 0xFF00FF00) | ((((c1 & 0x00FF00) * f1) + ((c2 & 0x00FF00) * factor)) & 0x00FF0000))) >>> 8;
	}

	static public int mix(int dst, int src) {
		int a = getA(src);
		if (a <= 0) return dst;
		if (a >= 0xFF) return src;
		return packRGB_A(
			blend(dst, src, a * 256 / 255),
			clampFF(getA(dst) + getA(src))
		);
	}

	static private int clamp(int v, int min, int max) {
		if (v < min) return min;
		if (v > max) return max;
		return v;
	}

	static public void setMixed(int[] src, int srcOffset, int[] dst, int dstOffset, int count) {
		FastMemInt.selectDST(dst);
		FastMemInt.selectSRC(src);
		for (int n = 0; n < count; n++) {
			FastMemInt.setDST(dstOffset + n, mix(FastMemInt.getDST(dstOffset + n), FastMemInt.getSRC(srcOffset + n)));
			//FastMemInt.setDST(dstOffset + n, 0xFFFFFFFF);
		}
	}
}
