package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.ICiteable;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.CASProcessor;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.io.ECHAPreregistrationListReader.echa_tags;

/**
 * IUCLID5  .i5d files support http://iuclid.eu/ 
 * Reads  ReferenceSubstances only. 
 * For a better support for .i5d and .i5z files use https://github.com/ideaconsult/i5
 * or compile ambit with Maven profile -P i5
 * @author nina
 *
 */

public class I5ReaderSimple   extends DefaultIteratingChemObjectReader implements IRawReader<IStructureRecord>, ICiteable {
	protected static Logger logger = Logger.getLogger(I5ReaderSimple.class.getName());
	protected IStructureRecord record;
	protected XMLStreamReader reader ;
	protected ArrayList<String> synonyms = new ArrayList<String>();
	protected String tmpValue="";	
	protected CASProcessor casProcessor = new CASProcessor();
	public static String I5_URL="http://iuclid.eu";
	public static String I5_REFERENCE="IUCLID5";
	public static Property casProperty = Property.getInstance(AmbitCONSTANTS.CASRN,
								LiteratureEntry.getInstance(I5_REFERENCE, I5_URL)); 
	public static Property ecProperty = Property.getInstance("EC",
								LiteratureEntry.getInstance(I5_REFERENCE, I5_URL));
	public static Property nameProperty = Property.getInstance(AmbitCONSTANTS.NAMES,
								LiteratureEntry.getInstance(I5_REFERENCE, I5_URL));
	protected Property registrationProperty = Property.getInstance(echa_tags.REGISTRATION_DATE.toString(),
								LiteratureEntry.getInstance(I5_REFERENCE, I5_URL));
	protected ILiteratureEntry reference;
	protected boolean attachment = false;
	protected i5_tags parentTag = null;
	
	public ILiteratureEntry getReference() {
		return reference;
	}

	public void setReference(ILiteratureEntry reference) {
		this.reference = reference;
	}

	protected enum i5_tags {
		AttachmentDocument,
		ReferenceSubstance,
		modificationHistory,
		ownershipProtection,
		name,
		ecSubstanceInventoryEntryRef,
		number,
		referenceSubstanceInformation,
		casInformation,
		casNumber,
		synonyms,
		synonym,
		iupacName,
		groupCatInfo,
		referenceSubstanceStructure,
		molecularFormula,
		smilesNotation,
		inChI,
		structureFormula,
		molecularWeightRange
		;
	} 
	
	protected enum i5_refs_attr {
		documentReferencePK,
		version
	}
    public I5ReaderSimple(InputStreamReader in) throws CDKException {
    	super();
    	record = new StructureRecord(-1,-1,null,null);
    	setReader(in);
    }	
		
    public I5ReaderSimple(InputStream in) throws CDKException {
    	this(new InputStreamReader(in));
    }	
	
    public void setReader(InputStream in) throws CDKException {
    	try {
    		XMLInputFactory factory = XMLInputFactory.newInstance();
    		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
    		reader =   factory.createXMLStreamReader(in,"UTF-8");
    		
    	} catch (Exception x) {
    		reader = null;
    		throw new CDKException(x.getMessage(),x);
    	}
    	
    }
    public void setReader(Reader in) throws CDKException {
    	try {
    		XMLInputFactory factory = XMLInputFactory.newInstance();
    		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
    		reader =   factory.createXMLStreamReader(in);
    		
    	} catch (Exception x) {
    		reader = null;
    		throw new CDKException(x.getMessage(),x);
    	}
    	
    }
	public void close() throws IOException {
		try {
			reader.close();
		} catch (XMLStreamException x) {
			throw new IOException(x.getMessage());
		}

	}

