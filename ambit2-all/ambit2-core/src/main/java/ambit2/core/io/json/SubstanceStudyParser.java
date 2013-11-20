package ambit2.core.io.json;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;

public class SubstanceStudyParser {
	private SubstanceStudyParser() {
	}
	public static List<ProtocolApplication> parseProtocolApplication(InputStream in) throws Exception{
		List<ProtocolApplication> list = new ArrayList<ProtocolApplication>();
		ObjectMapper dx = new ObjectMapper();
		try {
			JsonNode root = dx.readTree(in);
			JsonNode papps = root.get("protocolapplication");
			if (papps instanceof ArrayNode) {
				ArrayNode a = (ArrayNode)papps;
				for (int i=0; i < a.size(); i++) {
					if (a.get(i) instanceof ObjectNode) {
						list.add(SubstanceStudyParser.parseProtocolApplication((ObjectNode)a.get(i)));
					}
				}
			}
			return list;
		} catch (Exception x) {
			throw x;
		} finally {
 			try { if (in!=null) in.close();} catch (Exception x) {}
		}
	}
	public static ProtocolApplication parseProtocolApplication(ObjectNode node) {
		Protocol protocol = parseProtocol((ObjectNode)node.get(ProtocolApplication._fields.protocol.name()));
		ProtocolApplication pa = new ProtocolApplication(protocol);
		pa.setDocumentUUID(node.get(ProtocolApplication._fields.uuid.name()).getTextValue());
		parseOwner((ObjectNode)node.get(ProtocolApplication._fields.owner.name()),pa);
		pa.setEffects(parseEffects((ArrayNode)node.get(ProtocolApplication._fields.effects.name())));
		pa.setParameters(parseParams((ObjectNode)node.get(ProtocolApplication._fields.parameters.name())));
		return pa;
		
	}
	public static void parseOwner(ObjectNode node, ProtocolApplication record) {
		if (node==null) return ;
		JsonNode jn = node.get(ProtocolApplication._fields.substanceuuid.name());
		if (jn!=null) {
			record.setSubstanceUUID(jn.getTextValue());
		}
	}		
	public static Params parseParams(ObjectNode node) {
		if (node==null) return null;
		Params params = new Params();
		Iterator<Entry<String,JsonNode>> i = node.getFields();
		while (i.hasNext()) {
			Entry<String,JsonNode> val = i.next();
			params.put(val.getKey(),val.getValue().getTextValue());
		}
		return params;
	}
	public static Protocol parseProtocol(ObjectNode node) {
		if (node==null) return null;
		Protocol p = new Protocol(node.get(Protocol._fields.endpoint.name()).asText());
		JsonNode jn = node.get(Protocol._fields.topcategory.name());
		if (jn!=null && !"".equals(jn.getTextValue()) && !"null".equals(jn.asText())) 
			p.setTopCategory(jn.asText());	
		jn = node.get(Protocol._fields.category.name());
		if (jn!=null && !"".equals(jn.getTextValue()) && !"null".equals(jn.asText())) 
			p.setCategory(jn.asText());
		ArrayNode guidance = (ArrayNode)node.get(Protocol._fields.guidance.name());
		if (guidance!=null)
		for (int i=0; i < guidance.size();i++) {
			p.addGuidance(guidance.get(i).asText());
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
		if (jn!=null && !"".equals(jn.getTextValue()) && !"null".equals(jn.asText())) {
			record.setUnit(jn.asText());
		}	
	}	
}
