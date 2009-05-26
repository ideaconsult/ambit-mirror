package ambit2.rest.structure.build3d;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.core.io.MDLWriter;
import ambit2.mopac.MopacShell;
import ambit2.rest.ChemicalMediaType;

public class Build3DResource extends Resource {
	protected static String delim = null;	
	protected String smiles = null;
	protected static String bracketLeft="[";
	protected static String bracketRight="]";
	protected MopacShell shell;
	
	public Build3DResource(Context context, Request request, Response response) {
		super(context,request,response);
		try {
			shell = new MopacShell();
		} catch (Exception x) {
			x.printStackTrace();
			shell = null;
		}
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));	
		try {
			this.smiles = Reference.decode(request.getAttributes().get("smiles").toString());
		} catch (Exception x) {
			this.smiles = null;
		}		
		if (delim == null) {
			StringBuilder d = new StringBuilder();
			d.append("-=#+-()/\\.@");
			for (char a='a';a<='z';a++)	d.append(a);
			for (char a='A';a<='Z';a++)	d.append(a);
			delim = d.toString();
		}		
	}
	public Representation getRepresentation(Variant variant) {
		
		try {
	        if (smiles != null) {
	        	IAtomContainer mol = getMolecule(smiles);
	        	if ((mol ==  null) || (mol.getAtomCount()==0)) {
		        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        		return new StringRepresentation("No image",variant.getMediaType());	   
	        	}
	        	final IAtomContainer newmol = shell.process(mol);	        	
	        	return new OutputRepresentation(ChemicalMediaType.CHEMICAL_MDLSDF) {
	        		@Override
	        		public void write(OutputStream out) throws IOException {
	        			MDLWriter writer  = new MDLWriter(out);
	        			try {
	        				writer.setSdFields(newmol.getProperties());
	        				writer.write(newmol);
	        			} catch (CDKException x) {
	        				x.printStackTrace();
	        			} finally {
		        			out.flush();
		        			out.close();
	        			}
	        		}
	        	};
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        	return new StringRepresentation("Undefined query",variant.getMediaType());	        	
	        }
		} catch (Exception x) {
			x.printStackTrace();
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("No results for query "+smiles,variant.getMediaType());	
		
		}
	}			
	public IMolecule getMolecule(String smiles) throws InvalidSmilesException {
		SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		//This is a workaround for a bug in CDK smiles parser

		StringTokenizer t = new StringTokenizer(smiles,"[]",true);
		int bracket = 0;
		Hashtable<String, Integer> digits = new Hashtable<String, Integer>();
		while (t.hasMoreTokens())  {
			String token = t.nextToken();
			if (bracketLeft.equals(token)) { bracket++; continue;}
			if (bracketRight.equals(token)) { bracket = 0; continue;}
			if (bracket>0) continue;
			
			StringTokenizer t1 = new StringTokenizer(token,delim,false);
			while (t1.hasMoreTokens()) {
				String d = t1.nextToken();
				Integer i = digits.get(d);
				if (i==null) digits.put(d,1);
				else digits.put(d,i+1);
			}
			Iterator<Integer> d = digits.values().iterator();
			while (d.hasNext())
				if ((d.next() %2)==1) throw new InvalidSmilesException(smiles);
		}
	

	
		return parser.parseSmiles(smiles);
	}	
}
