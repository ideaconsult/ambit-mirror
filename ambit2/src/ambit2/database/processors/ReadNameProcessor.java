package ambit2.database.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.CDKConstants;

import ambit2.exceptions.AmbitException;

/**
 * Read chemical names from database. See example at {@link ambit2.database.search.DbSimilarityByAtomenvironmentsReader}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ReadNameProcessor extends ReadIdentifierProcessor {
	public ReadNameProcessor(Connection connection) throws AmbitException {
		super(connection);
		setIdentifierLabel(CDKConstants.NAMES);
	}

	public void prepare(Connection connection) throws AmbitException {
		try {
			psSubstance = connection.prepareStatement("select name from name join structure using(idstructure) where idsubstance=?");
			psStructure = connection.prepareStatement("select name from name where idstructure=?");
		} catch (SQLException x) {
			
		}

	}
    @Override
    public Object getIdentifierValue(ResultSet rs) throws Exception {
        return rs.getString("name");
    }

	public String toString() {
		return "Read chemical names from database";
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
