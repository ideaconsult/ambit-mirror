package ambit2.structure2name.rules;

public class FunctionalGroupData 
{
	public String name = null;
	public String atomSymbol = null;
	public String prefix = null;
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
			
		if (name != null)
		{
			sb.append("  ");
			sb.append(name);
		}
		
		if (atomSymbol != null)
		{
			sb.append("  ");
			sb.append(atomSymbol);
		}
		return sb.toString();
	}
	
	
}
