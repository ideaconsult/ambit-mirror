package ambit2.test.io;

import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.Joinable;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;
import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;

import ambit2.database.ConnectionPool;
import ambit2.io.RawIteratingSDFReader;
import ambit2.repository.PropertyWriter;
import ambit2.repository.RepositoryReader;
import ambit2.repository.RepositoryWriter;
import ambit2.repository.StructureRecord;
import ambit2.workflow.AmbitWorkflowContextPanel;

import com.microworkflow.events.WorkflowContextEvent;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;
import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.JoinRowSetImpl;

public class TestRawIteratingSDFReader extends TestCase {
	public void xtest() throws Exception  {
		
		ConnectionPool pool = new ConnectionPool("localhost","3306","ambit2","root","sinanica",1,1);
        Connection connection = pool.getConnection();
        
		String filename = "D:/nina/Chemical Databases/EINECS/einecs_structures_V13Apr07.sdf";
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new FileReader(filename));
		RepositoryWriter writer = new RepositoryWriter(connection);
		int records = 0;
		long now = System.currentTimeMillis();
		while (reader.hasNext()) {
			Object o = reader.next();
			writer.write(o.toString());
			records ++;
			if ((records % 50) == 0)
				System.out.println(records);
		}
		reader.close();
		writer.close();
		now = System.currentTimeMillis() - now;
		System.out.println("Records "+records + " " + now + " ms.");
	}
	public void xtestPropertyWriter() throws Exception {
		
		ConnectionPool pool = new ConnectionPool("localhost","3306","ambit_repository","root","sinanica",1,1);
        Connection connection = pool.getConnection();
        RepositoryReader reader = new RepositoryReader(connection);
        PropertyWriter propertyWriter = new PropertyWriter(connection);
        reader.open();
        int records = 0;
		long now = System.currentTimeMillis();
		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		StructureRecord o  ;
		while (reader.hasNext()) {
			o = reader.next();
			String content = reader.getStructure(o.getIdstructure()).trim();
			
			IteratingMDLReader mReader = new IteratingMDLReader(new StringReader(content),b);
			
			if (mReader.hasNext()) {
				Object mol = mReader.next();
				if (mol instanceof IMolecule) {
					o.setProperties(((IMolecule)mol).getProperties());
					propertyWriter.write(o);
				}
				
			}
			
			o.clear();
			mReader.close();
			mReader = null;
			records ++;
			if ((records % 50) == 0)
				System.out.println(records);
		}
		reader.close();
		propertyWriter.close();
		now = System.currentTimeMillis() - now;
		System.out.println("msec/records "+(double)now/(double)records);
	}
    public void testCachedRecordSet() throws Exception {
        /**
  Contacts.setDataSourceName("Databases/ContactsDB/Datasource");
  Contacts.setCommand("SELECT name, telephone from Contacts");
  Contacts.execute();
         */
/**
  // get connection from pool
  InitialContext ctx = new InitialContext(); 
  javax.sql.DataSource ds =
    (javax.sql.DataSource)ctx.lookup("Databases/ContactsDB/DataSource");
  java.sql.Connection con = ds.getConnection();
  Contacts.setCommand("SELECT name, telephone from Contacts");
  // supply the connection to the RowSet
  Contacts.execute(con);
 */        
    	ConnectionPoolDataSource src;
        
        Class.forName("com.mysql.jdbc.Driver");
        
        DataSource dataSource = setupDataSource("jdbc:mysql://localhost:3306/ambit2?user=root&password=sinanica");
         
        CachedRowSet join = new CachedRowSetImpl();
        join.setPageSize(10);
        join.setCommand("SELECT s.idstructure,selected,uncompress(structure),format FROM query_results as q join structure as s using (idstructure)");
        //join.setCommand("SELECT s.idstructure,selected,format FROM query_results as q join structure as s using (idstructure)");
        
        Connection c = dataSource.getConnection();
        int [] keys = {1,2};
        join.execute(c);
        c.close();

        int records = 0;
        int pages = 0;

        join.nextPage();
        join.first();
        final WorkflowContext wc = new WorkflowContext();
        IWorkflowContextFactory f = new IWorkflowContextFactory() {
            public WorkflowContext getWorkflowContext() {
                return wc;
            }
        };
        AmbitWorkflowContextPanel p = new AmbitWorkflowContextPanel(f);
        wc.put(WorkflowContextEvent.WF_ANIMATE, new Boolean(true));
        wc.put("Result", join);
        JOptionPane.showMessageDialog(null,p);


        try {
            join.acceptChanges(dataSource.getConnection());
        } catch (SyncProviderException spe) {
        	SyncResolver resolver = spe.getSyncResolver();
        	Object crsValue; // value in crs
        	Object resolverValue; // value in the SyncResolver object
        	Object resolvedValue; // value to be persisted
        	while (resolver.nextConflict()) {
	        	if (resolver.getStatus() == SyncResolver.UPDATE_ROW_CONFLICT) {
	        	int row = resolver.getRow();
	        	join.absolute(row);
	        	int colCount = join.getMetaData().getColumnCount();
	        	for (int j = 1; j <= colCount; j++) {
		        	if (resolver.getConflictValue(j) != null) {
			        	crsValue = join.getObject(j);
			        	resolverValue = resolver.getConflictValue(j);
			        	 // compare crsValue and resolverValue to determine the
			//        	 value to be persisted
			        	if ("uncompress(structure)".equals(join.getMetaData().getColumnName(j))) continue;
			        	
			        	System.out.println(join.getMetaData().getColumnName(j)+ crsValue);
			        	resolvedValue = crsValue;
			        	resolver.setResolvedValue(j, resolvedValue);
			        }
	        	}
	        	}
        	}
        }

        join.close();
        //System.out.println(pages+"\t"+records);

    }
    public static DataSource setupDataSource(String connectURI) {
        //
        // First, we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool connectionPool = new GenericObjectPool(null);

        //
        // Next, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,null);

        //
        // Now we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);

        //
        // Finally, we create the PoolingDriver itself,
        // passing in the object pool we created.
        //
        PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

        return dataSource;
    }
    /**
select SUBSTRING_INDEX(user(),'@',1)

insert into sessions (user_name) values (SUBSTRING_INDEX(user(),'@',1))
insert into query (idsessions,name) values (1,"test")


insert ignore into query_results (idquery,idstructure,selected)
select 1,idstructure,1 from structure limit 100,200

     */
}