	public IResourceFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNext() {
		try {
		    while (reader.hasNext()) {
		    	int type = reader.next();
	            switch (type) {
	            case XMLStreamConstants.START_ELEMENT: {
	    			i5_tags tag = null;
	    			try {
	    				tag = i5_tags.valueOf(reader.getName().getLocalPart());
		    			tmpValue = "";
		    			switch (tag) {
		    			case AttachmentDocument: {
		    				attachment = true;
		    		    	return  false;
		    			}
		    			case ReferenceSubstance: {
		    				attachment = false;
		    				record.clear();
		    				synonyms.clear();
		    		    	record.setFormat(null);
		    		    	record.setContent(null);
		    				for (int i=0; i < reader.getAttributeCount(); i++) 
		    					try {
		    						if (i5_refs_attr.documentReferencePK.name().equals(reader.getAttributeLocalName(i))) {
		    							String value = reader.getAttributeValue(i);
		    							int slashpos = value.indexOf("/");
		    							if (slashpos>0)
		    								record.setProperty(Property.getI5UUIDInstance(),value.substring(0,slashpos));
		    							else
		    								record.setProperty(Property.getI5UUIDInstance(),value);
		    						}	
		    					} catch (Exception x) { }
		    				parentTag = i5_tags.ReferenceSubstance;		    					
		    				break;
		    			}
		    			case referenceSubstanceInformation: {
		    				parentTag = i5_tags.referenceSubstanceInformation;
		    				break;
		    			}
		    			case referenceSubstanceStructure: {
		    				parentTag = i5_tags.referenceSubstanceStructure;
		    				break;
		    			}		    			
		    			default: {
		    				attachment = false;
		    			}
		    			}            	
	    			} catch (Exception x) { }	
	            	break;
	            }
	            case XMLStreamConstants.END_DOCUMENT: {
	            	return false;
	            }
	            case XMLStreamConstants.END_ELEMENT: {
	            	if (attachment) return false;
	            	i5_tags tag = null;
	            	try {
	            		tag = i5_tags.valueOf(reader.getName().getLocalPart());
	            	} catch (Exception x) { continue;}	
	        		switch (tag) {
	        		case ReferenceSubstance: { 
	        			parentTag = null;
	        			for (int i=0;i < synonyms.size();i++)
	        				record.setProperty(
	        						Property.getInstance(AmbitCONSTANTS.NAMES,
	        						LiteratureEntry.getInstance(String.format("%s %s#%d",I5_REFERENCE,echa_tags.SYNONYM.toString(),i+1, 
	        								I5_URL),I5_URL))
	        						,synonyms.get(i));
	
	        			
	        			return true;
	        		}
	        		case name : {
	        			if ((parentTag==null) || (i5_tags.ReferenceSubstance.equals(parentTag)))
	        				record.setProperty(nameProperty,tmpValue);
	        			break;				
	        		}
	        		case iupacName : {
	        			record.setProperty(Property.getNameInstance(),tmpValue);
	        			break;				
	        		}
	        		case molecularFormula: {
	        			record.setFormula(tmpValue);
	        			break;
	        		}
	        		case number: {
	        			record.setProperty(ecProperty,tmpValue);
	        			break;
	        		}			
	        		case casNumber: {
	        			try {
	        				record.setProperty(casProperty,casProcessor.process(tmpValue));
	        			} catch (Exception x) {
	            			record.setProperty(casProperty,tmpValue);
	        			}
	        			break;
	        		}
	        		case synonym: {
	        			if (synonyms.indexOf(tmpValue)<0)
	        				synonyms.add(tmpValue);
	        			break;			
	        		}
	        		case smilesNotation: {
	        			record.setSmiles(tmpValue);
	        			break;			
	        		}	  
	        		case inChI: {
	        			String smi = record.getSmiles();
	        			record.setFormat(MOL_TYPE.INC.name());
	        			record.setContent(tmpValue);
	        			record.setInchi(null);
	        			record.setSmiles(smi);
	        			break;			
	        		}	        
	        		case referenceSubstanceInformation: {
	        			parentTag = null;
	        		}
	        		case referenceSubstanceStructure: {
	        			parentTag = null;
	        		}
	        		default: {
	        			tmpValue = null;
	        		}
	        		}
	        		break;
	            }
	            case XMLStreamConstants.CHARACTERS: {
	            	 
	            	String value = reader.getText();
	            	if ((value!=null) && (!"".equals(value))) {
	            		tmpValue = tmpValue+value.trim();
	            	}

	            	break;
	            }
	            }
		    	
		    }
		} catch (XMLStreamException x) {
			logger.log(Level.SEVERE,x.getMessage(),x);

		}
		return false;
	}
	@Override
	public Object next() {
		return record;
	}
	@Override
	public IStructureRecord nextRecord() {
		return record;
	}

	
	public void parseDocument() throws Exception {

	}
}

