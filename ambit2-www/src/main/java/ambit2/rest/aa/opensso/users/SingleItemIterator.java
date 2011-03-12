package ambit2.rest.aa.opensso.users;

import java.util.Iterator;

public class SingleItemIterator<T> implements Iterator<T> {
	protected T item;

	public SingleItemIterator(T item) {
		this.item = item;
	}
	public boolean hasNext() {
		return item != null;
	}

	public T next() {
		T theItem = item;
		item = null;
		return theItem;
	}

	public void remove() {
	}
	
}