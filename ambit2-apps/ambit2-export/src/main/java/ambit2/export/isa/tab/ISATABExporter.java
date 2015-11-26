package ambit2.export.isa.tab;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.export.isa.IISAExport;
import ambit2.export.isa.ISAExportConfig;
import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;
import ambit2.export.isa.base.Investigation;

public class ISATABExporter implements IISAExport
{
	protected final static Logger logger = Logger.getLogger(ISATABExporter.class.getName());
	
	//basic io
	Iterator<SubstanceRecord> records = null;
	SubstanceEndpointsBundle endpointBundle = null;
	File outputDir = null;
	File exportJsonConfig = null;
	File xmlISAConfig =  null;
	
	//work variables
	ISATABExportConfig cfg = null;
	Investigation investigation = null;
	
	public ISATABExporter()
	{	
	}
	
	public ISATABExporter(	Iterator<SubstanceRecord> records, 
							File outputDir, 
							File exportJsonConfig,
							SubstanceEndpointsBundle endpointBundle
							)
	{
		setRecords(records);
		setOutputDir(outputDir);
		setExportJsonConfig(exportJsonConfig);
		setEndpointBundle(endpointBundle);
	}
	
	public void export() throws Exception
	{
		if (records == null)
			throw new Exception("Null input records iterator!");
		
		if (outputDir == null)
			throw new Exception("Null output directory!");
		
		if (exportJsonConfig == null)
			throw new Exception("Null json config file!");
		
		cfg = ISATABExportConfig.loadFromJSON(exportJsonConfig);
		
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
		return exportJsonConfig;
	}

	public void setExportJsonConfig(File exportJsonConfig) {
		this.exportJsonConfig = exportJsonConfig;
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
	
	/*
	public Investigation getInvestigation() {
		return investigation;
	}
	*/

	@Override
	public ISAFormat getISAFormat() {
		return ISAFormat.TAB;
	}

	@Override
	public ISAVersion getISAVersion() {
		return ISAVersion.Ver1_0;
	}	
}
