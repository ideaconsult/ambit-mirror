/**
 * Created on 2005-3-18
 *
 */
package ambit2.test.database;

import junit.framework.TestCase;
import ambit2.database.DbConnection;
import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorGroup;
import ambit2.data.descriptors.DescriptorGroups;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;
import ambit2.database.core.DbDescriptors;
import ambit2.database.core.DbReference;
import ambit2.database.exception.DbAmbitException;
import ambit2.database.query.DescriptorQueryList;
import ambit2.exceptions.AmbitException;


/**
 * JUnit test for {@link ambit2.database.core.DbDescriptors} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbDescriptorsTest extends TestCase {
	private DbConnection dbconn = null;
	private DbDescriptors dbd = null;
	private DbReference dbr = null;
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbDescriptorsTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		dbconn = new DbConnection("localhost","3306","ambittest","lri_admin","lri");
		dbconn.open();
		dbd = new DbDescriptors(dbconn);
		dbd.initialize();
		dbr = new DbReference(dbconn);
		dbr.initialize();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		dbd.close(); dbd = null;
		dbr.close(); dbr = null;
		dbconn.close();
		dbconn = null;
		
	}
	/**
	 * Constructor for DbDescriptorsTest.
	 * @param arg0
	 */
	public DbDescriptorsTest(String arg0) {
		super(arg0);
	}

	public void testAddDescriptor() {
		try {
			LiteratureEntry ref = ReferenceFactory.createEmptyReference();
			
			DescriptorGroups groups = new DescriptorGroups();
			groups.addItem(DescriptorFactory.createDescriptorPartitionGroup());
			
			DescriptorDefinition d = DescriptorFactory.createLogP(ReferenceFactory.createKOWWinReference());
			d.setReference(ref);
			d.setDescriptorGroups(groups);
			
			dbd.addDescriptor(d);
			
			assertTrue(d.getId() > -1);
			
		} catch (DbAmbitException x) {
			x.printStackTrace();
			fail();
		}
	}

	public void testAddGroup() {
		try {
			DescriptorGroup group = DescriptorFactory.createDescriptorPartitionGroup();
			int id = dbd.getGroupIDByName(group.getName());
			if (id > -1) dbd.deleteGroup(group.getName());
			id = dbd.addGroup(group);
			assertTrue(id > -1);
		} catch (DbAmbitException x) {
			x.printStackTrace();
			fail();			
		}
	}

	public void testAddDescriptorGroup() {
		try {
			DescriptorGroup group = DescriptorFactory.createDescriptorPartitionGroup();
			
			DescriptorDefinition d = DescriptorFactory.createLogP(ReferenceFactory.createKOWWinReference());
			int id = dbd.getDescriptorIdByName(d);
			 
			dbd.addDescriptorGroup(id, group);
			assertTrue(group.getId() > -1);
		} catch (DbAmbitException x) {
			x.printStackTrace();
			fail();			
		}		
	}
	
	public void testLoadQuery() {
		try {
			dbconn = new DbConnection("localhost","33060","ambit","root","");
			dbconn.open(false);
			dbd = new DbDescriptors(dbconn);
			dbd.initialize();
			DescriptorQueryList query = new DescriptorQueryList();
			dbd.loadQuery(query); 
			System.out.println(query.toString());
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();			
		}
	}



}
