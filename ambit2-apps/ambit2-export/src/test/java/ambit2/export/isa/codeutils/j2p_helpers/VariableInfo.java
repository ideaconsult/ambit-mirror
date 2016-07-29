package ambit2.export.isa.codeutils.j2p_helpers;

import java.util.List;


public class VariableInfo 
{
	public static enum  Type {
		ARRAY, BOOLEAN, INTEGER, NUMBER, NULL, OBJECT, STRING 
	}
	
	public static enum  StringFormat {
		UNSPECIFIED, URI_FORMAT, DATE_TIME_FORMAT
	}
	
	public Type type = null;
	public String name = null;
	public String jsonPropertyName = null;
	public StringFormat stringFormat = StringFormat.UNSPECIFIED;
	public String objectClass = null; //used only for types ARRAY and OBJECT
	public List<String> enumList = null;
	
	public String getJavaSource(JavaSourceConfig sourceConfig)
	{
		switch (type)
		{
		case ARRAY:
			return "List<" + objectClass+"> "+ name + 
					sourceConfig.getArrayInitialization(objectClass) + ";";
		
		case BOOLEAN:
			return "boolean " + name + 
					sourceConfig.getBooleanInitialization() + ";";
		
		case INTEGER:
			return "int " + name + 
					sourceConfig.getIntegerInitialization() + ";";
		
		case NUMBER:
			return sourceConfig.number + " " + name + 
					sourceConfig.getNumberInitialization() + ";";
			
		case OBJECT:
			return objectClass + " " + name + 
					sourceConfig.getObjectInitialization(objectClass) + ";";
			
		case STRING:
		{	
			if (stringFormat != null)
			{
				if (stringFormat == StringFormat.URI_FORMAT)
					if (sourceConfig.FlagHandleURIString)
						return "URI " + name + 
							sourceConfig.getURIInitialization() + ";";
				
				if (stringFormat == StringFormat.DATE_TIME_FORMAT)
					if (sourceConfig.FlagHandleDateString)
						return "Date " + name + 
							sourceConfig.getDateInitialization() + ";";
				
			}
			return "String " + name + 
					sourceConfig.getStringInitialization() + ";";
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
