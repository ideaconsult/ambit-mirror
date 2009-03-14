package ambit2.core.io;

import java.io.File;
import java.io.FileInputStream;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;


public class IteratingMolFolderReader extends IteratingFolderReader<IAtomContainer,IIteratingChemObjectReader> {
	
	
	public IteratingMolFolderReader() {
		this(null);
	}
	public IteratingMolFolderReader(File[] files) {
		super(files);
	}	

	protected IIteratingChemObjectReader getItemReader(int index) throws Exception {
		return FileInputState.getReader(new FileInputStream(files[index]), files[index].getName());		
	}
	public Object next() {
		Object o = super.next();
		if ((o!=null) && (o instanceof IChemObject)) {
			if (((IChemObject)o).getProperty(CDKConstants.NAMES)==null) 
				((IChemObject)o).setProperty(CDKConstants.NAMES,files[index].getName());
		}
		return o;
	}


}
