/**
 * Created on 2005-3-18
 *
 */
package ambit.database.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import ambit.data.descriptors.Descriptor;
import ambit.data.literature.LiteratureEntry;
import ambit.data.model.Model;
import ambit.data.model.ModelType;
import ambit.database.DbConnection;
import ambit.database.DbCore;
import ambit.database.exception.DbAmbitException;
import ambit.database.exception.DbModelException;



/**
 * 
 * Database API<br>
 * Reads, writes and looks for {@link ambit.data.model.Model}s in the database
 * These are relatively low level functions, could be redesigned.  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbModel extends DbCore {
	protected static final String AMBIT_insertQSAR = "INSERT IGNORE INTO qsars (idqsar,name,note,keyw,idmodeltype,idref,stat_N,stat_R2,model) VALUES (null,?,?,?,?,?,?,?,?);";
	
	protected static final String AMBIT_deleteQSAR = "DELETE from qsars where idqsar=";
	protected static final String AMBIT_deleteQSARdescriptor = "DELETE from qsardescriptors where idqsar=";
	protected static final String AMBIT_deleteQSARpoint = "DELETE from qsardata where idqsar=";
	protected static final String AMBIT_insertQSARuser = "INSERT IGNORE into qsar_user (idqsar,idambituser) VALUES(";
	protected static final String AMBIT_getQSARuser = "SELECT from qsar_user where idqsar=";
	protected static final String AMBIT_getQSARID="SELECT idqsar from qsars ";
	
	protected DbReference dbr = null;
	//protected DbDescriptors dbd = null;
	//protected ExperimentSearchProcessor esp = null;
	//protected ExperimentWriter dbw = null;
	/**
	 * 
	 */
	public DbModel(DbConnection conn) {
		super(conn);
	}

	public void close() throws SQLException {
		super.close();
		if (dbr != null) dbr.close(); dbr = null;
	//	if (dbd != null) dbd.close(); dbd = null;
	}
	public void initializeInsertModel() throws DbAmbitException {

		initializePrepared(AMBIT_insertQSAR);
	
	}	

	/**
	 * Looks for {@link ModelType} in the database, sets model type identifier if found 
	 * @param mtype
	 * @return model type identifier if found in the database, otherwise -1
	 * @throws DbModelException
	 */
	public int getModelType(ModelType mtype) throws DbModelException {
		int id = -1;
		try {
			ResultSet rs = stmt.executeQuery("SELECT idmodeltype from modeltype where modeltype='"+
					mtype.getName() + "';");
			if (rs.next()) id = rs.getInt(1);
			rs.close();
			mtype.setId(id);
			return id;
		} catch (SQLException x) {
			throw new DbModelException(null,"getModelType",x);
		}	
	}
	/**
	 * Inserts {@link ModelType} into the database, sets model type identifier if successfully added
	 * @param mtype
	 * @return model type identifier 
	 * @throws DbModelException
	 */
	public int addModelType(ModelType mtype) throws DbModelException {
		try {
			stmt.executeUpdate("INSERT IGNORE into modeltype (idmodeltype,modeltype) VALUES(null,'" +
					mtype.getName() + "');");
			int id = getAutoGeneratedKey(stmt);
			mtype.setId(id);
			return id;
		} catch (SQLException x) {
			throw new DbModelException(mtype,"addModelType",x);
		}		
	}
	/**
	 * Inserts {@link Model} into the database, sets model identifier if successfully added
	 * @param model
	 * @return model type identifier as in database
	 * @throws DbAmbitException
	 */
	public int addModel(Model model)  throws DbAmbitException {
		  if (model.isEmpty()) return -1;
		  
		  //verify if model type exists
		  ModelType mtype = model.getModelType();
		  int idmodeltype = getModelType(mtype);
		  //add to db if not
		  if (idmodeltype == -1) idmodeltype = addModelType(mtype);
		  if (idmodeltype == -1) return -1; 

		  //initialize class that will access literature entries
		  if (dbr == null) {
		  	dbr = new DbReference(dbconn);
		  	dbr.initialize();
		  	dbr.initializeInsertEntry();
		  }
		  LiteratureEntry reference = model.getReference();
		  //add literature entry to database and get database identifier
		  int idref = dbr.addReference(reference);		  

		  int idmodel = getQSARID(model); 
		  
		  //model already exists in the database
		  if (idmodel > -1) return idmodel;

		//"INSERT INTO QSARS (idqsar,name,note,keyw,idmodeltype,idref,stat_N,stat_R2,model) VALUES (null,?,?,?,?,?,?,?,?);";
		  //idqsar,name,note,keyw,idmodeltype,idref,stat_N,stat_R2,model
		  try {
			  ps.clearParameters();
			  ps.setString(1,model.getName());
			  ps.setString(2,model.getNote());
			  ps.setString(3,model.getKeywords());
			  ps.setInt(4,idmodeltype);
			  ps.setInt(5,idref);
			  ps.setInt(6,model.getN_Points());
			  ps.setDouble(7,model.getStats().getR2());
			  ps.setString(8,model.getEquation());
			  ps.executeUpdate();
			  idmodel =  getAutoGeneratedKey(ps);
			  model.setId(idmodel);
			  //TODO Use QSARDescriptorWriter to add descriptors
			  /*
			  if (dbd == null) {
			  	dbd = new DbDescriptors(dbconn);
			  	dbd.initialize();
			  	dbd.initializeInsert();
			  }
			  DescriptorsList descriptors = model.getDescriptors();
			  //add descriptors
			  //TODO throw exception , rollback 
			  if (dbd.addDescriptorsList(descriptors) != descriptors.size()) return -1; 
			  //add descriptors to model
			  for (int i = 0; i < descriptors.size(); i++) {
			  	Descriptor d = descriptors.getDescriptor(i);
			  	addDescriptor(idmodel, d.getId(), d.getOrderInModel());
			  }	
			  
			   * */
//			add user to model 
			  //addModelUser(idmodel,dbconn.getUser().getId());
			  return idmodel;
		  } catch (SQLException x ) {
			  throw new DbModelException(model,"addModel",x);
		  }
	}
	/**
	 * add user to model
	 * @param idqsar
	 * @param iduser
	 * @return number of users added
	 * @throws DbModelException
	 */
	public int addModelUser(int idqsar,int iduser) throws DbModelException {
		try {
		stmt.executeUpdate(AMBIT_insertQSARuser +
				idqsar + "," + iduser + ");");
		return getAutoGeneratedKey(stmt);
		} catch (SQLException x) {
			throw new DbModelException(null,"getModelUser",x);
		}
	}
	public int getModelUser(int idqsar) throws DbModelException {
		int id = -1;
		try {
			ResultSet rs = stmt.executeQuery(AMBIT_getQSARuser 
					+idqsar + AMBIT_smtdelimiter);
			if (rs.next()) id = rs.getInt(1);
			rs.close();
			return id;
		} catch (SQLException x) {
			throw new DbModelException(null,"getModelUser",x);
		}
	}
	/**
	 * delete model
	 * @param idqsar  {@link Model} identifier as in database (table qsars)
	 * @return number of models deleted
	 * @throws DbModelException
	 */
	public int deleteModel(int idqsar) throws DbModelException {
		try {
		return stmt.executeUpdate(AMBIT_deleteQSAR + idqsar + AMBIT_smtdelimiter);
		} catch (SQLException x) {
			throw new DbModelException(null,"deleteModel",x);
		}
	}
	/**
	 * 
	 * @param idqsar - {@link Model} identifier as in database
	 * @param name  - {@link Descriptor} name 
	 * @return  {@link Descriptor} identifier as in database 
	 * @throws DbModelException
	 */
	public int getDescriptor(int idqsar,String name) throws DbModelException {
		int id = -1;
		try {
			ResultSet rs = stmt.executeQuery("SELECT qsardescriptors.iddescriptor from qsardescriptors,ddictionary where name='"+
					name + "' and qsardescriptors.iddescriptor=ddictionary.iddescriptor;");
			if (rs.next()) {
				id = rs.getInt(1);
			}
			rs.close();
			return id;
		} catch (SQLException x) {
			throw new DbModelException(null,"getDescriptor",x);
		}
	}
	/**
	 * Deletes {@link Descriptor} for a {@link Model} from database 
	 * @param idqsar - {@link Model} identifier as in database
	 * @param iddescriptor {@link Descriptor} identifier as in database
	 * @return number of descriptors deleted
	 * @throws DbModelException
	 */
	public int deleteDescriptor(int idqsar,int iddescriptor) throws DbModelException {
		try {
		return stmt.executeUpdate(AMBIT_deleteQSARdescriptor+
				idqsar + " and iddescriptor="+iddescriptor + AMBIT_smtdelimiter);
		} catch (SQLException x) {
			throw new DbModelException(null,"deleteDescriptor",x);
		}
	}
	/**
	 * Delete all descriptors for a {@link Model} from database
	 * @param idqsar {@link Model} identifier as in database
	 * @throws DbModelException
	 */
	public void deleteDescriptors(int idqsar) throws DbModelException {
		try {
		stmt.executeUpdate(AMBIT_deleteQSARdescriptor+
				idqsar + AMBIT_smtdelimiter);
		} catch (SQLException x) {
			throw new DbModelException(null,"deleteDescriptors",x);
		}
	}	

	/**
	 * Delete a point for a given model from database
	 * @param idqsar {@link Model} identifier as in database
	 * @param idStructure {@link ambit.misc.AmbitCONSTANTS#AMBIT_IDSTRUCTURE} structure identifier as in database
	 * @return number of points deleted
	 * @throws DbModelException
	 */
	public int deleteQSARpoint(int idqsar, int idStructure) throws DbModelException {
		try {
		return stmt.executeUpdate(AMBIT_deleteQSARpoint + 
				idqsar + " and idstructure ="+idStructure + AMBIT_smtdelimiter);
		} catch (SQLException x) {
			throw new DbModelException(null,"deleteQSARpoint",x);
		}
	}
	/**
	 * Delete all points for a given model from database
	 * @param idqsar {@link Model} identifier as in database
	 * @return number of points deleted
	 * @throws DbModelException
	 */
	public int deleteQSARpoints(int idqsar) throws DbModelException {
		try {
		return stmt.executeUpdate(AMBIT_deleteQSARpoint +
				idqsar  + AMBIT_smtdelimiter);
		} catch (SQLException x) {
			throw new DbModelException(null,"deleteQSARpoints",x);
		}
	}
	//UNIQUE KEY `qsars_all` (`name`,`idmodeltype`,`idres`,`idref`),
	public int getQSARID(Model model)  throws DbModelException {
		if (model.hasID()) return model.getId();
		else if (model.getModelType().hasID() &
				model.getReference().hasID()) {
		
			String sql = AMBIT_getQSARID + 
			" where name='" + model.getName() + 
			"' and idmodeltype=" + model.getModelType().getId()+
			" and idref=" + model.getReference().getId()+
			AMBIT_smtdelimiter;
			
			int id =-1;
			try {
				ResultSet rs = stmt.executeQuery(sql);
				if (rs.next()) id = rs.getInt(1);
				model.setId(id);
				return id;
			} catch (SQLException x){
				throw new DbModelException(model,"Error in getQSARID",x);
			}
		} else return -1;
	}
}
