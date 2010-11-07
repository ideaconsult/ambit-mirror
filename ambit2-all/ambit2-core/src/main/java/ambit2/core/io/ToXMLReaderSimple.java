package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;

public class ToXMLReaderSimple  extends DefaultIteratingChemObjectReader implements IRawReader<IStructureRecord>  {
	protected int compounds = 0;
	protected final String URI = "http://opentox.org/toxml.owl#";
	protected int study = 0;
	protected int test = 0;
	protected Property inchiProperty = Property.getInChIInstance();
	
	protected Hashtable<String, Integer> tagCount = new Hashtable<String, Integer>();
	
	protected enum toxml_names {
		chemName,
		IUPACName {
			@Override
			public String sameas() {
				return Property.opentox_IupacName;
			}
		},
		synonym,
		trade {
			@Override
			public String sameas() {
				return Property.opentox_TradeName;
			}
		};
		public String sameas() {
			return Property.opentox_Name;
		}
	}
	protected enum toxml_attributes {
		sources,
		type
	}
	/**
	 * toxml.xsd 3.0.8
	 */
	protected enum toxml_tags_level1 {
		Compounds,
		Compound,
		Ids,
		OtherIds,
		Names,
		InChI,
		Descriptors,
		Manufacturers,
		Formulae,
		PhysicalProperties,
		OtherProperties,
		ToxicityStudies,
		KnownDrugInformation,
		ModelApplications,
		Datasets,
		TextDatasets,
		OtherRtecsInformation,
		Structure,
		AdditionalMolFiles,
		InvalidMolFiles,
		AdditionalMolecularFormulas,
		InvalidMolecularFormulas,
		Comments
	}
	
	protected enum toxml_tags {
		Default,
		Id,
		Name,
		InChI,
		Code,
		Study,
		Tests,
		Test,
		TextDatum,
		Datum,
		Source,
		Value,
		LeadscopeStudyId;
	}
	protected IStructureRecord record;
	protected XMLStreamReader reader ;
	protected String tmpValue="";	
	
	protected int recordCount = 0;

	protected Stack<String> tags = new Stack<String>();

	
	protected Stack<Property> properties = new Stack<Property>();
	protected Stack<String[]> attributes = new Stack<String[]>();
	
	protected Stack<Hashtable<String,Integer>> branches = new Stack<Hashtable<String,Integer>>();

