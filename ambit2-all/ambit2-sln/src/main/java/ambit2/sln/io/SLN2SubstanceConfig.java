package ambit2.sln.io;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class SLN2SubstanceConfig 
{
	public static class ConversionFlags 
	{
		//Conversion flags 
		public boolean proportion = true;
		public boolean compositionUUID = true;		
		public boolean smiles = true;
		public boolean inchi = true;
		public boolean inchiKey = true;
		public boolean relation = true;
		public boolean content = true;
		public boolean properties = true;
		public boolean strType = true;
		public boolean facets = true;
		public boolean relationMetric = true;
		public boolean relationType = true;

		/*
			public boolean FlagReference = false;
			public boolean FlagSelected = false;
			public boolean FlagDataEntryID = false;
			public boolean FlagId_srcdataset = false;
			public boolean FlagIdchemical = false;
			public boolean FlagIdstructure = false;
		 */

	}	
		
	public ConversionFlags conversion = new ConversionFlags(); 

	//Conversion attribute names for CompositionRelation fields
	public String proportion_SLNAttr = "proportion";
	public String compositionUUID_SLNAttr = "compositionUUID";
	public String name_SLNAttr = "name";
	public String relationMetric_SLNAttr = "proportion";  //the field name in class CompositionRelation is "relation"
	public String relationType_SLNAttr = "role";

	//Conversion attribute names for Structure Record fields
	public String inchiKey_SLNAttr = "inchiKey";
	public String formula_SLNAttr = "formula";
	public String idchemical_SLNAttr = "idchemical";
	public String idstructure_SNLAttr = "idstricture";
	public String content_SNLAttr = "content";
	public String format_SLNAttr = "format";
	public String reference_SLNAttr = "reference";
	public String properties_SLNAttr = "properties";
	public String type_SLNAttr = "type";

	/*
		public String selected_SLNAttr = "selected";
		public String facets_SLNAttr = "facets";
		public String dataEntryID_SLNAttr = "dataEntryID";
		public String id_srcdataset_SLNAttr = "id_srcdataset";
	 */

	public boolean FlagAddImplicitHAtomsOnSLNAtomConversion = false;

	//Used only for saving configuration as a JSON
	public JsonFlags jsonFlags = new JsonFlags(); 
	
	public static class JsonFlags 
	{
		public boolean proportion = false;
		public boolean compositionUUID = false;		
		public boolean smiles = false;
		public boolean inchi = false;
		public boolean inchiKey = false;
		public boolean relation = false;
		public boolean content = false;
		public boolean properties = false;
		public boolean strType = false;
		public boolean facets = false;
		public boolean relationMetric = false;
		public boolean relationType = false;
		
		
	}

	

	public static SLN2SubstanceConfig extractConfigFromJson(JsonNode node, List<String> errors)
	{
		SLN2SubstanceConfig cfg = new SLN2SubstanceConfig();

		//TODO

		return cfg;
	}

	public String toJSONKeyWord(String offset) {
		int nFields = 0;
		StringBuffer sb = new StringBuffer();
		sb.append(offset+"{"+"\n");

		//Conversion flags
		sb.append(offset+"\t\"CONVERSION_FLAGS\" :"+"\n");
		sb.append(offset+"\t{"+"\n");
		
		if(jsonFlags.proportion) {
			if (nFields > 0) {
				sb.append(",\n");
			}
			sb.append(offset +  "\t\t\"PROPORTION\" : " + conversion.proportion);
			nFields++;
		}
		
		sb.append("\n");
		sb.append(offset+"\t}"+"\n");
		
		
		if (nFields > 0)
			sb.append("\n");

		sb.append(offset +"}");

		return sb.toString();

	}



}
