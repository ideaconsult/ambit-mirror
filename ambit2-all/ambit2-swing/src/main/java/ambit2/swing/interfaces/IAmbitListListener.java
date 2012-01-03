/**
 * Created on 2005-3-29
 * Modified 2012-1-2
 */
package ambit2.swing.interfaces;

import ambit2.swing.events.AmbitListChanged;

/**
 * An {@link ambit.data.IAmbitObjectListener} listening for {@link AmbitListChanged} event
 * @author Nina Jeliazkova
 * Modified: 2005-4-6
 *
 * Copyright (C) 2005  AMBIT project http://ambit.sf.net
 *
 * Contact: www.ideaconsult.net
 * 
 */
public interface IAmbitListListener<T extends AmbitObject> extends IAmbitObjectListener<T> {
    public void ambitListChanged(AmbitListChanged<T> event);
}
