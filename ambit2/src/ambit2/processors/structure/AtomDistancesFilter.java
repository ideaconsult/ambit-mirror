package ambit2.processors.structure;



import org.openscience.cdk.interfaces.IChemObject;

import ambit2.database.query.DistanceQuery;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;
import ambit2.processors.results.AtomDistancesResult;

/**
 * Skips molecules without assigned {@link ambit2.processors.results.AtomDistancesResult#property}<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class AtomDistancesFilter extends AtomDistanceProcessor {
    public AtomDistancesFilter(DistanceQuery query) {
        super(query);
       
    }	
	public Object process(Object object) throws AmbitException {
		if (object == null) return null;
		IChemObject ac = (IChemObject)super.process(object);
		Object r = ac.getProperty(AtomDistancesResult.property);
		if ((r!=null) && (r instanceof AtomDistancesResult) && 
				(((AtomDistancesResult)r).size()>0))return ac;
		else return null;
	}
    public IAmbitResult getResult() {
        return  createResult();
    }	
}
