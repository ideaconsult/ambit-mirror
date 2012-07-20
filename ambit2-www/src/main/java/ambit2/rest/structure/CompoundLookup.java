package ambit2.rest.structure;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.CASProcessor;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.EINECS;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryAbstractReporter;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryField;
import ambit2.db.search.structure.QueryFieldMultiple;
import ambit2.db.search.structure.QueryStructure;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.pubchem.NCISearchProcessor;
import ambit2.rest.DisplayMode;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.task.CallableQueryProcessor;

/**
 * /query/compound/{structure identifier}/{representation}
 * Offers same interface as Chemical Identifier Resolver 
 * http://cactus.nci.nih.gov/chemical/structure/documentation
 * http://cactus.nci.nih.gov/chemical/structure/"structure identifier"/"representation"
 * @author nina
 *
 */
public class CompoundLookup extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public static String resource = "/compound";
	protected static final String resourceKey = "term";
	protected static final String representationKey = "representation";
	public static final String resourceID = String.format("/{%s}",resourceKey);
	public static final String representationID = String.format("/{%s}",representationKey);
	protected String text = null;
	protected String[] text_multi = null;
	protected NCISearchProcessor.METHODS rep_id = null;
	
	enum _searchtype {
		cas,
		einecs,
		text,
		smiles,
		inchi,
		url,
		ambitid
	}
	protected static String URL_as_id = "url";
	protected static String SEARCH_as_id = "search";
	protected _searchtype searchType = null;
	protected Form params;

	/**
	 * SMILES, InChI, InChI key (lookup) , identifiers, names
	 */

	/*
	 */
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		//parse params

		
		Object id = null;
		try {
			id = getRequest().getAttributes().get(resourceKey);
			if (id == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No structure identifier");
			text = Reference.decode(id.toString().trim());
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Invalid structure identifier",id));
		}
		id = null;
		try {
			id = getRequest().getAttributes().get(representationKey);
			if (id == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No structure representation");
			rep_id = NCISearchProcessor.METHODS.valueOf(Reference.decode(id.toString()));
		} catch (Exception x) {
			rep_id = null;
		}
		
		Form form = getParams();
		try { headless = Boolean.parseBoolean(form.getFirstValue("headless")); } catch (Exception x) { headless=false;}		
		boolean casesens = "true".equals(form.getFirstValue(QueryResource.caseSensitive))?true:false;
		boolean retrieveProperties = "true".equals(form.getFirstValue(QueryResource.returnProperties))?true:false;
			
		
		String url = null;
		IQueryRetrieval<IStructureRecord>  query = null;
		if (isURL(text)) try {
			
			url = form.getFirstValue(search_param);
			if (url==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No search parameter!");
			Object q = CallableQueryProcessor.getQueryObject(new Reference(url), getRequest().getRootRef(),getApplication().getContext());
			if (q==null) {
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,"TODO: retrieve compounds from foreign urls");
			} else if (q instanceof AbstractStructureQuery) {
				query = (IQueryRetrieval<IStructureRecord>)q;
			}	
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format("%s %s",url,x.getMessage()),x);
		} 
		else {
			if (isSearchParam(text)) {
				text = form.getFirstValue(search_param);
				if (text !=null) text = text.trim();
				text_multi = form.getValuesArray(search_param);
			}

			int idcompound = isAmbitID(text);
			//query
			if ((text_multi!= null) && (text_multi.length>1)) {
				query =  getMultiTextQuery(null,casesens,retrieveProperties, text_multi);
			} else	if (CASProcessor.isValidFormat(text)) { //then this is a CAS number
				if (CASNumber.isValid(text)) {
					searchType = _searchtype.cas;
					query =  getTextQuery(Property.getCASInstance(),casesens,retrieveProperties,text);
				}
			} else if (EINECS.isValidFormat(text)) { //this is EINECS
				//we'd better not search for invalid numbers
				if (EINECS.isValid(text)) {
					searchType = _searchtype.einecs;
					query =  getTextQuery(Property.getEINECSInstance(),casesens,retrieveProperties,text);
				}
			} else if (idcompound>0)  {
				IStructureRecord record = new StructureRecord();
				record.setIdchemical(idcompound);
				QueryStructureByID q = new QueryStructureByID();
				q.setPageSize(1);
				q.setChemicalsOnly(true);
				q.setValue(record);
				query = q;
			} else {
				//if inchi
				if (isInChI(text)) {
					QueryStructure q = new QueryStructure();
					q.setChemicalsOnly(true);
					q.setFieldname(ExactStructureSearchMode.inchi);
					q.setValue(text);
					query = q;
				} else try {
					//inchi, inchikey
					String[] inchi = smiles2inchi(text);
					QueryStructure q = new QueryStructure();
					q.setChemicalsOnly(true);
					q.setFieldname(ExactStructureSearchMode.inchi);
					//eventually search by inchikey?
					q.setValue(inchi[0]); 
					query = q;
				} catch (Exception x) {
					x.printStackTrace();
					query= null;
				}
			}
		}
		if (query == null) query = getTextQuery(null,casesens,retrieveProperties, text);

		setPaging(form, query);
		
		setTemplate(createTemplate(form));
		setGroupProperties(context, request, response);
		return query;
	}
	
	protected Form getParams() {
		if (params == null) 
			if (Method.GET.equals(getRequest().getMethod()))
				params = getResourceRef(getRequest()).getQueryAsForm();
			//if POST, the form should be already initialized
			else params = getRequest().getEntityAsForm();
		return params;
	}
	protected QueryField getTextQuery(Property property, boolean caseSensitive, boolean retrieveProperties, String value) {
		QueryField q_by_name = new QueryField();
		q_by_name.setFieldname(property);
    	q_by_name.setCaseSensitive(caseSensitive);
    	q_by_name.setRetrieveProperties(retrieveProperties);
    	q_by_name.setSearchByAlias(true);
    	q_by_name.setNameCondition(StringCondition.getInstance(StringCondition.C_EQ));
    	q_by_name.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
    	q_by_name.setChemicalsOnly(true);
    	q_by_name.setValue(value==null?null:value.toString());
		return q_by_name;
	}
	
	protected QueryFieldMultiple getMultiTextQuery(Property property, boolean caseSensitive, boolean retrieveProperties,  String[] value) {
		QueryFieldMultiple q_by_name = new QueryFieldMultiple();
		
    	q_by_name.setCaseSensitive(caseSensitive);
    	q_by_name.setRetrieveProperties(retrieveProperties);
    	q_by_name.setSearchByAlias(true);
    	List<String> values = new ArrayList<String>();
    	

	    	int cas = 0;
	    	int einecs = 0;
	    	int inchi = 0;
	    	for (String v : value) {
				if (CASProcessor.isValidFormat(v))
				if (CASNumber.isValid(v)) cas++;
				else if (EINECS.isValidFormat(v)) 
				if (EINECS.isValid(v)) einecs++;
				if (v.startsWith("InChI=")) inchi++;
	    		values.add(v);
	    	}
	    	/*
	   
	    	if (cas==value.length) { property = Property.getCASInstance(); q_by_name.setCaseSensitive(false); }
	    	else
	    	if (einecs==value.length) { property = Property.getEINECSInstance();  q_by_name.setCaseSensitive(false);}
	    	else if((cas+einecs)==value.length) q_by_name.setCaseSensitive(false);
	    	else if (inchi>0) q_by_name.setCaseSensitive(false);
	    	else q_by_name.setCaseSensitive(false);
    		*/
    
    	
    	q_by_name.setFieldname(property);
    	q_by_name.setChemicalsOnly(true);
    	q_by_name.setValue(values);
		return q_by_name;
	}	
	@Override
	protected void setGroupProperties(Context context, Request request,
			Response response) throws ResourceException {
		if (rep_id==null) return;
		String[] r = rep_id.getOpenToxEntry();
		if (r==null) return;
		Template gp = new Template();
		for (int i=0; i < r.length;i++) {
			Property p = new Property(r[i]);
			p.setLabel(r[i]);
			p.setEnabled(true);
			p.setOrder(i+1);
			gp.add(p);
		}
		setGroupProperties(gp);
	}
	@Override
	protected Template createTemplate(Context context, Request request,
			Response response) throws ResourceException {
		/*
		String[] r = rep_id.getOpenToxEntry();
		if (r==null) return super.createTemplate(context, request, response);
		
		String[] featuresURI = new String[r.length];
		for (int i=0; i < r.length;i++)
			featuresURI[i] = String.format("%s/%s?sameas=%s",getRequest().getRootRef(),OpenTox.URI.feature,Reference.encode(r[i]));
		
		return createTemplate(context, request, response, featuresURI);
		*/
		return super.createTemplate(context, request, response);
	}
	
	public boolean isInChI(String inchi)  {
		return ((inchi!= null) && inchi.startsWith(AmbitCONSTANTS.INCHI)); 
	}	
	/*
	public IAtomContainer isInChI(String inchi) throws Exception {
		if ((inchi!= null) && inchi.startsWith(AmbitCONSTANTS.INCHI)) {
			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
			InChIToStructure c =f.getInChIToStructure(inchi, SilentChemObjectBuilder.getInstance());
			if ((c==null) || (c.getAtomContainer()==null) || (c.getAtomContainer().getAtomCount()==0)) 
				throw new Exception("Invalid InChI");
			searchType = _searchtype.inchi;
			return c.getAtomContainer();
		} else return null;
	}
	*/
	public String[] smiles2inchi(String smiles) throws ResourceException {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		try {
			IAtomContainer c = p.parseSmiles(smiles);
			if ((c==null) || (c.getAtomCount()==0)) 
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,smiles);
			FixBondOrdersTool fbt = new FixBondOrdersTool();
			c = fbt.kekuliseAromaticRings((IMolecule)c);
			//inchi can't process aromatic bonds...
			for (IBond bond:c.bonds()) bond.setFlag(CDKConstants.ISAROMATIC, false); 
			//now generate unique inchi, as SMILES are not
			searchType = _searchtype.inchi;
			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
			InChIGenerator gen = f.getInChIGenerator(c);
			INCHI_RET status = gen.getReturnStatus();
			if (INCHI_RET.OKAY.equals(status) || INCHI_RET.WARNING.equals(status))
				return new String[] {gen.getInchi(),gen.getInchiKey()};
			else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Error converting smiles to InChI: %s %s %s %s",status,gen.getMessage(),gen.getLog(),smiles));
			
		} catch (ResourceException x)	 {
			throw x;
		} catch (InvalidSmilesException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		} catch (CDKException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		
	}
	
	public boolean isURL(String text) {
		return URL_as_id.equals(text.toLowerCase());
	}
	public boolean isSearchParam(String text) {
		return SEARCH_as_id.equals(text.toLowerCase());
	}	
	public int isAmbitID(String text) {
		try {
			searchType = _searchtype.ambitid;
			return Integer.parseInt(text);
		} catch (Exception x) {
			searchType = null;
			return 0;
		}
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			params = getParams();
			
			return get(variant);
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
				String.format("%s not supported",entity==null?"":entity.getMediaType()));
	}
	
	protected void configureRDFWriterOption(String defaultWriter) {
		try { 
			Object jenaOption = params.getFirstValue("rdfwriter");
			
			//if no option ?rdfwriter=jena|stax , then take from properties rdf.writer
			//if not defined there, use jena
			rdfwriter = RDF_WRITER.valueOf(jenaOption==null?defaultWriter:jenaOption.toString().toLowerCase());
		} catch (Exception x) { 
			rdfwriter = RDF_WRITER.jena;
		}
	}
	
	protected QueryAbstractReporter createHTMLReporter(Dimension d) {
		return new CompoundHTMLReporter(getCompoundInDatasetPrefix(),getRequest(),getDocumentation(),
						DisplayMode.properties,null,getTemplate(),getGroupProperties(),d,headless);
	}
	/*
	@Override
	protected void processNotFound(NotFoundException x, int retry) throws Exception {
		if (retry>0) super.processNotFound(x, retry);
		else
			if (searchType == null) { //not cas, smiles, einecs, inchi, ambitid, just plain text
				Name2StructureProcessor processor = new Name2StructureProcessor();
				IAtomContainer atomcontainer = processor.process(text);
				QueryExactStructure q = new QueryExactStructure();
				q.setChemicalsOnly(true);
				q.setValue(atomcontainer);
				queryObject = q;
			} else 
				super.processNotFound(x, retry);
	}
	*/
}
