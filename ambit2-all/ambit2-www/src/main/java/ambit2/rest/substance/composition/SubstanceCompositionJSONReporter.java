package ambit2.rest.substance.composition;

import java.io.Writer;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.json.JSONUtils;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.structure.ConformerURIReporter;
import ambit2.rest.substance.SubstanceURIReporter;

/**
 * Substance composition JSON serialization
 * @author nina
 *
 * @param <Q>
 */
public class SubstanceCompositionJSONReporter<Q extends IQueryRetrieval<CompositionRelation>> extends QueryReporter<CompositionRelation,Q,Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	protected String jsonpCallback = null;
	protected final ConformerURIReporter<IQueryRetrieval<IStructureRecord>> cmpReporter;
	protected final SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> substanceReporter;
	public Reference getBaseReference() {
		return cmpReporter.getBaseReference();
	}
	
	enum jsonFeature {
		substance,
		compound,
		relation,
		proportion
		;
		
		public String jsonname() {
			return name();
		}
	}
	public SubstanceCompositionJSONReporter(Request request, ResourceDoc doc,String jsonpCallback) {
		super();
		cmpReporter = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(request, null);
		substanceReporter = new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(request, null);
		this.jsonpCallback = jsonpCallback;
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
	public Object processItem(CompositionRelation item) throws AmbitException {
		try {
			if (item==null) return null;
			if (item.getFirstStructure()==null) return null;
			if (item.getSecondStructure()==null) return null;
			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n{"+
					"\n\t\"%s\": {\"URI\" : %s }," + 
					"\n\t\"%s\": {\"URI\" : %s }," +
					"\n\t\"%s\":\"%s\"," + 
					"\n\t\"%s\":%s" + //metric
					"\n}",
					jsonFeature.substance.jsonname(),JSONUtils.jsonQuote(substanceReporter.getURI(item.getFirstStructure())),
					jsonFeature.compound.jsonname(),JSONUtils.jsonQuote(cmpReporter.getURI(item.getSecondStructure())),
					jsonFeature.relation.jsonname(),item.getRelationType().name(),
					jsonFeature.proportion.jsonname(),item.getRelation().toJSON()
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
			output.write("\"composition\":[");
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
