package ambit2.rest.algorithm.util;

import java.io.IOException;
import java.io.OutputStream;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.Mol2Writer;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;

import ambit2.db.search.StringCondition;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.namestructure.Name2StructureProcessor;
import ambit2.rest.ChemicalMediaType;
/**
 * Name2structure convertor based on opsin
 * @author nina
 *
 */
public class Name2StructureResource extends Resource {
	public static final String resource = "name2structure";
	protected Name2StructureProcessor processor = new Name2StructureProcessor();
	protected String name = null;
	public Name2StructureResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));	
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_SMILES));		
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));		
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));	

		Form form = request.getResourceRef().getQueryAsForm();
		Object key = form.getFirstValue("search");
		if (key != null) {
			name = Reference.decode(key.toString());
		} else name = null; 		
	}

	public Representation getRepresentation(Variant variant) {
		
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
