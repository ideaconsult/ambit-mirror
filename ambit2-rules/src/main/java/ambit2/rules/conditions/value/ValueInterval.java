package ambit2.rules.conditions.value;

import ambit2.rules.conditions.value.IValue.Relation;

public class ValueInterval implements IValueInterval  
{
	private double loValue = 0.0;
	private double hiValue = 1.0;
	private Relation loRelation = Relation.GREATER_THAN_OR_EQUALS;
	private Relation hiRelation = Relation.LESS_THAN_OR_EQUALS;
	
	public ValueInterval(){
	}
	
	public ValueInterval(double loValue, double hiValue){
		setLoValue(loValue);
		setHiValue(hiValue);
		setLoRelation(Relation.GREATER_THAN_OR_EQUALS);
		setHiRelation(Relation.LESS_THAN_OR_EQUALS);
	}
	
	public ValueInterval(double loValue, double hiValue, Relation loRelation, Relation hiRelation){
		setLoValue(loValue);
		setHiValue(hiValue);
		setLoRelation(Relation.GREATER_THAN_OR_EQUALS);
		setHiRelation(Relation.LESS_THAN_OR_EQUALS);
	}	
	
	@Override
	public double getLoValue() {
		return loValue;
	}

	@Override
	public void setLoValue(double value) {
		this.loValue = value;
	}

	@Override
	public Relation getLoRelation() {
		return loRelation;
	}

	@Override
	public void setLoRelation(Relation relation) {
		this.loRelation = relation;
	}

	@Override
	public double getHiValue() {
		return hiValue;
	}

	@Override
	public void setHiValue(double value) {
		this.hiValue = value;
	}

	@Override
	public Relation getHiRelation() {
		return hiRelation;
	}

	@Override
	public void setHiRelation(Relation relation) {
		this.hiRelation = relation;
	}
}
