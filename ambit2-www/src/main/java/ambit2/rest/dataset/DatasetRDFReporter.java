package ambit2.rest.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyRDFReporter;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.OT.OTProperty;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * RDF/XML
 * @author nina
 *
 * @param <Q>
 */
public class DatasetRDFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryRDFReporter<IStructureRecord,Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6410553622662161903L;
	protected PropertyRDFReporter propertyReporter;
	protected CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter;
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
	protected Resource dataset;
	
	public DatasetRDFReporter(Request request,ResourceDoc doc,MediaType mediaType, Template template,Profile groupedProperties) {
		super(request,mediaType,doc);
		setGroupProperties(groupedProperties);
		setTemplate(template==null?new Template(null):template);
		initProcessors();
		propertyReporter = new PropertyRDFReporter(request,mediaType,doc);
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
				processItem(target);
				return target;
			};
		});			
	}
	@Override
	public void header(OntModel output, Q query) {
		super.header(output,query);
		OT.OTClass.Dataset.createOntClass(output);
		OT.OTClass.DataEntry.createOntClass(output);
		OT.OTClass.Feature.createOntClass(output);
		OT.OTClass.FeatureValue.createOntClass(output);
		OT.OTClass.Compound.createOntClass(output);
		
		output.createAnnotationProperty(DC.title.getURI());
		output.createAnnotationProperty(DC.description.getURI());
		//output.createAnnotationProperty(DC.identifier.getURI());
		output.createAnnotationProperty(DC.type.getURI());
		
		dataset = output.createIndividual(
				String.format("%s:%s",uriReporter.getRequest().getOriginalRef().getScheme(),uriReporter.getRequest().getOriginalRef().getHierarchicalPart()),
				OT.OTClass.Dataset.getOntClass(output));
		
		//dataset.addProperty(DC.identifier, uriReporter.getRequest().getOriginalRef().toString());
		//dataset.addProperty(DC.title,query.toString());
		//dataset.addProperty(DC.description,uriReporter.getRequest().getOriginalRef().toString());
		try {
			propertyReporter.setOutput(getJenaModel());
		} catch (Exception x) {}
		
		if (header == null) 
			header = template2Header(template,true);
		
		for (ambit2.base.data.Property p : header) try {
			propertyReporter.processItem(p);
		} catch (Exception x) {}		 
	}

	@Override

	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			
			if (header == null) 
				header = template2Header(template,true);
			
			boolean sort = false;
			for (Property p : item.getProperties()) 
				if (p.getId()<=0) continue;
				else
				if (Collections.binarySearch(header,p,comp)<0) {
					header.add(p);
					sort = true;
				}
			if (sort) Collections.sort(header,comp);
			
				
			Individual dataEntry = getJenaModel().createIndividual(
						OT.OTClass.DataEntry.getOntClass(getJenaModel()));
			dataset.addProperty(OT.OTProperty.dataEntry.createProperty(getJenaModel()),dataEntry);
			int i = 0;
			
			String uri = item.getType().equals(STRUC_TYPE.NA)?
					compoundReporter.getURI(item):uriReporter.getURI(item);
			Individual compound = getJenaModel().createIndividual(
					uri,OT.OTClass.Compound.getOntClass(getJenaModel()));
			//compound.addProperty(DC.identifier, uriReporter.getURI(item));			
			dataEntry.addProperty(OT.OTProperty.compound.createProperty(getJenaModel()), compound);
			
//			if (item.getIdstructure()>0)
//				writer.write(String.format("/conformer/%d",item.getIdstructure()));
			
			for (ambit2.base.data.Property p : header) {
				Object value = item.getProperty(p);
				if (value == null) continue;
				
				p.setClazz((value instanceof Number)?Double.class:String.class);
				Individual feature = (Individual)propertyReporter.processItem(p);

				Individual featureValue = getJenaModel().createIndividual(OT.OTClass.FeatureValue.getOntClass(getJenaModel()));
				featureValue.addProperty(OT.OTProperty.feature.createProperty(getJenaModel()),feature);

				featureValue.addLiteral(OT.DataProperty.value.createProperty(getJenaModel()),
							getJenaModel().createTypedLiteral(value, 
							(value instanceof Number)?XSDDatatype.XSDdouble: XSDDatatype.XSDstring));
				
				i++;
				dataEntry.addProperty(OT.OTProperty.values.createProperty(getJenaModel()),featureValue);
				
				if (p.isNominal())
					feature.addProperty(OTProperty.acceptValue.createProperty(getJenaModel()), value.toString());
			}
			return dataEntry;
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
		
	}
	public void open() throws DbAmbitException {
		
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
	@Override
	protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch(Q query) {
		if (query.isPrescreen()) {
			DbReader<IStructureRecord> reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else	
			return super.createBatch(query);
	}
}
