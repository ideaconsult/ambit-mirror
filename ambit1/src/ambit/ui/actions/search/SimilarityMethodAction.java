/*
 * Created on 2005-12-18
 *
 */
package ambit.ui.actions.search;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.readers.SearchFactory;
import ambit.ui.UITools;
import ambit.ui.actions.AmbitAction;

/**
 * Sets the similarity method to use. See example for {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class SimilarityMethodAction extends AmbitAction implements Observer{
	Object[] possibilities = {SearchFactory.title[SearchFactory.MODE_FINGERPRINT],
            SearchFactory.title[SearchFactory.MODE_ATOMENVIRONMENT]};
    /**
     * @param userData
     * @param mainFrame
     */
    public SimilarityMethodAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Similarity method");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public SimilarityMethodAction(Object userData, JFrame mainFrame, String arg0) {
        this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/search.png"));
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public SimilarityMethodAction(Object userData, JFrame mainFrame,
            String arg0, Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        if (userData instanceof Observable)
        	update(((Observable) userData),null);        
    }
    /* (non-Javadoc)
     * @see ambit.ui.actions.AmbitAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
    	
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
        
			
			String s = (String)JOptionPane.showInputDialog(
			                    mainFrame,"Options",
			                    "Select similarity method",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    possibilities,
			                    dbaData.getSimilarityMethod());
	
	//		If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
					dbaData.setSimilarityMethod(SearchFactory.getSimilarityByTitle(s));
					putValue(NAME,"Similarity method: " + s);
			}		
		}			
		

    }
    public void update(Observable arg0, Object arg1) {
    	if (arg0 instanceof AmbitDatabaseToolsData ) {
   			putValue(NAME,"Similarity method: " + ((AmbitDatabaseToolsData) arg0).getSimilarityMethod() );
   			putValue(SHORT_DESCRIPTION,"Access similarity by " + ((AmbitDatabaseToolsData) arg0).getSimilarityMethod());
    	}
    	
    }   
}
