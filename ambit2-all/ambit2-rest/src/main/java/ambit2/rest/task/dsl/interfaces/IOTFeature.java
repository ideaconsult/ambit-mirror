package ambit2.rest.task.dsl.interfaces;

import org.restlet.data.Reference;

import ambit2.rest.task.dsl.OTAlgorithm;


public interface IOTFeature extends IOTObject {
	/*
	IOTObject hasSource();
	void setSource(IOTObject ot);

	String getUnits();
	void setUnits(String units);
	IOTObject getSameAs();
	void setSameAs(IOTObject ot);
	
	boolean isNominal();
	void setNominal(boolean value);
	boolean isNumeric();
	void setNumeric(boolean value);	
	*/
	boolean isNominal();
	boolean isNumeric();	
	String getUnits();
	IOTObject getSameAs();
	OTAlgorithm getAlgorithm();
	IOTFeature withFeatureService(Reference uri) throws Exception;
}
