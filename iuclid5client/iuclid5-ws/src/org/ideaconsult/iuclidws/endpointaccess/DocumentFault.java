
/**
 * DocumentFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.ideaconsult.iuclidws.endpointaccess;

public class DocumentFault extends java.lang.Exception{
    
    private org.ideaconsult.iuclidws.types.Types.DocumentFault faultMessage;
    
    public DocumentFault() {
        super("DocumentFault");
    }
           
    public DocumentFault(java.lang.String s) {
       super(s);
    }
    
    public DocumentFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.ideaconsult.iuclidws.types.Types.DocumentFault msg){
       faultMessage = msg;
    }
    
    public org.ideaconsult.iuclidws.types.Types.DocumentFault getFaultMessage(){
       return faultMessage;
    }
}
    