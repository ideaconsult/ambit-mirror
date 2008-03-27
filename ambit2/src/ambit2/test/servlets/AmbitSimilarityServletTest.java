package ambit2.test.servlets;

import java.io.PrintWriter;
import java.sql.Connection;

import junit.framework.TestCase;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.database.ConnectionPool;
import ambit2.database.search.DbExactSearchReader;
import ambit2.database.search.DbSearchReader;
import ambit2.servlets.AmbitSimilarityServlet;

public class AmbitSimilarityServletTest extends TestCase {
	public void testSimilarity() {
		try {
			AmbitSimilarityServlet servlet = new AmbitSimilarityServlet();
			servlet.setImageURL("http://localhost:8080/image?smiles=");
		    servlet.setDisplayURL("http://ambit2.acad.bg/ambit/php/display2.php");			
			ConnectionPool pool = new ConnectionPool("localhost","3306","ambit","guest","guest",1,1);
			Connection connection = pool.getConnection();
			 
			/*
			IMolecule mol = MoleculeFactory.makeAlkane(6);
	        DbSearchReader reader = new DbSimilarityByAtomenvironmentsReader(
	                connection,
	                mol,
	                null,
	                0.5,
	                0,100);
	                			
			
	        DbSearchReader reader = new DbSimilarityByFingerprintsReader(
	                connection,
	                mol,
	                null,
	                0.5,
	                0,100); 
	        */
			IMolecule mol = new Molecule();
			mol.setProperty(CDKConstants.CASRN,"50-00-0");
	        DbSearchReader reader = new DbExactSearchReader(
	                connection,
	                mol,
	                null,
	                0,100);			
	        PrintWriter out = new PrintWriter(System.out);
	        servlet.dbSimilarity(reader,mol,out,AmbitSimilarityServlet.columnsExactSearch);
	        //servlet.dbSubstructureSearch(reader, mol, out);
			pool.returnConnection(connection);
			pool = null;
			out.flush();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
}
