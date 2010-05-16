package ambit2.rest.task.dsl;

import org.restlet.data.Reference;

public class OTTask extends OTObject {

	 public static OTTask task(Reference uri) throws Exception  { 
		    return new OTTask(uri);
	 }
	 protected OTTask(Reference uri) {
		 super(uri);
	}
}
