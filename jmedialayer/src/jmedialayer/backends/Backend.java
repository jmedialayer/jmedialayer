package jmedialayer.backends;

import com.jtransc.JTranscSystem;
import com.jtransc.io.ra.RAFile;
import com.jtransc.io.ra.RAStream;
import com.jtransc.time.JTranscClock;
import jmedialayer.JMediaLayer;
import jmedialayer.audio.Audio;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Backend {
	private G1 g1;
	private Audio audio;
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

	final public Audio getAudio() {
		if (audio == null) audio = createAudio();
		return audio;
	}

	final public Input getInput() {
		if (input == null) input = createInput();
		return input;
	}

	protected G1 createG1() {
		return new G1();
	}

	protected Audio createAudio() {
		return new Audio();
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
			showException(t);
		} finally {
			preEnd();
		}
	}

	protected File getFileFromPath(String path) {
		URL url = Backend.class.getClassLoader().getResource(path);
		if (url == null) {
			return new File(path);
		} else {
			return new File(url.getFile());
		}
	}

	protected byte[] readBytes(String path) throws IOException {
		return FileUtils.read(getFileFromPath(path));
	}

	public RAStream openRAStreanSync(String path) throws IOException {
		File fileFromPath = getFileFromPath(path);
		if (!fileFromPath.exists()) return null;
		return new RAFile(fileFromPath);
	}

	protected final ResourcePromise<Bitmap32> _loadBitmap32Sync(String path) {
		try {
			Bitmap bmp = ImageFormats.read(readBytes(path));
			return ResourcePromise.resolved(bmp.toBitmap32());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	// Default implemented in _loadBitmap32Sync in order to avoid
	// including when not used.
	public ResourcePromise<Bitmap32> loadBitmap32(String path) {
		throw new RuntimeException("Must override loadBitmap32");
	}

	protected void waitNextFrame() {
		JTranscClock.impl.sleep(1000.0 / 60.0);
	}

	protected void preStep() {
		for (Timer timer : new ArrayList<>(timers)) {
			double now = JTranscSystem.fastTime();
			if (timer.active && (now >= timer.triggerTime)) {
				if (timer.repetitionTime > 0) {
					timer.triggerTime = now + timer.repetitionTime;
				} else {
					timer.active = false;
				}
				timer.action.run();
			}
		}
		Iterator<Timer> iterator = timers.iterator();
		while (iterator.hasNext()) {
			Timer timer = iterator.next();
			if (!timer.active) iterator.remove();
		}
	}

	protected void postStep() {
	}

	protected void preEnd() {
	}

	private ArrayList<Timer> timers = new ArrayList<>();

	public Timer setTimeout(int ms, Runnable action) {
		return setTimeoutInterval(ms, 0, action);
	}

	public Timer setInterval(int ms, Runnable action) {
		return setTimeoutInterval(ms, ms, action);
	}

	public Timer setIntervalStartingNow(int ms, Runnable action) {
		return setTimeoutInterval(0, ms, action);
	}

	private Timer setTimeoutInterval(int firstMs, int repeatMs, Runnable action) {
		Timer timer = new Timer();
		timer.active = true;
		timer.triggerTime = JTranscSystem.fastTime() + firstMs;
		timer.repetitionTime = repeatMs;
		timer.action = action;
		timers.add(timer);
		return timer;
	}

	public Timer setTween(final int timeMs, final TweenHandler action) {
		if (timeMs <= 0) {
			action.step(1.0);
			return new Timer();
		} else {
			final double start = JTranscSystem.fastTime();
			final Timer[] timer = new Timer[1];
			timer[0] = setIntervalStartingNow(16, new Runnable() {
				@Override
				public void run() {
					final double current = JTranscSystem.fastTime();
					int elapsed = (int) (current - start);
					double step = (double) elapsed / (double) timeMs;
					double stepClamped = Math.max(0.0, Math.min(step, 1.0));
					action.step(stepClamped);
					if (stepClamped >= 1.0) {
						timer[0].stop();
					}
				}
			});
			return timer[0];
		}
	}

	static public class Timer {
		boolean active;
		double triggerTime;
		double repetitionTime;
		Runnable action;

		public void stop() {
			active = false;
		}
	}

	/**
	 * Prevents device going to sleep
	 */
	public void powerTick() {
	}

	protected void _showExceptionInline(Throwable t) {
		Bitmap32 errorBuffer = new Bitmap32(getNativeWidth(), getNativeHeight());
		errorBuffer.clear(0);
		EmbeddedFont.draw(errorBuffer, 0, 0, Objects.toString(t));
		getG1().updateBitmap(errorBuffer);
		postStep();
		while (!getInput().isPressing(Keys.START)) {
			waitNextFrame();
		}
	}

	public void showException(Throwable t) {
		t.printStackTrace();
	}

	public interface TweenHandler {
		void step(double step);
	}

	public interface StepHandler {
		void step(int dtMs);
	}
}
