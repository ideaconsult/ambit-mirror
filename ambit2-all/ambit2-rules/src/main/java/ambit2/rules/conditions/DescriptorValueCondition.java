package ambit2.rules.conditions;

import ambit2.rules.conditions.value.IValue;

public class DescriptorValueCondition implements IDescriptorValueCondition 
{
	private IValue value = null;
	private IDescriptorSolver solver = null;
	private String descrName = null;
	private boolean FlagNegated = false;
	
	public DescriptorValueCondition(IValue value, IDescriptorSolver solver, String descrName)
	{
		setValue(value);
		setDescriptorSolver(solver);
		setDescriptorName(descrName);
	}
	
	@Override
	public boolean isTrue(Object target) {
		if (target instanceof Double)
			return isTrue((Double) target);
		if (target instanceof Integer)
			return isTrue(((Integer) target).doubleValue());
		
		if (solver == null)
			return false;
		
		Object res = solver.calculateDescriptor(descrName, target);
		if (res instanceof Double)
			return isTrue((Double)res);
		
		return false;
	}
	
	@Override
	public boolean isTrue(Double target) {
		if (value == null)
			return false;
		//System.out.println("isTrue: target" + target + "  " + value.getRelation().getRelationString() + " "+value.getValue());
		
		if (this.isNegated())
			return !(value.getRelation().check(value.getValue(), target));
		else
			return value.getRelation().check(value.getValue(), target);
	}

	@Override
	public IValue getValue() {
		return value;
	}

	@Override
	public void setValue(IValue value) {
		this.value = value;
	}

	@Override
	public boolean isNegated() {
		return FlagNegated;
	}
	
	@Override
	public void setIsNegated(boolean isNeg) {
		FlagNegated = isNeg;
	}

	@Override
	public void setDescriptorSolver(IDescriptorSolver solver) {
		this.solver = solver;
	}

	@Override
	public IDescriptorSolver getDescriptorSolver() {
		return solver;
	}

	@Override
	public void setDescriptorName(String name) {
		descrName = name;
	}

	@Override
	public String getDescriptorName() {
		return descrName;
	}
	
	public String toString(){
		return "" + descrName + " " + value.getRelation().getRelationString()+ " " + value.getValue();
	}
}
