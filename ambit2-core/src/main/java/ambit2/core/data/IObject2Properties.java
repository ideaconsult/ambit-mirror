package ambit2.core.data;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import ambit2.base.data.Property;

public interface IObject2Properties<D extends Object> extends IProcessor<D, List<Property>> {
	@Override
	public List<Property> process(D target) throws AmbitException;
}
