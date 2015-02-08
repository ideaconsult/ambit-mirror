package ambit2.rest.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.rdf.ns.OT;
import net.idea.restnet.rdf.ns.OT.DataProperty;
import net.idea.restnet.rdf.ns.OT.OTClass;
import net.idea.restnet.rdf.ns.OT.OTProperty;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.rest.OpenTox;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class DatasetRDFWriter extends AbstractStaxRDFWriter<IStructureRecord, IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1825074197173628894L;
	protected String datasetIndividual = null;
	protected List<Property> header = null;
	protected PropertyURIReporter propertyReporter;
	protected CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter;
	protected Comparator<Property> comp;
	protected Profile groupProperties;
	protected String licenseURI = null;

	public String getLicenseURI() {
		return licenseURI;
	}
	public void setLicenseURI(String licenseURI) {
		this.licenseURI = licenseURI;
	}
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile gp) {
		this.groupProperties = gp;
	}
	protected Template template;
	
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	public CompoundURIReporter<IQueryRetrieval<IStructureRecord>> getCompoundReporter() {
		return compoundReporter;
	}
	public void setCompoundReporter(
			CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter) {
		this.compoundReporter = compoundReporter;
	}
	
	public PropertyURIReporter getPropertyReporter() {
		return propertyReporter;
	}
	public void setPropertyReporter(PropertyURIReporter propertyReporter) {
		this.propertyReporter = propertyReporter;
	}
	public DatasetRDFWriter(Reference baseReference,ResourceDoc doc) {
		this(new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(baseReference),
			new PropertyURIReporter(baseReference),
			new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(baseReference)
		);
	}
	public DatasetRDFWriter(Request request,ResourceDoc doc) {
		this(new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(request),new PropertyURIReporter(request));
	}
	public DatasetRDFWriter(CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter,
			PropertyURIReporter propertyReporter) {
		this(compoundReporter,propertyReporter,null);
	}
	public DatasetRDFWriter(CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter,
			PropertyURIReporter propertyReporter,
			QueryURIReporter<IStructureRecord, IQueryRetrieval<IStructureRecord>> uriReporter) {
		super();
		setCompoundReporter(compoundReporter);
		setPropertyReporter(propertyReporter);
		setUriReporter(uriReporter);
		datasetIndividual = null;

		comp = new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getId()-o2.getId();
			}
		};
			
	}
	public String getDatasetIndividual() {
		return datasetIndividual;
	}

	public void setDatasetIndividual(String datasetIndividual) {
		this.datasetIndividual = datasetIndividual;
	}


	@Override
	public IStructureRecord process(IStructureRecord item)
			throws AmbitException {
		if (datasetIndividual == null) try {
			getOutput().writeStartElement(OT.NS,"Dataset");
			datasetIndividual = createDatasetURI(item.getDatasetID());
			if (datasetIndividual!= null) getOutput().writeAttribute(RDF.getURI(),"about",datasetIndividual);
			else {
				if (uriReporter.getResourceRef()==null)
					datasetIndividual = "-";
				else
				datasetIndividual = String.format("%s:%s",uriReporter.getResourceRef().getScheme(),
						uriReporter.getResourceRef().getHierarchicalPart());
			}
			if (getLicenseURI()!=null) {
				getOutput().writeStartElement("dcterms","license",DCTerms.NS); //value
			    getOutput().writeAttribute("rdf", RDF.getURI(),  "resource", getLicenseURI());
			    getOutput().writeEndElement();
			}
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}

	
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
					
					String featureURI = propertyReporter.getURI(p);
					if (p.getId()>0)
						getOutput().writeAttribute(RDF.getURI(),"resource",featureURI);
					else {
						/**
              <ot:Feature>
                <dc:creator>http://ambit.sourceforge.net</dc:creator>
                <ot:hasSource>Default</ot:hasSource>
                <owl:sameAs rdf:resource="http://www.opentox.org/api/1.1#IUPACName"/>
                <ot:units></ot:units>
                <dc:title>http://www.opentox.org/api/1.1#IUPACName</dc:title>
              </ot:Feature>
						 */
						getOutput().writeStartElement(OT.NS,"Feature");
						
						getOutput().writeStartElement(DC.NS,"title"); //value
						getOutput().writeCharacters(p.getName());
						getOutput().writeEndElement();
						
						getOutput().writeStartElement(OWL.NS,"sameAs"); //value
						getOutput().writeAttribute(RDF.getURI(),"resource",p.getLabel());
						getOutput().writeEndElement();
						
						getOutput().writeEndElement();
					}
					
					getOutput().writeEndElement(); //feature
					
					getOutput().writeStartElement(OT.NS,"value"); //value
					if (value instanceof Number) {
						p.setClazz(Double.class);
						getOutput().writeAttribute(RDF.getURI(),"datatype","http://www.w3.org/2001/XMLSchema#double");
					} else {
						getOutput().writeAttribute(RDF.getURI(),"datatype","http://www.w3.org/2001/XMLSchema#string");
					}
					getOutput().writeCharacters(value.toString());
					getOutput().writeEndElement(); //value					
					
					if (p.isNominal() && (value instanceof Comparable)) 
						p.addAllowedValue((Comparable)value);
				} catch (Exception x) {
					logger.log(Level.WARNING,x.getMessage(),x);
				} finally {

					
					getOutput().writeEndElement(); //FeatureValue
					getOutput().writeEndElement();//values
					
				}
	
				//if (p.isNominal())
				//	feature.addProperty(OTProperty.acceptValue.createProperty(getJenaModel()), value.toString());
					
			}
			//getOutput().flush();
