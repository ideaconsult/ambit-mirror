package ambit2.core.processors.structure.key;

import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.I5Utils;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

public class ReferenceSubstanceUUID extends PropertyKey<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2679648406249584207L;

	@Override
	public boolean isKeyValid(Property key) {
		return key.isI5UUID();
	}
	public String process(IStructureRecord structure) throws AmbitException {
		if (structure == null)
			throw new AmbitException("Empty molecule!");

		if ((key == null) || (structure.getProperty(key) == null)) {
			// find which key corresponds to I5UUID
			for (Property newkey : structure.getProperties()) {
				Object i5uuid = structure.getProperty(newkey);
				if (i5uuid == null)
					continue;
				if (!isKeyValid(newkey)) continue;
				this.key = newkey;
				return i5uuid.toString();
			}
		}
		if (key == null)
			throw new AmbitException("I5 UUID tag not defined");
		Object o = structure.getProperty(key);
		if (o == null)
			return null;
		else 
			return o.toString();
	}

	@Override
	protected boolean isValid(Object key, Object value) {
		try {
			String[] uuid = I5Utils.splitI5UUID(value.toString());
			UUID.fromString(uuid[1]);
			return true;
		} catch (Exception x) {
			return false;
		}

	}

	public Class getType() {
		return String.class;
	}
	@Override
	public Object getQueryKey() {
		return getKey();
	}
}
