package ambit2.export.isa.codeutils;

import java.io.File;
import java.util.logging.Logger;

public class Json2Pojo 
{
	protected static Logger logger = Logger.getLogger("JSON2POJO");
	
	public File sourceDir = null;
	public File targetDir = null;
	public String javaPackage = "default";
	
	
	public boolean FlagEmptyTargetDirBeforeRun = true;
	
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
		
		
		iterateSourceDir();
	}
	
	public void iterateSourceDir() throws Exception
	{
		for (File file : sourceDir.listFiles()) 
		{
			
		}
	}
	
	
	public static void delete(File f) 
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
