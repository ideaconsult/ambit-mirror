package ambit2.export.isa;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.schema.JsonSchema;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.test.ProtocolApplicationTestFactory;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.export.isa.base.ISALocation;
import ambit2.export.isa.json.ISAJsonExportConfig;
import ambit2.export.isa.v1_0.ISAJsonExporter1_0;
import ambit2.export.isa.v1_0.ISAJsonMapper1_0;
import ambit2.export.isa.v1_0.objects.Investigation;





public class ISAJsonTestUtils 
{

	public static void main(String[] args) throws Exception 
	{
		//testObjectToJson();
		//testJsonToObject();
		
		/*
		testISALocation("assay.0.0.process[1].element");
		testISALocation("assay.0.0.element");
		testISALocation("study.4.process[2].element5");
		testISALocation("study.4.element5.subel");
		testISALocation("investigation.element");
		*/
		
		//testISAJsonMapper1_0(new String[] {"investigation.filename", "investigation.title"},
		//				new String[] {"myTest.txt", "MyTitle"}	);
		
		List<SubstanceRecord> sr = new ArrayList<SubstanceRecord>();
		sr.add(getTestSubstanceRecord());
		testJsonExport(sr.iterator(), null, null, getTestSubstanceEndpointsBundle());
		
	}
	
	public static void testJsonExport(Iterator<SubstanceRecord> records, 
			File outputDir, 
			File exportConfig,
			SubstanceEndpointsBundle endpointBundle) throws Exception
	{
		ISAJsonExporter1_0 exporter = new ISAJsonExporter1_0(outputDir, exportConfig);
		exporter.export(endpointBundle,records);
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
	
	public static void testISAJsonMapper1_0(String locations[], String data[]) throws Exception
	{
		Investigation investigation = new Investigation();
		ISAJsonMapper1_0 isaJM = new ISAJsonMapper1_0();
		isaJM.setTargetDataObject(investigation);
		
		for (int i = 0; i < locations.length; i++)
		{
			ISALocation loc = ISALocation.parseString(locations[i]);
			System.out.println("Parsing location " + locations[i]);
			System.out.println("Putting data " + data[i]);
			
			isaJM.putString(data[i], loc);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(investigation);
		System.out.println(jsonString);
	}
	
	protected static SubstanceEndpointsBundle getTestSubstanceEndpointsBundle()
	{
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
		
		bundle.setDescription("Test Bundle description");
		bundle.setTitle("Test Bundle title");
		//TODO
		
		return bundle;
	}
	
	protected static SubstanceRecord getTestSubstanceRecord()
	{
		SubstanceRecord record = new SubstanceRecord();
		record.setContent("Generated test content");
		record.setOwnerUUID("owner-uuid-1234567890");
		record.setSubstanceUUID("record-uuid-1234567890");
		
		
		//Add measurements
		ProtocolApplication pa = ProtocolApplicationTestFactory.initpa();
		pa.setSubstanceUUID(record.getOwnerUUID());
		record.addMeasurement(pa);
		
		EffectRecord eff = (EffectRecord)pa.getEffects().get(0);
		IParams par = (IParams)eff.getConditions();
		par.put("condition1", "value1");
		
		addCompositionData(record);
		
		
		//record.addMeasurement(ProtocolApplicationTestFactory.initpc());
		//record.addMeasurement(ProtocolApplicationTestFactory.initbiodeg());
		
		return record;
	}
	
	protected static void addCompositionData(SubstanceRecord record)
	{
		List<CompositionRelation> list = new ArrayList<CompositionRelation>();
		
		
		IStructureRecord s1 = new StructureRecord();
		Proportion p1 = new Proportion();
		CompositionRelation rel1 = new CompositionRelation(record,s1, STRUCTURE_RELATION.HAS_CONSTITUENT, p1);
		s1.setFormula("C5H10");
		s1.setSmiles("CCC=C");
		s1.setInchi("DUMMYINCHI0001");
		s1.setInchiKey("DUMMYINCHIKEY0001");
		p1.setReal_lowervalue(0.5);
		list.add(rel1);
		
		IStructureRecord s2 = new StructureRecord();
		Proportion p2 = new Proportion();
		CompositionRelation rel2 = new CompositionRelation(record,s2, STRUCTURE_RELATION.HAS_CONSTITUENT, p2);
		s2.setFormula("C6H12");
		s2.setSmiles("CCCC=C");
		s2.setInchi("DUMMYINCHI0002");
		s2.setInchiKey("DUMMYINCHIKEY0002");
		p2.setReal_lowervalue(0.5);
		list.add(rel2);
		
		record.setRelatedStructures(list);
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
