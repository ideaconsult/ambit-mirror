package ambit2.rules.conditions.value;

public interface IValue 
{
	public double getValue();
	public void setValue(double value);
	
	public Relation getRelation();
	public void setRelation(Relation relation);
	
	
	public static enum Relation {
		EQUALS ("=") {
			@Override
			public boolean check(double value, double comparisonTarget)
			{
				return (comparisonTarget == value); 
			}
		},
		LESS_THAN ("<") {
			@Override
			public boolean check(double value, double comparisonTarget)
			{
				return (comparisonTarget < value); 
			}
		},
		LESS_THAN_OR_EQUALS ("<=") {
			@Override
			public boolean check(double value, double comparisonTarget)
			{
				return (comparisonTarget <= value); 
			}
		}, 
		GREATER_THAN (">"){
			@Override
			public boolean check(double value, double comparisonTarget)
			{
				return (comparisonTarget > value); 
			}
		}, 
		GREATER_THAN_OR_EQUALS (">=") {
			@Override
			public boolean check(double value, double comparisonTarget)
			{
				return (comparisonTarget >= value); 
			}
		},		
		DIFFERENT ("!=") {
			@Override
			public boolean check(double value, double comparisonTarget)
			{
				return (comparisonTarget != value); 
			}
		}; 
		
		private final String string;
		
		private Relation(String string){
			this.string = string;
		}
		
		public String getRelationString(){
			return string;
		}
		
		public static Relation getRelationFromString(String s)
		{
			Relation r[] = Relation.values();
			for (int i = 0; i < r.length; i++)
				if (s.equals(r[i].getRelationString()))
					return r[i];
			//Some additional string cases:
			if (s.equals("<>"))
				return Relation.DIFFERENT;
			if (s.equals("=="))
				return Relation.EQUALS;
			
			return null;
		}
		
		public boolean check(double value, double comparisonTarget)
		{
			//default value is overridden for each enum value  
			return true; 
		}
	}
	
}
