package jmedialayer.backends.browser;

import com.jtransc.annotation.JTranscAddFile;
import com.jtransc.annotation.JTranscAddMembers;
import com.jtransc.annotation.JTranscKeep;
import com.jtransc.target.Js;
import jmedialayer.backends.Backend;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.G1;

@SuppressWarnings("JavacQuirks")
@JTranscAddMembers(target = "js", value = {
	"this.canvas = null;",
	"this.ctx = null;",
})
@JTranscAddFile(target = "js", process = true, src = "jmedialayer/backends/browser/index.html", dst = "index.html")
public class BrowserBackend extends Backend {
	static public final int WIDTH = 960;
	static public final int HEIGHT = 544;

	@Override
	public int getNativeWidth() {
		return WIDTH;
	}

	@Override
	public int getNativeHeight() {
		return HEIGHT;
	}

	public BrowserBackend() {
		init_video();
	}

	public void init_video() {
		Js.v_raw("var _this = this;");
		Js.v_raw("var canvas = document.createElement('canvas');");
		Js.v_raw("canvas.width = 960;");
		Js.v_raw("canvas.height = 544;");
		Js.v_raw("document.body.appendChild(canvas);");
		Js.v_raw("this.canvas = canvas;");
		Js.v_raw("this.ctx = canvas.getContext('2d');");
		Js.v_raw("document.body.addEventListener('keyup', function(e) { _this['{% METHOD jmedialayer.backends.browser.BrowserBackend:handleKeyEvent %}'](false, e.keyCode); });");
		Js.v_raw("document.body.addEventListener('keydown', function(e) { _this['{% METHOD jmedialayer.backends.browser.BrowserBackend:handleKeyEvent %}'](true, e.keyCode); });");
	}

	private int pad_buttons;

	// @TODO: This shouldn't be neccessary! since this is referenced in init_video Js.raw methods
	@JTranscKeep
	private void handleKeyEvent(boolean pressing, int keyCode) {
		System.out.println("handleKeyEvent:" + pressing + "," + keyCode);
		pad_buttons = 0;
		int mask = convertToMask(keyCode);
		if (pressing) {
			pad_buttons |= mask;
		} else {
			pad_buttons &= ~mask;
		}
	}

	private int convertToMask(int keyCode) {
		/*
		switch (keyCode) {
			case 13: return PSP2_CTRL_START; // RETURN
			case 32: return PSP2_CTRL_SELECT; // SPACE
			case 37: return PSP2_CTRL_LEFT; // LEFT
			case 38: return PSP2_CTRL_UP; // UP
			case 39: return PSP2_CTRL_RIGHT; // RIGHT
			case 40: return PSP2_CTRL_DOWN; // DOWN
			case 87: return PSP2_CTRL_TRIANGLE; // W
			case 64: return PSP2_CTRL_SQUARE; // A
			case 83: return PSP2_CTRL_CROSS; // S
			case 68: return PSP2_CTRL_CIRCLE; // D
			case 81: return PSP2_CTRL_LTRIGGER; // Q
			case 69: return PSP2_CTRL_RTRIGGER; // E
		}
		*/
		return 0;
	}

	public void draw_pixels(int x, int y, int[] colors, int width, int height) {
		Js.v_raw("var data = this.ctx.createImageData(p3, p4);");
		Js.v_raw("var dd = data.data;");
		Js.v_raw("var area = p3 * p4;");
		Js.v_raw("for (var n = 0, m = 0; n < area; n++) {");
		Js.v_raw("	var c = p2.data[n];");
		Js.v_raw("	dd[m++] = (c >> 0) & 0xFF;");
		Js.v_raw("	dd[m++] = (c >> 8) & 0xFF;");
		Js.v_raw("	dd[m++] = (c >> 16) & 0xFF;");
		Js.v_raw("	dd[m++] = (c >> 24) & 0xFF;");
		Js.v_raw("}");
		Js.v_raw("this.ctx.putImageData(data, p0, p1);");
	}

	@Override
	protected G1 createG1() {
		return new G1() {
			@Override
			public void updateBitmap(Bitmap32 bmp) {
				draw_pixels(0, 0, bmp.data, bmp.width, bmp.height);
			}
		};
	}

	@Override
	public void loop(StepHandler step) {
		Js.v_raw("var _this = this;");
		Js.v_raw("var prev = Date.now();");
		Js.v_raw("function one() {");
		Js.v_raw("	var current = Date.now();");
		//Js.v_raw("	_this['{% METHOD jmedialayer.backends.browser.BrowserBackend:clear_screen %}']();");
		//Js.v_raw("	_this['{% METHOD jmedialayer.backends.browser.BrowserBackend:input_read %}']();");
		Js.v_raw("	p0['{% METHOD jmedialayer.backends.Backend@StepHandler:step %}']((current - prev)|0);");
		Js.v_raw("	prev = current;");
		Js.v_raw("	requestAnimationFrame(one);");
		Js.v_raw("};");
		Js.v_raw("one();");
	}
}
