package ambit2.base.data.study;

import java.io.Serializable;

import ambit2.base.json.JSONUtils;

/**
 * Effect record. Modelled like IUCLID5 effects level
 * @author nina
 *
 * @param <ENDPOINT>
 * @param <CONDITIONS>
 * @param <UNIT>
 */
public class EffectRecord<ENDPOINT,CONDITIONS,UNIT> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7520767344716433227L;
	protected ENDPOINT endpoint;
	public ENDPOINT getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(ENDPOINT endpoint) {
		this.endpoint = endpoint;
	}
	public UNIT getUnit() {
		return unit;
	}
	public void setUnit(UNIT unit) {
		this.unit = unit;
	}
	public String getLoQualifier() {
		return loQualifier;
	}
	public void setLoQualifier(String loQualifier) {
		this.loQualifier = loQualifier;
	}
	public Double getLoValue() {
		return loValue;
	}
	public void setLoValue(double loValue) {
		this.loValue = loValue;
	}
	public String getUpQualifier() {
		return upQualifier;
	}
	public void setUpQualifier(String upQualifier) {
		this.upQualifier = upQualifier;
	}
	public Double getUpValue() {
		return upValue;
	}
	public void setUpValue(double upValue) {
		this.upValue = upValue;
	}
	public CONDITIONS getConditions() {
		return conditions;
	}
	public void setConditions(CONDITIONS conditions) {
		this.conditions = conditions;
	}
	protected UNIT unit;
	protected String loQualifier;
	protected Double loValue = null;
	protected String upQualifier;
	protected Double upValue = null;
	protected CONDITIONS conditions;
	protected String textValue;
	
	public String getTextValue() {
		return textValue;
	}
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	public void clear() {
		endpoint = null;
		unit = null;
		conditions = null;
		loQualifier = null;
		upQualifier = null;
		upValue = null;
		loValue = null;
		
	}
	public static enum _fields {
		endpoint,
		conditions,
		result,
		unit,
		loQualifier,
		loValue,
		upQualifier,
		upValue,
		textValue
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.endpoint.name())));
		b.append(":\t");
		b.append(endpoint==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.toString())));
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.conditions.name())));
		b.append(":\t");		
		b.append(getConditions()==null?"{}":getConditions().toString());
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.result.name())));
		b.append(":\t{\n\t");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.unit.name())));
		b.append(":\t");
		b.append(getUnit()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUnit().toString())));
		if (getLoQualifier()!=null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.loQualifier.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getLoQualifier())));
		}
		if (getLoValue()!=null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.loValue.name())));
			b.append(":\t");
			b.append(getLoValue());
		}
		if (getUpQualifier()!=null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.upQualifier.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUpQualifier())));
		}		
		if (getUpValue()!=null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.upValue.name())));
			b.append(":\t");
			b.append(getUpValue());
		}
		if (getTextValue()!=null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.textValue.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getTextValue())));
		}		
		
		b.append("\n\t}\n}");
		return b.toString();
	}	
}
