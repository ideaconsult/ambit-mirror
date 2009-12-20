package ambit2.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.model.AbstractModel;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.OT;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Can be queried by ?subject=""&predicate=""&object=""
 * @author nina
 *
 * @param <T>
 */
public class RDFGraphResource<T extends Serializable> extends AbstractResource<OntModel, T, IProcessor<OntModel,Representation>> {
	public static final String resource="/ontology";
	public static OntModel defaultOntology;
	protected boolean useStatic= true;
	protected enum arguments {
		subject,
		predicate,
		object,
		literal;
		public String get(Form form) {
			return form.getFirstValue(toString());
		}
		public Resource getResource(Form form,OntModel model) {
			String r = get(form);
			if (r == null) return null;
			return model.createResource(Reference.decode(r));
		}
		public Property getProperty(Form form,OntModel model) {
			String r = get(form);
			if (r == null) return null;
			return model.createProperty(r);
		}
		public Literal getLiteral(Form form,OntModel model) {
			String r = get(form);
			if (r == null) return null;
			return model.createLiteral(r);
		}			
	}
	
	protected Resource subject;
	protected Property predicate;
	protected RDFNode object;
	protected Literal literal;
	protected String search;

	public RDFGraphResource() {
		super();
	}
	
	public RDFGraphResource(boolean useStatic) {
		super();
		this.useStatic = useStatic;
	}	
	protected OntModel getOntology(OntModel model) throws ResourceException {
		try {
			if (search==null) return model;
			ClientResource client = new ClientResource(Reference.decode(search.trim()));
			MediaType[] mt = {
					MediaType.APPLICATION_RDF_XML,
					MediaType.TEXT_RDF_N3,
					MediaType.TEXT_RDF_NTRIPLES,
					MediaType.APPLICATION_RDF_TURTLE,
					
			};
			for (MediaType m : mt) {
				Representation r = client.get(m);
				if (client.getStatus().equals(Status.SUCCESS_OK)) {
					readOWL(r.getStream(),model);
					return model;
				}
			}
			
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		}
		return model;
	}
	@Override
	protected OntModel createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			OntModel o = OT.createModel();
			Form form = getRequest().getResourceRef().getQueryAsForm();
			subject = arguments.subject.getResource(form, o);
			predicate = arguments.predicate.getProperty(form, o);
			object = arguments.object.getResource(form, o);
			literal = arguments.literal.getLiteral(form, o);
			search = form.getFirstValue(QueryResource.search_param);
			
			OntModel model = null;
			if (useStatic) {
				if (defaultOntology==null) {
					model = OT.createModel();

					readOWL(DescriptorsFactory.class.getClassLoader().getResourceAsStream("ambit2/descriptors/descriptor-algorithms.owl"),
						model);
					readOWL(DescriptorsFactory.class.getClassLoader().getResourceAsStream("ambit2/descriptors/echa-endpoints.owl"),
							model);	

					readOWL(AbstractModel.class.getClassLoader().getResourceAsStream("ambit2/model/AlgorithmTypes.owl"),
							model);		
					defaultOntology = model;	
				} else model = defaultOntology;
				//defaultOntology = getOntology(defaultOntology==null?OT.createModel():defaultOntology);
							
			} else 
				model = getOntology(OT.createModel(OntModelSpec.OWL_MEM));



			object = object==null?literal:object;	
			return model;
		} catch (Exception x) {
			throw new ResourceException(x);
		}finally {
		
		}	
	}
	
	protected void readOWL(InputStream in , OntModel model) throws Exception {
		try {
			model.read(in,null);
		} catch (Exception x) {
			Logger.getLogger(getClass().getName()).severe(x.toString());
		} finally {
			try { if (in != null) in.close();} catch (Exception x) {}
		}
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {MediaType.TEXT_HTML,
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES	
				});		
		

	}
	
	@Override
	public IProcessor<OntModel, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		return new DefaultAmbitProcessor<OntModel, Representation>() {
			public Representation process(OntModel target) throws AmbitException {
				return new OutputRepresentation(MediaType.TEXT_HTML) {
					@Override
					public void write(OutputStream out) throws IOException {
						OutputStreamWriter writer = new OutputStreamWriter(out);
						AmbitResource.writeHTMLHeader(writer,"Ontology playground" ,getRequest());
						
						//queryObject.l
						StmtIterator iter =  queryObject.listStatements(
						new SimpleSelector(subject,predicate,object==null?literal:object));
						                       //,BO.isClassifiedAs,(RDFNode) q));
								/*
						new SimpleSelector(queryObject.createResource(), 
								BO.isClassifiedAs, 
								q));
								*/
						writer.write("<table class='results'>");
						writer.write("<tr>");
						writer.write(String.format("<th class='results_odd'>Name space %s</th>",subject==null?"":subject.getNameSpace()));
						writer.write(String.format("<th class='results'>Subject=%s</th>",subject==null?"Any":subject.getLocalName()));
						writer.write(String.format("<th class='results_odd'>Predicate=%s</th>",predicate==null?"Any":predicate));
						writer.write(String.format("<th class='results'>Object=%s</th>",object==null?"Any":object));
						writer.write("</tr>");
						int hc = 0;
						while (iter.hasNext()) {
							
							writer.write(String.format(
									"<tr %s>",
									(hc %2)==1?"class=\"results\"":"class=\"results_odd\""		
							));
							Statement st = iter.next();
							writer.write("<td>");
							writer.write(String.format("URI %s",
									st.getSubject().getURI()));
	
							
							writer.write("</td><td>");							
							writer.write(String.format("<a href='%s?object=%s&search=%s'>%s</a>",
									"",
									Reference.encode(st.getSubject().toString()),
									search==null?"":search,
									st.getSubject()));
							
							writer.write(String.format("&nbsp;<a href='%s?subject=%s&search=%s'>%s</a>",
									"",
									Reference.encode(st.getSubject().toString()),
									search==null?"":search,
									"[Define]"));	
							
							writer.write("</td><td>");
							writer.write(String.format("<a href='%s?predicate=%s&search=%s'>%s</a>",
									"",
									Reference.encode(st.getPredicate().toString()),
									search==null?"":search,
									st.getPredicate().getLocalName()));							
							writer.write("</td><td>");
							
							if (st.getObject()instanceof Resource) {
								writer.write(String.format("<a href='%s?%s=%s&search=%s'>%s</a>",
										"",
										"object",
										Reference.encode(st.getObject().toString()),
										search==null?"":search,
										((Resource)st.getObject())));
								
								writer.write(String.format("&nbsp;<a href='%s?subject=%s&search=%s'>%s</a>",
										"",
										Reference.encode(st.getObject().toString()),
										search==null?"":search,
										"[Define]"));									
						
							} else {
								writer.write(String.format("<a href='%s?%s=%s&search=%s'>%s</a>",
										"",
										"literal",
										Reference.encode(((Literal)st.getObject()).getString()),
										search==null?"":search,
										((Literal)st.getObject()).getString()));	
							}
						
								
							writer.write("</td>");
							writer.write("</tr>");
							hc++;
						}
						writer.write("</table>");
						more(writer);
						AmbitResource.writeHTMLFooter(writer,"BO" ,getRequest());
						writer.flush();
					}
				};
			}
		};
	}
	//quick hack for a demo
	protected void more(OutputStreamWriter out) throws IOException {
		
	}
	/*
	@Override
	public IProcessor<Iterator<T>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
			) 
			return new OutputStreamConvertor(
					new PropertyRDFReporter(getRequest(),variant.getMediaType())
					,variant.getMediaType());
		else 
			return super.createConvertor(variant);
	}
	*/
}
