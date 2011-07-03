package ambit2.core.data.model;


public class Parameter<T> {
	public enum ParameterScope {mandatory,optional};
	protected ParameterScope scope;
	protected T value;
	protected String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Parameter(String name,T value) {
		super();
		this.name = name;
		setValue(value);
	}
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
