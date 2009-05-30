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
                "jdbc:mysql://localhost:33060/ambit-test", "guest", "guest");
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        String ids = "(10,11,29141)";
        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        partialDataSet.addTable("users", "SELECT * FROM users WHERE user_name=\"guest\"");
        partialDataSet.addTable("roles", "SELECT * FROM roles");              
        partialDataSet.addTable("user_roles", "SELECT * FROM user_roles WHERE user_name=\"guest\"");
        
        partialDataSet.addTable("catalog_references", "SELECT * FROM catalog_references");

   
        partialDataSet.addTable("chemicals", "SELECT * FROM chemicals WHERE idchemical in "+ids);
        partialDataSet.addTable("structure", "SELECT idstructure,idchemical,structure,format,updated,user_name,type_structure FROM structure join chemicals using(idchemical) WHERE idchemical in "+ids);

        partialDataSet.addTable("properties", "SELECT * FROM properties");         
  
        partialDataSet.addTable("template","select * from template");
        partialDataSet.addTable("template_def","select * from template_def");       
        
        partialDataSet.addTable("dictionary", "SELECT * FROM dictionary");        
            
        partialDataSet.addTable("src_dataset", "SELECT * FROM src_dataset");
        partialDataSet.addTable("tuples","select * from tuples"); 
        partialDataSet.addTable("property_number", "SELECT * FROM property_number");        
        partialDataSet.addTable("property_string", "SELECT * FROM property_string");
        partialDataSet.addTable("property_int", "SELECT * FROM property_int");            
        partialDataSet.addTable("property_values", "SELECT id,idproperty,idstructure,idvalue,idtype,status,property_values.user_name FROM property_values join structure using(idstructure) where idchemical in "+ids);     
        partialDataSet.addTable("property_tuples", "SELECT * FROM property_tuples");          
        partialDataSet.addTable("struc_dataset", "SELECT * FROM struc_dataset");        
        
        partialDataSet.addTable("query", "SELECT * FROM query where idquery<5");
        partialDataSet.addTable("query_results", "SELECT * FROM query_results where idquery<5");
        partialDataSet.addTable("sessions", "SELECT * FROM sessions");
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
