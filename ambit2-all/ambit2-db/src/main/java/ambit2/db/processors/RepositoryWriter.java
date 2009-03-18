/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.structure.InchiProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.AbstractStructureQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.QueryField;
import ambit2.db.search.QueryStructure;
import ambit2.db.search.StringCondition;
import ambit2.hashcode.MoleculeAndAtomsHashing;

/**
<pre>
tables chemicals, structure
</pre>  
http://bugs.mysql.com/bug.php?id=39191
 * @author nina
 *
 */
public class RepositoryWriter extends AbstractRepositoryWriter<IStructureRecord,List<IStructureRecord>> {
    /**
     * 
     */
    private static final long serialVersionUID = 6530309499663693100L;
    protected static final String select_chemical = "SELECT idchemical FROM CHEMICALS where idchemical=?";
    protected PreparedStatement ps_selectchemicals;

    protected static final String insert_chemical = "INSERT INTO CHEMICALS (idchemical,smiles,hashcode) values (null,?,?)";
	protected PreparedStatement ps_chemicals;
	protected static final String insert_structure = "INSERT INTO STRUCTURE (idstructure,idchemical,structure,format,updated,user_name) values (null,?,compress(?),?,CURRENT_TIMESTAMP,SUBSTRING_INDEX(user(),'@',1))";
	protected PreparedStatement ps_structure;

	protected static final String insert_dataset = "insert into struc_dataset SELECT ?,id_srcdataset from src_dataset where name=?";
	protected PreparedStatement ps_dataset;	
	
