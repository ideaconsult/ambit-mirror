package ambit2.core.io.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.ProtocolApplicationAnnotated;
import ambit2.base.data.study.ReliabilityParams;
import ambit2.base.data.study.Value;
import ambit2.base.data.study.ValueAnnotated;
import ambit2.base.data.study._FIELDS_RELIABILITY;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.interfaces.ICiteable;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.io.IRawReader;

public class SubstanceStudyParser extends DefaultIteratingChemObjectReader
		implements IRawReader<IStructureRecord>, ICiteable {
	protected static Logger logger = Logger.getLogger(SubstanceStudyParser.class.getName());
	protected ObjectMapper dx = new ObjectMapper();
	protected ArrayNode substance;
	protected ArrayNode study;
	protected int index = -1;
	protected SubstanceRecord record = null;

	public SubstanceStudyParser(InputStreamReader reader) throws Exception {
		super();
		setReader(reader);
	}

	@Override
	public void setReader(Reader reader) throws CDKException {
		try {
			JsonNode root = dx.readTree(reader);
			JsonNode node = root.get("substance");
			if (node instanceof ArrayNode) {
				substance = (ArrayNode) node;
				index = -1;
			} else {
				substance = null;
				node = root.get("study");
				if (node instanceof ArrayNode) {
					study = (ArrayNode) node;
					index = -1;
				}
			}
		} catch (Exception x) {
			throw new CDKException(x.getMessage(), x);
		} finally {
			try {
				reader.close();
			} catch (Exception x) {
			}
		}
	}

	@Override
	public void setReader(InputStream in) throws CDKException {
		try {
			setReader(new InputStreamReader(in, "UTF-8"));
		} catch (Exception x) {
			throw new CDKException(x.getMessage(), x);
		}

	}

	@Override
	public IResourceFormat getFormat() {
		return null;
	}

	@Override
	public void close() throws IOException {

	}

	protected void generateDocumentUUID(ProtocolApplication papp)
			throws Exception {
		papp.setDocumentUUID("GNTD-"
				+ UUID.nameUUIDFromBytes(
						Integer.toHexString(papp.hashCode()).getBytes())
						.toString());
	}

	protected void handleException(Exception x) {
		x.printStackTrace();
	}

	@Override
	public boolean hasNext() {
		index++;
		if (substance != null) {
			if (index < substance.size()) {
				record = parseSubstance(substance.get(index));
				JsonNode papps = substance.get(index).get("study");
				try {
					record.setMeasurements(parseProtocolApplications(papps,
							record));
				} catch (Exception x) {
					handleException(x);
				}
				JsonNode composition = substance.get(0).get("composition");
				record.setRelatedStructures(parseComposition(composition));
				return true;
			} else {
				record = null;
				return false;
			}
		} else if (study != null) {
			if (index < study.size()) {
				// record = parseSubstance(substance.get(index));
				record = new SubstanceRecord();
				JsonNode papps = study.get(index);
				try {
					record.setSubstanceUUID(papps.get("owner").get("substance")
							.get("uuid").textValue());
				} catch (Exception x) {
					x.printStackTrace();
				}
				try {
					record.setMeasurements(parseProtocolApplications(papps,
							record));
				} catch (Exception x) {
					handleException(x);
				}

				return true;
			} else {
				record = null;
				return false;
			}
		}
		record = null;
		return false;
	}

	@Override
	public Object next() {
		return record;
	}

	@Override
	public void setReference(ILiteratureEntry reference) {
	}

	@Override
	public ILiteratureEntry getReference() {
		return null;
	}

	@Override
	public IStructureRecord nextRecord() {
		return record;
	}

	public SubstanceRecord parseSubstance(Reader reader) throws Exception {
		ObjectMapper dx = new ObjectMapper();
		try {
			JsonNode root = dx.readTree(reader);
			JsonNode substance = root.get("substance");
			if (substance instanceof ArrayNode) {
				SubstanceRecord record = parseSubstance(substance.get(0));
				JsonNode papps = substance.get(0).get("study");
				record.setMeasurements(parseProtocolApplications(papps, record));
				JsonNode composition = substance.get(0).get("composition");
				record.setRelatedStructures(parseComposition(composition));
				return record;
			}
			return null;
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception x) {
			}
		}
	}

	public SubstanceRecord parseSubstance(JsonNode node) {
		if (node == null)
			return null;
		SubstanceRecord record = new SubstanceRecord();
		record.setSubstanceName(node.get(
				SubstanceRecord.jsonSubstance.name.name()).textValue());
		record.setSubstanceUUID(node.get(
				SubstanceRecord.jsonSubstance.i5uuid.name()).textValue());
		record.setOwnerName(node.get(
				SubstanceRecord.jsonSubstance.ownerName.name()).textValue());
		record.setOwnerUUID(node.get(
				SubstanceRecord.jsonSubstance.ownerUUID.name()).textValue());
		record.setPublicName(node.get(
				SubstanceRecord.jsonSubstance.publicname.name()).textValue());
		record.setSubstancetype(node.get(
				SubstanceRecord.jsonSubstance.substanceType.name())
				.textValue());
		record.setFormat(node.get(SubstanceRecord.jsonSubstance.format.name())
				.textValue());
		JsonNode subnode = node
				.get(SubstanceRecord.jsonSubstance.externalIdentifiers.name());
		if (subnode instanceof ArrayNode) {
			ArrayNode ids = (ArrayNode) subnode;
			List<ExternalIdentifier> extids = new ArrayList<ExternalIdentifier>();
			record.setExternalids(extids);
			for (int i = 0; i < ids.size(); i++) {
				if (ids.get(i) instanceof ObjectNode) {
					extids.add(new ExternalIdentifier(((ObjectNode) ids.get(i))
							.get("type").textValue(), ((ObjectNode) ids
							.get(i)).get("id").textValue()));
				}
			}
		}
		subnode = node.get(SubstanceRecord.jsonSubstance.referenceSubstance
				.name());
		if (subnode != null) {
			record.setReferenceSubstanceUUID(subnode.get(
					SubstanceRecord.jsonSubstance.i5uuid.name()).textValue());
		}
		return record;
	}

	/**
	 * TODO
	 * 
	 * @param node
	 * @return
	 */
	public List<CompositionRelation> parseComposition(JsonNode node) {
		// if (node==null)
		return null;
	}

	public List<ProtocolApplication> parseProtocolApplication(Reader reader,
			SubstanceRecord record) throws Exception {

		ObjectMapper dx = new ObjectMapper();
		try {
			JsonNode root = dx.readTree(reader);
			JsonNode papps = root.get("study");
			return parseProtocolApplications(papps, record);
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception x) {
			}
		}
	}

	public List<ProtocolApplication> parseProtocolApplications(JsonNode papps,
			SubstanceRecord record) throws Exception {
		if (papps == null)
			return null;
		List<ProtocolApplication> list = new ArrayList<ProtocolApplication>();
		if (papps instanceof ArrayNode) {
			ArrayNode a = (ArrayNode) papps;
			for (int i = 0; i < a.size(); i++) {
				if (a.get(i) instanceof ObjectNode) {
					ProtocolApplication p = parseProtocolApplication((ObjectNode) a
							.get(i));
					p.setSubstanceUUID(record.getSubstanceUUID());
					if (p.getDocumentUUID() == null)
						generateDocumentUUID(p);
					list.add(p);
				}
			}
		} else if (papps instanceof ObjectNode) {
			ProtocolApplication p = parseProtocolApplication((ObjectNode) papps);
			p.setSubstanceUUID(record.getSubstanceUUID());
			if (p.getDocumentUUID() == null)
				generateDocumentUUID(p);
			list.add(p);
		}
		return list;

	}

	public ProtocolApplication parseProtocolApplication(ObjectNode node) {
		if (node == null)
			return null;
		ProtocolApplication pa = null;
		if (node.get("effects_to_delete") == null) {
			Protocol protocol = parseProtocol((ObjectNode) node
					.get(ProtocolApplication._fields.protocol.name()));
			pa = new ProtocolApplication(protocol);
			JsonNode docuuid = node
					.get(ProtocolApplication._fields.uuid.name());
			pa.setDocumentUUID(docuuid == null ? null : docuuid.textValue());
			parseOwner((ObjectNode) node.get(ProtocolApplication._fields.owner
					.name()), pa);
			parseStudyReference(
					(ObjectNode) node.get(ProtocolApplication._fields.citation
							.name()), pa);
			parseReliability(
					(ObjectNode) node.get(ProtocolApplication._fields.reliability
							.name()), pa);
			parseInterpretation(
					(ObjectNode) node.get(ProtocolApplication._fields.interpretation
							.name()), pa);
			pa.setEffects(parseEffects((ArrayNode) node
					.get(ProtocolApplication._fields.effects.name()), protocol));
			pa.setParameters(parseParams((ObjectNode) node
					.get(ProtocolApplication._fields.parameters.name())));
		} else {
			Protocol protocol = parseProtocol((ObjectNode) node
					.get(ProtocolApplication._fields.protocol.name()));
			pa = new ProtocolApplicationAnnotated(protocol);
			((ProtocolApplicationAnnotated) pa)
					.setRecords_to_delete(parseValuesAnnotated(
							(ArrayNode) node.get("effects_to_delete"), protocol));
		}
		return pa;

	}

	// ProtocolApplicationAnnotated

	protected void parseReliability(ObjectNode node, ProtocolApplication record) {
		if (node == null)
			return;
		ReliabilityParams reliability = new ReliabilityParams();

		JsonNode jn = node.get(_FIELDS_RELIABILITY.r_isUsedforMSDS.name());
		try {
			reliability.setIsUsedforMSDS(jn.asBoolean());
		} catch (Exception x) {
		}
		jn = node.get(_FIELDS_RELIABILITY.r_isRobustStudy.name());
		try {
			reliability.setIsRobustStudy(jn.asBoolean());
		} catch (Exception x) {
		}
		jn = node.get(_FIELDS_RELIABILITY.r_isUsedforClassification.name());
		try {
			reliability.setIsUsedforClassification(jn.asBoolean());
		} catch (Exception x) {
		}
		jn = node.get(_FIELDS_RELIABILITY.r_purposeFlag.name());
		try {
			reliability.setPurposeFlag(jn.textValue());
		} catch (Exception x) {
		}
		jn = node.get(_FIELDS_RELIABILITY.r_studyResultType.name());
		try {
			reliability.setStudyResultType(jn.textValue());
		} catch (Exception x) {
		}
		jn = node.get(_FIELDS_RELIABILITY.r_value.name());
		try {
			reliability.setValue(jn.textValue());
		} catch (Exception x) {
		}
		record.setReliability(reliability);
	}

	protected void parseStudyReference(ObjectNode node,
			ProtocolApplication record) {
		if (node == null)
			return;
		JsonNode jn = node.get("title");
		record.setReference(jn.textValue());
		jn = node.get("owner");
		if (jn != null)
			record.setReferenceOwner(jn.textValue());
		jn = node.get("year");
		if (jn != null)
			record.setReferenceYear(jn.textValue());
	}

	protected void parseCompany(ObjectNode node, ProtocolApplication record) {
		if (node == null)
			return;
		JsonNode jn = node.get(ProtocolApplication._fields.uuid.name());
		if (jn != null)
			record.setCompanyUUID(jn.textValue());
		jn = node.get(ProtocolApplication._fields.name.name());
		if (jn != null)
			record.setCompanyName(jn.textValue());
	}

	public void parseOwner(ObjectNode node, ProtocolApplication record) {
		if (node == null)
			return;
		parseSubstance(
				(ObjectNode) node.get(ProtocolApplication._fields.substance
						.name()), record);
		parseCompany((ObjectNode) node.get(ProtocolApplication._fields.company
				.name()), record);
	}

	protected void parseSubstance(ObjectNode node, ProtocolApplication record) {
		if (node == null)
			return;
		JsonNode jn = node.get(ProtocolApplication._fields.uuid.name());
		if (jn != null) {
			record.setSubstanceUUID(jn.textValue());
		}
	}

	public void parseInterpretedResult(ObjectNode node,
			ProtocolApplication record) {
		if (node == null)
			return;
		JsonNode jn = node.get(ProtocolApplication._fields.result.name());
		if (jn != null) {
			record.setInterpretationResult(jn.textValue());
		}
	}

	public void parseInterpretationCriteria(ObjectNode node,
			ProtocolApplication record) {
		if (node == null)
			return;
		JsonNode jn = node.get(ProtocolApplication._fields.criteria.name());
		if (jn != null) {
			record.setInterpretationCriteria(jn.textValue());
		}
	}

	public void parseInterpretation(ObjectNode node, ProtocolApplication record) {
		if (node == null)
			return;
		parseInterpretedResult(node, record);
		parseInterpretationCriteria(node, record);
	}

	public static IParams parseParams(ObjectNode node) {
		if (node == null)
			return null;
		IParams params = new Params();
		Iterator<Entry<String, JsonNode>> i = node.fields();
		while (i.hasNext()) {
			Entry<String, JsonNode> val = i.next();
			JsonNode value = val.getValue();
			if (value instanceof ObjectNode) {
				ObjectNode range = (ObjectNode) value;
				Value rvalue = new Value();
				rvalue.setLoQualifier(null);
				rvalue.setUpQualifier(null);
				rvalue.setLoValue(null);
				rvalue.setUpValue(null);
				rvalue.setUnits(null);
				rvalue.setErrQualifier(null);
				rvalue.setErrValue(null);

				JsonNode jn = range.get(EffectRecord._fields.textValue.name());
				if (jn != null && !"".equals(jn.textValue()))
					params.put(val.getKey(), jn.textValue());
				else {
					jn = range.get(EffectRecord._fields.loQualifier.name());
					if (jn != null) {
						if (!"".equals(jn.textValue()))
							rvalue.setLoQualifier(jn.textValue());
					}
					jn = range.get(EffectRecord._fields.loValue.name());
					if (jn != null && !"".equals(jn.textValue()))
						rvalue.setLoValue(jn.asDouble());

					jn = range.get(EffectRecord._fields.upQualifier.name());
					if (jn != null) {
						if (!"".equals(jn.textValue()))
							rvalue.setUpQualifier(jn.textValue());
					}
					jn = range.get(EffectRecord._fields.upValue.name());
					if (jn != null && !"".equals(jn.textValue()))
						rvalue.setUpValue(jn.asDouble());

					jn = range.get(EffectRecord._fields.unit.name());
					if (jn != null && !"".equals(jn.textValue())
							&& !"null".equals(jn.textValue())) {
						rvalue.setUnits(jn.textValue());
					}

                    jn = range.get(EffectRecord._fields.errQualifier.name());
                    if (jn != null) {
                        if (!"".equals(jn.textValue()))
                            rvalue.setErrQualifier(jn.textValue());
                    }
					
                    jn = range.get(EffectRecord._fields.errorValue.name());
                    if (jn != null && !"".equals(jn.textValue()))
                        rvalue.setErrValue(jn.asDouble());
                    
					params.put(val.getKey(), rvalue);

				}
			} else
				params.put(val.getKey(), value.textValue());
		}
		return params;
	}

	public Protocol parseProtocol(ObjectNode node) {
		if (node == null)
			return null;
		JsonNode endpointnode = node.get(Protocol._fields.endpoint.name());
		Protocol p = new Protocol(endpointnode == null ? null
				: endpointnode.textValue());
		JsonNode jn = node.get(Protocol._fields.topcategory.name());
		if (jn != null && !"".equals(jn.textValue())
				&& !"null".equals(jn.textValue()))
			p.setTopCategory(jn.textValue());
		jn = node.get(Protocol._fields.category.name()).get("code");
		if (jn != null && !"".equals(jn.textValue())
				&& !"null".equals(jn.textValue()))
			p.setCategory(jn.textValue());
		ArrayNode guidance = (ArrayNode) node.get(Protocol._fields.guideline
				.name());
		if (guidance != null)
			for (int i = 0; i < guidance.size(); i++) {
				p.addGuideline(guidance.get(i).textValue());
			}
		return p;
	}

	protected EffectRecord createEffectRecord(Protocol protocol) {
		return new EffectRecord();
	}

	public List<EffectRecord> parseEffects(ArrayNode node, Protocol protocol) {
		if (node == null || node.size() == 0)
			return null;
		List<EffectRecord> effects = new ArrayList<EffectRecord>();
		for (int i = 0; i < node.size(); i++) {
			EffectRecord record = createEffectRecord(protocol);
			JsonNode jn = node.get(i).get(EffectRecord._fields.endpoint.name());
			if (jn != null)
				record.setEndpoint(jn.textValue());
			record.setConditions(parseParams((ObjectNode) node.get(i).get(
					EffectRecord._fields.conditions.name())));
			parseResult(
					(ObjectNode) node.get(i).get(
							EffectRecord._fields.result.name()), record);
			effects.add(record);
		}
		return effects;

	}

	public void parseResult(ObjectNode node, EffectRecord record) {
		if (node == null)
			return;
		JsonNode jn = node.get(EffectRecord._fields.loQualifier.name());
		if (jn != null) {
			if (!"".equals(jn.textValue()))
				record.setLoQualifier(jn.textValue());
		}
		jn = node.get(EffectRecord._fields.loValue.name());
		if (jn != null && !"".equals(jn.textValue()))
			record.setLoValue(jn.asDouble());
		jn = node.get(EffectRecord._fields.upQualifier.name());
		if (jn != null) {
			if (!"".equals(jn.textValue()))
				record.setUpQualifier(jn.textValue());
		}
		jn = node.get(EffectRecord._fields.upValue.name());
		if (jn != null && !"".equals(jn.textValue()))
			record.setUpValue(jn.asDouble());

		jn = node.get(EffectRecord._fields.unit.name());
		if (jn != null && !"".equals(jn.textValue())
				&& !"null".equals(jn.textValue())) {
			record.setUnit(jn.textValue());
		}
		jn = node.get(EffectRecord._fields.textValue.name());
		if (jn != null && !"".equals(jn.textValue())
				&& !"null".equals(jn.textValue())) {
			record.setTextValue(jn.textValue());
		}

	}

	public void parseValueAnnotated(ObjectNode node, ValueAnnotated record) {
		if (node == null)
			return;
		JsonNode jn = node.get(ValueAnnotated._fields.idresult.name());
		if (jn != null) {
			int ivalue = jn.asInt(-1);
			record.setIdresult(ivalue>0?ivalue:jn.asText());
		}	
		jn = node.get(ValueAnnotated._fields.deleted.name());
		if (jn != null)
			record.setDeleted(jn.asBoolean(true));
		jn = node.get(ValueAnnotated._fields.remarks.name());
		if (jn != null)
			record.setRemark(jn.textValue());
	}

	public List<ValueAnnotated> parseValuesAnnotated(ArrayNode node,
			Protocol protocol) {
		if (node == null || node.size() == 0)
			return null;
		List<ValueAnnotated> effects = new ArrayList<ValueAnnotated>();
		for (int i = 0; i < node.size(); i++) {
			ValueAnnotated record = new ValueAnnotated();
			parseValueAnnotated(
					(ObjectNode) node.get(i).get(
							EffectRecord._fields.result.name()), record);
			effects.add(record);
		}
		return effects;

	}
	
	public static IParams parseConditions(ObjectMapper dx,Object json) {
		if (json != null)
			try {

				JsonNode conditions = dx.readTree(new StringReader(json
						.toString()));
				if (conditions instanceof ObjectNode) {
					return SubstanceStudyParser
							.parseParams((ObjectNode) conditions);
				}
			} catch (Exception x) {
				logger.log(Level.FINE, x.getMessage());
			}
		return null;
	}
	
	public static IParams parseTextValueProteomics(ObjectMapper dx, String t) {
		IParams nonzero = null;
		if (t != null && t.startsWith("{")) {
			IParams proteomics = SubstanceStudyParser.parseConditions(dx,t.toString());
			Iterator i = proteomics.keySet().iterator();
			nonzero = new Params();
			while (i.hasNext()) {
				Object p = i.next();
				try {
					Value node = (Value) proteomics.get(p);
					if (node != null && (node.getLoValue() != null)
							&& (node.getLoValue() instanceof Double)
							&& ((Double) node.getLoValue()) > 0) {
						nonzero.put(p, node.getLoValue());
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
		return nonzero;
	}
}
