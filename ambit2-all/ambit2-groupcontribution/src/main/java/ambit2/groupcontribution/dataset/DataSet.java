package ambit2.groupcontribution.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.io.FileInputState;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.smarts.SmartsHelper;

public class DataSet 
{
	public List<DataSetObject> dataObjects = new ArrayList<DataSetObject>();
	public int nErrors = 0;
	File datafile = null;
	
	public DataSet(){		
	}
	
	
	public DataSet(File f) throws Exception
	{
		datafile = f;
		loadDataFromFile(datafile);		
	}
	
	public void loadDataFromFile(File file) throws Exception
	{
		int records_read = 0;
		int records_processed = 0;
		int records_error = 0;
		
		if (!file.exists()) 
			throw new FileNotFoundException(file.getAbsolutePath());
		
		InputStream in = new FileInputStream(file);
		IIteratingChemObjectReader<IAtomContainer> reader = null;
		try 
		{
			reader = getReader(in,file.getName());
			while (reader.hasNext()) 
			{
				IAtomContainer molecule  = reader.next();
				records_read++;
				
				if (molecule==null) {
					records_error++;
					DataSetObject dso = new DataSetObject();
					dso.molecule = null;
					dso.error = "Unable to read chemical object";
					dataObjects.add(dso);
					continue;
				}
				
				if (molecule.getAtomCount() == 0)
				{
					records_error++;
					DataSetObject dso = new DataSetObject();
					dso.molecule = null;
					dso.error = "Empty chemical object. Problem on object creation.";
					dataObjects.add(dso);
					continue;
				}
				
				try
				{
					processMolecule(molecule);
					DataSetObject dso = new DataSetObject();
					dso.molecule = molecule;
					dataObjects.add(dso);
					records_processed++;
				}
				catch (Exception x)
				{}
			}
			
		}
		catch (Exception x1) {
			//logger.log(Level.SEVERE, String.format("[Record %d] Error %s\n", records_read, file.getAbsoluteFile()), x1);
		} 
		finally {
			try { reader.close(); } catch (Exception x) {}
		}
		
		nErrors = records_error;
	}
	
	static public IIteratingChemObjectReader<IAtomContainer> getReader(InputStream in, String extension) throws CDKException, AmbitIOException {
		FileInputState instate = new FileInputState();
		IIteratingChemObjectReader<IAtomContainer> reader ;
		if (extension.endsWith(FileInputState._FILE_TYPE.SDF_INDEX.getExtension())) {
			reader = new InteractiveIteratingMDLReader(in,SilentChemObjectBuilder.getInstance());
			((InteractiveIteratingMDLReader) reader).setSkip(true);
		} else reader = instate.getReader(in,extension);
		
		reader.setReaderMode(Mode.RELAXED);
		reader.setErrorHandler(new IChemObjectReaderErrorHandler() {
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd,
					Exception exception) {
				exception.printStackTrace();
			}
			
			@Override
			public void handleError(String message, int row, int colStart, int colEnd) {
				System.out.println(message);
			}
			
			@Override
			public void handleError(String message, Exception exception) {
				exception.printStackTrace();				
			}
			
			@Override
			public void handleError(String message) {
				System.out.println(message);
			}
		});
		return reader;
	}
	
	
	void processMolecule(IAtomContainer molecule) throws Exception
	{
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		CDKHueckelAromaticityDetector.detectAromaticity(molecule);
	}
	
	public static DataSet makeDataSet(List<String> smilesList, 
			List<Double> propertyValues, String propertyName) throws Exception
	{
		DataSet ds = new DataSet();
		
		for (int i = 0; i < smilesList.size(); i++)
		{		
			IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smilesList.get(i));
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
			DataSetObject dso = new DataSetObject();
			dso.molecule = mol;
			if (propertyValues != null)
				dso.molecule.setProperty(propertyName, propertyValues.get(i));
			ds.dataObjects.add(dso);
		}
		
		return ds;
	}
	
	public String reportErrorsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dataObjects.size(); i++)
		{
			DataSetObject dso = dataObjects.get(i);
			if (dso.error == null)
				continue;
			
			sb.append("#");
			sb.append(i+1);
			sb.append("  ");
			if (dso.molNotation != null)
				sb.append (dso.molNotation);
			else
			{
				try {
					String smiles = SmartsHelper.moleculeToSMILES(dso.molecule, true);
					sb.append(smiles);
				}
				catch (Exception e) {}
			}
			sb.append("  ");
			sb.append(dso.error);
			sb.append("\n");
		}
		
		return sb.toString();
		
	}
}
