package ambit.database.writers;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.database.DbConnection;
import ambit.exceptions.AmbitException;
import ambit.exceptions.DescriptorCalculationException;

/**
 * Writes descriptors to database. 
 * Descriptors are expected to come as molecule properties and the descriptor value should be of type  {@link org.openscience.cdk.qsar.DescriptorValue}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class CDKDescriptorWriter extends AbstractDescriptorWriter {
	
	public CDKDescriptorWriter(DbConnection conn) {
		super(conn);
	}
	public CDKDescriptorWriter(DbConnection conn, Descriptor cdkDescriptor,
			DescriptorDefinition ambitDescriptor) {
		super(conn, cdkDescriptor, ambitDescriptor);
	}
	public CDKDescriptorWriter(DbConnection conn, DescriptorsHashtable descriptors) {
		super(conn,descriptors);
	}		
	protected double getDescriptorValue(IChemObject object,Object descriptor) throws AmbitException  {
			
			Object property = object.getProperty(descriptor);
			if (property == null) throw new AmbitException(ERR_NOVALUE + descriptor.toString());
			double value;
			if (property instanceof DescriptorValue) {
				DescriptorValue dv = (DescriptorValue)property;
				IDescriptorResult dr = dv.getValue();
				if (dr instanceof DoubleResult) {
					value = ((DoubleResult)dr).doubleValue();
				} else if (dr instanceof IntegerResult) {
					value = ((IntegerResult)dr).intValue();	
				} else  throw new AmbitException(ERR_UNKNOWNTYPE+property.getClass().getName());
			}	else 
				if (property instanceof Exception)	throw new DescriptorCalculationException((Exception)property);
				else throw new AmbitException(ERR_UNKNOWNTYPE+property.getClass().getName());
			return value;
	}
	public String toString() {
		return "Write descriptor values to database";
	}

}
