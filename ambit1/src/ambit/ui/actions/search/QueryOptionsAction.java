/*
 * Created on 2005-12-18
 *
 */
package ambit.ui.actions.search;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit.database.data.AmbitDatabaseToolsData;
import ambit.ui.UITools;
import ambit.ui.actions.AmbitAction;
import ambit.ui.query.SimilarityOptions;

/**
 * Sets the similarity method to use. See example for {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class QueryOptionsAction extends AmbitAction implements Observer{

    /**
     * @param userData
     * @param mainFrame
     */
    public QueryOptionsAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Options");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public QueryOptionsAction(Object userData, JFrame mainFrame, String arg0) {
        this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/search.png"));
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public QueryOptionsAction(Object userData, JFrame mainFrame,
            String arg0, Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        putValue(SHORT_DESCRIPTION, "Various options influencing search results");
        if (userData instanceof Observable)
        	update(((Observable) userData),null);        
    }
    /* (non-Javadoc)
     * @see ambit.ui.actions.AmbitAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
    	
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
            SimilarityOptions options = new SimilarityOptions(dbaData);
            options.setPreferredSize(new Dimension(400,150));
            if (JOptionPane.showConfirmDialog(mainFrame,options,"Search options",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            	dbaData.setTMPFile(options.getTMPFile());
                dbaData.setSimilarityMethod(options.getMethod());
                dbaData.setSimilarityThreshold(options.getThreshold());
                dbaData.setPageSize(options.getMaxResults());
                dbaData.setPage(options.getPage());
                dbaData.setAquire_endpoint(options.getEndpoint());
                dbaData.setAquire_endpoint_description(options.getEndpointDescription());
                dbaData.setAquire_species(options.getSpecies());
                dbaData.setResultDestination(options.getDestination());
                dbaData.setSource(options.getSource());
                dbaData.setAquire_simpletemplate(options.isUseSimpletemplate());
                //putValue(NAME,"Similarity method: " + options.getMethod());
            }
            options = null;
			/*
			String s = (String)JOptionPane.showInputDialog(
			                    mainFrame,"Options",
			                    "Select similarity method",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    possibilities,
			                    dbaData.getSimilarityMethod());
	
	//		If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
					dbaData.setSimilarityMethod(s);
					putValue(NAME,"Similarity method: " + s);
			}
            */		
		}			
		

    }
    public void update(Observable arg0, Object arg1) {
    	if (arg0 instanceof AmbitDatabaseToolsData ) {
   			//putValue(NAME,"Similarity method: " + ((AmbitDatabaseToolsData) arg0).getSimilarityMethod() );
   			//putValue(SHORT_DESCRIPTION,"Access similarity by " + ((AmbitDatabaseToolsData) arg0).getSimilarityMethod());
    	}
    	
    }   
}

