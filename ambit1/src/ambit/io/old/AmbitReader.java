/**
 * Created on 2005-3-2
 *
 */
package ambit.io.old;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import ambit.data.descriptors.DescriptorsList;
import ambit.io.IColumnTypeSelection;
import ambit.io.IReadData;

/**
 * @deprecated
 * A parent class to all classes implementing input from text files 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public abstract class AmbitReader {
	protected IColumnTypeSelection columnOption = null; 
	protected IReadData readData = null;	
    protected DescriptorsList descriptors;
    protected List values;    
	/**
	 * 
	 */
	public AmbitReader() {
		super();
        descriptors = null;
        values = new ArrayList();
	}

	
	/**
	 * @return Returns the readData.
	 */
	public IReadData getReadData() {
		return readData;
	}
	/**
	 * @param readData The readData to set.
	 */
	public void setReadData(IReadData readData) {
		this.readData = readData;
	}
    
	/**
	 * @return Returns the columnOption.
	 */
	public IColumnTypeSelection getColumnOption() {
		return columnOption;
	}
	

	/**
	 * @param columnOption The columnOption to set.
	 */
	public void setColumnOption(IColumnTypeSelection columnOption) {
		this.columnOption = columnOption;
	}
    public abstract List extractColumnKeys(String line);
    
    public abstract void extractRowKeyAndData(String line, List columnKeys);
    public abstract void readDataset(Reader in) throws IOException;
    
    public int autoScan(Reader in,int maxrecords) throws IOException {
    	int r = 0;
    		BufferedReader reader = new BufferedReader(in);
            while (reader.readLine() != null) {
            	r++;
            }
    		
    	System.out.println("Records " + r);
    	
    	return r;
    }
    public void readDataset(InputStream inStream, IReadData data) throws IOException {
		setReadData(data);		
		InputStreamReader ir = new InputStreamReader(inStream);
		readDataset(ir);
		ir.close();
		inStream.close();
    }

    public void readDataset(String filename, IReadData data) throws IOException {
        	
		FileInputStream in = new FileInputStream(filename);
		readDataset(in,data);
		in.close();
    }
}
