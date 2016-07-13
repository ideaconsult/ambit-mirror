package ambit2.export.isa.codeutils.j2p_helpers;

public class JavaSourceConfig 
{
	public static enum VarInit {
		NO_INIT, EMPTY, NULL, UNSPECIFIED
	};
	
	public String indent = "\t";
	public VarInit init = VarInit.NO_INIT;
	
	//These are specific init options for various variable types
	//If different from UNSPECIFIED override the default 'init'
	public VarInit arrayInit = VarInit.UNSPECIFIED;
	public VarInit booleanInit = VarInit.UNSPECIFIED;
	public VarInit integerInit = VarInit.UNSPECIFIED;
	public VarInit numberInit = VarInit.UNSPECIFIED;
	public VarInit objectInit = VarInit.UNSPECIFIED;
	public VarInit stringInit = VarInit.UNSPECIFIED;
	
	public VarInit getInit(VariableInfo.Type varType)
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
			
		}
		
		return VarInit.NO_INIT;
	}
	
}
