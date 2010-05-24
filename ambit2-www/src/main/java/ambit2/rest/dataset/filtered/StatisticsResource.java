package ambit2.rest.dataset.filtered;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.update.dataset.QueryCountDatasetIntersection;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.query.QueryResource;


public class StatisticsResource extends QueryResource<QueryCountDatasetIntersection,String>  {
	public static String resource = "/stats";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_PLAIN,
				MediaType.APPLICATION_JAVA_OBJECT
				
		});		
	}	
	@Override
	public IProcessor<QueryCountDatasetIntersection, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return 
		new OutputWriterConvertor(new CountReporter(),MediaType.TEXT_PLAIN);
	}

	
	@Override
	protected QueryCountDatasetIntersection createQuery(Context context,
			Request request, Response response) throws ResourceException {
		QueryCountDatasetIntersection q = new QueryCountDatasetIntersection();
		
		String[] datasetsURI =  getParams().getValuesArray(OpenTox.params.dataset_uri.toString());
		Template t = new Template(String.format("%s%s/{%s}",getRequest().getRootRef(),DatasetStructuresResource.dataset,DatasetStructuresResource.datasetKey));
		for (int i=0; i < datasetsURI.length;i++ ) {
			String datasetURI = datasetsURI[i];
			Map<String, Object> vars = new HashMap<String, Object>();
			t.parse(datasetURI, vars);
			if (i==0) q.setFieldname(vars.get(DatasetStructuresResource.datasetKey).toString());
			else q.setValue(vars.get(DatasetStructuresResource.datasetKey).toString());
		}		
		setStatus(Status.SUCCESS_OK);
		return q;
	}

}


class CountReporter extends QueryReporter<String, QueryCountDatasetIntersection, Writer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4756603556533645998L;

	@Override
	public void footer(Writer output, QueryCountDatasetIntersection query) {
		
		
	}

	@Override
	public void header(Writer output, QueryCountDatasetIntersection query) {
		
	}

	@Override
	public Object processItem(String item) throws AmbitException {
		try { 
			getOutput().write(item); 
			getOutput().write("\n");
		} catch (Exception x) {
			throw new AmbitException(x);
		};
		return item;
	}

	public void open() throws DbAmbitException {
	
	}

}