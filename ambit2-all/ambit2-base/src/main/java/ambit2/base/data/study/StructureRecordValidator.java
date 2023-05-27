package ambit2.base.data.study;

import java.io.File;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import net.idea.modbcum.p.DefaultAmbitProcessor;

public class StructureRecordValidator extends
		DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {

	/**
     * 
     */
    protected static Logger logger_cli = Logger.getLogger(StructureRecordValidator.class.getName());
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
		this(UUID.randomUUID().toString(), false, "XLSX");
	}

	public StructureRecordValidator(String filename, boolean fixErrors, String prefix) {
		super();
		setFilename(filename);
		setFixErrors(fixErrors);
		setPrefix(prefix);
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

	public static SubstanceRecord basic_validation(SubstanceRecord record, String content, boolean xlsx, Date updated ) throws Exception {
        record.setContent(content);
        record.setFormat(xlsx ? "xlsx" : "xls");
        if (record.getRelatedStructures() != null && !record.getRelatedStructures().isEmpty()) {

            for (int i = record.getRelatedStructures().size() - 1; i >= 0; i--) {
                CompositionRelation rel = record.getRelatedStructures().get(i);
                int props = 0;
                for (Property p : rel.getSecondStructure().getRecordProperties()) {
                    Object val = rel.getSecondStructure().getRecordProperty(p);
                    if (val != null && !"".equals(val.toString()))
                        props++;
                }
                if ((rel.getContent() == null || "".equals(rel.getContent())) && (props == 0))
                    record.getRelatedStructures().remove(i);

            }

        }
        if (record.getMeasurements() != null)
            for (ProtocolApplication papp : record.getMeasurements()) {
                papp.setUpdated(updated);
            }
        return record;
    }
	public IStructureRecord validate(SubstanceRecord record) throws Exception {
		if (record.getOwnerName() == null) {
			logger.log(Level.WARNING, "Missing substance owner name");
			if (fixErrors) {
				record.setOwnerName(filename);
				logger.log(Level.WARNING,
						"Assigned file name to substance owner name");
			}
		}
		if (record.getSubstanceUUID() == null) {
			logger.log(Level.WARNING, "Missing substance UUID");
			if (fixErrors) {
				record.setSubstanceUUID(generateUUIDfromString(prefix,
						record.getOwnerName() + record.getSubstanceName()));
				logger.log(Level.WARNING,
						"Generated substance UUID from substance name");
			}
		}
		if (record.getMeasurements() == null) {
			logger.log(Level.WARNING, "No measurements");
		} else
			for (ProtocolApplication papp : record.getMeasurements()) {
				validate(record, papp);
			}
		if (record.getRelatedStructures() == null
				|| record.getRelatedStructures().isEmpty()) {
			logger.log(Level.WARNING, "No composition");
		} else {
			IStructureRecord referenceStructure = null;
			double proportion = 0;
			for (CompositionRelation rel : record.getRelatedStructures()) {
				validate(record, rel);
				switch (rel.getRelationType()) {
				case HAS_CONSTITUENT: {
					Object tvo = (rel.getRelation() == null || rel.getRelation().getTypical_value()==null) ? 0 : rel
							.getRelation().getTypical_value();
					double tv = 0;
					if (tvo != null)
						tv = (Double) tvo;
					if (referenceStructure == null || (proportion < tv)) {
						referenceStructure = rel.getSecondStructure();
						if (rel.getRelation() == null)
							proportion = 0;
						else
							proportion = rel.getRelation().getTypical_value() == null ? 0
									: rel.getRelation().getTypical_value();
					}
					break;
				}
				}
			}
			if ((record.getReferenceSubstanceUUID() == null)
					&& referenceStructure != null) {
				String uuid = generateUUIDfromString(prefix, null);
				record.setReferenceSubstanceUUID(uuid);
				referenceStructure.setRecordProperty(
						Property.getI5UUIDInstance(), uuid);
			}

		}

		if (record.getOwnerUUID() == null) {
			log(record, "Missing substance owner UUID");
			if (fixErrors) {
				record.setOwnerUUID(generateUUIDfromString(prefix,
						record.getOwnerName()));
				log(record,"Generated substance owner UUID");
			}
		}

		return record;
	}

	protected void log(SubstanceRecord record, String msg) {
		logger.log(Level.WARNING,
				String.format("%s\t%s", record.getSubstanceUUID(), msg));
	}

	public IStructureRecord validate(SubstanceRecord record,
			ProtocolApplication<Protocol, IParams, String, IParams, String> papp)
			throws Exception {
		if (papp.getSubstanceUUID() == null) {
			log(record, "Missing substance UUID in protocol application!");
			papp.setSubstanceUUID(record.getSubstanceUUID());
		}
		if (papp.getDocumentUUID() == null) {
			logger.log(Level.WARNING, "Missing measurement UUID");
			if (isFixErrors()) {
				papp.setDocumentUUID(generateUUIDfromString(prefix, null));
				log(record, "Generated measurement UUID");
			}
		}
		if (papp.getProtocol() == null)
			log(record, "Missing protocol");
		else {
			if (papp.getProtocol().getCategory() == null) {
				log(record, "Missing protocol category");
				if (isFixErrors()) {
					papp.getProtocol().setCategory(
							Protocol._categories.UNKNOWN_TOXICITY_SECTION
									.name());
					papp.getProtocol().setTopCategory(
							Protocol._categories.UNKNOWN_TOXICITY_SECTION
									.getTopCategory());
				}
			}
			if (papp.getProtocol().getTopCategory() == null) {
				log(record, "Missing protocol top category");
				if (isFixErrors()) {
					try {
						String c = papp.getProtocol().getCategory();
						if (!c.endsWith("_SECTION"))
							c = c + "_SECTION";
						Protocol._categories category = Protocol._categories
								.valueOf(c);
						papp.getProtocol().setCategory(category.name());
						papp.getProtocol().setTopCategory(
								category.getTopCategory());
					} catch (Exception x) {
						papp.getProtocol().setTopCategory(
								Protocol._categories.UNKNOWN_TOXICITY_SECTION
										.getTopCategory());
						papp.getProtocol().setCategory(
								Protocol._categories.UNKNOWN_TOXICITY_SECTION
										.name());
					}

				}
			}
		}
		if (papp.getInterpretationResult() == null)
			log(record, "Missing interpretation result");
		if (papp.getInterpretationCriteria() == null)
			log(record, "Missing interpretation criteria");

		if (papp.getEffects() == null)
			logger.log(Level.WARNING, "Missing results");
		else
			for (EffectRecord<String, IParams, String> effect : papp
					.getEffects()) {
				if (effect.getEndpoint() == null)
					log(record, "Missing endpoint name");
				if (effect.getConditions() == null
						|| effect.getConditions().isEmpty())
					log(record, "Missing conditions");
				if (effect.getLoValue() == null && effect.getUpValue() == null
						&& effect.getTextValue() == null)
					log(record, "Missing result value");
				if ((effect.getLoValue() != null || effect.getUpValue() != null)
						&& effect.getUnit() == null)
					log(record, "Value without units");
			}
		return record;
	}

	public IStructureRecord validate(SubstanceRecord record,
			CompositionRelation rel) throws Exception {
		if (rel.getCompositionUUID() == null) {
			log(record, "Missing composition UUID");
			if (isFixErrors()) {
				String uuid = generateUUIDfromString(prefix,
						record.getSubstanceUUID());
				rel.setCompositionUUID(uuid);
				log(record, "Generated composition UUID");
			}
		}
		return record;
	}

	public static String generateUUIDfromString(String prefix, String id) {
		return prefix
				+ "-"
				+ (id == null ? UUID.randomUUID() : UUID.nameUUIDFromBytes(id
						.getBytes()));
	}
}
