package ambit2.export.isa.json;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.export.isa.IISAExport;
import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;



public class ISAJsonExporter implements IISAExport
{
	protected final static Logger logger = Logger.getLogger(ISAJsonExporter.class.getName());
	protected ISAVersion isaVersion = ISAVersion.Ver2_0; //default
	
	//Basic io variables
	Iterator<SubstanceRecord> records = null;
	SubstanceEndpointsBundle endpointBundle = null;
	File outputDir = null;
	File exportConfig = null;
	File xmlISAConfig =  null;
	
	//work variables
	ISAJsonExportConfig cfg = null;
	
	public ISAJsonExporter()
	{	
	}
	
	public ISAJsonExporter(	Iterator<SubstanceRecord> records, 
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
	
	public void export() throws Exception
	{
		if (records == null)
			throw new Exception("Null input records iterator!");
		
		if (outputDir == null)
			throw new Exception("Null output directory!");
		
		if (exportConfig == null)
			throw new Exception("Null export config file!");
		
		cfg = ISAJsonExportConfig.loadFromJSON(exportConfig);
		
		if (!records.hasNext())
			throw new Exception("No records to iterate");
		
		//TODO
		
		while (records.hasNext())
		{
			SubstanceRecord rec = records.next();
			handleRecord(rec);
		}
	}
	
	void handleRecord(SubstanceRecord rec)
	{
		//TODO
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
		return isaVersion;
	}	
}
