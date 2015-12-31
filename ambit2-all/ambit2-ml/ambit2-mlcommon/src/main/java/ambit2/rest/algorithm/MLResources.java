package ambit2.rest.algorithm;

import ambit2.rest.OpenTox;

public class MLResources {
	public final static String algorithm = OpenTox.URI.algorithm.getURI();
	public final static String algorithmKey = OpenTox.URI.algorithm.getKey();
	
	public final static String model_resource = OpenTox.URI.model.getURI();
	public final static String model_resourcekey =  OpenTox.URI.model.getKey();
	public final static String model_resourceID = OpenTox.URI.model.getResourceID();
	
	private MLResources() {
	}
}
