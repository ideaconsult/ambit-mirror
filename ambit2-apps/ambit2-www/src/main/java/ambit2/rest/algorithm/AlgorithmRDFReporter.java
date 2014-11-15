package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Parameter;
import ambit2.rest.BO;
import ambit2.rest.rdf.OTA;
import ambit2.rest.rdf.OTA.OTAClass;
import ambit2.rest.reporters.CatalogRDFReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
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
	public AlgorithmRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
		reporter =  new AlgorithmURIReporter(request,doc);
	}
	@Override
	public void processItem(Algorithm item, Writer output) {

		Individual algorithm = getJenaModel().createIndividual(
				reporter.getURI(item),
				OT.OTClass.Algorithm.getOntClass(getJenaModel()));
		
		algorithm.addLiteral(DC.title,
				 getJenaModel().createTypedLiteral(item.getName(),XSDDatatype.XSDstring));
		algorithm.addLiteral(DC.date,
				 getJenaModel().createTypedLiteral(new Date(System.currentTimeMillis()),XSDDatatype.XSDdateTime));		
		algorithm.addLiteral(DC.format,
				 getJenaModel().createTypedLiteral(item.getFormat()==null?"":item.getFormat(),XSDDatatype.XSDstring));		
		algorithm.addLiteral(DC.description,
				 getJenaModel().createTypedLiteral(item.getDescription()==null?"":item.getDescription(),XSDDatatype.XSDstring));			
		algorithm.addLiteral(DC.publisher,
				 getJenaModel().createTypedLiteral(reporter.getURI(null),XSDDatatype.XSDanyURI));	
		
		if (item.getImplementationOf() !=null) {
			Individual boAlgorithm = getJenaModel().createIndividual(
					item.getImplementationOf(),
					BO.BOClass.MolecularDescriptor.getOntClass(getJenaModel()));
			algorithm.addProperty(BO.instanceOf,boAlgorithm);
		}
		
		String[] types = item.getType();
		for (String type:types)
			if ((type!=null) && !"".equals(type)) {
				
				for (OTAClass value: OTA.OTAClass.values()) 
					if (value.getNS().equals(type)) {
						OntClass c = value.createOntClass(getJenaModel());
						//algorithm.addProperty(RDF.type, type); 
						algorithm.addOntClass(c);
					}
				//algorithm.addProperty(OT.OTProperty.isA.createProperty(getJenaModel()), type);
				
			}
		
		List<Parameter> params = item.getParameters();
		if (params != null)
		for (Parameter param :params) {
			Individual iparam = getJenaModel().createIndividual(
					OT.OTClass.Parameter.getOntClass(getJenaModel()));
			iparam.addLiteral(OT.DataProperty.paramScope.createProperty(getJenaModel()),
					 getJenaModel().createTypedLiteral(param.getScope(),XSDDatatype.XSDstring));	
			iparam.addLiteral(OT.DataProperty.paramValue.createProperty(getJenaModel()),
					 getJenaModel().createTypedLiteral(param.getValue(),
							 param.getValue() instanceof Number?
									 (param.getValue() instanceof Integer)?XSDDatatype.XSDinteger:XSDDatatype.XSDdouble:
									 XSDDatatype.XSDstring));	
			algorithm.addProperty(OT.OTProperty.parameters.createProperty(getJenaModel()), iparam);
		}
	}
	
	@Override
	public void header(Writer output, Iterator<Algorithm> query) {
		super.header(output, query);
		try {
			getJenaModel().setNsPrefix("bo","http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#");
			getJenaModel().setNsPrefix("bo1","http://ambit.sourceforge.net/descriptors.owl#");
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		OT.OTClass.Algorithm.createOntClass(getJenaModel());
		OT.OTClass.Parameter.createOntClass(getJenaModel());
		/*
		OTA.OTAClass.Classification.createOntClass(getJenaModel());
		OTA.OTAClass.Clustering.createOntClass(getJenaModel());
		OTA.OTAClass.Regression.createOntClass(getJenaModel());
		OTA.OTAClass.Rules.createOntClass(getJenaModel());
		OTA.OTAClass.Learning.createOntClass(getJenaModel());
		OTA.OTAClass.LazyLearning.createOntClass(getJenaModel());
		OTA.OTAClass.EagerLearning.createOntClass(getJenaModel());
		OTA.OTAClass.SingleTarget.createOntClass(getJenaModel());
		OTA.OTAClass.DescriptorCalculation.createOntClass(getJenaModel());
		*/
		getJenaModel().createAnnotationProperty(DC.title.getURI());
		getJenaModel().createAnnotationProperty(DC.description.getURI());
		getJenaModel().createAnnotationProperty(DC.type.getURI());
		getJenaModel().createAnnotationProperty(DC.publisher.getURI());
		getJenaModel().createAnnotationProperty(DC.format.getURI());
		getJenaModel().createAnnotationProperty(DC.date.getURI());		

	}


	

}
