package ambit2.db.test;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

public class ExtractData {
	
    public static void main(String[] args) throws Exception {
    	extract();
    }
    protected static void extract() throws Exception {
        // database connection
        Class driverClass = Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ambit-test", "guest", "guest");
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        String ids = "(11)";
        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        partialDataSet.addTable("users", "SELECT * FROM users WHERE user_name=\"guest\"");
        partialDataSet.addTable("substance_experiment", "SELECT * FROM substance_experiment");
        
        partialDataSet.addTable("substance_protocolapplication", "SELECT * FROM substance_protocolapplication");
        
        partialDataSet.addTable("catalog_references", "SELECT * FROM catalog_references");

   
        partialDataSet.addTable("chemicals", "SELECT * FROM chemicals WHERE idchemical in "+ids);
        partialDataSet.addTable("structure", "SELECT idstructure,idchemical,structure,format,user_name,type_structure,atomproperties FROM structure join chemicals using(idchemical) WHERE idchemical in "+ids);
        partialDataSet.addTable("substance", "SELECT * FROM substance ");
        
        partialDataSet.addTable("properties", "SELECT * FROM properties");         
  
        partialDataSet.addTable("template","select * from template");
        partialDataSet.addTable("template_def","select idtemplate,idproperty from template_def");       
        
        partialDataSet.addTable("dictionary", "SELECT * FROM dictionary");
        
        partialDataSet.addTable("bundle", "SELECT * FROM bundle");
        partialDataSet.addTable("bundle_substance", "SELECT * FROM bundle_substance");
        partialDataSet.addTable("bundle_endpoints", "SELECT * FROM bundle_endpoints");
            
        partialDataSet.addTable("src_dataset", "SELECT id_srcdataset,name,user_name,idreference FROM src_dataset");
        partialDataSet.addTable("tuples",String.format("select * from tuples where idtuple in (select idtuple from property_tuples join property_values using(id) join structure using(idstructure) where structure.idchemical in %s)",ids)); 
 
       // partialDataSet.addTable("property_string", "SELECT * FROM property_string");
       // partialDataSet.addTable("property_values", "SELECT id,idproperty,idstructure,idvalue_string,value_num,status,property_values.user_name FROM property_values join structure using(idstructure) where idchemical in "+ids);     
       // partialDataSet.addTable("property_tuples", "SELECT idtuple,id FROM property_tuples join property_values using(id) where idstructure in (select idstructure from structure where idchemical in "+ids+")");          
        partialDataSet.addTable("struc_dataset", "SELECT idstructure,id_srcdataset FROM struc_dataset  where idstructure in (select idstructure from structure where idchemical in "+ids+")");        
        
       // partialDataSet.addTable("sessions", "SELECT idsessions,user_name FROM sessions");        
       // partialDataSet.addTable("query", "SELECT * FROM query");
       // partialDataSet.addTable("query_results", "SELECT * FROM query_results where idstructure in (select idstructure from structure where idchemical in "+ ids+")");

        //BIGINT serializes to long and gives errors for 
        partialDataSet.addTable("fp1024", "SELECT idchemical,fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16,time,bc,status FROM fp1024 join chemicals using(idchemical) WHERE idchemical in "+ids);
        partialDataSet.addTable("sk1024", "SELECT idchemical,fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16,time,bc,status FROM sk1024 join chemicals using(idchemical) WHERE idchemical in "+ids);
                
        FlatDtdDataSet.write(partialDataSet, new FileOutputStream("src/test/resources/ambit2/db/processors/test/partial-dataset.dtd"));
        FlatXmlDataSet.write(partialDataSet, 
        		new FileOutputStream("src/test/resources/ambit2/db/processors/test/partial-dataset.xml"));
        
        /*
        // full database export
        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full-dataset.xml"));
        */
    }
}
