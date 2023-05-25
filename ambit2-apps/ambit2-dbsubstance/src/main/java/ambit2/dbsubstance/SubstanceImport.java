package ambit2.dbsubstance;

import java.io.File;
import java.io.InputStream;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.mysql.jdbc.CommunicationsException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.StructureRecordValidator;
import ambit2.base.data.study.Value;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.data.substance.ParticleTypes;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.core.io.IRawReader;
import ambit2.db.substance.processor.DBSubstanceWriter;
import net.enanomapper.expand.SubstanceRecordMapper;
import net.idea.modbcum.i.processors.IProcessor;

public class SubstanceImport extends DBSubstanceImport {
	
		
	protected static Properties pUndefined = new Properties();
	protected static Properties cUndefined = new Properties();
	protected static Properties vUndefined = new Properties();
	protected static Properties eUndefined = new Properties();
	protected static Properties unitsUndefined = new Properties();
	protected DictionaryConfig config;
	
	
	public SubstanceImport(DictionaryConfig dict) {
		super();
		if (dict == null)
			config = new DictionaryConfig();
		else config = dict;
	}
	public SubstanceImport() {
		this(new DictionaryConfig());
	}
	public static Properties getUnitsUndefined() {
		return unitsUndefined;
	}

	public static void setUnitsUndefined(Properties unitsUndefined) {
		SubstanceImport.unitsUndefined = unitsUndefined;
	}

	/**
	 * Undefined endpoints
	 * 
	 * @return
	 */
	public static Properties geteUndefined() {
		return eUndefined;
	}

	public static void seteUndefined(Properties eUndefined) {
		SubstanceImport.eUndefined = eUndefined;
	}

	/**
	 * Undefined values
	 * 
	 * @return
	 */
	public static Properties getvUndefined() {
		return vUndefined;
	}

	public static void setvUndefined(Properties vUndefined) {
		SubstanceImport.vUndefined = vUndefined;
	}

	/**
	 * Undefined conditions
	 * 
	 * @return
	 */
	public static Properties getcUndefined() {
		return cUndefined;
	}

	public static void setcUndefined(Properties cUndefined) {
		SubstanceImport.cUndefined = cUndefined;
	}

	/**
	 * Undefined protocol parameters
	 * 
	 * @return
	 */
	public static Properties getpUndefined() {
		return pUndefined;
	}

	public static void setpUndefined(Properties dict) {
		pUndefined = dict;
	}



	@Override
	protected IRawReader<IStructureRecord> createParser(InputStream stream, boolean xlsx) throws Exception {
		_parsertype mode = getParserType();
		InputStream in = null;
		if (gzipped)
			in = new GZIPInputStream(stream);
		else
			in = stream;
		if (mode == null)
			throw new Exception("Unsupported parser type " + mode);

		return super.createParser(in, xlsx);

	}

	@Override
	protected void configure(DBSubstanceWriter writer, SubstanceRecord record) {
		_parsertype mode = getParserType();
		if ("coseu".equals(mode.name())) {
			if (isSplitRecord()) {
				logger_cli.log(Level.WARNING, record.getSubstanceName());

				writer.setImportedRecord(record);
			}
		}

	}

	protected Object mapTerm(Object key) {
		if (key==null) return key;
		
		String _key = key.toString().trim().toUpperCase().replaceAll(" ", "_");
		Object term = _key;
		// original 2 enm
		try {
			term = config.getString(_DICT.pDictionary1,key.toString().trim());
		} catch (Exception x) {
			// not found, use what we have
			term = null;
		}
		if (term == null) {
			if (pUndefined.get(key) == null)
				pUndefined.put(key, "");
			term = _key;
		}

		// enm to nr
		try {
			Object term_nr = config.getString(_DICT.pDictionary2,term.toString());
			if (term_nr != null)
				term = term_nr;
		} catch (Exception x) {
			// not found, use what we have
		}
		return term;
	}
	protected String normalizeDOI(String doi) {
		return doi==null?null:doi.replace("http://", "https://").replace("https://dx.doi.org","https://www.doi.org").replace("https://doi.org", "https://www.doi.org").replace("%2F","/");
	}
	protected Object mapValue(Object value) {

		if (value instanceof String) {
			String v = ((String) value).trim();
			if ("".equals(v)) return null;
			value = v;
			if (v.startsWith("http")) {
				value = normalizeDOI(v);
			} else {
				String lookup = null;
				try {
					lookup = config.getString(_DICT.valuesDictionary,v);
				} catch (Exception x) {
					// x.printStackTrace();
				}
	
				if (lookup != null) {
					value = lookup;
				} else {
					if (vUndefined.get(v) == null)
						vUndefined.put(v, "");
	
				}
			}
		} else if (value instanceof Value) {
			String units = mapUnits(((Value) value).getUnits());
			((Value) value).setUnits(units);
			if (units != null)
				value = convertUnits((Value) value);

		} else if (value instanceof IParams) {
			String units = mapUnits(((IParams) value).getUnits());
			((IParams) value).setUnits(units);
			if (units != null)
				value = convertUnits((IParams) value);

		}

		return value;

	}

