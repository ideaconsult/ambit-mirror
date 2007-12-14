package ambit.ui.actions.search;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.Compound;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadNameProcessor;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.database.processors.ReadStructureProcessor;
import ambit.database.search.DbExactSearchReader;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.UniqueIDProcessor;
import ambit.ui.UITools;

/**
 * Exact search in database. See example for {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbExactSearchAction extends DbSearchAction {

	public DbExactSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Search compound");
	}

	public DbExactSearchAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/search.png"));
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
