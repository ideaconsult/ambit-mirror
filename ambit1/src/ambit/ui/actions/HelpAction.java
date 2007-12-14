package ambit.ui.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.Icon;
import javax.swing.JFrame;

import ambit.applications.dbadmin.AmbitDatabase;
import ambit.exceptions.AmbitException;

public class HelpAction extends AmbitAction {
	private HelpBroker fHelp = null;
	private CSH.DisplayHelpFromSource fDisplayHelp = null;

	public HelpAction(Object userData, JFrame mainFrame) {
		this(userData,mainFrame,"Help",null);
		
	}

	public HelpAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData,mainFrame,arg0,null);
	}

	public HelpAction(Object userData, JFrame mainFrame, String arg0, Icon arg1) {
		super(userData, mainFrame,arg0,arg1);
	    putValue(SHORT_DESCRIPTION, "Ambit Database Help");
	    putValue(LONG_DESCRIPTION, "Help");
	    try {
	    	initHelpSystem();
	    } catch (Exception x){
	    	x.printStackTrace();
	    }
	}
	
	public void actionPerformed(ActionEvent event) {
		try {
		fHelp.setCurrentView(new View("TOC").toString());
	    fDisplayHelp.actionPerformed( event );
		} catch (Exception x) {
			x.printStackTrace();
			
		}

	}
	private void initHelpSystem() throws AmbitException {
	    //optimization to avoid repeated init
	    if ( fHelp != null && fDisplayHelp != null) return;
	    
	    //(uses the classloader mechanism)
	    ClassLoader loader = AmbitDatabase.class.getClassLoader();
	    URL helpSetURL = HelpSet.findHelpSet(loader, "ambit/applications/dbadmin/help/help.hs");
	    if (helpSetURL == null) throw new AmbitException("Cannot find help system.");
	    try {
	      HelpSet helpSet = new HelpSet(null, helpSetURL);
	      fHelp = helpSet.createHelpBroker();
	      //fHelp.enableHelpKey( mainFrame.getRootPane(), "Getting started", helpSet );
	      fDisplayHelp = new CSH.DisplayHelpFromSource(fHelp);
	    }
	    catch (HelpSetException ex) {
	      throw new AmbitException("Cannot create help system with: " + helpSetURL);
	    }
	    if (fHelp == null) throw new AmbitException("Cannot find help system.");
	  }
	
	/** 
	  * Type-safe enumeration for the style of presentation of the 
	  * the Help system. 
	  */
	  static final class View { 
	    //All Strings used here must match the helpset identifier for 
	    //the particular view.
	    static final View Search =  new View("Search");
	    static final View Contents =  new View("TOC");
	    static final View Index =  new View("Index");
	    public String toString() { 
	      return fName;  
	    } 
	    private final String fName;
	    private View(String aName) { 
	      fName = aName;
	    }
	  }  

}
