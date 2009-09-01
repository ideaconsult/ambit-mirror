
/**
 * InventoryEngineStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
        package org.ideaconsult.iuclidws.inventory;

        

        /*
        *  InventoryEngineStub java implementation
        */

        
        public class InventoryEngineStub extends org.apache.axis2.client.Stub
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
     _service = new org.apache.axis2.description.AxisService("InventoryEngine" + getUniqueSuffix());
     addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[6];
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "putLiteratureInventoryEntry"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[0]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "deleteLiteratureInventoryEntries"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[1]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "getLiteratureInventoryQuery"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[2]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "putLiteratureInventoryEntries"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[3]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "executeLiteratureInventoryQuery"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[4]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "deleteLiteratureInventoryEntry"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[5]=__operation;
            
        
        }

    //populates the faults
    private void populateFaults(){
         
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","InventoryEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$InventoryEngineNotAvailableFault");
           


    }

    /**
      *Constructor that takes in a configContext
      */

    public InventoryEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
       java.lang.String targetEndpoint)
       throws org.apache.axis2.AxisFault {
         this(configurationContext,targetEndpoint,false);
   }


   /**
     * Constructor that takes in a configContext  and useseperate listner
     */
   public InventoryEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
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
    public InventoryEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {
        
                    this(configurationContext,"http://192.168.1.11:8080/i5wsruntime/services/InventoryEngine/" );
                
    }

    /**
     * Default Constructor
     */
    public InventoryEngineStub() throws org.apache.axis2.AxisFault {
        
                    this("http://192.168.1.11:8080/i5wsruntime/services/InventoryEngine/" );
                
    }

    /**
     * Constructor taking the target endpoint
     */
    public InventoryEngineStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null,targetEndpoint);
    }



        
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.inventory.InventoryEngine#putLiteratureInventoryEntry
                     * @param putLiteratureInventoryEntry
                    
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineFault : 
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntryResponse putLiteratureInventoryEntry(

                            org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntry putLiteratureInventoryEntry)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineFault
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/InventoryEngine/PutLiteratureInventoryEntry");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    putLiteratureInventoryEntry,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "putLiteratureInventoryEntry")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntryResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntryResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault)ex;
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
                     * @see org.ideaconsult.iuclidws.inventory.InventoryEngine#deleteLiteratureInventoryEntries
                     * @param deleteLiteratureInventoryEntries
                    
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineFault : 
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntriesResponse deleteLiteratureInventoryEntries(

                            org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntries deleteLiteratureInventoryEntries)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineFault
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/InventoryEngine/DeleteLiteratureInventoryEntries");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    deleteLiteratureInventoryEntries,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "deleteLiteratureInventoryEntries")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntriesResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntriesResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault)ex;
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
                     * @see org.ideaconsult.iuclidws.inventory.InventoryEngine#getLiteratureInventoryQuery
                     * @param getLiteratureInventoryQuery
                    
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineFault : 
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQueryResponse getLiteratureInventoryQuery(

                            org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQuery getLiteratureInventoryQuery)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineFault
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/InventoryEngine/GetLiteratureInventoryQuery");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    getLiteratureInventoryQuery,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "getLiteratureInventoryQuery")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQueryResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQueryResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault)ex;
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
                     * @see org.ideaconsult.iuclidws.inventory.InventoryEngine#putLiteratureInventoryEntries
                     * @param putLiteratureInventoryEntries
                    
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineFault : 
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntriesResponse putLiteratureInventoryEntries(

                            org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntries putLiteratureInventoryEntries)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineFault
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[3].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/InventoryEngine/PutLiteratureInventoryEntries");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    putLiteratureInventoryEntries,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "putLiteratureInventoryEntries")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntriesResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntriesResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault)ex;
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
                     * @see org.ideaconsult.iuclidws.inventory.InventoryEngine#executeLiteratureInventoryQuery
                     * @param executeLiteratureInventoryQuery
                    
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineFault : 
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQueryResponse executeLiteratureInventoryQuery(

                            org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQuery executeLiteratureInventoryQuery)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineFault
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[4].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/InventoryEngine/ExecuteLiteratureInventoryQuery");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    executeLiteratureInventoryQuery,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "executeLiteratureInventoryQuery")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQueryResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQueryResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault)ex;
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
                     * @see org.ideaconsult.iuclidws.inventory.InventoryEngine#deleteLiteratureInventoryEntry
                     * @param deleteLiteratureInventoryEntry
                    
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineFault : 
                     * @throws org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntryResponse deleteLiteratureInventoryEntry(

                            org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntry deleteLiteratureInventoryEntry)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineFault
                        ,org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[5].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/InventoryEngine/DeleteLiteratureInventoryEntry");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    deleteLiteratureInventoryEntry,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "deleteLiteratureInventoryEntry")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntryResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntryResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.inventory.InventoryEngineNotAvailableFault)ex;
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

           
          
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntry param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntry.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.InventoryEngineFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntries param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntries.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntriesResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntriesResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQuery param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQuery.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQueryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQueryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntries param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntries.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntriesResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntriesResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQuery param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQuery.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQueryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQueryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntry param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntry.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntry param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntry.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntries param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntries.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQuery param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQuery.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntries param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntries.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQuery param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQuery.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntry param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntry.MY_QNAME,factory));
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
        
                if (org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntry.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntry.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntryResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntries.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntries.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntriesResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntriesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQuery.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQuery.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQueryResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQueryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntries.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntries.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntriesResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntriesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQuery.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQuery.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQueryResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQueryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntry.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntry.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntryResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.InventoryEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    
   }
   
