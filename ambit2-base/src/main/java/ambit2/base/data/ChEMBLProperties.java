package ambit2.base.data;

public class ChEMBLProperties {
	public static final String chembl = "ChEMBL";
	public static final String chemblSparql = "http://rdf.farmbio.uu.se/chembl/";
	
	public static final String chemblPrefix = "http://rdf.farmbio.uu.se/chembl/onto/#";
	public static final String dcPrefix = "http://purl.org/dc/elements/1.1/";
	public static final String biboPrefix = "http://purl.org/ontology/bibo/";
	
	
	public enum ChEMBL_Class {
		Compound,
		Activity,
		Target,
		Assay,
		Resource;  //journal or article
		
		public String getURI() {
			return String.format("%s%s",chemblPrefix,toString());
		}
		public String getCaption() {
			return String.format("chembl:%s",toString());
		}
	}
	
	public enum ChEMBL_Property {
		//Compound properties
		SMILES  {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Compound;}
			@Override
			public String getPrefix() {
				return "http://www.opentox.org/api/1.1#";
			}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}
		},
		compoundSameAs  {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Compound;}
			@Override
			public String getPrefix() {
				return "http://www.w3.org/2002/07/owl#sameAs";
			}			
		},	
		NAME  {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Compound;}
			@Override
			public String getPrefix() {
				return "http://www.opentox.org/api/1.1#";
			}
		},				
		assayOrganism  {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Assay;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},
			
		assayHasDescription  {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Assay;}
		},
		targetHasDescription  {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
		},		
		activityExtractedFrom {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Activity;}
		},
		assayExtractedFrom {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Assay;}
		},		
		hasTarget {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Assay;}
		},
		hasAssayType {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Assay;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},
		hasTargetCount {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Assay;}
			public String getPrefix() {	return "http://pele.farmbio.uu.se/chembl/onto/#";}
		},
		hasConfScore {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Assay;}
		},
		//Activity properties
		forMolecule {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Activity;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(ChEMBL_Class.Compound.getURI());
			}
		},
		standardUnits {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Activity;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},
		onAssay {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Activity;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},
		standardValue {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Activity;}
		},			
		relation {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Activity;}
		},
		activityType {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Activity;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},	
		targetOrganism  {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
		
		},			
		//Target properties
		sameAs {
			public String getPrefix() {	return "http://www.w3.org/2002/07/owl#";}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
		},	
		hasTargetType {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
		},	
		label {
			public String getPrefix() {	return "http://www.w3.org/2000/01/rdf-schema";}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			
		},	
		hasKeyword {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
		},	

		sequence {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
		},	
		hasTaxonomy {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
		},			
		classL1 {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},	
		classL2 {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},	
		classL3 {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},	
		classL4 {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},	
		classL5 {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},	
		classL6 {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},	
		classL7 {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},		
		classL8 {
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			@Override
			public Property getProperty(String arg0) {
				return super.getProperty(toString());
			}			
		},			
		identifier {
			public String getPrefix() {	return dcPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
			
		},		
		title {
			public String getPrefix() {	return dcPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Target;}
		},
		//ResourceProperties
		volume {
			public String getPrefix() {	return biboPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		},
		pageStart {
			public String getPrefix() {	return biboPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		},
		isPartOf {
			public String getPrefix() {	return dcPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		},
		pmid {
			public String getPrefix() {	return biboPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		},
		date {
			public String getPrefix() {	return dcPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		},		
		issue {
			public String getPrefix() {	return biboPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		},
		pageEnd {
			public String getPrefix() {	return biboPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		},
		doip {
			public String getPrefix() {	return biboPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		},
		doi {
			public String getPrefix() {	return biboPrefix;}
			public ChEMBL_Class getDomain() { return ChEMBL_Class.Resource;}
		};
	
		public String getPrefix() {
			return chemblPrefix;
		}
		public abstract ChEMBL_Class getDomain();
		public String getURI() {
			return String.format("%s%s",getPrefix(),toString());
		}
		public Property getProperty() {
			return getProperty(toString());
		}
		public Property getProperty(String uri) {
			Property p = new Property(getURI(),getChEMBLReference(getDomain()));
			p.setLabel(uri==null?toString():uri);
			return p;
		}
	}
	
	private ChEMBLProperties() {}
	
	public static synchronized LiteratureEntry getChEMBLReference(ChEMBL_Class chemblClass) {
		return LiteratureEntry.getInstance(chemblClass.getURI(),chemblPrefix);	
	}
	public static synchronized Property getChEMBL_CompoundURI_Instance() {
		Property p = new Property(ChEMBL_Property.forMolecule.getURI(),getChEMBLReference(ChEMBL_Class.Compound));
		p.setLabel(ChEMBL_Class.Compound.getURI());
		return p;
	}
	
	public static synchronized Property getChEMBLPropertyInstance(String property,ChEMBL_Class clazz ) {
		Property p = new Property(property,getChEMBLReference(clazz));
		p.setLabel(Property.opentox_Name);
		return p;
	}		
	
	public static synchronized Property getChEMBL_Name_Instance() {
		Property p = new Property("chembl:Name",getChEMBLReference(ChEMBL_Class.Compound));
		p.setLabel(Property.opentox_Name);
		return p;
	}		
	public static synchronized Property getChEMBL_Smiles_Instance() {
		Property p = new Property("chembl:Smiles",getChEMBLReference(ChEMBL_Class.Compound));
		p.setLabel(Property.opentox_SMILES);
		return p;
	}		
	
	public static synchronized Property getChEMBL_Activity_Instance(String assay) {
		Property p = new Property(ChEMBL_Class.Activity.getCaption(),getChEMBLReference(ChEMBL_Class.Activity));
		p.setLabel(ChEMBL_Class.Activity.getURI());
		return p;
	}		
}
