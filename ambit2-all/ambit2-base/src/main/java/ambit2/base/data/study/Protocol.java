package ambit2.base.data.study;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.json.JSONUtils;

public class Protocol {
	String category;
	String endpoint;
	List<String> guideline;
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
		TO_REPEATED_ORAL_SECTION {
			@Override
			public String toString() {
				return "Repeated dose toxicity - oral";
			}
		},
		TO_SENSITIZATION_HUMAN_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation";
			}
		},
		TO_SENSITIZATION_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation";
			}
		},		
		TO_SKIN_IRRITATION_SECTION {
			@Override
			public String toString() {
				return "Skin irritation / Corrosion";
			}			
		},
		TO_EYE_IRRITATION_SECTION {
			@Override
			public String toString() {
				return "Eye irritation";
			}			
		},		
		TO_REPRODUCTION_SECTION {
			@Override
			public String toString() {
				return "Toxicity to reproduction";
			}
		},
		TO_GENETIC_IN_VITRO_SECTION {
			@Override
			public String toString() {
				return "Genetic toxicity in vitro";
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
		guideline
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

	public List<String> getGuideline() {
		return guideline;
	}
	public void setGuideline(List<String> guide) {
		this.guideline = guide;
	}
	public void addGuideline(String guide) {
		if (this.guideline==null) this.guideline = new ArrayList<String>();
		this.guideline.add(guide);
	}
	public Protocol(String endpoint) {
		this(endpoint,null);
	}
	public Protocol(String endpoint, String guideline) {
		setEndpoint(endpoint);
		if (guideline!=null) addGuideline(guideline);
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
		b.append(JSONUtils.jsonQuote(_fields.guideline.name()));		
		b.append(": [");				
		if (guideline!=null)
			for (int i=0; i < guideline.size(); i++) {
				if (i>0) b.append(",");
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(guideline.get(i))));
			}	
		b.append("]}");
		return b.toString();
	}

}
