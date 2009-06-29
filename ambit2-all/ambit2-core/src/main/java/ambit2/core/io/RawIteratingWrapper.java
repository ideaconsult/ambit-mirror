package ambit2.core.io;

import java.io.IOException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeWriter;

public class RawIteratingWrapper<R extends IIteratingChemObjectReader> implements IRawReader<IStructureRecord>{
	protected R reader;
	protected MoleculeWriter writer ;
	protected LiteratureEntry reference;	
	protected final StructureRecord r = new StructureRecord();
	
	public RawIteratingWrapper(R reader) {
		this.reader = reader;
		writer = new MoleculeWriter();
	}
	

	public LiteratureEntry getReference() {
		return reference;
	}
	public void setReference(LiteratureEntry reference) {
		this.reference = reference;
	}	
	public IStructureRecord nextRecord() {
        Object o = next();
        if (o instanceof IStructureRecord) return (IStructureRecord)o;
        else try {
        	StructureRecord r = new StructureRecord(-1,-1,writer.process((IAtomContainer)o),"SDF");
        	r.setReference(reference);
        	return r;
        } catch (Exception x) {
        	StructureRecord r = new StructureRecord(-1,-1,null,"SDF");
        	r.setReference(reference);
        	return r;
        }
	}

	public boolean accepts(Class arg0) {
		return reader.accepts(arg0);
	}

	public void addChemObjectIOListener(IChemObjectIOListener arg0) {
		reader.addChemObjectIOListener(arg0);
		
	}

	public void close() throws IOException {
		reader.close();
		
	}

	public IResourceFormat getFormat() {
		return reader.getFormat();
	}

	public IOSetting[] getIOSettings() {
		return reader.getIOSettings();
	}

	public void removeChemObjectIOListener(IChemObjectIOListener arg0) {
		reader.removeChemObjectIOListener(arg0);
		
	}

	public boolean hasNext() {
		return reader.hasNext();
	}

	public Object next() {
		Object o = reader.next();
		if (o instanceof IAtomContainer) try {
			
			r.setIdchemical(-1);
			r.setIdstructure(-1);
			r.setFormat("SDF");
			r.setContent(writer.process((IAtomContainer)o));
			r.setReference(getReference());
			return r;  
		} catch (Exception x) {
			r.setIdchemical(-1);
			r.setIdstructure(-1);
			r.setFormat("SDF");
			r.setContent(null);
			r.setReference(getReference());
			return r;  
		} else return o;
	}

	public void remove() {
		reader.remove();
		
	}
	
}
