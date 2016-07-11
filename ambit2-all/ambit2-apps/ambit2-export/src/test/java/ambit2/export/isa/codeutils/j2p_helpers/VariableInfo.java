package ambit2.export.isa.codeutils.j2p_helpers;

public class VariableInfo 
{
	public static enum  Type {
		STRING, ARRAY, OBJECT; 
	}
	
	public Type type = null;
	public String name = null;
	public String objectClass = null; //used only for type OBJECT
	
	public static Type getTypeFromString(String s)
	{
		if (s.equals("string"))
			return Type.STRING;
		
		if (s.equals("array"))
			return Type.ARRAY;
		
		if (s.equals("object"))
			return Type.OBJECT;
		
		return null;
	}
	
}
