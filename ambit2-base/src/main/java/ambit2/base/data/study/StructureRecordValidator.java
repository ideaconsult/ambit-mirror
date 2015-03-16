package ambit2.base.data.study;

import java.util.UUID;
import java.util.logging.Level;

import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;

public class StructureRecordValidator extends DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {

    /**
     * 
     */
    private static final long serialVersionUID = 201323883830157691L;
    protected boolean fixErrors = false;
    protected String prefix = "XLSX";
    protected String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPrefix() {
	return prefix;
    }

    public void setPrefix(String prefix) {
	this.prefix = prefix;
    }

    public StructureRecordValidator() {
	this(UUID.randomUUID().toString(),false);
    }

    public StructureRecordValidator(String filename, boolean fixErrors) {
	super();
	setFilename(filename);
	setFixErrors(fixErrors);
    }

    public boolean isFixErrors() {
	return fixErrors;
    }

    public void setFixErrors(boolean fixErrors) {
	this.fixErrors = fixErrors;
    }

    @Override
    public IStructureRecord process(IStructureRecord record) throws Exception {
	if (record instanceof SubstanceRecord)
	    return validate((SubstanceRecord) record);
	return record;
    }

    public IStructureRecord validate(SubstanceRecord record) throws Exception {
	if (record.getOwnerName()==null) {
	    logger.log(Level.WARNING, "Missing substance owner name");
	    if (fixErrors) {
		record.setOwnerName(filename);
		logger.log(Level.WARNING, "Assigned file name to substance owner name");
	    }
	}	
	if (record.getCompanyUUID()==null) {
	    logger.log(Level.WARNING, "Missing substance UUID");
	    if (fixErrors) {
		record.setCompanyUUID(generateUUIDfromString(prefix, record.getOwnerName() + record.getCompanyName()));
		logger.log(Level.WARNING, "Generated substance UUID from substance name");
	    }
	}   		
	if (record.getMeasurements() == null) {
	    logger.log(Level.WARNING, "No measurements");
	} else
	    for (ProtocolApplication papp : record.getMeasurements()) {
		validate(record, papp);
	    }
	if (record.getRelatedStructures() == null || record.getRelatedStructures().isEmpty()) {
	    logger.log(Level.WARNING, "No composition");
	} else {
	    IStructureRecord referenceStructure = null;
	    double proportion = 0;
	    for (CompositionRelation rel : record.getRelatedStructures()) {
		validate(record, rel);
		switch (rel.getRelationType()) {
		case HAS_CONSTITUENT : {
		    if (referenceStructure==null || (proportion < rel.getRelation().getTypical_value())) {
			referenceStructure = rel.getSecondStructure();
			proportion =  rel.getRelation().getTypical_value()==null?0:rel.getRelation().getTypical_value();
		    }
		    break;
		}
		}
	    }
	    if ((record.getReferenceSubstanceUUID()==null) && referenceStructure!=null) {
		String uuid = generateUUIDfromString(prefix, null);
		record.setReferenceSubstanceUUID(uuid);
		referenceStructure.setProperty(Property.getI5UUIDInstance(), uuid);
	    }

	}

	if (record.getOwnerUUID()==null) {
	    logger.log(Level.WARNING, "Missing substance owner UUID");
	    if (fixErrors) {
		record.setOwnerUUID(generateUUIDfromString(prefix, record.getOwnerName()));
		logger.log(Level.WARNING, "Generated substance owner UUID");
	    }
	}    

	return record;
    }

    public IStructureRecord validate(SubstanceRecord record,
	    ProtocolApplication<Protocol, IParams, String, IParams, String> papp) throws Exception {
	if (papp.getSubstanceUUID()==null) {
	    logger.log(Level.SEVERE, "Missing substance UUID in protocol application!");
	    papp.setSubstanceUUID(record.getCompanyUUID());
	}
	if (papp.getDocumentUUID() == null) {
	    logger.log(Level.SEVERE, "Missing measurement UUID");
	    if (isFixErrors()) {
		papp.setDocumentUUID(generateUUIDfromString(prefix, null));
		logger.log(Level.WARNING, "Generated measurement UUID");
	    }
	}
	if (papp.getProtocol() == null)
	    logger.log(Level.SEVERE, "Missing protocol");
	else {
	    if (papp.getProtocol().getCategory() == null) {
		logger.log(Level.SEVERE, "Missing protocol category");
		if (isFixErrors()) {
		    papp.getProtocol().setCategory(Protocol._categories.UNKNOWN_TOXICITY_SECTION.name());
		    papp.getProtocol().setTopCategory(Protocol._categories.UNKNOWN_TOXICITY_SECTION.getTopCategory());
		}
	    }
	    if (papp.getProtocol().getTopCategory() == null) {
		logger.log(Level.SEVERE, "Missing protocol top category");
		if (isFixErrors()) {
		    try {
			String c = papp.getProtocol().getCategory();
			if (!c.endsWith("_SECTION")) c = c + "_SECTION";
			Protocol._categories category = Protocol._categories.valueOf(c);
			papp.getProtocol().setCategory(category.name());
			papp.getProtocol().setTopCategory(category.getTopCategory());
		    } catch (Exception x) {
			papp.getProtocol().setTopCategory(Protocol._categories.UNKNOWN_TOXICITY_SECTION.getTopCategory());
			papp.getProtocol().setCategory(Protocol._categories.UNKNOWN_TOXICITY_SECTION.name());
		    }
		    
		}
	    }
	}
	if (papp.getInterpretationResult() == null)
	    logger.log(Level.WARNING, "Missing interpretation result");
	if (papp.getInterpretationCriteria() == null)
	    logger.log(Level.WARNING, "Missing interpretation criteria");

	if (papp.getEffects() == null)
	    logger.log(Level.SEVERE, "Missing results");
	else
	    for (EffectRecord<String, IParams, String> effect : papp.getEffects()) {
		if (effect.getEndpoint()==null)  logger.log(Level.SEVERE, "Missing endpoint name");
		if (effect.getConditions()==null || effect.getConditions().isEmpty())  logger.log(Level.WARNING, "Missing conditions");
		if (effect.getLoValue()==null && effect.getUpValue()==null && effect.getTextValue()==null)  logger.log(Level.SEVERE, "Missing result value");
		if ((effect.getLoValue()!=null || effect.getUpValue()!=null) && effect.getUnit()==null)  logger.log(Level.WARNING, "Value without units");
	    }
	return record;
    }

    public IStructureRecord validate(SubstanceRecord record, CompositionRelation rel) throws Exception {
	if (rel.getCompositionUUID() == null) {
	    logger.log(Level.SEVERE, "Missing composition UUID");
	    if (isFixErrors()) {
		String uuid = generateUUIDfromString(prefix, null);
		rel.setCompositionUUID(uuid);
		logger.log(Level.WARNING, "Generated composition UUID");
	    }
	}
	return record;
    }

    public static String generateUUIDfromString(String prefix, String id) {
	return prefix + "-" + (id == null ? UUID.randomUUID() : UUID.nameUUIDFromBytes(id.getBytes()));
    }
}
