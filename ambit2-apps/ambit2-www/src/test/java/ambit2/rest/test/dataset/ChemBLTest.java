package ambit2.rest.test.dataset;

import org.junit.Test;
import org.opentox.dsl.OTOntologyService;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.rdf.chembl.ChEMBLRawReader;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.query.QuerySolution;

public class ChemBLTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset", port);
	}
	
	

	
	public static final String findClasses = 
		"SELECT DISTINCT ?class\n"+
		"WHERE { [] a ?class }\n"+
		"ORDER BY ?class";
	
	public static final String findProperties = 
		"SELECT DISTINCT ?property\n"+
		"WHERE { [] ?property [] }\n"+
		"ORDER BY ?property";	
	
	public static final String chemblCompounds = 
		"PREFIX  chembl:<http://pele.farmbio.uu.se/chembl/onto/#>\n"+
		"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"PREFIX bo:<http://www.blueobelisk.org/chemistryblogs/>\n"+
		"select * where \n"+
		"{\n"+
		" ?compound a chembl:SmallMolecule.\n"+
		" ?compound bo:smiles ?smiles.\n"+
		" ?compound bo:inchi ?inchi.\n"+
		" OPTIONAL {?compound dc:title ?name.}\n"+
		"}\n"+
		"order by ?compound \n"+
		"LIMIT 10000\n";


	 
	@Test 
	public void readCompounds() throws Exception {
		
		OTOntologyService os = new OTOntologyService("http://rdf.farmbio.uu.se/chembl/sparql"){
			/**
		     * 
		     */
		    private static final long serialVersionUID = 6044026883931991782L;
			int count = 0;
			@Override
			public Object processSolution(QuerySolution row) throws Exception {
				count++;
				System.out.println(
						String.format("%d\t%s\t%s\t",
								count,
						row.get("compound"),row.get("smiles")));
				return null;
			}
			@Override
			public Object createObject() throws Exception {
				return null;
			}
		};
		os.query(chemblCompounds);
	}
	
	@Test 
	public void retrieveAssay() throws Exception {
		
		ChEMBLRawReader reader = new ChEMBLRawReader();
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			
			System.out.println(record);
			for (Property key : record.getProperties()) {
				System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s",
						record.getProperty(key),
						key.getName(),key.getLabel(),key.getTitle(),key.getUnits(),key.getUrl()
						));	
			}
		}
		reader.close();
	}
}
