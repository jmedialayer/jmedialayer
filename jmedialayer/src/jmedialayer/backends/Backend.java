package jmedialayer.backends;

import com.jtransc.time.JTranscClock;
import jmedialayer.graphics.G1;
import jmedialayer.input.Input;

public class Backend {
	private G1 g1;
	private Input input;
	protected boolean running = true;

	public int getNativeWidth() {
		return 640;
	}

	public int getNativeHeight() {
		return 480;
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
		while (running) {
			double current = JTranscClock.impl.fastTime();
			preStep();
			step.step((int) (current - prev));
			postStep();
			waitNextFrame();
			prev = current;
		}
		preEnd();
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
