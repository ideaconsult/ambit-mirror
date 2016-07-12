package ambit2.export.isa.codeutils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import ambit2.export.isa.codeutils.j2p_helpers.ClassNameGenerator;
import ambit2.export.isa.codeutils.j2p_helpers.JavaClassInfo;
import ambit2.export.isa.codeutils.j2p_helpers.VariableInfo;

public class Json2Pojo 
{
	protected static Logger logger = Logger.getLogger("JSON2POJO");
	
	//Configuration variables
	public File sourceDir = null;
	public File targetDir = null;
	public String javaPackage = "default";	
	
	public boolean FlagEmptyTargetDirBeforeRun = true;
	public boolean FlagExceptionOnIncorrectReference = true;
	public boolean FlagResultOnlyToLog = false;
	
	public String jsonFileExtension = "json";
	public String endLine = "\n";
	public ClassNameGenerator classNameGenerator = new ClassNameGenerator(this);
	
	//Preliminary list of class names which are to be added for
	//variables of type object
	public Map<String, String> classNameSuggestions = new HashMap<String, String>();
	
	
	//work variables:
	Map<String, JavaClassInfo> schemaClasses = new HashMap<String, JavaClassInfo>();
	List<JavaClassInfo> addedClasses = new ArrayList<JavaClassInfo>();
	String jsonError = null;
	
	
	public void run() throws Exception
	{
		if (sourceDir == null)
			throw new Exception("Source directory is null!");
		
		if (!sourceDir.exists())
			throw new Exception("Source directory does not exists: " + sourceDir.getName());
	
		if (!sourceDir.isDirectory())
			throw new Exception("Source is not a directory: " + sourceDir.getName());
		
		if (targetDir == null)
			throw new Exception("Target directory is null!");
		
		if (!targetDir.exists())
			throw new Exception("Target directory does not exists: " + targetDir.getName());
	
		if (!targetDir.isDirectory())
			throw new Exception("Target is not a directory: " + targetDir.getName());
		
		
		if (FlagEmptyTargetDirBeforeRun)
		{
			for (File file : targetDir.listFiles())
				delete(file);
		}
		
		iterateSourceDir();
		
		generateTargetFiles();
	}
	
	void iterateSourceDir() throws Exception
	{
		for (File file : sourceDir.listFiles()) 
		{	
			if (file.isFile())
			{
				if (isJsonFile(file))
					handleJsonSchemaFile(file);
				continue;
			}
			
			//TODO handle sub-directories if needed
		}
	}
	
	
	void handleJsonSchemaFile(File file) throws Exception
	{
		System.out.println("Handling json schema: " + file.getName());
		String schemaName = file.getName().substring(0, (file.getName().length() - jsonFileExtension.length()-1));
		
		if (schemaName.equals(""))
			return;  //this should not happen
		
		if (schemaClasses.containsKey(schemaName))
			return; //This schema has already been processed
		
		String jcName = classNameGenerator.getJavaClassNameForSchema(schemaName);		
		JavaClassInfo jci = new  JavaClassInfo();
		schemaClasses.put(schemaName, jci);
		jci.schemaName = schemaName;
		jci.javaPackage = javaPackage;
		jci.javaClassName = jcName;
		
		readJsonSchema(file.getAbsolutePath(), jci);
	}
	
