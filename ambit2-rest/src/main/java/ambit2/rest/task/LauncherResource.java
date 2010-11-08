package ambit2.rest.task;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.rest.OpenTox;
import ambit2.rest.TaskApplication;

public class LauncherResource extends ServerResource {
	protected String dataset_uri;
	protected String model_uri;
	protected Form input;
	protected Reference result;
	
	protected Reference dataset_service;
	protected Reference application_root;
	
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		input = getRequest().getResourceRef().getQueryAsForm();
		dataset_uri = input.getFirstValue(OpenTox.params.dataset_uri.toString());
		model_uri = input.getFirstValue(OpenTox.params.model_uri.toString());
		this.dataset_service = getDatasetService();
		this.application_root = getApplicationRoot();

	}
	protected Reference getDatasetService() {
		return new Reference(String.format("%s/%s",
				getRequest().getRootRef(),OpenTox.URI.dataset.toString()));
	}
	protected Reference getApplicationRoot() {
		return getRequest().getRootRef();
	}	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		
		return new OutputRepresentation(MediaType.TEXT_HTML) {
			@Override
			public void write(OutputStream stream) throws IOException {
				OutputStreamWriter writer = null;  
				try {
            		writer = new OutputStreamWriter(stream,"UTF-8");	
            		writer.write(String.format("<html><head><title>%s</title></head><body>","Model Launcher"));
            		writer.write("<h3>Model launcher</h3>");
            		writer.write("<h5>Calculate descriptors, prepares a dataset and runs the model</h5>");
            		writer.write(String.format("<form action='' method='%s' name='form'>","POST"));
            		writer.write(String.format("Dataset URI&nbsp;<input type='text'  size='120'  name='dataset_uri' value='%s'><br>",dataset_uri==null?"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/169":dataset_uri));
            		writer.write(String.format("Model URI&nbsp;<input type='text' size='120' name='model_uri' value='%s'><br>",model_uri==null?"http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/TUMOpenToxModel_kNN_6":model_uri));
            		writer.write(String.format("<input type='submit' name='launch' value='%s'><br>","Run"));
            		writer.write("</form>");
					//Callable<Reference> c = createCallable();
					//c.call();
            		writer.flush();
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
					try {writer.write(String.format("</body></html>"));} catch (Exception x) {}
				}
				//return new StringRepresentation("Expects dataset_uri, feature_uris[], model_uri");
				
			}
		};

	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			//System.out.println(getClass().getName());
			Form form = new Form(entity);
			//System.out.println(form);
			Task<Reference,Object> task =  ((TaskApplication)getApplication()).addTask(
					getRequest().getRootRef().toString(),
					createCallable(form),
					getRequest().getRootRef(),false);		
			task.update();
			//System.out.println(task);
			setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
			FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>();
			return tc.createTaskRepresentation(task, variant,getRequest(), getResponse(),null);

		} catch (Exception x) {
			x.printStackTrace();
			if (x.getCause() instanceof ResourceException)
				getResponse().setStatus( ((ResourceException)x.getCause()).getStatus());
			else
				getResponse().setStatus(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
		}
		return null;
	}
	
	protected Callable<Reference> createCallable(Form form) throws ResourceException {
				
		try {
			if (form.getFirstValue(OpenTox.params.dataset_service.toString())==null)
				form.add(OpenTox.params.dataset_service.toString(), getDatasetService().toString());
			return new CallablePOST(form,getRequest().getRootRef());		
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} finally {
			//try { connection.close(); } catch (Exception x) {}
		}
		

			
	}	
}
