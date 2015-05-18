package ambit2.base.relation.composition;

import java.io.Serializable;

import ambit2.base.json.JSONUtils;

/**
 * 
 * @author nina
 *
 */
public class Proportion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 578781054130658046L;
	protected String function;
	
	protected String typical;
	protected Double typical_value;
	protected String typical_unit;
	
	protected String real_lower;	
	protected Double real_lowervalue;
	protected String real_upper;
	protected Double real_uppervalue;
	protected String real_unit;
	
	public void clear() {
		function = null;
		typical = null;
		typical_value = null;
		typical_unit = null;
		real_lower = null;
		real_lowervalue = null;
		real_upper = null;
		real_uppervalue = null;
		real_unit = null;
	}
	enum _jsonfields {
		typical {
			@Override
			public String toJSON(Proportion p) {
				return String.format("\"%s\": {\"precision\": %s,\"value\": %s,\"unit\": %s}", 
						name(),
						JSONUtils.jsonQuote(JSONUtils.jsonEscape(p.getTypical())),
						JSONUtils.jsonExpNumber(p.getTypical_value()),
						JSONUtils.jsonQuote(p.getTypical_unit())
						);
			}			
		},
		real {
			@Override
			public String toJSON(Proportion p) {
				return String.format("\"%s\": {\"lowerPrecision\": %s,\"lowerValue\": %s,\"upperPrecision\": %s,\"upperValue\": %s,\"unit\": %s}", 
						name(),
						JSONUtils.jsonQuote(JSONUtils.jsonEscape(p.getReal_lower())),
						JSONUtils.jsonExpNumber(p.getReal_lowervalue()),
						JSONUtils.jsonQuote(JSONUtils.jsonEscape(p.getReal_upper())),
						JSONUtils.jsonExpNumber(p.getReal_uppervalue()),
						JSONUtils.jsonQuote(p.getReal_unit())
						);
			}			
		},		
		function_as_additive {
			@Override
			public String toJSON(Proportion p) {
				return String.format("\"%s\":%s", name(), JSONUtils.jsonQuote(p.getFunction()));
			}				
		};
	
		public abstract String toJSON(Proportion p);
		
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getTypical() {
		return typical;
	}
	public void setTypical(String typical) {
		this.typical = typical;
	}

	
	public String getReal_lower() {
		return real_lower;
	}
	public void setReal_lower(String real_lower) {
		this.real_lower = real_lower;
	}
	public String getReal_upper() {
		return real_upper;
	}
	public void setReal_upper(String real_upper) {
		this.real_upper = real_upper;
	}

	
	public Double getTypical_value() {
		return typical_value;
	}
	public void setTypical_value(Double typical_value) {
		this.typical_value = typical_value;
	}
	

	public Double getReal_lowervalue() {
		return real_lowervalue;
	}
	public void setReal_lowervalue(Double real_lowervalue) {
		this.real_lowervalue = real_lowervalue;
	}
	public Double getReal_uppervalue() {
		return real_uppervalue;
	}
	public void setReal_uppervalue(Double real_uppervalue) {
		this.real_uppervalue = real_uppervalue;
	}

	public String getTypical_unit() {
		return typical_unit;
	}
	public void setTypical_unit(String typical_unit) {
		this.typical_unit = typical_unit;
	}
	public String getReal_unit() {
		return real_unit;
	}
	public void setReal_unit(String real_unit) {
		this.real_unit = real_unit;
	}
	public String toJSON() {
		StringBuilder b = new StringBuilder();
		b.append("\t{\n");
		String comma = "";
		for (_jsonfields f : _jsonfields.values()) {
			b.append(comma);
			b.append("\n\t");
			b.append(f.toJSON(this));
			comma = ",";
		}
		b.append("\n\t}\n");
		return b.toString();
	}
}
