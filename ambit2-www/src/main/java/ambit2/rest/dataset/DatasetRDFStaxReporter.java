package ambit2.rest.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.QueryStaXReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.OT.DataProperty;
import ambit2.rest.rdf.OT.OTClass;
import ambit2.rest.rdf.OT.OTProperty;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
<pre>
<rdf:RDF
    xmlns:ac="http://apps.ideaconsult.net:8080/ambit2/compound/"
    xmlns:ot="http://www.opentox.org/api/1.1#"
    xmlns:bx="http://purl.org/net/nknouf/ns/bibtex#"
    xmlns:otee="http://www.opentox.org/echaEndpoints.owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:ar="http://apps.ideaconsult.net:8080/ambit2/reference/"
    xmlns="http://apps.ideaconsult.net:8080/ambit2/"
    xmlns:am="http://apps.ideaconsult.net:8080/ambit2/model/"
    xmlns:af="http://apps.ideaconsult.net:8080/ambit2/feature/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:ad="http://apps.ideaconsult.net:8080/ambit2/dataset/"
    xmlns:ag="http://apps.ideaconsult.net:8080/ambit2/algorithm/"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:ota="http://www.opentox.org/algorithmTypes.owl#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
  xml:base="http://apps.ideaconsult.net:8080/ambit2/">
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Dataset"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Compound"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Feature"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#FeatureValue"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#DataEntry"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#dataEntry"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#compound"/>
  <ot:Dataset rdf:about="dataset/112">
    <ot:dataEntry>
      <ot:DataEntry>
        <ot:compound>
          <ot:Compound rdf:about="compound/147678/conformer/419677"/>
        </ot:compound>
      </ot:DataEntry>
    </ot:dataEntry>
  </ot:Dataset>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/description"/>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/type"/>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/title"/>
</rdf:RDF>
</pre>
 * @author nina
 *
 * @param <Q>
 */
