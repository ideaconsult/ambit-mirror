package ambit2.export.isa.codeutils;

import java.io.File;
import java.util.logging.Logger;

public class Json2Pojo 
{
	protected static Logger logger = Logger.getLogger("JSON2POJO");
	
	public File sourceDir = null;
	public File targetDir = null;
	public String javaPackage = "default";
	
	public void run() throws Exception
	{
		if (sourceDir == null)
		{	
			logger.info("Source directory is null!");
			return;
		}
		else
			if (!sourceDir.isDirectory())
			{
				logger.info("Source path is not a directory: " + sourceDir);
				return;
			}
		
		if (targetDir == null)
		{	
			logger.info("Target directory is null!");
			return;
		}
		else
			if (!targetDir.isDirectory())
			{
				logger.info("Target path is not a directory: " + sourceDir);
				return;
			}
		
		
		iterateSourceDir();
		
	}
	
	public void iterateSourceDir() throws Exception
	{
		//TODO
	}
	
	
}
