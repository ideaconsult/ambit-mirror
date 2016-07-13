package ambit2.export.isa.codeutils.j2p_helpers;

import java.util.ArrayList;
import java.util.List;

import ambit2.export.isa.codeutils.j2p_helpers.VariableInfo.Type;

public class JavaClassInfo 
{
	public String schemaName = "";
	public String javaPackage = "default";
	public String javaClassName = "";
	public List<VariableInfo> variables = new ArrayList<VariableInfo>();
	
	public List<String> getNeededImports()
	{
		List<String> imports = new ArrayList<String>();
		
		//Check for variable of type ARRAY;
		boolean FlagArray = false;
		for (VariableInfo vi: variables)
			if (vi.type == Type.ARRAY)
			{
				FlagArray = true;
				break;
			}
		
		if (FlagArray)
		{
			imports.add("import java.util.ArrayList;");
			imports.add("import java.util.List;");
		}
		
		return imports;
	}
	
}
