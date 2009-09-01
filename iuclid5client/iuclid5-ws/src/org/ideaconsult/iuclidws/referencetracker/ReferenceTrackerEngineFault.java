
/**
 * ReferenceTrackerEngineFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.ideaconsult.iuclidws.referencetracker;

public class ReferenceTrackerEngineFault extends java.lang.Exception{
    
    private org.ideaconsult.iuclidws.types.Types.ReferenceTrackerEngineFault faultMessage;
    
    public ReferenceTrackerEngineFault() {
        super("ReferenceTrackerEngineFault");
    }
           
    public ReferenceTrackerEngineFault(java.lang.String s) {
       super(s);
    }
    
    public ReferenceTrackerEngineFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.ideaconsult.iuclidws.types.Types.ReferenceTrackerEngineFault msg){
       faultMessage = msg;
    }
    
    public org.ideaconsult.iuclidws.types.Types.ReferenceTrackerEngineFault getFaultMessage(){
       return faultMessage;
    }
}
    