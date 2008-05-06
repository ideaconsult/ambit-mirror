/**
 * Created on 2005-3-2
 *
 */
package ambit2.io.old;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import ambit2.io.IColumnTypeSelection;
import ambit2.data.descriptors.Descriptor;
import ambit2.data.descriptors.DescriptorType;
import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;


/**
 * Reads SRC BCFWIN software output files *.out 
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-4-7
 * @see <a href="http://www.syrres.com/esc/bcfwin.htm" target=_blank>SRC BcfWin program</a>
 */
public class OutBcfWin extends OutReader {
	
	private final String _logkowEstimated = "Log Kow  (estimated)  :";
	private final String _logkowExperimental = "Log Kow (experimental):";
	private final String _logkowUsed ="Log Kow used by BCF estimates:";
	private final String _bcfEstimated = "Estimated Log BCF =";
	private final String _equation = "Equation Used to Make BCF estimate:";
	private final String _factor = "Correction(s):";	
	Descriptor dLogKow, dBCF, dEquation, dFactor;
	/**
	 * 
	 */
	public OutBcfWin() {
		super();
	}
	public void getOUTFileDescriptors(DescriptorsList list) {
		super.getOUTFileDescriptors(list);
		LiteratureEntry ref = ReferenceFactory.createBCFWinReference();
		
		dBCF = new Descriptor(_bcfEstimated,
				DescriptorType._typeReal,
				IColumnTypeSelection._ctYpredicted,ref);
		list.addItem(dBCF);
		dLogKow = new Descriptor(_logkowUsed,
				DescriptorType._typeReal,IColumnTypeSelection._ctX,ref);		
		list.addItem(dLogKow);		

		dEquation = new Descriptor(_equation,
				DescriptorType._typeStr,IColumnTypeSelection._ctEquation,ref);		
		list.addItem(dEquation);		

		dFactor = new Descriptor(_factor,
				DescriptorType._typeStr,IColumnTypeSelection._ctUnknown,ref);		
		list.addItem(dFactor);		
	}
	public DescriptorsList initDescriptors() {
		super.initDescriptors();
		return descriptors;
	}

     
    public void extractRecord(ArrayList record) {
    	//System.out.println(records + "\t" + record.toString());
    }
    
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
        	} else if (line.indexOf(_bcfEstimated) > -1) {
        		//parse estimated bcf
        		String bcf = line.replaceAll(_bcfEstimated,"").trim();
        		int n = bcf.indexOf("BCF =");
        		readData.setData(dBCF,bcf.substring(0,n-1).trim());        		
        		
                line = reader.readLine();
                line = reader.readLine();                
        		//readData.addRow();
                records++;
        	} else if (line.indexOf(_smiles) > -1) {
        		readData.addRow();
        		//TODO parse CAS
        		while (prev.indexOf("0") == 0) prev = prev.substring(1); 
        		readData.setData(dCas,prev.trim());
        		//SMILES
        		String smi = line.replaceAll(_smiles,"").trim();        		
        		smi = smi.replaceAll("CL","Cl").trim();        		
        		readData.setData(dSmiles,smi);

        	} else if (line.indexOf(_name) > -1) {
        		//parse NAME
        		readData.setData(dName,line.replaceAll(_name,"").trim());
        	} else if (line.indexOf(_logkowUsed) > -1) {
        		//parse logKow used
        		readData.setData(dLogKow,line.replaceAll(_logkowUsed,"").trim());
        	} else if (line.indexOf(_equation) > -1) {
        		//parse logKow used
        		prev = line;
        		line = reader.readLine();
        		readData.setData(dEquation,line.trim());
        	} else if (line.indexOf(_factor) > -1) {
        		//parse logKow used
        		prev = line;
        		line = reader.readLine();
        		readData.setData(dFactor,line.trim());
        	}

        	
        	prev = line;
            line = reader.readLine();
            lineIndex++;
        }
    */         
    }
    
    
	public static void main(String[] args) {
	    //TODO rewrite it as IteratingReader and junit test
	    /*
		if (args.length == 0) {
			System.out.println("usage:\tOutBCFWin file.out");
			return;
		}
		OutBcfWin c = new OutBcfWin();
		ReadDataFingerprints dr = new ReadDataFingerprints();
		try {
		c.readDataset(args[0], dr);
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
			System.out.println((i/10.0) + ".\t" + hist[i]);		
		}
		BitSet consensus = dr.profileToBitSet(0.1);		
		System.out.println(consensus.toString());
		double[] distance =  dr.compare(consensus); 	
		
		MySimpleLogger log = new MySimpleLogger();
		try {
		log.open("logs\\outbcfwin.csv");

		log.writeln("Tanimoto," +dr.getHeader(','));		
		for (int i = 0; i < (dr.getNPoints()); i++)
			log.writeln(distance[i] + "," + dr.rowToString(i,','));
		} catch (IOException x) {
			x.printStackTrace();
		}		
		log.flush();
		log.close();
		c = null;
		dr = null;		
		c = null;
		dr = null;
		*/
	}
	
}
