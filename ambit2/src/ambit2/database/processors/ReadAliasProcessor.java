package ambit2.database.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.exceptions.AmbitException;

/**
 * Reads identifiers from database (alias table) and assigns them to the molecule . See example at {@link ambit2.database.search.DbSimilarityByAtomenvironmentsReader}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ReadAliasProcessor extends ReadIdentifierProcessor {
	
	public ReadAliasProcessor(Connection connection) throws AmbitException {
		super(connection);
	}

	public void prepare(Connection connection) throws AmbitException {
		try {
			psSubstance = connection.prepareStatement("select alias,alias_type from alias join structure using(idstructure) where idsubstance=?");
			psStructure = connection.prepareStatement("select alias,alias_type from alias where idstructure=?");
		} catch (SQLException x) {
			
		}

	}
    @Override
    public Object getIdentifierValue(ResultSet rs) throws Exception {
        return rs.getString("alias");
    }
    protected void setIdentifierValue(IChemObject mol, ResultSet rs)  throws Exception {
        while (rs.next()) {
            mol.setProperty(rs.getString("alias_type"),getIdentifierValue(rs));
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
		            		mol.setProperty(rs.getString("alias_type"),rs.getString("alias"));
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
	public String toString() {
		return "Read aliases from database";
	}
	public void close() {
		try {
			psSubstance.close();
			psStructure.close();
		} catch (Exception x) {
			logger.error(x);
		}
		super.close();
	}
}
