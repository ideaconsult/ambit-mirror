package ambit2.descriptors.processors;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.processors.IAmbitResult;
import ambit2.descriptors.AtomEnvironment;

/**
 * A list of {@link ambit2.data.descriptors.AtomEnvironment}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class AtomEnvironmentList extends ArrayList<AtomEnvironment> implements IAmbitResult {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8190211422146555342L;
	protected static Logger logger = Logger.getLogger(AtomEnvironmentList.class.getName());

	protected String title = "";
	protected boolean noDuplicates = true;
	public AtomEnvironmentList() {
		super();
	}

	public AtomEnvironmentList(int arg0) {
		super(arg0);
	}

	public AtomEnvironmentList(Collection arg0) {
		super(arg0);

	}

	public void update(Object object) throws AmbitException {

	}

	public void write(Writer writer) throws AmbitException {

	}

    @Override
    public boolean add(AtomEnvironment o) {
    	if (noDuplicates) {
	        int found = indexOf(o);
	        if (found==-1) {
	            o.setFrequency(1);
	            return super.add(o);
	        }  else {
	            get(found).incFrequency();
	            return true;
	        }
    	} else {
            o.setFrequency(1);
            return super.add(o);    		
    	}
    }
    @Override
    public boolean addAll(Collection<? extends AtomEnvironment> c) {
        Iterator<? extends AtomEnvironment> i = c.iterator();
        while (i.hasNext()) {
            add(i.next());
        }
        return true;
    }
	public String toString() {
		String newLine = System.getProperty("line.separator");
		StringBuffer b = new StringBuffer();
		for (int i=0; i < size();i++) {
            b.append(get(i).getFrequency());
            b.append('\t');
			b.append(get(i).toString());
			b.append(newLine);
		}	
		return b.toString();
			
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
    public int frequency() {
        int c  = 0;
        for (int i=0; i < size();i++)
            c+= get(i).getFrequency();
        return c;
    }  
    /**
     * Hellinger distance  between two discrete probability distributions pi and qi.
     * Range is (0,2), where 2 means equal distributions.     
     * * @param hist
     * @return sum(( sqrt(Pi) + sqrt(Qi) )^2)
     */
    public float hellinger(AtomEnvironmentList hist) throws Exception {
       
        
        int cq = frequency();
        if (cq == 0) return 0;
        int cp = hist.frequency();
        if (cp == 0) { 
            logger.warning("The histogram to compare with is empty!");
            throw new Exception("The histogram to compare with is empty!");        
        }

        double HD  = 0;
        double prob[][] = new double[cq][2];
        int count = 0;
        int pcount = 0;
        //iterate through this map
        Iterator<AtomEnvironment> iMap =  iterator();
        AtomEnvironment ae;
        double p,q;
        while (iMap.hasNext()) {
            ae =  iMap.next();
            if (ae != null) {
                q =  (double) ae.getFrequency()/cq;
                prob[count][0] = q;
                if (q > 0) {
                    int found = hist.indexOf(ae);
                    if (found >=0) {
                    	
                        prob[count][1] = hist.get(found).getFrequency();
                        count ++;
                    };
                }
            } 
        }
        
        
        for (int i = 0; i < count; i++) {
        	prob[i][1] = prob[i][1]/((float)cp); 
            HD += Math.pow(Math.sqrt(prob[i][0])+Math.sqrt(prob[i][1]),2.0);
        }
        prob = null;
        //logger.debug("Hellinger \t"+HD);
        HD = Math.sqrt(HD);
        //logger.debug("Hellinger normalized \t"+HD);
        return (float)HD;
        
        
    }

	public boolean isNoDuplicates() {
		return noDuplicates;
	}

	public void setNoDuplicates(boolean noDuplicates) {
		this.noDuplicates = noDuplicates;
	}
    
    
}
