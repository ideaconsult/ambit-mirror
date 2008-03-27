package ambit2.ui.actions.search;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.DbConnection;
import ambit2.data.molecule.Compound;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.ReadAliasProcessor;
import ambit2.database.processors.ReadNameProcessor;
import ambit2.database.processors.ReadSMILESProcessor;
import ambit2.database.processors.ReadStructureProcessor;
import ambit2.database.search.DbExactSearchReader;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.structure.UniqueIDProcessor;
import ambit2.ui.UITools;

/**
 * Exact search in database. See example for {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbExactSearchAction extends DbSearchAction {

	public DbExactSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Search compound");
	}

	public DbExactSearchAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/search.png"));
	}

	public DbExactSearchAction(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		interactive = false;
		putValue(AbstractAction.SHORT_DESCRIPTION,arg0);
	}

	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query,int page, int pagesize) throws AmbitException {
	    
		IMolecule mol = null;
		if (query instanceof IMolecule ) mol = (IMolecule) query;
		else if (query instanceof Compound) mol = ((Compound)query).getMolecule();
		//mol = editQuery(mol);
		if (mol != null)
		return new DbExactSearchReader(connection,mol,null, page,pagesize);
		else return null;
	}
	/*
	public ChemObjectWriter getWriter() {
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = (AmbitDatabaseToolsData) userData;		
			return new CurrentMoleculeWriter(dbaData.getMolecules());
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
				processors.add(new ReadStructureProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadAliasProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadNameProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));				

			} catch (Exception x) {
				logger.error(x);
			}
			return processors;
		} else return super.getProcessor();
	}
	/*
	@Override
	protected SourceDataset getResultsDataset(DbConnection conn) {
		return new SourceDataset("Exact search",ReferenceFactory.createSearchReference("Exact search"));
	}
	*/

}
