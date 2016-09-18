package jmedialayer.util;

import com.jtransc.annotation.JTranscAddHeader;
import com.jtransc.annotation.JTranscInline;
import com.jtransc.annotation.JTranscMethodBody;

@SuppressWarnings("JavacQuirks")
@JTranscAddHeader(target = "cpp", value = {
	"static int32_t *int_memA = NULL;",
	"static int32_t *int_memB = NULL;",
	"static int32_t *int_memC = NULL;",
})
public class FastMemInt {
	static private int[] memA;
	static private int[] memB;
	static private int[] memC;

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memA = (int32_t *)GET_OBJECT(JA_I, p0)->getOffsetPtr(0);")
	static public void selectA(int[] mem) {
		FastMemInt.memA = mem;
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memB = (int32_t *)GET_OBJECT(JA_I, p0)->getOffsetPtr(0);")
	static public void selectB(int[] mem) {
		FastMemInt.memB = mem;
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memC = (int32_t *)GET_OBJECT(JA_I, p0)->getOffsetPtr(0);")
	static public void selectC(int[] mem) {
		FastMemInt.memC = mem;
	}

	///////////////////////////////////////////////////////////////////////

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memA[p0] = p1;")
	static public void setA(int index, int value) {
		FastMemInt.memA[index] = value;
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memB[p0] = p1;")
	static public void setB(int index, int value) {
		FastMemInt.memB[index] = value;
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memC[p0] = p1;")
	static public void setC(int index, int value) {
		FastMemInt.memC[index] = value;
	}

	/////////////////////////////////////////////////////////////////////////

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "return int_memA[p0];")
	static public int getA(int index) {
		return FastMemInt.memA[index];
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "return int_memB[p0];")
	static public int getB(int index) {
		return FastMemInt.memB[index];
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "return int_memC[p0];")
	static public int getC(int index) {
		return FastMemInt.memC[index];
	}
}
