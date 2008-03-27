package ambit2.processors.toxtree;

import org.openscience.cdk.interfaces.IMolecule;

import toxTree.core.IDecisionMethod;
import toxTree.core.IDecisionResult;
import toxTree.exceptions.DecisionMethodException;
import verhaar.VerhaarScheme;
import ambit2.similarity.ISimilarityProcessor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.editors.ToxTreeEditor;
import ambit2.exceptions.AmbitException;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IAmbitResult;


/**
 * Encapsulates {@link toxTree.core.IDecisionMethod} as a processor. 
 * This is the way to invoke {@link verhaar.VerhaarScheme} , {@link toxTree.tree.cramer.CramerRules}
 * or other Toxtree modules as {@link ambit2.processors.IAmbitProcessor} 
 * <br>Example<br>
 * ToxTreeProcessor p = new ToxTreeProcessor(new VerhaarScheme());
 * 
 * p.process(molecule);
 * 
 * TODO use toxtree plugin mehanism for arbitrary plugins
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ToxTreeProcessor extends DefaultAmbitProcessor {
	protected IDecisionMethod method;
	protected IDecisionResult result;

    public ToxTreeProcessor() {
        this(null);
    }
	public ToxTreeProcessor(IDecisionMethod method) {
		super();
		if (method == null)
			try {
				//setMethod(new CramerRules());
				setMethod(new VerhaarScheme());
			} catch (DecisionMethodException x) {
				setMethod(null);
			}
		else	
			setMethod(method);
	}
	public Object process(Object object) throws AmbitException {
		if (object instanceof IMolecule) {
			IMolecule mol = (IMolecule) object;
			try { 
                method.setParameters(mol);
				method.classify(mol, result);
				
				//mol.setProperty(method.toString(),result.getCategory());
                
                mol.setProperty(result.getResultPropertyName(),result.getCategory());
                //mol.setProperty(result.getResultPropertyName()+"#explanation",result.explain(false));
				//mol.setProperty(method.getClass().getName(),method.explainRules(result,false));

			//} catch (DecisionResultException x) {
				//throw new AmbitException(x);
			} catch (Exception x) {
				throw new AmbitException(x);

			}
		}
		return object;
	}

	public IAmbitResult createResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAmbitResult getResult() {
		return null;
	}

	public void setResult(IAmbitResult result) {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public IDecisionMethod getMethod() {
		return method;
	}
	public void setMethod(IDecisionMethod method) {
		this.method = method;
		if (method != null)
			this.result = method.createDecisionResult(); 
	}
	public String toString() {
		return method.toString();
	}
	/* (non-Javadoc)
     * @see ambit2.processors.DefaultAmbitProcessor#getEditor()
     */
    public IAmbitEditor getEditor() {
        return new ToxTreeEditor(method);
    }
}
