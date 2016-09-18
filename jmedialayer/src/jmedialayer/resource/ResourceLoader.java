package jmedialayer.resource;

public class ResourceLoader<T> {
	public boolean loaded;
	public final String path;
	public T result;

	public ResourceLoader(String path) {
		this.path = path;
	}

	final public ResourceLoader<T> load() {
		loadInternal();
		return this;
	}

	protected void loadInternal() {
	}
}
