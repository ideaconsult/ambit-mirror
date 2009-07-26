package ambit2.rest.similarity;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QuerySimilarityBitset;
import ambit2.rest.query.StructureQueryResource;

public class SimilarityResource extends StructureQueryResource<QuerySimilarityBitset> {
	protected String dataset_id;
	protected static String delim = null;
	protected static String bracketLeft="[";
	protected static String bracketRight="]";
	
	public SimilarityResource(Context context, Request request, Response response) {
		super(context,request,response);
		
		try {
			this.dataset_id = Reference.decode(request.getAttributes().get("dataset_id").toString());
		} catch (Exception x) {
			this.dataset_id = null;
		}
		if (delim == null) {
			StringBuilder d = new StringBuilder();
			d.append("-=#+-()/\\.@");
			for (char a='a';a<='z';a++)	d.append(a);
			for (char a='A';a<='Z';a++)	d.append(a);
			delim = d.toString();
		}

	}

	@Override
	protected QuerySimilarityBitset createQuery(Context context,
			Request request, Response response) throws AmbitException {
		Double threshold = 0.0;
        try {
        	threshold = new Double(Reference.decode(request.getAttributes().get("threshold").toString()));
        } catch (Exception x) {
        	threshold = 0.5;
        }		
        
		QuerySimilarityBitset q = new QuerySimilarityBitset();
		q.setThreshold(threshold);
		q.setCondition(NumberCondition.getInstance(">"));		
		
		
		
		try {
			System.out.println(request.getAttributes().get("smiles").toString());
	        String smiles = Reference.decode(request.getAttributes().get("smiles").toString());
	        System.out.println(smiles);
			q.setValue(getBitset(getMolecule(smiles)));			
			return q;
		} catch (InvalidSmilesException x) {
			throw new AmbitException(x);
		} catch (Exception x) {
			throw new AmbitException(x);
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
	public BitSet getBitset(IMolecule molecule) throws AmbitException {
		FingerprintGenerator gen = new FingerprintGenerator();
		return gen.process(molecule);
	}

		
}
