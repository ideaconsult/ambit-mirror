package ambit2.export.isa.codeutils.j2p_helpers;

import java.util.ArrayList;
import java.util.List;

import ambit2.export.isa.v1_0.objects.Comment;

public class VariableInfo 
{
	public static enum  Type {
		ARRAY, BOOLEAN, INTEGER, NUMBER, NULL, OBJECT, STRING 
	}
	
	public Type type = null;
	public String name = null;
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
			return "String " + name + ";";
			
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
