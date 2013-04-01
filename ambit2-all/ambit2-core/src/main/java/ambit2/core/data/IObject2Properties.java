package ambit2.core.data;

import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;

public interface IObject2Properties<D extends Object> extends IProcessor<D, List<Property>> {
	@Override
	public List<Property> process(D target) throws AmbitException;
}
