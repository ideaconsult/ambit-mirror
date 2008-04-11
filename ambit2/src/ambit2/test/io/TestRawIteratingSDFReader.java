package ambit2.test.io;

import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;

import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;

import com.microworkflow.events.WorkflowContextEvent;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;
import com.sun.rowset.CachedRowSetImpl;

import ambit2.database.ConnectionPool;
import ambit2.io.RawIteratingSDFReader;
import ambit2.repository.PropertyWriter;
import ambit2.repository.RepositoryReader;
import ambit2.repository.RepositoryWriter;
import ambit2.repository.StructureRecord;
import ambit2.workflow.AmbitWorkflowContextPanel;

public class TestRawIteratingSDFReader extends TestCase {
	public void xtest() throws Exception  {
		
		ConnectionPool pool = new ConnectionPool("localhost","3306","ambit_repository","root","sinanica",1,1);
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
	public void testPropertyWriter() throws Exception {
		
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
        CachedRowSet crs = new CachedRowSetImpl();
        Class.forName("com.mysql.jdbc.Driver");
        // initialize our CachedRowSet bean
        crs.setUsername("root");         // example userid 
        crs.setPassword("");     // example password
        crs.setPageSize(10);
        crs.setUrl("jdbc:mysql://localhost:33060/ambit"); // example DSN
        crs.setCommand("SELECT idstructure,uncompress(structure) from structure limit 1000");
        crs.execute();
        int records = 0;
        int pages = 0;
/*
        while (crs.nextPage()) {
            while (crs.next()) {
                System.out.println(crs.getString(1));
                records++;
            }
            pages++;
      }
            */
        crs.nextPage();
        crs.first();
        final WorkflowContext wc = new WorkflowContext();
        IWorkflowContextFactory f = new IWorkflowContextFactory() {
            public WorkflowContext getWorkflowContext() {
                return wc;
            }
        };
        AmbitWorkflowContextPanel p = new AmbitWorkflowContextPanel(f);
        wc.put(WorkflowContextEvent.WF_ANIMATE, new Boolean(true));
        wc.put("Result", crs);
        JOptionPane.showMessageDialog(null,p);
        crs.close();
        //System.out.println(pages+"\t"+records);

    }
}
