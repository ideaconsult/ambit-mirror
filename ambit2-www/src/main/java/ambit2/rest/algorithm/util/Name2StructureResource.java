package ambit2.rest.algorithm.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.Mol2Writer;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.namestructure.Name2StructureProcessor;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.query.QueryResource;
/**
 * Name2structure convertor based on opsin
 * @author nina
 *
 */
public class Name2StructureResource extends ServerResource {
	public static final String resource = "name2structure";
	protected Name2StructureProcessor processor = new Name2StructureProcessor();
	protected String name = null;
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		MediaType[] mimeTypes = new MediaType[] {
				MediaType.TEXT_HTML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_CML,
				MediaType.IMAGE_PNG,
				MediaType.APPLICATION_PDF,
			//	MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_PLAIN
				};
        
        for (MediaType mileType:mimeTypes) getVariants().add(new Variant(mileType));
       // getVariants().put(Method.GET, variants);
        //getVariants().put(Method.POST, variants);		


		Form form = getRequest().getResourceRef().getQueryAsForm();
		Object key = form.getFirstValue(QueryResource.search_param);
		if (key != null) {
			name = Reference.decode(key.toString());
		} else name = null; 		
	}

	public Representation get(Variant variant) {
		
		try {
	        if (name != null) {
	        	return new OutputRepresentation(ChemicalMediaType.CHEMICAL_MDLSDF) {
	        		@Override
	        		public void write(OutputStream out) throws IOException {
	        			try {
		    	        	IAtomContainer mol = processor.process(name);
		        			Mol2Writer writer = new Mol2Writer();
		        			writer.setWriter(out);
		        			writer.write(mol);
	        			} catch (Exception x) {
	        				x.printStackTrace();
	        			} finally {
		        			out.flush();
		        			out.close();	        				
	        			}
	        		}
	        	};
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Undefined query");
	        	return null;	        	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED,x);
			return null;	
		
		}
	}		
}
