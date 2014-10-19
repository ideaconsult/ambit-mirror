package ambit2.core.processors.structure.key;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

/**
 * returns pubchem SID given the structure properties
 * @author nina
 *
 */
public class PubchemSID  extends PropertyKey<Number> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5172763951258433726L;
	public PubchemSID() {
		super(Property.getInstance("PUBCHEM_SID","PUBCHEM", "http://pubchem.ncbi.nlm.nih.gov"));
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

	@Override
	public boolean useExactStructureID() {
		return true;
	}

}