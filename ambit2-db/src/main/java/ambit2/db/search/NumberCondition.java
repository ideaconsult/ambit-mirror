package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryCondition;

//TODO refactor with enums
public class NumberCondition implements IQueryCondition{
	private static List<NumberCondition> instances = null;
	public static final String between = "between";
	public static final String[] conditions = {">",">=","=","<=","<",between};
	protected int index;
	
	protected NumberCondition(int index) {
		setIndex(index);
	}
	public String getName() {
		return conditions[getIndex()];
	}

	public String getSQL() {
		return conditions[getIndex()];
	}
	private int getIndex() {
		return index;
	}
	private void setIndex(int index) {
		this.index = index;
	}
	public static List<NumberCondition> getAlowedValues() {
		if (instances == null) {
			ArrayList<NumberCondition> list = new ArrayList<NumberCondition>();
			for (int i=0; i < conditions.length;i++)
				list.add(new NumberCondition(i));
			return list;
		}
		return instances;
	}
	public static NumberCondition getInstance(String condition) {
		if (instances == null) {
			ArrayList<NumberCondition> list = new ArrayList<NumberCondition>();
			for (int i=0; i < conditions.length;i++)
				list.add(new NumberCondition(i));
			instances = list;
		}
		for (int i=0; i < conditions.length;i++)
			if (condition.equals(instances.get(i).getName()))
				return instances.get(i);
		return instances.get(0);
	}
	@Override
	public String toString() {
		return getSQL();
	}
}
