package jmedialayer.backends.browser;

import com.jtransc.annotation.JTranscAddFile;
import com.jtransc.annotation.JTranscAddMembers;
import com.jtransc.annotation.JTranscKeep;
import com.jtransc.annotation.JTranscMethodBody;
import com.jtransc.target.Js;
import jmedialayer.backends.Backend;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.G1;
import jmedialayer.input.Input;
import jmedialayer.input.Keys;
import jmedialayer.backends.ResourcePromise;

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

	@JTranscMethodBody(target = "js", value = {
		"var _this = this;",
		"var canvas = document.createElement('canvas');",
		"canvas.width = 960;",
		"canvas.height = 544;",
		"document.body.appendChild(canvas);",
		"this.canvas = canvas;",
		"this.ctx = canvas.getContext('2d');",
		"document.body.addEventListener('keyup', function(e) { _this['{% METHOD jmedialayer.backends.browser.BrowserBackend:handleKeyEvent %}'](false, e.keyCode); });",
		"document.body.addEventListener('keydown', function(e) { _this['{% METHOD jmedialayer.backends.browser.BrowserBackend:handleKeyEvent %}'](true, e.keyCode); });",
	})
	native public void init_video();

	//private int pad_buttons;
	private boolean[] pressing = new boolean[0x400];

	// @TODO: This shouldn't be neccessary! since this is referenced in init_video Js.raw methods
	@JTranscKeep
	private void handleKeyEvent(boolean pressing, int keyCode) {
		//System.out.println("handleKeyEvent:" + pressing + "," + keyCode);
		//pad_buttons = 0;
		this.pressing[keyCode & 0x3FF] = pressing;
		//int mask = convertToMask(keyCode);
		//if (pressing) {
		//	pad_buttons |= mask;
		//} else {
		//	pad_buttons &= ~mask;
		//}
	}

	private int convertToKeyCode(Keys key) {
		switch (key) {
			case LEFT:
				return 37;
			case UP:
				return 38;
			case RIGHT:
				return 39;
			case DOWN:
				return 40;
		}
		return 0;
	}

	private Keys convertToKeys(int keyCode) {
		switch (keyCode) {
			case 13:
				return Keys.START; // RETURN
			//case 32: return PSP2_CTRL_SELECT; // SPACE
			case 37:
				return Keys.LEFT; // LEFT
			case 38:
				return Keys.UP; // UP
			case 39:
				return Keys.RIGHT; // RIGHT
			case 40:
				return Keys.DOWN; // DOWN
			//case 87: return PSP2_CTRL_TRIANGLE; // W
			//case 64: return PSP2_CTRL_SQUARE; // A
			//case 83: return PSP2_CTRL_CROSS; // S
			//case 68: return PSP2_CTRL_CIRCLE; // D
			//case 81: return PSP2_CTRL_LTRIGGER; // Q
			//case 69: return PSP2_CTRL_RTRIGGER; // E
		}
		return Keys.NONE;
	}

	public void draw_pixels(int x, int y, int[] colors, int width, int height) {
		Js.v_raw("var x = p0, y = p1, colors = p2, width = p3, height = p4;");
		Js.v_raw("var data = this.ctx.createImageData(width, height);");
		Js.v_raw("var dd = data.data;");
		Js.v_raw("var area = width * height;");
		Js.v_raw("(new Int32Array(dd.buffer)).set(colors.data);");
		Js.v_raw("this.ctx.putImageData(data, x, y);");
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
	protected Input createInput() {
		return new Input() {
			@Override
			public boolean isPressing(Keys key) {
				return pressing[convertToKeyCode(key)];
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

	@Override
	public ResourcePromise<Bitmap32> loadBitmap32(String path) {
		ResourcePromise<Bitmap32> out = new ResourcePromise<>();
		_loadBitmap32(out, path);
		return out;
	}

	@JTranscMethodBody(target = "js", value = {
		"var promise = p0;",
		"var path = p1;",
		"var image = new Image();",
		"image.onload = function(e) {",
		"	var width = image.width;",
		"	var height = image.height;",
		"	var area = width * height;",
		"	var canvas = document.createElement('canvas');",
		"	canvas.width = image.width;",
		"	canvas.height = image.height;",
		"	var ctx = canvas.getContext('2d');",
		"	ctx.drawImage(image, 0, 0);",
		"	var data = ctx.getImageData(0, 0, image.width, image.height);",
		"	var content = new Int32Array(data.data.buffer);",
		//"	//for (var n = 0; n < content.length; n++) {",
		//"	//	var v = content[n];",
		//"	//	var r = (v >>> 0) & 0xFF;",
		//"	//	var g = (v >>> 8) & 0xFF;",
		//"	//	var b = (v >>> 16) & 0xFF;",
		//"	//	var a = (v >>> 24) & 0xFF;",
		//"	//	content[n] = (r << 0) | (g << 8) | (b << 16) | (a << 24);",
		//"	//}",
		"	var bmp = {% CONSTRUCTOR jmedialayer.graphics.Bitmap32:(II[I)V %}(image.width, image.height, JA_I.fromTypedArray(content));",
		"	promise['{% METHOD jmedialayer.backends.ResourcePromise:setResult %}'](bmp);",
		"};",
		"image.src = N.istr(path);",
	})
	native private void _loadBitmap32(ResourcePromise<Bitmap32> out, String path);
}
