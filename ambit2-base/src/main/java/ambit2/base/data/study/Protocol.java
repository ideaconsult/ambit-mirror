package ambit2.base.data.study;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.json.JSONUtils;

public class Protocol {
	String category;
	String endpoint;
	List<String> guidance;
	//hack for IUCLID categories, as i can't find the exact titles in the schema :(
	private enum _categories {
		PC_PARTITION_SECTION {
			@Override
			public String toString() {
				return "Partition coeffcicient";
			}
		},
		TO_BIODEG_WATER_SCREEN_SECTION {
			@Override
			public String toString() {
				return "Biodegradation in water - screening tests";
			}
		},
		TO_ACUTE_ORAL_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity - oral";
			}
		},
		EC_FISHTOX_SECTION {
			@Override
			public String toString() {
				return "Short-term toxicity to fish";
			}
		}				
	}
	
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
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(topCategory)));
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.category.name()));		
		b.append(": {");
			b.append(JSONUtils.jsonQuote("code"));b.append(": ");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(category)));
			b.append(",");
			b.append(JSONUtils.jsonQuote("title"));b.append(": ");
			try {
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_categories.valueOf(category).toString())));
			} catch (Exception x) {
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(category)));
			}
		b.append("},\n\t");
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
