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
		
		//------------------------------------------------------
		//Automatic generation if java classes from json schemas
		//------------------------------------------------------
		//Step 1
		//CodeUtils.clearJsonFileSuffix(new File("/git-repositories/isa-api/isatools/schemas/isa_model_version_1_0_schemas/core"), 
		//		new File("/temp2/test/test2"), "_schema");
		
		//Step 2 pojo
		
		//Step 3
		//javaRename ("D:/Projects/Nina/jsonschema2pojo-0.4.15/target-isa1/ambit2/export/isa/v1_0/objects");
		//------------------------------------------------------
		
		
		testJsonToPojo();
		
	}
	
	
	public static void javaRename(String targetDir) throws Exception
	{	
		CodeUtils.renameJavaClass(null, new File(targetDir),"ProcessSequence", "Process");
		CodeUtils.renameJavaClass(null, new File(targetDir),"ExecutesProtocol", "Protocol");
		
		CodeUtils.renameJavaClass(null, new File(targetDir),"CharacteristicCategory", "MaterialAttribute");  
		CodeUtils.renameJavaClass(null, new File(targetDir),"Characteristic", "MaterialAttributeValue");
		 
		CodeUtils.renameJavaClass(null, new File(targetDir),"DataFile", "Data");
		CodeUtils.renameJavaClass(null, new File(targetDir),"DerivesFrom", "Source");
		CodeUtils.renameJavaClass(null, new File(targetDir),"MeasurementType", "OntologyAnnotation");
		
		CodeUtils.renameJavaClass(null, new File(targetDir),"ParameterValue", "ProcessParameterValue");
		CodeUtils.renameJavaClass(null, new File(targetDir),"Parameter", "ProtocolParameter");
		
		
		
		
		/*
		 *  (previous rename version)
		 * 
		CodeUtils.renameJavaClass(null, new File(targetDir),"ProcessSequence", "Process");
		CodeUtils.renameJavaClass(null, new File(targetDir),"ExecutesProtocol", "Protocol");
		
		CodeUtils.renameJavaClass(null, new File(targetDir),"CharacteristicCategory", "MaterialAttribute");  
		CodeUtils.renameJavaClass(null, new File(targetDir),"Characteristic", "MaterialAttributeValue");
		 
		CodeUtils.renameJavaClass(null, new File(targetDir),"DataFile", "Data");
		CodeUtils.renameJavaClass(null, new File(targetDir),"DerivesFrom", "Source");
		CodeUtils.renameJavaClass(null, new File(targetDir),"MeasurementType", "OntologyAnnotation");
		
		CodeUtils.renameJavaClass(null, new File(targetDir),"ParameterValue", "ProcessParameterValue");
		CodeUtils.renameJavaClass(null, new File(targetDir),"Parameter", "ProtocolParameter");
		 */
	}
	
	public static void testJsonToPojo() throws Exception 
	{
		Json2Pojo j2p = new Json2Pojo();
		j2p.sourceDir = new File("tttt");
		
		j2p.run();
		
	}
	

}
