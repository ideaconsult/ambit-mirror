package ambit2.db.readers;

import java.io.Serializable;

import ambit2.base.data.Property;
import ambit2.db.processors.AbstractPropertyWriter;
import ambit2.db.processors.AbstractPropertyWriter.mode;

public class PropertyValue<T> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2035617875115805031L;
    int id;
    AbstractPropertyWriter.mode error = mode.UNKNOWN;

    public AbstractPropertyWriter.mode getError() {
	return error;
    }

    public void setError(AbstractPropertyWriter.mode error) {
	this.error = error;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    Property property;
    T value;

    public PropertyValue() {
	this(null, null);
    }

    public PropertyValue(Property property) {
	this(property, null);
    }

    public PropertyValue(Property property, T value) {
	this(property, value, mode.UNKNOWN);
    }

    public PropertyValue(Property property, T value, mode error) {
	setProperty(property);
	setValue(value);
	setError(error);
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
	return String.format("%s = %s", getProperty(), getValue());
    }

}
