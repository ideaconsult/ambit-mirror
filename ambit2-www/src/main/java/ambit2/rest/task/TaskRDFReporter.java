package ambit2.rest.task;

import java.io.Writer;
import java.util.Date;
import java.util.Iterator;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.vocabulary.DC;

import ambit2.rest.OT;
import ambit2.rest.algorithm.CatalogRDFReporter;

/**
 * RDF generation for {@link Task}
 * @author nina
 *
 */
public class TaskRDFReporter extends CatalogRDFReporter<Task<Reference>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3789102915378513270L;

	public TaskRDFReporter(Request request, MediaType mediaType) {
		super(request, mediaType);
	}
	@Override
	public void header(Writer output, Iterator<Task<Reference>> query) {
		super.header(output, query);
		OT.OTClass.Task.createOntClass(getJenaModel());
	}

	@Override
	public void processItem(Task<Reference> item, Writer output) {
		String ref;
		try {
			ref = item.getReference().toString();
		} catch (Exception x) {
			ref = "";
		}
		Individual task = getJenaModel().createIndividual(
				ref,
				OT.OTClass.Task.getOntClass(getJenaModel()));
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
