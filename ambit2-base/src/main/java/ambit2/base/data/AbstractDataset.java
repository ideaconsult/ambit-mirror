package ambit2.base.data;

import java.util.Hashtable;

public class AbstractDataset implements ISourceDataset {
	protected int ID = -1;
	protected Hashtable<String, String> properties = new Hashtable<String, String>();
	public enum _props {
		name,
		source,
		seeAlso
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5988138848650016188L;

	public AbstractDataset() {
		super();
	}
	@Override
	public int getID() {
		return ID;
	}

	@Override
	public String getName() {
		return properties.get(_props.name.name());
	}

	@Override
	public String getSource() {
		return properties.get(_props.source.name());
	}

	@Override
	public void setID(int id) {
		this.ID = id;

	}

	@Override
	public void setName(String value) {
		properties.put(_props.name.name(), value);

	}

	@Override
	public void setSource(String value) {
		properties.put(_props.source.name(), value);

	}

}
