package ambit.database.writers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.openscience.cdk.interfaces.IChemObject;

import ambit.data.AmbitUser;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.structure.SmilesGeneratorProcessor;

/**
 * Writes SMILES to the database. 
 * Expects unique smiles in object.getProperty(AmbitCONSTANTS.UNIQUE_SMILES) , otherwise tries to generate it.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class SmilesWriter extends AbstractDbStructureWriter {
	protected SmilesGeneratorProcessor processor = null;
	public SmilesWriter(Connection connection,AmbitUser user) {
		super(connection,user);
	}	
	public int write(int idstructure, IChemObject object) throws AmbitException {
		try {
			int unique = 0;
			Object smiles = null;
			prepareStatement();
			ps.clearParameters();
			ps.setInt(4,idstructure);
			
			if (object != null) {
				smiles = object.getProperty(AmbitCONSTANTS.UNIQUE_SMILES);
				if (smiles== null) {
					unique = 0;
					smiles = object.getProperty(AmbitCONSTANTS.SMILES);
					if (processor == null) processor = new SmilesGeneratorProcessor();
					object = (IChemObject) processor.process(object);
					smiles = object.getProperty(AmbitCONSTANTS.UNIQUE_SMILES);
					
					unique = 1;					
				} else unique = 1;
				
				
				if (smiles == null)throw new AmbitException("Undefined SMILES");
				
				
				Object smiles_time =  object.getProperty(AmbitCONSTANTS.UNIQUE_SMILES_TIME);
				if (smiles_time == null) ps.setNull(3,Types.INTEGER);
				else ps.setLong(3,((Long)smiles_time).longValue());
			} else {
				smiles = "ERROR";
			}
			ps.setString(1,smiles.toString());		      	
			ps.setInt(2,unique);
			ps.executeUpdate();
			
			records_written++;
			if ((records_written % 1000) == 0)
			    if (!connection.getAutoCommit())
			        connection.commit();
			    
			return idstructure;
			
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

	protected void prepareStatement() throws SQLException {
		if (ps != null) return;
		final String AMBIT_UpdateSmilesByID = "UPDATE substance set smiles=?,usmiles=?,time_elapsed=? where idsubstance=(select idsubstance from structure where idstructure=?)";
		ps =connection.prepareStatement(AMBIT_UpdateSmilesByID);
	}
	public String toString() {
		return "Write SMILES to database";
	}
}
