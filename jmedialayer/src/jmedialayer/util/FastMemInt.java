package jmedialayer.util;

import com.jtransc.annotation.JTranscAddHeader;
import com.jtransc.annotation.JTranscInline;
import com.jtransc.annotation.JTranscMethodBody;

@SuppressWarnings("JavacQuirks")
@JTranscAddHeader(target = "cpp", value = {
	"static int32_t *int_memSRC = NULL;",
	"static int32_t *int_memDST = NULL;",
	"static int32_t *int_memTMP = NULL;",
})
public class FastMemInt {
	static private int[] memSRC;
	static private int[] memDST;
	static private int[] memTMP;

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memSRC = (int32_t *)GET_OBJECT(JA_I, p0)->getOffsetPtr(0);")
	static public void selectSRC(int[] mem) {
		FastMemInt.memSRC = mem;
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memDST = (int32_t *)GET_OBJECT(JA_I, p0)->getOffsetPtr(0);")
	static public void selectDST(int[] mem) {
		FastMemInt.memDST = mem;
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memTMP = (int32_t *)GET_OBJECT(JA_I, p0)->getOffsetPtr(0);")
	static public void selectTMP(int[] mem) {
		FastMemInt.memTMP = mem;
	}

	///////////////////////////////////////////////////////////////////////

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memSRC[p0] = p1;")
	static public void setSRC(int index, int value) {
		FastMemInt.memSRC[index] = value;
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memDST[p0] = p1;")
	static public void setDST(int index, int value) {
		FastMemInt.memDST[index] = value;
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "int_memTMP[p0] = p1;")
	static public void setTMP(int index, int value) {
		FastMemInt.memTMP[index] = value;
	}

	/////////////////////////////////////////////////////////////////////////

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "return int_memSRC[p0];")
	static public int getSRC(int index) {
		return FastMemInt.memSRC[index];
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "return int_memDST[p0];")
	static public int getDST(int index) {
		return FastMemInt.memDST[index];
	}

	@JTranscInline
	@JTranscMethodBody(target = "cpp", value = "return int_memTMP[p0];")
	static public int getTMP(int index) {
		return FastMemInt.memTMP[index];
	}
}
