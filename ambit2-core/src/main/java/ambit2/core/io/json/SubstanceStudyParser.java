package ambit2.core.io.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.ReliabilityParams;
import ambit2.base.data.study._FIELDS_RELIABILITY;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.interfaces.ICiteable;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.io.IRawReader;

public class SubstanceStudyParser extends DefaultIteratingChemObjectReader implements IRawReader<IStructureRecord>, ICiteable {

	protected ObjectMapper dx = new ObjectMapper();
	protected ArrayNode substance;
	protected int index = -1;
	protected SubstanceRecord record = null; 
	
	public SubstanceStudyParser(InputStreamReader reader) throws Exception {
		super();
		setReader(reader);
	}
	

	@Override
	public void setReader(Reader reader) throws CDKException {
		try {
			JsonNode node = dx.readTree(reader).get("substance");
			if (node instanceof ArrayNode) {
				substance = (ArrayNode)node;
				index = -1;
			}
			else substance = null;
		} catch (Exception x ) {
			throw new CDKException(x.getMessage(),x);
		} finally {
			try { reader.close();} catch (Exception x) {}
		}
	}

	@Override
	public void setReader(InputStream in) throws CDKException {
		try {
			setReader(new InputStreamReader(in,"UTF-8"));
		} catch (Exception x) {
			throw new CDKException(x.getMessage(),x);
		}
		
	}

