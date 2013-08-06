package ambit2.base.relation.composition;

import java.io.Serializable;

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
	protected String typical;
	protected String function;
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
	protected Double typical_value;
	protected String typical_unit;
	
	protected Double real_value;
	
	protected String real_lower;	
	protected Double real_lowervalue;
	
	protected String real_upper;
	protected Double real_uppervalue;
	
	protected String real_unit;
	
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
	
	public Double getReal_value() {
		return real_value;
	}
	public void setReal_value(Double real_value) {
		this.real_value = real_value;
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
	
}
