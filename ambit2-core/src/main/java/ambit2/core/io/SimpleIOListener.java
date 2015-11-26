/* SimpleIOListener.java
 * Author: Nina Jeliazkova
 * Date: Aug 28, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.core.io;

import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.listener.IWriterListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.IOSetting.Importance;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;

public class SimpleIOListener implements IReaderListener, IWriterListener {

    protected Importance level;
    protected Profile properties;
    protected int counter= 0;

    /**
     * 
     * @param level
     */
    public SimpleIOListener(Importance level) {
        super();
        this.level = level;
        properties =  new Profile();

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.listener.SwingGUIListener#processIOSettingQuestion(org.openscience.cdk.io.setting.IOSetting)
     */
    public void processIOSettingQuestion(IOSetting setting) {
        selectProperties(setting);
    }
    
    protected void selectProperties(IOSetting setting) {
        Property.IO_QUESTION question = Property.IO_QUESTION.valueOf(setting.getQuestion());
        switch (question) {
        case IO_START: { counter = 0;}
        case IO_STOP: { if (counter > 0)
            if (setting.getLevel().ordinal() <= this.level.ordinal())
                onStopEvent();
            else; //silent
        }
        case IO_TRANSLATE_NAME: { 
            if (!"".equals(setting.getName().trim())) {
                //if (properties.get(setting.getName())==null) {
					Property p = Property.getInstance(setting.getName(),"I/O");
					p.setLabel(setting.getDefaultSetting());
					p.setOrder(counter);
                    if (setting.getLevel().ordinal() > this.level.ordinal())
                        p.setEnabled(true);
                    properties.add(p);
                    counter++;
                //}   
            }
            }
        default: {}
        }        
    }
    protected void onStopEvent() {
        
    }
    public void frameRead(ReaderEvent event) {
        
        
    }
    public Profile getProperties() {
        return properties;
    }
    public void setProperties(Profile properties) {
        this.properties = properties;
    }
}