	protected String mapUnits(Object units) {
		if (units != null) {
			units = units.toString().trim().toLowerCase();
			if ("".equals(units)) {
				return null;
			} else {
				String lookup = null;
				try {
					lookup = config.getString(_DICT.unitsDictionary,units.toString());
				} catch (Exception x) {
					// x.printStackTrace();
				}
				if (lookup != null) {
					return lookup;
				} else {
					Object unit_dict = null;
					try {
						unit_dict = unitsUndefined.get(units);
					} catch (Exception x) {

					}
					if (unit_dict == null)
						unitsUndefined.put(units, "");

				}

			}
			return (String) units;
		}
		return null;

	}

	protected String mapEndpoints(Object endpoints) {
		if (endpoints != null) {
			endpoints = endpoints.toString().trim().toUpperCase().replace(" ", "_");
			if ("".equals(endpoints)) {
				return null;
			} else {
				String lookup = null;
				try {
					lookup = config.getString(_DICT.endpointsDictionary,endpoints.toString());
				} catch (Exception x) {
					// x.printStackTrace();
				}
				if (lookup != null) {
					return lookup;
				} else {
					Object endpoints_dict = null;
					try {
						endpoints_dict = eUndefined.get(endpoints);
					} catch (Exception x) {

					}
					if (endpoints_dict == null)
						eUndefined.put(endpoints, "");

				}

			}
			return (String) endpoints;
		}
		return null;

	}

	// TODO use jqudt
	protected Value convertUnits(Value value) {
		if ("d".equals(value.getUnits()) && (value.getUpValue() == null)
				&& (Double.valueOf(1.0).equals(value.getLoValue()))) {
			value.setUnits("h");
			value.setLoValue(24);
		}
		return value;

	}

	protected IParams convertUnits(IParams value) {
		if ("d".equals(value.getUnits()) && (value.getUpValue() == null)
				&& (Double.valueOf(1.0).equals(value.getLoValue()))) {
			value.setUnits("h");
			value.setLoValue(24);
		}
		return value;

	}

	protected void mapParameters(ProtocolApplication<Protocol, IParams, String, IParams, String> papp) {
		IParams newparams = new Params();

		if (papp.getParameters() != null) {
			Iterator<Entry<Object, Object>> t = papp.getParameters().entrySet().iterator();
			while (t.hasNext()) {
				Entry<Object, Object> entry = t.next();
				Object term = mapTerm(entry.getKey());
				Object value = mapValue(entry.getValue());
				newparams.put(term, value);
			}
			papp.setParameters(newparams);
		}
	}

	protected void mapConditions(EffectRecord<String, IParams, String> effect) {
		if (effect.getConditions() != null) {
			IParams newparams = new Params();
			Iterator<Entry<Object, Object>> t = effect.getConditions().entrySet().iterator();
			while (t.hasNext()) {
				Entry<Object, Object> entry = t.next();
				Object term = mapTerm(entry.getKey());
				Object value = mapValue(entry.getValue());
				newparams.put(term, value);
			}
			effect.setConditions(newparams);
		}
	}

	protected void hack_emptyendpoint_annotation(EffectRecord<String, IParams, String> effectRecord) {

		if ("".equals(effectRecord.getEndpoint()) && effectRecord.getConditions() != null) {
			Object ep = effectRecord.getConditions().get("EndPoint");
			if (ep != null) {
				if (ep.toString().toUpperCase().startsWith("AVG")) {
					effectRecord.setEndpoint(ep.toString().toUpperCase().replaceAll("AVG ", "").trim());
				} else
					effectRecord.setEndpoint(ep.toString());
			}
		}

	}

