package ambit2.rest.dataset;

import java.util.Comparator;
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
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.QueryStaXReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.OT.OTClass;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

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
	
	public DatasetRDFStaxReporter(Request request,Template template,Profile groupedProperties) {
		super(request);
		setGroupProperties(groupedProperties);
		setTemplate(template==null?new Template(null):template);
		initProcessors();
		propertyReporter = new PropertyURIReporter(request);
		
	}
	@Override
	protected QueryURIReporter<IStructureRecord, IQueryRetrieval<IStructureRecord>> createURIReporter(
			Request req) {
		compoundReporter = new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(req);
		return new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(req);
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
				processItem(target);
				return target;
			};
		});			
	}	
	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
/**
 *   <ot:dataEntry>
      <ot:DataEntry>
 */
			getOutput().writeStartElement(ot,"dataEntry"); //property
			getOutput().writeStartElement(ot,"DataEntry");  //object
			
/**
 * 	        <ot:compound>
	          <ot:Compound rdf:about="compound/147763/conformer/419873"/>
	        </ot:compound>
 */
			getOutput().writeStartElement(ot,"compound"); //property
			getOutput().writeStartElement(ot,"Compound"); //property
			
			String uri = item.getType().equals(STRUC_TYPE.NA)?
					compoundReporter.getURI(item):uriReporter.getURI(item);
					
			getOutput().writeAttribute(rdf,"about",uri);
			getOutput().writeEndElement();
			getOutput().writeEndElement();
