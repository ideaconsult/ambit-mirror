package ambit2.rest.task.waffles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.predictor.ModelPredictor;
import ambit2.waffles.ShellWafflesLearn;

public class WafflesPredictor extends ModelPredictor<File,File> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2836970514469085155L;
	protected ShellWafflesLearn waffles;
	
	public WafflesPredictor(Reference applicationRootReference,ModelQueryResults model, ModelURIReporter modelReporter) {
		this(applicationRootReference,model,modelReporter,null);
	}
	public WafflesPredictor(Reference applicationRootReference,ModelQueryResults model, ModelURIReporter modelReporter, String[] targetURI) {
		super(applicationRootReference,model,modelReporter,null,targetURI);
	}
	
	@Override
	public Object predict(File dataset) throws AmbitException {
		try {
			File results = File.createTempFile("waffles_results_",".arff");
			waffles.predict(dataset,predictor, results,"-ignore 0");
			System.out.println(results);
			
			return results;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			//try { if (dataset.exists()) dataset.delete();} catch (Exception x) {}
		}
	}
	@Override
	public String getCompoundURL(File target) throws AmbitException {
		return null;
	}

	@Override
	public File createPredictor(ModelQueryResults model) throws ResourceException {
		 try {
			 waffles = new ShellWafflesLearn();
		 } catch (Exception x) {
			 throw new ResourceException(Status.SERVER_ERROR_INTERNAL,model.getName(),x);
		 }
		 this.model = model;
		 BufferedWriter writer = null;
		 try {
			 File file = File.createTempFile("waffles_model_",".json");
			 writer = new BufferedWriter(new FileWriter(file));
			 writer.write(model.getContent());
			 writer.flush();
			 return file;
		 } catch (Exception x) {
			 throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,model.getName(),x);
		 } finally {
			 try { writer.close(); } catch (Exception x) {}
		 }
	}
}

