package ambit2.export.isa.v1_0;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.export.isa.IISAExport;
import ambit2.export.isa.base.ExternalDataFileHeader;
import ambit2.export.isa.base.ExternalDataFileManager;
import ambit2.export.isa.base.ISAConst;
import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;
import ambit2.export.isa.json.ISAJsonExportConfig;
import ambit2.export.isa.json.ISAJsonExporter;
import ambit2.export.isa.v1_0.objects.Assay;
import ambit2.export.isa.v1_0.objects.Constituent;
import ambit2.export.isa.v1_0.objects.FactorValue;
import ambit2.export.isa.v1_0.objects.Investigation;
import ambit2.export.isa.v1_0.objects.Linkage;
import ambit2.export.isa.v1_0.objects.MaterialAttributeValue;
import ambit2.export.isa.v1_0.objects.Materials;
import ambit2.export.isa.v1_0.objects.Materials_;
import ambit2.export.isa.v1_0.objects.McmMaterial;
import ambit2.export.isa.v1_0.objects.Process;
import ambit2.export.isa.v1_0.objects.ProcessParameterValue;
import ambit2.export.isa.v1_0.objects.Protocol;
import ambit2.export.isa.v1_0.objects.ProtocolParameter;
import ambit2.export.isa.v1_0.objects.Publication;
import ambit2.export.isa.v1_0.objects.Sample;
import ambit2.export.isa.v1_0.objects.Source;
import ambit2.export.isa.v1_0.objects.Study;
import net.idea.modbcum.i.reporter.Reporter;

