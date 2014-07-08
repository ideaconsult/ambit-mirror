package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryCondition;

public class EQCondition implements IQueryCondition {
	private static List<EQCondition> instances= null;
	private final static String eq = "=";
	private EQCondition() {
	}
	public String getName() {
		return eq;
	}

	public String getSQL() {
		return eq;
	}
	public static EQCondition getInstance() {
		List<EQCondition>  c = getAlowedValues();
		return c.get(0);
	}
	public static List<EQCondition> getAlowedValues() {
		if (instances == null) {
			instances = new ArrayList<EQCondition>();
			instances.add(new EQCondition());
			return instances;
		}
		return instances;
	}	
	@Override
	public String toString() {

		return eq;
	}
}
