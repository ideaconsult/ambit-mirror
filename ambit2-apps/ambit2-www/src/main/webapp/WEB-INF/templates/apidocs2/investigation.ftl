  /investigation:
    get:
      operationId: getInvestigationResults
      summary: Query protocol applications
      parameters:
        - in: query
          name: type
          description: query type
          schema:
            type: string
            enum: [byinvestigation,byassay,bysubstance,byprovider,bycitation,bystudytype,bystructure_inchikey,bystructure_smiles,bystructure_name,bysubstance_name,bysubstance_type]
          required: true
          example: bystudytype
        - in: query
          name: search
          description: Search parameter, UUID of the investigation or a substance
          schema:
            type: string
          required: true
          example: PC_GRANULOMETRY_SECTION          
        - in: query
          name: inchikey
          description: Search parameter, InChI key(s) of the substance component(s), comma delimited
          schema:
            type: string
          required: false
          example: "YUYCVXFAYWRXLS-UHFFFAOYSA-N"       
        - in: query
          name: id
          description: Search parameter, chemical structure or substance identifier(s), comma delimited
          schema:
            type: string
          required: false 
          example: ""          
<#include "/apidocs2/parameters_page.ftl" >          

      responses:
        '200':
          description: OK. Entries found
          content:
             application/json:
                schema:
                  $ref: '#/components/schemas/Investigation'
             application/x-javascript:
                schema:
                  $ref: '#/components/schemas/Investigation'
             application/vnd.openxmlformats-officedocument.spreadsheetml.sheet:
                schema:
                  $ref: '#/components/schemas/Investigation'
             text/csv:
                schema:
                  $ref: '#/components/schemas/Investigation'
             text/plain:
                schema:
                  $ref: '#/components/schemas/Investigation'
        '404':
          description: Entries not found
      externalDocs:
          description: Learn more about operations provided by this API.
          url: http://ambit.sf.net          
            
components:
  schemas:
    Investigation:      # Object definition
      type: object
      properties:
        substanceType:
          type: string
        name:
          type: string
        publicname:
          type: string
        owner_name: 
          type: string      
        topcategory: 
          type: string  
        endpointcategory: 
          type: string   
        guidance: 
          type: string            
        endpoint: 
          type: string            
        document_uuid: 
          type: string            
        reference:
          type: string
        reference_owner:
          type: string
        reference_year:
          type: string          
        effectendpoint: 
          type: string            
        resulttype: 
          type: string            
        textValue: 
          type: string            
        loQualifier: 
          type: string            
        loValue: 
          type: number
        upQualifier: 
          type: string                      
        upValue: 
          type: number                      
        unit: 
          type: string                      
        errQualifier: 
          type: string
        err: 
          type: number                      
        s_uuid: 
          type: string                      
        assay: 
          type: string            
        investigation: 
          type: string
        type_s: 
          type: string          
        updated: 
          type: string          
        studyResultType: 
          type: string                    
        _childDocuments_: 
          type: object                              
                