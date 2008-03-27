package ambit2.database.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;

/**
 * Reads SMILES from database.See the example at {@link ambit2.database.search.DbSimilarityByFingerprintsReader}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ReadSMILESProcessor extends ReadIdentifierProcessor {
	public ReadSMILESProcessor(Connection connection) throws AmbitException {
		super(connection);
        setIdentifierLabel(AmbitCONSTANTS.SMILES);
	}

	public void prepare(Connection connection) throws AmbitException {
		try {
			psStructure = connection.prepareStatement("select smiles from substance join structure using(idsubstance) where idstructure=?");
			psSubstance = connection.prepareStatement("select smiles from substance where idsubstance=?");

		} catch (SQLException x) {
			logger.error(x);
		}	
	}
    /*
	public Object process(Object object) throws AmbitException {
		try {
		ResultSet rs = null;
			if (object instanceof IChemObject) {
				   IChemObject mol = (IChemObject) object;
		            Object idsubstance = mol.getProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE);
		            if (idsubstance != null) {
		            	int id  = ((Integer) idsubstance).intValue();
		            	if (id > 0) {
			            	psSubstance.clearParameters();
			            	psSubstance.setInt(1,id);
			            	rs = psSubstance.executeQuery();
		            	}
		            } 
		            if (rs == null) {
		            	Object idstructure = mol.getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
		            	if (idstructure != null) {
			            	int id  = ((Integer) idstructure).intValue();
			            	if (id > 0) {
				            	psStructure.clearParameters();
				            	psStructure.setInt(1,id);
				            	rs = psStructure.executeQuery();
			            	}
		            	}
		            }
		            if (rs != null) {
		            	while (rs.next()) {
		            		String s = rs.getString("smiles");
		            		if (s!= null)
		            			mol.setProperty(AmbitCONSTANTS.SMILES,s);
		            	}
		            	rs.close();
		            	rs = null;
		            }	
			}
			return object;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
    */
	public void close() {
		try {
			psSubstance.close();
			psStructure.close();
		} catch (Exception x) {
			logger.error(x);
		}
		super.close();
	}
	public String toString() {
		return "Read SMILES from database";
	}

    @Override
    public Object getIdentifierValue(ResultSet rs) throws Exception {
        return rs.getString("smiles");
    }
}
