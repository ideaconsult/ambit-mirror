package ambit2.db.search;

public interface IParameterizedQuery<F,T,C extends IQueryCondition> {
	public T getValue();
	public void setValue(T value);
	public C getCondition();
	public void setCondition(C condition);
	public F getFieldname();
	public void setFieldname(F fieldname);
}
