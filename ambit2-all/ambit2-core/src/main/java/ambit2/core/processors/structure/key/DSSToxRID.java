package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;

public class DSSToxRID extends PropertyKey<Number> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2117618200041344844L;
	/**
	 * 
	 */

	public DSSToxRID() {

		super(Property.getInstance("DSSTox_RID","DSSTox", "http://www.epa.gov/NCCT/dsstox/StandardChemFieldDefTable.html#DSSTox_RID"));
	}


	@Override
	protected boolean isValid(Object newkey, Object value) {
		if (newkey != null) {
			newkey = newkey.toString();
			if (key.getName().equals(newkey)) try {
				return Integer.parseInt(value.toString()) > 0;
			} catch(Exception x) {
				return false;
			}
		} 
		return false;
	}
	@Override
	public Number process(IStructureRecord structure) throws AmbitException {
		Object o = super.process(structure);
		if (o == null) return null;
		try {
			return new Integer(o.toString());
		} catch (NumberFormatException x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public Object getQueryKey() {
		return getKey();
	}
	public Class getType() {
		return Integer.class;
	}
}