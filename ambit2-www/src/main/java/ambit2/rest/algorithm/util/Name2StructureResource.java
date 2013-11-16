package ambit2.rest.algorithm.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import ambit2.core.io.MDLWriter;
import ambit2.namestructure.Name2StructureProcessor;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.ProtectedResource;
import ambit2.rest.query.QueryResource;
/**
 * Name2structure convertor based on opsin
 * @author nina
 *
 */
public class Name2StructureResource extends ProtectedResource {
	public static final String resource = "name2structure";
	protected Name2StructureProcessor processor = new Name2StructureProcessor();
	protected String name = null;
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		MediaType[] mimeTypes = new MediaType[] {
				//MediaType.TEXT_HTML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				//ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_CML,
				//MediaType.IMAGE_PNG,
				//MediaType.APPLICATION_PDF,
			//	MediaType.TEXT_XML,
				//MediaType.TEXT_URI_LIST,
				//MediaType.TEXT_PLAIN
				};
        
        for (MediaType mileType:mimeTypes) getVariants().add(new Variant(mileType));
       // getVariants().put(Method.GET, variants);
        //getVariants().put(Method.POST, variants);		


		Form form = getResourceRef(getRequest()).getQueryAsForm();
		Object key = form.getFirstValue(QueryResource.search_param);
		if (key != null) {
			name = Reference.decode(key.toString());
		} else name = null; 		
	}

	public Representation get(Variant variant) {
		setFrameOptions("SAMEORIGIN");
		try {
	        if (name != null) {
	        	if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) 
	        		return new OutputRepresentation(ChemicalMediaType.CHEMICAL_CML) {
	        			@Override
	        			public void write(OutputStream out) throws IOException {
	        				try {
		        				OpsinResult result = processor.name2structure(name);
		        				processor.opsin2cml(result, out);
		        				out.flush();
	        				} catch (Exception x) {
	        					throw new IOException(x.getMessage());
	        				}
	        			}
	        		};
	        	else 
	        	return new OutputRepresentation(ChemicalMediaType.CHEMICAL_MDLSDF) {
	        		@Override
	        		public void write(OutputStream out) throws IOException {
	        			try {
		    	        	IAtomContainer mol = processor.process(name);
		        			MDLWriter writer = new MDLWriter();
		        			writer.setSdFields(mol.getProperties());
		        			writer.setWriter(out);
		        			writer.write(mol);
	        			} catch (Exception x) {
	        				getLogger().log(Level.WARNING,x.getMessage(),x);
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
