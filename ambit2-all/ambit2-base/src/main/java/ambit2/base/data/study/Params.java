package ambit2.base.data.study;

import java.util.HashMap;
import java.util.Iterator;

import ambit2.base.json.JSONUtils;

public class Params<VALUE> extends HashMap<String, VALUE> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8844381138806492152L;
	
	public static final String unit = "unit";
	public static final String loValue = "loValue";
	public static final String upValue = "upValue";
	public static final String loQualifier = "loQualifier";
	public static final String upQualifier = "upQualifier";
	
	public Params() {
		super();
	}
	public Params(String key,VALUE value) {
		super();
		put(key,value);
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