	protected DbSrcDatasetWriter datasetWriter;
	protected PropertyValuesWriter propertyWriter;
	protected SourceDataset dataset;
	protected MoleculeReader molReader;
	protected SmilesKey key;
	protected CASKey casKey;
	protected AbstractStructureQuery<String,String,StringCondition> query_chemicals;
	protected AbstractStructureQuery<String,String,StringCondition> query_cas;
	protected QueryExecutor exec;
	protected HashcodeKey hashcode;

	
	public RepositoryWriter() {
		datasetWriter = new DbSrcDatasetWriter();
		propertyWriter = new PropertyValuesWriter();
		molReader = new MoleculeReader();
		key = new SmilesKey();
		casKey = new CASKey();
		hashcode = new HashcodeKey();
		query_chemicals = new QueryStructure();
		query_chemicals.setId(-1);
		query_chemicals.setFieldname(key.getKey());
		
		query_cas = new QueryField();
		query_cas.setId(-1);
		exec = new QueryExecutor();

	}
	@Override
	public void open() throws DbAmbitException {
		super.open();
		datasetWriter.open();
		propertyWriter.open();
	}
	@Override
	public synchronized void setConnection(Connection connection)
			throws DbAmbitException {
		super.setConnection(connection);
		datasetWriter.setConnection(connection);
		propertyWriter.setConnection(connection);
		exec.setConnection(connection);
	}
	public SourceDataset getDataset() {
		return dataset;
	}
	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
		propertyWriter.setDataset(dataset);
	}
	public void writeDataset(IStructureRecord structure) throws SQLException {
		if (getDataset() == null) setDataset(new SourceDataset("Default"));
		datasetWriter.write(dataset);
		
		ps_dataset.clearParameters();
		ps_dataset.setInt(1,structure.getIdstructure());
		ps_dataset.setString(2,dataset.getName());
		ps_dataset.execute();
	}
	public void writeProperties(IStructureRecord structure,IAtomContainer molecule) throws SQLException, AmbitException {
			if (molecule == null)
				return;
			propertyWriter.process(structure);
			structure.setProperties(null);

	}	
	protected IAtomContainer getAtomContainer(IStructureRecord structure) {
		try {
			IAtomContainer molecule = molReader.process(structure);
			if (structure.getProperties()==null)
				structure.setProperties(molecule.getProperties());
			else
				structure.getProperties().putAll(molecule.getProperties());
			return molecule;
		} catch (AmbitException x) {
			logger.error(x);
			return null;
		}
	}
	protected void findChemical(AbstractStructureQuery<String,String,StringCondition> query, String value, IStructureRecord record)  {
		if (value == null) return;
		ResultSet rs = null;
		try {
			query.setValue(value);
			rs = exec.process(query);
			while (rs.next()) {
				record.setIdchemical(rs.getInt(2));
				break;
			}
		} catch (Exception x) {
			logger.error(x);
		} finally {
			try {
			 exec.closeResults(rs);
			} catch (Exception x) {logger.error(x);}
		}
		
	}
	public List<IStructureRecord> write(IStructureRecord structure) throws SQLException {
		IAtomContainer molecule = getAtomContainer(structure);
		Long hash = null;
		try {
			hash = hashcode.process(molecule);
		} catch (Exception x) {
			logger.warn(x);
			x.printStackTrace();
			hash = 0L;
		} finally {
			
		}
		String smiles = null;
		try {
			smiles = key.process(molecule);
		} catch (Exception x) {
			smiles = null;
		}
        //find if a structure with specified idchemical exists
        if (structure.getIdchemical() > 0) {
        	if (structure.getIdstructure()>0) {
        		
        	} else {
	        	ps_selectchemicals.clearParameters();
	            ps_selectchemicals.setInt(1,structure.getIdchemical());
	            
	            ResultSet rs = ps_selectchemicals.executeQuery();
	            structure.setIdchemical(-1);
	            structure.setIdstructure(-1);
	            try {
		            while (rs.next()) {
		                structure.setIdchemical(rs.getInt(1));
		            }
	            } catch (Exception x) {
	            } finally {
	            	rs.close();
	            }
        	}
        } else {
        	//find by CAS
        	String cas = null;
        	try {
        		cas = casKey.process(structure);
        		if (cas!=null)
        			findChemical(query_cas,cas,structure);
        	} catch (Exception x) {
        		logger.warn(x);
        	}
        	//if not found, find by SMILES
        	if (structure.getIdchemical()<=0)
        		findChemical(query_chemicals,smiles,structure);
        }
        List<IStructureRecord> sr = new ArrayList<IStructureRecord>();
        //add a new idchemical if idchemical <=0
        if (structure.getIdchemical() <= 0) {
        	if (ps_chemicals == null)
       		 ps_chemicals = connection.prepareStatement(insert_chemical,Statement.RETURN_GENERATED_KEYS);
        	if (smiles==null)
        		ps_chemicals.setNull(1,Types.CHAR);
        	else
        		ps_chemicals.setString(1,smiles);
        	if (hash == null) hash = 0L;
        	ps_chemicals.setLong(2,hash);
        	
    		ps_chemicals.executeUpdate();
    		ResultSet rs = ps_chemicals.getGeneratedKeys();
    		try {
	    		while (rs.next()) {
	                structure.setIdchemical(rs.getInt(1));
	    		} 
    		} catch (Exception x) {
    			logger.error(x);
    		} finally {
	    		rs.close();
	    		ps_chemicals.close();
	    		ps_chemicals = null;
    		}
    	
        }
        //add a new entry in structure table
        if (structure.getIdstructure() <= 0) {
	        if (ps_structure==null)
	        	ps_structure = connection.prepareStatement(insert_structure,Statement.RETURN_GENERATED_KEYS);
			ps_structure.clearParameters();        
	        ps_structure.setInt(1,structure.getIdchemical());
	        ps_structure.setString(2,structure.getWritableContent());
	        ps_structure.setString(3,structure.getFormat());
	        ps_structure.executeUpdate();
	        ResultSet rss = ps_structure.getGeneratedKeys();
	        while (rss.next())  {
	        	StructureRecord record = new StructureRecord(structure.getIdchemical(),rss.getInt(1),null,structure.getFormat());
	        	writeDataset(record);
	        	try {
	        		structure.setIdstructure(record.getIdstructure());
	        		writeProperties(structure,molecule);
	        	} catch (AmbitException x) {
	        		logger.warn(x);
	        	}
	            sr.add(record);
	        }
	        rss.close();
	        ps_structure.close();
	        ps_structure = null;
			molecule = null;
        } else {
        	try {
        		writeProperties(structure,molecule);
        	} catch (AmbitException x) {
        		logger.warn(x);
        	}        	
        }
        
        return sr;
        
	}
	protected void prepareStatement(Connection connection) throws SQLException {
		 ps_chemicals = connection.prepareStatement(insert_chemical,Statement.RETURN_GENERATED_KEYS);
		 ps_structure = connection.prepareStatement(insert_structure,Statement.RETURN_GENERATED_KEYS);
         ps_selectchemicals = connection.prepareStatement(select_chemical);
         ps_dataset = connection.prepareStatement(insert_dataset);
         datasetWriter.prepareStatement(connection);
	}
	public void close() throws SQLException {
        try {
        if (ps_dataset != null)
        	ps_dataset.close();
        if (ps_chemicals != null)
            ps_chemicals.close();
        if (ps_structure != null)
            ps_structure.close();
        if (ps_selectchemicals != null)
            ps_selectchemicals.close();        
        if (datasetWriter != null)
        	datasetWriter.close();
        if (propertyWriter != null)
        	propertyWriter.close();
        if (exec != null)
        	exec.close();
        } catch (SQLException x) {
            logger.error(x);
        }
        super.close();
	}
}

