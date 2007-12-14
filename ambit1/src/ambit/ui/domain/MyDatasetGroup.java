/*
 * Created on 12-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ambit.ui.domain;

import org.jfree.data.general.DatasetGroup;


/**
 * A class to be used in {@link MyCustomRenderer}
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 12-Apr-2005
 */
public class MyDatasetGroup extends DatasetGroup {
	protected int tag = 0;
	public MyDatasetGroup(int tag) {
		super();
		this.tag = tag;
	}
	/**
	 * @return Returns the tag.
	 */
	public int getTag() {
		return tag;
	}
	/**
	 * @param tag The tag to set.
	 */
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public String toString() {
		return "Tag " + tag + " " + super.toString();
	}
	public boolean isTestDataset() {
		return tag==2;
	}
	public boolean isTrainingDataset() {
		return tag==1;
	}	
}
