package jmedialayer.util;

public class Stopwatch {
	private long start;
	private long last;

	public Stopwatch() {
		start();
		start = last;
	}

	public void start() {
		this.last = System.currentTimeMillis();
	}

	public int stamp() {
		long current = System.currentTimeMillis();
		int result = (int) (current - last);
		this.last = current;
		return result;
	}
}
