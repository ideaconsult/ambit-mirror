package ambit2.fastox;

import java.io.Writer;

import org.opentox.rdf.OT;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.rest.rdf.RDFMetaDatasetIterator;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class DatasetTools {
	public static Model retrieveDataset(Model model, Reference datasetURI) throws Exception {
		try {
			model =  (model == null)?OT.createModel(null,datasetURI,MediaType.TEXT_RDF_N3):
							model.read(datasetURI.toString());
		} catch (Exception x) {
			model = (model == null)?OT.createModel(null,datasetURI,MediaType.APPLICATION_RDF_XML):
				model.read(datasetURI.toString());			
		}
		/*
		String[] s= new String[] {  "ChemicalName","IUPACName","CASRN","EINECS","REACHRegistrationDate"};
		for (String n:s) 
		try {
			if (model != null)
				model.read(String.format("%s?sameas=%s",wizard.getService(SERVICE.feature),
							Reference.encode(String.format("http://www.opentox.org/api/1.1#%s",n))));
		} catch (Exception x) {}
		*/
		return model;
	}
	
	protected static String queryAllFeaturesInDataset = 
		"PREFIX  dc:   <http://purl.org/dc/elements/1.1/>\n"+
		"PREFIX  ot:   <http://www.opentox.org/api/1.1#>\n"+
		"PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"SELECT DISTINCT  ?f\n"+
		"WHERE\n"+
		"  { \n"+
	    "{ ?dataset  ot:dataEntry  ?d .}\n"+
		"	      { ?d  rdf:type  ot:DataEntry .}\n"+
		"	      { ?d  ot:values  ?v .}\n"+
		"	      { ?v  ot:value  ?value .}\n"+
		"	      { ?v  ot:feature  ?f .}\n"+
		"	  }\n";

	protected static String queryCompoundFeaturesSameAs = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?o ?value ?otitle\n"+
		"		where {\n"+
		"	     ?dataset ot:dataEntry ?d.\n"+
		"		 ?d rdf:type ot:DataEntry.\n"+
		"	     ?d ot:compound <%s>.\n"+
		"	     ?d ot:values ?v.\n"+
		"	     ?v ot:value ?value.\n"+
		"	     ?v ot:feature ?f.\n"+
		"	     ?f owl:sameAs ?o.\n"+
		"	     ?f owl:sameAs %s.\n"+
		"	     OPTIONAL {?o dc:title ?otitle}.\n"+
		" }\n"+
		"	ORDER by ?value";
	
	protected static String queryPredictedFeatures = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?endpoint ?endpointName ?Model ?mname ?f ?o ?value ?otitle\n"+
		"		where {\n"+
		"	     ?dataset ot:dataEntry ?d.\n"+
		"		 ?d rdf:type ot:DataEntry.\n"+
		"	     ?d ot:compound <%s>.\n"+
		"	     ?d ot:values ?v.\n"+
		"	     ?v ot:value ?value.\n"+
		"	     ?v ot:feature ?f.\n"+
		"	     ?Model rdf:type ot:Model.\n"+
		"	     ?f owl:sameAs ?o.\n"+		
		//"	     ?Model ot:predictedVariables ?f\n"+
		"	     OPTIONAL {?f dc:title ?otitle}.\n"+
		"		 OPTIONAL {?Model dc:title ?mname.}\n"+
		"{\n"+
		"{ ?Model ot:dependentVariables ?f. } UNION { ?Model ot:predictedVariables ?f. }\n"+
		"}\n"+		
		"        OPTIONAL {?f owl:sameAs ?endpoint}.\n"+
		"        OPTIONAL {?endpoint dc:title ?endpointName}.\n"+		
		" }\n"+
		"	ORDER by ?endpoint ?Model ?f";	

	protected static String queryCompoundFeatures = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?o ?value ?otitle\n"+
		"		where {\n"+
		"	     ?dataset ot:dataEntry ?d.\n"+
		"		 ?d rdf:type ot:DataEntry.\n"+
		"	     ?d ot:compound <%s>.\n"+
		"	     ?d ot:values ?v.\n"+
		"	     ?v ot:value ?value.\n"+
		"	     ?v ot:feature ?f.\n"+
		"	     ?f dc:title ?otitle.\n"+
		"%s	}\n";
	
	
	protected static String queryCompounds = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?c \n"+
		"		where {\n"+
		"	     ?d ot:compound ?c.\n"+
		"		}\n"+
		"	ORDER by ?c";

	
	
	protected static String queryNotID = 
		"?f owl:sameAs ?o. FILTER ( ?o !=  ot:ChemicalName ). FILTER ( ?o !=  ot:CASRN). FILTER ( ?o !=  ot:EINECS). FILTER ( ?o !=  ot:IUPACName). FILTER ( ?o !=  ot:REACHRegistrationDate).\n";
	
	protected static String queryString = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?c ?o ?value ?name ?Model ?mname\n"+
		"		where {\n"+
		"	     OPTIONAL {  ?dataset ot:dataEntry ?d}.\n"+
		"		 OPTIONAL { ?d rdf:type ot:DataEntry}.\n"+
		"	     ?d ot:compound ?c.\n"+
		"	     OPTIONAL {?d ot:values ?v}.\n"+
		"	     OPTIONAL {?v ot:value ?value}.\n"+
		"	     OPTIONAL {?v ot:feature ?f}.\n"+
		"	     OPTIONAL {?f owl:sameAs ?o}.\n"+

		"	OPTIONAL {\n"+
		"	{ ?f owl:sameAs ot:ChemicalName.}\n"+
		"	UNION\n"+
		"	{ ?f owl:sameAs ot:CASRN.}\n"+
		"	UNION\n"+
		"	{ ?f owl:sameAs ot:EINECS.}\n"+	
		"	UNION\n"+
		"	{ ?f owl:sameAs ot:REACHRegistrationDate.}\n"+			
		"	UNION\n"+
		"	{ ?f owl:sameAs ot:InChI.}\n"+
		"	UNION\n"+
		"	{ ?f owl:sameAs ot:MolecularFormula.}\n"+
		"	UNION\n"+
		"	{ ?f owl:sameAs ot:SMILES.}\n"+
			
		"%s	}\n"+
		
		"		}\n"+
		"	ORDER by ?c ?o";

	public static String modelVars = 
		"  UNION "+
		"	{ ?Model rdf:type ot:Model. ?Model ot:predictedVariables ?f. ?f dc:title ?name. OPTIONAL {?Model dc:title ?mname.} }" ;

	protected static String getModelColumn(IToxPredictSession session, QuerySolution solution, Reference rootReference) {
		Resource m = solution.getResource("Model");
		Literal mname = solution.getLiteral("mname");
		Resource endpoint = solution.getResource("endpoint");
		Literal ename = solution.getLiteral("endpointName");
		if (m!=null) {
			/*
			String download = ModelTools.getDownloadURI(session,m.getURI(), 
						ChemicalMediaType.CHEMICAL_MDLSDF,rootReference);
						*/
			String download = null;
			return String.format(
					
					"%s&nbsp;<a href='%s' target='_blank' title='%s' alt='%s'><img border='0' src='%s/images/chart_line.png' alt='Model %s' title='Model %s'>%s</a>&nbsp;%s",
					ename==null?(endpoint==null?"":endpoint.getLocalName()):ename.getString(),
					m.getURI(),
					mname==null?m.getURI():mname.getString(),
					mname==null?m.getURI():mname.getString(),
					rootReference.toString(),
					mname==null?m.getURI():mname.getString(),
					mname==null?m.getURI():mname.getString(),
					mname==null?m.getURI():mname.getString(),
					download==null?"":download);
		}
		else return null;
	}	
	protected static String getNameColumn(QuerySolution solution,String caption) {
		Resource sameas = solution.getResource("o");
		Literal sameName = solution.getLiteral("otitle");		
		if (caption==null)
			return sameName!=null?sameName.getString():sameas!=null?sameas.getLocalName():"";
		else return caption; 
	}
	protected static String getValueColumn(QuerySolution solution) {
		Literal literal = solution.getLiteral("value");
		if ((literal==null) || literal.getString().equals(".") || literal.getString().equals("")) return null;
		return literal!=null?literal.getString():"";
	}
		//references do not appear, since they are not yet into the RDF model
	public static int renderCompoundFeatures(
				IToxPredictSession session,
			    String sparqlQuery,
				Model model, 
				Writer writer,
				Resource compound, 
				String param,
				String caption,
				Reference rootReference) throws Exception {
		QueryExecution qe = null;
		try {
			String compoundURI = compound.getURI();
			//System.out.println(String.format(sparqlQuery,compoundURI,param));
			Query query = QueryFactory.create(String.format(sparqlQuery,compoundURI,param));
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			int records = 0;
			String thisModel = null;
			while (results.hasNext()) {
				records++;
				QuerySolution solution = results.next();

				String value = getValueColumn(solution);
				if (value == null) continue;
				
				String modelString = getModelColumn(session,solution,rootReference);
				if (modelString != null) {
					if (!modelString.equals(thisModel)) {
						writer.write("<tr class='predictions'>");
						writer.write("<th align='left' colspan='3' class='predictions'>");
						thisModel = modelString;
						writer.write(modelString);
						
						writer.write("</th>");
						writer.write("</tr>");
					} 
						//predictions
						writer.write("<tr><td width='10%'></td><th colspan='1' align='left' valign='top' width='50%'>");
						writer.write(getNameColumn(solution, caption));
						writer.write("</th><td>");
						if (caption==null) 
							writer.write("<font color='#636bd2'>"); //didn't work with style
						writer.write(value);
						if (caption==null) 
							writer.write("</font>");
						writer.write("</td></tr>");						
			
				} else {
					writer.write("<tr><th colspan='2' align='left' valign='top' width='30%'>");
					writer.write(getNameColumn(solution, caption));
					writer.write("</th><td>");
					if (caption==null) 
						writer.write("<font color='#636bd2'>"); //didn't work with style
					writer.write(value);
					if (caption==null) 
						writer.write("</font>");
					writer.write("</td></tr>");
				}
				
			}

			return records;
		}catch (Exception x) {
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
		}		
	}	
	/*
	public static int renderDataset1(IToxPredictSession session,Model model, Writer writer,String more,Reference rootReference,String search,String condition) throws Exception {
		QueryExecution qe = null;
		try {
			Query query = QueryFactory.create(queryCompounds);
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			writer.write("<table width='100%' >");
			
			String download = ModelTools.getDatasetDownloadUri(session,session.getSearchQuery(), ChemicalMediaType.CHEMICAL_MDLSDF,rootReference);
			if (download!= null) writer.write(String.format("<tr><th align='right' title='Download' colspan='3'>Download as %s</th></tr>",download));
			String compoundURI = null;
			int records = 0;
			while (results.hasNext()) {
				records++;
				QuerySolution solution = results.next();

				Resource compound = solution.getResource("c");
				compoundURI = compound.getURI();
				writer.write("<tr class='chemicalid'>");
				writer.write("<td valign='top'>");
				writer.write(String.format("<img src='%s?media=%s&w=400&h=400' width='250' height='250' title='%s' alt='%s'>",
						compoundURI,
						Reference.encode("image/png"),compoundURI,compoundURI));					
				writer.write("</td>");
				writer.write("<td valign='top' colspan='2'>");
				writer.write("<table width=='100%' class='chemicalid'>");
				
				renderCompoundFeatures(session,queryCompoundFeaturesSameAs,model,writer, compound,"ot:CASRN","CAS RN",rootReference);
				renderCompoundFeatures(session,queryCompoundFeaturesSameAs,model,writer, compound,"ot:EINECS","EINECS",rootReference);
				renderCompoundFeatures(session,queryCompoundFeaturesSameAs,model,writer, compound,"ot:IUPACName","IUPAC name",rootReference);
				renderCompoundFeatures(session,queryCompoundFeaturesSameAs,model,writer, compound,"ot:ChemicalName","Synonym",rootReference);
				renderCompoundFeatures(session,queryCompoundFeaturesSameAs,model,writer, compound,"ot:REACHRegistrationDate","REACH Registration date",rootReference);
				

				if (search!=null) {
	
					String param;
					if ("=".equals(condition)) 
						param = String.format("FILTER (?value = \"%s\") .",search);
					else
						param = String.format("FILTER (regex(?value, \"%s\")) .",search);
					renderCompoundFeatures(session,queryCompoundFeatures,model,writer, compound,param,null,rootReference);

				}
				
				writer.write("<th align='left'>Quality label</th>");
				String consensus = getCompoundLabel(compoundURI,"consensus");
				String tag = getCompoundLabel(compoundURI,"comparison");
				if ((tag!=null)&&!"".equals(tag))
						writer.write(String.format("<td colspan='2' title='%s'>%s</td>", consensus==null?"":consensus,tag==null?"":tag));
				
				writer.write("</tr>");
				//renderCompoundFeatures(queryCompoundFeatures,model,writer, compound,String.format(querySameAs,"<http://www.opentox.org/echaEndpoints.owl#Carcinogenicity>"),rootReference);
	
				writer.write("</table></td></tr>");
				
				writer.write("<tr>");
				renderCompoundFeatures(session,queryPredictedFeatures,model,writer, compound,"",null,rootReference);
				writer.write("</tr><tr>");
				

			}
			//if (compoundURI != null) writer.write("</table></td></tr>");
			writer.write("</table>");

			return records;
		}catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
		}		
	}
	*/
	public static int renderDataset(Model model, Writer writer,String more,Reference rootReference) throws Exception {
		QueryExecution qe = null;
		try {
			Query query = QueryFactory.create(String.format(queryString,more));
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			writer.write("<table class='results'>");
			String compoundURI = null;
			int records = 0;
			while (results.hasNext()) {
				records++;
				QuerySolution solution = results.next();
				//DISTINCT ?c ?o ?f ?name ?value ?dataset
				Resource compound = solution.getResource("c");
				if (!compound.getURI().equals(compoundURI)) {
					if (compoundURI != null) writer.write("</table></td></tr>");
					compoundURI = compound.getURI();
					writer.write("<tr>");
					writer.write("<td>");
					writer.write(String.format("<img src='%s?media=%s&w=400&h=400' width='250' height='250' alt='%s'>",
							compoundURI,
							Reference.encode("image/png"),compoundURI));					
					writer.write("</td>");
					writer.write("<td>");
					writer.write("<table>");
				}
				
				writer.write("<tr>");
				writer.write("<th>");
				Resource same = solution.getResource("o");
				Literal name = solution.getLiteral("name");
				

				Resource m = solution.getResource("Model");
				Literal mname = solution.getLiteral("mname");
				if (m!=null)
					writer.write(String.format(
							"<a href='%s' target='_blank' title='Model %s' alt='%s'><img border='0' src='%s/images/chart_line.png' alt='Model %s' title='Model %s'></a>",
							m.getURI(),
							mname==null?m.getURI():mname.getString(),
							mname==null?m.getURI():mname.getString(),
							rootReference.toString(),
							mname==null?m.getURI():mname.getString(),
							mname==null?m.getURI():mname.getString()));
				
				writer.write("&nbsp;");
				writer.write((name!=null)?name.getString():
					(same!=null)?same.getLocalName():"");
								
				writer.write("</th><td>");
				Literal literal = solution.getLiteral("value");
				writer.write(literal!=null?literal.getString():"");
				writer.write("</td></tr>");
				
			}
			if (compoundURI != null) writer.write("</table></td></tr>");
			writer.write("</table>");

			return records;
		}catch (Exception x) {
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
		}		
	}
	
	public static void renderCompoundDatasets(Writer writer,String compoundURI) {
		RDFMetaDatasetIterator i=null;
		try {
			
			i = new RDFMetaDatasetIterator(new Reference(String.format("%s/datasets",compoundURI)));
			i.setCloseModel(true);
			writer.write("<tr>");
			writer.write("<th align='left' title='Found in datasets'>Dataset name&nbsp;</th>");
			writer.write("<td colspan='2'>");
			String delimiter = "";
			while(i.hasNext()) {
				ISourceDataset d = i.next();
				if (d.getName().indexOf(".rdf")>0) continue;
				writer.write(String.format("%s&nbsp;<a href='%s' title='%s' target=_blank>%s</a>",delimiter,
						d instanceof SourceDataset?((SourceDataset)d).getURL():"",
						d.getSource(),d.getName()));
				delimiter = ",";
			}
			writer.write("</td>");
			writer.write("</tr>");
		} catch (Exception x) {
		} finally {
			try { i.close(); } catch (Exception x) {} 
		}
	}
	
	public static String getCompoundLabel(String compoundURI,String label) {
		String tag = null;
		Representation p=null;
		try {
			ClientResource r = new ClientResource(String.format("%s/%s",compoundURI,label));
			p = r.get(MediaType.TEXT_PLAIN);
			return p.getText();
		
		} catch (Exception x) {
			tag = null;
		} finally {
			try { p.release();} catch (Exception x) {}
		}
		return tag;
	}
}

