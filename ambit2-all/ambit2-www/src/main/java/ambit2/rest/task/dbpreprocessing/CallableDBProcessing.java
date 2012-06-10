package ambit2.rest.task.dbpreprocessing;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.Algorithm;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.TaskResult;

public abstract class CallableDBProcessing<USERID> extends	CallableQueryProcessor<Object, IStructureRecord,USERID>  { 
	protected Reference applicationRootReference;
	public CallableDBProcessing(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,USERID token) {
		super(applicationRootReference,form,context,token);
		this.applicationRootReference = applicationRootReference;
	}
	
	@Override
	protected Object createTarget(Reference reference) throws Exception {
		try {
			Object q = getQueryObject(reference, applicationRootReference,context);
			return q==null?reference:q;
		} catch (Exception x) {
			return reference;
		}
	}
	
	@Override
	protected TaskResult createReference(Connection connection)
			throws Exception {
		return new TaskResult(sourceReference.toString(),false);
	}
}
