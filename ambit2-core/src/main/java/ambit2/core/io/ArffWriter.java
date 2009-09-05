package ambit2.core.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;

public class ArffWriter extends FilesWithHeaderWriter {
	protected BufferedWriter writer;
    public ArffWriter(Writer out) {
        try {
            writer = new BufferedWriter(out);
        } catch (Exception exc) {
        }
    }
	@Override
	protected void writeHeader() throws IOException {
		/*
		@relation steroids_10mols_AlogP2

		@attribute MolName {aldosterone,androstanediol,19-nortestosterone,epicorticosterone,cortisolacetat,prednisolone,testosterone,17a-hydroxyprogesterone,progesterone,pregnenolone,etiocholanolone}
		@attribute MW numeric
		@attribute naAromAtom numeric
		@attribute topoShape numeric
		@attribute nHBDon numeric
		@attribute nHBAcc numeric
		@attribute Alogp2 numeric

		@data
		*/
		writer.write(String.format("@relation %s\n\n", "Dataset"));
		for (int i=0; i < getHeader().size(); i++) {
			Object o = getHeader().get(i);
			writer.write(String.format("@attribute %s numeric\n", o.toString()));
		}
		writer.write("\n@data\n");
	}
	public void setWriter(Writer arg0) throws CDKException {
        try {
            writer = new BufferedWriter(arg0);
        } catch (Exception x) {
        	throw new CDKException(x.getMessage());
        }
		
	}
	public void setWriter(OutputStream out) throws CDKException {
		setWriter(new OutputStreamWriter(out));
        
	}
	public void write(IChemObject mol) throws CDKException {
		try {
			for (int i=0; i < getHeader().size(); i++) {
				Object o = getHeader().get(i);
				if (i>0) writer.write(',');
				Object p = mol.getProperty(o);
				writer.write(p==null?"?":p.toString());
			}
			writer.write('\n');
		} catch (IOException x) {
			throw new CDKException(x.getMessage());
		}
	}
	public boolean accepts(Class arg0) {
		return false;
	}
	public void close() throws IOException {
		writer.close();
		
	}
	public IResourceFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

}
