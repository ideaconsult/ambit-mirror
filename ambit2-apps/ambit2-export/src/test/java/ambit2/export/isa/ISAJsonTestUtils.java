package ambit2.export.isa;

import java.io.File;

import ambit2.export.isa.json.ISAJsonExportConfig;

import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;




public class ISAJsonTestUtils 
{

	public static void main(String[] args) throws Exception 
	{
		//testObjectToJson();
		testJsonToObject();
	}
	
	public static void testISAJsonExportConfig(String jsonFileName) throws Exception
	{
		ISAJsonExportConfig conf = ISAJsonExportConfig.loadFromJSON(new File(jsonFileName));
		
	}
	
	public static void testObjectToJson() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		TestClass01 testClassObj = new TestClass01();
		
		String jsonString = mapper.writeValueAsString(testClassObj);
		System.out.println(jsonString);
	}
	
	public static void testJsonToObject() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = "{\"p2\" : 4, \"testClass02\" : {}}";
		TestClass01 t1 = mapper.readValue(jsonInString, TestClass01.class);
		
		String jsonOutString = mapper.writeValueAsString(t1);
		System.out.println(jsonInString);
		System.out.println(jsonOutString);
	}
	
	public static void test00()
	{
		JsonNode node;
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectWriter wri;
		
		JsonSchema schema = null;
		//mapper.schemaBasedWriter(schema);	
		//Since 1.9, use ObjectMapper.writer(FilterProvider) instead.
		
		FilterProvider filtProv = null;
		
		//ObjectWriter.withSchema(FormatSchema schema) 
		
		
	}
	
	
	
	
	static class TestClass01{
		public int a[] = {1, 2, 3, 100};
		public int p1 = 2;
		public int p2 = 5;
		public String myStr = "My String";
		public TestClass02 testClass02 = new TestClass02();
	}
	
	static class TestClass02{
		public double d = 12.5;
		public boolean b = true;
	}

}
