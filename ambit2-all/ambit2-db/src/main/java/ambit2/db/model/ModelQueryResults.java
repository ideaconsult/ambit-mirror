package ambit2.db.model;

import ambit2.core.data.model.ModelWrapper;
import ambit2.db.readers.IQueryRetrieval;

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
			String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6622500017764287129L;
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
