package ambit2.base.data.study;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.json.JSONUtils;

public class Protocol {
	String endpoint;
	List<String> guidance;
	
	public List<String> getGuidance() {
		return guidance;
	}
	public void setGuidance(List<String> guidance) {
		this.guidance = guidance;
	}
	public void addGuidance(String guidance) {
		if (this.guidance==null) this.guidance = new ArrayList<String>();
		this.guidance.add(guidance);
	}
	public Protocol(String endpoint) {
		this(endpoint,null);
	}
	public Protocol(String endpoint, String guidance) {
		setEndpoint(endpoint);
		if (guidance!=null) addGuidance(guidance);
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{\"endpoint\":");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint)));
		b.append(",\"guidance\": [");
		if (guidance!=null)
			for (int i=0; i < guidance.size(); i++) {
				if (i>0) b.append(",");
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(guidance.get(i))));
			}	
		b.append("]}");
		return b.toString();
	}

}
