package ambit2.some;

import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.core.io.RawIteratingReader;

/**
 * Reads SOME records 
 * @author nina
 *
 */
public class SOMERawReader extends RawIteratingReader<String> {
	
	public  enum someindex {
        atomIndex,
        atomType,
        aliphaticHydroxylation {
        	@Override
        	public String toString() {
        		return "metabolic priority for (1) Aliphatic Hydroxylation";
        	}
        },
        aromaticHydroxylation {
        	@Override
        	public String toString() {
        		return "metabolic priority for (2) Aromatic Hydroxylation";
        	}
        },        
        NDealkylation {
        	@Override
        	public String toString() {
        		return "metabolic priority for (3) N-Dealkylation";
        	}
        },   
        NOxidation {
        	@Override
        	public String toString() {
        		return "metabolic priority for (4) N-Oxidation";
        	}
        },           
        ODealkylation {
        	@Override
        	public String toString() {
        		return "metabolic priority for (5) O-Dealkylation";
        	}
        },   
        SOxidation {
        	@Override
        	public String toString() {
        		return "metabolic priority for (6) S-Oxidation";
        	}
        };

	}
	protected String firstLine = null;
	public SOMERawReader(Reader in) throws CDKException {
		super(in);
	
	}
	
	@Override
	public IResourceFormat getFormat() {
		return null;
	}

	@Override
	public boolean isEndOfRecord(String line) {
		return line.startsWith("@SOME results:");
	}

	@Override
	public String nextRecord() {
		return recordBuffer==null?null:recordBuffer.toString();
	}
	
	public boolean hasNext() {
		recordBuffer = null;
		try {
			while (input.ready()) {
				String line = input.readLine();
				
				if (isEndOfRecord(line) && (recordBuffer!=null)) {
					firstLine = line;
					return true;
				}
				
				if (recordBuffer==null) {
					recordBuffer = new StringBuffer();
					if (firstLine!= null) recordBuffer.append(firstLine);
				}
				recordBuffer.append(line);
				recordBuffer.append('\n');

			}
			//return recordBuffer!=null;
			return acceptLastRecord();
		} catch (Exception x) {
			logger.error(x);
		}
		return false;
	}	

	@Override
	protected boolean acceptLastRecord() {
		return recordBuffer!=null;
	}
	
	public static  IProcessor<IAtomContainer, IChemObjectSelection> getAtomSelector(IAtomContainer structure,someindex index) throws Exception {
		return new IProcessor<IAtomContainer, IChemObjectSelection>() {
			@Override
			public IChemObjectSelection process(IAtomContainer target)
					throws AmbitException {
				return null;
			}
    		public boolean isEnabled() {return true;}
    		public long getID() {return 0;}

    		public void setEnabled(boolean arg0) {}
    		
		};
	}

}
