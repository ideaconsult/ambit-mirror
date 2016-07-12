package ambit2.export.isa.codeutils.j2p_helpers;

public class JavaSourceConfig 
{
	public static enum VarInit {
		NO_INIT, EMPTY, NULL
	};
	
	public String indent = "\t";
	public VarInit variableInit = VarInit.NO_INIT;
	
}
