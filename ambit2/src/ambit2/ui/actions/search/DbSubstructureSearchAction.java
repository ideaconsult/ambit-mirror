package ambit2.ui.actions.search;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.data.molecule.Compound;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.ReadAliasProcessor;
import ambit2.database.processors.ReadNameProcessor;
import ambit2.database.processors.ReadSMILESProcessor;
import ambit2.database.processors.ReadSubstanceProcessor;
import ambit2.database.processors.SubstructureSearchProcessor;
import ambit2.database.search.DbSubstructurePrescreenReader;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.ui.UITools;

/**
 * Substructure search in database. See example for {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbSubstructureSearchAction extends DbSearchAction {
    IMolecule mol = null;
	public DbSubstructureSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Substructure",null);
	}

	public DbSubstructureSearchAction(Object userData, JFrame mainFrame,
			String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/search.png"));
	}

	public DbSubstructureSearchAction(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		putValue(AbstractAction.SHORT_DESCRIPTION,"Substructure search");
		interactive = false;
	}
	/*
	public IteratingChemObjectReader getSearchReader(Connection connection, Object query, String limit) throws AmbitException{
		Molecule mol = null;
		if (query instanceof Molecule ) mol = (Molecule) query;
		else if (query instanceof Compound) mol = ((Compound)query).getMolecule();
		
		if (mol != null)
			return new DbSubstructureSearchReader(connection,mol,null, limit);
		else return null;
	}	
	*/
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query, int page, int pagesize) throws AmbitException{
	    //query = editQuery(null);
		mol = null;
		if (query instanceof Molecule ) mol = (Molecule) query;
		else if (query instanceof Compound) mol = ((Compound)query).getMolecule();
		try {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);		    
		} catch (CDKException x) {
		    logger.error(x);
		}

		if (mol != null) {
		    try {
			MFAnalyser mfa = new MFAnalyser((Molecule)mol.clone());
			mol = (Molecule) mfa.removeHydrogensPreserveMultiplyBonded();
		    } catch (Exception c) {
		        logger.error(c);
		    }
			
			ISharedDbData dbaData = ((ISharedDbData) userData);
		    return new DbSubstructurePrescreenReader(connection,mol,((AmbitDatabaseToolsData)dbaData).getSrcDataset(), page,pagesize);
		} else return null;
	}		
    public IAmbitProcessor getProcessor() {
        ProcessorsChain processors = new ProcessorsChain();
        if (userData instanceof ISharedDbData) {
            ISharedDbData dbaData = (ISharedDbData) userData;
            try {
	        processors.add(new ReadSubstanceProcessor(dbaData.getDbConnection().getConn()));
            } catch (Exception x) {
                return null;
            }
            processors.add(new SubstructureSearchProcessor(mol));
            try {
            	processors.add(new ReadAliasProcessor(dbaData.getDbConnection().getConn()));
            	processors.add(new ReadNameProcessor(dbaData.getDbConnection().getConn()));
            	processors.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));  
            } catch (Exception x) {
            	logger.error(x);
            }
		}

        return processors;

    }		
    /*
    @Override
    protected SourceDataset getResultsDataset(DbConnection conn) {
    	return new SourceDataset("Substructure search",ReferenceFactory.createSearchReference("Substructure search"));
    }
    */
}
