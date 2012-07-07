package ambit2.rest.similarity;

import java.io.Writer;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRelation;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.property.QueryPairwiseTanimoto;
import ambit2.rest.OpenTox;
import ambit2.rest.structure.ConformerURIReporter;

/**
 * JSON
 * @author nina
 *
 * @param <Q>
 */
public class PairwiseSimilarityJSONReporter<Q extends IQueryRetrieval<IStructureRelation<Double>>> extends QueryReporter<IStructureRelation<Double>,Q,Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	protected final Reference baseReference;
	protected final Request request;
	protected final ConformerURIReporter<IQueryRetrieval<IStructureRecord>> cmpReporter;
	public Reference getBaseReference() {
		return baseReference;
	}
	
	enum jsonFeature {
		datasetURI,
		source,
		target,
		value,
		value2
		;
		
		public String jsonname() {
			return name();
		}
	}
	public PairwiseSimilarityJSONReporter(Request request) {
		super();
		this.request = request;
		this.baseReference = request.getRootRef();
		cmpReporter = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(request, null);
	}
	
	public ConformerURIReporter<IQueryRetrieval<IStructureRecord>> getCmpReporter() {
		return cmpReporter;
	}

	/**
	 * <pre>
		{"source":0,	"target":2,	"value":0.28,	"value2":0.82},
	 * </pre>	
	 */
	@Override
	public Object processItem(IStructureRelation<Double> item) throws AmbitException {
		try {
			if (item==null) return null;
			if (item.getStructures()==null) return null;
			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n{"+
					"\n\t\"%s\":\"%s\"," + 
					"\n\t\"%s\":\"%s\"," + 
					"\n\t\"%s\":%6.4f" + //similarity
					"\n}",
					jsonFeature.source.jsonname(),cmpReporter.getURI(item.getStructures()[0]),
					jsonFeature.target.jsonname(),cmpReporter.getURI(item.getStructures()[1]),
					jsonFeature.value.jsonname(),item.getRelation()
					));
			comma = ",";
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}
	/*
	protected String annotation2json(PropertyAnnotation annotation) {
		if (annotation!=null)
			
				PropertyAnnotationRDFReporter.annotation2RDF(a, jenaModel, feature,uriReporter.getBaseReference().toString());
			else return null;
	}
	*/
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]}");
		} catch (Exception x) {}
	};
	
	
	@Override
	public void header(Writer output, Q query) {
		try {
			output.write("{\n");
			if (query instanceof QueryPairwiseTanimoto) {
				((QueryPairwiseTanimoto)query).getFieldname();
				((QueryPairwiseTanimoto)query).getValue();
				Reference datasetURI = baseReference.clone();
				datasetURI.addSegment(OpenTox.URI.dataset.name());
				if (((QueryPairwiseTanimoto)query).getFieldname() instanceof IStoredQuery)
					datasetURI.addSegment(String.format("R%d",((QueryPairwiseTanimoto)query).getFieldname().getID()));
				else
					datasetURI.addSegment(Integer.toString(((QueryPairwiseTanimoto)query).getFieldname().getID()));
				output.write(String.format("\"%s\":\"%s\",\n",jsonFeature.datasetURI.jsonname(),datasetURI));
			}
			output.write("\"links\":[");
		} catch (Exception x) {}
	};
	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	@Override
	public void open() throws DbAmbitException {
		
	}
}
