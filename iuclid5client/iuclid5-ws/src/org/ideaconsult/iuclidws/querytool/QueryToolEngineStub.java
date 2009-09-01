
/**
 * QueryToolEngineStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
        package org.ideaconsult.iuclidws.querytool;

        

        /*
        *  QueryToolEngineStub java implementation
        */

        
        public class QueryToolEngineStub extends org.apache.axis2.client.Stub
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
     _service = new org.apache.axis2.description.AxisService("QueryToolEngine" + getUniqueSuffix());
     addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[2];
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "executeQuery"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[0]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "countQuery"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[1]=__operation;
            
        
        }

    //populates the faults
    private void populateFaults(){
         
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineNotAvailableFault"),"org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineNotAvailableFault"),"org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$QueryToolEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineFault"),"org.ideaconsult.iuclidws.querytool.QueryToolEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineFault"),"org.ideaconsult.iuclidws.querytool.QueryToolEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineFault"),"org.ideaconsult.iuclidws.types.Types$QueryToolEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineNotAvailableFault"),"org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineNotAvailableFault"),"org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$QueryToolEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineFault"),"org.ideaconsult.iuclidws.querytool.QueryToolEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineFault"),"org.ideaconsult.iuclidws.querytool.QueryToolEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","QueryToolEngineFault"),"org.ideaconsult.iuclidws.types.Types$QueryToolEngineFault");
           


    }

    /**
      *Constructor that takes in a configContext
      */

    public QueryToolEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
       java.lang.String targetEndpoint)
       throws org.apache.axis2.AxisFault {
         this(configurationContext,targetEndpoint,false);
   }


   /**
     * Constructor that takes in a configContext  and useseperate listner
     */
   public QueryToolEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
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
    public QueryToolEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {
        
                    this(configurationContext,"http://192.168.1.11:8080/i5wsruntime/services/QueryToolEngine/" );
                
    }

    /**
     * Default Constructor
     */
    public QueryToolEngineStub() throws org.apache.axis2.AxisFault {
        
                    this("http://192.168.1.11:8080/i5wsruntime/services/QueryToolEngine/" );
                
    }

    /**
     * Constructor taking the target endpoint
     */
    public QueryToolEngineStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null,targetEndpoint);
    }



        
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.querytool.QueryToolEngine#executeQuery
                     * @param executeQueryExpression
                    
                     * @throws org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.querytool.QueryToolEngineFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpressionResponse executeQuery(

                            org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpression executeQueryExpression)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.querytool.QueryToolEngineFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/QueryToolEngine/ExecuteQuery");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    executeQueryExpression,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "executeQuery")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpressionResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpressionResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.querytool.QueryToolEngineFault){
                          throw (org.ideaconsult.iuclidws.querytool.QueryToolEngineFault)ex;
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
                     * @see org.ideaconsult.iuclidws.querytool.QueryToolEngine#countQuery
                     * @param countQueryExpression
                    
                     * @throws org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.querytool.QueryToolEngineFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.CountQueryExpressionResponse countQuery(

                            org.ideaconsult.iuclidws.types.Types.CountQueryExpression countQueryExpression)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.querytool.QueryToolEngineFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/QueryToolEngine/CountQuery");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    countQueryExpression,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "countQuery")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.CountQueryExpressionResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.CountQueryExpressionResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.querytool.QueryToolEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.querytool.QueryToolEngineFault){
                          throw (org.ideaconsult.iuclidws.querytool.QueryToolEngineFault)ex;
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
    
        
        
        
        private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpression param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpression.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpressionResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpressionResponse.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.QueryToolEngineNotAvailableFault param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(org.ideaconsult.iuclidws.types.Types.QueryToolEngineNotAvailableFault.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.QueryToolEngineFault param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(org.ideaconsult.iuclidws.types.Types.QueryToolEngineFault.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CountQueryExpression param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CountQueryExpression.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
        private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.CountQueryExpressionResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {

        
                    try{
                         return param.getOMElement(org.ideaconsult.iuclidws.types.Types.CountQueryExpressionResponse.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                

        }
    
                                
                                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpression param, boolean optimizeContent)
                                    throws org.apache.axis2.AxisFault{

                                         
                                                try{

                                                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                        emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpression.MY_QNAME,factory));
                                                        return emptyEnvelope;
                                                    } catch(org.apache.axis2.databinding.ADBException e){
                                                        throw org.apache.axis2.AxisFault.makeFault(e);
                                                    }
                                            

                                    }
                            
                         
                         /* methods to provide back word compatibility */

                         
                                
                                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.CountQueryExpression param, boolean optimizeContent)
                                    throws org.apache.axis2.AxisFault{

                                         
                                                try{

                                                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                        emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.CountQueryExpression.MY_QNAME,factory));
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
    
            if (org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpression.class.equals(type)){
            
                       return org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpression.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
       
            if (org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpressionResponse.class.equals(type)){
            
                       return org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpressionResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
       
            if (org.ideaconsult.iuclidws.types.Types.QueryToolEngineNotAvailableFault.class.equals(type)){
            
                       return org.ideaconsult.iuclidws.types.Types.QueryToolEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
       
            if (org.ideaconsult.iuclidws.types.Types.QueryToolEngineFault.class.equals(type)){
            
                       return org.ideaconsult.iuclidws.types.Types.QueryToolEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
       
            if (org.ideaconsult.iuclidws.types.Types.CountQueryExpression.class.equals(type)){
            
                       return org.ideaconsult.iuclidws.types.Types.CountQueryExpression.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
       
            if (org.ideaconsult.iuclidws.types.Types.CountQueryExpressionResponse.class.equals(type)){
            
                       return org.ideaconsult.iuclidws.types.Types.CountQueryExpressionResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
       
            if (org.ideaconsult.iuclidws.types.Types.QueryToolEngineNotAvailableFault.class.equals(type)){
            
                       return org.ideaconsult.iuclidws.types.Types.QueryToolEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
       
            if (org.ideaconsult.iuclidws.types.Types.QueryToolEngineFault.class.equals(type)){
            
                       return org.ideaconsult.iuclidws.types.Types.QueryToolEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                

            }
       
    } catch (java.lang.Exception e) {
    throw org.apache.axis2.AxisFault.makeFault(e);
    }
       return null;
    }




}
