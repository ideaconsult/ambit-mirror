package ambit2.rest.legacy;


import java.io.Serializable;

import org.restlet.data.Reference;

@Deprecated
public interface IOTObject  extends Serializable {
	String getName();
	IOTObject withName(String name);
	
	Reference getUri();
	
	boolean isSelected();
	void setSelected(boolean value);
	
	boolean isForbidden();
	public void get(String mediaType) throws Exception;
	public IOTObject getPage(int page,int pageSize) throws Exception;
}
