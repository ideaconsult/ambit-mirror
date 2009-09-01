
/**
 * InventoryEngineFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.ideaconsult.iuclidws.inventory;

public class InventoryEngineFault extends java.lang.Exception{
    
    private org.ideaconsult.iuclidws.types.Types.InventoryEngineFault faultMessage;
    
    public InventoryEngineFault() {
        super("InventoryEngineFault");
    }
           
    public InventoryEngineFault(java.lang.String s) {
       super(s);
    }
    
    public InventoryEngineFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.ideaconsult.iuclidws.types.Types.InventoryEngineFault msg){
       faultMessage = msg;
    }
    
    public org.ideaconsult.iuclidws.types.Types.InventoryEngineFault getFaultMessage(){
       return faultMessage;
    }
}
    