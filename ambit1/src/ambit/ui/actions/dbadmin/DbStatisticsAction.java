package ambit.ui.actions.dbadmin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Observable;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit.data.ISharedData;
import ambit.database.data.ISharedDbData;
import ambit.ui.AmbitColors;
import ambit.ui.UITools;
import ambit.ui.actions.AmbitAction;
import ambit.ui.query.DbStatusPanel;

/**
 * Displays database statistics. See example at {@link ambit.ui.actions.dbadmin.DbConnectionStatusAction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbStatisticsAction extends AmbitAction {
	public static String caption = "Statistics";
	public DbStatisticsAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,caption);
		// TODO Auto-generated constructor stub
	}

	public DbStatisticsAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/database.png"));
	}

	public DbStatisticsAction(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
	}
    public void run(ActionEvent arg0) {
        if (userData instanceof Observable) {
        	DbStatusPanel panel = new DbStatusPanel("AMBIT Database statistics",Color.white,AmbitColors.DarkClr);
        	panel.setPreferredSize(new Dimension(400,300));
        	if (userData instanceof ISharedDbData) 
        		panel.setText(getStatistics(((ISharedDbData)userData).getDbConnection().getConn()).toString());
	        JOptionPane.showMessageDialog(mainFrame,panel,((ISharedDbData)userData).getDbConnection().getDatabase() + " statistics",JOptionPane.PLAIN_MESSAGE);
	        panel = null;
        }
    }
    protected StringBuffer getStatistics(Connection connection) {
    	StringBuffer b = new StringBuffer();    	
    	if (connection == null) {
    		b.append("[DATABASE] Not connected!");
    		return b;
    	}

    	String[] sql = {
    			"Select count(*) as 'Datasets' from src_dataset",
    			"Select count(*) as 'Chemical compounds' from substance",
    			"Select count(*) as 'CAS registry numbers' from cas",
    			"Select count(*) as 'Chemical names' from name",
    			"Select count(*) as 'Other aliases and identifiers' from alias",
    			"Select count(*) as 'Literature references' from literature",
    			"Select count(*) as 'Author names' from author",
    			"Select count(*) as 'Journals' from journal",
    			"Select count(*) as 'Descriptor specifications' from ddictionary",
    			"Select count(*) as 'Descriptor values' from dvalues",
    			"Select count(*) as 'QSAR models' from qsars",
    			"Select count(*) as 'Compounds in QSAR models' from qsardata where pointtype='Training'",
    			"Select count(*) as 'Validation compounds for QSAR models' from qsardata where pointtype='Validation'",
    			"Select count(*) as 'Experimental results' from study_results",
    			"Select count(*) as 'Study types' from study",
    			"Select count(*) as 'Study templates' from template"
    	};

    	
    	try {
    		java.sql.DatabaseMetaData db = connection.getMetaData();
    		b.append(db.getDatabaseProductName());
    		b.append('\t');
    		b.append(db.getDatabaseProductVersion());
        	b.append('\n');
        	
    		b.append(db.getDriverName());
    		b.append('\t');
    		b.append(db.getDriverVersion());
        	b.append('\n');
        	
    		b.append(db.getCatalogTerm());
    		b.append('\t');        	
    		b.append(db.getURL());
        	b.append('\n');
        	b.append("user\t");
        	b.append(db.getUserName());
        	b.append('\n');
        	        	
        	b.append('\n');        	
	    	Statement st = connection.createStatement();
	    	ResultSet rs = null;
	    	for (int i=0;i < sql.length;i++) {
	    		rs = st.executeQuery(sql[i]);
	    		if (rs != null) {
	    		while (rs.next()) {	
		    		b.append(rs.getMetaData().getColumnName(1));
		    		b.append('\t');
		    		b.append(rs.getInt(1));
		    		b.append('\n');
	    		}
	    		rs.close();
	    		}
	    		rs = null;
	    		
	    	}
	    	st.close();
	    	st = null;
    	} catch (SQLException x) {
    		b.append("ERROR");
    		b.append(x.getMessage());
    		logger.error(x);
    		((ISharedData)getUserData()).getJobStatus().setError(x);
    	}
    	return b;
    }
}
