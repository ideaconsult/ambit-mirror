package ambit2.db.processors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.UUID;

import net.idea.i5.io.I5CONSTANTS;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.interfaces.ICiteable;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.Proportion;
import ambit2.core.io.IRawReader;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class NanoWikiRDFReader extends DefaultIteratingChemObjectReader implements IRawReader<IStructureRecord>, ICiteable {
	protected Model rdf;
	protected ResIterator materials;
	protected SubstanceRecord record;
	
	public NanoWikiRDFReader(Reader reader) throws CDKException {
		super();
		setReader(reader);
	}
	public static String generateUUID(String prefix) {
		return prefix + "-" + UUID.randomUUID();
	}	

	@Override
	public void setReader(Reader reader) throws CDKException {
		try {
			rdf = ModelFactory.createDefaultModel();
			rdf.read(reader,null,"RDF/XML");
			Resource materialtype = rdf.createResource("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Category-3AMaterials");
			materials = rdf.listResourcesWithProperty(RDF.type, materialtype );
		} finally {
			try {reader.close();} catch (Exception x) {}
		}
	}

	
	@Override
	public void setReader(InputStream reader) throws CDKException {
		try {
			setReader(new InputStreamReader(reader,"UTF-8"));
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
		rdf.close();
	}

	@Override
	public boolean hasNext() {
		if (materials==null) return false;
		if (materials.hasNext()) {
			record = new SubstanceRecord();
			record.setExternalids(new ArrayList<ExternalIdentifier>());
			parseMaterial(rdf, materials.next(),record);
			parseCoatings(rdf, materials.next(),record);
			return true;
		} else {
			record = null;
			return false;
		}
	}

	@Override
	public Object next() {
		if (materials==null) return null;
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

	/*
	private void parseStudy(Model rdf,RDFNode studyNode,SubstanceRecord record) {
		StmtIterator ii = rdf.listStatements(studyNode.asResource(),null,(RDFNode)null);
		String endpoint = "unknown";
		Protocol protocol = null;
		ProtocolApplication papp = new ProtocolApplication(protocol);
		record.addtMeasurement(papp);
		while (ii.hasNext()) {
			Statement stmt = ii.next();
			Property p = stmt.getPredicate();
			RDFNode node = stmt.getObject();
			if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AHas_Endpoint")) {
				endpoint = node.asResource().getLocalName(); //TODO this is a resource
				protocol = new Protocol(endpoint);
				papp.setProtocol(protocol);
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AHas_Identifier")) {
				
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AHas_Study_Type")) {
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AHas_Source")) {
			} else if (p.getURI().equals("http://www.w3.org/2000/01/rdf-schema#isDefinedBy")) {
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AHas_Q2")) {
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AHas_R2")) {
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AHas_RMSEP")) {
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AUses_Descriptor")) {
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AHas_EA")) {
			} else if (p.getURI().equals("http://127.0.0.1/mediawiki/index.php/Special:URIResolver/Property-3AFor_Cell_line")) {				
				
				
			}
			//printStmt(stmt);
		}

	}
	*/
	private static final String m_material =
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX mw: <http://127.0.0.1/mediawiki/index.php/Special:URIResolver/>\n"+
		"SELECT distinct ?composition ?coating ?id ?altid ?label ?type ?id ?label2 ?source\n"+
		"WHERE {\n"+
		"<%s> rdf:type mw:Category-3AMaterials.\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Chemical_Composition ?composition.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Chemical_Coating ?coating.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Identifier ?id.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Label ?label.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_NM_Type ?type.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_alternative_Identifier ?altid.}\n"+
		"OPTIONAL {<%s> rdfs:label ?label2.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Source ?source.}\n"+
		"}";

	private static final String m_coating =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX mw: <http://127.0.0.1/mediawiki/index.php/Special:URIResolver/>\n"+
		"SELECT distinct ?coating ?chemical ?smiles\n"+
		"WHERE {\n"+
		"<%s> mw:Property-3AHas_Chemical_Coating ?coating.\n"+
		"?coating mw:Property-3AHas_Chemical ?chemical.\n"+
		"OPTIONAL {\n"+
		"?chemical mw:Property-3AHas_SMILES ?smiles.\n"+
		"}\n"+
		"} order by ?coating ?chemical ?smiles\n";
		
	private void parseCoatings(Model rdf,RDFNode material,SubstanceRecord record) {
		ProcessSolution.execQuery(rdf, String.format(m_coating, 
				material.asResource().getURI()
				),
				new ProcessCoatings(rdf,material,record));
	}	
	
	private void parseMaterial(Model rdf,RDFNode material,SubstanceRecord record) {
		ProcessSolution.execQuery(rdf, String.format(m_material, 
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI(),
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI(),
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI()
				),
				new ProcessMaterial(rdf,material,record));
	}
}


class ProcessSolution {
	public int process(ResultSet rs) {
		int records = 0;
		processHeader(rs);
		System.out.println();
		while (rs.hasNext()) {
			records++;
			QuerySolution qs = rs.next();
			process(rs,qs);
		}
		return records;
	}
	void processHeader(ResultSet rs) {
		for (String name : rs.getResultVars()) {
			System.out.print(name);
			System.out.print("\t");
		}		
	}
	void process(ResultSet rs,QuerySolution qs) {
		for (String name : rs.getResultVars()) {
			RDFNode node = qs.get(name);
			if (node ==null) ;
			else if (node.isLiteral()) System.out.print(node.asLiteral().getString());
			else if (node.isResource()) System.out.print(node.asResource().getURI());
			else System.out.print(node.asNode().getName());
			 System.out.print("\t");
		}
		System.out.println();
	}
	protected static int execQuery(Model rdf,String sparqlQuery,ProcessSolution processor) {
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution qe = QueryExecutionFactory.create(query,rdf);
		int records = 0;
		try {
			ResultSet rs = qe.execSelect();
			records = processor.process(rs);
		} finally {
			qe.close();	
		}
		return records;
	}	

}


class ProcessMeasurement extends ProcessSolution {
	SubstanceRecord record;
	public ProcessMeasurement(SubstanceRecord record) {
		this.record = record;
	}
	@Override
	void processHeader(ResultSet rs) {
	}
	
	enum endpoints {
		Zeta_Potential {
			@Override
			public String getCategory() {
				return Protocol._categories.ZETA_POTENTIAL_SECTION.name();
			}				

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}
			@Override
			public String getTag() {
				return I5CONSTANTS.eZETA_POTENTIAL;
			}
		},
		Isoelectric_point {
			@Override
			public String getCategory() {
				return Protocol._categories.ZETA_POTENTIAL_SECTION.name();
			}				
			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}
			@Override
			public String getTag() {
				return I5CONSTANTS.eISOELECTRIC_POINT;
			}
		},
		Aggregation {
			@Override
			public String getCategory() {
				return Protocol._categories.AGGLOMERATION_AGGREGATION_SECTION.name();
			}					
			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}
			@Override
			public String getTag() {
				return I5CONSTANTS.eAGGLO_AGGR_SIZE;
			}
		},
		Particle_Size {
			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}
			@Override
			public String getCategory() {
				return Protocol._categories.PC_GRANULOMETRY_SECTION.name();
			}				
			@Override
			public String getTag() {
				return I5CONSTANTS.pPARTICLESIZE;
			}
		},
		Hydrodynamic_size {
			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}
			@Override
			public String getCategory() {
				return Protocol._categories.PC_GRANULOMETRY_SECTION.name();
			}		
			@Override
			public String getTag() {
				//hydrodynamic and aerodynamic size is the same
				// MMAD is Mass median aerodynamic diameter  - is this what NanoWiki assumes here?
				return I5CONSTANTS.pMMAD;
			}
		}, 		
		Toxicity {
			//what kind of toxicity endpoint???
			@Override
			public String getCategory() {
				//best guess
				return Protocol._categories.TO_GENETIC_IN_VITRO_SECTION.name();
			}
		},
		Toxicity_Classifier {
			//what kind of toxicity endpoint???
			@Override
			public String getCategory() {
				//best guess
				return Protocol._categories.TO_GENETIC_IN_VITRO_SECTION.name();
			}			
		},
		Oxidation_State_Concentration,
		Log_Reciprocal_EC50 {
			// what endpoint?
			@Override
			public String getCategory() {
				//best guess
				return Protocol._categories.TO_GENETIC_IN_VITRO_SECTION.name();
			}			
		},
		Cytotoxicity {
			@Override
			public String getCategory() {
				//best guess
				return Protocol._categories.TO_GENETIC_IN_VITRO_SECTION.name();
			}
		},
		Log_GI50,
		Percentage_Non_2DViable_Cells {
			@Override
			public String getCategory() {
				//best guess
				return Protocol._categories.TO_GENETIC_IN_VITRO_SECTION.name();
			}
			@Override
			public String getUnit() {
				return "%";
			}
		},
		Bioassay_Profile {
			//????
		};
		public String getCategory() {
			return Protocol._categories.UNKNOWN_TOXICITY_SECTION.name();
		}
		public String getTopCategory() {
			return "TOX";
		}
		public String getTag() {
			return name();
		}
		public String getUnit() {
			return null;
		}
	}
	@Override
	void process(ResultSet rs, QuerySolution qs) {
		RDFNode value = qs.get("value");
		String endpoint = null;
		try {endpoint = qs.get("endpoint").asResource().getLocalName();}catch (Exception x) {endpoint = qs.get("endpoint").toString(); }
		Protocol protocol = new Protocol(endpoint); 
		String measuredEndpoint = endpoint;
		try {
			endpoints ep = endpoints.valueOf(endpoint.replace("-","_").replace(" ","_"));
			protocol.setCategory(ep.getCategory());
			protocol.setTopCategory(ep.getTopCategory());
			measuredEndpoint = ep.getTag();
		} catch (Exception x) {
			
			protocol.setCategory(Protocol._categories.UNKNOWN_TOXICITY_SECTION.name());
			protocol.setTopCategory("TOX");
		}	
				
		RDFNode method = qs.get("method");
		try {protocol.addGuideline(method.asResource().getLocalName());} catch (Exception x) {}
		
		ProtocolApplication<Protocol,IParams,String,IParams,String> papp = new ProtocolApplication<Protocol,IParams,String,IParams,String>(protocol);
		//papp.setReliability(reliability)
		papp.setParameters(new Params());
		try {
			if (method!=null)
				papp.getParameters().put(I5CONSTANTS.methodType,method.asResource().getLocalName());
		} catch (Exception x) {
			
		}
		papp.setDocumentUUID(NanoWikiRDFReader.generateUUID("NWKI"));
		try {papp.setReference(qs.get("definedBy").asResource().getURI());} catch (Exception x) {}
		try {
			papp.setCompanyName(qs.get("measurement").asResource().getURI());
		} catch (Exception x) {}
		try {papp.setInterpretationResult(qs.get("resultInterpretation").asLiteral().getString());} catch (Exception x) {}
		
		EffectRecord<String,IParams,String> effect = new EffectRecord<String,IParams,String>();
		effect.setEndpoint(measuredEndpoint);
		effect.setConditions(new Params());
		try {effect.setTextValue(qs.get("resultInterpretation").asLiteral().getString());} catch (Exception x) {}
		try {
			if (value!=null) effect.setLoValue(Double.parseDouble(value.asLiteral().getString()));
		} catch (Exception x) {
			effect.setTextValue(value.asLiteral().getString());
		}
		
		RDFNode valueError = qs.get("valueError");
		try {
			effect.setStdDev(Double.parseDouble(valueError.asLiteral().getString()));
		} catch (Exception x) {}
		
		try {	effect.setUnit(qs.get("valueUnit").asLiteral().getString());
		} catch (Exception x) {
		}

		RDFNode dose = qs.get("dose"); 
		if (dose!=null) {
			IParams v = new Params();
			try {v.setLoValue(Double.parseDouble(dose.asLiteral().getString()));} catch (Exception x) {v.setLoValue(null);}
			try {v.setUnits(qs.get("doseUnit").asLiteral().getString());} catch (Exception x) {}
			IParams conditions = effect.getConditions(); 
			if (effect.getConditions()==null) conditions = new Params();				
			conditions.put(I5CONSTANTS.cDoses, v);
			effect.setConditions(conditions);
		}
		papp.addEffect(effect);
		//qs.get("label");
		//qs.get("definedBy");
		record.addtMeasurement(papp);
 }
}
	
