package jmedialayer.backends;

import com.jtransc.time.JTranscClock;
import jmedialayer.JMediaLayer;
import jmedialayer.graphics.Bitmap;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.EmbeddedFont;
import jmedialayer.graphics.G1;
import jmedialayer.imaging.ImageFormats;
import jmedialayer.input.Input;
import jmedialayer.input.Keys;
import jmedialayer.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Backend {
	private G1 g1;
	private Input input;
	protected boolean running = true;

	public String getVersion() {
		return JMediaLayer.getVersion();
	}

	public int getNativeWidth() {
		return 960;
	}

	public int getNativeHeight() {
		return 544;
	}

	final public G1 getG1() {
		if (g1 == null) g1 = createG1();
		return g1;
	}

	final public Input getInput() {
		if (input == null) input = createInput();
		return input;
	}

	protected G1 createG1() {
		return new G1();
	}

	protected Input createInput() {
		return new Input();
	}

	public void loop(StepHandler step) {
		double prev = JTranscClock.impl.fastTime();
		try {
			while (running) {
				double current = JTranscClock.impl.fastTime();
				preStep();
				step.step((int) (current - prev));
				postStep();
				waitNextFrame();
				prev = current;
			}
		} catch (Throwable t) {
			Bitmap32 errorBuffer = new Bitmap32(getNativeWidth(), getNativeHeight());
			errorBuffer.clear(0);
			EmbeddedFont.draw(errorBuffer, 0, 0, Objects.toString(t));
			g1.updateBitmap(errorBuffer);
			postStep();
			while (!getInput().isPressing(Keys.START)) {
				waitNextFrame();
			}
		} finally {
			preEnd();
		}
	}

	protected byte[] readBytes(String path) throws IOException {
		URL url = Backend.class.getClassLoader().getResource(path);
		return FileUtils.read(new File(url.getFile()));
	}

	protected final ResourcePromise<Bitmap32> _loadBitmap32Sync(String path) {
		try {
			Bitmap bmp = ImageFormats.read(readBytes(path));
			return ResourcePromise.resolved(bmp.toBitmap32());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public ResourcePromise<Bitmap32> loadBitmap32(String path) {
		throw new RuntimeException("Must override loadBitmap32");
	}

	protected void waitNextFrame() {
		JTranscClock.impl.sleep(1000.0 / 60.0);
	}

	protected void preStep() {
	}

	protected void postStep() {
	}

	protected void preEnd() {
	}

	public interface StepHandler {
		void step(int dtMs);
	}
}
