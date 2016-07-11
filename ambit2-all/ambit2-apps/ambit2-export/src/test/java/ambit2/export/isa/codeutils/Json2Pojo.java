package ambit2.export.isa.codeutils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import ambit2.export.isa.codeutils.j2p_helpers.ClassNameGenerator;
import ambit2.export.isa.codeutils.j2p_helpers.JavaClassInfo;

public class Json2Pojo 
{
	protected static Logger logger = Logger.getLogger("JSON2POJO");
	
	//Configuration variables
	public File sourceDir = null;
	public File targetDir = null;
	public String javaPackage = "default";	
	
	public boolean FlagEmptyTargetDirBeforeRun = true;
	public String jsonFileExtension = "json";
	public ClassNameGenerator classNameGenerator = new ClassNameGenerator(this);
	
	
	//work variables:
	Map<String, JavaClassInfo> schemaClasses = new HashMap<String, JavaClassInfo>();
	List<JavaClassInfo> addedClasses = new ArrayList<JavaClassInfo>();
	
	
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
		String schemaName = file.getName().substring(0, file.getName().length() - jsonFileExtension.length());
		
		if (schemaName.equals(""))
			return;  //this should not happen
		
		String jcName = classNameGenerator.getJavaClassNameForSchema(schemaName);		
		JavaClassInfo jci = new  JavaClassInfo();
		jci.javaPackage = javaPackage;
		jci.javaClassName = jcName;
		
	}
	
	void generateTargetFiles() throws Exception
	{
		//TODO
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
	
}