class ProcessNMMeasurement extends ProcessSolution {
		SubstanceRecord record;
		String endpoint;
		String topCategory;
		String category;
		
		public ProcessNMMeasurement(SubstanceRecord record,String topCategory,String category,String endpoint) {
			this.record = record;
			this.endpoint = endpoint;
			this.topCategory = topCategory;
			this.category = category;
		}
		@Override
		void processHeader(ResultSet rs) {
		}
		@Override
		void process(ResultSet rs, QuerySolution qs) {
			RDFNode value = qs.get("value");
			if (value==null && qs.get("valueMin")==null) return;
			Protocol protocol = new Protocol(endpoint); 
			protocol.setCategory(category);
			protocol.setTopCategory(topCategory);
			
			RDFNode method = qs.get("method");
			try {protocol.addGuideline(method.asResource().getLocalName());} catch (Exception x) {}
			
			ProtocolApplication<Protocol,Params,String,Params,String> papp = new ProtocolApplication<Protocol,Params,String,Params,String>(protocol);
			papp.setDocumentUUID(NanoWikiRDFReader.generateUUID("NWKI"));
			papp.setSubstanceUUID(record.getCompanyUUID());
			papp.setParameters(new Params());
			
			try {
				if (method!=null)
					papp.getParameters().put(I5CONSTANTS.methodType,method.asResource().getLocalName());
			} catch (Exception x) {}
			
			EffectRecord effect = new EffectRecord();
			effect.setEndpoint(endpoint);
			effect.setConditions(new Params());
			
			try {
				if (value!=null) effect.setLoValue(Double.parseDouble(value.asLiteral().getString()));
			} catch (Exception x) {
				effect.setTextValue(value.asLiteral().getString());
			}
			try {effect.setUpValue(value.asLiteral().getDouble()+qs.get("valueError").asLiteral().getDouble());effect.setLoQualifier("<=");effect.setUpQualifier("<=");} catch (Exception x) {}
			
			try {effect.setLoValue(qs.get("valueMin").asLiteral().getDouble());;effect.setLoQualifier("<=");} catch (Exception x) {}
			try {effect.setUpValue(qs.get("valueMax").asLiteral().getDouble());effect.setUpQualifier("<=");} catch (Exception x) {}
			
			try {effect.setUnit(qs.get("valueUnit").asLiteral().getString());} catch (Exception x) {}

			papp.addEffect(effect);
			record.addtMeasurement(papp);
		}
}

