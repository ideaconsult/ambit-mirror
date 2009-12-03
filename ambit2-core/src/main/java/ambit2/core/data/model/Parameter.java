package ambit2.core.data.model;

public class Parameter<T> {
	public enum ParameterScope {mandatory,optional};
	protected ParameterScope scope;
	protected T value;
	public ParameterScope getScope() {
		return scope;
	}
	public void setScope(ParameterScope scope) {
		this.scope = scope;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
}
