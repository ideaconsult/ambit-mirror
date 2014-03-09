package ambit2.base.data.substance;

import ambit2.base.data.study.EffectRecord._fields;
import ambit2.base.json.JSONUtils;

public class ExternalIdentifier {
	protected String systemDesignator;
	protected String systemIdentifier;
	
	public ExternalIdentifier(String systemDesignator,String systemIdentifier) {
		setSystemDesignator(systemDesignator);
		setSystemIdentifier(systemIdentifier);
	}
	public ExternalIdentifier() {
		this(null,null);
	}
	public String getSystemDesignator() {
		return systemDesignator;
	}
	public void setSystemDesignator(String systemDesignator) {
		this.systemDesignator = systemDesignator;
	}
	public String getSystemIdentifier() {
		return systemIdentifier;
	}
	public void setSystemIdentifier(String systemIdentifier) {
		this.systemIdentifier = systemIdentifier;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape("type")));
		b.append(":\t");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(systemDesignator)));
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape("id")));
		b.append(":\t");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(systemIdentifier)));
		b.append("\n\t}");
		return b.toString();
	}
}
