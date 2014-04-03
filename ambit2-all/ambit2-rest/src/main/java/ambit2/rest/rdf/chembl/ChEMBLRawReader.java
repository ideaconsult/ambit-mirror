package ambit2.rest.rdf.chembl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.base.data.ChEMBLProperties.ChEMBL_Property;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.io.IRawReader;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Reads SPARQL results from http://rdf.farmbio.uu.se/chembl/ into iterator
 * @author nina
 *
 */

public class ChEMBLRawReader implements IRawReader<IStructureRecord>{
	protected QueryExecution ex = null;
	protected ResultSet results;
	protected IStructureRecord record;
	
	public static final String sparqlEndpoint = "http://rdf.farmbio.uu.se/chembl/sparql";
	public final String sparql = 
		"PREFIX  chembl:<http://pele.farmbio.uu.se/chembl/onto/#>\n"+
		"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"PREFIX bo:<http://www.blueobelisk.org/chemistryblogs/>\n"+
		"select ?forMolecule ?SMILES ?Activity ?Assay ?Target ?standardValue ?standardUnits ?activityType ?relation ?classL2 ?classL3 ?classL4 ?classL5 where\n"+ 
		"{\n"+
		"?forMolecule a chembl:SmallMolecule.\n"+
		"?forMolecule bo:smiles ?SMILES.\n"+
		"?Activity chembl:forMolecule ?forMolecule.\n"+
		"?Activity a chembl:Activity.\n"+
		"?Activity chembl:type ?activityType.\n"+
		"?Activity chembl:relation ?relation.\n"+
		"?Activity chembl:standardValue ?standardValue.\n"+
		"?Activity chembl:standardUnits ?standardUnits.\n"+
		"?Activity chembl:onAssay ?Assay.\n"+
		"?Assay chembl:hasTarget ?Target.\n"+
		"?Target chembl:classL2 ?classL2.\n"+
		"?Target chembl:classL3 ?classL3.\n"+
		"?Target chembl:classL4 ?classL4.\n"+
		"?Target chembl:classL5 ?classL5.\n"+
		"FILTER (?classL2 = \"P450\") .\n"+
		"FILTER (?classL3 = \"3\") .\n"+
		"FILTER (?classL4 =  \"A\") .\n"+
		"FILTER (?classL5 =  \"4\") .\n"+
		"}\n"+
		"LIMIT 10\n";
			
	public ChEMBLRawReader() {
    	record = new StructureRecord();
    	record.setFormat(MOL_TYPE.SDF.toString());
    	record.setContent("");

		ex = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparql);
		results = ex.execSelect();
	}
	
	@Override
	public IStructureRecord nextRecord() {
		return record;
	}

	@Override
	public void handleError(String arg0) throws CDKException {
		
	}

	@Override
	public void handleError(String arg0, Exception arg1) throws CDKException {
		
	}

	@Override
	public void handleError(String arg0, int arg1, int arg2, int arg3)
			throws CDKException {
	
	}

	@Override
	public void handleError(String arg0, int arg1, int arg2, int arg3,
			Exception arg4) throws CDKException {
	
	}

	@Override
	public void setErrorHandler(IChemObjectReaderErrorHandler arg0) {
	}

	@Override
	public void setReader(Reader arg0) throws CDKException {
	}

	@Override
	public void setReader(InputStream arg0) throws CDKException {
	}

	@Override
	public void setReaderMode(Mode arg0) {
	}

	@Override
	public boolean accepts(Class<? extends IChemObject> arg0) {
		return false;
	}

	@Override
	public void addChemObjectIOListener(IChemObjectIOListener arg0) {
	}

	@Override
	public void close() throws IOException {
		try { ex.close();} catch (Exception x) {} ;
	}

	@Override
	public IResourceFormat getFormat() {
		return null;
	}

	@Override
	public IOSetting[] getIOSettings() {
		return null;
	}

	@Override
	public void removeChemObjectIOListener(IChemObjectIOListener arg0) {
	}

	@Override
	public boolean hasNext() {
		try {
			boolean ok = results.hasNext();
			if (ok) {
				record = processSolution(results.next());
			} else {
				record.clear();
			}
			return ok;
		} catch (Exception x) {
			record.clear();
			return false;
		}
	}

	public IStructureRecord processSolution(QuerySolution row) throws Exception {
		record.clear();
		RDFNode assay = row.get("Assay");
		RDFNode target = row.get("Target");
		RDFNode activity = row.get("Activity");
		
		String[] vars = {
				"SMILES","forMolecule","standardValue","standardUnits",
				"activityType","relation","classL2","classL3","classL4","classL5"
		};
				
		for (String var : vars) {
			RDFNode node = row.get(var);
			String value = null;
			if (node.isLiteral()) value = ((Literal)node).getString();
			else if (node.isURIResource()) value = ((Resource)node).getURI();
			else continue;
			
			ChEMBL_Property property = ChEMBL_Property.valueOf(var);
			String uri = null;
			switch (property.getDomain()) {
			case Activity : {
					uri = ((Resource)activity).getURI();
					break;
				}
			case Assay : {
					uri = ((Resource)assay).getURI();
					break;					
				}

			case Target : {
					uri = ((Resource)target).getURI();
					break;
				}	
				default: uri = null;
			}
			Property p = property.getProperty(uri);
			record.setProperty(p, value);
		}
		return record;

	}
	
	@Override
	public Object next() {
		return record;
	}

	@Override
	public void remove() {
	
	}

}
