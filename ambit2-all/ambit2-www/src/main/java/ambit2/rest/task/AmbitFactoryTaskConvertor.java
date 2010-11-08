package ambit2.rest.task;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.Request;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.Reporter;
import ambit2.rest.ResourceDoc;

/**
 * Same as parent class, with custom HTML reporter for Task object
 * @author nina
 *
 * @param <USERID>
 */
public class AmbitFactoryTaskConvertor<USERID> extends
		FactoryTaskConvertor<USERID> {
	@Override
	public synchronized Reporter<Iterator<Task<Reference, USERID>>, Writer> createTaskReporterHTML(
			Request request,ResourceDoc doc) throws AmbitException, ResourceException {
		return	new TaskHTMLReporter<USERID>(request,doc);
	}
}
