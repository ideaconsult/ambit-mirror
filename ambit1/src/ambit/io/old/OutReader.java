/**
 * Created on 03-Mar-2005
 *
 */
package ambit.io.old;

import java.util.List;

import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorsList;

/**
 * A parent class for {@link OutBcfWin} and {@link OutKowWin} classes 
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-4-7
 */
public abstract class OutReader extends AmbitReader  {
	protected int records = 0;
	protected final String _smiles = "SMILES :";
	protected final String _name = "CHEM   :";
	Descriptor dCas, dName, dSmiles;
	public List extractColumnKeys(String line) {
		return null;
	}	
	
	public void getOUTFileDescriptors(DescriptorsList list) {
	    /*
		LiteratureEntry ref = ReferenceFactory.createEmptyReference();
		dCas = new Descriptor(DefaultColumnTypeSelection._columnTypesS[DefaultColumnTypeSelection._ctCAS],
				DescriptorType._typeStr,IColumnTypeSelection._ctCAS,ref);
		list.addItem(dCas);		
		dSmiles = new Descriptor(DefaultColumnTypeSelection._columnTypesS[DefaultColumnTypeSelection._ctSMILES],
				DescriptorType._typeStr,IColumnTypeSelection._ctSMILES,ref);		
		list.addItem(dSmiles);
		dName = new Descriptor(DefaultColumnTypeSelection._columnTypesS[DefaultColumnTypeSelection._ctChemName],
				DescriptorType._typeStr,IColumnTypeSelection._ctChemName,ref);		
		list.addItem(dName);
		*/		
	}
	public DescriptorsList initDescriptors() {	
		descriptors = new DescriptorsList();
		getOUTFileDescriptors(descriptors);
		return descriptors;
	}
    public void extractRowKeyAndData(String line, List columnKeys) {
//    	System.out.println(line);

    }
    
}
