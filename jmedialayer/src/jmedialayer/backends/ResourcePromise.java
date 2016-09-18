package jmedialayer.backends;

import java.util.ArrayList;

public class ResourcePromise<T> {
	public boolean done = false;
	public T result;
	private ArrayList<Handler<T>> handlers = new ArrayList<>();

	public void then(Handler<T> handler) {
		if (done) {
			handler.handle(result);
		} else {
			handlers.add(handler);
		}
	}

	public ResourcePromise<T> setResult(T result) {
		if (!done) {
			this.result = result;
			this.done = true;
			for (Handler<T> handler : handlers) {
				handler.handle(result);
			}
			handlers.clear();
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
