package ambit2.core.io;

import java.io.Reader;

import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;

public class RawIteratingSDFReader extends RawIteratingReader<IStructureRecord> {
	public static final String delimiter = "$$$$"; 
	protected final StructureRecord r = new StructureRecord();
	public RawIteratingSDFReader(Reader in) {
		super(in);
	}

	@Override
	public boolean isEndOfRecord(String line) {
		return line.contains(delimiter);
	}
	@Override
	public IResourceFormat getFormat() {
	    return MDLV2000Format.getInstance();
	}
    @Override
    public IStructureRecord nextRecord() {
        Object o = next();
        if (o instanceof IStructureRecord) return (IStructureRecord)o;
        else return new StructureRecord(-1,-1,o.toString(),"SDF");
    }
    @Override
    public Object next() {
    	Object o = super.next();
		r.setIdchemical(-1);
		r.setIdstructure(-1);
		r.setFormat("SDF");
		r.setContent(o.toString());
		return r;    	
    }
    
}
