package ambit2.rest.structure;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
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
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryExactStructure;
import ambit2.db.search.structure.QueryField;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.pubchem.NCISearchProcessor;
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
	protected NCISearchProcessor.METHODS rep_id = null;
	
	protected static String URL_as_id = "url";
	
	protected Form params;
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
	}
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
		String url = null;
		IQueryRetrieval<IStructureRecord>  query = null;
		if (isURL(text)) try {
			Form form = getParams();
			url = form.getFirstValue(search_param);
			if (url==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No search parameter!");
			Object q = CallableQueryProcessor.getQueryObject(new Reference(url), getRequest().getRootRef());
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
			int idcompound = isAmbitID(text);
			//query
			
			if (CASProcessor.isValidFormat(text)) { //then this is a CAS number
				if (CASNumber.isValid(text)) query =  getTextQuery(Property.getCASInstance(),true,text);
			} else if (EINECS.isValidFormat(text)) { //this is EINECS
				//we'd better not search for invalid numbers
				if (EINECS.isValid(text)) query =  getTextQuery(Property.getEINECSInstance(),true,text);
			} else if (idcompound>0)  {
				IStructureRecord record = new StructureRecord();
				record.setIdchemical(idcompound);
				QueryStructureByID q = new QueryStructureByID();
				q.setPageSize(1);
				q.setChemicalsOnly(true);
				q.setValue(record);
				query = q;
			} else {
				IAtomContainer structure = null;
				//if inchi
				try { structure = isInChI(text);
				} catch (Exception x) { structure = null;}
				//if smiles
				if (structure==null)
					try { structure = isSMILES(text);
					} catch (Exception x) { structure = null;}
				//exact structure
				if (structure != null) {
					QueryExactStructure q = new QueryExactStructure();
					q.setChemicalsOnly(true);
					q.setValue(structure);
					
					query = q;
				} 
			}
		}
		if (query == null) query = getTextQuery(null,false,text);

		Form form = getParams();
		setPaging(form, query);
		
		setTemplate(createTemplate(form));
		setGroupProperties(context, request, response);
		return query;
	}
	
	protected Form getParams() {
		if (params == null) 
			if (Method.GET.equals(getRequest().getMethod()))
				params = getRequest().getResourceRef().getQueryAsForm();
			//if POST, the form should be already initialized
			else 
				params = getRequest().getEntityAsForm();
		return params;
	}
	protected QueryField getTextQuery(Property property, boolean caseSensitive, String value) {
		QueryField q_by_name = new QueryField();
		q_by_name.setFieldname(property);
    	q_by_name.setCaseSensitive(caseSensitive);
    	q_by_name.setRetrieveProperties(true);
    	q_by_name.setSearchByAlias(true);
    	q_by_name.setNameCondition(StringCondition.getInstance(StringCondition.C_EQ));
    	q_by_name.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
    	q_by_name.setChemicalsOnly(true);
    	q_by_name.setValue(value);
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
	
	public IAtomContainer isInChI(String inchi) throws Exception {
		if (inchi.startsWith(AmbitCONSTANTS.INCHI)) {
			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
			InChIToStructure c =f.getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
			if ((c==null) || (c.getAtomContainer()==null) || (c.getAtomContainer().getAtomCount()==0)) 
				throw new Exception("Invalid InChI");
			return c.getAtomContainer();
		} else throw new Exception("Invalid InChI");
	}
	public IAtomContainer isSMILES(String smiles) throws Exception {
		SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		IAtomContainer c = p.parseSmiles(smiles);
		if ((c==null) || (c.getAtomCount()==0)) throw new InvalidSmilesException(smiles);
		return c;
	}	
	public boolean isURL(String text) {
		return URL_as_id.equals(text.toLowerCase());
	}	
	public int isAmbitID(String text) {
		try {
			return Integer.parseInt(text);
		} catch (Exception x) {
			return 0;
		}
	}
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		return super.post(entity);
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			if (params==null) params = new Form(entity);
			return get(variant);
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
				String.format("%s not supported",entity==null?"":entity.getMediaType()));
	}
}
