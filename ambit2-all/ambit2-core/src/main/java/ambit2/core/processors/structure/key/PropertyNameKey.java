package ambit2.core.processors.structure.key;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;

/**
 * User defined name to join structure records
 * @author nina
 *
 */
public class PropertyNameKey extends PropertyKey<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3707216961952814110L;

	public PropertyNameKey() {
		super();
	}	
	public PropertyNameKey(String name) {
		super(new NameOnlyProperty(name));
	}

	@Override
	protected boolean isValid(Object newkey, Object value) {
		if (newkey != null) {
			newkey = newkey.toString().toUpperCase();
			if ((key.getName()!=null) && key.getName().equals(newkey)) try {
				return value != null;
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
