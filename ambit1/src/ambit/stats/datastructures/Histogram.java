/**
 * <b>Filename</b> Histogram.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-20
 * <b>Project</b> ambit
 */
package ambit.stats.datastructures;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import ambit.log.AmbitLogger;

/**
 * A histogram. Used in {@link ambit.domain.DataCoverageAtomEnvironmentCore}
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-20
 */
public class Histogram<T extends Comparable> extends TreeMap<T,MutableInt>{
    protected static AmbitLogger logger = new AmbitLogger(Histogram.class.getName());
    public static final double ln2_1 = 1/ Math.log(2);
    /**
     * 
     */
    public Histogram() {
        super();
    }
    public void clearFlag() {

        Iterator iMap = entrySet().iterator();
        
        while (iMap.hasNext()) {
            Map.Entry object = (Map.Entry) iMap.next();
            if (object != null) {
                MutableInt freq = (MutableInt) object.getValue();
                freq.setFlag(false);
            } 
        }
        iMap = null;
    }
    public int addObject(T object,int frequency) {
        int count = 1;
        
        if (containsKey(object)) {
            MutableInt freq = get(object);
            freq.setValue(freq.getValue() + frequency);
            count = freq.getValue();
        } else
            put(object, new MutableInt(frequency));
        return count;
    }
    
    public int addObject(T object) {
        int count = 1;
        if (containsKey(object)) {
            MutableInt freq = get(object);
            freq.increment();
            count = freq.getValue();
        } else
            put(object, new MutableInt(1));
        return count;
    }


    protected boolean getFlag(T object) {
        MutableInt mi = (MutableInt) get(object);
        if (mi != null) return mi.isFlag(); else return false;
    }        
    public void setFlag(T object, boolean value) {
        MutableInt mi = (MutableInt) get(object);
        if (mi != null) mi.setFlag(value);
    }    
    public int getFrequency(T object) {
        MutableInt mi = get(object);
        if (mi == null) return 0;
        else return mi.getValue();
    }
    public Object getMaxFrequency() {
    	
        Iterator iMap = entrySet().iterator();
        int max = 0;
        Object maxObject = null;
        while (iMap.hasNext()) {
            Map.Entry object = (Map.Entry) iMap.next();
            if (object != null) {
                
                MutableInt freq = (MutableInt) object.getValue();
                if (freq.getValue() > max) {
                    max = freq.getValue();
                    maxObject = object.getKey();
                }
            } 
        }
        return maxObject;
    }    

    public int count() {
        int c  = 0;
        Iterator iMap = entrySet().iterator();
        
        while (iMap.hasNext()) {
            Map.Entry object = (Map.Entry) iMap.next();
            if (object != null) {
                MutableInt freq = (MutableInt) object.getValue();
                c+= freq.getValue();
            } 
        }
        iMap = null;
        return c;
    }  
    /**
     * Information entropy of a discrete probability distribution pi
     * @return d = sum(pi * logger(pi))
     */
    public double entropy() {
        logger.debug("entropy()");
        
        int c = count();
        if (c == 0) return 0;

        double e  = 0;
        Iterator iMap = entrySet().iterator();
        double p;
        while (iMap.hasNext()) {
            Map.Entry object = (Map.Entry) iMap.next();
            if (object != null) {
                MutableInt freq = (MutableInt) object.getValue();
                p =  (double) freq.getValue() / c;
                e+= p*Math.log(p);
            } 
        }
        return -e*ln2_1;
        
    }

    /**
     * Kullback-Leibler distance (or relative entropy) between two discrete probability distributions pi and qi     * @param hist
     * @return sum(pi * logger ( pi / qi)
     */
    protected double relativeEntropy(Histogram hist) throws Exception {
        int cq = count();
        if (cq == 0) return 0;
        int cp = hist.count();
        if (cp == 0) throw new Exception("The histogram to compare with is empty!");        

        double KL  = 0;
        Iterator<Entry<T,MutableInt>> iMap = entrySet().iterator();
        Entry<T,MutableInt> object;
        double p,q;
        while (iMap.hasNext()) {
            object = iMap.next();
            if (object != null) {
                MutableInt freq = (MutableInt) object.getValue();
                q =  (double) freq.getValue()/cq;
                if (q > 0) {
                    p = (double)hist.getFrequency(object.getKey())/cp;
                    KL += p*Math.log(p/q);
                }
            } 
        }
        return KL*ln2_1 ;
    }    

