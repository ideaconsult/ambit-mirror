package ambit2.rest.task.dsl.interfaces;

import org.restlet.data.Reference;

public interface IOTObject {
	String getName();
	IOTObject withName(String name);
	
	Reference getUri();
	
}
