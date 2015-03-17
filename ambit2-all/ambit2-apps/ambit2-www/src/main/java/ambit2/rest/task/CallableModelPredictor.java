package ambit2.rest.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.AbstractDBProcessor;

import org.codehaus.stax2.XMLOutputFactory2;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.PropertyValuesWriter;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.property.ValuesReader;
import ambit2.rest.dataset.DatasetRDFWriter;
import ambit2.rest.model.predictor.ModelPredictor;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

/**
 * 
 * @author nina
 *
 */
public class CallableModelPredictor<ModelItem,Predictor extends ModelPredictor,USERID> 
					extends CallableQueryProcessor<Object, IStructureRecord,USERID> {
	protected Reference applicationRootReference;
	protected Predictor predictor;
	public Predictor getPredictor() {
	    return predictor;
	}

	public void setPredictor(Predictor predictor) {
	    this.predictor = predictor;
	}
	protected boolean foreignInputDataset = false;
	protected String tmpFileName;
	protected String dataset_service;
	protected RDFFileWriter rdfFileWriter = null;
	
	public CallableModelPredictor(Form form, 
			Reference appReference,
			Context context,
			Predictor predictor,
			USERID token
				) {
		super(form,context,token);
		setPredictor(predictor);
		this.applicationRootReference = appReference;
	}	

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		try {
			prepareForeignProcessing(reference);
			Object q = getQueryObject(reference, applicationRootReference,context);
			return q==null?reference:q;
		} catch (Exception x) {
			return reference;
		}
	}

	protected void prepareForeignProcessing(Reference reference) throws Exception {
		foreignInputDataset = !applicationRootReference.isParent(reference);
		//foreignInputDataset = true; 
		int pos = reference.toString().lastIndexOf("/dataset/");
		if (pos>0)
			dataset_service = reference.toString().substring(0,pos+9);
	}
	
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors() throws Exception {

		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();

		if (predictor != null) {
			if (predictor.isStructureRequired()) {
				RetrieveStructure r = new RetrieveStructure(true);
				r.setPageSize(1);
				r.setPage(0);
				p1.add(new ProcessorStructureRetrieval(r));
			}
			
			IProcessor<IStructureRecord, IStructureRecord> valuesReader = getValuesReader();
			if (valuesReader != null)
				p1.add(valuesReader);
			
			p1.add(predictor);
		}
		p1.setAbortOnError(true);
		
		IProcessor<IStructureRecord, IStructureRecord> writer = getWriter();
		if (writer != null) p1.add(writer);
		
		return p1;
	}
	
	protected IProcessor<IStructureRecord,IStructureRecord> getValuesReader() {
		if  ((predictor.getModel().getPredictors().size()>0) &&  (predictor.isValuesRequired())) {
			ValuesReader readProfile = new ValuesReader(null);  //no reader necessary
			readProfile.setProfile(predictor.getModel().getPredictors());
			return readProfile;
		} else return null;
	}

	@Override
	protected IBatchStatistics runBatch(Object target) throws Exception {
		IBatchStatistics stats = super.runBatch(target);
		if (rdfFileWriter!=null) rdfFileWriter.close(); 
		//can't find good way to close processors(writers) ... TODO extension of iprocessor interface & batch 
		return stats;
	}
	
	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() throws Exception  {
		if (foreignInputDataset) {
			File file = File.createTempFile("mresult_",".rdf");
			tmpFileName = file.getAbsolutePath();
			rdfFileWriter = new RDFFileWriter(file,applicationRootReference,null);
			return rdfFileWriter;
		} else {
			PropertyValuesWriter writer = new PropertyValuesWriter();
			writer.setDataset(new SourceDataset(sourceReference.toString(),
					new LiteratureEntry(predictor.getModelReporter().getURI(predictor.getModel()),sourceReference.toString())));
			return writer;
		}
	}
	/**
	 * Returns reference to the same dataset, with additional features, predicted by the model
	 */
	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
			if (foreignInputDataset) {
				File tmpFile = new File(tmpFileName);
				if (!tmpFile.exists()) throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"No results available!");
				try {
					
					RemoteTask task = new RemoteTask(new Reference(dataset_service),MediaType.TEXT_URI_LIST,
							new FileRepresentation(tmpFile,MediaType.APPLICATION_RDF_XML),Method.POST);
					//wait to complete, so that we can delete the tmp file
					Thread.sleep(200);
					while (!task.isDone()) {
						task.poll();
						Thread.sleep(800);
						Thread.yield();
					}
					if (task.isERROR()) 
						throw task.getError();
					else	
						return new TaskResult(task.getResult().toString());
				} catch (Exception x) {
					throw x;
				} finally {
					try { if (tmpFile.exists()) tmpFile.delete();} catch (Exception x) {}
				}

			} else {
				String predicted = predictor.createResultReference();
				String q = sourceReference.toString().indexOf("?")>0?"&":"?";
				TaskResult task = new TaskResult(
						String.format("%s%s%s",
						sourceReference.toString(),
						q,
						predicted)
						);
				task.setNewResource(false);
				return task;
			}
	}

	

}

class RDFFileWriter extends AbstractDBProcessor<IStructureRecord, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7626753873168651075L;
	protected DatasetRDFWriter recordWriter;

	public RDFFileWriter(File file, Reference appReference, Template template) throws Exception {
		super();
		recordWriter = new DatasetRDFWriter(appReference,null);
		recordWriter.setTemplate(template==null?new Template():template);
		XMLStreamWriter writer = null;
		try {
			
			XMLOutputFactory factory      =  XMLOutputFactory2.newInstance();
			writer  = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(new FileOutputStream(file),"UTF-8"));
			recordWriter.setOutput(writer);
			recordWriter.header(writer);
			
		} catch (Exception  x) {
			throw new IOException(x.getMessage());
		} finally {
			
			
		}
	}
	@Override
	public void close() {
		try {
			recordWriter.footer(recordWriter.getOutput());
			
			recordWriter.close();} catch (Exception x) {}
	}
	@Override
	public IStructureRecord process(IStructureRecord target)
			throws AmbitException {
		for (Property p: target.getProperties())
			recordWriter.getTemplate().add(p);
		
		return recordWriter.process(target);
	}
	@Override
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	
	
}
