package ambit2.base.data.study;

import java.util.HashMap;
import java.util.Iterator;

import ambit2.base.json.JSONUtils;

public class Params<VALUE> extends HashMap<String, VALUE> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8844381138806492152L;
	
	public Params() {
		super();
	}
	public Params(VALUE value) {
		super();
		setLoValue(value);
	}
	public Params(String key,VALUE value) {
		super();
		put(key,value);
	}
	
	public VALUE getUnits() {
		return get(_FIELDS_RANGE.unit.name());
	}
	public void setUnits(VALUE unit) {
		 put(_FIELDS_RANGE.unit.name(),unit);
	}

	public VALUE getLoValue() {
		return get(_FIELDS_RANGE.loValue.name());
	}
	public VALUE getUpValue() {
		return get(_FIELDS_RANGE.upValue.name());
	}
	public void setLoValue(VALUE value) {
		put(_FIELDS_RANGE.loValue.name(),value);
	}	
	public void setUpValue(VALUE value) {
		put(_FIELDS_RANGE.upValue.name(),value);
	}
	public VALUE getUpQualifier() {
		return get(_FIELDS_RANGE.upQualifier.name());
	}
	public VALUE getLoQualifier() {
		return get(_FIELDS_RANGE.loQualifier.name());
	}
	public void setUpQualifier(VALUE qualifier) {
		put(_FIELDS_RANGE.upQualifier.name(),qualifier);
	}
	public void setLoQualifier(VALUE qualifier) {
		put(_FIELDS_RANGE.loQualifier.name(),qualifier);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{");
		Iterator<String> keys = keySet().iterator();
		String comma = null;
		while (keys.hasNext()) {
			if (comma!=null) b.append(comma);
			String key = keys.next();
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(key)));
			b.append(":");
			
			VALUE value = get(key);
			if (value==null)
				b.append("null");
			else if (value instanceof Params)
				b.append(value.toString());
			else if (value instanceof Number)
				b.append(value);
			else
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(value.toString())));
			comma = ",";
		}
		b.append("}");
		return b.toString();
	}
}
