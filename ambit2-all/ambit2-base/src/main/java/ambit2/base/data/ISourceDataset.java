package ambit2.base.data;

import java.io.Serializable;

public interface ISourceDataset extends Serializable {
	public void setName(String name);
	public String getName();
    public int getID();
    public void setID(int id);
    public String getSource();
    public void setSource(String name);
}
