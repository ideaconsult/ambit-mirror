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
	this(value, "  ");
    }

    public Value(VALUE value, String qualifier) {
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

    public String toJSON(StringBuilder b) {
	String comma = "";
	if (getLoQualifier() != null && !"".equals(getLoQualifier())) {
	    b.append(comma);
	    b.append(EffectRecord._fields.loQualifier.toJSON());
	    b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getLoQualifier())));
	    comma = ",";
	}
	if (getLoValue() != null &&  !"".equals(getLoValue().toString())) {
	    b.append(comma);
	    b.append(EffectRecord._fields.loValue.toJSON());
	    serialize(getLoValue(), b);
	    comma = ",";
	}
	if (getUnits() != null && !"".equals(getUnits().toString())) {
	    b.append(comma);
	    b.append(EffectRecord._fields.unit.toJSON());
	    b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUnits())));
	    comma = ",";
	}
	if (getUpQualifier() != null && !"".equals(getUpQualifier())) {
	    b.append(comma);
	    b.append(EffectRecord._fields.upQualifier.toJSON());
	    b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUpQualifier())));
	    comma = ",";
	}
	if (getUpValue() != null &&  !"".equals(getUpValue().toString())) {
	    b.append(comma);
	    b.append(EffectRecord._fields.upValue.toJSON());
	    serialize(getUpValue(), b);
	    comma = ", ";
	}
	if (getAnnotation() != null) {
	    b.append(comma);
	    b.append("\t\"annotation\":");
	    b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getAnnotation())));
	    comma = ",";
	}
	return comma;
    }
    @Override
    public String toString() {
	StringBuilder b = new StringBuilder();
	b.append("{");
	toJSON(b);
	b.append("}");
	return b.toString();
    }

    public String toHumanReadable() {
	StringBuilder b = new StringBuilder();
	if (getLoQualifier()!=null) { b.append(getLoQualifier()); b.append(" "); }
	if (getLoValue()!=null && !"".equals(getLoValue().toString())) { b.append(getLoValue()); b.append(" "); }
	if (getUpQualifier()!=null) { b.append(getUpQualifier()); b.append(" "); }
	if (getUpValue()!=null && !"".equals(getUpValue().toString())) { b.append(getUpValue()); b.append(" "); }
	if (getUnits()!=null) { b.append(getUnits());  }
	return b.toString();
    }
    private void serialize(VALUE value, StringBuilder b) {
	if (value == null)
	    b.append("null");
	else if (value instanceof Params)
	    b.append(value.toString());
	else if (value instanceof Number)
	    b.append(JSONUtils.jsonNumber((Number)value));
	else
	    b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(value.toString())));
    }
}
