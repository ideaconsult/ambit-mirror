package ambit2.export.isa;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.export.isa.base.ISALocation;
import ambit2.export.isa.json.ISAJsonExportConfig;
import ambit2.export.isa.v1_0.ISAJsonExporter1_0;

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
		//testJsonToObject();
		
		testISALocation("assay.0.0.process[1].element");
		testISALocation("assay.0.0.element");
		testISALocation("study.4.process[2].element5");
		testISALocation("study.4.element5.subel");
		testISALocation("investigation.element");
		
		List<SubstanceRecord> sr = new ArrayList<SubstanceRecord>();
		sr.add(null);
		//testJsonExport(sr.iterator(), null, null, null);
		
	}
	
	public static void testJsonExport(Iterator<SubstanceRecord> records, 
			File outputDir, 
			File exportConfig,
			SubstanceEndpointsBundle endpointBundle) throws Exception
	{
		ISAJsonExporter1_0 exporter = new ISAJsonExporter1_0(records, outputDir, exportConfig,  endpointBundle);
		exporter.export();
		System.out.println(exporter.getResultAsJson());
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
	
	public static void testISALocation(String isaLoc) throws Exception
	{
		ISALocation loc = ISALocation.parseString(isaLoc);
		System.out.println(isaLoc);
		System.out.println(loc.toString());
		System.out.println();
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
