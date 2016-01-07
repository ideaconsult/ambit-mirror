package ambit2.export.isa.v1_0;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.export.isa.IISAExport;
import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;
import ambit2.export.isa.json.ISAJsonExportConfig;
import ambit2.export.isa.json.ISAJsonExporter;
import ambit2.export.isa.v1_0.objects.Investigation;
import ambit2.export.isa.v1_0.objects.Publication;

public class ISAJsonExporter1_0 implements IISAExport
{
	protected final static Logger logger = Logger.getLogger(ISAJsonExporter.class.getName());
	
	//Basic io variables
	Iterator<SubstanceRecord> records = null;
	SubstanceEndpointsBundle endpointBundle = null;
	File outputDir = null;
	File exportConfig = null;
	File xmlISAConfig =  null;

	//work variables
	ISAJsonExportConfig cfg = null;
	
	//ISA data
	Investigation investigation = null;
	ISAJsonMapper1_0 isaMapper = null;

	public ISAJsonExporter1_0()
	{	
	}

	public ISAJsonExporter1_0(
			Iterator<SubstanceRecord> records, 
			File outputDir, 
			File exportConfig,
			SubstanceEndpointsBundle endpointBundle
			)
	{
		setRecords(records);
		setOutputDir(outputDir);
		setExportJsonConfig(exportConfig);
		setEndpointBundle(endpointBundle);
	}
	
	public File getExportJsonConfig() {
		return exportConfig;
	}

	public void setExportJsonConfig(File exportConfig) {
		this.exportConfig = exportConfig;
	}

	public File getXmlISAConfig() {
		return xmlISAConfig;
	}

	public void setXmlISAConfig(File xmlISAConfig) {
		this.xmlISAConfig = xmlISAConfig;
	}

	public File getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public Iterator<SubstanceRecord> getRecords() {
		return records;
	}

	public void setRecords(Iterator<SubstanceRecord> records) {
		this.records = records;
	}

	public SubstanceEndpointsBundle getEndpointBundle() {
		return endpointBundle;
	}

	public void setEndpointBundle(SubstanceEndpointsBundle endpointBundle) {
		this.endpointBundle = endpointBundle;
	}

	@Override
	public ISAFormat getISAFormat() {
		return ISAFormat.JSON;
	}

	@Override
	public ISAVersion getISAVersion() {
		return ISAVersion.Ver1_0;
	}

	public void export() throws Exception
	{
		if (records == null)
			throw new Exception("Null input records iterator!");

		//if (outputDir == null)
		//	throw new Exception("Null output directory or file!");

		if (exportConfig == null)
		{	
			cfg = ISAJsonExportConfig.getDefaultConfig();
		}
		else
			cfg = ISAJsonExportConfig.loadFromJSON(exportConfig);

		if (!records.hasNext())
			throw new Exception("No records to iterate");

		investigation = new Investigation();
		
		handleBundle();
		
		//Some temp code
		investigation.publications.add(new Publication());
		investigation.publications.add(new Publication());
		
		while (records.hasNext())
		{
			SubstanceRecord rec = records.next();
			handleRecord(rec);
		}
		
		saveDataToOutputDir();
	}
	
	void handleBundle()
	{
		if (endpointBundle ==null)
			return;
		
		investigation.title = endpointBundle.getTitle();
		investigation.description = endpointBundle.getDescription();
		
	}
	
	void handleRecord(SubstanceRecord rec) throws Exception
	{
		if (rec == null)
			return;
		
		//rec.getContent()
		
		ILiteratureEntry litEntry = rec.getReference();
	}
	
	void addLiteratureEntry(ILiteratureEntry litEntry)
	{
		//TODO
	}
	
	
	void saveDataToOutputDir() throws Exception
	{	
		if (cfg.singleJSONFile)
		{
			//ObjectMapper mapper = new ObjectMapper();
			//String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(investigation);
			
			//TODO
		}
		else
		{
			//TODO
		}
		
	}
	
	public String getResultAsJson() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(investigation);
		return jsonString;
	}
	
	
	
	
}