	void readJsonSchema (String jsonFileName, JavaClassInfo jci) throws Exception
	{
		//Function is recursive: 
		//on some occasions readProperty() may call readJsonSchema()
		
		FileInputStream fin = new FileInputStream(jsonFileName); 
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		
		try {
			rootNode = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {
			try {fin.close();} catch (Exception x) {}	
		}
		
		JsonNode node = rootNode.path("type");
		if (node.isMissingNode())
		{	
			logger.info("Field \"type\" is missing for schema: " + jsonFileName);
		}
		else
		{
			//handle schema type if needed
		}
		
		//Iterate schema properties
		JsonNode propNode = rootNode.path("properties");
		if (propNode.isMissingNode())
			throw new Exception("Field \"properties\" is missing for schema: " + jsonFileName);
		
		StringBuffer errors = new StringBuffer();
		
		Iterator<String> propFields = propNode.getFieldNames();
		while (propFields.hasNext())
		{
			String fieldName = propFields.next();
			JsonNode fieldNode = propNode.get(fieldName);
			String err = readProperty(jci, fieldName, fieldNode);
			if (err != null)
				errors.append(err + endLine);
		}
		
		if  (!errors.toString().isEmpty())
			throw new Exception("Property errors in schema: " + 
					jci.schemaName + endLine + errors.toString());
	}
	
	String readProperty(JavaClassInfo jci, String fieldName, JsonNode fieldNode)
	{	
		VariableInfo var = new VariableInfo();
		
		//handle name
		if (fieldName.startsWith("@"))
			var.name =  fieldName.substring(1);
		else
			var.name = fieldName;
		
		System.out.println("  " + var.name);
		
		//handle variable type
		JsonNode typeNode = fieldNode.path("type");
		if (typeNode.isMissingNode())
		{
			JsonNode refNode = fieldNode.path("$ref");
			if (refNode.isMissingNode())
			{
				if (FlagExceptionOnIncorrectReference)
					return "Missing reference for field " + var.name;
				else
					logger.info("Missing reference for field " + var.name + 
							" in schema " + jci.schemaName);
			}
			else
			{
				String ref = extractStringKeyword(fieldNode, "$ref", true);
				if (ref == null)
				{
					if (FlagExceptionOnIncorrectReference)
						return "Incorrect reference for field " + var.name + 
								" : " + jsonError;
					else
						logger.info("Incorrect reference for field " + var.name + 
								" in schema " + jci.schemaName + " : " + jsonError);
				}
				else
				{	
					JavaClassInfo newClass = handleReference(ref);
					if (newClass == null)
					{
						if (FlagExceptionOnIncorrectReference)
							return "Incorrect reference \""+ ref + "\" for field " + var.name;
						else
							logger.info("Incorrect reference \""+ ref + "\" for field " + var.name + 
									" in schema " + jci.schemaName);
					}
					else
					{
						//Register new added class
						//TODO (check for whether it is already registered class
					}
				}
			}
		}
		else
		{	
			String fieldType = extractStringKeyword(fieldNode, "type", true);
			if (fieldType == null)
				return ("Incorrect \"type\" for property \"" + fieldName +"\"");
			if (fieldType.isEmpty())
				return ("Empty \"type\" for property \"" + fieldName +"\"");
			
			var.type = VariableInfo.getTypeFromString(fieldType);
			if (var.type == null)
				return ("Incorrect \"type\":\"" + fieldType +  "\" for property \"" + fieldName +"\"");
		}
		
			
		//TODO handle variables of types: array, object, string
		
		
		jci.variables.add(var);
		return null;
	}
	
	JavaClassInfo handleReference(String ref)
	{
		//TODO
		return null;
	}
	
	JavaClassInfo getClassForObjectVarible(JsonNode propNode)
	{
		//TODO
		return null;
	}
	
	void generateTargetFiles() throws Exception
	{	
		if (FlagResultOnlyToLog)
		{
			System.out.println();
			Set<String> keys = schemaClasses.keySet();
			for (String key: keys)
			{
				System.out.println(key);
				System.out.println("--------------------");
				System.out.println(generateJavaSource(schemaClasses.get(key)));
				System.out.println();
			}
			
			return;
		}
		
		
	}
	
	
	String generateJavaSource(JavaClassInfo jci)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("package " + jci.javaPackage + endLine);
		sb.append(endLine);
		sb.append("public class " + jci.javaClassName + endLine);
		sb.append("{" + endLine);
		
		
		sb.append("}" + endLine);
		
		return sb.toString();
	}
	
	
	boolean isJsonFile(File file)
	{
		String name = file.getName();
		int dot_pos = name.lastIndexOf(".");
		if (dot_pos != -1)
			if (dot_pos < name.length())
			{	
				String fileExt = name.substring(dot_pos+1);
				if (fileExt.equalsIgnoreCase(jsonFileExtension))
					return true;
			}
		return false;
	}
	
	void delete(File f) 
	{	
		//recursive deletion of file/directory
		if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                delete(child);
            }
        }
        f.delete();
    }
	
	
	
	
	public String extractStringKeyword(JsonNode node, String keyword, boolean isRequired)
	{
		jsonError = null;
		JsonNode keyNode = node.path(keyword);
		if(keyNode.isMissingNode())
		{
			if(isRequired)
			{	
				jsonError = "Keyword " + keyword + " is missing!";
				return null;
			}
			return "";
		}
		
		if (keyNode.isTextual())
		{	
			return keyNode.asText();
		}
		else
		{	
			jsonError = "Keyword " + keyword + " is not of type text!";
			return null;
		}			
	}
	
}
