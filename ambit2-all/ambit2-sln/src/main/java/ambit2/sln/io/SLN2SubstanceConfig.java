package ambit2.sln.io;

public class SLN2SubstanceConfig 
{
	//Conversion flags 
	public boolean FlagProportion = true;
	public boolean FlagCompositionUUID = true;
	public boolean FlagSmiles = true;
	public boolean FlagInchi = true;
	public boolean FlagInchiKey = true;
	public boolean FlagRelation = true;
	public boolean FlagContent = true;
	public boolean FlagProperties = true;
	public boolean FlagStrType = true;
	public boolean FlagFacets = true;
	public boolean FlagRelationMetric = true;
	public boolean FlagRelationType = true;
	
	/*
		public boolean FlagReference = false;
		public boolean FlagSelected = false;
		public boolean FlagDataEntryID = false;
		public boolean FlagId_srcdataset = false;
		public boolean FlagIdchemical = false;
		public boolean FlagIdstructure = false;
	 */


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
}
