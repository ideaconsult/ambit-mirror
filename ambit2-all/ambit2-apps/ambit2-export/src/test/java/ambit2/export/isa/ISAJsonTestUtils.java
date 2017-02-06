package ambit2.export.isa;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.ser.FilterProvider;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.test.TestSubstanceFactory;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.export.isa.base.ISALocation;
import ambit2.export.isa.json.ISAJsonExportConfig;
import ambit2.export.isa.v1_0.ISAJsonExporter1_0;
import ambit2.export.isa.v1_0.ISAJsonMapper1_0;
import ambit2.export.isa.v1_0.objects.Investigation;

public class ISAJsonTestUtils {

	public static void main(String[] args) throws Exception {
		// testObjectToJson();
		// testJsonToObject();

		/*
		 * testISALocation("assay.0.0.process[1].element");
		 * testISALocation("assay.0.0.element");
		 * testISALocation("study.4.process[2].element5");
		 * testISALocation("study.4.element5.subel");
		 * testISALocation("investigation.element");
		 */

		// testISAJsonMapper1_0(new String[] {"investigation.filename",
		// "investigation.title"},
		// new String[] {"myTest.txt", "MyTitle"} );

		List<SubstanceRecord> sr = new ArrayList<SubstanceRecord>();
		sr.add(TestSubstanceFactory.getTestSubstanceRecord());
		testJsonExport(sr.iterator(), new File("/Volumes/Data/test-isa.json"), (File)null,
				TestSubstanceFactory.getTestSubstanceEndpointsBundle());

	}

	public static void testJsonExport(Iterator<SubstanceRecord> records,
			File outputDir, File exportConfig,
			SubstanceEndpointsBundle endpointBundle) throws Exception {
		
		ISAJsonExportConfig cfg = ISAJsonExportConfig.getDefaultConfig();
		cfg.FlagSaveCompositionAsStudy = false;
		ISAJsonExporter1_0 exporter = new ISAJsonExporter1_0(outputDir, cfg);
		
		exporter.setOutputDir(outputDir);
		exporter.setExportJsonConfig(exportConfig);
		
		exporter.export(endpointBundle, records);
		System.out.println(exporter.getResultAsJson());
	}
	
	
	public static void testJsonExport(Iterator<SubstanceRecord> records,
			File outputDir, ISAJsonExportConfig cfg, File externalDataFile,
			SubstanceEndpointsBundle endpointBundle) throws Exception {
		
		ISAJsonExporter1_0 exporter = new ISAJsonExporter1_0(outputDir, cfg);
		exporter.setOutputDir(outputDir);
		exporter.setExternalDataFile(externalDataFile);
		
		exporter.export(endpointBundle, records);
		System.out.println(exporter.getResultAsJson());
	}
	

	public static void testISAJsonExportConfig(String jsonFileName)
			throws Exception {
		ISAJsonExportConfig conf = ISAJsonExportConfig.loadFromJSON(new File(
				jsonFileName));

	}

	public static void testObjectToJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		TestClass01 testClassObj = new TestClass01();

		String jsonString = mapper.writeValueAsString(testClassObj);
		System.out.println(jsonString);
	}

	public static void testJsonToObject() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = "{\"p2\" : 4, \"testClass02\" : {}}";
		TestClass01 t1 = mapper.readValue(jsonInString, TestClass01.class);

		String jsonOutString = mapper.writeValueAsString(t1);
		System.out.println(jsonInString);
		System.out.println(jsonOutString);
	}

	public static void testISALocation(String isaLoc) throws Exception {
		ISALocation loc = ISALocation.parseString(isaLoc);
		System.out.println(isaLoc);
		System.out.println(loc.toString());
		System.out.println();
	}

	public static void testISAJsonMapper1_0(String locations[], String data[])
			throws Exception {
		Investigation investigation = new Investigation();
		ISAJsonMapper1_0 isaJM = new ISAJsonMapper1_0();
		isaJM.setTargetDataObject(investigation);

		for (int i = 0; i < locations.length; i++) {
			ISALocation loc = ISALocation.parseString(locations[i]);
			System.out.println("Parsing location " + locations[i]);
			System.out.println("Putting data " + data[i]);

			isaJM.putString(data[i], loc);
		}

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(investigation);
		System.out.println(jsonString);
	}

	
	public static void test00() {
		JsonNode node;
		ObjectMapper mapper = new ObjectMapper();

		ObjectWriter wri;

		JsonSchema schema = null;
		// mapper.schemaBasedWriter(schema);
		// Since 1.9, use ObjectMapper.writer(FilterProvider) instead.

		FilterProvider filtProv = null;

		// ObjectWriter.withSchema(FormatSchema schema)

	}
	
	public static void addTestEffectRecords(ProtocolApplication pa)
	{
		EffectRecord eff0 = new EffectRecord();
		eff0.setLoValue(345);
		eff0.setLoQualifier("<=");
		eff0.setUnit("nm");
		pa.addEffect(eff0);
		
		eff0 = new EffectRecord();
		eff0.setUpValue(123.4);
		eff0.setUpQualifier(">");
		eff0.setErrorValue(11.1);
		eff0.setErrQualifier("~");
		eff0.setUnit("nm");
		pa.addEffect(eff0);
		
		eff0 = new EffectRecord();
		eff0.setUpValue(333);
		eff0.setUpQualifier("mean");
		eff0.setErrorValue(11.1);
		eff0.setErrQualifier("sd");
		eff0.setUnit("nm");
		pa.addEffect(eff0);
		
		eff0 = new EffectRecord();
		eff0.setTextValue("NA");
		pa.addEffect(eff0);
	}

	static class TestClass01 {
		public int a[] = { 1, 2, 3, 100 };
		public int p1 = 2;
		public int p2 = 5;
		public String myStr = "My String";
		public TestClass02 testClass02 = new TestClass02();
	}

	static class TestClass02 {
		public double d = 12.5;
		public boolean b = true;
	}

}
