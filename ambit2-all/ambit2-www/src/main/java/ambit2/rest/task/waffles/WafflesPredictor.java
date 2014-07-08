package ambit2.rest.task.waffles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.util.Base64;
import org.restlet.resource.ResourceException;

import ambit2.core.data.model.ModelQueryResults;
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
			File results = File.createTempFile("wflprdct_",".arff");
			results.deleteOnExit();
			//TODO multi labels
			String labels = classIndex>=0?String.format("-labels %d", classIndex+1):""; //hm, ignoring 0 seems to keep numbering anyway
			waffles.predict(dataset,predictor, results,String.format("-ignore 0 %s",labels));
			
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
			 Form form = new Form(model.getContent());
			 header = getHeader(form);
			 classIndex = getClassIndex(form);
			 if ((header != null) &&  (classIndex>=0) && (classIndex<header.numAttributes()))	
		 			header.setClassIndex(classIndex);			 
 			 String model_json = new String(Base64.decode(form.getFirstValue("wafflesmodel")));
 			 model.setContent(model_json);
		 } catch (Exception x) {
			 throw new ResourceException(Status.SERVER_ERROR_INTERNAL,model.getName(),x);
		 }
		 this.model = model;
		 BufferedWriter writer = null;
		 try {
			 File file = File.createTempFile("wflmodel_",".json");
			 file.deleteOnExit();
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

