package ambit2.database.readers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import ambit2.database.DbConnection;
import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.data.molecule.SourceDataset;
import ambit2.database.core.DbDescriptors;
import ambit2.database.exception.DbAmbitException;

/**
 * Reads structures without specified descriptors from the database. Used by {@link ambit2.ui.actions.process.DBCalculateDescriptorsAction} ; {@link ambit2.ui.actions.process.DbMOPACAction}, {@link ambit2.ui.actions.process.CalculateMOPACAction}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbStructureMissingDescriptorsReader extends DbStructureReader {
    public static String MISSING_DESCRIPTOR = 
        //"select structure.idstructure,idsubstance,uncompress(structure) as ustructure from structure where idstructure not in (select idstructure from dvalues where iddescriptor=?) limit ";
        "select structure.idstructure,idsubstance from structure where idstructure not in (select idstructure from dvalues where iddescriptor=?)";
    //"select structure.idstructure,idsubstance,atom1,atom2,uncompress(structure) as ustructure from structure left join atom_distance using(idstructure) where atom1 is null group by (idstructure) limit ";
    protected PreparedStatement stmt = null;
    protected String caption = "";

    /**
     * 
     * @param dbConn {@link DbConnection}
     * @param descriptor {@link DescriptorDefinition} descriptor 
     * @param page
     * @param pagesize
     */
    public DbStructureMissingDescriptorsReader(DbConnection dbConn, DescriptorDefinition descriptor, int page , int pagesize) {
        super();
        caption = descriptor.getName();
        DescriptorDefinition d = null;
        if (descriptor.getId() == -1) {
        	DbDescriptors dbd = new DbDescriptors(dbConn);
        	
        	try {
        		dbd.initialize();
        		if (dbd.getDescriptorIdByName(descriptor) > 0) 
        			d = descriptor;
        		else {
        			if (dbd.addDescriptor(descriptor) > 0)
        				d = descriptor;
        		}
        	} catch (DbAmbitException x) {
        		d = null;
        		logger.error(descriptor.getName(),x);
        	}
        	
        }
		
        try {
            stmt = dbConn.getConn().prepareStatement(MISSING_DESCRIPTOR + " limit " + Long.toString(page) + "," + Long.toString(pagesize),
            		java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            stmt.clearParameters();
            stmt.setInt(1,descriptor.getId());
			stmt.setFetchSize(Integer.MIN_VALUE);
			ResultSet rs = stmt.executeQuery();
			System.out.println(stmt);
			setResultset(rs);
        } catch (SQLException x) {
            logger.error(x);
            stmt = null;
        }
    }
    /**
     * 
     * @param dbConn
     * @param descriptors  {@link DescriptorsHashtable} descriptors
     * @param page
     * @param pagesize
     * <pre>
select L.idstructure,idsubstance from (
select structure.idstructure,idsubstance from structure where idstructure not in (select idstructure from dvalues where iddescriptor=?)
union
select structure.idstructure,idsubstance from structure where idstructure not in (select idstructure from dvalues where iddescriptor=?)
) as L
join struc_dataset using(idstructure) where id_srcdataset=?
limit ?,?
     * </pre>
     */
    public DbStructureMissingDescriptorsReader(DbConnection dbConn, DescriptorsHashtable descriptors, SourceDataset srcDataset, int page , int pagesize) {
        super();
        StringBuffer sql = new StringBuffer();
        caption = "";
        try {
	        Enumeration keys = descriptors.keys();
	        sql.append("select L.idstructure,idsubstance from (");
	        int c = 0;
		    while (keys.hasMoreElements()) {
		    	DescriptorDefinition descriptor = descriptors.getAmbitDescriptor(keys.nextElement());
		        if (descriptor.getId() == -1) {
		        	DbDescriptors dbd = new DbDescriptors(dbConn);
		        	
		        	try {
		        		dbd.initialize();
		        		if (dbd.getDescriptorIdByName(descriptor) <= 0)  { 
		        			if (dbd.addDescriptor(descriptor) <= 0) continue;
		        		}
		        	} catch (DbAmbitException x) {
		        		logger.debug(x);
		        		logger.error(descriptor.getName(),x);
		        	}
		        	
		        }
		        caption = caption + "\n" + descriptor.getName();
        		if (c > 0) sql.append("\nunion\n");		        
        		sql.append(MISSING_DESCRIPTOR);
        		c++;
		        
		    }	
		    sql.append("\n) as L");
		    if (srcDataset != null) sql.append("\njoin struc_dataset using(idstructure) where id_srcdataset=?");
		    sql.append("\nlimit ?,?");
		    /*
		    if (c > 0) {
		    	sql.append("\nlimit ");
		    	sql.append(Integer.toString(page));
		    	sql.append(",");
		    	sql.append(Integer.toString(pagesize));
		    }
		    */
		   // logger.debug(sql.toString());
	    } catch (Exception x) {
    		logger.debug(x);
    		logger.error(x.getMessage());	    	
	    }
 
		
        try {
            stmt = dbConn.getConn().prepareStatement(sql.toString(),
            		java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            stmt.clearParameters();
            
            Enumeration keys = descriptors.keys();
            int c=1;
            while (keys.hasMoreElements()) {
		    	DescriptorDefinition descriptor = descriptors.getAmbitDescriptor(keys.nextElement());
		        if (descriptor.getId() > 0) {
		        	stmt.setInt(c,descriptor.getId());
		        	c++;
		        }
            }    
            if (srcDataset != null) { stmt.setInt(c,srcDataset.getId()); c++; }
            
            stmt.setInt(c, page * pagesize); c++;
            stmt.setInt(c, (page+1) * pagesize);
			stmt.setFetchSize(Integer.MIN_VALUE);
			System.out.println(stmt);
			ResultSet rs = stmt.executeQuery();
			setResultset(rs);
        } catch (SQLException x) {
        	logger.debug(x);
            logger.error(x);
            stmt = null;
        }
    }    
	public DbStructureMissingDescriptorsReader(ResultSet resultset) {
		super(resultset);
	}
 
    public void close() throws IOException {
    	super.close();
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException x) {
            logger.error(x);
        }
        super.close();
    }
    public String toString() {
   		return "Reads structures without descriptor " + caption + " values from database";
    }
}
