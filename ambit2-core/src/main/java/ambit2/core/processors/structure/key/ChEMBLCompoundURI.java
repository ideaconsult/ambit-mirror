package ambit2.core.processors.structure.key;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.ChEMBLProperties.ChEMBL_Property;
import ambit2.base.interfaces.IStructureRecord;

public class ChEMBLCompoundURI extends PropertyKey<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8549426639690399378L;
	public ChEMBLCompoundURI() {
		super(ChEMBL_Property.forMolecule.getProperty());
	}

	@Override
	protected boolean isValid(Object newkey, Object value) {
		if (newkey != null) {
			newkey = newkey.toString().toUpperCase();
			if (key.getName().equals(newkey)) try {
				return Integer.parseInt(value.toString()) > 0;
			} catch(Exception x) {
				return false;
			}
		} 
		return false;
	}
	@Override
	public String process(IStructureRecord structure) throws AmbitException {
		Object o = super.process(structure);
		if (o == null) return null;
		try {
			return o.toString();
		} catch (NumberFormatException x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public Object getQueryKey() {
		return getKey();
	}
	public Class getType() {
		return String.class;
	}
}
