package ambit2.rest.template;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryDOMReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.property.PropertyDOMParser;
import ambit2.rest.property.PropertyDOMReporter;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.AbstractDOMParser;

public class OntologyDOMReporter<Q extends IQueryRetrieval<Object>> extends QueryDOMReporter<Object, Q> {
	protected PropertyDOMReporter<IQueryRetrieval<Property>> reporterProperty;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7852309113522482578L;
	protected boolean recursive = false;
	protected int count = 0;
	protected AbstractDOMParser parser;
	public OntologyDOMReporter(Request ref) {
		this(ref,false);
	}
	public OntologyDOMReporter(Request ref,Boolean isRecursive) {
		super(ref);
		reporterProperty = new PropertyDOMReporter<IQueryRetrieval<Property>>(ref);
		recursive = isRecursive;
		parser = new PropertyDOMParser(){
			@Override
			public void parse(Reader reader)
					throws ParserConfigurationException, IOException,
					SAXException, AmbitException {
				super.parse(reader);
			}
			@Override
			public void handleItem(Property item) throws AmbitException {
				processItem(item);
				
			}
		};	
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference) {
		return new OntologyURIReporter(reference);
	}
	@Override
	public Document getOutput() throws AmbitException {
		Document doc = super.getOutput();
		reporterProperty.setOutput(doc);
		return doc;
	}
	@Override
	public void footer(Document output, Q query) {
	
	}

	@Override
	public void header(Document doc, Q query) {
		doc.appendChild(doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_featuredefs));
	}

	@Override
	public void processItem(Object item) throws AmbitException {
		if (item == null) return;
		count++;
		if (item instanceof Property) {
			((Property) item).setOrder(count);
			reporterProperty.processItem((Property) item);
		} else if (item instanceof Dictionary){
			Property p  = new Property(((Dictionary)item).getTemplate());
			p.setLabel(((Dictionary) item).getParentTemplate());
			p.setClazz(Dictionary.class);
			p.setId(-1);
			p.setEnabled(true);
			p.setOrder(count);
			reporterProperty.processItem(p);
			

			if (recursive) {
			
				
		
				Reference newuri = (uriReporter.getBaseReference()!=null)?
					new Reference(uriReporter.getURI(item)+"/view/tree"):
					new Reference("riap://application"+uriReporter.getURI(item)+"/view/tree");
					
				if (!newuri.equals(uriReporter.getRequest().getOriginalRef())) {
					readDeep(newuri, parser);
				}					
			}

		}
	}
	@Override
	public Element getItemElement(Document doc, Object item) {
		if (item instanceof Property)
			return reporterProperty.getItemElement(doc,(Property) item);

        return null;
	}

}