/*
 * Commented - not sure will have effect on the error, but whould be safe otherwise
Caused by: javax.xml.stream.XMLStreamException: ClientAbortException:  java.io.IOException
         at com.sun.xml.stream.writers.XMLStreamWriterImpl.flush(XMLStreamWriterImpl.java:404)
         at com.sun.xml.txw2.output.DelegatingXMLStreamWriter.flush(DelegatingXMLStreamWriter.java:56)
         at ambit2.rest.dataset.DatasetRDFWriter.process(DatasetRDFWriter.java:231)
 */
			return item;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try { getOutput().writeEndElement(); } catch (Exception x) {}
			try { getOutput().writeEndElement(); } catch (Exception x) {}
		}
	
	}
	protected String createDatasetURI(int datasetID) {

		if (uriReporter.getResourceRef() ==null) {
			return null;
		} else 	if (uriReporter.getResourceRef().getQueryAsForm().getFirstValue(OpenTox.params.feature_uris.toString()) != null) {
			return null;
		} else {
			if (datasetID<=0)
				return
					String.format("%s:%s",
							uriReporter.getResourceRef().getScheme(),
							uriReporter.getResourceRef().getHierarchicalPart()
							);
			else {
				String datasetPrefix = String.format("/%s/%d",OpenTox.URI.dataset.name(), datasetID);
				return
						String.format("%s%s",
								uriReporter.getBaseReference(),
								datasetPrefix
								);
			}
		}
	}
	public void header(javax.xml.stream.XMLStreamWriter writer) {
		super.header(writer);
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
			



		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	};
	public void footer(javax.xml.stream.XMLStreamWriter writer) {
		
		try {
			writer.writeEndElement();
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
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
				
				if (p.getClazz().equals(Dictionary.class )) {
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
				if ((uri!=null) && (uri.indexOf("http://")<0) && (uri.indexOf("https://")<0)) {
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
			super.footer(writer);
			
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
			
			if ((uri.indexOf("http://")<0) && (uri.indexOf("https://")<0)) {
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
			
			if (template!=null) {
				it = template.getProperties(true);
				if (it != null)
					while (it.hasNext()) {
						Property t = it.next();
						if (!propertiesOnly || (propertiesOnly && (t.getId()>0)))
							h.add(t);
					}
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
		
		public void close() {
			try { getOutput().close();} catch (Exception x) {}
		}
}
