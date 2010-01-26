package ambit2.rest.task;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.concurrent.Callable;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import com.hp.hpl.jena.ontology.OntModel;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.RDFStructuresReader;
import ambit2.rest.rdf.RDFPropertyIterator;

public abstract class CallableQueryProcessor<Target,Result> implements Callable<Reference> {
	protected AbstractBatchProcessor batch; 
	protected Target target;
	protected Reference sourceReference;
	//protected AmbitApplication application;
	protected Context context;
	protected Reference applicationRootReference;

	public CallableQueryProcessor(Form form,Reference applicationRootReference,Context context) {
		Object dataset = OpenTox.params.dataset_uri.getFirstValue(form);
		this.sourceReference = dataset==null?null:new Reference(dataset.toString());
		this.context = context;
		this.applicationRootReference = applicationRootReference;
	}
	
	public Reference call() throws Exception {
		Context.getCurrentLogger().info("Start()");
		Connection connection = null;
		try {
			DBConnection dbc = new DBConnection(context);
			connection = dbc.getConnection();
			target = createTarget(sourceReference);
			batch = createBatch(target);
			
			if (batch != null) {
				batch.setCloseConnection(false);
				batch.setProcessorChain(createProcessors());
				try {
		    		if ((connection==null) || connection.isClosed()) throw new Exception("SQL Connection unavailable ");			
					batch.setConnection(connection);
				} catch (Exception x) { connection = null;}
				/*
				batch.addPropertyChangeListener(AbstractBatchProcessor.PROPERTY_BATCHSTATS,new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						context.getLogger().info(evt.getNewValue().toString());
						
					}
				});
				*/
				batch.process(target);
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
	
	protected AbstractBatchProcessor createBatch(Target target) throws Exception{
		if (target == null) throw new Exception("");
		if (target instanceof AbstractStructureQuery)
			return new DbReaderStructure();
		else
			return new RDFStructuresReader(applicationRootReference.toString());
	}
	protected abstract Target createTarget(Reference reference) throws Exception;
	protected abstract Reference createReference(Connection connection) throws Exception ;
	protected abstract ProcessorsChain<Result, IBatchStatistics, IProcessor> createProcessors() throws Exception;
	//protected abstract QueryURIReporter createURIReporter(Request request); 
	
	protected Property createPropertyFromReference(Reference attribute, LiteratureEntry le) {
		RDFPropertyIterator reader = null;
		try {
			reader = new RDFPropertyIterator(attribute);
			reader.setBaseReference(applicationRootReference);
			while (reader.hasNext()) {
				return reader.next();
			}
			return null;	
		} catch (Exception x) {
			return new Property(attribute.toString(),le);
		} finally {
			try {reader.close(); } catch (Exception x) {}
		}
	}	
	
	protected Profile createProfileFromReference(Reference ref, LiteratureEntry le) {
		return createProfileFromReference(ref, le,new Profile<Property>());
	}
	protected Profile createProfileFromReference(Reference ref, LiteratureEntry le, Profile profile) {
		
		RDFPropertyIterator reader = null;
		OntModel jenaModel = null;
		try {
			reader = new RDFPropertyIterator(ref);
			jenaModel = reader.getJenaModel();
			reader.setBaseReference(applicationRootReference);
			while (reader.hasNext()) {
				Property p = reader.next();
				p.setEnabled(true);
				profile.add(p);
			}
			
		} catch (Exception x) {
			Property p = new Property(ref.toString(),le);
			p.setEnabled(true);
			profile.add(p);
		} finally {
			try {reader.close(); } catch (Exception x) {}
			try {jenaModel.close(); } catch (Exception x) {}
		}
		return profile;	
	}		
}
