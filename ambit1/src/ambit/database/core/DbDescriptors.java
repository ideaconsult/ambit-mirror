/**
 * Created on 2005-3-18
 *
 */
package ambit.database.core;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorGroup;
import ambit.data.descriptors.DescriptorGroups;
import ambit.data.descriptors.DescriptorsList;
import ambit.data.literature.LiteratureEntry;
import ambit.database.DbConnection;
import ambit.database.DbCore;
import ambit.database.exception.DbAmbitException;
import ambit.database.exception.DbDescriptorException;
import ambit.database.query.DescriptorQuery;
import ambit.database.query.DescriptorQueryList;


/**
 * Database API<br>
 * Reads , writes and looks for {@link ambit.data.descriptors.Descriptor} into the database 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbDescriptors extends DbCore {
	private PreparedStatement ps;
	private DbReference dbr = null;
	
	protected static final String AMBIT_insertDescriptor = "INSERT INTO ddictionary (iddescriptor,name,units,error,comments,islocal,idref) VALUES (null,?,?,?,?,?,?);";
	protected static final String AMBIT_selectDescriptor = "SELECT iddescriptor,name,units,error,comments,islocal,idref from ddictionary ";
	protected static final String AMBIT_insertDescriptorUser = "INSERT into dict_user (iddescriptor,idambituser) VALUES(";
	protected static final String AMBIT_getDescriptorUser = "SELECT idambituser,iddescriptoruser from dict_user";
	
	protected static final String AMBIT_getDescriptorGroups = "SELECT dgroup.iddgroup,groupname from descrgroups join dgroup using(iddgroup) where iddescriptor=?";
	protected PreparedStatement psDescriptorGroups = null;
	
	public DbDescriptors(DbConnection conn) {
		super(conn);
	}		
	public void initializeInsert() throws DbDescriptorException {
		try {
		ps = conn.prepareStatement(AMBIT_insertDescriptor);
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"initializeInsert",x);
		}
	}
	public void close() throws SQLException {
		super.close();
		if (ps != null) ps.close();
		ps = null;
		if (dbr != null) dbr.close(); dbr = null;
		if (psDescriptorGroups != null) psDescriptorGroups.close(); psDescriptorGroups = null;
	}
	protected int  getDescriptor(ResultSet rs, DescriptorDefinition descriptor) throws DbAmbitException {
		int id = -1;
		try {
		if (rs.next()) {
			id = rs.getInt("iddescriptor");
			descriptor.setId(id);
			descriptor.setName(rs.getString("name"));
			descriptor.setUnits(rs.getString("units"));
			descriptor.setRemark(rs.getString("comments"));
			descriptor.writeDefaultError(rs.getDouble("error"));
			descriptor.setLocal(rs.getBoolean("islocal"));
			
			if (dbr == null) { 
				dbr = new DbReference(dbconn);
				dbr.initializeInsertEntry();
				dbr.initialize();
			}
			int idref = rs.getInt("idref");
			LiteratureEntry reference = descriptor.getReference();
			if (reference == null) {
				reference = new LiteratureEntry();
				descriptor.setReference(reference);
			}
			dbr.getReference(idref,reference);
			
			DescriptorGroups groups = descriptor.getDescriptorGroups();
			if (groups == null) groups = new DescriptorGroups();
			getDescriptorGroups(id,groups);
			descriptor.setDescriptorGroups(groups);
		}	
		return id;
		} catch (SQLException x) {
			throw new DbDescriptorException(descriptor,"getDescriptor",x);
		}
	}
	public int getDescriptorByName(DescriptorDefinition descriptor) throws DbAmbitException {
		int id = -1;
		try {
			ResultSet rs = stmt.executeQuery(
					AMBIT_selectDescriptor + "where name='" + 
					descriptor.getName() + "';");
			id = getDescriptor(rs,descriptor);
			rs.close();
			return id;
		} catch (SQLException x) {
			throw new DbDescriptorException(descriptor,"getDescriptorByName",x);
		}
	}
	public int getDescriptorById(DescriptorDefinition descriptor) throws DbAmbitException {
		int id = -1;
		try {
			ResultSet rs = stmt.executeQuery(
					AMBIT_selectDescriptor + "where iddescriptor=" + 
					descriptor.getId() + ";");
			id = getDescriptor(rs,descriptor);
			rs.close();
			return id;
		} catch (SQLException x) {
			throw new DbDescriptorException(descriptor,"getDescriptorByName",x);
		}
	}
	public int getDescriptorIdByName(DescriptorDefinition descriptor) throws DbDescriptorException {
		int id = -1;
		try {
			ResultSet rs = stmt.executeQuery(
					"SELECT iddescriptor from ddictionary where name='" + 
					descriptor.getName() + "';");
			if (rs.next()) {
				id = rs.getInt(1);
				descriptor.setId(id);
			}	
			rs.close();
			return id;
		} catch (SQLException x) {
			throw new DbDescriptorException(descriptor,"getDescriptorByName",x);
		}
	}
	/**
	 * inserts a descriptor into dictionary. Also insert a group affiliation if available
	 * @param descriptor
	 * @return iddescriptor as read from ddictionary table 
	 * @throws SQLException
	 */
	public int addDescriptor(DescriptorDefinition descriptor) throws DbAmbitException {
		
		int iddescriptor = getDescriptorIdByName(descriptor);
		if (iddescriptor == -1) { 
			LiteratureEntry reference = descriptor.getReference();
			int idref = reference.getId();
			if (idref == -1) {
				if (dbr == null) { 
					dbr = new DbReference(dbconn);
					dbr.initializeInsertEntry();
					dbr.initialize();
				}
				idref = dbr.getReference(reference);
				if (idref == -1)
					idref = dbr.addReference(reference);
			}
			if (idref == -1 ) return -1;
			if (ps == null)  initializeInsert();
			try {
				ps.clearParameters();
				ps.setString(1,descriptor.getName());
				ps.setString(2,descriptor.getUnits());
				ps.setDouble(3,descriptor.readDefaultError());
				ps.setString(4,descriptor.getRemark());
				ps.setBoolean(5,descriptor.isLocal());
				ps.setInt(6,idref);
				ps.executeUpdate();
			
				iddescriptor = getAutoGeneratedKey(ps);
				descriptor.setId(iddescriptor);
				if (iddescriptor > -1) {
					DescriptorGroups groups = descriptor.getDescriptorGroups();
					if (groups != null) 
		    		for (int i = 0; i < groups.size(); i++)
		    			addDescriptorGroup(iddescriptor, (DescriptorGroup) groups.getItem(i));
				}
				addDescriptorUser(iddescriptor,dbconn.getUser().getId());
			} catch (SQLException x) {
				throw new DbDescriptorException(descriptor,"addDescriptor",x);
			}
		}	
		return iddescriptor;
	}
	
	public int getDescriptorGroups(int iddescriptor, DescriptorGroups groups) throws DbDescriptorException {
		try {
			groups.clear();
			if (psDescriptorGroups == null)
				psDescriptorGroups = conn.prepareStatement(AMBIT_getDescriptorGroups);
			psDescriptorGroups.setInt(1,iddescriptor);
			ResultSet rsa = psDescriptorGroups.executeQuery();
			//ResultSet rsa = stmt.executeQuery("SELECT dgroup.iddgroup,groupname from descrgroups join dgroup using(iddgroup) where iddescriptor="+iddescriptor+";");
			while (rsa.next()) {
				groups.addItem(new DescriptorGroup(rsa.getString(2),rsa.getInt(1)));
			}
			rsa.close();
			return groups.size();
		} catch (SQLException x) {
			throw new DbDescriptorException(groups,"getDescriptorGroups",x);
		}
	}
	
	public int getGroupIDByName(String groupname) throws DbDescriptorException {
		int idGroup = -1;
		try {
			ResultSet rs = stmt.executeQuery("SELECT iddgroup from dgroup where groupname='"+groupname+"';");
			if (rs.next()) idGroup = rs.getInt(1);
			rs.close();
			return idGroup;
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"getGroupIDByName",x);
		}
	}
	public int getGroupByName(DescriptorGroup group) throws DbDescriptorException {
		int idGroup = -1;
		try {
			ResultSet rs = stmt.executeQuery("SELECT iddgroup from dgroup where groupname='"+
					group.getName()+"';");
			if (rs.next()) idGroup = rs.getInt(1);
			rs.close();
			group.setId(idGroup);
			return idGroup;
		} catch (SQLException x) {
			throw new DbDescriptorException(group,"getGroupByName",x);
		}
	}	
	public int addGroup(DescriptorGroup group) throws DbDescriptorException  {
		int idGroup = getGroupByName(group);
		try {
			if (idGroup == -1) { 
				String s = "INSERT INTO dgroup (iddgroup,groupname) VALUES (null,'" + group.getName() + "');";
				stmt.executeUpdate(s);		
				idGroup = getAutoGeneratedKey(stmt);
			}	
			group.setId(idGroup);
		    return idGroup;
		} catch (SQLException x) {
			throw new DbDescriptorException(group,"addGroup",x);
		}
	}
	public int deleteDescriptor(String name) throws DbDescriptorException  {
		try {
		String s = "DELETE FROM ddictionary WHERE name='" + name + "';";
		return stmt.executeUpdate(s);
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"deleteDescriptor",x);
		}
	}	
	
	public int deleteGroup(String groupName) throws DbDescriptorException  {
		try {
			String s = "DELETE FROM dgroup WHERE groupname='" + groupName + "';";
			return stmt.executeUpdate(s);
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"deleteGroup",x);
		}
		
	}	
	public void addDescriptorGroup(int iddescriptor, DescriptorGroup group) throws DbDescriptorException {
		int idgroup = addGroup(group);
		try {
		String s = "INSERT ignore INTO DESCRGROUPS (iddescriptor,iddgroup) VALUES ( " + 
					iddescriptor + "," + idgroup + ");";
		
		stmt.executeUpdate(s);
		} catch (SQLException x) {
			throw new DbDescriptorException(group,"addDescriptorGroup",x);
		}
	}
	/**
	 * Looks for descriptors into ddictionary table.
	 * Updates iddescriptor in DescriptorDefinition entries
	 * @param descriptors
	 * @return number of descriptors successfully found by name
	 * @throws SQLException
	 */
	public int getDescriptorsList(DescriptorsList descriptors) throws DbAmbitException {
		int c = 0;

			for (int i = 0; i < descriptors.size(); i ++)
				if (getDescriptorByName(descriptors.getDescriptor(i)) > -1) c++;
			return c;

	}
	
	public DescriptorQueryList loadQuery(DescriptorQueryList descriptors) throws DbAmbitException {
		int id = -1;
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(AMBIT_selectDescriptor); 
			
			while (true) {
				DescriptorQuery query = new DescriptorQuery("",null);
				id = getDescriptor(rs,query);
				if (id > 0)
					descriptors.addItem(query);
				else break;
				
			}
			rs.close();
			s.close();
			s = null;
			return descriptors;
		} catch (SQLException x) {
			x.printStackTrace();
			throw new DbDescriptorException(descriptors,x);
		}

	}	
	/**
	 * inserts descriptors from a List into ddictionary table. 
	 * If descriptor already exists there  (verified by an unique name)
	 * does not add it again, but just updates its identifier by 
	 * calling DescriptorDefinition.setIddescriptor()  
	 * @param descriptors
	 * @return number of descriptors added
	 * @throws SQLException
	 */
	public int addDescriptorsList(DescriptorsList descriptors) throws DbAmbitException {
		int c = 0;
		for (int i = 0; i < descriptors.size(); i ++) {
			Descriptor d = descriptors.getDescriptor(i);
			if (getDescriptorByName(d) == -1) {
				if (addDescriptor(d) > -1) c++; 
			} else c++;
		}	
		return c;	
	}
	
	public int addDescriptorUser(int iddescriptor,int iduser) throws DbDescriptorException {
		try {
		stmt.executeUpdate(AMBIT_insertDescriptorUser +
				iddescriptor + "," + iduser + ");");
		return getAutoGeneratedKey(stmt);
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"addDescriptorGroup",x);
		}
	}
	public int getDescriptorUser(int idDescriptor) throws DbDescriptorException {
		int id = -1;
		try {
			ResultSet rs = stmt.executeQuery(AMBIT_getDescriptorUser +
					" where iddescriptor=" 
					+idDescriptor + AMBIT_smtdelimiter);
			if (rs.next()) id = rs.getInt(1);
			rs.close();
			return id;
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"addDescriptorGroup",x);
		}
	}
	
}
