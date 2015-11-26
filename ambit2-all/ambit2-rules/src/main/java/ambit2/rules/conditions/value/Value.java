package ambit2.rules.conditions.value;

public class Value implements IValue 
{
	private double value = 0.0;
	private Relation relation = Relation.EQUALS; 
	
	public Value(){
	}
	
	public Value(double value, Relation relation){
		setValue(value);
		setRelation(relation);
	}
	
	public Value(double value, String relationString){
		setValue(value);
		Relation rel = Relation.getRelationFromString(relationString);
		setRelation(rel);
	}
	
	@Override
	public double getValue() {
		return value;
	}
	
	@Override
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public Relation getRelation() {
		return relation;
	}

	@Override
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
}