	protected void cleanupEmptyRecords(SubstanceRecord srecord, List<ProtocolApplication> m) {
		if (m == null)
			return;
		for (int i = m.size() - 1; i >= 0; i--) {
			ProtocolApplication<Protocol, IParams, String, IParams, String> papp = m.get(i);

			papp.setReference(normalizeDOI(papp.getReference()));
			if (papp.getProtocol().getGuideline()!=null)
			for (int j=0; j < papp.getProtocol().getGuideline().size(); j++) {
				String guidance = papp.getProtocol().getGuideline().get(j);
				if (guidance.startsWith("http"))
					papp.getProtocol().getGuideline().set(j, normalizeDOI(guidance));
			}
			mapParameters(papp);
			boolean empty = true;
			if (papp.getEffects() != null) {
				List<EffectRecord<String, IParams, String>> tempEffects = new ArrayList<EffectRecord<String, IParams, String>>(); 
				for (EffectRecord<String, IParams, String> effect : papp.getEffects()) {

					hack_emptyendpoint_annotation(effect);

					if (papp.getProtocol().getGuideline() == null)
						logger_cli.log(Level.SEVERE, String.format("!!! null guideline %s %s", srecord.getPublicName(),
								papp.getProtocol().getEndpoint()));

					if (effect.getEndpoint() == null) {

						logger_cli.log(Level.SEVERE,
								String.format("!!! null endpoint %s %s %s", srecord.getPublicName(),
										papp.getProtocol().getEndpoint(), papp.getProtocol().getGuideline().get(0)));
					} else {
						effect.setEndpoint(mapEndpoints(effect.getEndpoint()));
						
						if ("COMMA_DELIMITED_LIST".equals(effect.getEndpointType())) try {
							effect.setEndpointType(null);
							
							String[] vals = effect.getTextValue().toString().split(",");
							for (int j=0; j < vals.length; j++) {
								EffectRecord erecord = effect;
								if (j>0) {
									erecord = new EffectRecord<String, IParams, String>();
									erecord.setEndpoint(effect.getEndpoint());
									erecord.setUnit(effect.getUnit());
									erecord.setLoQualifier(effect.getLoQualifier());
									tempEffects.add(erecord);
									
								} else {
									erecord.setTextValue(null);
								}
								try {
									erecord.setLoValue(Double.parseDouble(vals[j].trim()));
								} catch (Exception x) {
									erecord.setTextValue(vals[j].trim());
								}
							}
						} catch (Exception x) {
							x.printStackTrace();
						} 						
						/*
						 * if (eUndefined.get(effect.getEndpoint()) == null)
						 * eUndefined.put(effect.getEndpoint(), effect.getEndpoint());
						 */
						Protocol._categories pc = null;
						try {
							pc = Protocol._categories.valueOf(papp.getProtocol().getCategory());
						} catch (Exception x) {
							pc = Protocol._categories.PC_UNKNOWN_SECTION;
						}
						switch (pc) {
						case CRYSTALLINE_PHASE_SECTION:
							break;
						case SURFACE_CHEMISTRY_SECTION:
							
						    break;
						case IMPURITY_SECTION:
						case ANALYTICAL_METHODS_SECTION:
							// clean if empty element value
							if (effect.getLoValue() == null && effect.getUpValue() == null)
									effect.setTextValue(null);
							break;
						default:
							if (effect.getLoValue() != null || effect.getUpValue() != null)
								effect.setTextValue(null);
						}

					}
				}

				List<EffectRecord<String, IParams, String>> effects = papp.getEffects();
				papp.getEffects().addAll(tempEffects);
				for (int j = effects.size() - 1; j >= 0; j--) {
					if (!effects.get(j).isEmpty()) {
						empty = false;
						mapConditions(effects.get(j));
					} else {
						logger_cli.log(Level.WARNING,
								String.format("Remove empty effect record\t%s\t%s\t%s\t%s\t%s [%s]",
										srecord.getSubstanceName(), srecord.getPublicName(),
										papp.getProtocol().getTopCategory(), papp.getProtocol().getCategory(),
										papp.getProtocol().getEndpoint(), papp.getEffects().get(j).getErrQualifier()));
						papp.getEffects().remove(j);
					}
				}

				if (empty && ((papp.getInterpretationResult() == null) || "".equals(papp.getInterpretationResult()))) {
					m.remove(i);
					logger_cli.log(Level.WARNING, String.format("Remove empty protocol application\t%s\t%s\t%s\t%s",
							srecord.getSubstanceName(), srecord.getPublicName(), papp.getProtocol().getTopCategory(),
							papp.getProtocol().getCategory(), papp.getProtocol().getEndpoint()));
				}

			}
		}
	}