	@Override
	public IResourceFormat getFormat() {
		return null;
	}

	

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public boolean hasNext() {
		index++;
		if (index<substance.size()) {
			record = parseSubstance(substance.get(index));
			JsonNode papps = substance.get(index).get("study");
			record.setMeasurements(parseProtocolApplications(papps));
			JsonNode composition = substance.get(0).get("composition");
			record.setRelatedStructures(parseComposition(composition));
			return true;
		} else {
			record = null;
			return false;
		}
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
	
	public static SubstanceRecord parseSubstance(Reader reader) throws Exception {
		ObjectMapper dx = new ObjectMapper();
		try {
			JsonNode root = dx.readTree(reader);
			JsonNode substance = root.get("substance");
			if (substance instanceof ArrayNode) {
				SubstanceRecord record = parseSubstance(substance.get(0));
				JsonNode papps = substance.get(0).get("study");
				record.setMeasurements(parseProtocolApplications(papps));
				JsonNode composition = substance.get(0).get("composition");
				record.setRelatedStructures(parseComposition(composition));
				return record;
			}
			return null;
		} catch (Exception x) {
			throw x;
		} finally {
 			try { if (reader!=null) reader.close();} catch (Exception x) {}
		}
	}
	public static SubstanceRecord parseSubstance(JsonNode node) {
		if (node==null) return null;
		SubstanceRecord record = new SubstanceRecord();
		record.setCompanyName(node.get(SubstanceRecord.jsonSubstance.name.name()).getTextValue());
		record.setCompanyUUID(node.get(SubstanceRecord.jsonSubstance.i5uuid.name()).getTextValue());
		record.setOwnerName(node.get(SubstanceRecord.jsonSubstance.ownerName.name()).getTextValue());
		record.setOwnerUUID(node.get(SubstanceRecord.jsonSubstance.ownerUUID.name()).getTextValue());
		record.setPublicName(node.get(SubstanceRecord.jsonSubstance.publicname.name()).getTextValue());
		record.setSubstancetype(node.get(SubstanceRecord.jsonSubstance.substanceType.name()).getTextValue());
		record.setFormat(node.get(SubstanceRecord.jsonSubstance.format.name()).getTextValue());
		JsonNode subnode = node.get(SubstanceRecord.jsonSubstance.externalIdentifiers.name());
		if (subnode instanceof ArrayNode) {
			ArrayNode ids = (ArrayNode)subnode;
			List<ExternalIdentifier> extids = new ArrayList<ExternalIdentifier>();
			record.setExternalids(extids);
			for (int i=0; i < ids.size(); i++) {
				if (ids.get(i) instanceof ObjectNode) {
					extids.add(new ExternalIdentifier(
							((ObjectNode) ids.get(i)).get("type").getTextValue(),
							((ObjectNode) ids.get(i)).get("id").getTextValue()
							));
				}
			}
		}
		subnode = node.get(SubstanceRecord.jsonSubstance.referenceSubstance.name());
		if (subnode!=null) {
			record.setReferenceSubstanceUUID(subnode.get(SubstanceRecord.jsonSubstance.i5uuid.name()).getTextValue());	
		}
		return record;
	}	
	/**
	 * TODO
	 * @param node
	 * @return
	 */
	public static List<CompositionRelation> parseComposition(JsonNode node) {
		//if (node==null) 
			return null;
	}
	public static List<ProtocolApplication> parseProtocolApplication(Reader reader) throws Exception{
		
		ObjectMapper dx = new ObjectMapper();
		try {
			JsonNode root = dx.readTree(reader);
			JsonNode papps = root.get("study");
			return parseProtocolApplications(papps);
		} catch (Exception x) {
			throw x;
		} finally {
 			try { if (reader!=null) reader.close();} catch (Exception x) {}
		}	
	}
	
	
	public static List<ProtocolApplication> parseProtocolApplications(JsonNode papps) {
		if (papps==null) return null;
		List<ProtocolApplication> list = new ArrayList<ProtocolApplication>();
		if (papps instanceof ArrayNode) {
			ArrayNode a = (ArrayNode)papps;
			for (int i=0; i < a.size(); i++) {
				if (a.get(i) instanceof ObjectNode) {
					list.add(SubstanceStudyParser.parseProtocolApplication((ObjectNode)a.get(i)));
				}
			}
		}
		return list;

	}
	public static ProtocolApplication parseProtocolApplication(ObjectNode node) {
		if (node==null) return null;
		Protocol protocol = parseProtocol((ObjectNode)node.get(ProtocolApplication._fields.protocol.name()));
		ProtocolApplication pa = new ProtocolApplication(protocol);
		pa.setDocumentUUID(node.get(ProtocolApplication._fields.uuid.name()).getTextValue());
		parseOwner((ObjectNode)node.get(ProtocolApplication._fields.owner.name()),pa);
		parseStudyReference((ObjectNode)node.get(ProtocolApplication._fields.citation.name()), pa);
		parseReliability((ObjectNode)node.get(ProtocolApplication._fields.reliability.name()), pa);
		parseInterpretation((ObjectNode)node.get(ProtocolApplication._fields.interpretation.name()),pa);
		pa.setEffects(parseEffects((ArrayNode)node.get(ProtocolApplication._fields.effects.name())));
		pa.setParameters(parseParams((ObjectNode)node.get(ProtocolApplication._fields.parameters.name())));
		return pa;
		
	}

	protected static void parseReliability(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
		ReliabilityParams reliability = new ReliabilityParams();
		
		JsonNode jn = node.get(_FIELDS_RELIABILITY.r_isUsedforMSDS.getTag());
		reliability.setIsUsedforMSDS(jn.asBoolean());
		jn = node.get(_FIELDS_RELIABILITY.r_isRobustStudy.getTag());
		reliability.setIsRobustStudy(jn.asBoolean());
		jn = node.get(_FIELDS_RELIABILITY.r_isUsedforClassification.getTag());
		reliability.setIsUsedforClassification(jn.asBoolean());
		jn = node.get(_FIELDS_RELIABILITY.r_purposeFlag.getTag());
		reliability.setPurposeFlag(jn.getTextValue());
		jn = node.get(_FIELDS_RELIABILITY.r_studyResultType.getTag());
		reliability.setStudyResultType(jn.getTextValue());
		jn = node.get(_FIELDS_RELIABILITY.r_value.getTag());
		reliability.setValue(jn.getTextValue());
		record.setReliability(reliability);
	}
	protected static void parseStudyReference(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
		JsonNode jn = node.get("title");
		record.setReference(jn.getTextValue());
		jn = node.get("owner");
		if (jn!= null) record.setReferenceOwner(jn.getTextValue());
		jn = node.get("year");
		if (jn!=null)
			record.setReferenceYear(jn.getTextValue());
	}

	
	protected static void parseCompany(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
		JsonNode jn = node.get(ProtocolApplication._fields.uuid.name());
		if (jn!=null) record.setCompanyUUID(jn.getTextValue());
		jn = node.get(ProtocolApplication._fields.name.name());
		if (jn!=null) record.setCompanyName(jn.getTextValue());
	}
	public static void parseOwner(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
		parseSubstance((ObjectNode)node.get(ProtocolApplication._fields.substance.name()), record);
		parseCompany((ObjectNode)node.get(ProtocolApplication._fields.company.name()), record);
	}
	protected static void parseSubstance(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
		JsonNode jn = node.get(ProtocolApplication._fields.uuid.name());
		if (jn!=null) {
			record.setSubstanceUUID(jn.getTextValue());
		}
	}	
	public static void parseInterpretedResult(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
			JsonNode jn = node.get(ProtocolApplication._fields.result.name());
		if (jn!=null) {
			record.setInterpretationResult(jn.getTextValue());
		}
	}	
	public static void parseInterpretationCriteria(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
			JsonNode jn = node.get(ProtocolApplication._fields.criteria.name());
		if (jn!=null) {
			record.setInterpretationCriteria(jn.getTextValue());
		}
	}	
	public static void parseInterpretation(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
		parseInterpretedResult(node, record);
		parseInterpretationCriteria(node, record);
	}		
	public static IParams parseParams(ObjectNode node) {
		if (node==null) return null;
		IParams params = new Params();
		Iterator<Entry<String,JsonNode>> i = node.getFields();
		while (i.hasNext()) {
			Entry<String,JsonNode> val = i.next();
			params.put(val.getKey(),val.getValue().getTextValue());
		}
		return params;
	}
	public static Protocol parseProtocol(ObjectNode node) {
		if (node==null) return null;
		Protocol p = new Protocol(node.get(Protocol._fields.endpoint.name()).getTextValue());
		JsonNode jn = node.get(Protocol._fields.topcategory.name());
		if (jn!=null && !"".equals(jn.getTextValue()) && !"null".equals(jn.getTextValue())) 
			p.setTopCategory(jn.getTextValue());	
		jn = node.get(Protocol._fields.category.name()).get("code");
		if (jn!=null && !"".equals(jn.getTextValue()) && !"null".equals(jn.getTextValue())) 
			p.setCategory(jn.getTextValue());
		ArrayNode guidance = (ArrayNode)node.get(Protocol._fields.guideline.name());
		if (guidance!=null)
		for (int i=0; i < guidance.size();i++) {
			p.addGuideline(guidance.get(i).getTextValue());
		}
		return p;
	}
	public static List<EffectRecord> parseEffects(ArrayNode node) {
		if(node==null || node.size()==0) return null;
		List<EffectRecord> effects = new ArrayList<EffectRecord>();
		for (int i=0; i< node.size(); i++) {
			EffectRecord record = new EffectRecord();
			JsonNode jn = node.get(i).get(EffectRecord._fields.endpoint.name());
			if (jn!=null)
				record.setEndpoint(jn.getTextValue());
			record.setConditions(parseParams((ObjectNode)node.get(i).get(EffectRecord._fields.conditions.name())));
			parseResult((ObjectNode)node.get(i).get(EffectRecord._fields.result.name()),record);
			effects.add(record);
		}
		return effects;

	}
	public static void parseResult(ObjectNode node, EffectRecord record) {
		if (node==null) return ;
		JsonNode jn = node.get(EffectRecord._fields.loQualifier.name());
		if (jn!=null) {
			if (!"".equals(jn.getTextValue()))
					record.setLoQualifier(jn.getTextValue());
		}
		jn = node.get(EffectRecord._fields.loValue.name());
		if (jn!=null) record.setLoValue(jn.asDouble());
		jn = node.get(EffectRecord._fields.upQualifier.name());
		if (jn!=null) {
			if (!"".equals(jn.getTextValue()))
					record.setUpQualifier(jn.getTextValue());
		}
		jn = node.get(EffectRecord._fields.upValue.name());
		if (jn!=null) record.setUpValue(jn.asDouble());
		
		jn = node.get(EffectRecord._fields.unit.name());
		if (jn!=null && !"".equals(jn.getTextValue()) && !"null".equals(jn.getTextValue())) {
			record.setUnit(jn.getTextValue());
		}	
	}
}
