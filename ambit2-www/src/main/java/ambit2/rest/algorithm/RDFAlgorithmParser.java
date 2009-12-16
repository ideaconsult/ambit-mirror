package ambit2.rest.algorithm;

import java.io.Serializable;

import ambit2.core.data.model.Algorithm;
import ambit2.rest.OT;
import ambit2.rest.RDFBatchParser;
import ambit2.rest.OT.OTClass;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFAlgorithmParser<T extends Serializable> extends RDFBatchParser<Algorithm<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1858626318484831791L;

	public RDFAlgorithmParser(String baseReference, OTClass objectToParse) {
		super(baseReference, objectToParse);
	}

	@Override
	protected Algorithm<T> createRecord() {
		return new Algorithm<T>();
	}

	@Override
	protected void parseRecord(Resource newEntry, Algorithm<T> record) {
		//get the compound
		StmtIterator alg =  jenaModel.listStatements(new SimpleSelector(newEntry,OT.OTProperty.algorithm.createProperty(jenaModel),(RDFNode)null));
		while (alg.hasNext()) {
			Statement st = alg.next();
			parseParameters(st.getObject(),record);

		}	

	}	
	protected void parseParameters(RDFNode dataEntry,Algorithm<T> record) {
		//get the compound
		//StmtIterator params =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.parameters,(RDFNode)null));

	}
}
