package ambit2.rest.toxpredict.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.base.io.DownloadTool;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.rest.AbstractResource;
import ambit2.rest.OpenTox;

public class SimplePredict extends ServerResource {
	protected Form params;
	protected RemoteTask task;
	protected static Properties properties;
	public SimplePredict() {
		super();
	}
	protected Form getParams() {
		if (params == null) 
			if (Method.GET.equals(getRequest().getMethod()))
				params = getRequest().getResourceRef().getQueryAsForm();
			//if POST, the form should be already initialized
			else 
				params = getRequest().getEntityAsForm();
		return params;
	}
	protected static Properties initProperties() {
		Properties p = new Properties();
		InputStream in = SimplePredict.class.getClassLoader().getResourceAsStream("ambit2/fastox/config/config.prop");
		try {
			p.load(in);
			return p;
		} catch (Exception x) {
			return null;
		} finally {
			try {in.close();} catch (Exception x) {}
		}
		
	}
	public static Reference getService(SERVICE service) {
		if (properties==null) properties = initProperties();
		return service.getValue(properties);
	}
	protected void customizeVariants(MediaType[] mimeTypes) {
	        for (MediaType m:mimeTypes) getVariants().add(new Variant(m));
	}
	@Override
	protected void doInit() throws ResourceException {
		try {
			customizeVariants(new MediaType[] {
					MediaType.TEXT_HTML,
					MediaType.TEXT_PLAIN,
					MediaType.TEXT_CSV,
					MediaType.TEXT_URI_LIST,
					MediaType.TEXT_PLAIN,
					MediaType.APPLICATION_RDF_XML,
					MediaType.APPLICATION_RDF_TURTLE,
					MediaType.TEXT_RDF_N3,
					MediaType.TEXT_RDF_NTRIPLES,
					
			});		
	
			Form form = getParams();
			String model = form.getFirstValue(OpenTox.params.model_uri.toString());
			if (model == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			String search = form.getFirstValue(AbstractResource.search_param);
			if (search==null) search= Integer.toString((int)Math.floor(100000*Math.random()));
			Reference ref = getService(SERVICE.application);
			ref.addSegment("query").addSegment("compound").addSegment(Reference.encode(search));
			ref.addQueryParameter(OpenTox.params.page.toString(),form.getFirstValue(OpenTox.params.page.toString()));
			ref.addQueryParameter(OpenTox.params.pagesize.toString(),form.getFirstValue(OpenTox.params.pagesize.toString()));
	
			Reference superservice = getService(SERVICE.application);
			superservice.addSegment("algorithm").addSegment("superservice");
			Form params = new Form();
			params.add(OpenTox.params.dataset_uri.toString(),ref.toString());
			params.add(OpenTox.params.model_uri.toString(),model);
			
			task = new RemoteTask(superservice,MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.POST);
			setStatus(Status.SUCCESS_OK);
			
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
  
	}
	protected void pipeResults(RemoteTask task,MediaType mime, OutputStream out) throws  Exception {
		ClientResource client = null;
		Representation r = null;
		try {
			client = new ClientResource(task.getResult());
			r = client.get(mime);
			if (client.getStatus().equals(Status.SUCCESS_OK)) 
				DownloadTool.download(r.getStream(),out);
			else throw new ResourceException(client.getStatus());
		} finally {
			try { client.release(); } catch (Exception x) {}
			try { r.release(); } catch (Exception x) {}
		}
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		long now = System.currentTimeMillis();
		while (!task.poll()) { 
			if ((System.currentTimeMillis()-now) > 100000) throw new ResourceException(Status.SERVER_ERROR_GATEWAY_TIMEOUT,task.getUrl().toString());
		}
		if (task.isERROR()) throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,task.getError());
		final MediaType mime = variant.getMediaType();
		return new OutputRepresentation(variant.getMediaType()) {
			@Override
			public void write(OutputStream out) throws IOException {
				try {
					pipeResults(task, mime, out);
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					throw new ResourceException(x);

				} finally {
					out.close();
				}
			}
		};		

	}
}
