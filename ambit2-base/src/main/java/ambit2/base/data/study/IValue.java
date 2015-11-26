package ambit2.base.data.study;

import net.idea.modbcum.i.JSONSerializable;

/**
 * 
 * @author nina
 *
 * @param <VALUE>  Typical: scalar value (double, integer, boolean, string)
 * @param <QUALIFIER> Typical: string < <= > >=
 * @param <UNITS> Typical: string
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
public interface IValue<VALUE,QUALIFIER,UNITS> extends JSONSerializable {
	public UNITS getUnits();
	/**
	 * 
	 * @param unit
	 */
	public void setUnits(UNITS unit);
	/**
	 * lower value
	 * @return
	 */
	public VALUE getLoValue();
	/**
	 * Upper value
	 * @return
	 */
	public VALUE getUpValue();
	public void setLoValue(VALUE value);
	public void setUpValue(VALUE value);
	public QUALIFIER getUpQualifier();
	public QUALIFIER getLoQualifier();
	public void setUpQualifier(QUALIFIER qualifier);
	public void setLoQualifier(QUALIFIER qualifier);
	
	public String getAnnotation();
	public void setAnnotation(String annotation);
	public String toHumanReadable();
}
