package ambit2.rest.similarity;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.SimilarityRelation;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.property.QueryPairwiseTanimoto;
import ambit2.rest.OpenTox;
import ambit2.rest.structure.ConformerURIReporter;

/**
 * JSON
 * @author nina
 *
 * TODO - refactor as {@link StructureRelationJSONReporter}
 * @param <Q>
 */
public class PairwiseSimilarityJSONReporter<Q extends IQueryRetrieval<SimilarityRelation>> extends QueryReporter<SimilarityRelation,Q,Writer> {
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
	public Object processItem(SimilarityRelation item) throws AmbitException {
		try {
			if (item==null) return null;
			if (item.getFirstStructure()==null) return null;
			if (item.getSecondStructure()==null) return null;
			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n{"+
					"\n\t\"%s\":\"%s\"," + 
					"\n\t\"%s\":\"%s\"," + 
					"\n\t\"%s\":%6.4f" + //similarity
					"\n}",
					StructureRelationJSONReporter.jsonFeature.source.jsonname(),cmpReporter.getURI(item.getFirstStructure()),
					StructureRelationJSONReporter.jsonFeature.target.jsonname(),cmpReporter.getURI(item.getSecondStructure()),
					StructureRelationJSONReporter.jsonFeature.value.jsonname(),item.getRelation()
					));
			comma = ",";
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
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
				output.write(String.format("\"%s\":\"%s\",\n",StructureRelationJSONReporter.jsonFeature.datasetURI.jsonname(),datasetURI));
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
