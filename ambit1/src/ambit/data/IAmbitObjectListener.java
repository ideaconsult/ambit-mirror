/**
 * Created on 2005-3-28
 *
 */
package ambit.data;

import java.util.EventListener;

/**
 * To receive notification of an {@link AmbitObject} change event
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public interface IAmbitObjectListener extends EventListener {
    /**
     * Receives notification of an AmbitObject change event.
     *
     * @param event  information about the event.
     */
    public void ambitObjectChanged(AmbitObjectChanged event);	
}
