package ambit2.db.readers;

import java.io.Serializable;

import ambit2.base.data.Property;

public class PropertyValue<T> implements Serializable {
	int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	Property property;
	T value;
	public PropertyValue() {
		this(null,null);
	}
	public PropertyValue(Property property) {
		this(property,null);
	}
	public PropertyValue(Property property,T value) {
		setProperty(property);
		setValue(value);
	}		
	public Property getProperty() {
		return property;
	}
	public void setProperty(Property property) {
		this.property = property;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s = %s", getProperty(),getValue());
	}
	
}