/**
 *  <ot:values>
          <ot:FeatureValue>
            <ot:value rdf:datatype="http://www.w3.org/2001/XMLSchema#double"
            >1000000.0</ot:value>
            <ot:feature>
              <ot:NumericFeature rdf:about="feature/22562">
                <dc:creator>194.141.0.136</dc:creator>
                <ot:hasSource>ToxCast_Cellumen_20091214.txt</ot:hasSource>
                <owl:sameAs rdf:resource="http://www.opentox.org/echaEndpoints.owl#ToxicoKinetics"/>
                <ot:units></ot:units>
                <dc:title>CLM_Hepat_Apoptosis_48hr</dc:title>
                <rdf:type rdf:resource="http://www.opentox.org/api/1.1#Feature"/>
              </ot:NumericFeature>
            </ot:feature>
          </ot:FeatureValue>
        </ot:values>
 */
			
			for (ambit2.base.data.Property p : header) {
				Object value = item.getProperty(p);
				if (value == null) continue;
				try {
					getOutput().writeStartElement(ot,"values"); //property
					getOutput().writeStartElement(ot,"FeatureValue"); //property
					getOutput().writeStartElement(ot,"feature"); //feature
					getOutput().writeAttribute(rdf,"resource",propertyReporter.getURI(p));
					getOutput().writeEndElement(); //feature
					
					getOutput().writeStartElement(ot,"value"); //value
					if (value instanceof Number) {
						getOutput().writeAttribute(rdf,"dataType","http://www.w3.org/2001/XMLSchema#double");
					} else {
						getOutput().writeAttribute(rdf,"dataType","http://www.w3.org/2001/XMLSchema#string");
					}
					getOutput().writeCharacters(value.toString());
					getOutput().writeEndElement(); //value					
					
					//getOutput().writeAttribute(ot,"about",propertyURIReporter.getURI(item));
					getOutput().flush();
				} catch (Exception x) {
					
				} finally {

					
					getOutput().writeEndElement(); //FeatureValue
					getOutput().writeEndElement();//values
					
				}
	/*
				
				if (p.isNominal())
					feature.addProperty(OTProperty.acceptValue.createProperty(getJenaModel()), value.toString());
					*/
			}



			return item;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			//<ot:dataEntry
			try { getOutput().writeEndElement(); } catch (Exception x) {}
			//ot:DataEntry
			try { getOutput().writeEndElement(); } catch (Exception x) {}
		}
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void header(javax.xml.stream.XMLStreamWriter writer, Q query) {
		try {
			writer.writeStartElement("ot","Dataset");
			/*
			dataset = output.createIndividual(
					String.format("%s:%s",uriReporter.getRequest().getOriginalRef().getScheme(),uriReporter.getRequest().getOriginalRef().getHierarchicalPart()),
					OT.OTClass.Dataset.getOntClass(output));
					*/
		} catch (Exception x) {
			
		}
	};
	
	public void footer(javax.xml.stream.XMLStreamWriter writer, Q query) {
		try {
			writer.writeEndElement();
		} catch (Exception x) {}
		
			//write properties
		for (ambit2.base.data.Property p : header) try {
				/**
              <ot:Feature rdf:about="feature/21586">
                <dc:creator>http://www.epa.gov/NCCT/dsstox/sdf_isscan_external.html</dc:creator>
                <ot:hasSource>ISSCAN_v3a_1153_19Sept08.1222179139.sdf</ot:hasSource>
                <owl:sameAs rdf:resource="http://www.opentox.org/api/1.1#ChemicalName"/>
                <ot:units></ot:units>
                <dc:title>ChemName</dc:title>
              </ot:Feature>
				 */
				getOutput().writeStartElement(ot,"Feature"); //feature
				getOutput().writeAttribute(rdf,"about",propertyReporter.getURI(p));
				
				if (p.isNominal()) {
					//NominalFeature
					getOutput().writeStartElement(rdf,"type"); //feature
					getOutput().writeAttribute(rdf,"resource","http://www.opentox.org/api/1.1#NominalFeature");
					getOutput().writeCharacters(p.getReference().getURL());	
					getOutput().writeEndElement();
				}
				
				boolean numeric = (p.getClazz()==Number.class) || (p.getClazz()==Double.class)
								|| (p.getClazz()==Float.class) || (p.getClazz()==Integer.class)
								|| (p.getClazz()==Long.class);
				if (numeric) {
					//NominalFeature
					getOutput().writeStartElement(rdf,"type"); //feature
					getOutput().writeAttribute(rdf,"resource","http://www.opentox.org/api/1.1#NumericFeature");
					getOutput().writeCharacters(p.getReference().getURL());	
					getOutput().writeEndElement();
				}
				
				if (p.getClazz() ==Dictionary.class ) {
					//TupleFeature
					getOutput().writeStartElement(rdf,"type"); //feature
					getOutput().writeAttribute(rdf,"resource","http://www.opentox.org/api/1.1#TupleFeature");
					getOutput().writeCharacters(p.getReference().getURL());	
					getOutput().writeEndElement();					
				}
		
				getOutput().writeStartElement(dc,"creator"); //feature
				getOutput().writeCharacters(p.getReference().getURL());	
				getOutput().writeEndElement();
				
				getOutput().writeStartElement(dc,"hasSource"); //feature
				getOutput().writeCharacters(hasSource(p));		
				getOutput().writeEndElement();				
				
	
				
				String uri = p.getLabel();
				if(uri==null) uri  = Property.guessLabel(p.getName());
				if ((uri!=null) && (uri.indexOf("http://")<0)) {
					uri = String.format("%s%s",OT.NS,Reference.encode(uri));
				}
				getOutput().writeStartElement(owl,"sameAs"); //feature
				getOutput().writeCharacters(uri);
				getOutput().writeEndElement();						
				
				if (p.getUnits()!=null) {
					getOutput().writeStartElement(ot,"units"); //feature
					getOutput().writeCharacters(p.getUnits());	
					getOutput().writeEndElement();
				}
				
				getOutput().writeStartElement(dc,"title"); //feature
				getOutput().writeCharacters(p.getName());
				getOutput().writeEndElement();	
			} 
			catch (Exception x) {}
			finally {
				try {getOutput().writeEndElement(); } catch (Exception x) {}
			}
	};
	
	protected String hasSource(Property item) throws Exception {
		String uri = item.getTitle();
		if (uri.indexOf("http://")<0) {
			String source  = null;
			
			
			if (_type.Algorithm.equals(item.getReference().getType())) {
				uri = String.format("%s/algorithm/%s",uriReporter.getBaseReference(),Reference.encode(uri));
				//TODO ot:Algorithm
			} else if (_type.Model.equals(item.getReference().getType())) {
				uri = String.format("%s/model/%s",uriReporter.getBaseReference(),Reference.encode(uri));
				//TODO ot:Model
			} else if (_type.Dataset.equals(item.getReference().getType())) {
				//this seems to confuse everybody's else parsers ...
				//uri = String.format("%s/dataset/%s",uriReporter.getBaseReference(),Reference.encode(uri));
			}
			
		} 
		return uri;
	}
}
