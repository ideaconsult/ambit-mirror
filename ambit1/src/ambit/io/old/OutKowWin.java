/**
 * Created on 03-Mar-2005
 *
 */
package ambit.io.old;

import java.io.IOException;
import java.io.Reader;

import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorsList;

/**
 * Reads SRC KOWWIN software output files *.out 
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-4-7
 * @see <a href="http://www.syrres.com/esc/kowwin.htm" target=_blank>SRC KowWin program</a> 
 */
public class OutKowWin extends OutReader {
	protected final String _logKowEstimated = "Log Kow   =";
	protected Descriptor dLogKow;

	public void getOUTFileDescriptors(DescriptorsList list) {
	    /*
		super.getOUTFileDescriptors(list);
		LiteratureEntry ref = ReferenceFactory.createKOWWinReference(); 
		
		dLogKow = new Descriptor(
				DefaultColumnTypeSelection._columnTypesS[DefaultColumnTypeSelection._ctYpredicted],
				DescriptorType._typeStr,
				IColumnTypeSelection._ctYpredicted,
				ref);
		descriptors.addItem(dLogKow);
		*/	
	}	
	public DescriptorsList initDescriptors() {	
		super.initDescriptors();
		return descriptors;
	}
	/* (non-Javadoc)
	 * @see ambit.io.AmbitReader#readDataset(java.io.Reader)
	 */
    public void readDataset(Reader in) throws IOException {
    	/*
        BufferedReader reader = new BufferedReader(in);
        if (descriptors == null) initDescriptors();
        int lineIndex = 0;
        if (readData == null) readData = new AllData();  
        readData.initialize(descriptors);
        
        String prev ="";
        String line = reader.readLine();
        records = 0;

        //readData.addRow();
        while (line != null) {
        	if (line.indexOf("NO CAS Match in SMILECAS") > -1) {
                records++;
        	} else if (line.indexOf(_logKowEstimated) > -1) {
        		//parse estimated bcf
        		String lk = line.replaceAll(_logKowEstimated,"").trim();
        		
        		readData.setData(dLogKow,lk);        		
        		
                line = reader.readLine();
                line = reader.readLine();                
        		//readData.addRow();
                records++;
        	} else if (line.indexOf(_smiles) > -1) {
        		//TODO parse CAS
        		readData.addRow();
        		while (prev.indexOf("0") == 0) prev = prev.substring(1); 
        		readData.setData(dCas,prev.trim());
        		//SMILES
        		String smi = line.replaceAll(_smiles,"").trim();
        		smi = smi.replaceAll("CL","Cl").trim();
        		//smi = smi.replaceAll("(H)","[H]").trim();
        		//smi = smi.replaceAll("[se]","[Se]").trim();        		
        		
        		readData.setData(dSmiles,smi);

        	} else if (line.indexOf(_name) > -1) {
        		//parse NAME
        		readData.setData(dName,line.replaceAll(_name,"").trim());
        	}	
        	prev = line;
            line = reader.readLine();
            lineIndex++;
        }
        */
             
    }


	public static void main(String[] args) {
	    /*
		if (args.length == 0) {
			System.out.println("usage:\tOutKowWin file.out");
			return;
		}
		OutKowWin c = new OutKowWin();
		ReadDataFingerprints dr = new ReadDataFingerprints();
		c.setReadData(dr);
		FileInputStream in;
		try {
			in = new FileInputStream(args[0]);
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			return;
		}
		InputStreamReader ir = new InputStreamReader(in);
		try {
			c.readDataset(ir);
			ir.close();
			in.close();
			
		} catch (IOException x) {
			x.printStackTrace();
		}



		double fpProfile[] = dr.getFPProfile();
		StringBuffer buf = new StringBuffer();
		for (int i =0; i< 1024; i++) {
			System.out.print((i+1) + ".\t");
			System.out.print(Double.toString(fpProfile[i]));
			System.out.println();
		}
		System.out.println();	
		double[] hist = dr.getHist();
		for (int i =0; i <  hist.length; i++) {
			System.out.println((i/10.0) + "\t" + hist[i]);		
		}
		
		c = null;
		dr = null;
				*/
	}

}
