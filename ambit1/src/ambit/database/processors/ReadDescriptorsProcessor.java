package ambit.database.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IChemObject;

import ambit.data.IAmbitEditor;
import ambit.database.query.DescriptorQuery;
import ambit.database.query.DescriptorQueryList;
import ambit.database.query.DistanceQuery;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.IAmbitDBProcessor;
import ambit.processors.IAmbitResult;
/**
 * Reads descriptors from database and assigns as molecule properties 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ReadDescriptorsProcessor extends DefaultAmbitProcessor implements IAmbitDBProcessor {
	protected DescriptorQueryList descriptors = null;
	protected PreparedStatement ps = null;
	protected static final String sql = "select iddescriptor,value from dvalues where idstructure=? ";
	protected static final String sqlDescriptors = " and iddescriptor in ";
	protected String descriptorsIDS = "";
	protected Connection connection;
	
	public ReadDescriptorsProcessor(Connection connection) {
		this(null,connection);
	}
	public ReadDescriptorsProcessor(DescriptorQueryList descriptors, Connection connection) {
		super();
		if (descriptors == null) this.descriptors = new DescriptorQueryList();
		else this.descriptors = descriptors;
		ps = createPreparedStatement(connection);
		this.connection = connection;
	}
	protected PreparedStatement createPreparedStatement(Connection connection) {
		boolean lookup = false;
		PreparedStatement ps = null;
		StringBuffer b = new StringBuffer();
		char d = ' ';
		if (descriptors != null) {
			for (int i=0; i < descriptors.size();i++) {
				DescriptorQuery q = descriptors.getDescriptorQuery(i);
				if (q.isEnabled() && !(q instanceof DistanceQuery)) {
					b.append(d);
					b.append(q.getId());
					if (q.getId() > -1) lookup = true;
					d=',';
				} 
			}
			descriptorsIDS = b.toString();
			if (lookup)
			try {
				ps = connection.prepareStatement(sql + sqlDescriptors + "(" + descriptorsIDS + ")");
			} catch (SQLException x) {
				ps = null;
			}
		} else 	
			try {
				ps = connection.prepareStatement(sql);
			} catch (SQLException x) {
				ps = null;
			}
		return ps;	
	}
	public Object process(Object object) throws AmbitException {
	    if (object == null) return null;
		Object o = ((IChemObject) object).getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
		if (o==null) return object;
		int idstructure = -1;
		try {
			//idstructure = Integer.parseInt(o.toString());
		    idstructure = ((Integer) o).intValue();
		} catch (Exception x) {
			return object;
		}
		if (ps == null) ps = createPreparedStatement(connection);
		if (ps != null) {
			try {
				ps.clearParameters();
				ps.setInt(1,idstructure);
				//ps.setString(2,descriptorsIDS);
				ResultSet rs = ps.executeQuery();
				int iddescriptor;
				String value;
				
				while (rs.next()) {
					iddescriptor = rs.getInt("iddescriptor");
					value = rs.getString(2);
					if (value != null)
					for (int i=0; i < descriptors.size();i++) {
						DescriptorQuery q = descriptors.getDescriptorQuery(i);
						if (q.getId() == iddescriptor) {
							((IChemObject) object).removeProperty(q.getName());
							((IChemObject) object).setProperty(q,value);
							break;
						}			
					}	
				}
				rs.close();
			} catch (SQLException x) {
				x.printStackTrace();
			}
		} else {
			logger.warn("Prepared statement does not exist.");
		}
		return object;
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
	/* (non-Javadoc)
     * @see ambit.processors.IAmbitDBProcessor#close()
     */
    public void close() {
    	
        try {
        	if (ps != null) ps.close();
        } catch (SQLException x) {
        	logger.info(x);
        }
        connection = null;
    }
    public IAmbitEditor getEditor() {
    	return new ReadDescriptorsEditor(descriptors);
    }
    public String toString() {
    	return "Reads descriptors \n" + descriptors.toString() + "\nfrom database ";
    }
}
