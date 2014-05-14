package ambit2.base.data.study;

public interface IValue<VALUE,QUALIFIER,UNITS> {
	public UNITS getUnits();
	public void setUnits(UNITS unit);

	public VALUE getLoValue();
	public VALUE getUpValue();
	public void setLoValue(VALUE value);
	public void setUpValue(VALUE value);
	public QUALIFIER getUpQualifier();
	public QUALIFIER getLoQualifier();
	public void setUpQualifier(QUALIFIER qualifier);
	public void setLoQualifier(QUALIFIER qualifier);
	
	public String getAnnotation();
	public void setAnnotation(String annotation);
}
