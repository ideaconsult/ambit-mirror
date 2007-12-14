/**
 * Created on 2005-3-29
 *
 */
package ambit.domain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.descriptors.DescriptorFactory;
import ambit.data.model.Model;
import ambit.data.model.ModelFactory;
import ambit.data.molecule.CompoundFactory;
import ambit.data.molecule.MolProperties;
import ambit.exceptions.AmbitException;
import ambit.exceptions.AmbitIOException;
import ambit.io.DefaultAmbitIOListener;
import ambit.io.FileInputState;

/**
 * provides static funcitions to create several {@link ambit.domain.QSARDataset}s
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class QSARDatasetFactory   {

	protected QSARDatasetFactory(){
		super();
	}

	/**
	 * Loads Debnath dataset from a filename
	 * @param data
	 * @param model
	 * @param filename
	 * @return {@link QSARDataset} containing Debnath mutagenicity dataset 
	 */
	public static QSARDataset loadDebnathSmilesDataset(QSARDataset data,
						Model model,
						String filename) throws AmbitException {
		loadQSARdataset(data,filename,DescriptorFactory.createDebnathSmilesFileDescriptors());
		return data;
	}
	/**
	 * Loads Debnath dataset from an {@link java.io.InputStream} 
	 * @param data
	 * @param model
	 * @param stream
	 * @return {@link QSARDataset} containing Debnath mutagenicity dataset
	 */
	public static QSARDataset loadDebnathSmilesDataset(
	        QSARDataset data,
			Model model, InputStream stream, String filename)  throws AmbitException  {

		loadQSARdataset(data,stream,filename,DescriptorFactory.createDebnathSmilesFileDescriptors());
		return data;
	}
	
	/**
	 * 
	 * @param data
	 * @param model
	 * @param filename
	 * @return {@link QSARDataset} containing Debnath mutagenicity dataset
	 */
	public static QSARDataset loadDebnathDataset(
						QSARDataset data,
						Model model,
						String filename)  throws AmbitException  {
		loadQSARdataset(data,filename,DescriptorFactory.createDebnathFileDescriptors());
		return data;
	}

	public static QSARDataset createBCFGrammaticadataset(String filename)  throws AmbitException  {
		Model m = ModelFactory.createBCFGrammaticaModel();
		QSARDataset d = new QSARDataset(m);
		loadBCFGrammaticaDataset(d,m,filename);
		return d;
	}	
	
	/**
	 * loads BCF Grammatica dataset from a file
	 * @param filename
	 * @return {@link QSARDataset} containing Grammatica BCF dataset
	 */
	public static QSARDataset loadBCFGrammaticaDataset(
			QSARDataset data,
			Model model,
			String filename)  throws AmbitException  {

		loadQSARdataset(data,filename,DescriptorFactory.createGrammaticaFileDescriptors());
		return data;
	}	   	
	/**
	 * 
	 * @param m
	 * @param filename
	 * @return {@link QSARDataset} containing Debnath mutagenicity dataset
	 */
	public static QSARDataset createDebnathDataset(Model m,
						String filename)  throws AmbitException  {
		QSARDataset d = new QSARDataset(m);
		loadDebnathDataset(d,m,filename);
		return d;
	}	
	
	/**
	 * 
	 * @param filename
	 * @return {@link QSARDataset} containing Debnath mutagenicity dataset
	 */
	public static QSARDataset createDebnathDataset(String filename) throws AmbitException  {
		Model m = ModelFactory.createDebnathMutagenicityQSAR();
		return createDebnathDataset(m,filename);
	}
	
	public static QSARDataset createGlendeDataset() throws AmbitException {
		Model m = ModelFactory.createDebnathMutagenicityQSAR();
		QSARDataset d = new QSARDataset(m);
		AllData data = d.getData();
		data.addRow(CompoundFactory.createBenzene());
		data.addRow(CompoundFactory.create2Aminofluorene());
		data.addRow(CompoundFactory.create2Aminonaphthalene());
		data.addRow(CompoundFactory.create4Aminobiphenyl());
		return d;
	}	

	/**
	 * creates BCFWin dataset from a file
	 * @param filename
	 * @return
	 
	public static QSARDataset createBCFWinDataset(String filename) {
		Model m = ModelFactory.createBCFWINModel();
		OutBcfWin c = new OutBcfWin();
		AllData dr = new AllData();
		try {
			c.readDataset(filename, dr);
		} catch (IOException x) {
			x.printStackTrace();

		}
		QSARDataset d = new QSARDataset(m,dr);
		return d;
	}
	*/
	/**
	 * Creates KOWWIN dataset from a stream
	 * @param in
	 * @return
	 
	public static QSARDataset createKOWWinDataset(InputStream in) {
		Model m = ModelFactory.createKOWWINModel();
		OutKowWin c = new OutKowWin();
		AllData dr = new AllData();
		try {
			c.readDataset(in, dr);
		} catch (IOException x) {
			x.printStackTrace();
		}
		QSARDataset d = new QSARDataset(m,dr);
		return d;
	}
	*/	
	/**
	 * Creates KOWWIN dataset from a file
	 * @param filename
	 * @return
	 
	public static QSARDataset createKOWWinDataset(String filename) {
		Model m = ModelFactory.createKOWWINModel();
		OutKowWin c = new OutKowWin();
		AllData dr = new AllData();
		try {
			c.readDataset(filename, dr);
		} catch (IOException x) {
			x.printStackTrace();
		}
		QSARDataset d = new QSARDataset(m,dr);
		return d;
	}
	*/
	public static boolean loadQSARdataset(QSARDataset data,
	        	InputStream stream,
	        	String filename,
	        	MolProperties properties)  throws AmbitException { 
            IIteratingChemObjectReader reader = FileInputState.getReader(
                    stream,filename);
            reader.addChemObjectIOListener(new DefaultAmbitIOListener(properties));
            try {
                data.load(reader);
            } catch (IOException x) {
                throw new AmbitIOException(x);
            }
	        return true;
	  
	}
	
	public static boolean loadQSARdataset(QSARDataset data,String filename,MolProperties properties) throws AmbitException {	    try {
	    return loadQSARdataset(data,new FileInputStream(filename),filename,properties);
	    } catch (IOException x) {
	        throw new AmbitIOException(x);
	    }
	}
}
