package ambit2.base.data.study;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.json.JSONUtils;

public class Protocol {
	String category;
	String endpoint;
	List<String> guidance;
	
	public static enum _fields {
		topcategory,
		category,
		endpoint,
		guidance
	}

	protected String topCategory;	
	public String getTopCategory() {
		return topCategory;
	}
	public void setTopCategory(String topCategory) {
		this.topCategory = topCategory;
	}

	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

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
		b.append("{");

		b.append("\n\t");
		b.append(JSONUtils.jsonQuote(_fields.topcategory.name()));		
		b.append(":");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(topCategory)));
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.category.name()));		
		b.append(":");		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(category)));
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.endpoint.name()));		
		b.append(":");		
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint)));
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.guidance.name()));		
		b.append(": [");				
		if (guidance!=null)
			for (int i=0; i < guidance.size(); i++) {
				if (i>0) b.append(",");
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(guidance.get(i))));
			}	
		b.append("]}");
		return b.toString();
	}

}
