package ambit2.ui.domain;

import ambit2.domain.QSARDataset;

public interface IADPlotsType {
	public String getXLabel(QSARDataset ds);
	public String getYLabel(QSARDataset ds);
	public boolean isXDescriptor();
	public boolean isYDescriptor();
    public int getXindex();
    public void setXindex(int xindex);
    public int getYindex();
    public void setYindex(int yindex);
    void setName(String name);
    String getName();
    public String[] predefinedvalues();
    public String getTitle();
}
