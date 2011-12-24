package ambit2.base.data;

import ambit2.base.data.QLabel.QUALITY;

/**
 * Quality label
 * Q(experts) = (100*a+100*b +1) / (100*d + 10*c+1)
10 experts max
labels
<ul>
OK,ProbablyOK,Unknown,ProbablyERROR,ERROR
</ul>
1)automatic verification -> assigns OK, ? , Not OK
2)Expert verification (up to 10 experts) - assigns (a)OK(expert), (b) Almost OK (expert), (c) Almost Not OK (expert), (d) Not OK (expert)
<p>
Each experts assign exactly one label and one of the values a,b,c,d is incremented by one
Q is calculated by the formula above
<p>
--
Auto verification
1)inherent coherence of a single origin (e.g. SMILES -structure correspondence)
<ul>
<li>select a property that contains SMILES (not the smiles field from chemicals table!)
<li>compare this property with the smiles field from chemicals table
<li>compare this property with structure, if chemicals.smiles is null
</ul>
<p>
2)coherence of multiple origins - compare all structures of same idchemical - assign OK if same, notOK if not same
<ul>
<li>retrieve a chemical (by idchemical)
<li>retrieve structures per chemical
<li>calculate fingerprints per structure
<li>compare structure fingerprints with fingerprints from fp1024 (per chemical), if different, assign "ERROR", otherwise assign "ProbablyOK"
<li>(slow) select one structure and compare all other structures (same chemical) with isomrphism test; assign "ERROR" if not same. 
</ul>   
<p>
3)identificators correspondence between different origins
<ul>
<li>the user selects key field (e.g. CAS) and fields to be compared (e.g. structure, names, descriptors)
<li>select field (e.g. CAS)
<li>select field to be compared (e.g. structure)
<li>for all structures having same CAS calculate Q metric 
<li>save metric under e.g. CAS-QA user
</ul>
+select field (e.g. smiles, structure)
+select field to be compared (e.g. CAS)
+ for all CAS having same structures calculate Q metric  - NOK if there are differences
+save metric under e.g. struc-CAS-QA user
<p>

+select field (e.g. PubchemCID)
+select field to be compared (e.g. EINECS)
+ for all EINECS having same CID calculate Q metric  ok if all einecs are the same, nok if difference
+save metric under e.g. CID-QA user
- auto verification assigns auto labels as above
<p>
 * @author nina
 *
 */
public class QLabel extends AbstractLabel<QUALITY> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6648276842177265920L;
	public enum QUALITY { OK,ProbablyOK,Unknown,ProbablyERROR,ERROR};
	
	public QLabel() {
		
	}
	public QLabel(QUALITY label) {
		setLabel(label);
	}	
	
}
