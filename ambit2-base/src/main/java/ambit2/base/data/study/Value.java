package ambit2.base.data.study;

import ambit2.base.json.JSONUtils;

/**
 * 
 * @author nina
 *
 * @param <VALUE>
<pre>
{
	"unit":	"mg/kg bw",
	"loQualifier":	">=",,
	"loValue":	100.0,
	"upQualifier":	"<",
	"upValue":	200.0
	}
</pre>
 */
public class Value<VALUE> implements IValue<VALUE, String, String> {
	protected String units;
	protected VALUE loValue;
	protected VALUE upValue;
	protected String loQualifier;
	protected String upQualifier;
	protected String annotation;
	
	public Value() {
	}
	public Value(VALUE value) {
		this(value,"  ");
	}
	public Value(VALUE value,String qualifier) {
		setLoValue(value);
		setLoQualifier(qualifier);
	}
	@Override
	public String getAnnotation() {
		return annotation;
	}
	@Override
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	@Override
	public String getUnits() {
		return units;
	}

	@Override
	public void setUnits(String unit) {
		this.units = unit;
	}

	@Override
	public VALUE getLoValue() {
		return loValue;
	}

	@Override
	public VALUE getUpValue() {
		return upValue;
	}

	@Override
	public void setLoValue(VALUE value) {
		this.loValue = value;
	}

	@Override
	public void setUpValue(VALUE value) {
		this.upValue = value;
	}

	@Override
	public String getUpQualifier() {
		return upQualifier;
	}

	@Override
	public String getLoQualifier() {
		return loQualifier;
	}

	@Override
	public void setUpQualifier(String qualifier) {
		this.upQualifier = qualifier;
	}

	@Override
	public void setLoQualifier(String qualifier) {
		this.loQualifier = qualifier;
	}
	@Override
	public String toString() {
		String comma = "";
		StringBuilder b = new StringBuilder();
		b.append("{\n");
		if (getUnits()!=null) {
			b.append(comma);
			b.append("\t\"unit\":");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUnits())));
			comma = ",\n";
		}
		if (getLoQualifier()!=null) {
			b.append(comma);
			b.append("\t\"loQualifier\":");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getLoQualifier())));
			comma = ",\n";
		}		
		if (getLoValue()!=null) {
			b.append(comma);
			b.append("\t\"loValue\":");
			serialize(getLoValue(), b);
			comma = ",\n";
		}
		if (getUpQualifier()!=null) {
			b.append(comma);
			b.append("\t\"upQualifier\":");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUpQualifier())));
			comma = ",\n";
		}
		if (getUpValue()!=null) {
			b.append(comma);
			b.append("\t\"upValue\":");
			serialize(getUpValue(), b);
			comma = ",\n";
		}		
		if (getAnnotation()!=null) {
			b.append(comma);
			b.append("\t\"annotation\":");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getAnnotation())));
			comma = ",\n";
		}				
		b.append("\n}\n");
		return b.toString();
	}
	
	
	private void serialize(VALUE value,StringBuilder b) {
		if (value==null)
			b.append("null");
		else if (value instanceof Params)
			b.append(value.toString());
		else if (value instanceof Number)
			b.append(value);
		else
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(value.toString())));
	}
}