    public Iterator<Entry<T, MutableInt>> getIterator() {
        return  entrySet().iterator();
    }
    public double tanimoto(Histogram hist)  {
        double c = 0;
        if (hist == null) return 0;
        if (size() == 0) return 0;
        if (hist.size() == 0) return 0;
        
        int count = 0; 
        int myCount = hist.count();
        int commonFreq = 0;
        
        Iterator<Entry<T,MutableInt>> iMap = entrySet().iterator();
        Entry<T,MutableInt> object;
        double p,q;
        while (iMap.hasNext()) {
            object = iMap.next();
            if (object != null) {
                //if (hist.getFrequency(object.getKey()) > 0) c++;
                commonFreq = hist.getFrequency(object.getKey());
                MutableInt freq = (MutableInt) object.getValue();
                if ((commonFreq > 0) && (commonFreq > freq.getValue()))
                    commonFreq = freq.getValue();
                c+= commonFreq;
                count+= freq.getValue();
            }    
        }
        if ((count + myCount - c) < c) {
            return c/(count + myCount - c);
        } else
            return c/(count + myCount - c);
    }
    /**
     * Hellinger distance  between two discrete probability distributions pi and qi     * @param hist
     * @return sum(( sqrt(pi) + sqrt(qi) )^2)
     */
    public double hellinger(Histogram hist) throws Exception {
        //logger.debug("Hellinger\tThis histogram\n" + this);        
        //logger.debug("Hellinger\tThe second histogram\n" + hist);        
        
        int cq = count();
        if (cq == 0) return 0;
        int cp = hist.count();
        if (cp == 0) { 
            logger.error("The histogram to compare with is empty!");
            throw new Exception("The histogram to compare with is empty!");        
        }

        double HD  = 0;
        double prob[][] = new double[cq][2];
        int count = 0;
        int pcount = 0;
        //iterate through this map
        Iterator<Entry<T,MutableInt>> iMap = entrySet().iterator();
        Entry<T,MutableInt> object;
        double p,q;
        while (iMap.hasNext()) {
            object = iMap.next();
            if (object != null) {
                MutableInt freq = object.getValue();
                q =  (double) freq.getValue()/cq;
                prob[count][0] = q;
                if (q > 0) {
                    //p = (double)hist.getFrequency(object.getKey())/cp;
                    prob[count][1] = (double)hist.getFrequency(object.getKey());
                    pcount += prob[count][1];
                    count ++;
                    
                }
            } 
        }
        for (int i = 0; i < count; i++) {
            prob[i][1] = prob[i][1] / cp;
            HD += Math.pow(Math.sqrt(prob[i][0])+Math.sqrt(prob[i][1]),2.0);
        }
        prob = null;;
        HD = Math.sqrt(HD);
        return HD;
    }

    protected double getFrequencySum(boolean flag, MutableInt count) {
    	double s = 0;
    	Iterator<MutableInt> frequencies = values().iterator();
    	while (frequencies.hasNext()) {
    		MutableInt freq = frequencies.next();
    		if (freq.isFlag())
                freq.setFlag(false);
            else {
                s += (double) freq.getValue();
                count.increment();
            }        		
    	}
    	return s;
    }
    public String toString() {
        int cq = count();
        if (cq == 0) return "";
        
        StringBuffer b = new StringBuffer();
        
        
        Iterator<Entry<T,MutableInt>> iMap = entrySet().iterator();
        Entry<T,MutableInt> object;
        double q;
        int o = 0;
        while (iMap.hasNext()) {
            object = iMap.next();
            o++;
            if (object != null) {
            	b.append( object.getKey());
            	b.append("\t");
                MutableInt freq = object.getValue();
                
                q =  (double) freq.getValue();
                b.append(q);
                b.append("\n");
                
                /*
                b.append(q/cq);
                b.append("\t");
                */                

                
            } 
        }     
        return b.toString();
    }
}

