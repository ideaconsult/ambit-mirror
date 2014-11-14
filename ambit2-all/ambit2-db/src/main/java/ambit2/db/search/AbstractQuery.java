package ambit2.db.search;

import java.util.logging.Logger;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.config.Preferences;

import com.jgoodies.binding.beans.Model;

/**
 * Abstract class for a query.
 * <pre>
 * F: field to search for
 * T: value to search for
 * C: condition (implements {@link IQueryCondition})
 * </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 10, 2008
 */
public abstract class AbstractQuery<F,T,C extends IQueryCondition,ResultType> extends Model 
								implements IQueryObject<ResultType>,IParameterizedQuery<F,T,C>{
	/**
	 * 
	 */
	protected static Logger logger = Logger.getLogger(AbstractQuery.class.getName());
	private static final long serialVersionUID = 3120118597644963365L;
	protected F fieldname;
	protected T value;
	protected C condition;
	protected boolean selected;
	protected String name;
	protected Integer id=-1;
	protected long maxRecords = 10000;
	protected int page = 0;

	public AbstractQuery() {
		super();
		try {
			setPageSize(Integer.parseInt(Preferences.getProperty(Preferences.MAXRECORDS)));
		} catch (Exception x) {
			setPageSize(1000);
		}	
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public C getCondition() {
		return condition;
	}

	public void setCondition(C condition) {
		this.condition = condition;
	}

	public F getFieldname() {
		return fieldname;
	}

	public void setFieldname(F fieldname) {
		this.fieldname = fieldname;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean test(T object) throws AmbitException {
		throw new AmbitException("Not implemented");
	}
	@Override
	public String toString() {
		if ((getFieldname()==null) && (getValue()==null)) return getClass().getName();
		StringBuilder b = new StringBuilder();
		if (getFieldname()==null) b.append("Property");
		else b.append(getFieldname());
		b.append(' ');
		b.append(getCondition()==null?"":getCondition());
		b.append(' ');
		if (getValue() != null) 
			b.append(getValue());
		return b.toString();
	}
	public void setPageSize(long records) {
		this.maxRecords = records>0?records:10000;
		
	}
	public long getPageSize() {
		return maxRecords;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getKey() {
		return null;
	}
	public String getCategory() {
		return null;
	}
	@Override
	public boolean supportsPaging() {
		return true;
	}
	
}
