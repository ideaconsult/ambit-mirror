package ambit2.db;

import java.io.Serializable;

public class PropertiesTuple implements Serializable {
	protected int id;
	protected SourceDataset dataset;
	
	public PropertiesTuple() {
		this(-1,null);
	}
	public PropertiesTuple(int id, SourceDataset dataset) {
		if (dataset==null) setDataset(new SourceDataset());
		else setDataset(dataset);
		setId(id);
	}	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public SourceDataset getDataset() {
		return dataset;
	}
	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
	}
	
	@Override
	public String toString() {
		//return dataset!=null?dataset.toString():Integer.toString(getId());
		return String.format("Tuple %d", getId());
	}
}
