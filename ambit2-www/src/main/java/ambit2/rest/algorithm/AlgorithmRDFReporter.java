package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Parameter;
import ambit2.rest.OT;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * RDF output for {@link Algorithm}
 * @author nina
 *
 */
public class AlgorithmRDFReporter extends CatalogRDFReporter<Algorithm> {
	protected AlgorithmURIReporter reporter;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2332767360556001891L;
	public AlgorithmRDFReporter(Request request,MediaType mediaType) {
		super(request,mediaType);
		reporter =  new AlgorithmURIReporter(request);
	}
	@Override
	public void processItem(Algorithm item, Writer output) {

		Individual algorithm = getJenaModel().createIndividual(
				reporter.getURI(item),
				OT.OTClass.Algorithm.getOntClass(getJenaModel()));
		algorithm.addLiteral(DC.title,
				 getJenaModel().createTypedLiteral(item.getName(),XSDDatatype.XSDstring));
		algorithm.addLiteral(DC.identifier,
				 getJenaModel().createTypedLiteral(reporter.getURI(item),XSDDatatype.XSDanyURI));
		algorithm.addLiteral(DC.date,
				 getJenaModel().createTypedLiteral(new Date(System.currentTimeMillis()),XSDDatatype.XSDdateTime));		
		algorithm.addLiteral(DC.format,
				 getJenaModel().createTypedLiteral(item.getFormat()==null?"":item.getFormat(),XSDDatatype.XSDstring));		
		algorithm.addLiteral(DC.description,
				 getJenaModel().createTypedLiteral(item.getDescription()==null?"":item.getDescription(),XSDDatatype.XSDstring));			
		algorithm.addLiteral(DC.publisher,
				 getJenaModel().createTypedLiteral(reporter.getURI(null),XSDDatatype.XSDanyURI));	
		
		if ((item.getType()!=null) && !"".equals(item.getType()))
			algorithm.addProperty(OT.isA, item.getType());
		
		List<String> stats = item.getStatistics();
		if (stats != null)
		for (String  stat : stats)
			algorithm.addLiteral(OT.statisticsSupported,
					 getJenaModel().createTypedLiteral(stats.toString(),XSDDatatype.XSDstring));

		List<Parameter> params = item.getParameters();
		if (params != null)
		for (Parameter param :params) {
			Individual iparam = getJenaModel().createIndividual(
					OT.OTClass.Parameter.getOntClass(getJenaModel()));
			iparam.addLiteral(OT.paramScope,
					 getJenaModel().createTypedLiteral(param.getScope(),XSDDatatype.XSDstring));	
			iparam.addLiteral(OT.paramValue,
					 getJenaModel().createTypedLiteral(param.getValue(),
							 param.getValue() instanceof Number?
									 (param.getValue() instanceof Integer)?XSDDatatype.XSDinteger:XSDDatatype.XSDdouble:
									 XSDDatatype.XSDstring));	
			algorithm.addProperty(OT.parameters, iparam);
		}
	}
	
	@Override
	public void header(Writer output, Iterator<Algorithm> query) {
		super.header(output, query);
		OT.OTClass.Algorithm.createOntClass(getJenaModel());
		OT.OTClass.Parameter.createOntClass(getJenaModel());
	}
	
	

}
