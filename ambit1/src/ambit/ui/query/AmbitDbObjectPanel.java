/**
 * Created on 2005-3-25
 *
 */
package ambit.ui.query;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.database.DbConnection;
import ambit.database.core.DbSearch;
import ambit.database.exception.DbAmbitException;
import ambit.domain.AllData;
import ambit.io.FileOutputState;
import ambit.io.MyIOUtilities;
import ambit.ui.data.AmbitObjectDialog;
import ambit.ui.data.AmbitObjectPanel;


/**
 * a GUI panel for {@link ambit.data.AmbitObject} with database functionality  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitDbObjectPanel extends AmbitObjectPanel {
	protected JLabel searchLabel = null;
	protected JLabel updateLabel = null;

	protected DbSearch dbs = null;
	protected AmbitObject searchObject = null;
	protected DbConnection dbconn;
	/**
	 * @param title
	 * @param obj
	 */
	public AmbitDbObjectPanel(String title, AmbitObject obj, DbConnection conn) {
		super(title, obj);
		this.dbconn = conn;
	}

	/**
	 * @param title
	 * @param bClr
	 * @param fClr
	 * @param obj
	 */
	public AmbitDbObjectPanel(String title, Color bClr, Color fClr,
			AmbitObject obj,DbConnection conn) {
		super(title, bClr, fClr, obj );
		this.dbconn = conn;
	}
	protected void addWidgets() {
		super.addWidgets();
		
		if (getAO() == null) return;

		   JPanel bottomPanel = new JPanel(new FlowLayout());
		   bottomPanel.setOpaque(true);
		   bottomPanel.setBackground(backClr);
		
		   GridBagConstraints cc = new GridBagConstraints();
		   
		   
		   Color ccc = Color.black;
		   searchLabel = createTitledLabel("Search","<html><u>Search</u><html>","Search in database",ccc,false);
		   searchLabel.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			search();
	   		}
		   });
		   bottomPanel.add(searchLabel);			
		   
		   JLabel l = createTitledLabel("Edit","<html><u>Edit</u><html>","Edit",ccc,false);
		   l.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog( null,
						"Edit",
					    "to be done",						
					    JOptionPane.INFORMATION_MESSAGE);
	   		}
		   });
		   
		   bottomPanel.add(l);		   
		   
		   updateLabel = createTitledLabel("Add","<html><u>Add to database</u><html>","add to database",ccc,false);
		   updateLabel.addMouseListener(new MouseAdapter() {
		   		public void mouseClicked(MouseEvent e) {
		   			add();
		   		}
		   });
		   bottomPanel.add(updateLabel);

		   l = createTitledLabel("Export","<html><u>Export</u><html>","Export to text file",ccc,false);
		   l.addMouseListener(new MouseAdapter() {
		   		public void mouseClicked(MouseEvent e) {
		   			String info = "Save to file failed.";
		   			if (MyIOUtilities.saveFile(null,"",FileOutputState.extensions,FileOutputState.extensionDescription,ao))
		   				info = "Data successfully saved";
					JOptionPane.showMessageDialog(null,
								"Status",
							    info,						
							    JOptionPane.INFORMATION_MESSAGE);		   				
		   				
		   		}
		   });
		   bottomPanel.add(l);
		   
			
			cc.insets = new Insets(5,2,5,2);

	    	cc.weightx = 1;			
	    	cc.gridwidth = GridBagConstraints.REMAINDER;
			cc.anchor = GridBagConstraints.NORTH;
		   ((GridBagLayout) layout).setConstraints(bottomPanel,cc);			
		   add(bottomPanel);		   
		
	}
	public void setConnection(DbConnection conn) {
		this.dbconn = conn;
	}
	
	protected void search() {
		if (ao == null) return;
		try {
			if (ao instanceof AmbitList) 
				searchObject = (AmbitObject) subpanel.getAO().clone();
			else if (ao instanceof AllData)			
				searchObject = (AmbitObject) subpanel.getAO().clone();				
			else 
				searchObject = (AmbitObject) ao.clone();
			searchObject.setId(-1);
			searchObject.setEditable(true);
			if (searchObject == null) return;
		} catch (CloneNotSupportedException x) {
			JOptionPane.showMessageDialog( null,
					"Search",
				    "to be done",						
				    JOptionPane.INFORMATION_MESSAGE);
			
			x.printStackTrace();
			return;
		} catch (NullPointerException x) {
			x.printStackTrace();
			return;
		}
		
		String caption = "Searching for "+ searchObject.toString();
		//TODO clone object for the search
		if ((dbconn == null) || (dbconn.getConn() == null))  {
			JOptionPane.showMessageDialog( null,
				    "Not connected to database",
					caption,
				    JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		AmbitObjectDialog d = AmbitObjectDialog.createAndShow(
				true,
				"Search",
				this,searchObject);
		d.setTitle(caption);
		/*
		Container owner = getParent();
		while (!(owner instanceof Frame)) 
			owner = owner.getParent(); 
		
		AmbitObjectDialog d = new AmbitObjectDialog((Frame) owner,true,searchObject);
		d.qpanel.setAo(searchObject);
		d.centerParent((Frame) owner);
		d.setVisible(true);
		*/
		if (d.getResult() == JOptionPane.OK_OPTION) {
			if (dbs == null) dbs = new DbSearch(dbconn);
			AmbitObject results = dbs.search(searchObject);
			if (results != null) 
				setAO(results);
			else 
				JOptionPane.showMessageDialog( null,
				    "Nothing found",						
					caption,
				    JOptionPane.INFORMATION_MESSAGE);
		}
		d = null;
	}
	
	protected void add() {
		AmbitObject addObject = ao;
		if (ao == null) return;
		if (ao instanceof AmbitList) addObject = subpanel.getAO();
		else addObject = ao;
		if (addObject == null) return;
		String caption = "Adding for "+ addObject.toString();
		//TODO clone object for the search
		if (dbconn == null)  {
			JOptionPane.showMessageDialog( null,
				    "Not connected to database",
					caption,
				    JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		if (dbs == null) dbs = new DbSearch(dbconn);
		String info = "Failed";
		try {
		if (dbs.add(addObject)) info = "Success";
		} catch (DbAmbitException x) {
			info = x.getMessage();
		}
		
				JOptionPane.showMessageDialog( null,
				    info,						
					caption,
				    JOptionPane.INFORMATION_MESSAGE);
				
	}
	

}
