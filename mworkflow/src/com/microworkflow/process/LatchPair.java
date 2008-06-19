package com.microworkflow.process;

public class LatchPair<T,R> {
	protected T value;
	protected ValueLatch<R> latch;
	
	public LatchPair() {
		latch = new ValueLatch<R>();
	}
	public LatchPair(T value) {
		this();
		setValue(value);
	}
	
	public ValueLatch<R> getLatch() {
		return latch;
	}
	public void setLatch(ValueLatch<R> latch) {
		this.latch = latch;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public Class getType() {
		return value.getClass();
	}
	
}
