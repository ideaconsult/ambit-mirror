package ambit2.export.isa.codeutils.j2p_helpers;

import ambit2.export.isa.codeutils.Json2Pojo;

public class ClassNameGenerator 
{
	private Json2Pojo j2p = null;
	
	public boolean FlagRemoveSchemaSuffix = true;
	public String additionSuffixForDuplication = "_";
	
	public ClassNameGenerator(Json2Pojo j2p)
	{
		this.j2p = j2p;
	}
	
	public String getJavaClassNameForSchema(String schemaName)
	{
		//TODO
		return null;
	}
	
	public String getJavaClassNameForVariable(String varName)
	{
		//TODO
		return null;
	}
	
}
