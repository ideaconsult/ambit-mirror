
/**
 * EndpointAccessEngineStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
        package org.ideaconsult.iuclidws.endpointaccess;

        

        /*
        *  EndpointAccessEngineStub java implementation
        */

        
        public class EndpointAccessEngineStub extends org.apache.axis2.client.Stub
        {
        protected org.apache.axis2.description.AxisOperation[] _operations;

        //hashmaps to keep the fault mapping
        private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();
        private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();
        private java.util.HashMap faultMessageMap = new java.util.HashMap();

        private static int counter = 0;

        private static synchronized String getUniqueSuffix(){
            // reset the counter if it is greater than 99999
            if (counter > 99999){
                counter = 0;
            }
            counter = counter + 1; 
            return Long.toString(System.currentTimeMillis()) + "_" + counter;
        }

    
    private void populateAxisService() throws org.apache.axis2.AxisFault {

     //creating the Service with a unique name
     _service = new org.apache.axis2.description.AxisService("EndpointAccessEngine" + getUniqueSuffix());
     addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[11];
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "copyEndpoint"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[0]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "deleteEndpoint"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[1]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "referenceEndpoint"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[2]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "referenceEndpoints"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[3]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "updateEndpoint"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[4]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "copyEndpoints"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[5]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "deleteEndpoints"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[6]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "createNewEndpoint"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[7]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "createNewEndpointSummary"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[8]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "detachEndpoint"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[9]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "renameEndpoint"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[10]=__operation;
            
        
        }

    //populates the faults
    private void populateFaults(){
         
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.endpointaccess.DocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentFault"),"org.ideaconsult.iuclidws.types.Types$DocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","EndpointAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$EndpointAccessEngineNotAvailableFault");
           


    }

    /**
      *Constructor that takes in a configContext
      */

    public EndpointAccessEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
       java.lang.String targetEndpoint)
       throws org.apache.axis2.AxisFault {
         this(configurationContext,targetEndpoint,false);
   }


   /**
     * Constructor that takes in a configContext  and useseperate listner
     */
   public EndpointAccessEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
        java.lang.String targetEndpoint, boolean useSeparateListener)
        throws org.apache.axis2.AxisFault {
         //To populate AxisService
         populateAxisService();
         populateFaults();

        _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext,_service);
        
	
        configurationContext = _serviceClient.getServiceContext().getConfigurationContext();

        _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(
                targetEndpoint));
        _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);
        
            //Set the soap version
            _serviceClient.getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        
    
    }

    /**
     * Default Constructor
     */
    public EndpointAccessEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {
        
                    this(configurationContext,"http://192.168.1.11:8080/i5wsruntime/services/EndpointAccessEngine/" );
                
    }

    /**
     * Default Constructor
     */
    public EndpointAccessEngineStub() throws org.apache.axis2.AxisFault {
        
                    this("http://192.168.1.11:8080/i5wsruntime/services/EndpointAccessEngine/" );
                
    }

    /**
     * Constructor taking the target endpoint
     */
    public EndpointAccessEngineStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null,targetEndpoint);
    }



        
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#copyEndpoint
                     * @param copyEndpoint
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CopyEndpointResponse copyEndpoint(

                            org.ideaconsult.iuclidws.types.Types.CopyEndpoint copyEndpoint)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/CopyEndpoint");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    copyEndpoint,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "copyEndpoint")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.CopyEndpointResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CopyEndpointResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#deleteEndpoint
                     * @param deleteEndpoint
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.DeleteEndpointResponse deleteEndpoint(

                            org.ideaconsult.iuclidws.types.Types.DeleteEndpoint deleteEndpoint)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/DeleteEndpoint");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    deleteEndpoint,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "deleteEndpoint")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.DeleteEndpointResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.DeleteEndpointResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#referenceEndpoint
                     * @param referenceEndpoint
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.ReferenceEndpointResponse referenceEndpoint(

                            org.ideaconsult.iuclidws.types.Types.ReferenceEndpoint referenceEndpoint)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/ReferenceEndpoint");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    referenceEndpoint,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "referenceEndpoint")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.ReferenceEndpointResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.ReferenceEndpointResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#referenceEndpoints
                     * @param referenceEndpoints
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.ReferenceEndpointsResponse referenceEndpoints(

                            org.ideaconsult.iuclidws.types.Types.ReferenceEndpoints referenceEndpoints)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[3].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/ReferenceEndpoints");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    referenceEndpoints,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "referenceEndpoints")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.ReferenceEndpointsResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.ReferenceEndpointsResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#updateEndpoint
                     * @param updateEndpoint
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.UpdateEndpointResponse updateEndpoint(

                            org.ideaconsult.iuclidws.types.Types.UpdateEndpoint updateEndpoint)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[4].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/UpdateEndpoint");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    updateEndpoint,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "updateEndpoint")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.UpdateEndpointResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.UpdateEndpointResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#copyEndpoints
                     * @param copyEndpoints
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CopyEndpointsResponse copyEndpoints(

                            org.ideaconsult.iuclidws.types.Types.CopyEndpoints copyEndpoints)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[5].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/CopyEndpoints");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    copyEndpoints,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "copyEndpoints")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.CopyEndpointsResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CopyEndpointsResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#deleteEndpoints
                     * @param deleteEndpoints
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.DeleteEndpointsResponse deleteEndpoints(

                            org.ideaconsult.iuclidws.types.Types.DeleteEndpoints deleteEndpoints)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[6].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/DeleteEndpoints");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    deleteEndpoints,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "deleteEndpoints")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.DeleteEndpointsResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.DeleteEndpointsResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#createNewEndpoint
                     * @param createNewEndpoint
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CreateNewEndpointResponse createNewEndpoint(

                            org.ideaconsult.iuclidws.types.Types.CreateNewEndpoint createNewEndpoint)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[7].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/CreateNewEndpoint");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    createNewEndpoint,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "createNewEndpoint")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.CreateNewEndpointResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CreateNewEndpointResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#createNewEndpointSummary
                     * @param createNewEndpointSummary
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummaryResponse createNewEndpointSummary(

                            org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummary createNewEndpointSummary)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[8].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/CreateNewEndpointSummary");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    createNewEndpointSummary,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "createNewEndpointSummary")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummaryResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummaryResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#detachEndpoint
                     * @param detachEndpoint
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.DetachEndpointResponse detachEndpoint(

                            org.ideaconsult.iuclidws.types.Types.DetachEndpoint detachEndpoint)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[9].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/DetachEndpoint");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    detachEndpoint,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "detachEndpoint")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.DetachEndpointResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.DetachEndpointResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngine#renameEndpoint
                     * @param renameEndpoint
                    
                     * @throws org.ideaconsult.iuclidws.endpointaccess.DocumentFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.RenameEndpointResponse renameEndpoint(

                            org.ideaconsult.iuclidws.types.Types.RenameEndpoint renameEndpoint)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.endpointaccess.DocumentFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault
                        ,org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[10].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/EndpointAccessEngine/RenameEndpoint");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    renameEndpoint,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "renameEndpoint")));
                                                
        //adding SOAP soap_headers
         _serviceClient.addHeadersToEnvelope(env);
        // set the message context with that soap envelope
        _messageContext.setEnvelope(env);

        // add the message contxt to the operation client
        _operationClient.addMessageContext(_messageContext);

        //execute the operation client
        _operationClient.execute(true);

         
               org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                                           org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
                
                
                                java.lang.Object object = fromOM(
                                             _returnEnv.getBody().getFirstElement() ,
                                             org.ideaconsult.iuclidws.types.Types.RenameEndpointResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.RenameEndpointResponse)object;
                                   
         }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                                (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                   new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.DocumentFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.DocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.endpointaccess.EndpointAccessEngineNotAvailableFault)ex;
                        }
                        

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                       // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
            } finally {
                _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            }
        }
            


       /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
       private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
        }
       return returnMap;
    }

    
    
    private javax.xml.namespace.QName[] opNameArray = null;
    private boolean optimizeContent(javax.xml.namespace.QName opName) {
        

        if (opNameArray == null) {
            return false;
        }
        for (int i = 0; i < opNameArray.length; i++) {
            if (opName.equals(opNameArray[i])) {
                return true;   
            }
        }
        return false;
    }
           
          
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CopyEndpoint param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CopyEndpoint.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CopyEndpointResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CopyEndpointResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DocumentFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DocumentFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteEndpoint param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteEndpoint.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteEndpointResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteEndpointResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ReferenceEndpoint param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ReferenceEndpoint.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ReferenceEndpointResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ReferenceEndpointResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ReferenceEndpoints param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ReferenceEndpoints.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ReferenceEndpointsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ReferenceEndpointsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.UpdateEndpoint param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.UpdateEndpoint.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.UpdateEndpointResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.UpdateEndpointResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CopyEndpoints param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CopyEndpoints.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CopyEndpointsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CopyEndpointsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteEndpoints param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteEndpoints.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteEndpointsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteEndpointsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CreateNewEndpoint param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateNewEndpoint.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CreateNewEndpointResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateNewEndpointResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummary param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummary.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummaryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummaryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DetachEndpoint param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DetachEndpoint.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DetachEndpointResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DetachEndpointResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.RenameEndpoint param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.RenameEndpoint.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.RenameEndpointResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.RenameEndpointResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CopyEndpoint param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CopyEndpoint.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.DeleteEndpoint param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteEndpoint.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.ReferenceEndpoint param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.ReferenceEndpoint.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.ReferenceEndpoints param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.ReferenceEndpoints.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.UpdateEndpoint param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.UpdateEndpoint.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CopyEndpoints param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CopyEndpoints.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.DeleteEndpoints param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteEndpoints.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CreateNewEndpoint param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateNewEndpoint.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummary param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummary.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.DetachEndpoint param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.DetachEndpoint.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.RenameEndpoint param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.RenameEndpoint.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             


        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
        return factory.getDefaultEnvelope();
        }


        private  java.lang.Object fromOM(
        org.apache.axiom.om.OMElement param,
        java.lang.Class type,
        java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{

        try {
        
                if (org.ideaconsult.iuclidws.types.Types.CopyEndpoint.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CopyEndpoint.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CopyEndpointResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CopyEndpointResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteEndpoint.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteEndpoint.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteEndpointResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteEndpointResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ReferenceEndpoint.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ReferenceEndpoint.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ReferenceEndpointResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ReferenceEndpointResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ReferenceEndpoints.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ReferenceEndpoints.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ReferenceEndpointsResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ReferenceEndpointsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.UpdateEndpoint.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.UpdateEndpoint.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.UpdateEndpointResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.UpdateEndpointResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CopyEndpoints.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CopyEndpoints.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CopyEndpointsResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CopyEndpointsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteEndpoints.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteEndpoints.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteEndpointsResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteEndpointsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CreateNewEndpoint.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CreateNewEndpoint.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CreateNewEndpointResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CreateNewEndpointResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummary.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummary.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummaryResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummaryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DetachEndpoint.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DetachEndpoint.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DetachEndpointResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DetachEndpointResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.RenameEndpoint.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.RenameEndpoint.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.RenameEndpointResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.RenameEndpointResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.EndpointAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    
   }
   
