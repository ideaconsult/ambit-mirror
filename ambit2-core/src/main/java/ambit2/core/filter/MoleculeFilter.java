package ambit2.core.filter;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.filter.IntPropertyIntervalCondition.PropertyType;

public class MoleculeFilter 
{	
	public static final int  BIG_INTEGER = 1000000000;
	public static final char intervalSep = ',';
	public static final char openBracket = '[';
	public static final char closeBracket = ']';
	
	
	private ArrayList<IMoleculeFilterCondition> conditions = new ArrayList<IMoleculeFilterCondition>();
	
	public boolean useMolecule(IAtomContainer container, int recordNum)
	{
		for (IMoleculeFilterCondition cond : conditions)
			if (!cond.useMolecule(container, recordNum))
				return false;
		
		return true;
	};
	
	public boolean isAromaticityInfoNeeded()
	{
		for (IMoleculeFilterCondition cond : conditions)
			if (isAromaticityInfoNeeded(cond))
				return true;
		
		return false;
	}
	
	
	
	public ArrayList<IMoleculeFilterCondition> getConditions() {
		return conditions;
	}

	public void setConditions(ArrayList<IMoleculeFilterCondition> conditions) {
		this.conditions = conditions;
	}
	
	/**
	 * 
	 * @param filterString
	 * @return
	 * @throws Exception
	 * 
	 * The filterString: <condition1>;<condition2>; ...;<conditionk>
	 * <condition>: 
	 * 		#a-b  (record number in the interval [a,b])
	 * 		NA=a-b  
	 * 		NB=a-b
	 * 		CYCCLOMATIC = a-b
	 * 
	 */
	public static MoleculeFilter parseFromCommandLineString(String filterString) throws Exception
	{	
		MoleculeFilter filter = new MoleculeFilter();
		String tok[] = filterString.split(";");
		for (int i = 0; i < tok.length; i++)
		{
			IMoleculeFilterCondition cond = getConditionFromString(tok[i].trim());
			if (cond != null)
				filter.conditions.add(cond);
			else
				throw (new Exception("Incorrect filter condition string: " + tok[i]));
		}
		return filter;
	}

	public static IMoleculeFilterCondition getConditionFromString(String s) throws Exception
	{
		IMoleculeFilterCondition cond =  null; 
		
		if (s.startsWith("#Mol="))
		{
			RecordIntervalCondition rcond  = new RecordIntervalCondition();
			int interval[] = extractIntegerIntervalFromString(s.substring(5));
			if (interval == null)
				return null;
			rcond.setFirstValue(interval[0]);
			rcond.setLastValue(interval[1]);
			return rcond;
		}
		
		
		IntPropertyIntervalCondition pcond = null;
		int pos = 0;
		
		if (s.startsWith("NA="))
		{
			pcond  = new IntPropertyIntervalCondition();
			pcond.setPropertyType(PropertyType.NA);
			pos = 3;
		}
		
		if (s.startsWith("NB="))
		{
			pcond  = new IntPropertyIntervalCondition();
			pcond.setPropertyType(PropertyType.NB);
			pos = 3;
		}
		
		if (s.startsWith("CYCLOMATIC="))
		{
			pcond  = new IntPropertyIntervalCondition();
			pcond.setPropertyType(PropertyType.CYCLOMATIC);
			pos = 11;
		}
		
		if (s.startsWith("NAromAt="))
		{
			pcond  = new IntPropertyIntervalCondition();
			pcond.setPropertyType(PropertyType.NAromAt);
			pos = 8;
		}
		
		
		if (pcond != null)
		{
			int interval[] = extractIntegerIntervalFromString(s.substring(pos));
			if (interval == null)
				return null;
			
			pcond.setFirstValue(interval[0]);
			pcond.setLastValue(interval[1]);
			return pcond;
		}
		
		return cond;
	}
	
	private static int[] extractIntegerIntervalFromString(String s)
	{
		int interval[] = new int[2];
		int pos = 0;
		int res[];
		
		if (s.isEmpty())
			return null;
		
		if (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '-' || s.charAt(pos) == '+')
		{
			//This is the case of a single value - interval of the type [a,a]
			res = getIntegerFromPos(s, pos);

			if (res == null)
				return null;

			interval[0] = res[0];
			interval[1] = interval[0];
			pos = res[1];
			
			if (pos < s.length())
				return null;  //Error some extra symbols after interval definition
			
			return interval;
		}
		
		
		if (s.charAt(pos) == MoleculeFilter.openBracket)
			pos++;
		else
			return null;
		
		if (pos >= s.length())
			return null;
		
		
		if (s.charAt(pos) == MoleculeFilter.intervalSep)
		{
			pos++;
			interval[0] = -BIG_INTEGER;
		}
		else
		{
			//Read firstValue
			res = getIntegerFromPos(s, pos);

			if (res == null)
				return null;

			interval[0] = res[0];
			pos = res[1];

			if (pos >= s.length())
			{	
				return null;
			}

			if (s.charAt(pos) != MoleculeFilter.intervalSep)
				return null; //Error

			pos++;
		}
		
		
		if (s.charAt(pos) == MoleculeFilter.closeBracket)
		{
			//The last value is set to be BIG_INTEGER
			interval[1] = BIG_INTEGER;
			pos++;
			if (pos < s.length())
				return null;  //Error some extra symbols after interval definition
			
			return interval;
		}
		
		res = getIntegerFromPos(s, pos);
		
		if (res == null)
			return null;
		
		interval[1] = res[0];
		pos = res[1];
		
		if (s.charAt(pos) == MoleculeFilter.closeBracket)
		{
			pos++;
			if (pos < s.length())
				return null;  //Error some extra symbols after interval definition
			
			return interval;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param s
	 * @param pos
	 * @return [0] = integer value, [1] = end position in string s
	 * 
	 */
	private static int[] getIntegerFromPos(String s, int pos)
	{
		int curChar = pos;
		if (curChar >= s.length())
			return null;
		
		if (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '-' || s.charAt(pos) == '+')
			curChar++;
		else
			return null;
		
		
		while (curChar < s.length())
		{
			char ch = s.charAt(curChar);
			if (Character.isDigit(ch))
			{	
				curChar++;
			}
			else
				break;
		}
		
		int n;
		
		try{
			n = Integer.parseInt(s.substring(pos,curChar));
		}catch(Exception x){
			return null;
		}
		
		int res[] = new int[2];
		res[0] = n;
		res[1] = curChar;
		return(res);
	}
	
	public static boolean isAromaticityInfoNeeded(IMoleculeFilterCondition cond)
	{
		if (cond instanceof IntPropertyIntervalCondition)
		{
			IntPropertyIntervalCondition c = (IntPropertyIntervalCondition) cond;
			if (c.getPropertyType() == PropertyType.NAromAt)
				return true;
		}
		
		return false;
	}	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conditions.size(); i++)
			sb.append(conditions.get(i).toString() + ((i<conditions.size()-1)?";":""));
			
		return sb.toString();
	}
	
	
}
