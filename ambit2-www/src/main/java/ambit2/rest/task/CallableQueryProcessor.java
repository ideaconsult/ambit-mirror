package ambit2.rest.task;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.util.UUID;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ClientResource;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.RDFStructuresReader;

public abstract class CallableQueryProcessor<Target,Result> implements CallableTask {
	protected AbstractBatchProcessor batch; 
	protected Target target;
	protected Reference sourceReference;
	//protected AmbitApplication application;
	protected Context context;
	protected UUID uuid;

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public CallableQueryProcessor(Form form,Context context) {
		Object dataset = OpenTox.params.dataset_uri.getFirstValue(form);
		this.sourceReference = dataset==null?null:new Reference(dataset.toString().trim());
		this.context = context;
	}
	
	public Reference call() throws Exception {
		
		Context.getCurrentLogger().info("Start()");
		Connection connection = null;
		try {
			DBConnection dbc = new DBConnection(context);
			connection = dbc.getConnection();
			try {
				target = createTarget(sourceReference);
			} catch (Exception x) {
				target = (Target)sourceReference;
			}
			
			batch = createBatch(target);
			
			if (batch != null) {
				batch.setCloseConnection(false);
				batch.setProcessorChain(createProcessors());
				try {
		    		if ((connection==null) || connection.isClosed()) throw new Exception("SQL Connection unavailable ");			
					batch.setConnection(connection);
					batch.open();
				} catch (Exception x) { connection = null;}
				/*
				batch.addPropertyChangeListener(AbstractBatchProcessor.PROPERTY_BATCHSTATS,new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						context.getLogger().info(evt.getNewValue().toString());
						
					}
				});
				*/
				IBatchStatistics stats = runBatch(target);
			}
			return createReference(connection);
		} catch (Exception x) {

            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
            x.printStackTrace(new PrintWriter(stackTraceWriter));
			Context.getCurrentLogger().severe(stackTraceWriter.toString());
			throw x;
		} finally {
			Context.getCurrentLogger().info("Done");
			try { connection.close(); } catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
		/*
		try {
    		//connection = application.getConnection((Request)null);
    		//if (connection.isClosed()) connection = application.getConnection((Request)null);			
			return createReference(connection);
		} catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try { connection.close(); } catch (Exception x) {}
		}		
		*/	
		
	}
	protected IBatchStatistics runBatch(Target target) throws Exception {
		return batch.process(target);
	}
	
	protected AbstractBatchProcessor createBatch(Target target) throws Exception{
		if (target == null) throw new Exception("");
		if (target instanceof AbstractStructureQuery) {
			DbReaderStructure reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else
			return new RDFStructuresReader(target.toString());
	}
	protected abstract Target createTarget(Reference reference) throws Exception;
	protected abstract Reference createReference(Connection connection) throws Exception ;
	protected abstract ProcessorsChain<Result, IBatchStatistics, IProcessor> createProcessors() throws Exception;
	//protected abstract QueryURIReporter createURIReporter(Request request); 
	

	
	public static Object getQueryObject(Reference reference, Reference applicationRootReference) throws Exception {
		
		if (!applicationRootReference.isParent(reference)) throw 
			new Exception(String.format("Remote reference %s %s",applicationRootReference,reference));
		ObjectRepresentation<Serializable> repObject = null;
		ClientResource resource = null;
		try {
			resource  = new ClientResource(reference);
			resource.setMethod(Method.GET);
			resource.get(MediaType.APPLICATION_JAVA_OBJECT);
			if (resource.getStatus().isSuccess()) {
				repObject = new ObjectRepresentation<Serializable>(resource.getResponseEntity());
				Serializable object = repObject.getObject();
				return object;
			}
			return reference;
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (repObject!=null) repObject.release();} catch (Exception x) {}
			try { if (resource!=null) resource.release();} catch (Exception x) {}
		}
	}		
}
