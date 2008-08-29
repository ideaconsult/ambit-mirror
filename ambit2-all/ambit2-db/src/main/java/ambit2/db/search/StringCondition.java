package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

public class StringCondition implements IQueryCondition {
	private static List<StringCondition> instances = null;
	public static final String C_EQ="=";
	public static final String C_LIKE="like";
	public static final String C_NOTLIKE="not like";
	public static final String C_SOUNDSLIKE="sounds like";
	public static final String C_REGEXP="regexp";
	
	public static final String[] conditions = {C_EQ,C_LIKE,C_NOTLIKE,C_SOUNDSLIKE,C_REGEXP};
	protected int index;
	
	protected StringCondition(int index) {
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
	public static List<StringCondition> getAlowedValues() {
		if (instances == null) {
			ArrayList<StringCondition> list = new ArrayList<StringCondition>();
			for (int i=0; i < conditions.length;i++)
				list.add(new StringCondition(i));
			instances = list;
		}
		return instances;
	}
	public static StringCondition getInstance(String condition) {
		List<StringCondition> inst = getAlowedValues();
		for (int i=0; i < conditions.length;i++)
			if (condition.equals(inst.get(i).getName()))
				return inst.get(i);
		return inst.get(0);
	}
	@Override
	public String toString() {
		return getSQL();
	}

}
