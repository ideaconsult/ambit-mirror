package ambit2.export.isa.codeutils.j2p_helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

import ambit2.export.isa.v1_0.objects.Data;


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
	public String objectClass = null; //used only for types ARRAY, OBJECT and String enum
	public List<String> enumList = null;
	public String enumName = null;
	
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
			
			if (enumName != null)
				if (sourceConfig.FlagHandleEnumString)
					return objectClass + "." + enumName + " " + name + ";";
					
					
			return "String " + name + 
					sourceConfig.getStringInitialization() + ";";
		}	
			
		}
		return "";
	}
	
	public String getEnumJavaSource(JavaSourceConfig sourceConfig, String superClass, String indent, ClassNameGenerator cnGen)
	{
		if (enumName == null)
			return "";
		
		StringBuffer sb = new StringBuffer();
		sb.append(indent + "@Generated(\"" + sourceConfig.generatedInfo + "\")"  + sourceConfig.endLine);
		sb.append(indent + "public static enum " + enumName + " {" + sourceConfig.endLine);
		for (int i = 0; i < enumList.size(); i++)
		{	
			sb.append(indent + indent + cnGen.getJavaEnumElementValue(enumList.get(i)) 
					+ "(\"" + enumList.get(i) + "\")");
			if (i == (enumList.size()-1))
				sb.append(";" + sourceConfig.endLine);
			else
				sb.append("," + sourceConfig.endLine);
		}
		sb.append(indent + indent + "private final String value;" + sourceConfig.endLine);
		sb.append(indent + indent + "private static Map<String, " + objectClass + "." + enumName +
						"> constants = new HashMap<String, " +  objectClass + "." + enumName + ">();" +
						sourceConfig.endLine + sourceConfig.endLine);
		
		sb.append(indent + indent + "static {" + sourceConfig.endLine);
		sb.append(indent + indent + indent + "for (" + objectClass + "." + enumName + " c: values()) {" + sourceConfig.endLine);
		sb.append(indent + indent + indent + indent + "constants.put(c.value, c);" + sourceConfig.endLine);
		sb.append(indent + indent + indent + "}" + sourceConfig.endLine);
		sb.append(indent + indent + "}" + sourceConfig.endLine);
		sb.append(sourceConfig.endLine);
		
		sb.append(indent + indent + "private Type(String value) {" + sourceConfig.endLine);
		sb.append(indent + indent + indent + "this.value = value;" + sourceConfig.endLine);
		sb.append(indent + indent + "}" + sourceConfig.endLine);
		sb.append(sourceConfig.endLine);
		
		sb.append(indent + indent + "@JsonValue" + sourceConfig.endLine);
		sb.append(indent + indent + "@Override" + sourceConfig.endLine);
		sb.append(indent + indent + "public String toString() {" + sourceConfig.endLine);
		sb.append(indent + indent + indent + "return this.value;" + sourceConfig.endLine);
		sb.append(indent + indent + "}" + sourceConfig.endLine);
		sb.append(sourceConfig.endLine);
		
		sb.append(indent + indent + "public static " + objectClass + "." + enumName 
					+ " fromValue(String value) {" + sourceConfig.endLine);
		sb.append(indent + indent + indent + objectClass + "." + enumName 
					+ " constant = constants.get(value);" + sourceConfig.endLine);
		sb.append(indent + indent + indent  + "if (constant == null) {" + sourceConfig.endLine);
		sb.append(indent + indent + indent + indent + "throw new IllegalArgumentException(value);" + sourceConfig.endLine);
		sb.append(indent + indent + indent+ "} else {" + sourceConfig.endLine);
		sb.append(indent + indent + indent + indent + "return constant;" + sourceConfig.endLine);
		sb.append(indent + indent + indent + "}" + sourceConfig.endLine);
		sb.append(indent + indent + "}" + sourceConfig.endLine);
		sb.append(sourceConfig.endLine);
		
		
		//TODO
		
		
		sb.append(indent + "}" + sourceConfig.endLine);
		return sb.toString();
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
