package net.idea.ambit2.rest.nano;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import net.idea.ambit2.nano.NanoStructureNormalizer;
import nu.xom.Element;

import org.bitbucket.nanojava.data.Nanomaterial;
import org.bitbucket.nanojava.io.Deserializer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLList;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;

public class NanoCMLIteratingReader  extends DefaultIteratingChemObjectReader {
	protected List<Nanomaterial> nanomaterials;
	protected IStructureRecord record = new StructureRecord();
	protected int index = -1;
	
	public NanoCMLIteratingReader(InputStream in) throws Exception {
		this(in,SilentChemObjectBuilder.getInstance());

    }
	public NanoCMLIteratingReader(InputStream in,IChemObjectBuilder builder) throws Exception {
		super();
		setReader(in);

    }	
	public void setReader(InputStream in) throws CDKException {
		setReader(new InputStreamReader(in));

	}
	public void setReader(Reader reader) throws CDKException {
		try {
	        Element root = null;
	        root = new CMLBuilder().build(reader).getRootElement();
	        //quick and dirty
			nanomaterials = Deserializer.fromCML((CMLList)root);
		} catch (Exception x) {
			throw new CDKException(x.getMessage(),x);
		}
		
	}

	public IStructureRecord nextRecord() {
		return record;
	}

	public void close() throws IOException {
		
	}

	public IResourceFormat getFormat() {
		 return MDLV2000Format.getInstance();
	}

	public boolean hasNext() {
		index ++;
		if (index>=nanomaterials.size()) {
			record.clear();
			return false; 
		}
		record.clear();
		Nanomaterial nm = nanomaterials.get(index);
		record = NanoStructureNormalizer.nm2structure(nm, record);
		record.setContent(org.bitbucket.nanojava.io.Serializer.toCML(nanomaterials.get(index)).toXML());
		
		record.setFormat(MOL_TYPE.NANO.name());
		return true;
	}
	
	protected IStructureRecord createRecord() {
		if (record == null) return new StructureRecord();
		else record.clear(); return record;
	}
	public Object next() {
		return  nanomaterials.get(index);
	}
	


}
