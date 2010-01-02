package ambit2.rest.task;

import java.sql.Connection;
import java.util.concurrent.Callable;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.log.AmbitLogger;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.RDFStructuresReader;
import ambit2.rest.rdf.RDFPropertyIterator;

public abstract class CallableQueryProcessor<Target,Result> implements Callable<Reference> {
	protected AmbitLogger logger = new AmbitLogger(CallableQueryProcessor.class);
	protected AbstractBatchProcessor batch; 
	protected Target target;
	protected Reference sourceReference;
	protected AmbitApplication application;
	protected Reference applicationRootReference;

	public CallableQueryProcessor(Form form,Reference applicationRootReference,AmbitApplication application) {
		Object dataset = OpenTox.params.dataset_uri.getFirstValue(form);
		this.sourceReference = dataset==null?null:new Reference(dataset.toString());
		this.application = application;
		this.applicationRootReference = applicationRootReference;
	}
	
	public Reference call() throws Exception {
		Connection connection = null;
		try {
			target = createTarget(sourceReference);
			batch = createBatch(target);
			if (batch != null) {
				batch.setProcessorChain(createProcessors());
				try {
		    		connection = application.getConnection((Request)null);
		    		if (connection.isClosed()) connection = application.getConnection((Request)null);			
					batch.setConnection(connection);
				} catch (Exception x) { connection = null;}
				batch.process(target);
			}
		} catch (Exception x) {
			logger.error(x);
			throw x;
		} finally {
			try { connection.close(); } catch (Exception x) {}
		}
		try {
    		connection = application.getConnection((Request)null);
    		if (connection.isClosed()) connection = application.getConnection((Request)null);			
			return createReference(connection);
		} catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try { connection.close(); } catch (Exception x) {}
		}			
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
		try {
			reader = new RDFPropertyIterator(ref);
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
		}
		return profile;	
	}		
}
