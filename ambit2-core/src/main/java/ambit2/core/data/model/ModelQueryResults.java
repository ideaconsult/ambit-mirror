package ambit2.core.data.model;

import net.idea.modbcum.i.IQueryRetrieval;


/**
 * Training and test instances are available via {@link IQueryRetrieval}
 * Content: base64 encoded string
 * @author nina
 *
 */
public class ModelQueryResults extends ModelWrapper<
			String, 
			String, //training dataset uri
			String, //test dataset uri
			String,
			String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6622500017764287129L;
	protected String endpoint;
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	protected boolean hidden = false;
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	@Override
	public String toString() {
		return (getName()==null)||(getName().equals(""))?
				String.format("Model %d",getId()):
				String.format("Model %s",getName());
	}



}