//"SELECT distinct ?coating ?chemical ?smiles\n"+
class ProcessCoatings extends ProcessSolution {
	SubstanceRecord record;
	Model rdf;
	RDFNode material;
	String composition_uuid;
	public ProcessCoatings(Model rdf,RDFNode material,SubstanceRecord record) {
		this.record = record;
		this.rdf = rdf;
		this.material = material;
		composition_uuid = NanoWikiRDFReader.generateUUID("NWKI");
	}
	@Override
	void processHeader(ResultSet rs) {
	}
	@Override
	void process(ResultSet rs, QuerySolution qs) {
		
		IStructureRecord coating = new StructureRecord();
		record.addStructureRelation(composition_uuid, coating, STRUCTURE_RELATION.HAS_COATING, new Proportion());
		try {record.setOwnerName(qs.get("coating").asResource().getLocalName());} catch (Exception x) {};
		try {coating.setProperty(Property.getNameInstance(),qs.get("chemical").asResource().getLocalName());} catch (Exception x) {};
		try {coating.setContent(qs.get("smiles").asLiteral().getString()); coating.setFormat("INC"); coating.setSmiles(coating.getContent());} catch (Exception x) {};
	}
}
class ProcessMaterial extends ProcessSolution {
	SubstanceRecord record;
	Model rdf;
	RDFNode material;
	public ProcessMaterial(Model rdf,RDFNode material,SubstanceRecord record) {
		this.record = record;
		this.rdf = rdf;
		this.material = material;
	}
	@Override
	void processHeader(ResultSet rs) {
	}
	@Override
	void process(ResultSet rs, QuerySolution qs) {
		record.setReferenceSubstanceUUID(NanoWikiRDFReader.generateUUID("NWKI"));
		record.setCompanyUUID(NanoWikiRDFReader.generateUUID("NWKI"));
		
		try {record.setOwnerName(qs.get("source").asResource().getLocalName());} catch (Exception x) {};
		try {record.setSubstancetype(qs.get("type").asResource().getLocalName());} catch (Exception x) {};
		try {record.setCompanyName(qs.get("label2").asLiteral().getString());} catch (Exception x) {};
		try {record.setPublicName(qs.get("label").asLiteral().getString());} catch (Exception x) {};
		try {record.getExternalids().add(new ExternalIdentifier("Has_Identifier",qs.get("id").asLiteral().getString()));} catch (Exception x) {};
		try {record.getExternalids().add(new ExternalIdentifier("Alternative Identifier",qs.get("altid").asLiteral().getString()));} catch (Exception x) {};
		try {record.getExternalids().add(new ExternalIdentifier("Composition",qs.get("composition").asLiteral().getString()));} catch (Exception x) {};
		try {record.getExternalids().add(new ExternalIdentifier("Coating",qs.get("coating").asResource().getLocalName()));} catch (Exception x) {};

		parseSize(rdf, material, record);
		parseIEP(rdf, material, record);
		parseZetaPotential(rdf, material, record);
		parseMeasurement(rdf, material, record);
	}
	

