package jmedialayer.samples;

import jmedialayer.backends.Backend;
import jmedialayer.backends.BackendSelector;
import jmedialayer.backends.psvita.HenkakuPsvitaBackend;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.G1;

@SuppressWarnings("Convert2Lambda")
public class Sample1 {
	static public void main(String[] args) {
		//final Backend backend = new HenkakuPsvitaBackend();
		//Backend backend = new AwtBackend();
		final Backend backend = BackendSelector.getDefault();
		final Bitmap32 bmp = new Bitmap32(backend.getNativeWidth(), backend.getNativeHeight());
		final G1 g1 = backend.getG1();
		final int[] frame = {0};
		backend.loop(new Backend.StepHandler() {
			public void step(int dtMs) {
				bmp.clear(0xFFFF0000 + frame[0]);
				g1.updateBitmap(bmp);
				frame[0] += dtMs;
			}
		});
	}
}