public class ISAJsonExporter1_0 implements IISAExport,
		Reporter<SubstanceRecord, Investigation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1052243240384487847L;
	protected boolean enabled = true;

	protected final static Logger logger = Logger
			.getLogger(ISAJsonExporter.class.getName());

	// Basic io variables
	// private SubstanceEndpointsBundle endpointBundle = null;
	private File outputDir = null;
	private File exportConfig = null;
	private File xmlISAConfig = null;
	private File externalDataFile = null;
	private ExternalDataFileManager extDataManager = null;
	private OntologyManager1_0 ontologyManager = null;

	// work variables
	private ISAJsonExportConfig cfg = null;

	// ISA data
	private Investigation investigation = null;
	private ISAJsonMapper1_0 isaMapper = null;

	public ISAJsonExporter1_0() {
	}

	public ISAJsonExporter1_0(File outputDir, File exportConfig) {
		setOutputDir(outputDir);
		setExportJsonConfig(exportConfig);
	}
	
	public ISAJsonExporter1_0(File outputDir, ISAJsonExportConfig cfg) {
		setOutputDir(outputDir);
		this.cfg = cfg;
	}
	
	public ISAJsonExporter1_0(File outputDir, ISAJsonExportConfig cfg, File extDataFile) {
		setOutputDir(outputDir);
		setExternalDataFile(extDataFile);
		this.cfg = cfg;
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
	
	public void setExternalDataFile(File externalDataFile){
		this.externalDataFile = externalDataFile; 
	}
	
	public File getExternalDataFile() {
		return externalDataFile; 
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	@Override
	public ISAFormat getISAFormat() {
		return ISAFormat.JSON;
	}

	@Override
	public ISAVersion getISAVersion() {
		return ISAVersion.Ver1_0;
	}

	public void init(SubstanceEndpointsBundle endpointBundle) throws Exception {

		if (outputDir == null)
			logger.info("Null output directory or file! ISA data can be extracted with function getResultAsJson() ");

		if (exportConfig == null) 
		{
			if (cfg == null)
				cfg = ISAJsonExportConfig.getDefaultConfig();
		} else
			cfg = ISAJsonExportConfig.loadFromJSON(exportConfig);

		// if (!records.hasNext())
		// logger.info("No substance records are found!");

		investigation = new Investigation();
		isaMapper = new ISAJsonMapper1_0();
		isaMapper.setTargetDataObject(investigation); // The data is put
														// (mapped) into
														// investigation object
		initExternalDataFile();		
		ontologyManager = new OntologyManager1_0();

		handleBundle(endpointBundle);
	}

	public void export(SubstanceEndpointsBundle endpointBundle,
			Iterator<SubstanceRecord> records) throws Exception {
		if (records == null)
			throw new Exception("Records iterator not initialized!");

		init(endpointBundle);
		while (records.hasNext()) {
			SubstanceRecord rec = records.next();
			handleRecord(rec);
		}

		saveDataToOutputDir();
		
		if (extDataManager != null)
			extDataManager.close();
	}
	
	void initExternalDataFile() throws Exception
	{
		if (externalDataFile != null)
		{	
			extDataManager = new ExternalDataFileManager(externalDataFile, cfg.extDataFileType);
			if (cfg.FlagUseDeafultExtDataFileHeader)
				extDataManager.setFileHeader(ExternalDataFileHeader.getDeafultExternalDataFileHeader());
			
			extDataManager.setFlagUseAbsolutePathForFileLocation(cfg.FlagUseAbsolutePathForExternalDataFileLocation);
		}	
	}	

	void handleBundle(SubstanceEndpointsBundle endpointBundle) throws Exception {
		if (endpointBundle == null)
			return;

		isaMapper.putString(endpointBundle.getTitle(), cfg.bundleTitleLoc);
		isaMapper.putString(endpointBundle.getDescription(),
				cfg.bundleDescriptionLoc, cfg.FlagDescriptionAdditiveContent);
	}

	void addLiteratureEntry(ILiteratureEntry litEntry) throws Exception {
		Publication pub = new Publication();
		pub.title = litEntry.getTitle();
		pub.authorList = litEntry.getName();
		if (litEntry.getURL() != null)
			pub.comments.add(ISAJsonUtils1_0.getComment("URL",
					litEntry.getURL()));

		investigation.publications.add(pub);
	}

	void addCompositionAsStudy(SubstanceRecord rec) throws Exception {
		List<CompositionRelation> compRelList = rec.getRelatedStructures();
		if (compRelList == null)
			return;

		Study study = new Study();
		investigation.studies.add(study);
		study.identifier = "Composition-" + rec.getSubstanceUUID();
		study.description = "Substance composition";
		study.materials = new Materials_();
		study.id = new URI("#study/composition");

		if (cfg.FlagAllCompositionInOneProcess) {
			Process process = new Process();
			study.processSequence.add(process);
			Source source = new Source();
			Sample sample = new Sample();
			process.inputs.add(source);
			process.outputs.add(sample);

			source.name = rec.getSubstanceUUID();
			sample.name = "composition";

			// Storing composition info
			String s = rec.getFormula();
			if (s != null) {
				// sample.characteristics.add()
			}

			for (int i = 0; i < compRelList.size(); i++) {
				String preff = "Comp" + (i + 1) + ".";
				CompositionRelation comRel = compRelList.get(i);

				if (comRel.getFormula() != null)
					sample.factorValues.add(ISAJsonUtils1_0.getFactorValue(
							preff + "formula", comRel.getFormula()));

				if (comRel.getSmiles() != null)
					sample.factorValues.add(ISAJsonUtils1_0.getFactorValue(
							preff + "smiles", comRel.getSmiles()));

				// sample.characteristics.add(mav);
			}
		} else {
			// TODO
		}

	}

	void addCompositionAsExtensionMaterial(SubstanceRecord rec) throws Exception 
	{
		List<CompositionRelation> compRelList = rec.getRelatedStructures();
		if (compRelList == null)
			return;
	
		investigation.mcmMaterial = new McmMaterial();
		
		for (int i = 0; i < compRelList.size(); i++) 
		{	
			CompositionRelation comRel = compRelList.get(i);
			Constituent constit = new Constituent();
			investigation.mcmMaterial.constituents.add(constit);
			constit.id = new URI("#constituent/" + (i+1));
			
			String name = extractName(comRel);
			if (name != null)
				constit.name = name;
			
			String role = getRole(comRel, rec);
			if (role != null)
				constit.role = role;
			
			List<Linkage> linkages = getLinkages(comRel);
			if (linkages != null)
				constit.linkages.addAll(linkages);
			
			List<MaterialAttributeValue> characteristics = getConstituentCharacteristics(comRel);
			if (characteristics != null)
				constit.characteristics.addAll(characteristics);
		}
	}
	
	String extractName(CompositionRelation comRel)
	{
		//All these fields are taken from comRel.getSecondStructure()
		if (comRel.getName() != null)
			return comRel.getName();
		if (comRel.getSmiles() != null)
			return comRel.getSmiles();
		if (comRel.getFormula() != null)
			return comRel.getFormula();
		if (comRel.getContent() != null)
			return comRel.getContent();
		if (comRel.getInchiKey() != null)
			return comRel.getInchiKey();
		if (comRel.getInchi() != null)
			return comRel.getInchi();
		
		return null;
	}
	
	String getRole(CompositionRelation comRel, SubstanceRecord rec)
	{
		STRUCTURE_RELATION str_relation = comRel.getRelationType();
		if (str_relation == null)
			return null;
		
		String sr = str_relation.toString();
		if (sr.startsWith("HAS_"))
			return sr.substring(4);
		
		//Handle case sr.endsWith("_OF") - currently not needed!
		return null;
	}
	
	List<MaterialAttributeValue> getConstituentCharacteristics(CompositionRelation comRel)
	{			
		List<MaterialAttributeValue> chList = new ArrayList<MaterialAttributeValue>();
		
		if (cfg.FlagExportFormulaAsConstituentCharacteristics)
		{
			String s = comRel.getFormula();
			if (s != null)
			{
				MaterialAttributeValue mav = ISAJsonUtils1_0.getMaterialAttributeValue(s, "Chemical Formula", null, null);
				chList.add(mav);
			}
		}
		
		if (cfg.FlagExportSmilesAsConstituentCharacteristics)
		{
			String s = comRel.getSmiles();
			if (s != null)
			{
				MaterialAttributeValue mav = ISAJsonUtils1_0.getMaterialAttributeValue(s, "SMILES", null, null);
				chList.add(mav);
			}
		}
		
		if (cfg.FlagExportInchiAsConstituentCharacteristics)
		{
			String s = comRel.getInchi();
			if (s != null)
			{
				MaterialAttributeValue mav = ISAJsonUtils1_0.getMaterialAttributeValue(s, "InChI", null, null);
				chList.add(mav);
			}
		}
		
		if (cfg.FlagExportInchiKeyAsConstituentCharacteristics)
		{
			String s = comRel.getInchiKey();
			if (s != null)
			{
				MaterialAttributeValue mav = ISAJsonUtils1_0.getMaterialAttributeValue(s, "InChI Key", null, null);
				chList.add(mav);
			}
		}
		
		if (cfg.FlagExportStrucTypeAsConstituentCharacteristics)
		{
			STRUC_TYPE str_type  = comRel.getType();
			if (str_type != null)
			{
				String s = null;
				switch (str_type)
				{
				case D1:
					s = "1D";
					break;				
				case D2noH:
				case D2withH:
					s = "2D";
					break;
				case D3noH:
				case D3withH:
					s = "3D";
					break;
				case MARKUSH:
					s = "Markush";
					break;
				case NANO:
					s = "Nano material";
					break;
				case PDB:
					s = "PDB";
					break;
				}
				
				if (s != null)
				{	
					MaterialAttributeValue mav = ISAJsonUtils1_0.getMaterialAttributeValue(s, "Structure Type", null, null);
					chList.add(mav);
				}	
			}
		}
		
		if (cfg.FlagExportRelationAsConstituentCharacteristics)
		{
			Proportion prop = comRel.getRelation();
			if (prop != null)
			{
				Double loVal = prop.getReal_lowervalue();
				if (loVal != null)
				{	
					String unit = prop.getReal_unit();
					MaterialAttributeValue mav = ISAJsonUtils1_0.getMaterialAttributeValue(loVal, "Constituent proportion", null, unit);
					chList.add(mav);
				}	
			}
		}
		
		
		if (chList.isEmpty())
			return null;
		else
			return chList; 
	}
	
	List<Linkage> getLinkages(CompositionRelation comRel)
	{	
		//Currently the linkage info is not supported directly in 
		//Ambit data model
		//TODO
		return null;
	}

	void addProtocolApplication(ProtocolApplication pa, String id) throws Exception {
		// Each protocol application is stored as a separate study
		Study study = new Study();
		investigation.studies.add(study);
		study.identifier = pa.getDocumentUUID();
		study.materials = new Materials_();
		
		/*
		Material mat = new Material();
		mat.name = "Mat1";
		mat.id = new URI("#material/Mat1");
		study.materials.otherMaterials.add(mat);
		*/

		// Handle protocol info
		Protocol protocol = extractProtocolInfo(pa);
		String protocolIdSuffix = "study/" + id ;
		protocol.id = new URI("#protocol/" + protocolIdSuffix);
		study.protocols.add(protocol);
		study.id = new URI("#study/" + id);

		// TODO configurable handling of some ProtocolApplication fields:
		// companyName, ...

		ISAJsonUtils1_0.addStudyDescriptionContent(study, "Substance UUID: "
				+ pa.getSubstanceUUID());

		// Create a study process
		Process process = new Process();
		study.processSequence.add(process);
		
		Source source = new Source();
		source.name = pa.getSubstanceUUID();
		source.id = new URI("#source/" + source.name);
		study.materials.sources.add(source);
		
		Sample sample = new Sample();
		sample.name = source.name + "[" + protocolIdSuffix + "]";
		sample.id = new URI("#sample/" + sample.name);
		study.materials.samples.add(sample);
		
		//Storing object that contains only source and sample id
		process.inputs.add((Source) ObjectUtils.getIdInstance(source, source.id));
		process.outputs.add((Sample) ObjectUtils.getIdInstance(sample, sample.id));
		
		//Storing object that contains only protocol id
		process.executesProtocol = (Protocol) ObjectUtils.getIdInstance(protocol, protocol.id);

		/*
		 * if (!cfg.FlagSaveSourceAndSampleOnlyInProcess) {
		 * //study.sources.add(source); //study.samples.add(sample); }
		 */
		
		
		// Handle ProtocolApplication parameters info which is added to the
		// process and to the protocol objects
		if (pa.getParameters() != null)
			if (pa.getParameters() instanceof IParams) {
				IParams params = (IParams) pa.getParameters();
				Set keys = params.keySet();
				for (Object key : keys) {
					ProcessParameterValue ppv = new ProcessParameterValue();
					process.parameterValues.add(ppv);
					ppv.value = params.get(key);
					ppv.category = new ProtocolParameter();
					ppv.category.parameterName = ontologyManager
							.getOntologyAnotation(key.toString());

					// Protocol name is added also here
					protocol.parameters.add(ppv.category);
				}
			}

		// Handle InterpretationCriteria and InterpretationResult
		// TODO

		// Handle reference
		String ref = pa.getReference();
		if (ref != null) {
			Publication pub = new Publication();
			// TODO extract if possible intelligently the info
			pub.title = ref;
			study.publications.add(pub);
		}

		// Handle effects records
		List<EffectRecord> effects = pa.getEffects();
		for (int i = 0; i < effects.size(); i++)
			addEffectRecord(effects.get(i), study, ""+ (i+1) + "/" + protocolIdSuffix);
	}

	Protocol extractProtocolInfo(ProtocolApplication pa) {
		Protocol protocol = new Protocol();
		ambit2.base.data.study.Protocol prot = (ambit2.base.data.study.Protocol) pa
				.getProtocol();

		protocol.name = prot.getEndpoint();

		if (prot.getCategory() != null) {
			protocol.protocolType = ontologyManager.getOntologyAnotation(prot
					.getCategory());
		}

		if (prot.getGuideline() != null)
			if (!prot.getGuideline().isEmpty()) {
				StringBuffer sb = new StringBuffer();
				List<String> guides = prot.getGuideline();
				for (int i = 0; i < guides.size(); i++) {
					sb.append(guides.get(i));
					if (i < guides.size() - 1)
						sb.append(" ");
				}
				protocol.description = sb.toString();
			}

		return protocol;
	}

	void addEffectRecord(EffectRecord effect, Study study, String assayId) throws Exception
	{
		Assay assay = new Assay();
		study.assays.add(assay);
		assay.materials = new Materials();
		assay.id = new URI("#assay/" + assayId);
		
		/*
		Material mat = new Material();
		mat.name = "Mat1";
		try {mat.id = new URI("#material/Mat1");} catch (Exception x) {};
		assay.materials.otherMaterials.add(mat);
		*/
		
		Process process1 = null;
		int procNum = 1;

		// Process 1 describes the conditions for the measurement (effect
		// record)
		// if there are no conditions process 1 is not registered
		if (effect.getConditions() != null) 
		{	
			process1 = new Process();
			process1.id = new URI("#process/" + procNum + "/assay/" + assayId  );
			assay.processSequence.add(process1);

			process1.name = /* effect.getEndpoint() + */"[conditions]";
			
			Source source1 = new Source();
			source1.name = study.identifier;
			source1.id = new URI("#source/" + source1.name); //?
			//study.materials.sources.add(source1); //?
			
			Sample sample1 = new Sample();
			sample1.name = source1.name + "[conditions]";
			sample1.id = new URI("#sample/" + sample1.name); //?
			assay.materials.samples.add(sample1);
			
			storeConditionsAsFactors(effect, sample1);
			
			//Storing object that contains only source and sample id
			process1.inputs.add((Source) ObjectUtils.getIdInstance(source1, source1.id));
			process1.outputs.add((Sample) ObjectUtils.getIdInstance(sample1, sample1.id));
			
			procNum++;
		}

		// Process 2 describes the measurement itself (the effect record)
		Process process2 = new Process();
		process2.id = new URI("#process/" + procNum + "/assay/" + assayId  );
		assay.processSequence.add(process2);
		process2.name = effect.getEndpoint() == null ? null : effect
				.getEndpoint().toString();
		
		Source source2 = new Source();
		Sample sample2 = new Sample();
		process2.inputs.add(source2);
		process2.outputs.add(sample2);

		if (process1 == null) {
			source2.name = study.identifier;
		} else {
			Sample sample1 = (Sample) process1.outputs.get(0);
			source2.name = sample1.name;
		}

		// Store data to external data file
		if (extDataManager != null)
		{	
			String extIdentifier = storeEffectValueToExternalFile(effect);
			if (extIdentifier != null)
				sample2.id = new URI(extIdentifier);
		}
		
	}
	
	String storeEffectValueToExternalFile(EffectRecord effect) throws Exception
	{
		String locId = null;
		Object value = null;
		Object error = "";
		
		if (effect.getLoValue() != null)
		{	
			if (effect.getUpValue() != null)
				value = "" + effect.getLoValue() + "," + effect.getUpValue();
			else
			{
				if (effect.getLoQualifier() != null)
					value = "" + effect.getLoQualifier() + " " + effect.getLoValue();
				else
					value = effect.getLoValue();
			}
		}
		else
			if (effect.getUpValue() != null)
			{
				if (effect.getUpQualifier() != null)
					value = "" + effect.getUpQualifier() + " " + effect.getUpValue();
				else
					value = effect.getUpValue();
			}
			else
				if (effect.getTextValue() != null)
				{
					value = effect.getTextValue();
				}
				else
					return null; //no value stored
				
		
		locId = extDataManager.storeData(value).getRecordLocationAsIdentifier();
		
		
		//Store error
		if (effect.getErrorValue() != null)
		{
			if (effect.getErrQualifier() != null)
				error = "" + effect.getErrQualifier() + " " + effect.getErrorValue();
			else
				error = effect.getErrorValue();
		}
		extDataManager.storeData(error);

		//Store unit
		if (effect.getUnit() != null)
			extDataManager.storeData(effect.getUnit());
		else
			extDataManager.storeData("");
			
		extDataManager.finalizeRecord();
		return locId;
	}

	void storeConditionsAsFactors(EffectRecord effect, Sample sample) {
		Object conditions = effect.getConditions();
		if (conditions == null)
			return;

		if (conditions instanceof IParams) {
			IParams params = (IParams) conditions;
			Set keys = params.keySet();
			for (Object key : keys) {
				Object condValue = params.get(key);
				FactorValue fv = ISAJsonUtils1_0.getFactorValue(key.toString(),
						condValue);
				sample.factorValues.add(fv);
			}
		}
	}

	void saveDataToOutputDir() throws Exception {
		if (cfg.singleJSONFile) 
		{
			
			if (outputDir.isDirectory())
				throw new Exception("Output is a directory for singleJSONFile = true");
			
			ObjectMapper mapper = new ObjectMapper();
			//mapper.setSerializationInclusion(Inclusion.NON_NULL);
			String jsonString =
					mapper.writerWithDefaultPrettyPrinter().writeValueAsString(investigation);
			
			FileWriter writer = null;
			
			try {
				writer = new FileWriter(outputDir);
				writer.write(jsonString);
				writer.flush();
			}catch (Exception x) {
				//in case smth's wrong with the writer file, close it and throw an error
				try {writer.close(); } catch (Exception xx) {}
				throw x;
			} finally { }

			writer.close(); 
		} 
		else 
		{
			//Multiple json files
			// TODO
		}

	}

	public String getResultAsJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		//mapper.setSerializationInclusion(Inclusion.NON_NULL);
		
		//mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		
		String jsonString = mapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(investigation);
		return jsonString;
	}

	@Override
	public long getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	private void handleRecord(SubstanceRecord rec) throws Exception {
		if (rec == null)
			return;

		ILiteratureEntry litEntry = rec.getReference();
		if (litEntry != null)
			addLiteratureEntry(litEntry);

		if (cfg.FlagSaveCompositionAsStudy)
			addCompositionAsStudy(rec);

		if (cfg.FlagSaveCompositionAsExtensionMaterial)
			addCompositionAsExtensionMaterial(rec);

		if (rec.getMeasurements() != null)
			//for (ProtocolApplication pa : rec.getMeasurements())
			for (int i = 0; i < rec.getMeasurements().size(); i++)	
				addProtocolApplication(rec.getMeasurements().get(i),"" + (i+1));
	}

	@Override
	public void open() throws Exception {

	}

	@Override
	public Investigation process(SubstanceRecord record) throws Exception {
		handleRecord(record);
		return getOutput();
	}

	@Override
	public void setEnabled(boolean arg0) {
		this.enabled = arg0;
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public String getFileExtension() {
		return ISAConst.ISAFormat.JSON.getExtension();
	}

	@Override
	public String getLicenseURI() {
		return null;
	}

	@Override
	public Investigation getOutput() throws Exception {
		return investigation;
	}

	@Override
	public long getTimeout() {
		return 0;
	}

	@Override
	public void setLicenseURI(String arg0) {
	}

	@Override
	public void setOutput(Investigation output) throws Exception {
		this.investigation = output;
	}

	@Override
	public void setTimeout(long arg0) {

	}

}
