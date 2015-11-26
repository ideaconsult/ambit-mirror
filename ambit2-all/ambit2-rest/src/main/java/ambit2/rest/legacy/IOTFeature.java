package ambit2.rest.legacy;

import org.restlet.data.Reference;

/**
 * OpenTox Feature http://opentox.org/dev/apis/api-1.1/Feature
 * @author nina
 *
 */
@Deprecated
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
