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
	protected Double typical_value;
	public Double getTypical_value() {
		return typical_value;
	}
	public void setTypical_value(Double typical_value) {
		this.typical_value = typical_value;
	}
	public Double getTypical_lowervalue() {
		return typical_lowervalue;
	}
	public void setTypical_lowervalue(Double typical_lowervalue) {
		this.typical_lowervalue = typical_lowervalue;
	}
	public Double getTypical_uppervalue() {
		return typical_uppervalue;
	}
	public void setTypical_uppervalue(Double typical_uppervalue) {
		this.typical_uppervalue = typical_uppervalue;
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
	protected Double typical_lowervalue;
	protected Double typical_uppervalue;
	protected Double real_value;
	protected Double real_lowervalue;
	protected Double real_uppervalue;	
	protected String typical_unit;
	protected String real_unit;
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
