package ambit2.db.search;

import ambit2.core.exceptions.AmbitException;

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
public abstract class AbstractQuery<F,T,C extends IQueryCondition>  extends Model implements IQueryObject {
	protected F fieldname;
	protected T value;
	protected C condition;
	protected boolean selected;
	protected String name;
	protected Integer id;
	

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
}
