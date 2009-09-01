
/**
 * SessionEngineStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
        package org.ideaconsult.iuclidws.session;

import org.ideaconsult.iuclidws.types.Types.Logout;
import org.ideaconsult.iuclidws.types.Types.LogoutResponse;

        

        /*
        *  SessionEngineStub java implementation
        */

        
        public class SessionEngineStub extends org.apache.axis2.client.Stub
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
     _service = new org.apache.axis2.description.AxisService("SessionEngine" + getUniqueSuffix());
     addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[2];
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "logout"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[0]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/", "login"));
	    _service.addOperation(__operation);
	    

	    
	    
            _operations[1]=__operation;
            
        
        }

    //populates the faults
    private void populateFaults(){
         
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineNotAvailableFault"),"org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineNotAvailableFault"),"org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$SessionEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineFault"),"org.ideaconsult.iuclidws.session.SessionEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineFault"),"org.ideaconsult.iuclidws.session.SessionEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineFault"),"org.ideaconsult.iuclidws.types.Types$SessionEngineFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineNotAvailableFault"),"org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineNotAvailableFault"),"org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineNotAvailableFault"),"org.ideaconsult.iuclidws.types.Types$SessionEngineNotAvailableFault");
           
              faultExceptionNameMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineFault"),"org.ideaconsult.iuclidws.session.SessionEngineFault");
              faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineFault"),"org.ideaconsult.iuclidws.session.SessionEngineFault");
              faultMessageMap.put( new javax.xml.namespace.QName("http://echa.europa.eu/schemas/iuclid5/i5webservice/types/","SessionEngineFault"),"org.ideaconsult.iuclidws.types.Types$SessionEngineFault");
           


    }

    /**
      *Constructor that takes in a configContext
      */

    public SessionEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
       java.lang.String targetEndpoint)
       throws org.apache.axis2.AxisFault {
         this(configurationContext,targetEndpoint,false);
   }


   /**
     * Constructor that takes in a configContext  and useseperate listner
     */
   public SessionEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext,
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
    public SessionEngineStub(org.apache.axis2.context.ConfigurationContext configurationContext) throws org.apache.axis2.AxisFault {
        
                    this(configurationContext,"http://192.168.1.11:8080/i5wsruntime/services/SessionEngine/" );
                
    }

    /**
     * Default Constructor
     */
    public SessionEngineStub() throws org.apache.axis2.AxisFault {
        
                    this("http://192.168.1.11:8080/i5wsruntime/services/SessionEngine/" );
                
    }

    /**
     * Constructor taking the target endpoint
     */
    public SessionEngineStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null,targetEndpoint);
    }



        
                    /**
                     * Auto generated method signature
                     * 
                     * @see org.ideaconsult.iuclidws.session.SessionEngine#logout
                     * @param logout
                    
                     * @throws org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.session.SessionEngineFault : 
                     */

                    

                            public  LogoutResponse logout(

                            Logout logout)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.session.SessionEngineFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/SessionEngine/Logout");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    logout,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "logout")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.LogoutResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.LogoutResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.session.SessionEngineFault){
                          throw (org.ideaconsult.iuclidws.session.SessionEngineFault)ex;
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
                     * @see org.ideaconsult.iuclidws.session.SessionEngine#login
                     * @param login
                    
                     * @throws org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault : 
                     * @throws org.ideaconsult.iuclidws.session.SessionEngineFault : 
                     */

                    

                            public  org.ideaconsult.iuclidws.types.Types.LoginResponse login(

                            org.ideaconsult.iuclidws.types.Types.Login login)
                        

                    throws java.rmi.RemoteException
                    
                    
                        ,org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault
                        ,org.ideaconsult.iuclidws.session.SessionEngineFault{
              org.apache.axis2.context.MessageContext _messageContext = null;
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
              _operationClient.getOptions().setAction("http://echa.europa.eu/iuclid5/i5webservice/service/SessionEngine/Login");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              
              
                  addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");
              

              // create a message context
              _messageContext = new org.apache.axis2.context.MessageContext();

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    login,
                                                    optimizeContent(new javax.xml.namespace.QName("http://echa.europa.eu/iuclid5/i5webservice/service/",
                                                    "login")));
                                                
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
                                             org.ideaconsult.iuclidws.types.Types.LoginResponse.class,
                                              getEnvelopeNamespaces(_returnEnv));

                               
                                        return (org.ideaconsult.iuclidws.types.Types.LoginResponse)object;
                                   
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
                        
                        if (ex instanceof org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault){
                          throw (org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault)ex;
                        }
                        
                        if (ex instanceof org.ideaconsult.iuclidws.session.SessionEngineFault){
                          throw (org.ideaconsult.iuclidws.session.SessionEngineFault)ex;
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
    private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.Logout param, boolean optimizeContent)
    throws org.apache.axis2.AxisFault {

    
                try{
                     return param.getOMElement(org.ideaconsult.iuclidws.types.Types.Logout.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                } catch(org.apache.axis2.databinding.ADBException e){
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            

    }

    private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.LogoutResponse param, boolean optimizeContent)
    throws org.apache.axis2.AxisFault {

    
                try{
                     return param.getOMElement(org.ideaconsult.iuclidws.types.Types.LogoutResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                } catch(org.apache.axis2.databinding.ADBException e){
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            

    }

    private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.SessionEngineNotAvailableFault param, boolean optimizeContent)
    throws org.apache.axis2.AxisFault {

    
                try{
                     return param.getOMElement(org.ideaconsult.iuclidws.types.Types.SessionEngineNotAvailableFault.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                } catch(org.apache.axis2.databinding.ADBException e){
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            

    }

    private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.SessionEngineFault param, boolean optimizeContent)
    throws org.apache.axis2.AxisFault {

    
                try{
                     return param.getOMElement(org.ideaconsult.iuclidws.types.Types.SessionEngineFault.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                } catch(org.apache.axis2.databinding.ADBException e){
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            

    }

    private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.Login param, boolean optimizeContent)
    throws org.apache.axis2.AxisFault {

    
                try{
                     return param.getOMElement(org.ideaconsult.iuclidws.types.Types.Login.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                } catch(org.apache.axis2.databinding.ADBException e){
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            

    }

    private  org.apache.axiom.om.OMElement  toOM(org.ideaconsult.iuclidws.types.Types.LoginResponse param, boolean optimizeContent)
    throws org.apache.axis2.AxisFault {

    
                try{
                     return param.getOMElement(org.ideaconsult.iuclidws.types.Types.LoginResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                } catch(org.apache.axis2.databinding.ADBException e){
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            

    }

                            
                                private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.Logout param, boolean optimizeContent)
                                throws org.apache.axis2.AxisFault{

                                     
                                            try{

                                                    org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                    emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.Logout.MY_QNAME,factory));
                                                    return emptyEnvelope;
                                                } catch(org.apache.axis2.databinding.ADBException e){
                                                    throw org.apache.axis2.AxisFault.makeFault(e);
                                                }
                                        

                                }
                        
                     
                     /* methods to provide back word compatibility */

                     
                            
                                private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.ideaconsult.iuclidws.types.Types.Login param, boolean optimizeContent)
                                throws org.apache.axis2.AxisFault{

                                     
                                            try{

                                                    org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                                                    emptyEnvelope.getBody().addChild(param.getOMElement(org.ideaconsult.iuclidws.types.Types.Login.MY_QNAME,factory));
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

        if (org.ideaconsult.iuclidws.types.Types.Logout.class.equals(type)){
        
                   return org.ideaconsult.iuclidws.types.Types.Logout.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            

        }
   
        if (org.ideaconsult.iuclidws.types.Types.LogoutResponse.class.equals(type)){
        
                   return org.ideaconsult.iuclidws.types.Types.LogoutResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            

        }
   
        if (org.ideaconsult.iuclidws.types.Types.SessionEngineNotAvailableFault.class.equals(type)){
        
                   return org.ideaconsult.iuclidws.types.Types.SessionEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            

        }
   
        if (org.ideaconsult.iuclidws.types.Types.SessionEngineFault.class.equals(type)){
        
                   return org.ideaconsult.iuclidws.types.Types.SessionEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            

        }
   
        if (org.ideaconsult.iuclidws.types.Types.Login.class.equals(type)){
        
                   return org.ideaconsult.iuclidws.types.Types.Login.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            

        }
   
        if (org.ideaconsult.iuclidws.types.Types.LoginResponse.class.equals(type)){
        
                   return org.ideaconsult.iuclidws.types.Types.LoginResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            

        }
   
        if (org.ideaconsult.iuclidws.types.Types.SessionEngineNotAvailableFault.class.equals(type)){
        
                   return org.ideaconsult.iuclidws.types.Types.SessionEngineNotAvailableFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            

        }
   
        if (org.ideaconsult.iuclidws.types.Types.SessionEngineFault.class.equals(type)){
        
                   return org.ideaconsult.iuclidws.types.Types.SessionEngineFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            

        }
   
} catch (java.lang.Exception e) {
throw org.apache.axis2.AxisFault.makeFault(e);
}
   return null;
}

}