public class DatasetRDFStaxReporter <Q extends IQueryRetrieval<IStructureRecord>> extends QueryStaXReporter<IStructureRecord,Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1676895914680469360L;

	protected CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter;
	protected PropertyURIReporter propertyReporter;
	protected Comparator<Property> comp;
	


	
	protected Profile groupProperties;
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile gp) {
		this.groupProperties = gp;
	}
	protected Template template;
	protected List<Property> header = null;
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	public DatasetRDFStaxReporter(Request request,ResourceDoc doc,Template template,Profile groupedProperties) {
		super(request,doc);
		setGroupProperties(groupedProperties);
		setTemplate(template==null?new Template(null):template);
		initProcessors();
		propertyReporter = new PropertyURIReporter(request,doc);

		comp = new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getId()-o2.getId();
			}
		};
				
		
	}
	@Override
	protected QueryURIReporter<IStructureRecord, IQueryRetrieval<IStructureRecord>> createURIReporter(
			Request req,ResourceDoc doc) {
		compoundReporter = new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(req,doc);
		return new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(req,doc);
	}

	protected void initProcessors() {
		
		getProcessors().clear();
		if ((getGroupProperties()!=null) && (getGroupProperties().size()>0)) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveGroupedValuesByAlias)getQuery()).setRecord(target);
					return super.process(target);
				}
			});		
		
		if ((getTemplate()!=null) && (getTemplate().size()>0)) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate(),true)) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
		
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				try {
					processItem(target);
				} catch (Exception x) {
					x.printStackTrace();
				}
				return target;
			};
		});			
	}	
	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		
		if (header == null) 
			header = template2Header(template,true);
		try {

			getOutput().writeStartElement(OT.NS,"dataEntry"); //property
			getOutput().writeStartElement(OT.NS,"DataEntry");  //object
			
			getOutput().writeStartElement(OT.NS,"compound"); //property
			getOutput().writeStartElement(OT.NS,"Compound"); //property
			
			String uri = item.getType().equals(STRUC_TYPE.NA)?
					compoundReporter.getURI(item):uriReporter.getURI(item);
					
			getOutput().writeAttribute(RDF.getURI(),"about",uri);
			getOutput().writeEndElement();
			getOutput().writeEndElement();

			if (header != null)
			for (ambit2.base.data.Property p : header) {
				Object value = item.getProperty(p);
				if (value == null) continue;
				try {
					getOutput().writeStartElement(OT.NS,"values"); //property
					getOutput().writeStartElement(OT.NS,"FeatureValue"); //property
					
					getOutput().writeStartElement(OT.NS,"feature"); //feature
					getOutput().writeAttribute(RDF.getURI(),"resource",propertyReporter.getURI(p));
					getOutput().writeEndElement(); //feature
					
					getOutput().writeStartElement(OT.NS,"value"); //value
					if (value instanceof Number) {
						getOutput().writeAttribute(RDF.getURI(),"datatype","http://www.w3.org/2001/XMLSchema#double");
					} else {
						getOutput().writeAttribute(RDF.getURI(),"datatype","http://www.w3.org/2001/XMLSchema#string");
					}
					getOutput().writeCharacters(value.toString());
					getOutput().writeEndElement(); //value					
					
					if (p.isNominal() && (value instanceof Comparable)) 
						p.addAllowedValue((Comparable)value);
				} catch (Exception x) {

				} finally {

					
					getOutput().writeEndElement(); //FeatureValue
					getOutput().writeEndElement();//values
					
				}
	
				//if (p.isNominal())
				//	feature.addProperty(OTProperty.acceptValue.createProperty(getJenaModel()), value.toString());
					
			}



			return item;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try { getOutput().writeEndElement(); } catch (Exception x) {}
			try { getOutput().writeEndElement(); } catch (Exception x) {}
		}
	

	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	
	public void header(javax.xml.stream.XMLStreamWriter writer, Q query) {
		super.header(writer,query);
		try {
			writeClassTriple(writer, OTClass.Dataset);
			writeClassTriple(writer, OTClass.DataEntry);
			writeClassTriple(writer, OTClass.Feature);
			writeClassTriple(writer, OTClass.FeatureValue);
			writeClassTriple(writer, OTClass.Compound);
			
			writeObjectPropertyTriple(writer, OTProperty.compound);
			writeObjectPropertyTriple(writer, OTProperty.dataEntry);
			writeObjectPropertyTriple(writer, OTProperty.values);
			writeObjectPropertyTriple(writer, OTProperty.feature);
			writeObjectPropertyTriple(writer, OTProperty.hasSource);
			writeObjectPropertyTriple(writer, OTProperty.acceptValue);
			
			writeDataPropertyTriple(writer, DataProperty.units);
			writeDataPropertyTriple(writer, DataProperty.value);
			
			writeAnnotationPropertyTriple(writer,"http://purl.org/dc/elements/1.1/description");
			writeAnnotationPropertyTriple(writer,"http://purl.org/dc/elements/1.1/creator");
			writeAnnotationPropertyTriple(writer,"http://purl.org/dc/elements/1.1/type");
			writeAnnotationPropertyTriple(writer,"http://purl.org/dc/elements/1.1/title");
			
			String datasetUri = 
					String.format("%s:%s",uriReporter.getRequest().getOriginalRef().getScheme(),
							uriReporter.getRequest().getOriginalRef().getHierarchicalPart());
			writer.writeStartElement(OT.NS,"Dataset");
			writer.writeAttribute(RDF.getURI(),"about",datasetUri);

		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	
	public void footer(javax.xml.stream.XMLStreamWriter writer, Q query) {
		
		try {
			writer.writeEndElement();
		} catch (Exception x) {
			x.printStackTrace();
		}
	
			//write properties
		if (header!=null)
		for (ambit2.base.data.Property p : header) try {

				getOutput().writeStartElement(OT.NS,"Feature"); //feature
				getOutput().writeAttribute(RDF.getURI(),"about",propertyReporter.getURI(p));
				
				if (p.isNominal()) {
					//NominalFeature
					getOutput().writeStartElement(RDF.getURI(),"type"); //feature
					getOutput().writeAttribute(RDF.getURI(),"resource","http://www.opentox.org/api/1.1#NominalFeature");
					getOutput().writeEndElement();
					
					if (p.getAllowedValues()!=null)
						for (Comparable value : p.getAllowedValues()) {
							getOutput().writeStartElement(OT.NS,OTProperty.acceptValue.toString());
							getOutput().writeCharacters(value.toString()); //TODO make use of data type
							getOutput().writeEndElement();
						}
				}
				
				boolean numeric = (p.getClazz()==Number.class) || (p.getClazz()==Double.class)
								|| (p.getClazz()==Float.class) || (p.getClazz()==Integer.class)
								|| (p.getClazz()==Long.class);
				if (numeric) {
					//NominalFeature
					getOutput().writeStartElement(RDF.getURI(),"type"); //feature
					getOutput().writeAttribute(RDF.getURI(),"resource","http://www.opentox.org/api/1.1#NumericFeature");
					getOutput().writeEndElement();
				}
				
				if (p.getClazz() ==Dictionary.class ) {
					//TupleFeature
					getOutput().writeStartElement(RDF.getURI(),"type"); //feature
					getOutput().writeAttribute(RDF.getURI(),"resource","http://www.opentox.org/api/1.1#TupleFeature");
					getOutput().writeEndElement();					
				}
		
				getOutput().writeStartElement(DC.getURI(),"creator"); //feature
				getOutput().writeCharacters(p.getReference().getURL());	
				getOutput().writeEndElement();
				
				
				writeHasSource(p);
				
				String uri = p.getLabel();
				if(uri==null) uri  = Property.guessLabel(p.getName());
				if ((uri!=null) && (uri.indexOf("http://")<0)) {
					uri = String.format("%s%s",OT.NS,Reference.encode(uri));
				}
				

				getOutput().writeStartElement(OWL.getURI(),"sameAs"); //feature
				getOutput().writeAttribute(RDF.getURI(), "resource",uri);
				getOutput().writeEndElement();						
				
				if (p.getUnits()!=null) {
					getOutput().writeStartElement(OT.NS,"units"); //feature
					getOutput().writeCharacters(p.getUnits());	
					getOutput().writeEndElement();
				}
				
				getOutput().writeStartElement(DC.getURI(),"title"); //feature
				getOutput().writeCharacters(p.getName());
				getOutput().writeEndElement();	
			} 
			catch (Exception x) {}
			finally {
				try {getOutput().writeEndElement(); } catch (Exception x) {}
			}
			
			super.footer(writer, query);
	};
	/**
<pre>
    <ot:hasSource>
      <ot:Algorithm rdf:about="algorithm/ambit2.descriptors.PKASmartsDescriptor"/>
    </ot:hasSource>
</pre>
	 * @param item
	 * @return
	 * @throws Exception
	 */
	protected String writeHasSource(Property item) throws Exception {
		String otclass = null;
		String namespace = null;
		String uri = item.getTitle();
		
		if (uri.indexOf("http://")<0) {
			String source  = null;
			if (_type.Algorithm.equals(item.getReference().getType())) {
				otclass = OTClass.Algorithm.toString();
				namespace = OT.NS;
				uri = String.format("%s/algorithm/%s",uriReporter.getBaseReference(),Reference.encode(uri));
			} else if (_type.Model.equals(item.getReference().getType())) {
				otclass = OTClass.Model.toString();
				namespace = OT.NS;
				uri = String.format("%s/model/%s",uriReporter.getBaseReference(),Reference.encode(uri));
			} else if (_type.Dataset.equals(item.getReference().getType())) {
				//otclass = "Class";
				//namespace = OWL.getURI();
				//otclass = OTClass.Dataset;
				//should be as above , but this seems to confuse everybody's else parsers ...
				//uri = String.format("%s/dataset/%s",uriReporter.getBaseReference(),Reference.encode(uri));
			}
			
		} else  {
			
		}
		
		getOutput().writeStartElement(OT.NS,"hasSource"); //feature
		
		if (otclass==null) {
			
			getOutput().writeCharacters(uri); //TODO this is wrong, should be a resource 


		} else {
			getOutput().writeStartElement(namespace,otclass); //algorithm or model
			getOutput().writeAttribute(RDF.getURI(),"about",uri);
			getOutput().writeEndElement();
		}

		
		getOutput().writeEndElement();						
		return uri;
	}
	
	protected List<Property> template2Header(Template template, boolean propertiesOnly) {
		List<Property> h = new ArrayList<Property>();
		Iterator<Property> it;
		if (groupProperties!=null) {
			it = groupProperties.getProperties(true);
			while (it.hasNext()) {
				Property t = it.next();
				h.add(t);
			}
		}			
		
		it = template.getProperties(true);
		while (it.hasNext()) {
			Property t = it.next();
			if (!propertiesOnly || (propertiesOnly && (t.getId()>0)))
				h.add(t);
		}
		/*
		Collections.sort(h,new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getOrder()-o2.getOrder();
			}
		});	
		*/
		Collections.sort(h,comp);
		return h;
	}
	protected ambit2.db.processors.AbstractBatchProcessor<ambit2.db.readers.IQueryRetrieval<IStructureRecord>,IStructureRecord> createBatch(Q query) {
		if (query.isPrescreen()) {
			DbReader<IStructureRecord> reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else	
			return super.createBatch(query);
	};
}
