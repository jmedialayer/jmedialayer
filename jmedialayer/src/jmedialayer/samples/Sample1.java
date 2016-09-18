package jmedialayer.samples;

import jmedialayer.backends.Backend;
import jmedialayer.backends.BackendSelector;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.EmbeddedFont;
import jmedialayer.graphics.G1;
import jmedialayer.input.Input;
import jmedialayer.input.Keys;

@SuppressWarnings("Convert2Lambda")
public class Sample1 {
	static public void main(String[] args) {
		//final Backend backend = new HenkakuPsvitaBackend();
		//Backend backend = new AwtBackend();
		final Backend backend = BackendSelector.getDefault();
		final Bitmap32 bmp = new Bitmap32(backend.getNativeWidth(), backend.getNativeHeight());
		final Input input = backend.getInput();
		final G1 g1 = backend.getG1();
		final int[] frame = {0};
		final int[] x = {0};
		final int[] y = {0};
		backend.loop(new Backend.StepHandler() {
			public void step(int dtMs) {
				bmp.clear(0xFFFF0000 + (frame[0] / 10));
				bmp.fillrect(x[0], y[0], 100, 100, 0xFFFFFFFF);

				EmbeddedFont.draw(bmp, 200, 200, "Hello World!");

				g1.updateBitmap(bmp);

				if (input.isPressing(Keys.UP)) y[0]--;
				if (input.isPressing(Keys.DOWN)) y[0]++;
				if (input.isPressing(Keys.LEFT)) x[0]--;
				if (input.isPressing(Keys.RIGHT)) x[0]++;

				frame[0] += dtMs;

				//throw new RuntimeException("My fancy error!");
			}
		});
	}
}
