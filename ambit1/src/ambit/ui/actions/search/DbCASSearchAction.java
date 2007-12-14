package ambit.ui.actions.search;

import java.sql.Connection;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.database.DbConnection;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadNameProcessor;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.database.readers.DbStructureReader;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.UniqueIDProcessor;
import ambit.ui.UITools;

/**
 * CAS number search action. Example:
 * <pre>
        JFrame frame = null;
     	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(false);
     	DbOpenAction openAction = new DbOpenAction(dbadminData,frame);
     	DbConnectionStatusAction statusAction = new DbConnectionStatusAction(dbadminData,frame,"CAS search");
     	
     	DbResultsDestinationAction resultsAction = new DbResultsDestinationAction(dbadminData,frame,"Results destination");
     	
     	DbCASSearchAction casSearchAction = new DbCASSearchAction(dbadminData,frame,"CAS search");
     	DbNameSearchAction nameSearchAction = new DbNameSearchAction(dbadminData,frame,"Chemical name search");
     	DbFormulaSearchAction formulaSearchAction = new DbFormulaSearchAction(dbadminData,frame,"Chemical formula search");
     	DbSMILESSearchAction smilesSearchAction = new DbSMILESSearchAction(dbadminData,frame,"SMILES search");
     	
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
     	toolbar.add(nameSearchAction);
     	toolbar.add(formulaSearchAction);
     	toolbar.add(smilesSearchAction);
     	
     	mainPanel.add(toolbar,BorderLayout.NORTH);
     	mainPanel.add(panel,BorderLayout.CENTER);
     	
     	
     	JOptionPane.showMessageDialog(null,mainPanel);

 * </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbCASSearchAction extends DbExactSearchAction {
	protected String sql = "select structure.idstructure,idsubstance,uncompress(structure) as ustructure,casno from structure join cas using(idstructure) where casno=";
	public DbCASSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"CAS RN");

	}

	public DbCASSearchAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/search.png"));

	}

	public DbCASSearchAction(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);

	}
	protected String getSearchCriteria(String caption, String hint, String initial) {
		return (String)JOptionPane.showInputDialog(
                mainFrame,
                hint,
                caption,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                initial);
		
	}
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query,int page, int pagesize) throws AmbitException {
		String cas = "50-00-0";
		Object c = null;
		if (query instanceof IMolecule ) {
			c = ((IMolecule) query).getProperty(CDKConstants.CASRN);
			if (c != null)
				cas = c.toString();
		} 
		if (c == null) {
			IMolecule m = ((AmbitDatabaseToolsData) userData).getMolecule();
			if (m != null) {
				c = m.getProperty(CDKConstants.CASRN);
				if (c != null) cas = c.toString();
			}
			
		} 
		cas = getSearchCriteria("Search database by CAS Registry Number", 
				"Enter CAS Registry Number\n",cas);
			//If a string was returned, say so.
		if ((cas != null) && (cas.length() > 0)) {
			try {
				cas = IdentifiersProcessor.hyphenateCAS(cas);
			} catch (Exception x) {
				logger.error(x);
			}
			if (CASNumber.isValid(cas.trim())) {
				
				return new DbStructureReader(connection,sql + '"' + cas.trim() + "\" limit 0,1" );
			} else JOptionPane.showMessageDialog(mainFrame,"Invalid CAS Registry Number!\n"+cas);
		}
		return null;
	}
	/*
	public ChemObjectWriter getWriter() {
		if (userData instanceof AmbitDatabaseToolsData) {
				AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);		
				DataContainer studyList = (DataContainer) dbaData.getMolecules();
				studyList.clear();
				return new ListOfMoleculesWriter(studyList);
		} else return null;
	}
	*/
	public IAmbitProcessor getProcessor() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);		
			DbConnection conn = dbaData.getDbConnection();
			ProcessorsChain processors = new ProcessorsChain();
			try {
			    processors.add(new UniqueIDProcessor(AmbitCONSTANTS.AMBIT_IDSUBSTANCE));
				processors.add(new ReadAliasProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadNameProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));				
			} catch (Exception x) {
				logger.error(x);
			}
			return processors;
		} else return super.getProcessor();
	}	
}
