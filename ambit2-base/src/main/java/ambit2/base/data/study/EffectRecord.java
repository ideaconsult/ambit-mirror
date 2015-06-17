package ambit2.base.data.study;

import java.io.Serializable;

import ambit2.base.json.JSONUtils;

import com.google.common.base.Objects;

/**
 * Effect record. Modelled like IUCLID5 effects level
 * 
 * @author nina
 * 
 * @param <ENDPOINT>
 *            S
 * @param <CONDITIONS>
 * @param <UNIT>
 * 
 *            To create new record use
 * 
 *            new EffectRecord<String,new
 *            ambit2.base.data.study.Params(),String)
 */
public class EffectRecord<ENDPOINT, CONDITIONS, UNIT> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7520767344716433227L;

	protected String sampleID;
	protected UNIT unit;
	protected String loQualifier;
	protected Double loValue = null;
	protected String upQualifier;
	protected Double upValue = null;
	protected CONDITIONS conditions;
	protected Object textValue;
	protected String errQualifier;
	protected Double errValue = null;

	@Override
	public int hashCode() {
		return Objects.hashCode(unit, loQualifier, loValue, upQualifier,
				upValue, conditions == null ? null : conditions.hashCode(),
				textValue, errQualifier, errValue);
	}

	public String getSampleID() {
		return sampleID;
	}

	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}

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

	public String getErrQualifier() {
		return errQualifier;
	}

	public void setErrQualifier(String errQualifier) {
		this.errQualifier = errQualifier;
	}

	public Double getErrorValue() {
		return errValue;
	}

	public void setErrorValue(double errorValue) {
		this.errValue = errorValue;
	}

	public IParams getStdDev() {
		if (getConditions() instanceof IParams) {
			Object stddev = ((IParams) getConditions())
					.get(_FIELDS_RANGE.STD_DEV.name());
			if (stddev instanceof IParams)
				return (IParams) stddev;
		}
		return null;
	}

	public void setStdDev(double value) throws UnsupportedOperationException {
		setStdDev(value, null);
	}

	public void setStdDev(double value, String units)
			throws UnsupportedOperationException {
		setErrorValue(value);
		/*
		 * if (getConditions() instanceof IParams) { //this is a hack to enter
		 * std deviation in the condition fields. To be refactored together with
		 * the database storage. IParams cond = (IParams) getConditions(); if
		 * (cond==null) throw new
		 * UnsupportedOperationException("Use setConditions() first."); Object
		 * stddev = cond.get(_FIELDS_RANGE.STD_DEV.name()); if (stddev==null) {
		 * stddev = new Params(null); cond.put(_FIELDS_RANGE.STD_DEV.name(),
		 * stddev); } else if (stddev instanceof IParams) { } else new
		 * UnsupportedOperationException(); ((IParams)
		 * stddev).setLoValue(value); ((IParams) stddev).setUnits(units); } else
		 * throw new UnsupportedOperationException();
		 */
	}

	public CONDITIONS getConditions() {
		return conditions;
	}

	public void setConditions(CONDITIONS conditions) {
		this.conditions = conditions;
	}

	public Object getTextValue() {
		return textValue;
	}

	public void setTextValue(Object textValue) {
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
		endpoint, conditions, result, unit, loQualifier, loValue, upQualifier, upValue, errQualifier, errorValue, textValue;

		public String toJSON() {
			return "\"" + name() + "\":";
		}
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.endpoint
				.name())));
		b.append(":\t");
		b.append(endpoint == null ? null : JSONUtils.jsonQuote(JSONUtils
				.jsonEscape(endpoint.toString())));
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.conditions
				.name())));
		b.append(":\t");
		b.append(getConditions() == null ? "{}" : getConditions().toString());
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.result.name())));
		b.append(":\t{\n\t");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.unit.name())));
		b.append(":\t");
		b.append(getUnit() == null ? null : JSONUtils.jsonQuote(JSONUtils
				.jsonEscape(getUnit().toString())));
		if (getLoQualifier() != null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(_fields.loQualifier.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getLoQualifier())));
		}
		if (getLoValue() != null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.loValue
					.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonNumber(getLoValue()));
		}
		if (getUpQualifier() != null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(_fields.upQualifier.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getUpQualifier())));
		}
		if (getUpValue() != null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.upValue
					.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonNumber(getUpValue()));
		}
		if (getErrQualifier() != null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(_fields.errQualifier.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(getErrQualifier())));
		}
		if (getErrorValue() != null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(_fields.errorValue.name())));
			b.append(":\t");
			b.append(JSONUtils.jsonNumber(getErrorValue()));
		}
		if (getTextValue() != null) {
			b.append(",\n\t");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_fields.textValue
					.name())));
			b.append(":\t");
			if (getTextValue() instanceof Params)
				b.append(getTextValue().toString());
			else
				b.append(JSONUtils.jsonQuote(JSONUtils
						.jsonEscape(getTextValue().toString())));
		}

		b.append("\n\t}\n}");
		return b.toString();
	}

	public boolean isEmpty() {
		return (getLoValue() == null && getUpValue() == null && (getTextValue() == null || ""
				.equals(getTextValue())));
	}
}
