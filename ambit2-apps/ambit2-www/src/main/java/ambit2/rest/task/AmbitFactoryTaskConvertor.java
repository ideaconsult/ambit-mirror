package ambit2.rest.task;

import java.io.Writer;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.Request;
import org.restlet.resource.ResourceException;

import ambit2.rest.DisplayMode;

/**
 * Same as parent class, with custom HTML reporter for Task object
 * @author nina
 *
 * @param <USERID>
 */
public class AmbitFactoryTaskConvertor<USERID> extends
		FactoryTaskConvertor<USERID> {
	
	public AmbitFactoryTaskConvertor(ITaskStorage<USERID> storage) {
		super(storage);
	}	
	
	@Override
	public synchronized Reporter<java.util.Iterator<UUID>,Writer> createTaskReporterHTML(Request request, ResourceDoc doc, DisplayMode _dmode) throws AmbitException ,ResourceException {
		return	new TaskHTMLReporter<USERID>(storage,request,doc, _dmode);
	}
}