interface IStructureKey<Value,M> {
	public String getKey();
	public Value process(M molecule) throws AmbitException;
}

class CASKey implements IStructureKey<String,IStructureRecord> {
	protected String key=null;
	public CASKey() {
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String process(IStructureRecord structure) throws AmbitException {
		if (structure==null)
			throw new AmbitException("Empty molecule!");
		Object cas = structure.getProperty(key);
		if ((key == null) || (cas==null)) {
			//find which key corresponds to CAS
			Iterator keys = structure.getProperties().keySet().iterator();
			while (keys.hasNext()) {
				Object newkey = keys.next();
				if (CASNumber.isValid(structure.getProperties().get(newkey).toString())) {
					this.key = newkey.toString();
					return structure.getProperties().get(newkey).toString();
				}
			}
		}
		if (key == null) throw new AmbitException("CAS tag not defined");
		Object o = structure.getProperty(key);
		if (o != null) return o.toString();
		else return null;
	}
}

class SmilesKey implements IStructureKey<String,IAtomContainer> {
	protected SmilesGenerator gen;
	protected String key="smiles";
	public SmilesKey() {
		gen = new SmilesGenerator();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String process(IAtomContainer molecule) throws AmbitException {
		if ((molecule==null) || (molecule.getAtomCount()==0))
			throw new AmbitException("Empty molecule!");
		return gen.createSMILES((IMolecule)molecule);
	}
}

class EmptyKey implements IStructureKey<String,IAtomContainer> {
	
	public String getKey() {
		return null;
	}
	public String process(IAtomContainer molecule) throws AmbitException {
		return null;
	}
}

class InchiKey implements IStructureKey<String,IAtomContainer> {
	protected InchiProcessor inchi;
	protected String key;
	public InchiKey() {
		inchi = new InchiProcessor();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String process(IAtomContainer molecule) throws AmbitException {
		return inchi.process(molecule).getInchi();
	}
}

class HashcodeKey implements IStructureKey<Long,IAtomContainer> {
	protected MoleculeAndAtomsHashing hashing;
	protected String key;
	public HashcodeKey() {
		hashing = new MoleculeAndAtomsHashing();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Long process(IAtomContainer molecule) throws AmbitException {
		if ((molecule==null)||(molecule.getAtomCount()==0)) return 0L;
		return hashing.getMoleculeHash(molecule);
	}
}
