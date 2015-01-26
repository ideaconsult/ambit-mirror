package ambit2.base.processors;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

/**
 * Transform a string to CAS number format XXXXX-XX-X
 * 
 * @author nina
 * 
 */
public class CASProcessor extends DefaultAmbitProcessor<String, String> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 144610103301504232L;
    protected static final String zero = "0";
    protected static final String dash = "-";

    public String process(String target) throws AmbitException {
	target = target.trim();
	int index = 0;
	for (int i = 0; i < target.length(); i++) {
	    if (target.substring(index, index + 1).equals(zero))
		index++;
	    else
		break;
	}
	return target.substring(index);
	/*
	 * if (target.indexOf(dash)>0) return target.substring(index); try {
	 * b.append(target.substring(index,target.length()-3)); b.append(dash);
	 * b.append(target.substring(target.length()-3,target.length()-1));
	 * b.append(dash); b.append(target.substring(target.length()-1)); return
	 * b.toString(); } catch (Exception x) { return target.substring(index);
	 * }
	 */
    }

    public static boolean isValidFormat(String target) {
	try {
	    if (target.length() > 12)
		return false;
	    if (target.length() < 7)
		return false;
	    boolean ok = false;
	    for (int i = target.length() - 1; i >= 0; i--) {
		String d = target.substring(i, i + 1);
		if ((i == (target.length() - 2)) || (i == (target.length() - 5))) {
		    if (!d.equals(dash))
			return false;
		} else
		    Integer.parseInt(d);
		ok = true;
	    }
	    return ok;
	} catch (Exception x) {
	    return false;
	}
    }

}
/*
 * select name,value, mod(
 * if(length(value)>11,substring(right(value,12),1,1)*9,0)+
 * if(length(value)>10,substring(right(value,11),1,1)*8,0)+
 * if(length(value)>9,substring(right(value,10),1,1)*7,0)+
 * if(length(value)>8,substring(right(value,9),1,1)*6,0)+
 * if(length(value)>7,substring(right(value,8),1,1)*5,0)+
 * if(length(value)>6,substring(right(value,7),1,1)*4,0)+
 * substring(right(value,6),1,1)*3+ substring(right(value,4),1,1)*2+
 * substring(right(value,3),1,1)*1 ,10) cs, right(value,1) g from properties
 * join property_values using(idproperty) join property_string
 * using(idvalue_string) where (length(value)<=13) and idtype='STRING' and
 * substring(right(value,2),1,1)='-' and substring(right(value,5),1,1)='-' order
 * by length(value) desc limit 100
 */
