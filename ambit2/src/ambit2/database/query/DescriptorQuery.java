package ambit2.database.query;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.literature.LiteratureEntry;

/**
 * A query for a single {@link ambit2.data.descriptors.DescriptorDefinition}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DescriptorQuery extends DescriptorDefinition {
	public static NumberFormat nfSQL = DecimalFormat.getInstance();
	public static NumberFormat nfGUI = DecimalFormat.getInstance();
	public static final String[] conditions= {">",">=","=","<=","<","between"};
	public static final String[] logical= {" ","and","or"}; 
	protected double minValue = 0;
	protected double maxValue = 1;
	protected double value=0;
	protected int condition = 5;
	protected int logic = 0;
	protected boolean enabled = false;
	
	public DescriptorQuery(String name, LiteratureEntry reference) {
		this(name, 0, reference);
	}

	public DescriptorQuery(String name, int type, LiteratureEntry reference) {
		this(name, type, 0, reference);
	
	}

	public DescriptorQuery(String name, int type, int dtype,
			LiteratureEntry reference) {
		super(name, type, dtype, reference);
		nfGUI.setMaximumFractionDigits(4);
		nfSQL.setGroupingUsed(false);
		if (nfSQL instanceof DecimalFormat) {
			DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
			newSymbols.setDecimalSeparator('.');
//			newSymbols.setGroupingSeparator('');
		     ((DecimalFormat) nfSQL).setDecimalFormatSymbols(newSymbols);
		 }				
	}
	public String getLogic() {
		return logical[logic];
	}

	public void setLogic(String logic) {
		for (int i=0; i < logical.length;i++)
			if (logical[i].equals(logic)) {
				this.logic = i;
				break;
			}	
	}
	public String getCondition() {
		return conditions[condition];
	}

	public String condition2SQL() {
		StringBuffer b = new StringBuffer();
		if (condition == 5) {
			b.append(getCondition());
			b.append(' ');
			b.append(nfSQL.format(minValue));
			b.append(" and "); 
			b.append(nfSQL.format(maxValue));
			
		} else {
			b.append(getCondition());
			b.append(' ');
			b.append(nfSQL.format(value));
			
		}
		return b.toString();
	}
	
	public void setCondition(String condition) {
		for (int i=0; i < conditions.length;i++)
			if (conditions[i].equals(condition)) {
				this.condition = i;
				break;
			}	
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String toString() {
			String units = getUnits();
			if (units.equals("NA") || units.equals("")) units = ""; else units = " ["+units+"]";
		    String n = getName();
	    	int p = n.lastIndexOf('.');
	    	if ((p == -1) || ((p+1)==n.length())) return n + units;
	    	else return  n.substring(p+1) + units;
	    	

	}	
	public String name2SQL() {
		StringBuffer b = new StringBuffer();
		b.append("(name=\"");
		b.append(getName());
		b.append("\" and value ");
		b.append(condition2SQL());
		b.append(')');
		return b.toString();
	}
	public String id2SQL(String alias) {
		StringBuffer b = new StringBuffer();
		b.append(" ");
		b.append(alias);
		b.append("iddescriptor=");
		b.append(getId());
		b.append(" and ");
		b.append(alias);
		b.append("value ");
		b.append(condition2SQL());
		return b.toString();
	}
	public String toSQL(String field) {
		//select iddescriptor,dvalues.idstructure,value as eHomo from dvalues where iddescriptor=2 and value < -4
		//return "select iddescriptor,dvalues.idstructure,value as "+field + " from dvalues where " + id2SQL(); 
		return "select dvalues.idstructure from dvalues where" + id2SQL("");
	}
	public String getSQLTable(String alias) {
	    return "DVALUES as "+alias; 
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (id == -1) this.enabled = false;
		else this.enabled = enabled;
	}
	public boolean isTrue(double query) {
		switch (condition) {
		case 0: return (query > value);
		case 1: return query >= value;
		case 2: return query == value;
		case 3: return query <= value;
		case 4: return query < value;
		case 5: return (query >= minValue) && (query <=maxValue);	
		default: return false;
		}
	}
}
