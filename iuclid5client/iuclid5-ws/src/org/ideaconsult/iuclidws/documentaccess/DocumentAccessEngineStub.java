
/**
 * DocumentAccessEngineStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
        package org.ideaconsult.iuclidws.documentaccess;

        

        /*
        *  DocumentAccessEngineStub java implementation
        */

        
        public class DocumentAccessEngineStub extends org.apache.axis2.client.Stub
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
     _service = new org.apache.axis2.description.AxisService("DocumentAccessEngine" + getUniqueSuffix());
     addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[10];
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "closeDocument"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[0]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "determineDocumentType"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[1]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "createDocument"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[2]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "compareDocuments"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[3]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "deleteDocument"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[4]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "existsDocument"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[5]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "fetchDocument"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[6]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "openDocumentForReading"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[7]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "openDocumentForWriting"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[8]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "commitDocument"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[9]=__operation;
            
        
        }

    //populates the faults
    private void populateFaults(){
         
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.types.Types$DocumentNotFoundFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.types.Types$DocumentNotFoundFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentCreationFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentCreationFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentCreationFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentCreationFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentCreationFault"),"org.ideaconsult.iuclidws.types.Types$DocumentCreationFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.types.Types$DocumentNotFoundFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.types.Types$DocumentNotFoundFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.types.Types$DocumentNotFoundFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.types.Types$DocumentNotFoundFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.types.Types$DocumentNotFoundFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessEngineFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentAccessFault"),"org.ideaconsult.iuclidws.types.Types$DocumentAccessFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","DocumentNotFoundFault"),"org.ideaconsult.iuclidws.types.Types$DocumentNotFoundFault");
           


    }

    /**
      *Constructor that takes in a configContext
      */

    public DocumentAccessEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
       java.lang.String targetEndpoint)
       throws org.apache.axis2.AxisFault {
         this(configurationContext,targetEndpoint,false);
   }


   /**
     * Constructor that takes in a configContext  and useseperate listner
     */
   public DocumentAccessEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
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
    public DocumentAccessEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {
        
                    this(configurationContext,"http://192.168.1.11:8080/i5wsruntime/services/DocumentAccessEngine/" );
                
    }

    /**
     * Default Constructor
     */
    public DocumentAccessEngineStub() throws org.apache.axis2.AxisFault {
        
                    this("http://192.168.1.11:8080/i5wsruntime/services/DocumentAccessEngine/" );
                
    }

    /**
     * Constructor taking the target endpoint
     */
    public DocumentAccessEngineStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null,targetEndpoint);
    }



        
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#closeDocument
                     * @param closeDocument
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CloseDocumentResponse closeDocument(

                            org.ideaconsult.iuclidws.types.Types.CloseDocument closeDocument)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/CloseDocument");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    closeDocument,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "closeDocument")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.CloseDocumentResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CloseDocumentResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#determineDocumentType
                     * @param determineDocumentType
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.DetermineDocumentTypeResponse determineDocumentType(

                            org.ideaconsult.iuclidws.types.Types.DetermineDocumentType determineDocumentType)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/DetermineDocumentType");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    determineDocumentType,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "determineDocumentType")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.DetermineDocumentTypeResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.DetermineDocumentTypeResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#createDocument
                     * @param createDocument
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentCreationFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CreateDocumentResponse createDocument(

                            org.ideaconsult.iuclidws.types.Types.CreateDocument createDocument)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentCreationFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/CreateDocument");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    createDocument,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "createDocument")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.CreateDocumentResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CreateDocumentResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentCreationFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentCreationFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#compareDocuments
                     * @param compareDocuments
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CompareDocumentsResponse compareDocuments(

                            org.ideaconsult.iuclidws.types.Types.CompareDocuments compareDocuments)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[3].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/CompareDocuments");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    compareDocuments,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "compareDocuments")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.CompareDocumentsResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CompareDocumentsResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#deleteDocument
                     * @param deleteDocument
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.DeleteDocumentResponse deleteDocument(

                            org.ideaconsult.iuclidws.types.Types.DeleteDocument deleteDocument)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[4].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/DeleteDocument");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    deleteDocument,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "deleteDocument")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.DeleteDocumentResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.DeleteDocumentResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#existsDocument
                     * @param existsDocument
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.ExistsDocumentResponse existsDocument(

                            org.ideaconsult.iuclidws.types.Types.ExistsDocument existsDocument)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[5].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/ExistsDocument");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    existsDocument,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "existsDocument")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.ExistsDocumentResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.ExistsDocumentResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#fetchDocument
                     * @param fetchDocument
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.FetchDocumentResponse fetchDocument(

                            org.ideaconsult.iuclidws.types.Types.FetchDocument fetchDocument)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[6].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/FetchDocument");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    fetchDocument,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "fetchDocument")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.FetchDocumentResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.FetchDocumentResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#openDocumentForReading
                     * @param openDocumentForReading
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.OpenDocumentForReadingResponse openDocumentForReading(

                            org.ideaconsult.iuclidws.types.Types.OpenDocumentForReading openDocumentForReading)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[7].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/OpenDocumentForReading");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    openDocumentForReading,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "openDocumentForReading")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.OpenDocumentForReadingResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.OpenDocumentForReadingResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#openDocumentForWriting
                     * @param openDocumentForWriting
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.OpenDocumentForWritingResponse openDocumentForWriting(

                            org.ideaconsult.iuclidws.types.Types.OpenDocumentForWriting openDocumentForWriting)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[8].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/OpenDocumentForWriting");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    openDocumentForWriting,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "openDocumentForWriting")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.OpenDocumentForWritingResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.OpenDocumentForWritingResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault)ex;
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
                     * @see org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine#commitDocument
                     * @param commitDocument
                    
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault : 
                     * @throws org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CommitDocumentResponse commitDocument(

                            org.ideaconsult.iuclidws.types.Types.CommitDocument commitDocument)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault
                        ,org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[9].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/DocumentAccessEngine/CommitDocument");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    commitDocument,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "commitDocument")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.CommitDocumentResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CommitDocumentResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault){
                          throw (org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault)ex;
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
           
          
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CloseDocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CloseDocument.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CloseDocumentResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CloseDocumentResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DocumentAccessFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DetermineDocumentType param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DetermineDocumentType.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DetermineDocumentTypeResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DetermineDocumentTypeResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CreateDocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateDocument.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CreateDocumentResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateDocumentResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DocumentCreationFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DocumentCreationFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CompareDocuments param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CompareDocuments.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CompareDocumentsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CompareDocumentsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteDocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteDocument.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteDocumentResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteDocumentResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ExistsDocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExistsDocument.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ExistsDocumentResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExistsDocumentResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.FetchDocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.FetchDocument.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.FetchDocumentResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.FetchDocumentResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.OpenDocumentForReading param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.OpenDocumentForReading.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.OpenDocumentForReadingResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.OpenDocumentForReadingResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.OpenDocumentForWriting param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.OpenDocumentForWriting.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.OpenDocumentForWritingResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.OpenDocumentForWritingResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CommitDocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CommitDocument.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CommitDocumentResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CommitDocumentResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CloseDocument param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CloseDocument.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.DetermineDocumentType param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.DetermineDocumentType.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CreateDocument param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CreateDocument.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CompareDocuments param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CompareDocuments.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.DeleteDocument param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteDocument.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.ExistsDocument param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExistsDocument.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.FetchDocument param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.FetchDocument.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.OpenDocumentForReading param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.OpenDocumentForReading.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.OpenDocumentForWriting param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.OpenDocumentForWriting.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CommitDocument param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CommitDocument.MY_QNAME,factory));
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
        
                if (org.ideaconsult.iuclidws.types.Types.CloseDocument.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CloseDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CloseDocumentResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CloseDocumentResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DetermineDocumentType.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DetermineDocumentType.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DetermineDocumentTypeResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DetermineDocumentTypeResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CreateDocument.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CreateDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CreateDocumentResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CreateDocumentResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentCreationFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentCreationFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CompareDocuments.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CompareDocuments.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CompareDocumentsResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CompareDocumentsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteDocument.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteDocumentResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteDocumentResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ExistsDocument.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ExistsDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ExistsDocumentResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ExistsDocumentResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.FetchDocument.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.FetchDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.FetchDocumentResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.FetchDocumentResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.OpenDocumentForReading.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.OpenDocumentForReading.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.OpenDocumentForReadingResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.OpenDocumentForReadingResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.OpenDocumentForWriting.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.OpenDocumentForWriting.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.OpenDocumentForWritingResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.OpenDocumentForWritingResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CommitDocument.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CommitDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.CommitDocumentResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.CommitDocumentResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentAccessFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DocumentNotFoundFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    
   }
   
