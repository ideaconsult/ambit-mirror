package ambit2.export.isa.v1_0;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.export.isa.IISAExport;
import ambit2.export.isa.base.ExternalDataFileManager;
import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;
import ambit2.export.isa.json.ISAJsonExportConfig;
import ambit2.export.isa.json.ISAJsonExporter;
import ambit2.export.isa.v1_0.objects.Assay;
import ambit2.export.isa.v1_0.objects.Investigation;
import ambit2.export.isa.v1_0.objects.MeasurementType;
import ambit2.export.isa.v1_0.objects.Protocol;
import ambit2.export.isa.v1_0.objects.Publication;
import ambit2.export.isa.v1_0.objects.Sample;
import ambit2.export.isa.v1_0.objects.Source;
import ambit2.export.isa.v1_0.objects.Study;
import ambit2.export.isa.v1_0.objects.Process;

public class ISAJsonExporter1_0 implements IISAExport
{
	protected final static Logger logger = Logger.getLogger(ISAJsonExporter.class.getName());
	
	//Basic io variables
	Iterator<SubstanceRecord> records = null;
	SubstanceEndpointsBundle endpointBundle = null;
	File outputDir = null;
	File exportConfig = null;
	File xmlISAConfig =  null;
	File extDataFile = null;
	ExternalDataFileManager extDataManager = null;

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

		if (outputDir == null)	
			logger.info("Null output directory or file! ISA data can be extarcted with function getResultAsJson() ");
			

		if (exportConfig == null)
		{	
			cfg = ISAJsonExportConfig.getDefaultConfig();
		}
		else
			cfg = ISAJsonExportConfig.loadFromJSON(exportConfig);

		if (!records.hasNext())
			logger.info("No substance records are found!");
			
				
		investigation = new Investigation();
		isaMapper = new ISAJsonMapper1_0 ();
		isaMapper.setTargetDataObject(investigation);  //The data is put (mapped) into investigation object
		setExternalDataFile();
		extDataManager = new ExternalDataFileManager(extDataFile);
		
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
	
	void setExternalDataFile() throws Exception
	{
		//TODO
	}
	
	void handleBundle() throws Exception
	{
		if (endpointBundle ==null)
			return;
		
		isaMapper.putString(endpointBundle.getTitle(), cfg.bundleTitleLoc);
		isaMapper.putString(endpointBundle.getDescription(), cfg.bundleDescriptionLoc, cfg.FlagDescriptionAdditiveContent);
	}
	
	void handleRecord(SubstanceRecord rec) throws Exception
	{
		if (rec == null)
			return;
		
		
		ILiteratureEntry litEntry = rec.getReference();
		if (litEntry != null)
			addLiteratureEntry(litEntry);
		
		if (cfg.FlagSaveCompositionAsStudy)
			addCompositionAsStudy(rec);
		
		if (cfg.FlagSaveCompositionAsMaterial)
			addCompositionAsMaterial(rec);
		
		for (ProtocolApplication pa : rec.getMeasurements())
			addProtocolApplication(pa);
	}
	
	void addLiteratureEntry(ILiteratureEntry litEntry) throws Exception
	{
		Publication pub = new Publication();
		pub.title = litEntry.getTitle();
		//TODO
		investigation.publications.add(pub);
	}
	
	void addCompositionAsStudy(SubstanceRecord rec) throws Exception
	{
		//TODO
		
	}
	
	void addCompositionAsMaterial(SubstanceRecord rec) throws Exception
	{
		//TODO
		
	}
	
	void addProtocolApplication(ProtocolApplication pa) throws Exception
	{
		//Each protocol application is stored as a separate study
		Study study = new Study();
		investigation.studies.add(study);
		study.identifier = pa.getDocumentUUID();
				
		//Handle protocol info
		Protocol protocol = extractProtocolInfo(pa);
		study.protocols.add(protocol);
		
		//TODO configurable handling of some ProtocolApplication fields: companyName, ...
		
		ISAJsonUtils1_0.addStudyDescriptionContent(study, "Substance UUID: " + pa.getSubstanceUUID());
		
		
		//Create a study process
		Process process = new Process();
		study.processSequence.add(process);
		Source source = new Source();
		Sample sample = new Sample();
		process.inputs.add(source);
		process.outputs.add(sample);
		//process.executesProtocol = protocol; ??
		
		if (!cfg.FlagSaveSourceAndSampleOnlyInProcess)
		{
			study.sources.add(source);
			study.samples.add(sample);
		}
		
		//process.parameters
		source.name = pa.getSubstanceUUID();
		
		//TODO
		
		//Handle effects records
		List<EffectRecord> effects = pa.getEffects();
		for (EffectRecord eff : effects)
			addEffectRecord(eff, study);
	}
	
	Protocol extractProtocolInfo(ProtocolApplication pa) 
	{
		Protocol protocol = new Protocol();
		ambit2.base.data.study.Protocol prot = (ambit2.base.data.study.Protocol) pa.getProtocol();
		
		protocol.name = prot.getEndpoint();
		
		MeasurementType measType = new MeasurementType();
		measType.name = prot.getCategory();
		protocol.protocolType = measType;
		
		if (prot.getGuideline() != null)
			if (!prot.getGuideline().isEmpty())
			{
				StringBuffer sb = new StringBuffer();
				List<String> guides = prot.getGuideline();
				for (int i = 0; i < guides.size(); i++)
				{	
					sb.append(guides.get(i));
					if (i < guides.size()-1)
						sb.append(" ");
				}	
				protocol.description = sb.toString();
			}
		
		return protocol;
	}
	
	void addEffectRecord(EffectRecord effect, Study study)
	{
		Assay assay = new Assay();
		study.assays.add(assay);
		
		//Create assay processes
		Process process1 = new Process();
		Process process2 = new Process();
		assay.processSequence.add(process1);
		assay.processSequence.add(process2);
		
		//Set process 1
		Source source1 = new Source();
		Sample sample1 = new Sample();
		process1.inputs.add(source1);
		process1.outputs.add(sample1);
		source1.name = study.identifier;
		
		//Set process 2
		
		
		//Store data to external data file
		//String identifier = extDataManager.storeData(effect.getTextValue()).getLocationAsIdentifier();
		
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
