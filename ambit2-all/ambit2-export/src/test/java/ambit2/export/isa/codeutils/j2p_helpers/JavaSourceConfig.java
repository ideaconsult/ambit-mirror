package ambit2.export.isa.codeutils.j2p_helpers;

import java.util.HashMap;
import java.util.Map;


public class JavaSourceConfig 
{
	public static String WINDOWS_ENDLINE = "\r\n";
	public static String UNIX_ENDLINE = "\n";
	
	public static enum VarInit {
		NO_INIT, EMPTY, NULL, UNSPECIFIED
	};
	
	public String indent = "\t";
	public String endLine = "\n";
	
	public String generatedInfo = "ambit.json2pojo";
	
	public VarInit init = VarInit.NO_INIT;
	//These are specific init options for various variable types
	//If different from UNSPECIFIED override the default 'init'
	public VarInit arrayInit = VarInit.UNSPECIFIED;
	public VarInit booleanInit = VarInit.UNSPECIFIED;
	public VarInit integerInit = VarInit.UNSPECIFIED;
	public VarInit numberInit = VarInit.UNSPECIFIED;
	public VarInit objectInit = VarInit.UNSPECIFIED;
	public VarInit stringInit = VarInit.UNSPECIFIED;
	
	//custom init for list of classes
	public Map<String,VarInit> customClassInit = new HashMap<String,VarInit>();
	
	public String number = "double";
	public boolean FlagHandleURIString = true;
	public boolean FlagHandleDateString = true;
	public boolean FlagHandleEnumString = false;
	public boolean FlagJsonAnnotation = false;
	
	public boolean FlagUseSchemaTitleAsComment = true;
	public boolean FlagUseSchemaNameAsComment = false;
	public boolean FlagUseSchemaDescriptionAsComment = true;
	
	public VarInit getVarInit(VariableInfo.Type varType)
	{
		switch (varType)
		{
		case ARRAY:
			if (arrayInit == VarInit.UNSPECIFIED)
				return init;
			else
				return arrayInit;
			
		case BOOLEAN:
			if (booleanInit == VarInit.UNSPECIFIED)
				return init;
			else
				return booleanInit;
			
		case INTEGER:
			if (integerInit == VarInit.UNSPECIFIED)
				return init;
			else
				return integerInit;
			
		case NUMBER:
			if (numberInit == VarInit.UNSPECIFIED)
				return init;
			else
				return numberInit;	
		
		case OBJECT:
			if (objectInit == VarInit.UNSPECIFIED)
				return init;
			else
				return objectInit;	
		
		case STRING:
			if (stringInit == VarInit.UNSPECIFIED)
				return init;
			else
				return stringInit;	
			
		}
		
		return VarInit.NO_INIT;
	}
	
	public String getArrayInitialization(String objectClass)
	{
		VarInit vInit = getVarInit(VariableInfo.Type.ARRAY);
		if (vInit == VarInit.EMPTY)
			return " = new ArrayList<" + objectClass + ">()";
		
		if (vInit == VarInit.NULL)
			return " = null";
		
		return "";
	}
	
	public String getBooleanInitialization()
	{
		VarInit vInit = getVarInit(VariableInfo.Type.BOOLEAN);
		if (vInit == VarInit.EMPTY || vInit == VarInit.NULL)
			return " = false";
		
		return "";
	}
	
	public String getIntegerInitialization()
	{
		VarInit vInit = getVarInit(VariableInfo.Type.INTEGER);
		if (vInit == VarInit.EMPTY || vInit == VarInit.NULL)
			return " = 0";
		
		return "";
	}
	
	public String getNumberInitialization()
	{
		VarInit vInit = getVarInit(VariableInfo.Type.NUMBER);
		if (vInit == VarInit.EMPTY || vInit == VarInit.NULL)
			return " = 0.0";
		
		return "";
	}
	
	public String getObjectInitialization(String objectClass)
	{
		VarInit vInit = getVarInit(VariableInfo.Type.OBJECT);
		VarInit customInit = customClassInit.get(objectClass);
		if (customInit != null)
				vInit = customInit; 
		
		if (vInit == VarInit.EMPTY)
			return " = new " + objectClass + "()";
		
		if (vInit == VarInit.NULL)
			return " = null";
		
		return "";
	}
	
	public String getStringInitialization()
	{
		VarInit vInit = getVarInit(VariableInfo.Type.STRING);
		if (vInit == VarInit.EMPTY)
			return " = \"\"";
		
		if (vInit == VarInit.NULL)
			return " = null";
		
		return "";
	}
	
	public String getURIInitialization()
	{	
		VarInit vInit = getVarInit(VariableInfo.Type.STRING);
		if (vInit == VarInit.EMPTY)
			//return " = new URI(\"\")";  //check this case!!  + exception handling ...
			return "";  //URI is not initilized wince it requires exception handling;
		
		if (vInit == VarInit.NULL)
			return " = null";
		
		return "";
	}
	
	public String getDateInitialization()
	{	
		VarInit vInit = getVarInit(VariableInfo.Type.STRING);
		if (vInit == VarInit.EMPTY)
			return " = new Date()";  
		
		if (vInit == VarInit.NULL)
			return " = null";
		
		return "";
	}
	
}
