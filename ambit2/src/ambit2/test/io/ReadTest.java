/**
 * Created on 15-Mar-2005
 *
 */
package ambit2.test.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;


/**
 * a test
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ReadTest {

	/**
	 *  
	 */
	public ReadTest() {
		super();

	}

	public static void main(String[] args) {
		long base_time = System.currentTimeMillis();
		Vector L = new Vector(0);

		
		int rows = 0;
		try {
		//RandomAccessFile rf = new RandomAccessFile(args[0],"r");
		BufferedReader rf = new BufferedReader( 
				new InputStreamReader(new FileInputStream(args[0])));
		
		String line = rf.readLine();
		while (line != null) {
			double data[] = new double[500];
			L.add(data);
			line = rf.readLine();
			rows ++;
			System.out.println(rows);
		}
		rf.close();
		rf = null;
		} catch (IOException x) {
			x.printStackTrace();
		}
		
		base_time = System.currentTimeMillis() - base_time;
		System.out.println(base_time + "msec");
		while (true) ;
	}
}
