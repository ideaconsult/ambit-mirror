/**
 * <b>Filename</b> IDBSubstanceIterator.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-16
 * <b>Project</b> ambit
 */
package ambit.database.old;

import ambit.database.exception.DbAmbitException;

/**
 * @deprecated
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-16
 */
public interface IDBSubstanceIterator {
    public int iterate(String sqlSubstances, long limit, IDBMolAction action) throws DbAmbitException;
}

