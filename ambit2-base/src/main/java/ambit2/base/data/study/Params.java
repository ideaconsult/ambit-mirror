package ambit2.base.data.study;

import java.util.HashMap;
import java.util.Iterator;

import ambit2.base.json.JSONUtils;

public class Params extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8844381138806492152L;

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
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(get(key))));
			comma = ",";
		}
		b.append("}");
		return b.toString();
	}
}
