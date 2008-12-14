package ambit2.db.search;

import ambit2.core.data.experiment.TemplateField;

/**
 * A query for {@link TemplateField}. <br>
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2008
 */
public class TemplateFieldQuery<T> extends TemplateField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5425487763776870611L;
	protected T value;


	public TemplateFieldQuery() {
		super("");

	}
	public TemplateFieldQuery(String name, String units, boolean numeric,
			boolean isResult) {
		super(name, units, numeric, isResult);
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}

}
