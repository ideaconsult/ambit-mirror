package ambit2.ui.actions.search;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.ui.UITools;
import ambit2.ui.actions.AmbitAction;

/**
 * @deprecated. Use {@link QueryOptionsAction}.
 * Sets where to store query results. Options are :
 * <ul>
 * <li>{@link ambit2.database.data.ISharedDbData#MEMORY_LIST} Add query results into the list in memory.
 * <li>{@link ambit2.database.data.ISharedDbData#MEMORY_CURRENT} Replace current molecule in memory with the query result.
 * <li>{@link ambit2.database.data.ISharedDbData#RESULTS_FILE}  Export query results to a file.
 * </ul>
 * These options are interpreted by {@link ambit2.ui.actions.search.DbSearchAction}. Other application may invent other interpretations.
 * Example: creates 3 buttons and a panel to show the results. Try to play with different results destination options.
 <pre>
        JFrame frame = null;
     	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(false);
     	DbOpenAction openAction = new DbOpenAction(dbadminData,frame);
     	DbConnectionStatusAction statusAction = new DbConnectionStatusAction(dbadminData,frame,"CAS search");
     	
     	DbResultsDestinationAction resultsAction = new DbResultsDestinationAction(dbadminData,frame,"Results destination");
     	
     	DbCASSearchAction casSearchAction = new DbCASSearchAction(dbadminData,frame,"CAS search");
     	
     	CompoundPanel panel = new CompoundPanel(dbadminData.getMolecules(),null, Color.white,Color.black,JSplitPane.HORIZONTAL_SPLIT);
     	
     	JPanel mainPanel = new JPanel(new BorderLayout());
     	//Container mainPanel = frame.getContentPane();
     	mainPanel.setLayout(new BorderLayout());
     	mainPanel.setPreferredSize(new Dimension(600,400));
     	JToolBar toolbar = new JToolBar();
     	toolbar.add(openAction);
     	toolbar.add(statusAction);
     	toolbar.add(resultsAction);
     	toolbar.add(casSearchAction);
     	
     	mainPanel.add(toolbar,BorderLayout.NORTH);
     	mainPanel.add(panel,BorderLayout.CENTER);
     	
     	
     	JOptionPane.showMessageDialog(null,mainPanel);
 </pre> 
 * 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbResultsDestinationAction extends AmbitAction implements Observer {

	public DbResultsDestinationAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Results output");
	}

	public DbResultsDestinationAction(Object userData, JFrame mainFrame,
			String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/settings.png"));
	}

	public DbResultsDestinationAction(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
        if (userData instanceof Observable)
        	update(((Observable) userData),null);
	}
	public void actionPerformed(ActionEvent arg0) {
		if (userData instanceof ISharedDbData) {
			ISharedDbData dbaData = ((ISharedDbData) userData);

			String s = (String)JOptionPane.showInputDialog(
			                    mainFrame,"Options",
			                    "Select results destination",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    ISharedDbData.RESULTS_DESTINATION,
			                    ISharedDbData.RESULTS_DESTINATION[dbaData.getResultDestination()]);
	
	//		If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) 
				for (int i=0; i < ISharedDbData.RESULTS_DESTINATION.length;i++) 
					if (s.equals(ISharedDbData.RESULTS_DESTINATION[i])) {
						dbaData.setResultDestination(i);
						putValue(NAME,"Results output: " + s);
						break;
					}		
		}	
	}
	public void update(Observable arg0, Object arg1) {
    	if (arg0 instanceof AmbitDatabaseToolsData ) {
    		putValue(NAME,"Results output: " + ISharedDbData.RESULTS_DESTINATION[((AmbitDatabaseToolsData)userData).getResultDestination()]);
    		putValue(SHORT_DESCRIPTION,"Results will be stored in " + ISharedDbData.RESULTS_DESTINATION[((AmbitDatabaseToolsData)userData).getResultDestination()]);
    	}
    		
	}
}
