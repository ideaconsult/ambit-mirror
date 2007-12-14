package ambit.database.search;

import java.sql.Connection;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.smiles.SmilesParser;

import ambit.data.molecule.SourceDataset;
import ambit.database.AmbitID;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;

/**
 * 
 * Substructure search. 
 * Use {@link ambit.database.search.DbSubstructurePrescreenReader} if searching in database.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbSubstructureSearchReader extends DbSubstructurePrescreenReader {
	protected SmilesParser parser = null;
	public DbSubstructureSearchReader(Connection connection, IAtomContainer mol, SourceDataset srcDataset,double threshold,  int page, int pagesize)  throws AmbitException {
			super(connection,mol,srcDataset, page,pagesize);
			parser = new SmilesParser();

	}

	public Object next() {
		try {
			String smiles = resultset.getString("smiles");
			IMolecule mol = parser.parseSmiles(smiles);
			
			if ((mol != null) && (UniversalIsomorphismTester.isSubgraph(mol,query))) {

                int idsubstance = getSubstance();
				
                if (readIDOnly) 
                    return new AmbitID(idsubstance,-1);
                else {
                    mol.setProperty(AmbitCONSTANTS.SMILES, smiles);
                    mol.setProperty(AmbitCONSTANTS.FORMULA, resultset.getString("formula"));                    
                    mol.setProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE, new Integer(idsubstance));
                    return mol;
                }
				
			} else return null;
		} catch (SQLException x) {
			logger.error(x);
			return null;
		} catch (Exception x) {
			logger.error(x);
			return null;
		}
	}
	
	public String toString() {
		return "Search for substructure";
	}


}
