package ambit2.rules.conditions.value;

import ambit2.rules.conditions.value.IValue.Relation;


public interface IValueInterval 
{
	public double getLoValue();
	public void setLoValue(double value);
	public Relation getLoRelation();
	public void setLoRelation(Relation relation);
	
	public double getHiValue();
	public void setHiValue(double value);
	public Relation getHiRelation();
	public void setHiRelation(Relation relation);
}
