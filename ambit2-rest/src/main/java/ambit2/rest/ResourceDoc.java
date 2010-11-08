package ambit2.rest;

import java.io.Serializable;

public class ResourceDoc implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1857072019492981256L;
	protected static String rootDocURL = "http://opentox.org/dev/apis/api-1.1/%s";
	protected static String rootOWLURL = "http://opentox.org/api/1_1/opentox.owl#%s";
	
	protected String primaryTopic = "";
	protected String primaryDoc = "";
	
	public ResourceDoc() {
	}
	
	public ResourceDoc(String primaryDoc,String primaryTopic) {
		setPrimaryDoc(primaryDoc);
		setPrimaryTopic(primaryTopic);
	}
	public String getResource() {
		return primaryTopic;
	}
	public String getPrimaryDoc() {
		return String.format(rootDocURL, primaryDoc);
	}
	public void setPrimaryDoc(String primaryDoc) {
		this.primaryDoc = primaryDoc;
	}
	public String getPrimaryTopic() {
		return String.format(rootOWLURL, primaryTopic);
	}
	public void setPrimaryTopic(String primaryTopic) {
		this.primaryTopic = primaryTopic;
	}
}
