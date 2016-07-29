package ambit2.export.isa.codeutils.j2p_helpers;

import java.util.ArrayList;
import java.util.List;

import ambit2.export.isa.codeutils.j2p_helpers.VariableInfo.StringFormat;
import ambit2.export.isa.codeutils.j2p_helpers.VariableInfo.Type;

public class JavaClassInfo 
{
	public String schemaName = "";
	public String javaPackage = "default";
	public String javaClassName = "";
	
	public String propertyName = null;  
	public String propertySchemaName = null;
	
	//info from the json schema fields
	public String field_type = null;
	public String field_title = null;
	public String field_name = null;
	public String field_description = null;
	
	public List<VariableInfo> variables = new ArrayList<VariableInfo>();
	
	public List<String> getNeededImports(JavaSourceConfig sourceConfig)
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
		
		//Check for variable of type URI;
		boolean FlagURI = false;
		if (sourceConfig.FlagHandleURIString)
		{	
			for (VariableInfo vi: variables)
				if (vi.type == Type.STRING)
				{	
					if (vi.stringFormat == StringFormat.URI_FORMAT)
					{
						FlagURI = true;
						break;
					}
				}
		}
		
		//Check for variable of type Date;
		boolean FlagDate = false;
		if (sourceConfig.FlagHandleDateString)
		{	
			for (VariableInfo vi: variables)
				if (vi.type == Type.STRING)
				{	
					if (vi.stringFormat == StringFormat.DATE_TIME_FORMAT)
					{
						FlagDate = true;
						break;
					}
				}
		}	
		
		if (FlagArray)
		{
			imports.add("import java.util.ArrayList;");
			imports.add("import java.util.List;");
		}
		
		if (FlagURI)
		{
			imports.add("import java.net.URI;");
		}
		
		if (FlagDate)
		{
			imports.add("import java.util.Date;");
		}
		
		if (sourceConfig.FlagJsonAnnotation)
		{
			imports.add("import javax.annotation.Generated;");
			imports.add("import com.fasterxml.jackson.annotation.JsonInclude;");
			imports.add("import com.fasterxml.jackson.annotation.JsonProperty;");
			imports.add("import com.fasterxml.jackson.annotation.JsonPropertyOrder;");
		}
		
		return imports;
	}
	
	public List<String> getCommentLinesFromJsonFields(JavaSourceConfig sourceConfig)
	{
		List<String> lines = new ArrayList<String>();
		
		if (sourceConfig.FlagUseSchemaTitleAsComment)
			if (field_title != null)
				lines.add(field_title);
			
		if (sourceConfig.FlagUseSchemaNameAsComment)
			if (field_name != null)
				lines.add(field_name);	
		
		if (sourceConfig.FlagUseSchemaDescriptionAsComment)
			if (field_description != null)
				lines.add(field_description);	
		
		return lines;
	}
	
}
