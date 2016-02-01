package ambit2.export.isa.v1_0;

import ambit2.export.isa.v1_0.objects.OntologyAnnotation;

public class OntologyManager1_0 
{
	//TODO handle sources, terms, ...
	
	public OntologyAnnotation getOntologyAnotation(String value)
	{
		OntologyAnnotation ontAnn = new OntologyAnnotation();
		ontAnn.annotationValue = value;
		return ontAnn;
	}
	
	
	
}
