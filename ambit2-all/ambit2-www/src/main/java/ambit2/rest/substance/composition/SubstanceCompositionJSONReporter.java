package ambit2.rest.substance.composition;

import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.json.JSONUtils;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.reporters.QueryReporter;
import ambit2.pubchem.NCISearchProcessor;
import ambit2.rest.ResourceDoc;
import ambit2.rest.structure.CompoundJSONReporter;
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

	
	protected final CompoundJSONReporter<IQueryRetrieval<IStructureRecord>> cmpReporter;
	protected final SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> substanceReporter;

	public SubstanceCompositionJSONReporter(Request request, ResourceDoc doc,String jsonpCallback) {
		super();
		Profile groupProperties = getGroupProperties();
		cmpReporter = new CompoundJSONReporter(null,null,null,request,doc,request.getRootRef().toString(),false,null);
		cmpReporter.setGroupProperties(groupProperties);
		substanceReporter = new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(request, null);
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpCallback);
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(groupProperties)) {
			@Override
			public IStructureRecord process(IStructureRecord target)
					throws AmbitException {
				((RetrieveGroupedValuesByAlias)getQuery()).setRecord(target);
				return super.process(target);
			}
		});
		getProcessors().add(new DefaultAmbitProcessor<CompositionRelation,CompositionRelation>() {
			public CompositionRelation process(CompositionRelation target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	

	}
	protected Profile getGroupProperties() {
		String[] r = NCISearchProcessor.METHODS.names.getOpenToxEntry();
		Template gp = new Template();
		for (int i=0; i < r.length;i++) {
			Property p = new Property(r[i]);
			p.setLabel(r[i]);
			p.setEnabled(true);
			p.setOrder(i+1);
			gp.add(p);
		}
		return gp;
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
			
			StringWriter w = new StringWriter();
			cmpReporter.setOutput(w);
			cmpReporter.setComma(null);
			cmpReporter.processItem(item.getSecondStructure());
			
			getOutput().write(item.toJSON(substanceReporter.getURI(item.getFirstStructure()),w.toString()));
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
			output.write("\n],");
		} catch (Exception x) {}
		try {
			output.write("\n\"feature\":{\n");
			StringWriter w = new StringWriter();
			cmpReporter.setOutput(w);
			if (cmpReporter.getHeader()!=null)
			for (int j=0; j < cmpReporter.getHeader().size(); j++) 
				cmpReporter.getPropertyJSONReporter().processItem(cmpReporter.getHeader().get(j));
			output.write(w.toString());
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {output.write("}\n");} catch (Exception x) {}
		}
				
		try {
			output.write("\n}");
			if (jsonpCallback!=null) {
				output.write(");");
			}			
		} catch (Exception x) {}
	};
	
	
	@Override
	public void header(Writer output, Q query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			} 		
			
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
