package ambit2.rest.similarity;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.AbstractRelation;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.IStoredQuery;
import ambit2.rest.OpenTox;
import ambit2.rest.structure.ConformerURIReporter;

public class StructureRelationJSONReporter<Q extends IQueryRetrieval<AbstractRelation>> extends QueryReporter<AbstractRelation,Q,Writer> {
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
		relation,
		value,
		value2
		;
		
		public String jsonname() {
			return name();
		}
	}
	public StructureRelationJSONReporter(Request request) {
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
	public Object processItem(AbstractRelation item) throws AmbitException {
		try {
			if (item==null) return null;
			if (item.getFirstStructure()==null) return null;
			if (item.getSecondStructure()==null) return null;
			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n{"+
					"\n\t\"%s\":\"%s\"," + 
					"\n\t\"%s\":\"%s\"," +
					"\n\t\"%s\":\"%s\"," + 
					"\n\t\"%s\":%6.4f" + //metric
					"\n}",
					jsonFeature.source.jsonname(),cmpReporter.getURI(item.getFirstStructure()),
					jsonFeature.target.jsonname(),cmpReporter.getURI(item.getSecondStructure()),
					jsonFeature.relation.jsonname(),item.getRelationType(),
					jsonFeature.value.jsonname(),item.getRelation()
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
			Reference datasetURI = baseReference.clone();
			datasetURI.addSegment(OpenTox.URI.dataset.name());
			
			Object dataset = (query instanceof AbstractQuery)?((AbstractQuery)query).getValue():null;
			if (dataset instanceof IStoredQuery)
				datasetURI.addSegment(String.format("R%d",((IStoredQuery)dataset).getID()));
			else if (dataset instanceof SourceDataset)
				datasetURI.addSegment(Integer.toString(((ISourceDataset)dataset).getID()));
			
			output.write(String.format("\"%s\":\"%s\",\n",jsonFeature.datasetURI.jsonname(),datasetURI));

			output.write("\"links\":[");
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	@Override
	public void open() throws DbAmbitException {
		
	}
}
