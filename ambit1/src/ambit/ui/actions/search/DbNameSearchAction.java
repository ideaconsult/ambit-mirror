package ambit.ui.actions.search;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.database.DbConnection;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadCASProcessor;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.database.processors.ReadStructureProcessor;
import ambit.database.readers.DbStructureReader;
import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.ui.SpringUtilities;
import ambit.ui.UITools;

/**
 * Chemical name search in database. See example in  {@link ambit.ui.actions.search.DbCASSearchAction}.
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbNameSearchAction extends DbCASSearchAction {
	protected String sql = "select structure.idstructure,idsubstance,uncompress(structure) as ustructure,name from structure join name using(idstructure) where name ";
	protected String[][] comparisons = {
					{" = "," regexp "," sounds like "},
					{"Equals"," Substring ","Sounds like"},
					{"Exact match (case sensitive!)",
					"<html>Regular expression <br><b>\"benzene\"</b> : the string <b>\"benzene\"</b> can be anywhere within the name<br><b>\"^benzene\"</b> : The name starts with <b>\"benzene\"</b> <br><b>\"benzene$\"</b> : The name ends with <b>\"benzene\"</b>  </html>",
					"Looks for names with the same pronunciation"}
				};
	
	protected int comparison = 1;
	public DbNameSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Name");
		// TODO Auto-generated constructor stub
	}

	public DbNameSearchAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/search.png"));
	}

	public DbNameSearchAction(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
	}
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query,int page, int pagesize) throws AmbitException {
		String name = "";
		if (query instanceof Molecule ) {
			Object c = ((Molecule) query).getProperty(CDKConstants.NAMES);
			if (c != null)
				name = c.toString();
		}	
		name = getSearchCriteria("Search database by chemical name", 
				"Enter chemical name\n",name);
			//If a string was returned, say so.
		if ((name != null) && (name.length() > 0)) {
			return new DbStructureReader(connection,sql + comparisons[0][comparison] + '"' + name + "\" limit " + + page + "," + pagesize );
		}
		return null;
	}
	protected String getSearchCriteria(String caption, String hint, String initial) {
		ButtonGroup g = new ButtonGroup();
		ActionListener l = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i=0; i < 3; i++) 
					if (comparisons[0][i].equals(e.getActionCommand())) {
						comparison = i;
						System.out.println(comparisons[0][i]);
						break;
					}
				
			}
		};
		JPanel p = new JPanel();
		SpringLayout s = new SpringLayout();
		p.setLayout(s);
		
		p.add(new JLabel(hint));
		JFormattedTextField f = new JFormattedTextField(initial);
        f.setPreferredSize(new Dimension(300,32));
        f.setMinimumSize(new Dimension(200,24));
        f.setToolTipText("Use Ctrl-V to paste the text here");
		p.add(f);
		
		for (int i=0; i < 3; i++) {
			JRadioButton b = new JRadioButton(comparisons[1][i]);
			b.setSelected(i==comparison);
			b.setToolTipText(comparisons[2][i]);
			b.setActionCommand(comparisons[0][i]);
			b.addActionListener(l);
			g.add(b);
			if (i==1)
				p.add(new JLabel("Search mode:"));
			else
				p.add(new JLabel());
			p.add(b);
		}
		SpringUtilities.makeCompactGrid(p, 4, 2,2,2,2,2);
		if (JOptionPane.showConfirmDialog(mainFrame, p, caption, JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			return f.getText();
		} else return null;
		
		
		/*
		return (String)JOptionPane.showInputDialog(
                mainFrame,
                hint,
                caption,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                initial);
                */
		
	}	
	public IAmbitProcessor getProcessor() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);		
			DbConnection conn = dbaData.getDbConnection();
			ProcessorsChain processors = new ProcessorsChain();
			try {
				processors.add(new ReadStructureProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadCASProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadAliasProcessor(dbaData.getDbConnection().getConn()));
			} catch (Exception x) {
				logger.error(x);
			}
			return processors;
		} else return super.getProcessor();
	}
}
