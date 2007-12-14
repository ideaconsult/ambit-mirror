package ambit.ui.actions.search;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.Compound;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.readers.SearchFactory;
import ambit.database.search.DbSearchReader;
import ambit.database.search.DbSimilarityByFingerprintsReader;
import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitProcessor;
import ambit.ui.UITools;

/**
 * Database similarity search. See example for {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbSimilaritySearchAction extends DbSearchAction {

	public DbSimilaritySearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Similarty");
	}	

	public DbSimilaritySearchAction(Object userData, JFrame mainFrame,
			String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/search.png"));

	}

	public DbSimilaritySearchAction(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		putValue(AbstractAction.SHORT_DESCRIPTION,"Similarity search. Use \"Similarity method\" menu to select a similarity method.");
		interactive = false;
	}
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query, int page, int pagesize) throws AmbitException {
	    //query = editQuery(null);
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
				try {
					IMolecule mol = null;
					try {
					if (query instanceof IMolecule ) mol = (IMolecule) ((IMolecule) query).clone();
					else if (query instanceof Compound) 
						mol = (IMolecule) (((Compound)query).getMolecule()).clone();
					} catch (Exception x) {
					    throw new AmbitException(x);
					}
												
					HueckelAromaticityDetector.detectAromaticity(mol);
					MFAnalyser mfa = new MFAnalyser(mol);
					mol = (Molecule) mfa.removeHydrogensPreserveMultiplyBonded();
					if (mol != null) {
						
						DbSearchReader reader = null;
						
						if (dbaData instanceof AmbitDatabaseToolsData) {
                            int startPage = ((AmbitDatabaseToolsData)dbaData).getPage();
                            int pageSize = ((AmbitDatabaseToolsData)dbaData).getPageSize();
                            double threshold = ((AmbitDatabaseToolsData)dbaData).getSimilarityThreshold();
                            
                            reader = SearchFactory.createReader(((AmbitDatabaseToolsData)dbaData).getSimilarityMethod(), 
                                    connection, mol, ((AmbitDatabaseToolsData)dbaData).getSrcDataset(), threshold, page, pagesize);
                            /*
							if (((AmbitDatabaseToolsData)dbaData).getSimilarityMethod().equals("Atom Environments"))
								reader = new DbSimilarityByAtomenvironmentsReader(connection,
										mol,((AmbitDatabaseToolsData)dbaData).getSrcDataset(),threshold,startPage,pageSize);					
							else	
								reader = new DbSimilarityByFingerprintsReader(connection,
									mol,((AmbitDatabaseToolsData)dbaData).getSrcDataset(), threshold,startPage,pageSize);
                            
							*/
                            
						} else
						
							reader = new DbSimilarityByFingerprintsReader(connection,
									mol,null, 0,page,pagesize);						    
						mol = null;
						return reader;
						
					}	
				} catch (Exception x) {
					 JOptionPane.showMessageDialog(mainFrame,x.toString());
				}
		}
		return null;
	}
    public IAmbitProcessor getProcessor() {
    	
        if (userData instanceof ISharedDbData) {
            ISharedDbData dbaData = (ISharedDbData) userData;
            return SearchFactory.getProcessor(
                    dbaData.getDbConnection().getConn(),
                    null,
                    ((AmbitDatabaseToolsData)dbaData).getSimilarityMethod(),
                    true);
		} else return null;

    }
    /*
    @Override
    protected SourceDataset getResultsDataset(DbConnection conn) {
    	return new SourceDataset("Similarity search",ReferenceFactory.createSearchReference("Similarity search"));
    }
    */
	
}
