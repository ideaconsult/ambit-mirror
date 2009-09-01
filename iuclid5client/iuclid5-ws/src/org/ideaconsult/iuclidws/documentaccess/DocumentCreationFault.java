
/**
 * DocumentCreationFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.ideaconsult.iuclidws.documentaccess;

public class DocumentCreationFault extends java.lang.Exception{
    
    private org.ideaconsult.iuclidws.types.Types.DocumentCreationFault faultMessage;
    
    public DocumentCreationFault() {
        super("DocumentCreationFault");
    }
           
    public DocumentCreationFault(java.lang.String s) {
       super(s);
    }
    
    public DocumentCreationFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.ideaconsult.iuclidws.types.Types.DocumentCreationFault msg){
       faultMessage = msg;
    }
    
    public org.ideaconsult.iuclidws.types.Types.DocumentCreationFault getFaultMessage(){
       return faultMessage;
    }
}
    