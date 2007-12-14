package ambit.database.writers;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Hashtable;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.CMLWriter;

import ambit.data.AmbitUser;
import ambit.data.molecule.MoleculeTools;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;

/**
 * Writes structures into database. This concerns table structure. Used in {@link ambit.database.writers.DbSubstanceWriter}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbStructureWriter extends AbstractDbStructureWriter {
	protected Hashtable emptyprops = new Hashtable();
	protected DbStructureUserWriter userWriter = null;
	protected static final String AMBIT_insertStructure = 
		"INSERT INTO structure (idsubstance,idstructure,structure,type_structure,remark) VALUES (?,?,compress(?),?,?) ON DUPLICATE KEY UPDATE structure=compress(?),type_structure=?,remark=?,updated=now()";
	public DbStructureWriter(Connection connection,AmbitUser user) {
		super(connection,user);
	}
	public void write(IChemObject object) throws CDKException {
        try {
        	if (object == null) throw new CDKException("Null molecule!");
        	int idstructure = -1;
        	try {
        		idstructure = getIdStructure(object);
        	} catch (Exception x) {
        		idstructure = -1;
        		logger.info(x.getMessage());
        	}
        	idstructure = write(idstructure,object);
        	if (userWriter == null) userWriter = new DbStructureUserWriter(connection,user);
        	userWriter.write(idstructure, object);
        } catch (AmbitException x) {
        	logger.error(x);
        	throw new CDKException(x.getMessage());
        }
	}	
	public int write(int idstructure, IChemObject object) throws AmbitException {
       	int idsubstance = getIdSubstance(object);
       	return write(idsubstance,idstructure,object);
	}
	public int write(int idsubstance,int idstructure, IChemObject object) throws AmbitException {
		if (!(object instanceof IAtomContainer)) throw new AmbitException("Unsuppoted object "+object.getClass().getName());
		if (((IAtomContainer) object).getAtomCount() == 0) throw new AmbitException("Empty molecule!");
		StringWriter w;
		try {
			Hashtable props = (Hashtable) object.getProperties().clone();
			object.getProperties().clear();
			
			w = new StringWriter();
			CMLWriter cmlWriter = new CMLWriter(w);						
			cmlWriter.write(object);
			cmlWriter = null;
			
			object.setProperties(props);
		} catch (Exception e) {
			logger.error(e);
			throw new AmbitException(e);
		}
		try {
			Object strucType = object.getProperty(AmbitCONSTANTS.STRUCTURETYPE);
			if (strucType == null) MoleculeTools.analyzeSubstance((AtomContainer)object);
			strucType = object.getProperty(AmbitCONSTANTS.STRUCTURETYPE);
			//TODO
			Object remark = object.getProperty(CDKConstants.REMARK);
			if (remark == null) remark = "";	
			prepareStatement();
			ps.clearParameters();
			ps.setInt(1,idsubstance);
			if (idstructure >0)
				ps.setInt(2,idstructure);
			else ps.setNull(2,Types.INTEGER);
			ps.setString(3,w.toString());
			ps.setString(4,strucType.toString());		
			ps.setString(5,remark.toString());
			ps.setString(6,w.toString());
			ps.setString(7,strucType.toString());		
			ps.setString(8,remark.toString());
			
		    //w = null;
			ps.executeUpdate();
			if (idstructure <=0) {
				int newIDStructure = -1;
			    ResultSet tmpRS = ps.getGeneratedKeys();
			    if (tmpRS.next()) {
			    	newIDStructure = tmpRS.getInt(1);
			    } else {
			    	newIDStructure = -1;
			    }
			    tmpRS.close();
			    object.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,new Integer(newIDStructure));
			    return newIDStructure;
			}
			object.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,new Integer(idstructure));
			if (logger.isDebugEnabled())
				logger.debug("Structure written "+AmbitCONSTANTS.AMBIT_IDSTRUCTURE+"\t"+object.getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE));
		    return idstructure;
		} catch (Exception e) {
			logger.error(e);
			throw new AmbitException(e);
		}
	}	
	protected void prepareStatement() throws SQLException {
		if (ps == null)
			ps = connection.prepareStatement(AMBIT_insertStructure);

	}
	public String toString() {
		return "Write connectivity and/or 3D information to database";
	}

}
