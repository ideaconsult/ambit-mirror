/**
 * <b>Filename</b> IDBMolAction.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-16
 * <b>Project</b> ambit
 */
package ambit.database.old;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.exceptions.AmbitException;

/**
 * @deprecated
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-16
 */
public interface IDBMolAction {
    int doAction(int idsubstance, IMolecule mol) throws AmbitException;
    int onError(int idsubstance, IMolecule mol,Exception error) throws AmbitException;
}