	private static final String m_iep =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
		"PREFIX mw: <http://127.0.0.1/mediawiki/index.php/Special:URIResolver/>\n"+
		"SELECT DISTINCT ?value\n"+		
		"WHERE {\n"+
		"<%s> mw:Property-3AHas_IEP ?value.\n"+
		"}";
	
	private static final String m_size =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
		"PREFIX mw: <http://127.0.0.1/mediawiki/index.php/Special:URIResolver/>\n"+
		"SELECT DISTINCT " +
		"?method ?value ?valueUnit ?valueError ?valueMin ?valueMax\n"+		
		"WHERE {\n"+
		//"?material rdf:type mw:Category-3AMaterials.\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Size ?value.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Size_Error ?valueError.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Size_Units ?valueUnit.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Size_Min ?valueMin.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Size_Max ?valueMax.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Size_Method ?method.}\n"+
		"}";
	
	private static final String m_zetapotential =
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
		"PREFIX mw: <http://127.0.0.1/mediawiki/index.php/Special:URIResolver/>\n"+
		"SELECT DISTINCT " +
		"?method ?value ?valueUnit ?valueError\n"+		
		"WHERE {\n"+
		//"?material rdf:type mw:Category-3AMaterials.\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Zeta_potential ?value.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Zeta_Error ?valueError.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Zeta_Units ?valueUnit.}\n"+
		"OPTIONAL {<%s> mw:Property-3AHas_Zeta_Method ?method.}\n"+
		"}";	

