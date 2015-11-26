package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.ICiteable;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeWriter;

public class RawIteratingWrapper<R extends IIteratingChemObjectReader> implements IRawReader<IStructureRecord>, ICiteable{
	protected R reader;
	protected MoleculeWriter writer ;
	protected ILiteratureEntry reference;	
	protected IStructureRecord r;
   // protected IChemObjectReader.Mode mode = IChemObjectReader.Mode.RELAXED;
   // protected IChemObjectReaderErrorHandler errorHandler = null;
	
	public RawIteratingWrapper(R reader) {
		this.reader = reader;
		writer = new MoleculeWriter();
		r = createStructureRecord();
	}
	
	protected IStructureRecord createStructureRecord() {
		return new StructureRecord();
	}
	

	public ILiteratureEntry getReference() {
		return reference;
	}
	public void setReference(ILiteratureEntry reference) {
		this.reference = reference;
	}	
	public void setReader(InputStream reader) throws CDKException {
		throw new CDKException("Not implemented");
	}
	public void setReader(Reader reader) throws CDKException {
		throw new CDKException("Not implemented");
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

	protected Object transform(Object o) {
		if (o instanceof IAtomContainer) try {
			r.clear();
			r.setFormat("SDF");
			r.setContent(writer.process((IAtomContainer)o));
			Object ref = ((IAtomContainer)o).getProperty("REFERENCE");
			if (ref instanceof LiteratureEntry)
				r.setReference((LiteratureEntry)ref);
			else r.setReference(getReference());
			return r;  
		} catch (Exception x) {
			r.clear();
			r.setFormat("SDF");
			r.setContent(null);
			r.setReference(getReference());
			return r;  
		} else return o;
	}
	public Object next() {
		Object o = reader.next();
		return transform(o);
	}

	public void remove() {
		reader.remove();
		
	}
	

	   public void setReaderMode(ISimpleChemObjectReader.Mode mode) {
	    	reader.setReaderMode(mode);
	    }

	    /** {@inheritDoc} */
	    public void setErrorHandler(IChemObjectReaderErrorHandler handler) {
	        reader.setErrorHandler(handler);
	    }

	    /** {@inheritDoc} */
	    public void handleError(String message) throws CDKException {
	        reader.handleError(message);
	    }

	    /** {@inheritDoc} */
	    public void handleError(String message, Exception exception)
	    throws CDKException {
	        reader.handleError(message, exception);
	    }

	    /** {@inheritDoc} */
	    public void handleError(String message, int row, int colStart, int colEnd) throws CDKException {
	        reader.handleError(message, row, colStart, colEnd);
	    }

	    /** {@inheritDoc} */
	    public void handleError(String message, int row, int colStart, int colEnd, Exception exception)
	    throws CDKException {
	        reader.handleError(message, row, colStart, colEnd, exception);
	    }


		@Override
		public Collection<IChemObjectIOListener> getListeners() {
			return reader.getListeners();
		}

		@Override
		public <S extends IOSetting> S addSetting(IOSetting setting) {
			return reader.addSetting(setting);
		}

		@Override
		public void addSettings(Collection<IOSetting> settings) {
			reader.addSettings(settings);
		}

		@Override
		public boolean hasSetting(String name) {
			return reader.hasSetting(name);
		}

		@Override
		public <S extends IOSetting> S getSetting(String name) {
			return reader.getSetting(name);
		}

		@Override
		public <S extends IOSetting> S getSetting(String name, Class<S> c) {
			return reader.getSetting(name, c);
		}

		@Override
		public Collection<IOSetting> getSettings() {
			return reader.getSettings();
		}
	
}
