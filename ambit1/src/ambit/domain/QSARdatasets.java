/**
 * Created on 2005-1-18
 *
 */
package ambit.domain;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;



/**
 * A List of {@link QSARDataset}s 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class QSARdatasets extends AmbitList {
	/**
	 * 
	 */
	public QSARdatasets() {
		super();
	}
	
	public QSARDataset getDataSet(int i) {
		try {
			return (QSARDataset) (getItem(i));
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * @return Returns the caption of a given dataset.
	 */
	public String getCaption(int i) {
		if (i<0) return ""; else
		return getDataSet(i).getName();
	}
	/**
	 * @return Returns the filename a given dataset.
	 */
	public String getFilename(int i) {
		if (i <0) return ""; else
		return getDataSet(i).model.getFileWithData();
	}
	/**
	 * @return Returns the xnames a given dataset.
	 */
	/*
	public String[] getXnames(int i) {
		return getDataSet(i).getXnames();
	}
	*/
	/**
	 * @return Returns the yname a given dataset.
	 */
	public String getYname(int i) {
		if (i <0) return "" ; else 
		return getDataSet(i).getYname();
	}
	/**
	 * @return Returns the npoints a given dataset.
	 */
	public int getNpoints(int i) {
		if (i <0) return 0; else		
		return getDataSet(i).getNpoints();
	}
	
	public int getNdescriptors(int i) {
		if (i <0) return 0; else		
		return getDataSet(i).getNdescriptors();
	}

	public boolean isVisible(int i) {
		if (i <0) return false; else		
		return getDataSet(i).isVisible();
	}

	public void setVisible(int i,boolean v) {
		if (i >=0) getDataSet(i).setVisible(v);
	}
	
	public boolean isAdEstimated(int i) {
		if (i <0) return false; else		
		return getDataSet(i).isAdEstimated();
	}
	 public void refreshMethod() {
	 	for (int i = 0; i < size(); i++) {
	 		getDataSet(i).refreshMethod();
	 	}
		setSelectedIndex(getSelectedIndex());		
		fireAmbitObjectEvent();	 	
	 }	
	 public void setMethod(DataCoverage method) {
	 	for (int i = 0; i < size(); i++) {
	 		getDataSet(i).setMethod(method);
	 	}
		setSelectedIndex(getSelectedIndex());		
		fireAmbitObjectEvent();	 	
	 }
  	 public AmbitObject createNewItem() {
			return new QSARDataset();
	}	
  	 
 	/**
 	 * @param selectedIndex The selectedIndex to set.
 	 */
 	public void setSelectedIndex(int selectedIndex, boolean notify) {
 		if (this.selectedIndex != selectedIndex) { 
 			this.selectedIndex = selectedIndex;
 		}
 		if (notify) {
 			fireAmbitListEvent();
 			QSARDataset s = (QSARDataset) getSelectedItem();
 			if (s != null) {
 				s.fireAmbitObjectEvent(s);
				fireAmbitObjectEvent(s.coverage); 					
 			}
 		}
 	}
  	 
	 
}
