package jmedialayer.backends;

import com.jtransc.time.JTranscClock;
import jmedialayer.graphics.G1;

public class Backend {
    private G1 g1;

    final public G1 getG1() {
        if (g1 == null) g1 = createG1();
        return g1;
    }

    protected G1 createG1() {
        return new G1();
    }

    public void loop(StepHandler step) {
        double prev = JTranscClock.impl.fastTime();
        while (true) {
            double current = JTranscClock.impl.fastTime();
            step.step((int) (current - prev));
            JTranscClock.impl.sleep(1000.0 / 60.0);
            prev = current;
        }
    }

    public interface StepHandler {
        void step(int dtMs);
    }
}