	@Override
	protected void validate(StructureRecordValidator validator, IStructureRecord record) throws Exception {
		if (record instanceof SubstanceRecord) {

			_parsertype mode = getParserType();
			SubstanceRecord srecord = (SubstanceRecord) record;

			String nmcode = srecord.getPublicName();
			if (nmcode != null)
				nmcode = nmcode.trim();

			String jrcid = srecord.getPublicName() == null ? null
					: config.getMappedString(srecord.getPublicName().trim().toUpperCase(), _DICT.nmcode2jrcid);
			if (jrcid != null && jrcid.startsWith("NM-")) {
				nmcode = jrcid;
				jrcid = config.getMappedString(nmcode, _DICT.nmcode2jrcid);
			}

			if (jrcid != null && jrcid.startsWith("JRCNM")) {
				if (srecord.getExternalids() == null)
					srecord.setExternalids(new ArrayList<ExternalIdentifier>());
				srecord.getExternalids().add(new ExternalIdentifier("JRC ID", jrcid));
			}
			srecord.setPublicName(jrcid);

			String substancename = config.getMappedString(jrcid, _DICT.jrcid2substancename, null);

			if (nmcode != null && nmcode.startsWith("NM-")) {
				if (srecord.getSubstanceName().indexOf(nmcode) < 0)
					srecord.setSubstanceName(String.format("%s (%s)", nmcode, srecord.getSubstanceName()));
				if (srecord.getExternalids() == null)
					srecord.setExternalids(new ArrayList<ExternalIdentifier>());
				srecord.getExternalids().add(new ExternalIdentifier("NM code", nmcode));
			}

			if (srecord.getExternalids() != null) {
				List<ExternalIdentifier> ids = new ArrayList<>();
				for (ExternalIdentifier id : srecord.getExternalids()) {
					if (!"".equals(id.getSystemIdentifier()))
						ids.add(id);
				}
				srecord.setExternalids(ids);
			}

			if (substancename != null) {
				srecord.setSubstanceName(substancename.trim());
			}

			srecord.setSubstancetype(config.getMappedString(srecord.getPublicName(), _DICT.jrc2enm, srecord.getSubstancetype()));
			// composition based on substancetypes
			if (srecord.getRelatedStructures() == null && isAddDefaultComposition())
				try {
					List<CompositionRelation> cr = getDefaultComposition(srecord);
					if (cr != null)
						srecord.setRelatedStructures(cr);
				} catch (Exception x) {
				}

			if (srecord.getReferenceSubstanceUUID() == null) {
				logger_cli.log(Level.WARNING, "Missing Reference Substance UUID");

				if (srecord.getReferenceSubstanceUUID() == null)
					try {
						ParticleTypes stype = ParticleTypes.valueOf(srecord.getSubstancetype());
						logger_cli.log(Level.WARNING, "Missing Reference Substance UUID");
						String ref_uuid = stype.getReferenceUUID();
						if (ref_uuid != null)
							srecord.setReferenceSubstanceUUID(ref_uuid);

					} catch (Exception x) {
					}
			}

			List<ProtocolApplication> m = srecord.getMeasurements();

			cleanupEmptyRecords(srecord, m);
			if (srecord.getMeasurements() != null) {
				mapOwners(srecord.getMeasurements());
				mapGuideline(srecord.getMeasurements());
				for (ProtocolApplication<Protocol, IParams, String, IParams, String>  papp : m) {
					try {
						if (papp.getParameters()==null)
							papp.setParameters(new Params());
						papp.getParameters().put("__input_file", validator.getFilename());
					} catch (Exception x) {
						x.printStackTrace();
					}
					StringBuilder b = null;
					if (papp.getProtocol().getGuideline()!=null) {
						for (String guideline : papp.getProtocol().getGuideline()) 
							if (!"".equals(guideline.trim())) {
								if (guideline.toLowerCase().contains("dispersion"))
									continue;
								if (b==null) {
									b = new StringBuilder();
								} else b.append(";");
								b.append(guideline.trim());
							}
						papp.getProtocol().getGuideline().clear();
						if (b!=null)
							papp.getProtocol().getGuideline().add(b.toString());
						else
							papp.getProtocol().getGuideline().add("!!! SOP MISSING !!!");
					}
				}
			}
			
		}

		super.validate(validator, record);
	}

