package ambit2.rules.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import ambit2.base.data.StructureRecord;

public class JSONParsingUtils 
{
	public static enum STRUCTURE_RECORD_INPUT_INFO {
		all, smiles, inchi
	}
	
	
	public static void jsonNodeToJavaField(JsonNode node, Object targetObj, String fieldName, boolean FlagExactFieldTypeMatch) throws Exception
	{
		if (node == null)
			throw (new Exception("JsonNode is null!"));
		
		if (targetObj == null)
			throw (new Exception("Target java object is null!"));
				
		Class cls = targetObj.getClass();
		Field field;
		String fType;
		
		try{
			field = cls.getDeclaredField(fieldName);
			fType = field.getType().getName();	
		}
		catch (Exception e)
		{
			throw (new Exception("Field " + fieldName + " does not exists!") );
		}
			
		
		/*
		if (fType.equals("java.lang.String"))
		{	
			field.set(targetObj, keyValue);
			return;
		}	
		
		if (fType.equals("int"))
		{	
			int intValue = Integer.parseInt(keyValue);
			field.set(targetObj, intValue);
			return;
		}
		
		if (fType.equals("double"))
		{	
			double doubleValue = Double.parseDouble(keyValue);
			field.set(targetObj, doubleValue);
			return;
		}
		*/
		
		throw (new Exception("Unsupported field type: " + fType + " for the field: " + field.getName()));
		
		//TODO ... may be add some other field types 
		
	}
	
	
	
	//-------------- Utils for general extraction of JSON data --------------------- 
	
	
	/**
	 * @param node
	 * @param keyword
	 * @param isRequired
	 * @return String value extracted from the JSON node or empty string if isRecquired = false (this case 
	 * is not treated as an error hence no exception is thrown)
	 * @throws Exception with the JSON error or missing value error (if isRequired = true) 
	 */
	public static String extractStringKeyword(JsonNode node, String keyword, boolean isRequired) throws Exception
	{
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception ("Keyword " + keyword + " is missing!"));
			
			return "";
		}
		
		if (keyNode.isTextual())
		{	
			return keyNode.asText();
		}
		else
		{	
			throw (new Exception ( "Keyword " + keyword + " is not of type text!"));
		}			
	}
	
	
	/**
	 * 
	 * @param node
	 * @param keyword
	 * @param isRequired
	 * @return Double value extracted from the JSON node or null pointer if isRecquired = false (this case 
	 * is not treated as an error hence no exception is thrown)
	 * @throws Exception with the JSON error or missing value error (if isRequired = true)
	 */
	public static Double extractDoubleKeyword(JsonNode node, String keyword, boolean isRequired) throws Exception
	{
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception ("Keyword " + keyword + " is missing!"));
			
			return null;
		}
		
		if (keyNode.isDouble())
		{	
			return keyNode.asDouble();
		}
		else
		{	
			throw (new Exception ( "Keyword " + keyword + " is not of type Double!"));
		}			
	}
	
	/**
	 * 
	 * @param node
	 * @param keyword
	 * @param isRequired
	 * @return Integer value extracted from the JSON node or null pointer if isRecquired = false (this case 
	 * is not treated as an error hence no exception is thrown)
	 * @throws Exception with the JSON error or missing value error (if isRequired = true)
	 */
	public static Integer extractIntKeyword(JsonNode node, String keyword, boolean isRequired) throws Exception
	{
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception ("Keyword " + keyword + " is missing!"));
			
			return null;
		}
		
		if (keyNode.isInt())
		{	
			return keyNode.asInt();
		}
		else
		{	
			throw (new Exception ( "Keyword " + keyword + " is not of type Int!"));
		}			
	}
	
	
	/**
	 * 
	 * @param node
	 * @param keyword
	 * @param isRequired
	 * @return Boolean value extracted from the JSON node or null pointer if isRecquired = false (this case 
	 * is not treated as an error hence no exception is thrown)
	 * @throws Exception with the JSON error or missing value error (if isRequired = true)
	 */
	public static Boolean extractBooleanKeyword(JsonNode node, String keyword, boolean isRequired) throws Exception
	{	
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception("Keyword " + keyword + " is missing!"));
			
			return null;
		}
		
		if (keyNode.isBoolean())
		{	
			return keyNode.asBoolean();
		}
		else
		{	
			throw (new Exception("Keyword " + keyword + " is not of type Boolean!"));
		}			
	}
	
	public static Object[] extractArrayKeyword(JsonNode node, String keyword, boolean isRequired) throws Exception
	{
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception("Keyword " + keyword + " is missing!"));
			
			return null;
		}
		
		if (keyNode.isArray())
		{
			int n = keyNode.size();
			Object objects[] = new Object[n];
			for (int i = 0; i < n; i++)
				objects[i] =  extractObject(keyNode.get(i));
			
			return objects;
		}
		else
		{
			throw (new Exception("Keyword " + keyword + " is not an array!"));
		}
	} 
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public static Object extractObject (JsonNode node)
	{
		if (node.isTextual())
		{
			String s = node.asText();
			if (s != null)
				return s;
		}
		
		if (node.isInt())
		{
			int i = node.asInt();
			return  new Integer(i);
		}
		
		if (node.isDouble())
		{
			double d  = node.asDouble();
			return new Double(d);
		}
		
		//TODO - eventually add array object extraction
		
		return null;
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static String objectToJsonField(Object obj)
	{
		if (obj == null)
			return null;
		
		if (obj instanceof String)
			return ("\"" + obj.toString() + "\"");
		
		if (obj instanceof Integer)
			return obj.toString();
		
		if (obj instanceof Double)
			return obj.toString();
		
		if (obj instanceof double[])
		{
			return toJsonField((double[]) obj);
		}
			
		//TODO handle some other cases as arrays etc.
		
		return null;
	}
	/**
	 * 
	 * @param d
	 * @return
	 */
	public static String toJsonField(double d[])
	{
		if (d == null)
			return null;
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < d.length; i++)
		{
			sb.append(d[i]);
			if (i < (d.length-1))
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static List<StructureRecord> getStructureRecords(JsonNode node, 
												String keyword, 
												STRUCTURE_RECORD_INPUT_INFO inputInfo, 
												boolean isRequired) throws Exception
	{
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
				throw (new Exception("Keyword " + keyword + " is missing!"));
			
			return null;
		}
		
		if (keyNode.isArray())
		{
			List<StructureRecord> records = new ArrayList<StructureRecord>();
			int n = keyNode.size();
			StringBuffer errors = new StringBuffer();
			for (int i = 0; i < n; i++)
			{	
				try{
					StructureRecord sr = getStructureRecord(keyNode.get(i), inputInfo);
					records.add(sr);
				}
				catch(Exception e)
				{
					errors.append("Structure record error: " 
							+ keyword + "[" + i+1+"] " + e.getMessage() + "\n");
				}
			}	
			
			if (!errors.toString().equals(""))
				throw (new Exception(errors.toString()));
			
			return records;
		}
		else
		{
			throw (new Exception("Keyword " + keyword + " is not an array!"));
		}
		
		
	}
	
	public static StructureRecord getStructureRecord(JsonNode node, 
			STRUCTURE_RECORD_INPUT_INFO inputInfo) throws Exception
	{
		switch (inputInfo)
		{
		case smiles:
			
			if (node.isTextual())
			{
				StructureRecord sr = new StructureRecord();
				sr.setSmiles(node.asText());
				return sr;
			}
			else
				throw (new Exception("Json node is not textual!"));
			
		default:
			break;
		}
		
		return null;
	}
	
}
