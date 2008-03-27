/**
 * Created on 2005-3-29
 *
 */
package ambit2.data;

/**
 * An {@link ambit2.data.IAmbitObjectListener} listening for {@link AmbitListChanged} event
 * @author Nina Jeliazkova
 * Modified: 2005-4-6
 *
 * Copyright (C) 2005  AMBIT project http://luna.acad.bg/nina/projects
 *
 * Contact: nina@acad.bg
 * 
 */
public interface IAmbitListListener extends IAmbitObjectListener {
    public void ambitListChanged(AmbitListChanged event);
}
