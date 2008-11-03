package ambit2.db.test;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

public class ExtractDataForTest {
    public static void main(String[] args) throws Exception {
        // database connection
        Class driverClass = Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:33060/ambit2", "guest", "guest");
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        partialDataSet.addTable("chemicals", "SELECT * FROM chemicals WHERE idchemical in (7,10,11, 29141)");
        partialDataSet.addTable("structure", "SELECT * FROM structure join chemicals using(idchemical) WHERE idchemical in (7,10,11, 29141)");
        //BIGINT serializes to long and gives errors for 
        partialDataSet.addTable("fp1024", "SELECT * FROM fp1024 join chemicals using(idchemical) WHERE idchemical in (7,10,11, 29141)");
        partialDataSet.addTable("users", "SELECT * FROM users WHERE user_name=\"guest\"");
        partialDataSet.addTable("user_roles", "SELECT * FROM user_roles WHERE user_name=\"guest\"");
        partialDataSet.addTable("roles", "SELECT * FROM roles");         
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
