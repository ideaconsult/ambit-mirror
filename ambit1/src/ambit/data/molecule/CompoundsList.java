/**
 * Created on 2005-3-24
 *
 */
package ambit.data.molecule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.SMILESReader;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.exceptions.AmbitException;
import ambit.exceptions.AmbitIOException;
import ambit.io.DelimitedFileFormat;
import ambit.io.DelimitedFileReader;
import ambit.io.FileOutputState;
import ambit.log.AmbitLogger;


/**
 * A List of {@link ambit.data.molecule.Compound} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class CompoundsList extends AmbitList  {
    protected AmbitLogger logger = new AmbitLogger(CompoundsList.class);
	/**
	 * 
	 */
	public CompoundsList() {
		super();
	}
	/**
	 * 
	 * @param file
	 */
	public CompoundsList(File file) {
		super();
		try {
		    openFile(file);
		    setModified(true);
		} catch (AmbitException x) {
		    logger.error(x);
		    x.printStackTrace();
		}
	}

	/**
	 * @param initialCapacity
	 */
	public CompoundsList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @param c
	 */
	public CompoundsList(Collection c) {
		super(c);
	}
	/* (non-Javadoc)
	 * @see ambit.data.AmbitList#createNewItem()
	 */
	public AmbitObject createNewItem() {
		return new Compound();
	}

	public int getRowID(int row) {
		try {
			return	((Compound) getItem(row)).getOrderInModel();
		} catch (Exception x) {
			return row;
		}
	}
	public void exportToFile(File output) throws AmbitException {
	    	String filename = output.getName();
	    	try {
				IChemObjectWriter writer = FileOutputState.getWriter(new FileOutputStream(output), filename.toLowerCase());
		    	
		    	MDLWriter mdlWriter = null;
		    	if (writer instanceof MDLWriter) {
		    		mdlWriter = (MDLWriter) writer;
		    		mdlWriter.dontWriteAromatic();
		    		writer = mdlWriter;
		    	}

	    		filename = output.toString();
	    		String ext = filename.toLowerCase();

		    	if (writer != null) {
		    		 
		    		for (int c=0; c<size();c++) {
		    			IAtomContainer ac = ((Compound) getItem(c)).getMolecule();
		    			if (ac !=null) {
		    				c++;
		    				logger.debug("Writing compound \t"+Integer.toString(c)+ "...");
		    				if (mdlWriter != null) {
		    					mdlWriter.setSdFields(ac.getProperties());
		    				}
		    				writer.write(ac);
		    			}
		    		}
					writer.close();
					logger.info(AmbitIOException.MSG_SAVESUCCESS+filename+"\tCompounds written\t"+Integer.toString(size()));
		    	} else {
		    		throw new AmbitIOException(AmbitIOException.MSG_UNSUPPORTEDFORMAT+filename);
		    	}
	    	} catch (CDKException x) {
	    		x.printStackTrace();
	    		throw new AmbitIOException(AmbitIOException.MSG_ERRORONSAVE+filename,x);    		
	    	} catch (IOException x) {
	    		throw new AmbitIOException(AmbitIOException.MSG_ERRORONSAVE+filename,x);    		    		
	    	
			} catch (Exception x) {
			    throw new AmbitIOException(AmbitIOException.MSG_ERRORONSAVE+filename,x);    					
			}
    
	}
    public void openFile(File input) throws AmbitException {
        ReaderFactory factory = new ReaderFactory();
        String filename = input.getAbsolutePath();
        try {
        if (!input.isDirectory()) {
        	IChemObjectReader reader=null;
        	logger.info("Trying to read\t"+input);
        	String fe = input.toString().toLowerCase(); 
            if (fe.endsWith(".csv")) {  
      	      reader = new DelimitedFileReader(new FileReader(input));
              logger.info("Expecting SMILES format...");
            } else if (fe.endsWith(".txt")) {  
      	      reader = new DelimitedFileReader(new FileReader(input),
      	    		  new DelimitedFileFormat('\t','"'));
              logger.info("Expecting TXT format...");              
            } else if (fe.endsWith(".smi")) {  
        	      reader = new SMILESReader(new FileReader(input));
                logger.info("Expecting SMILES format...");
            } else 
            	reader = factory.createReader(new FileReader(input));
            if (reader != null) {
                try {
                	IChemFile content = (IChemFile)reader.read((IChemObject)new org.openscience.cdk.ChemFile());
	                //SetOfMolecules content = (SetOfMolecules) reader.read(new SetOfMolecules());
	                reader.close();
	                if (content == null) {
	                	throw new AmbitIOException(AmbitIOException.MSG_EMPTYFILE+filename);
	                }
	                // apply modifications

	                addAllAtomContainers(content);
	                if (size() <= 0) {
	                	throw new AmbitIOException(AmbitIOException.MSG_EMPTYFILE+filename);
	                } if ((size() == 1) && (fe.endsWith(".mol")) && (getItem(0) != null)) {
	                	getItem(0).setName(filename);
	                }
	                logger.info(AmbitIOException.MSG_OPENSUCCESS+filename+"\tMolecules read from file\t"+size());
	                return;
                } catch (CDKException x) {
                	throw new AmbitIOException(AmbitIOException.MSG_ERRORONOPEN+x+filename);
                } catch (Exception x) {
                    throw new AmbitIOException(AmbitIOException.MSG_ERRORONOPEN+x+filename);
                }
            } else {
                throw new AmbitIOException(AmbitIOException.MSG_UNSUPPORTEDFORMAT+filename);
            }
        } else throw new AmbitIOException("Input is a directory!"+filename);
        } catch (IOException x) {
            throw new AmbitIOException(AmbitIOException.MSG_ERRORONOPEN+x+filename);
        } catch (Exception x) {
            throw new AmbitIOException(AmbitIOException.MSG_ERRORONOPEN+x+filename);
        }
    }
    public void addAllAtomContainers(IChemFile file) throws AmbitIOException {
        IChemSequence[] sequences = file.getChemSequences();
        int acCount = 0;
        clear();
        for (int i=0; i<sequences.length; i++) {
            IChemModel[] models = sequences[i].getChemModels();
            for (int j=0; j<models.length; j++) {
                addSetOfMolecules(models[j].getSetOfMolecules());
            }
        }
        return;
    }
    public void addSetOfMolecules(ISetOfMolecules molecules) {
        int n  = molecules.getAtomContainerCount();
        for (int i=0; i < n; i++) {
            Compound c = new Compound(molecules.getMolecule(i));
            c.updateMolecule();
            addItem(c);
        }
    }
    /* (non-Javadoc)
     * @see ambit.data.molecule.IMoleculesIterator#addMolecule(org.openscience.cdk.interfaces.AtomContainer)
     */
    public void addMolecule(AtomContainer mol) {
        addItem(new Compound((Molecule)mol));

    }

}
