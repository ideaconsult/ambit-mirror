
/**
 * QueryEngineFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.ideaconsult.iuclidws.query;

public class QueryEngineFault extends java.lang.Exception{
    
    private org.ideaconsult.iuclidws.types.Types.QueryEngineFault faultMessage;
    
    public QueryEngineFault() {
        super("QueryEngineFault");
    }
           
    public QueryEngineFault(java.lang.String s) {
       super(s);
    }
    
    public QueryEngineFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.ideaconsult.iuclidws.types.Types.QueryEngineFault msg){
       faultMessage = msg;
    }
    
    public org.ideaconsult.iuclidws.types.Types.QueryEngineFault getFaultMessage(){
       return faultMessage;
    }
}
    