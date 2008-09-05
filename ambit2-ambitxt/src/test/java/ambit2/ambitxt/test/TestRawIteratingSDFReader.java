package ambit2.ambitxt.test;

import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;
import javax.swing.JOptionPane;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;

import ambit2.core.data.IStructureRecord;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.db.RepositoryReader;
import ambit2.db.processors.PropertyWriter;
import ambit2.db.processors.RepositoryWriter;
import ambit2.workflow.ui.AmbitWorkflowContextPanel;

import com.microworkflow.events.WorkflowContextEvent;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;
import com.sun.rowset.CachedRowSetImpl;

public class TestRawIteratingSDFReader extends RepositoryTest {
	public void xtest() throws Exception  {
		
        Connection connection = datasource.getConnection();
        
		String filename = "D:/nina/IdeaConsult/Ambit2/EINECS/einecs_structures_V13Apr07.sdf";
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new FileReader(filename));
		RepositoryWriter writer = new RepositoryWriter();
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		long now = System.currentTimeMillis();

		while (reader.hasNext()) {
            IStructureRecord record = reader.nextRecord();
			writer.write(record);
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
		
//		DataSource dataSource = DatasourceFactory.getDataSource("jdbc:mysql://localhost:3306/ambit2?user=root&password=");
        Connection connection = datasource.getConnection();
        RepositoryReader reader = new RepositoryReader();
        reader.setConnection(connection);
        PropertyWriter propertyWriter = new PropertyWriter();
        propertyWriter.setConnection(connection);
        reader.open();
        int records = 0;
		long now = System.currentTimeMillis();
		DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
		IStructureRecord o  ;
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
        Connection c = datasource.getConnection();
        PreparedStatement ps = c.prepareStatement("SELECT idstructure,uncompress(structure),format FROM structure where idstructure>?",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1,100);
        //ps.setFetchSize(10);
        ps.setMaxRows(100);
        ResultSet rs = ps.executeQuery();
        
        CachedRowSet join = new CachedRowSetImpl();
        //join.setFetchSize(10);
        join.setPageSize(10);
        //join.setMaxRows(30);
        //join.setCommand("SELECT idstructure,uncompress(structure),format FROM structure");
        //join.setCommand("SELECT s.idstructure,selected,uncompress(structure),format FROM query_results as q join structure as s using (idstructure)");
        //join.setCommand("SELECT s.idstructure,selected,format FROM query_results as q join structure as s using (idstructure)");
        join.populate(rs,1);

        int [] keys = {1,2};
        //join.execute(c);
        //c.close();

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
        //System.out.println(join.size());
        JOptionPane.showMessageDialog(null,p);


        try {
            join.acceptChanges(datasource.getConnection());
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
        c.close();
        //System.out.println(pages+"\t"+records);

    }
 
    /**
select SUBSTRING_INDEX(user(),'@',1)

insert into sessions (user_name) values (SUBSTRING_INDEX(user(),'@',1))
insert into query (idsessions,name) values (1,"test")


insert ignore into query_results (idquery,idstructure,selected)
select 1,idstructure,1 from structure limit 100,200

     */
}