    public ToXMLReaderSimple(InputStream in) throws CDKException {
    	record = new StructureRecord();
    	record.setFormat(MOL_TYPE.SDF.toString());
    	record.setContent("");
    	setReader(in);
    	Hashtable<String,Integer> counts = new Hashtable<String, Integer>();
    	counts.put(toxml_tags_level1.Compounds.toString(), 1);
    	branches.push(counts);

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

	public void setReader(InputStream in) throws CDKException {
    	setReader(new InputStreamReader(in));
	}
	

	public void close() throws IOException {
		try {
			reader.close();
		} catch (XMLStreamException x) {
			throw new IOException(x.getMessage());
		}

	}
	protected void processItem(String key, String value,boolean leaf) {
		
		//System.out.println(String.format("%s,%s,%s",tags.toString(),value==null?value:value.trim(),leaf));
		//System.out.println(String.format("%d %s",tags.size(),branches));
		
	}

	protected boolean newRecord(String tag,Integer tag_num,String parentTag,String path)  {

		//read attributes
		String[] attr = readAttributes();
		attributes.push(attr);
		
		//prepare property
		String parent = properties.size()==0?"":String.format("%s.",properties.peek());
		String pname = tag_num==1?String.format("%s%s",parent,tag):String.format("%s%s_%d",parent,tag,tag_num-1);
		
		String reference = attr[toxml_attributes.sources.ordinal()]==null?
				String.format("%s%s",URI,path):attr[toxml_attributes.sources.ordinal()];
		String alias = String.format("%s%s",URI,path);
		
		try { 
			toxml_tags_level1 thetag1;
			thetag1 =  toxml_tags_level1.valueOf(tag); 
			switch (thetag1) {
			case Compounds: {
				break;
			}
			case Compound: {
				study = 0; test = 0;
				compounds++;
				record.clear();
				//record.setIdchemical(compounds);
				break;
			}	
			case InChI: {
				if (toxml_tags.InChI.toString().equals(parentTag)) throw new Exception("Level 2");
				else {
					break;
				}
			}
			case ToxicityStudies: {
				study = 0;
				break;
			}
			case Ids: {
				break;
			}
			case Names: {
				break;
			}
			case OtherIds: {
				break;
			}
			default: {
				record.clearProperties();
				break;
			}
			}
			
		} catch (Exception x) { //level > 1
			toxml_tags thetag = toxml_tags.Default;
			try {thetag =  toxml_tags.valueOf(tag); } catch (Exception xx) {} 
			
			Property p;
			
			switch (thetag) {
			case Study: {
				study++;
				test = 0;
				record.clearProperties();
				break;
			}
			case InChI: {
				break;
			}
			case Code: {
				if (toxml_tags.InChI.toString().equals(parentTag))
					alias = Property.opentox_InChI;
			}
			case Tests: {
				test = 0;
				break;
			}			
			case Test: {
				test++;
				List<Property> l = new ArrayList<Property>();
				for (Property pp : record.getProperties()) {
					if (pp.getName().indexOf(thetag.toString())>0) l.add(pp) ;
				}
				for (Property pp : l)
						record.removeProperty(pp);
				break;
			}
			case Id: {
				if (attr[toxml_attributes.type.ordinal()]!=null)
					pname = tag_num==1?String.format("%s%s",parent,attr[toxml_attributes.type.ordinal()]):
						//String.format("%s%s_%d",parent,attr[toxml_attributes.type.ordinal()],tag_num-1);
						String.format("%s%s",parent,attr[toxml_attributes.type.ordinal()]);
				break;
			}			
			case Name: {
				if (attr[toxml_attributes.type.ordinal()]!=null) {
					pname = tag_num==1?String.format("%s%s",parent,attr[toxml_attributes.type.ordinal()]):
						//String.format("%s%s_%d",parent,attr[toxml_attributes.type.ordinal()],tag_num-1);
						String.format("%s%s",parent,attr[toxml_attributes.type.ordinal()]);
				}	
				try {
					alias = toxml_names.valueOf(attr[toxml_attributes.type.ordinal()]).sameas();
				} catch (Exception xx) { }

					
				break;
			}			

			default: {

				
				break;
			}
			}
			
			p = new Property(pname,
					new LiteratureEntry(reference,"ToXML"));
			p.setLabel(alias);			
			properties.push(p);
		}
		
		return false;
	}
	

	protected boolean endRecord(String tag,Integer tag_num,String parentTag, String path, String value) {
		boolean newRecord = false;
		try { 
			toxml_tags_level1 thetag1;
			thetag1 =  toxml_tags_level1.valueOf(tag); 
			switch (thetag1) {
			case	Compounds: return false;
			case	Compound: return false;
			case Ids: return false; //will read all ids after InChI
			case Names: return false;
			case OtherIds: return false;
			case InChI: {
				if (toxml_tags.InChI.toString().equals(parentTag)) throw new Exception("Level 2");
				else return true;
			}
			case ToxicityStudies: return study==0; //if > 0  was already written
			default: {

				newRecord =true;
				break;
			}
			}
			
		} catch (Exception x) { //level > 1
			newRecord = false;
			toxml_tags thetag = toxml_tags.Default;
			try {thetag =  toxml_tags.valueOf(tag);} catch (Exception xx) {} 
			Property key = properties.pop();
			switch (thetag) {
			case Study: {
				newRecord = (test==0);  //if there are tests, these were already written
				break;
			}
			case Tests: {
				record.clearProperties();
				break;
			}
			case Test: {
				newRecord = true;
				break;
			}			
			default: {
				break;
			}
			}
			
			if ((value!=null) && !"".equals(value.trim())) {
				if (toxml_tags.InChI.toString().equals(parentTag)) { 
					record.setInchi(String.format("InChI=%s", value.trim()));
					setProperty(key,record.getInchi());
				} else 	setProperty(key,value.trim());
			}
	
		}

		if (newRecord) {
			record.setProperty(inchiProperty,record.getInchi());
		}
		return newRecord;
	
	}	
	
	protected void setProperty(Property key, Object value) {
		Object anotherValue = record.getProperty(key);
		String oldName = key.getName();
		int count = 1;
		while (anotherValue != null) {
			key.setName(String.format("%s-%d", oldName,count));
			anotherValue = record.getProperty(key);
			count++;
		}
		record.setProperty(key, value);
	}

	protected String getDotPath() {
    	String dotPath = tags.toString().replace(",", ".");
    	dotPath = dotPath.replace(" ", "");
    	dotPath = dotPath.replace("[", "");
    	dotPath = dotPath.replace("]", "");
    	dotPath = dotPath.replace("Compounds.", "");
    	dotPath = dotPath.replace("Compound.", "");
    	dotPath = dotPath.replace("ToxicityStudies.", "");
    	return dotPath;
	}
	@Override
	public boolean hasNext() {
		
		try {
		    while (reader.hasNext()) {
		    	int type = reader.next();
	            switch (type) {
	            case XMLStreamConstants.START_ELEMENT: {
	            	
	            	String tag = reader.getName().getLocalPart();
	            	Hashtable<String, Integer> counts = branches.peek();
	            	
	            	Integer tag_num = counts.get(tag);
	            	if (tag_num==null) tag_num=1;
	            	else tag_num++; 
	            	counts.put(tag,tag_num);

	            	String parentTag = tags.size()==0?null:tags.peek();
	            	
	            	//stats
	            	tags.push(tag);
	            	branches.push(new Hashtable<String,Integer>());
	            	
	            	if (newRecord(tag,tag_num,parentTag,getDotPath())) return true;
	            	break;
	    			
	            }
	            case XMLStreamConstants.END_DOCUMENT: {
	            	break;
	            }
	            case XMLStreamConstants.END_ELEMENT: {
	            	
	            	processItem(tags.peek(),tmpValue,true);
	            	
	            	String tag = reader.getName().getLocalPart();
	            	Hashtable<String, Integer> counts = branches.pop();
	            	
	            	counts = branches.peek();
	            	Integer tag_num = counts.get(tag) ;
	            	
	            	tags.pop();
	            	
	            	if (endRecord(tag,tag_num,tags.size()==0?null:tags.peek(),getDotPath(),tmpValue)) {
	            		tmpValue = null;
	            		return true;
	            	} else {
	            		tmpValue = null;

	            		break;
	            	}
	        				
	            }
	            case XMLStreamConstants.CHARACTERS: {
	            	 
	            	String value = reader.getText();
	            	if ((value!=null) && (!"".equals(value))) {
	            		tmpValue = tmpValue==null?value:String.format("%s%s",tmpValue,value.trim());
	            	}
	            	break;
	            }
	            }
		    	
		    }
		    return false;
		} catch (Exception x) {
			x.printStackTrace();
			return false;
		}
	}

	protected String[] readAttributes() {
		String[] attr = new String[toxml_attributes.values().length];
		for (int i=0; i < attr.length;i++) attr[i] = null;
		
		for (int i=0; i < reader.getAttributeCount(); i++) 
			try {
				toxml_attributes a = toxml_attributes.valueOf(reader.getAttributeLocalName(i));
				attr[a.ordinal()] = reader.getAttributeValue(i);

			} catch (Exception x) { }

		return attr;
	}
	@Override
	public IStructureRecord nextRecord() {
		return record;
	}



	@Override
	public IResourceFormat getFormat() {
		return null;
	}




	@Override
	public Object next() {
		return record;
	}

}
