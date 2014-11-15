package ambit2.rest.reporters;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.rest.SimpleTaskResource;
import ambit2.rest.task.Task;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * RDF generation for {@link Task}
 * @author nina
 *
 */
public class TaskRDFReporter<USERID> extends CatalogRDFReporter<UUID> {
	protected ITaskStorage<USERID> storage;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3789102915378513270L;
	protected Reference baseRef;
	protected TaskURIReporter<USERID> urireporter;
	
	public TaskRDFReporter(ITaskStorage<USERID> storage, Request request, MediaType mediaType,ResourceDoc doc) {
		super(request, mediaType,doc);
		baseRef = request.getRootRef();
		urireporter = new TaskURIReporter<USERID>(storage,request,doc);
		this.storage = storage;
	}
	@Override
	public void header(Writer output, Iterator<UUID> query) {
		super.header(output, query);
		OT.OTClass.Task.createOntClass(getJenaModel());
		getJenaModel().createAnnotationProperty(DC.title.getURI());
		getJenaModel().createAnnotationProperty(DC.date.getURI());
	}
	
	@Override
	public void processItem(UUID name, Writer output) {
		String ref;
		ITask<ITaskResult,USERID> item = storage.findTask(name.toString());
		try {
			ref = item.getUri().toString();
		} catch (Exception x) {
			ref = item.getUri().toString();
		}
	
		Individual task = getJenaModel().createIndividual(String.format("%s%s/%s", baseRef,SimpleTaskResource.resource,item.getUuid()),
													OT.OTClass.Task.getOntClass(getJenaModel()));
													
		task.addLiteral(DC.title,
				 getJenaModel().createTypedLiteral(item.getName(),XSDDatatype.XSDstring));

		task.addLiteral(DC.date,
				 getJenaModel().createTypedLiteral(item.getStarted(),XSDDatatype.XSDdateTime));		
		task.addLiteral(OT.DataProperty.hasStatus.createProperty(getJenaModel()),
				 getJenaModel().createTypedLiteral(item.getStatus(),XSDDatatype.XSDstring));		
		task.addLiteral(OT.DataProperty.percentageCompleted.createProperty(getJenaModel()),
				 getJenaModel().createTypedLiteral(item.getPercentCompleted(),XSDDatatype.XSDfloat));
		
		if (item.getError()!=null) {
			Individual error = getJenaModel().createIndividual(OT.OTClass.ErrorReport.getOntClass(getJenaModel()));
			
			ResourceException exception = item.getError();
			
			error.addLiteral(OT.DataProperty.errorCode.createProperty(getJenaModel()),exception.getStatus().getCode());
			//error.addLiteral(OT.DataProperty.actor.createProperty(getJenaModel()),);
			error.addLiteral(OT.DataProperty.message.createProperty(getJenaModel()),exception.getMessage());
			error.addLiteral(OT.DataProperty.errorDetails.createProperty(getJenaModel()),exception.getStatus().getDescription());
			
			try {
		    	StringWriter w = new StringWriter();
		    	if (exception.getCause()==null)
		    		exception.printStackTrace(new PrintWriter(w));
		    	else exception.getCause().printStackTrace(new PrintWriter(w));
				error.addLiteral(OT.DataProperty.errorCause.createProperty(getJenaModel()),w.toString());
			} catch (Exception x) {
				
			}
			
			task.addProperty(OT.OTProperty.error.createProperty(jenaModel),error);
		}
		if (item.isDone()) try {
			task.addLiteral(OT.DataProperty.resultURI.createProperty(getJenaModel()),
				 getJenaModel().createTypedLiteral(item.getUri().toString(),XSDDatatype.XSDanyURI));
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		
	}
	@Override
	public String getFileExtension() {
		if (MediaType.APPLICATION_RDF_XML.equals(mediaType)) return "rdf";
		else if (MediaType.TEXT_RDF_N3.equals(mediaType)) return "n3";
		return "rdf";
	}
}
