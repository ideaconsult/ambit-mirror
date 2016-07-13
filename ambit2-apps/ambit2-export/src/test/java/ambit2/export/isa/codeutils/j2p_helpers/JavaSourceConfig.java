package ambit2.export.isa.codeutils.j2p_helpers;

public class JavaSourceConfig 
{
	public static enum VarInit {
		NO_INIT, EMPTY, NULL, UNSPECIFIED
	};
	
	public String indent = "\t";
	public VarInit init = VarInit.NO_INIT;
	public String number = "double";
	
	//These are specific init options for various variable types
	//If different from UNSPECIFIED override the default 'init'
	public VarInit arrayInit = VarInit.UNSPECIFIED;
	public VarInit booleanInit = VarInit.UNSPECIFIED;
	public VarInit integerInit = VarInit.UNSPECIFIED;
	public VarInit numberInit = VarInit.UNSPECIFIED;
	public VarInit objectInit = VarInit.UNSPECIFIED;
	public VarInit stringInit = VarInit.UNSPECIFIED;
	
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
		VarInit vInit = getVarInit(VariableInfo.Type.ARRAY);
		if (vInit == VarInit.EMPTY || vInit == VarInit.NULL)
			return " = false";
		
		return "";
	}
	
}