	private static final String m_sparql = 

		"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
		"PREFIX mw: <http://127.0.0.1/mediawiki/index.php/Special:URIResolver/>\n"+
		"SELECT DISTINCT \n"+
		"?study ?measurement ?label ?method ?definedBy ?endpoint ?dose ?doseUnit\n"+ 
		"?value ?valueUnit ?valueError ?resultInterpretation\n"+
		"WHERE {\n"+
		//"?material rdf:type mw:Category-3AMaterials.\n"+
		"?measurement mw:Property-3AHas_Entity <%s>.\n"+
		"OPTIONAl {?measurement rdfs:label ?label.}\n"+
		"OPTIONAl {?measurement mw:Property-3AHas_Method ?method.}\n"+
		"OPTIONAL {?measurement rdfs:isDefinedBy ?definedBy.}\n"+
		"OPTIONAL {?measurement mw:Property-3AHas_Endpoint ?endpoint.}\n"+
		"OPTIONAL {?measurement mw:Property-3AHas_Dose ?dose.}\n"+
		"OPTIONAL {?measurement mw:Property-3AHas_Dose_Units ?doseUnit.}\n"+
		"OPTIONAL {?measurement mw:Property-3AHas_Endpoint_Class ?resultInterpretation.}\n"+
		"OPTIONAL {?measurement mw:Property-3AHas_Endpoint_Value ?value.}\n"+
		"OPTIONAL {?measurement mw:Property-3AHas_Endpoint_Value_Units ?valueUnit.}\n"+
		"OPTIONAL {?measurement mw:Property-3AHas_Endpoint_Error ?valueError.}\n"+
		"OPTIONAL {?measurement mw:Property-3AHas_Study ?study.}\n"+
		"} ORDER by ?measurement\n";
	private void parseIEP(Model rdf,RDFNode material,SubstanceRecord record) {
		execQuery(rdf, String.format(m_iep, 
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI(),
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI()
				),
				new ProcessNMMeasurement(record,"P-CHEM",Protocol._categories.ZETA_POTENTIAL_SECTION.name(),I5CONSTANTS.eISOELECTRIC_POINT));
	}

	private void parseSize(Model rdf,RDFNode material,SubstanceRecord record) {
		execQuery(rdf, String.format(m_size, 
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI(),
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI()
				),
				new ProcessNMMeasurement(record,"P-CHEM",Protocol._categories.PC_GRANULOMETRY_SECTION.name(),I5CONSTANTS.pPARTICLESIZE));
	}
	private void parseZetaPotential(Model rdf,RDFNode material,SubstanceRecord record) {
		execQuery(rdf, String.format(m_zetapotential, 
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI(),
				material.asResource().getURI(),material.asResource().getURI(),material.asResource().getURI()
				),
				new ProcessNMMeasurement(record,"P-CHEM",Protocol._categories.ZETA_POTENTIAL_SECTION.name(),I5CONSTANTS.eZETA_POTENTIAL));
	}

	private void parseMeasurement(Model rdf,RDFNode material,SubstanceRecord record) {
		execQuery(rdf, String.format(m_sparql, material.asResource().getURI()),new ProcessMeasurement(record));
	}
	
}
