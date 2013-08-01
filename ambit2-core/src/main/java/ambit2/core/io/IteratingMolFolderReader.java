package ambit2.core.io;

import java.io.File;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.core.config.AmbitCONSTANTS;


public class IteratingMolFolderReader extends IteratingFolderReader<IAtomContainer,IIteratingChemObjectReader> {
	
	
	public IteratingMolFolderReader() {
		this(null);
	}
	public IteratingMolFolderReader(File[] files) {
		super(files);
	}	

	protected IIteratingChemObjectReader getItemReader(int index) throws Exception {
		return FileInputState.getReader(files[index]);		
	}
	public Object next() {
		Object o = super.next();
		if ((o!=null) && (o instanceof IChemObject)) {
			if (((IChemObject)o).getProperty(AmbitCONSTANTS.NAMES)==null) 
				((IChemObject)o).setProperty(AmbitCONSTANTS.NAMES,files[index].getName());
		}
		return o;
	}


}