	protected List<CompositionRelation> getDefaultComposition(SubstanceRecord srecord) throws Exception {
		try {
			ParticleTypes ptype = ParticleTypes.valueOf(srecord.getSubstancetype());
			List<CompositionRelation> crlist = new ArrayList<CompositionRelation>();

			StructureRecord core = new StructureRecord();
			core.setFormula(ptype.getFormula());
			if (ptype.getSMILES() != null) {
				core.setContent(ptype.getSMILES());
				core.setFormat("INC");
				core.setSmiles(ptype.getSMILES());
			}
			if (ptype.getCAS() != null)
				core.setRecordProperty(Property.getCASInstance(), ptype.getCAS());
			if (ptype.getEINECS() != null)
				core.setRecordProperty(Property.getEINECSInstance(), ptype.getEINECS());
			if (ptype.getName() != null || ptype.getFormula() != null)
				core.setRecordProperty(Property.getNameInstance(),
						ptype.getName() == null ? ptype.getFormula() : ptype.getName());
			// todo if there are no coating, will consider this is a component,
			// not a core
			Proportion p = new Proportion();
			// p.setTypical_value(100.0);
			// p.setTypical_unit("%");
			CompositionRelation cr = new CompositionRelation(srecord, core, ptype.getDefaultRole(), p);
			cr.setCompositionUUID(srecord.getSubstanceUUID());
			crlist.add(cr);
			return crlist;

		} catch (Exception x) {
			throw x;
		}

	}

	protected void mapGuideline(List<ProtocolApplication> papps) {
		for (ProtocolApplication papp : papps) {
			
			ProtocolApplication<Protocol, IParams, String, IParams, String> p = (ProtocolApplication<Protocol, IParams, String, IParams, String>) papp;
			if (p.getProtocol().getGuideline()!=null)	
			for (int i=0; i< p.getProtocol().getGuideline().size(); i++ ) {
				String guide = p.getProtocol().getGuideline().get(i);
				try {
					String term  = config.getString(_DICT.protocolDictionary,
							guide.replace(":", "").trim());
					if (term != null)
						p.getProtocol().getGuideline().set(i, term);
				} catch (Exception x) {
					
				}					
			
			}
		}

	}

	protected void mapOwners(List<ProtocolApplication> papps) throws Exception {
		for (ProtocolApplication papp : papps) {

			ProtocolApplication<Protocol, IParams, String, IParams, String> p = (ProtocolApplication<Protocol, IParams, String, IParams, String>) papp;
			String owner = p.getReferenceOwner();
			if (owner == null) throw new Exception("CITATION OWNER should not be null!");
			String cleaned = owner.replace("\n", " ").replace("\r", "").trim();
			try {
				String term = config.getString(_DICT.ownersDictionary,cleaned.replace(":", ""));
				if (term != null)
					cleaned = term;

			} catch (Exception x) {
				cleaned = cleaned.toUpperCase();
			} finally {

			}
			p.setReferenceOwner(cleaned);
		}

	}

	@Override
	protected Option createParserTypeOption() {
		return OptionBuilder.hasArg().withLongOpt("parser").withArgName("type")
				.withDescription("File parser mode : xls|xlsx|modnanotox|nanowiki").create("p");
	}

	@Override
	protected IProcessor<IStructureRecord, IStructureRecord> createMapper(boolean xlsx) {
		try {
			File expandMap = getExpandMap();
			
			return expandMap==null?null:new SubstanceRecordMapper(getPrefix(), getExpandMap());
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		long now = System.currentTimeMillis();
		try {
			SubstanceImport object = new SubstanceImport();
			if (object.parse(args)) {
				object.importFile();
			} else
				System.exit(0);
		} catch (ConnectException x) {
			logger_cli.log(Level.SEVERE, "Connection refused. Please verify if the remote server is responding.");
			System.exit(-1);
		} catch (CommunicationsException x) {
			logger_cli.log(Level.SEVERE, "Can't connect to MySQL server!");
			System.exit(-1);
		} catch (SQLException x) {
			logger_cli.log(Level.SEVERE, "SQL error", x);
			System.exit(-1);
		} catch (Exception x) {

			logger_cli.log(Level.WARNING, x.getMessage(), x);
			System.exit(-1);
		} finally {
			logger_cli.info(String.format("Completed in %s msec", (System.currentTimeMillis() - now)));
			logger_cli.info("Done.");
		}
	}
}
