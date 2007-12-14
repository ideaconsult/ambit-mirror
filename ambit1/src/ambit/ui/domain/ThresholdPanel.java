/**
 * Created on 2005-4-4
 *
 */
package ambit.ui.domain;

import java.awt.Color;
import java.util.TreeMap;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import ambit.ui.CorePanel;


/**
 * To show/edit a threshold for the {@link ambit.domain.DataCoverage}
 * TODO toi implement it 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ThresholdPanel extends CorePanel {
	TreeMap labes = null, edits = null;
	/**
	 * @param title
	 */
	public ThresholdPanel(String title) {
		super(title);

	}

	/**
	 * @param title
	 * @param bClr
	 * @param fClr
	 */
	public ThresholdPanel(String title, Color bClr, Color fClr) {
		super(title, bClr, fClr);

	}

	/* (non-Javadoc)
	 * @see ambit.ui.CorePanel#addWidgets()
	 */
	protected void addWidgets() {
		TreeMap labels = new TreeMap();
		TreeMap edits = new TreeMap();		
		SpinnerModel model =
	        new SpinnerNumberModel(100, //initial value
	                               50, //min
	                               100, //max
	                               1);  

		createSpinner(labels,edits,"Threshold","Threshold","100",			
                "Specify the percent of points \nin the training set," +
                " which will determine \nthe domain of the model:",model);
	}

}




