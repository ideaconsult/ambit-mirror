package ambit2.rest.task;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.algorithm.CatalogRDFReporter;
import ambit2.rest.rdf.OT;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * RDF generation for {@link Task}
 * @author nina
 *
 */
public class TaskRDFReporter<USERID> extends CatalogRDFReporter<Task<Reference,USERID>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3789102915378513270L;
	protected Reference baseRef;
	public TaskRDFReporter(Request request, MediaType mediaType) {
		super(request, mediaType);
		baseRef = request.getRootRef();
	}
	@Override
	public void header(Writer output, Iterator<Task<Reference,USERID>> query) {
		super.header(output, query);
		OT.OTClass.Task.createOntClass(getJenaModel());
		getJenaModel().createAnnotationProperty(DC.title.getURI());
		getJenaModel().createAnnotationProperty(DC.identifier.getURI());
		getJenaModel().createAnnotationProperty(DC.date.getURI());
	}

	@Override
	public void processItem(Task<Reference,USERID> item, Writer output) {
		String ref;
		try {
			ref = item.getReference().toString();
		} catch (Exception x) {
			ref = item.getUri().toString();
		}
		Individual task = getJenaModel().createIndividual(OT.OTClass.Task.getOntClass(getJenaModel()));
		task.addLiteral(DC.title,
				 getJenaModel().createTypedLiteral(item.getName(),XSDDatatype.XSDstring));
		task.addLiteral(DC.identifier,
				 getJenaModel().createTypedLiteral(ref,XSDDatatype.XSDanyURI));
		task.addLiteral(DC.date,
				 getJenaModel().createTypedLiteral(item.getStarted(),XSDDatatype.XSDdateTime));		
		task.addLiteral(OT.DataProperty.hasStatus.createProperty(getJenaModel()),
				 getJenaModel().createTypedLiteral(item.getStatus(),XSDDatatype.XSDstring));		
		task.addLiteral(OT.DataProperty.percentageCompleted.createProperty(getJenaModel()),
				 getJenaModel().createTypedLiteral(item.getPercentCompleted(),XSDDatatype.XSDfloat));			
		
	}

}
