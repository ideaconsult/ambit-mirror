package ambit2.export.isa.codeutils;

import java.io.File;

public class TestCodeUtils 
{
	public static void main(String[] args) throws Exception
	{
		//CodeUtils.delete(new File("/temp2/test/test01.txt"));
		//CodeUtils.replaceTextInFile("/temp2/test/mytest1.txt", "test", "mytest");
		
		//CodeUtils.renameJsonSchema(new File("/temp2/test/test1"), new File("/temp2/test/test2"), "process_schema", "process");
		
		//CodeUtils.clearJsonFileSuffix(new File("/temp2/test/test1"), new File("/temp2/test/test2"), "_schema");
		
		
		javaRename ("D:/Projects/Nina/jsonschema2pojo-0.4.15/target-isa1/ambit2/export/isa/v1_0/objects");
		
	}
	
	
	public static void javaRename(String targetDir) throws Exception
	{
		CodeUtils.renameJavaClass(null, new File(targetDir),"ProcessSequence", "Process");
		CodeUtils.renameJavaClass(null, new File(targetDir),"ExecutesProtocol", "Protocol");
		CodeUtils.renameJavaClass(null, new File(targetDir),"Characteristic", "MaterialAttribute");
	}
	

}
