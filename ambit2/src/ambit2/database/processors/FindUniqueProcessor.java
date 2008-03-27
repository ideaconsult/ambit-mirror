package ambit2.database.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.log.AmbitLogger;
import ambit2.processors.IAmbitResult;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.MoleculeTools;
import ambit2.data.molecule.StructureType;
import ambit2.database.core.DbSQL;

/**
 * Finds the structure with the same smiles in database and assigns its {@link ambit2.misc.AmbitCONSTANTS#AMBIT_IDSTRUCTURE}. <br>
 * See exapmle at {@link ambit2.database.writers.DbSubstanceWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class FindUniqueProcessor extends DefaultDbProcessor {
	protected static AmbitLogger logger = new AmbitLogger(FindUniqueProcessor.class);
	//protected PreparedStatement psFindSmilesCas;
	protected PreparedStatement psFindSmiles;
	//protected SmilesGenerator smilesGenerator = null;
	
    public FindUniqueProcessor(Connection connection) throws AmbitException {
        super(connection);
    }
	public void prepare(Connection connection) throws AmbitException {
		try {
//		if (psFindSmilesCas == null)
			//psFindSmilesCas = connection.prepareStatement(DbSQL.AMBIT_ExactStrucSearchBySmilesCAS);
		//if (psFindSmiles == null)
			psFindSmiles = connection.prepareStatement(DbSQL.AMBIT_ExactStrucSearchBySmiles);
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

	public void close() {
		try {
			if (psFindSmiles == null) psFindSmiles.close(); psFindSmiles = null;
			//if (psFindSmilesCas == null) psFindSmilesCas.close(); psFindSmilesCas = null;
		} catch (SQLException x) {
			logger.error(x);
		}
	}

	public Object process(Object object) throws AmbitException {
		if (object instanceof IMolecule) {
			try {
			//findSubstanceBySmilesCAS((Molecule) object);
				findSubstanceBySMILES((IMolecule) object);
			} catch (SQLException x) {
				throw new AmbitException(x);
			}
			return object;
		} else return null;
	}
	
	public IAmbitResult createResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAmbitResult getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResult(IAmbitResult result) {
		// TODO Auto-generated method stub

	}
	/**
	 * SMILES has to be assigned as a property AmbitCONSTANTS.UNIQUE_SMILES or AmbitCONSTANTS.SMILES 
	 * @param molecule 
	 * @return
	 * @throws SQLException
	 */
	private int findSubstanceBySMILES(IMolecule molecule) throws SQLException {
		boolean uniqueSmiles= false;
		Object smiles = molecule.getProperty(AmbitCONSTANTS.UNIQUE_SMILES);
		if ((smiles ==null) || (smiles.equals("")))  {
			smiles = molecule.getProperty(AmbitCONSTANTS.SMILES);
		} else if (smiles.toString().indexOf("error")>=0) 
			smiles = null;
		int idSubstance = -1;
		int idstructure = -1;
		StructureType type_structure = null;
		Compound found = null;
		if ((smiles !=null) && (!smiles.equals(""))) {
			ResultSet foundRS=null;
			try {
				psFindSmiles.clearParameters();
				psFindSmiles.setString(1,smiles.toString().trim());
				foundRS = psFindSmiles.executeQuery();
				Object old_type= molecule.getProperty(AmbitCONSTANTS.STRUCTURETYPE);
				if (old_type == null) MoleculeTools.analyzeSubstance(molecule);
				old_type= molecule.getProperty(AmbitCONSTANTS.STRUCTURETYPE);
				
				while (foundRS.next()) {
					idSubstance = foundRS.getInt(1);
					idstructure = foundRS.getInt(2);
					type_structure = new StructureType(foundRS.getString(3));
					if (old_type != null) 
						if (type_structure.getId() >= StructureType.strucType3DnoH) {
							logger.debug("Found structure of type "+type_structure + " will NOT be replaced. IDSTRUCTURE="+idstructure);
							//idstructure = -1; //3d structures will not be replaced
                            break;
						} else {
							logger.debug("Found structure of type "+type_structure + " will be replaced. IDSTRUCTURE="+idstructure);
							break;	
						}
				}
				foundRS.close();    
				
				molecule.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,new Integer(idstructure));
				molecule.setProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE,new Integer(idSubstance));				
			} catch (SQLException x) {
				x.printStackTrace();
			}			
		}
	    return idSubstance;
	}
	
	public String toString() {
		return "Verify if the compound already exists in the database";
	}
	
}
