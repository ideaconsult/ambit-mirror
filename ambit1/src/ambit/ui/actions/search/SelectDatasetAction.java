package ambit.ui.actions.search;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit.data.AmbitList;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.DbSourceDatasetList;
import ambit.data.molecule.SourceDataset;
import ambit.data.molecule.SourceDatasetList;
import ambit.database.core.DbSrcDataset;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.exceptions.AmbitException;
import ambit.ui.UITools;
import ambit.ui.actions.AmbitAction;
import ambit.ui.data.AmbitListEditor;

/**
 * An action to select which dataset within database  to be searched.
 * Updates {@link ambit.database.data.AmbitDatabaseToolsData}. See example for {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class SelectDatasetAction extends AmbitAction implements Observer {

    /**
     * @param userData
     * @param mainFrame
     */
    public SelectDatasetAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Similarity method");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public SelectDatasetAction(Object userData, JFrame mainFrame, String arg0) {
        this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/dataset.png"));
        
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public SelectDatasetAction(Object userData, JFrame mainFrame,
            String arg0, Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        if (userData instanceof Observable) {
        	((Observable) userData).addObserver(this);
        	update((Observable) userData,null);
        }        
    }
    public void actionPerformed(ActionEvent arg0) {
    	
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
			try {
				dbaData.setSrcDataset(dbaData.selectDataset(new DbSourceDatasetList(dbaData.getDbConnection()),false,mainFrame,true));
			} catch (AmbitException x) {
				dbaData.getJobStatus().setError(x);
			}
		}			
		

    }
    public void update(Observable arg0, Object arg1) {
    	if (arg0 instanceof AmbitDatabaseToolsData ) {
    		SourceDataset d = ((AmbitDatabaseToolsData) arg0).getSrcDataset();
    		if (d==null) {
    			putValue(NAME,"Dataset: ALL");
    			putValue(SHORT_DESCRIPTION,"Search within all database");
    		} else {
    			putValue(NAME,"Dataset: "+d.getReference().getName());
    			putValue(SHORT_DESCRIPTION,"Search within dataset " + d.getReference().getName());
    		}
    	}
    	
    }
}
