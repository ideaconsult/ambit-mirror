package ambit2.fastox;

import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.base.data.SourceDataset;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.RDFMetaDatasetIterator;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class DatasetTools {
	public static Model retrieveDataset(Model model, String datasetURI) throws Exception {
		try {
			model =  (model == null)?OT.createModel(null,new Reference(datasetURI),MediaType.TEXT_RDF_N3):
							model.read(datasetURI);
		} catch (Exception x) {
			model = (model == null)?OT.createModel(null,new Reference(datasetURI),MediaType.APPLICATION_RDF_XML):
				model.read(datasetURI);			
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

	
		//references do not appear, since they are not yet into the RDF model
	public static int renderCompoundFeatures(
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
			System.out.println(String.format(sparqlQuery,compoundURI,param));
			Query query = QueryFactory.create(String.format(sparqlQuery,compoundURI,param));
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			int records = 0;
			while (results.hasNext()) {
				records++;
				QuerySolution solution = results.next();

				Literal literal = solution.getLiteral("value");
				if ((literal==null) || literal.getString().equals(".") || literal.getString().equals("")) continue;
				
				writer.write(String.format("<tr %s>",param==null?"class='small_button'":""));
				writer.write("<th align='left' valign='top' width='30%'>");
				//Resource feature = solution.getResource("f");
				//writer.write(feature==null?"":feature.getURI());
				Resource sameas = solution.getResource("o");
				Literal sameName = solution.getLiteral("otitle");		
				if (caption==null)
					writer.write(sameName!=null?sameName.getString():sameas!=null?sameas.getLocalName():"");
				else writer.write(caption);
				writer.write("</th>");
				writer.write("<th>");
				RDFNode title = solution.get("title");
				writer.write(title!=null?title.toString():"&nbsp;");	
				writer.write("</th>");		
				
				writer.write("<th>");
				Resource src = solution.getResource("src");
				writer.write(src!=null?src.getLocalName():"");
				RDFNode url = solution.get("url");
				writer.write(url!=null?url.toString():"&nbsp");	
				writer.write("</th>");
				
				writer.write("<td>");
				Literal name = solution.getLiteral("name");
				writer.write(name!=null?name.getString():"");
				writer.write("</td>");	
				
				writer.write("<td>");
				writer.write(literal!=null?literal.getString():"");
				writer.write("</td>");
				writer.write("</tr>");
				
			}

			return records;
		}catch (Exception x) {
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
		}		
	}	
	
	public static int renderDataset1(Model model, Writer writer,String more,Reference rootReference,String search,String condition) throws Exception {
		QueryExecution qe = null;
		try {
			Query query = QueryFactory.create(queryCompounds);
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			writer.write("<table width='90%'>");
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
					writer.write("<table class='results_col'>");
				}
				
				renderCompoundFeatures(queryCompoundFeaturesSameAs,model,writer, compound,"ot:CASRN","CAS RN",rootReference);
				renderCompoundFeatures(queryCompoundFeaturesSameAs,model,writer, compound,"ot:EINECS","EINECS",rootReference);
				renderCompoundFeatures(queryCompoundFeaturesSameAs,model,writer, compound,"ot:IUPACName","IUPAC name",rootReference);
				renderCompoundFeatures(queryCompoundFeaturesSameAs,model,writer, compound,"ot:ChemicalName","Synonym",rootReference);
				renderCompoundFeatures(queryCompoundFeaturesSameAs,model,writer, compound,"ot:REACHRegistrationDate","REACH Registration date",rootReference);
				writer.write("</table>");
			
				if (search!=null) {
					writer.write("<table>");
					String param;
					if ("=".equals(condition)) 
						param = String.format("FILTER (?value = \"%s\") .",search);
					else
						param = String.format("FILTER (regex(?value, \"%s\")) .",search);
					renderCompoundFeatures(queryCompoundFeatures,model,writer, compound,param,null,rootReference);
					writer.write("</table>");
				}
				
				writer.write("<table>");
				writer.write("<tr>");
				
				writer.write("<th>Quality label</th>");
				String consensus = getCompoundLabel(compoundURI,"consensus");
				String tag = getCompoundLabel(compoundURI,"comparison");
				writer.write(String.format("<td title='%s'>%s</td>", consensus==null?"":consensus,tag==null?"":tag));

				writer.write("</tr>");
				//renderCompoundFeatures(queryCompoundFeatures,model,writer, compound,String.format(querySameAs,"<http://www.opentox.org/echaEndpoints.owl#Carcinogenicity>"),rootReference);
				writer.write("</table>");
				writer.write("<table>");
				renderCompoundDatasets(writer,compoundURI);
			}
			if (compoundURI != null) writer.write("</table></td></tr>");
			writer.write("</table>");

			return records;
		}catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
		}		
	}
	
	public static int renderDataset(Model model, Writer writer,String more,Reference rootReference) throws Exception {
		QueryExecution qe = null;
		try {
			Query query = QueryFactory.create(String.format(queryString,more));
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			writer.write("<table class='resuts'>");
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
			while(i.hasNext()) {
				SourceDataset d = i.next();
				writer.write("<tr>");
				writer.write("<th>Dataset name&nbsp;</th>");
				writer.write(String.format("<td title='%s %s'>",d.getTitle(),d.getURL()));
				writer.write(d.getName());
				writer.write("</td>");
				writer.write("</tr>");
			}
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

