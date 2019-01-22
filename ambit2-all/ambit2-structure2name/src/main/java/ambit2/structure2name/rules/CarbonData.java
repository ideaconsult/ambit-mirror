package ambit2.structure2name.rules;

public class CarbonData 
{
	public int number = 0;
	public String prefix = null;
	public String alchoholName = null;
	public String aldehydeName = null;
	public String acidName = null;
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(number);		
		if (prefix != null)
		{
			sb.append("  ");
			sb.append(prefix);
		}
		return sb.toString();
	}
	
}
