/**
 * Created on 2005-3-23
 *
 */
package ambit2.data;

import java.io.Serializable;

/**
 * A common interface for {@link AmbitObject} <br>
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public interface AmbitInterface extends Cloneable, Comparable, Serializable {
	/**
	 * Identifier
	 * @return
	 */
	int getId();
	/**
	 * set identifier
	 * @param id
	 */
	void setId(int id);
	/**
	 * name
	 * @return
	 */
	String getName();
	/**
	 * set name
	 * @param name
	 */
	void setName(String name);
	/**
	 * has ID assigned
	 * @return
	 */
	boolean hasID();
}
