/**
 * Created on 2005-3-24
 *
 */
package ambit.database.core;

import java.sql.SQLException;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorGroup;
import ambit.data.descriptors.DescriptorGroups;
import ambit.data.descriptors.DescriptorsList;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.Study;
import ambit.data.literature.AuthorEntry;
import ambit.data.literature.JournalEntry;
import ambit.data.literature.LiteratureEntry;
import ambit.data.model.Model;
import ambit.data.molecule.Compound;
import ambit.data.molecule.SourceDataset;
import ambit.data.molecule.SourceDatasetList;
import ambit.database.DbConnection;
import ambit.database.DbCore;
import ambit.database.exception.DbAmbitException;
import ambit.database.processors.ExperimentSearchProcessor;
import ambit.database.writers.ExperimentWriter;
import ambit.exceptions.AmbitException;


/**
 * @deprecated Use {@link ambit.database.search} instead.
 * Looks for {@link ambit.data.molecule.Compound}s in the database  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbSearch extends DbCore {
	protected ExperimentWriter dbw = null;
	protected ExperimentSearchProcessor esp = null;
	protected DbDescriptors dbd = null;
	protected DbReference dbr = null;
	protected DbSrcDataset dbc = null;
	protected DbModel dbm = null;	
	protected DbSearchCompound dbco = null;
	/**
	 * @param conn
	 */
	public DbSearch(DbConnection conn) {
		super(conn);

	}
	protected boolean initDBC() {
		if (dbc ==null) {
			dbc = new DbSrcDataset(dbconn);
			try {
				dbc.initialize();
				return true;
			} catch (DbAmbitException x) {
				x.printStackTrace();
				dbc = null;
				return false;
			}
		} else return true;
	}
	protected boolean initDBCompound() {
		if (dbco ==null) {
			dbco = new DbSearchCompound(dbconn);
			try {
				dbco.initialize();
				return true;
			} catch (DbAmbitException x) {
				x.printStackTrace();
				dbco = null;
				return false;
			}
		} else return true;
	}	
	
	protected boolean initDBD() {
		if (dbd ==null) {
			dbd = new DbDescriptors(dbconn);
			try {
				dbd.initialize();
				return true;
			} catch (DbAmbitException x) {
				x.printStackTrace();
				dbd = null;
				return false;
			}
		} else return true;
	}
	protected boolean initDBM() {
		if (dbm ==null) {
			dbm = new DbModel(dbconn);
			try {
				dbm.initialize();
				return true;
			} catch (DbAmbitException x) {
				x.printStackTrace();
				dbm = null;
				return false;
			}
		} else return true;
	}
	
	protected boolean initDBE() {
		if (dbw ==null) {
			dbw = new ExperimentWriter(dbconn);
		}
		try {
		if (esp == null)
		    esp = new ExperimentSearchProcessor(dbconn.getConn());
		} catch (AmbitException x) {
		    esp = null;
		}
		return true;
	}
	protected boolean initDBR() {
		if (dbr ==null) {
			dbr = new DbReference(dbconn);
			try {
				dbr.initialize();
				return true;
			} catch (DbAmbitException x) {
				x.printStackTrace();
				dbr = null;
				return false;
			}
		} else return true;
	}
	
	public AmbitList search(AmbitObject object)  {
		if (object instanceof Study) 
			try {
				if (initDBE())
					return esp.search((Study) object); 
			} catch (AmbitException x) {
				return null;
			}
		else if (object instanceof Experiment) 
			try {
				return esp.search((Experiment) object);
			} catch (AmbitException x) {
				return null;
			}

		else if (object instanceof DescriptorDefinition) 
			try {
				//TODO search descriptors
				if (initDBD()) {
					DescriptorsList l = new DescriptorsList();
					dbd.getDescriptorByName((Descriptor) object);
					l.addItem(object);
					return l;
				}	 
			} catch (DbAmbitException x) {
				return null;
			}
			
		else if (object instanceof DescriptorGroup) 
			try {
				if (initDBD()) {
					DescriptorGroups g = new DescriptorGroups();	
					dbd.getGroupByName((DescriptorGroup) object);
					g.addItem(object);
					return g;
				}	
			} catch (DbAmbitException x) {
				return null;
			}
		
		else if (object instanceof AuthorEntry) 
			try {
				if (initDBR())
					return dbr.searchAuthor((AuthorEntry) object); 
			} catch (DbAmbitException x) {
				return null;
			}
		else if (object instanceof LiteratureEntry) 
			try {
				if (initDBR())
					return dbr.searchReferences((LiteratureEntry) object); 
			} catch (DbAmbitException x) {
				x.printStackTrace();
				return null;
			}
		else if (object instanceof JournalEntry) 
			try {
				if (initDBR())
					return dbr.searchJournal((JournalEntry) object); 
			} catch (DbAmbitException x) {
				x.printStackTrace();
				return null;
			}
		else if (object instanceof SourceDataset) 
			try {
				if (initDBC())
					return dbc.searchDatasets((SourceDataset) object,new SourceDatasetList()); 
			} catch (DbAmbitException x) {
				x.printStackTrace();
				return null;
			}
		else if (object instanceof Compound) 
			try {
				if (initDBCompound())
					return dbco.searchBy((Compound) object); 
			} catch (SQLException x) {
				x.printStackTrace();
				return null;
			}
		else return null;
		
		//
		return null;
	}
	
	public boolean add(AmbitObject object) throws DbAmbitException {
		if (object instanceof Study) 
			if (initDBE())	return dbw.write((Study) object) > -1;
			else return false;
		else if (object instanceof Experiment) 
			if (initDBE()) return dbw.write((Experiment) object) > -1;
			else return false;
		else if (object instanceof DescriptorDefinition) 
			if (initDBD()) return dbd.addDescriptor((DescriptorDefinition) object) > -1;
			else return false;
		else if (object instanceof DescriptorGroup) 
			if (initDBD()) {
					//TODO add DescriptorGroup
					return false;
					//dbd.addDescripto((DescriptorGroup) object);
				}	else return false;
		else if (object instanceof AuthorEntry) 
			if (initDBR()) return dbr.addAuthor((AuthorEntry) object) > -1;
			else return false;
		else if (object instanceof LiteratureEntry) 
			if (initDBR()) return dbr.addReference((LiteratureEntry) object) > -1;
			else return false;
		else if (object instanceof JournalEntry) 
			if (initDBR()) return dbr.addJournal((JournalEntry) object) > -1;
			else return false;
		else if (object instanceof Model) 
			if (initDBM())	return dbm.addModel((Model) object) > -1;
			else return false;
		else if (object instanceof SourceDataset) 
			if (initDBC())	return dbc.addSourceDataSet((SourceDataset) object) > -1;
			else return false;		
		else return false;		

	}
	
}
