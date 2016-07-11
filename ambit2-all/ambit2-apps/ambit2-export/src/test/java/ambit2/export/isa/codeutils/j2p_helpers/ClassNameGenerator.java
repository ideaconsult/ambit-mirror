package ambit2.export.isa.codeutils.j2p_helpers;

import ambit2.export.isa.codeutils.Json2Pojo;

public class ClassNameGenerator 
{
	private Json2Pojo j2p = null;
	
	public boolean FlagRemoveSuffix = true;
	public String suffix = "_schema";
	
	public boolean FlagHandleTokens = true;
	public char tokenSeparatos[] = new char[] {'_', '-'}; 
	
	public String additionSuffixForDuplication = "_";
	
	public ClassNameGenerator(Json2Pojo j2p)
	{
		this.j2p = j2p;
	}
	
	public String getJavaClassNameForSchema(String schemaName)
	{
		String schemaName0 = schemaName;
		if (FlagRemoveSuffix)
		{
			if (schemaName.endsWith(suffix))
				schemaName0 = schemaName.substring(0, schemaName.length() - suffix.length());
		}
		
		
		//TODO
		return schemaName0;
	}
	
	public String getJavaClassNameForVariable(String varName)
	{
		//TODO
		return null;
	}
	
	String checkForDuplication(String jcName)
	{
		//TODO
		return jcName;
	}
	
}
