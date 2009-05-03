/* DefineProfile.java
 * Author: nina
 * Date: Dec 28, 2008
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.workflow.library;

import ambit2.base.data.Endpoints;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.SelectionBean;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Conditional;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.While;

/**
 * Encapsulates profile definition (what properties are of interest).
 * @author nina
 *
 */
public class DefineProfile extends While {
	protected final String SELECTION = "DefineProfile.SELECTION";
	protected enum ADD_PROPERTY {
		add_property {
			@Override
			public String toString() {
				return "Add properties (retrieved from database)";
			}
		},
		add_endpoint {
			@Override
			public String toString() {
				return "Add endpoint(s) (retrieved from database)";
			}
		},
		add_descriptor {
			@Override
			public String toString() {
				return "Add descriptor(s) (calculated)";
			}
		},		
		clear_start_over {
			@Override
			public String toString() {
				return "Clear the profile and start again";
			}
		},				
		no_more {
			@Override
			public String toString() {
				return "Quit profile definition, it is complete";
			}
		},		
	
	};		
	public DefineProfile() {
		
		SelectionBean<ADD_PROPERTY> more = new SelectionBean<ADD_PROPERTY>(
				ADD_PROPERTY.values(),"Define profile"
				);

        UserInteraction<SelectionBean<ADD_PROPERTY>> ui_more = new UserInteraction<SelectionBean<ADD_PROPERTY>>(
        		more,
        		SELECTION,"Define profile");
        
	    UserInteraction<Profile<Property>> defineProperty = new UserInteraction<Profile<Property>>(
	        		new Profile<Property>(),
	        		DBWorkflowContext.PROFILE,
	        		"Select properties");
	    
	    UserInteraction<Profile<Property>> defineEndpoint = new UserInteraction<Profile<Property>>(
        		new Endpoints(),
        		DBWorkflowContext.ENDPOINTS,
        		"Select endpoint(s)");	   
	    
	    DescriptorsFactory factory = new DescriptorsFactory();
	    Profile<Property> descriptors;
	    try {
	    	descriptors = factory.process(null);
	    } catch (Exception x) {
	    	x.printStackTrace();
	    	descriptors = new Profile<Property>();
	    }
	    UserInteraction<Profile<Property>> defineDescriptors = new UserInteraction<Profile<Property>>(
        		descriptors,
        		DBWorkflowContext.DESCRIPTORS,
        		"Select descriptor(s)");	 	    
	    

        Conditional descriptorCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION);
	    				if (o instanceof SelectionBean)
	    					return ADD_PROPERTY.add_descriptor.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                defineDescriptors,
                null);
        
        descriptorCondition.setName( ADD_PROPERTY.add_descriptor.toString());   
        
        Conditional propertyCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION);
	    				if (o instanceof SelectionBean)
	    					return ADD_PROPERTY.add_property.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                defineProperty,
                descriptorCondition);
        
        propertyCondition.setName( ADD_PROPERTY.add_property.toString());   
        
        Conditional endpointCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION);
	    				if (o instanceof SelectionBean)
	    					return ADD_PROPERTY.add_endpoint.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                defineEndpoint,
                propertyCondition);
        endpointCondition.setName( ADD_PROPERTY.add_endpoint.toString());           
        
        Sequence body = new Sequence();
        body.addStep(ui_more);
        body.addStep(endpointCondition);
        setBody(body);

        setTestCondition(new TestCondition() {
        	@Override
        	public boolean evaluate() {
    			Object o = getContext().get(SELECTION);
    			if (o instanceof SelectionBean) 
    		
    				switch (((SelectionBean<ADD_PROPERTY>)o).getSelected()) {
    				case add_property: {
    					return true;}
    				case add_descriptor: {
    					return true;}   
    				case add_endpoint: {
    					return true;}      				
    				case no_more: {
    					return false;
    					}
    				case clear_start_over: {
    					String[] s = new String[]{DBWorkflowContext.PROFILE,DBWorkflowContext.ENDPOINTS,DBWorkflowContext.DESCRIPTORS};
    					for (String a:s)
    					try {
    						Object profile = getContext().get(a);
    						if (profile != null) ((Profile)profile).clear();
    						getContext().put(a,null);
    					} catch (Exception x) {}  						
    					
    					return true;
    				}
    				default: return true;
    				
    				}
    			else return true;
        	}
        });
        
        setName("Define profile");	    
	}
}

