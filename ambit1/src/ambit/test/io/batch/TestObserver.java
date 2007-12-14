package ambit.test.io.batch;

import java.util.Observable;
import java.util.Observer;

public class TestObserver implements Observer {
	protected int count = 0;
	public TestObserver() {
		super();
		count = 0;
	}

	public void update(Observable arg0, Object arg1) {
		count++;

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
