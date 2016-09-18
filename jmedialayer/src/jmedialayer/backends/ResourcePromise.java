package jmedialayer.backends;

import java.util.Arrays;

public class ResourcePromise<T> {
	public boolean done = false;
	public T result;
	private Handler<T>[] handlers = new Handler[0];

	public void then(Handler<T> handler) {
		if (done) {
			handler.handle(result);
		} else {
			handlers = Arrays.copyOf(handlers, handlers.length + 1);
			handlers[handlers.length - 1] = handler;
		}
	}

	public ResourcePromise<T> setResult(T result) {
		if (!done) {
			this.result = result;
			this.done = true;
			if (handlers.length > 0) {
				for (Handler<T> handler : handlers) {
					handler.handle(result);
				}
				handlers = new Handler[0];
			}
		}
		return this;
	}

	public interface Handler<T> {
		void handle(T result);
	}

	static public <T> ResourcePromise<T> resolved(T result) {
		return new ResourcePromise<T>().setResult(result);
	}
}
