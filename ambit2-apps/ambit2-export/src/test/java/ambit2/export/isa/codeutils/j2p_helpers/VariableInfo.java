package ambit2.export.isa.codeutils.j2p_helpers;


public class VariableInfo 
{
	public static enum  Type {
		ARRAY, BOOLEAN, INTEGER, NUMBER, NULL, OBJECT, STRING 
	}
	
	public static enum  StringFormat {
		UNSPECIFIED, URL_FORMAT 
	}
	
	public Type type = null;
	public String name = null;
	public StringFormat stringFormat = StringFormat.UNSPECIFIED;
	public String objectClass = null; //used only for types ARRAY and OBJECT
	
	public String getJavaSource()
	{
		switch (type)
		{
		case ARRAY:
			return "List<" + objectClass+"> = new ArrayList<" + objectClass + ">();";
		
		case BOOLEAN:
			return "boolean " + name + ";";
		
		case INTEGER:
			return "int " + name + ";";
		
		case NUMBER:
			return "double " + name + ";";
			
		case OBJECT:
			return objectClass + " " + name + ";";
			
		case STRING:
		{	
			if (stringFormat != null)
			{
				if (stringFormat == StringFormat.URL_FORMAT)
					return "URL " + name + ";";
			}
			return "String " + name + ";";
		}	
			
		}
		return "";
	}
	
	public static Type getTypeFromString(String s)
	{
		//System.out.println("getTypeFromString " + s);
		if (s.equals("array"))
			return Type.ARRAY;
		
		if (s.equals("boolean"))
			return Type.BOOLEAN;
		
		if (s.equals("integer"))
			return Type.INTEGER;
		
		if (s.equals("number"))
			return Type.NUMBER;
		
		if (s.equals("null"))
			return Type.NULL;

		if (s.equals("object"))
			return Type.OBJECT;
		
		if (s.equals("string"))
			return Type.STRING;
		
		
		return null;
	}
	
}
