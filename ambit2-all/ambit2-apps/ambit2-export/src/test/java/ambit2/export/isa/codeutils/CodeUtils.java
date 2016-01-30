package ambit2.export.isa.codeutils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class CodeUtils 
{
	public static void renameJsonSchema(File sourceDir, File targetDir, String oldName, String newName) throws Exception
	{			
		System.out.println("renaming schema: " + oldName + " --> " + newName);
		System.out.println("-------------------------");
		
		handleSourceAndTagetDirs(sourceDir, targetDir);
		
		String oldFileName = oldName + ".json";
		String newFileName = newName + ".json";
		
		for (File file : targetDir.listFiles()) 
		{
			if (file.isDirectory())
				continue; //currently do nothing 
			
			if (file.getAbsolutePath().endsWith(oldFileName))
			{
				String oldPath = file.getAbsolutePath();
				String path = oldPath.replaceAll(oldFileName, newFileName);
				file.renameTo(new File(path));
				System.out.println("renaming " + oldPath + " to " + path);
				
				System.out.println("replacing in " + file.getAbsolutePath());
				replaceTextInFile(path, oldFileName, newFileName);  //in some case this is needed as well
			}
			else
			{
				System.out.println("replacing in " + file.getAbsolutePath());
				replaceTextInFile(file.getAbsolutePath(), oldFileName, newFileName);
			}
        }
	}
	
	
	public static void clearJsonFileSuffix(File sourceDir, File targetDir, String suffix) throws Exception
	{
		//Initial handling of source and target dirs
		handleSourceAndTagetDirs(sourceDir, targetDir);
				
		List<String> oldSchemas = new ArrayList<String>();
		List<String> newSchemas = new ArrayList<String>();
		
		for (File file : targetDir.listFiles()) 
		{
			if (!file.isFile())
				continue;
			
			String oldPath = file.getName();
			if (!oldPath.endsWith(".json"))
				continue; //Only json files are processed
			
			String oldPath1 = oldPath.substring(0, oldPath.length()-5);
			String path = null;
			if (oldPath1.endsWith(suffix))
				path = oldPath1.substring(0, oldPath1.length()-suffix.length());
			
			if (path != null)
			{
				oldSchemas.add(oldPath1);
				newSchemas.add(path);
			}	
		}
		
		for (int i = 0; i < newSchemas.size(); i++)
		{
			System.out.println();
			renameJsonSchema(null, targetDir, oldSchemas.get(i), newSchemas.get(i));
		}
	}
	
	
	public static void renameJavaClass(File sourceDir, File targetDir, String oldName, String newName) throws Exception
	{
		System.out.println("renaming java class: " + oldName + " --> " + newName);
		System.out.println("-------------------------");
		
		handleSourceAndTagetDirs(sourceDir, targetDir);
		
		String oldFileName = oldName + ".java";
		String newFileName = newName + ".java";
		
		for (File file : targetDir.listFiles()) 
		{
			if (file.isDirectory())
				continue; //currently do nothing 
			
			if (file.getAbsolutePath().endsWith(oldFileName))
			{
				String oldPath = file.getAbsolutePath();
				String path = oldPath.replaceAll(oldFileName, newFileName);
				File ranamedFile = new File(path);
				file.renameTo(ranamedFile);
				System.out.println("renaming " + oldPath + " to " + path);
				System.out.println("replacing in " + ranamedFile.getAbsolutePath());
				
				//replaceTextInFile(ranamedFile.getAbsolutePath(), oldName, newName);
				replaceTextInFile(ranamedFile.getAbsolutePath(), oldName+" ", newName + " ");
				replaceTextInFile(ranamedFile.getAbsolutePath(), oldName+">", newName + ">");
				//replaceTextInFile(ranamedFile.getAbsolutePath(), oldName+".", newName + ".");
			}
			else
			{
				System.out.println("replacing in " + file.getAbsolutePath());
				//replaceTextInFile(file.getAbsolutePath(), oldName, newName);
				replaceTextInFile(file.getAbsolutePath(), oldName+" ", newName + " ");
				replaceTextInFile(file.getAbsolutePath(), oldName+">", newName + ">");
				//replaceTextInFile(file.getAbsolutePath(), oldName+".", newName + ".");
			}
        }
	}
	
	public static void handleSourceAndTagetDirs(File sourceDir, File targetDir) throws Exception
	{
		if (!targetDir.isDirectory())
			throw new Exception("Target is not a directory: " + targetDir.getName());
				
		if (sourceDir != null)
		{
			if (!sourceDir.isDirectory())
				throw new Exception("Source is not a directory: " + sourceDir.getName());
			
			if (!sourceDir.exists())
				throw new Exception("Source directory does not exists: " + sourceDir.getName());
			
			if (targetDir.getAbsolutePath().equals(sourceDir.getAbsolutePath()))
			{	
				//do nothing target and source are the same
			}
			else
			{	
				if (!targetDir.exists())
				{	
					//Does nothing
					//It will be created on copying
				}
				else
				{	
					//Delete all files from the target
					for (File file : targetDir.listFiles())
						delete(file);
				}
					
				System.out.println("copying directory " + sourceDir + " to " + targetDir);
				FileUtils.copyDirectory(sourceDir, targetDir);
			}
		}
	}
	
	public static void replaceTextInFile(String fileName, String oldText, String newText) throws Exception
	{
		Path path = Paths.get(fileName);
		Charset charset = StandardCharsets.UTF_8;
				
		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll(oldText, newText);
		Files.write(path, content.getBytes(charset));
	}
	
	public static void delete(File f) 
	{
		System.out.println("deleting file: " + f.getAbsolutePath());
		if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                delete(child);
            }
        }
        f.delete();
    }
}
