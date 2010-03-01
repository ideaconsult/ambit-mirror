package ambit2.fastox;

import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.rdf.OT;

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
	public static Model retrieveDataset(Model model, String datasetURI) throws Exception {
		return (model == null)?OT.createModel(null,new Reference(datasetURI),MediaType.APPLICATION_RDF_XML):
							model.read(datasetURI);
	}
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
		"	{ ?f owl:sameAs ot:InChI.}\n"+
		"	UNION\n"+
		"	{ ?f owl:sameAs ot:MolecularFormula.}\n"+
		"	UNION\n"+
		"	{ ?f owl:sameAs ot:SMILES.}\n"+
			
		"%s"+
		
		/*
		"	UNION\n"+
		"	{ ?f owl:sameAs otee:Carcinogenicity.}\n"+
		*/
		"	}\n"+
		
		"		}\n"+
		"	ORDER by ?c ?o";

	public static String modelVars = 
		"  UNION "+
		"	{ ?Model rdf:type ot:Model. ?Model ot:predictedVariables ?f. ?f dc:title ?name. OPTIONAL {?Model dc:title ?mname.} }" ;

		
	public static int renderDataset(Model model, Writer writer,String more,Reference rootReference) throws Exception {
		QueryExecution qe = null;
		try {
			Query query = QueryFactory.create(String.format(queryString,more));
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			//ResultSetFormatter.out(System.out, results, query);
			writer.write("<table class='resuts'>");
			//http://ambit.uni-plovdiv.bg:8080/ambit2/feature?sameas=http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23ChemicalName
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
}
