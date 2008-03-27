package ambit2.database.writers;

import java.util.Enumeration;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.database.DbConnection;
import ambit2.exceptions.AmbitException;
import ambit2.exceptions.DescriptorCalculationException;
import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.DescriptorsHashtable;

/**
 * Writes descriptors to database. Descriptors are expected as molecule properties.
 * Used by {@link ambit2.database.writers.DbSubstanceWriter}, {@link ambit2.database.writers.QSARDescriptorsWriter}, {@link ambit2.ui.actions.process.CalculateMOPACAction}, {@link ambit2.ui.actions.process.DbMOPACAction} 
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class PropertyDescriptorWriter extends AbstractDescriptorWriter {

	public PropertyDescriptorWriter(DbConnection conn) {
		super(conn);
	}

	public PropertyDescriptorWriter(DbConnection conn, Object descriptor,
			DescriptorDefinition ambitDescriptor) {
		super(conn, descriptor, ambitDescriptor);
	}
	public PropertyDescriptorWriter(DbConnection conn, DescriptorsHashtable descriptors) {
		super(conn,descriptors);
	}	
	protected double getDescriptorValue(IChemObject object, Object descriptor)
			throws AmbitException {
		Object value = object.getProperty(descriptor.toString());
		if (value == null) throw new AmbitException(ERR_NOVALUE + descriptor.toString());
		if (value instanceof Exception) throw new DescriptorCalculationException((Exception)value);
		else {
			try {
				return Double.parseDouble(value.toString());
			} catch (NumberFormatException x) {
				throw new AmbitException(ERR_UNKNOWNTYPE,x);
			}
		}
			
	}
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Writes ");
		Enumeration e = descriptorLookup.keys();
		while (e.hasMoreElements()) { b.append(e.nextElement().toString()); b.append(" "); }
		b.append(" values to database");
		return b.toString() ;
	}

}
