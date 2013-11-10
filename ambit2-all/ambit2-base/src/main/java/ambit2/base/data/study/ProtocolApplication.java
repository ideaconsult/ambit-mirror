package ambit2.base.data.study;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.json.JSONUtils;

public class ProtocolApplication<PROTOCOL,PARAMS,ENDPOINT,CONDITIONS,UNIT> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 747315722852709360L;
	protected String documentUUID;
	public String getDocumentUUID() {
		return documentUUID;
	}
	public void setDocumentUUID(String documentUUID) {
		this.documentUUID = documentUUID;
	}

	protected PARAMS parameters;
	protected String reference;
	protected List<EffectRecord<ENDPOINT,CONDITIONS,UNIT>> effects;	
	
	protected PROTOCOL protocol;
	
	public ProtocolApplication(PROTOCOL protocol) {
		setProtocol(protocol);
	}

	public PROTOCOL getProtocol() {
		return protocol;
	}
	public void setProtocol(PROTOCOL protocol) {
		this.protocol = protocol;
	}
	public PARAMS getParameters() {
		return parameters;
	}
	public void setParameters(PARAMS parameters) {
		this.parameters = parameters;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public List<EffectRecord<ENDPOINT, CONDITIONS, UNIT>> getEffects() {
		return effects;
	}
	public void setEffects(List<EffectRecord<ENDPOINT, CONDITIONS, UNIT>> effects) {
		this.effects = effects;
	}
	public void addEffect(EffectRecord<ENDPOINT,CONDITIONS,UNIT> effect) {
		if (this.effects==null) effects = new ArrayList<EffectRecord<ENDPOINT,CONDITIONS,UNIT>>();
		effects.add(effect);
	}
	
	public void clear() {
		documentUUID = null;
		if (effects!=null) effects.clear();
		reference = null;
		parameters = null;
		protocol = null;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape("uuid")));
		b.append(":\t");
		b.append(getDocumentUUID()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getDocumentUUID().toString())));
		b.append(",\n");		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape("protocol")));
		b.append(":\t");
		b.append(protocol==null?null:protocol.toString());
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape("parameters")));
		b.append(":\t");		
		b.append(getParameters().toString());
		b.append(",\n");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape("effects")));
		b.append(":\t");
		b.append(getEffects()==null?null:getEffects().toString());
		b.append("\n}");
		return b.toString();
	}
}
