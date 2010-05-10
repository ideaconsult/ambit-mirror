package ambit2.rest.task.dsl;

import org.restlet.data.Reference;

public class OTTask extends OTObject {

	 public static OTTask task() throws Exception  { 
		    return new OTTask();
	 }
	 @Override
	 public OTTask withUri(Reference uri) throws Exception { 
		  this.uri = uri;
		  return this; 
	 }	 
}
