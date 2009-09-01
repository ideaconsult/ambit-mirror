
/**
 * ContainerEngineStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
        package org.ideaconsult.iuclidws.container;

        

        /*
        *  ContainerEngineStub java implementation
        */

        
        public class ContainerEngineStub extends org.apache.axis2.client.Stub
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
     _service = new org.apache.axis2.description.AxisService("ContainerEngine" + getUniqueSuffix());
     addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[3];
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "downloadContainer"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[0]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "importExternalContainer"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[1]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "uploadContainer"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[2]=__operation;
            
        
        }

    //populates the faults
    private void populateFaults(){
         
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$ContainerEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.container.ContainerEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.container.ContainerEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.types.Types$ContainerEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ExportDocumentFault"),"org.ideaconsult.iuclidws.container.ExportDocumentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ExportDocumentFault"),"org.ideaconsult.iuclidws.container.ExportDocumentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ExportDocumentFault"),"org.ideaconsult.iuclidws.types.Types$ExportDocumentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","OutOfDiskSpaceFault"),"org.ideaconsult.iuclidws.container.OutOfDiskSpaceFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","OutOfDiskSpaceFault"),"org.ideaconsult.iuclidws.container.OutOfDiskSpaceFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","OutOfDiskSpaceFault"),"org.ideaconsult.iuclidws.types.Types$OutOfDiskSpaceFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerContentFault"),"org.ideaconsult.iuclidws.container.ContainerContentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerContentFault"),"org.ideaconsult.iuclidws.container.ContainerContentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerContentFault"),"org.ideaconsult.iuclidws.types.Types$ContainerContentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$ContainerEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","IllegalContentTypeFault"),"org.ideaconsult.iuclidws.container.IllegalContentTypeFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","IllegalContentTypeFault"),"org.ideaconsult.iuclidws.container.IllegalContentTypeFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","IllegalContentTypeFault"),"org.ideaconsult.iuclidws.types.Types$IllegalContentTypeFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.container.ContainerEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.container.ContainerEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.types.Types$ContainerEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerContentFault"),"org.ideaconsult.iuclidws.container.ContainerContentFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerContentFault"),"org.ideaconsult.iuclidws.container.ContainerContentFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerContentFault"),"org.ideaconsult.iuclidws.types.Types$ContainerContentFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$ContainerEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","IllegalContentTypeFault"),"org.ideaconsult.iuclidws.container.IllegalContentTypeFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","IllegalContentTypeFault"),"org.ideaconsult.iuclidws.container.IllegalContentTypeFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","IllegalContentTypeFault"),"org.ideaconsult.iuclidws.types.Types$IllegalContentTypeFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.container.ContainerEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.container.ContainerEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","ContainerEngineFault"),"org.ideaconsult.iuclidws.types.Types$ContainerEngineFault");
           


    }

    /**
      *Constructor that takes in a configContext
      */

    public ContainerEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
       java.lang.String targetEndpoint)
       throws org.apache.axis2.AxisFault {
         this(configurationContext,targetEndpoint,false);
   }


   /**
     * Constructor that takes in a configContext  and useseperate listner
     */
   public ContainerEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
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
    public ContainerEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {
        
                    this(configurationContext,"http://192.168.1.11:8080/i5wsruntime/services/ContainerEngine/" );
                
    }

    /**
     * Default Constructor
     */
    public ContainerEngineStub() throws org.apache.axis2.AxisFault {
        
                    this("http://192.168.1.11:8080/i5wsruntime/services/ContainerEngine/" );
                
    }

    /**
     * Constructor taking the target endpoint
     */
    public ContainerEngineStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null,targetEndpoint);
    }



        
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.container.ContainerEngine#downloadContainer
                     * @param downloadContainer
                    
                     * @throws org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.container.ContainerEngineFault : 
                     * @throws org.ideaconsult.iuclidws.container.ExportDocumentFault : 
                     * @throws org.ideaconsult.iuclidws.container.OutOfDiskSpaceFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.DownloadContainerResponse downloadContainer(

                            org.ideaconsult.iuclidws.types.Types.DownloadContainerE downloadContainer)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.container.ContainerEngineFault
                        ,org.ideaconsult.iuclidws.container.ExportDocumentFault
                        ,org.ideaconsult.iuclidws.container.OutOfDiskSpaceFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/ContainerEngine/DownloadContainer");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    downloadContainer,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "downloadContainer")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.DownloadContainerResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.DownloadContainerResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ContainerEngineFault){
                          throw (org.ideaconsult.iuclidws.container.ContainerEngineFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ExportDocumentFault){
                          throw (org.ideaconsult.iuclidws.container.ExportDocumentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.OutOfDiskSpaceFault){
                          throw (org.ideaconsult.iuclidws.container.OutOfDiskSpaceFault)ex;
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
                     * @see org.ideaconsult.iuclidws.container.ContainerEngine#importExternalContainer
                     * @param importExternalContainer
                    
                     * @throws org.ideaconsult.iuclidws.container.ContainerContentFault : 
                     * @throws org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.container.IllegalContentTypeFault : 
                     * @throws org.ideaconsult.iuclidws.container.ContainerEngineFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.ImportExternalContainerResponse importExternalContainer(

                            org.ideaconsult.iuclidws.types.Types.ImportExternalContainer importExternalContainer)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.container.ContainerContentFault
                        ,org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.container.IllegalContentTypeFault
                        ,org.ideaconsult.iuclidws.container.ContainerEngineFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/ContainerEngine/ImportContainer");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    importExternalContainer,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "importExternalContainer")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.ImportExternalContainerResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.ImportExternalContainerResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ContainerContentFault){
                          throw (org.ideaconsult.iuclidws.container.ContainerContentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.IllegalContentTypeFault){
                          throw (org.ideaconsult.iuclidws.container.IllegalContentTypeFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ContainerEngineFault){
                          throw (org.ideaconsult.iuclidws.container.ContainerEngineFault)ex;
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
                     * @see org.ideaconsult.iuclidws.container.ContainerEngine#uploadContainer
                     * @param uploadContainer
                    
                     * @throws org.ideaconsult.iuclidws.container.ContainerContentFault : 
                     * @throws org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.container.IllegalContentTypeFault : 
                     * @throws org.ideaconsult.iuclidws.container.ContainerEngineFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.UploadContainerResponse uploadContainer(

                            org.ideaconsult.iuclidws.types.Types.UploadContainer uploadContainer)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.container.ContainerContentFault
                        ,org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.container.IllegalContentTypeFault
                        ,org.ideaconsult.iuclidws.container.ContainerEngineFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/ContainerEngine/UploadContainer");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    uploadContainer,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "uploadContainer")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.UploadContainerResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.UploadContainerResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ContainerContentFault){
                          throw (org.ideaconsult.iuclidws.container.ContainerContentFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.container.ContainerEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.IllegalContentTypeFault){
                          throw (org.ideaconsult.iuclidws.container.IllegalContentTypeFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.container.ContainerEngineFault){
                          throw (org.ideaconsult.iuclidws.container.ContainerEngineFault)ex;
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
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DownloadContainerE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DownloadContainerE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.DownloadContainerResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.DownloadContainerResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ContainerEngineNotAvailableFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ContainerEngineNotAvailableFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ContainerEngineFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ContainerEngineFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ExportDocumentFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExportDocumentFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.OutOfDiskSpaceFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.OutOfDiskSpaceFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ImportExternalContainer param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ImportExternalContainer.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ImportExternalContainerResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ImportExternalContainerResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ContainerContentFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ContainerContentFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.IllegalContentTypeFault param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.IllegalContentTypeFault.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.UploadContainer param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.UploadContainer.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.UploadContainerResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.ideaconsult.iuclidws.types.Types.UploadContainerResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.DownloadContainerE param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.DownloadContainerE.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.ImportExternalContainer param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.ImportExternalContainer.MY_QNAME,factory));
                                                            return emptyEnvelope;
                                                        } catch(org.apache.axis2.databinding.ADBException e){
                                                            throw org.apache.axis2.AxisFault.makeFault(e);
                                                        }
                                                

                                        }
                                
                             
                             /* methods to provide back word compatibility */

                             
                                    
                                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.UploadContainer param, boolean optimizeContent)
                                        throws org.apache.axis2.AxisFault{

                                             
                                                    try{

                                                            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                            emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.UploadContainer.MY_QNAME,factory));
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
        
                if (org.ideaconsult.iuclidws.types.Types.DownloadContainerE.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DownloadContainerE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.DownloadContainerResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.DownloadContainerResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ContainerEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ContainerEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ContainerEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ContainerEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ExportDocumentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ExportDocumentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.OutOfDiskSpaceFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.OutOfDiskSpaceFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ImportExternalContainer.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ImportExternalContainer.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ImportExternalContainerResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ImportExternalContainerResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ContainerContentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ContainerContentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ContainerEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ContainerEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.IllegalContentTypeFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.IllegalContentTypeFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ContainerEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ContainerEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.UploadContainer.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.UploadContainer.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.UploadContainerResponse.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.UploadContainerResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ContainerContentFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ContainerContentFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ContainerEngineNotAvailableFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ContainerEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.IllegalContentTypeFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.IllegalContentTypeFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.ideaconsult.iuclidws.types.Types.ContainerEngineFault.class.equals(type)){
                
                           return org.ideaconsult.iuclidws.types.Types.ContainerEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    
   }
   
