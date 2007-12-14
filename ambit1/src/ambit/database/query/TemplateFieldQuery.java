package ambit.database.query;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import ambit.data.experiment.TemplateField;

/**
 * A query for {@link ambit.data.experiment.TemplateField}. <br>
 * Used in {@link ambit.database.query.ExperimentQuery},{@link ambit.database.query.ExperimentConditionsQuery},{@link ambit.database.processors.ExperimentSearchProcessor}, {@link ambit.database.search.DbSearchExperiments}
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class TemplateFieldQuery extends TemplateField {
	public static NumberFormat nf = NumberFormat.getInstance(Locale.US);
	public static final String[] conditions= {">",">=","=","<=","<","between","ALL"};
	public static final String[] logical= {" ","and","or"}; 
	protected double minValue = 0;
	protected double maxValue = 1;
	protected Object value;
	protected int condition = 6;
	protected int logic = 0;
	protected boolean enabled = false;

	public TemplateFieldQuery() {
		this("");

	}

	public TemplateFieldQuery(String name) {
		this(name,-1);
	}

	public TemplateFieldQuery(String name, String units, boolean numeric,
			boolean isResult) {
		super(name, units, numeric, isResult);
		nf.setMaximumFractionDigits(4);
		nf.setGroupingUsed(false);
		if (nf instanceof DecimalFormat) {
			DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
			newSymbols.setDecimalSeparator('.');
			newSymbols.setGroupingSeparator(' ');
		     ((DecimalFormat) nf).setDecimalFormatSymbols(newSymbols);
		 }
	}

	public TemplateFieldQuery(String name, int id) {
		super(name, id);
		nf.setMaximumFractionDigits(4);
		nf.setGroupingUsed(false);
		if (nf instanceof DecimalFormat) {
			DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
			newSymbols.setDecimalSeparator('.');
			newSymbols.setGroupingSeparator(' ');
		     ((DecimalFormat) nf).setDecimalFormatSymbols(newSymbols);
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
		if (condition == 6) {
			
		} else if (condition == 5) {
			b.append(getCondition());
			b.append(' ');
			b.append(nf.format(Math.floor(minValue)));
			b.append(" and "); 
			b.append(nf.format(Math.ceil(maxValue)));
			
		} else {
			b.append(getCondition());
			b.append(' ');
			if (numeric)
				b.append(nf.format(value));
			else {
				b.append('"');
				b.append(value.toString());
				b.append('"');
			}	
			
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

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	public String toString() {
		String units = getUnits();
		if (units.equals("NA") || units.equals("")) units = ""; else units = " ["+units+"]";
	    String n = getName();
    	//int p = n.lastIndexOf('.');
    	//if (p == -1) return n + units;
    	//else return  n.substring(p+1) + units;
	    return n + units;
    }
	public String getSQLTable(String alias) {
	    if (isResult)
	        return "study_results  as "+alias; 
	    else return "study_conditions as "+alias;
	}

	public String id2SQL(String alias) {
		StringBuffer b = new StringBuffer();
		b.append(" ");
		b.append(alias);
		b.append("id_fieldname=");
		b.append(getId());
		if (condition < 6) { 
			b.append(" and ");
			b.append(alias);
			if (numeric) 
				b.append("value_num ");
			else 
				b.append("value ");
			b.append(condition2SQL());
		}
		return b.toString();
	}
	public String toSQL(String field) {
		StringBuffer b = new StringBuffer();
		b.append("select idexperiment from study_results ");
		if (condition == 6) return b.toString();
		else b.append("where");
		
		b.append(id2SQL(""));
		return b.toString(); 
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	
}
