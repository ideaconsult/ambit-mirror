  /substance:
    get: 
      operationId: getSubstances
      summary: List substances
      tags:
        - Substances      
      description: Returns a list of substances, according to the search criteria
      parameters:
        - in: query
          name: search
          description: Search parameter
          schema:
            type: string
        - in: query
          name: 'type'
          schema:
            type: string
            enum:
             - substancetype
             - name
             - like
             - regexp
             - uuif
             - CompTox
             - DOI
             - reliability
             - purposeFlag
             - studyResultType
             - isRobustStudy
             - citation
             - citationowner
             - topcategory
             - endpointcategory
             - params
             - owner_name
             - owner_uuid
             - related
             - reference
             - facet
        - in: query
          name: 'compound_uri'
          description: If type=related finds all substances containing this compound; if typ =reference - finds all substances with this compound as reference structure
          schema:
            type: string
        - in: query
          name: 'bundle_uri'
          description: Retrieves if selected in this bundle
          schema:
            type: string
        - in: query
          name: addDummySubstance
          description: Adds a compound record as substance in JSON; only if type=related
          schema:
            type: boolean
        - in: query
          name: studysummary
          description: If true retrieves study summary for each substance
          schema: 
            type: boolean
<#include "/apidocs2/parameters_page.ftl" >
                    
      responses:
        '200':
          description: OK. Substances found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Substance'
        '404':
          description: Substances not found    
          
         
  /substance/{uuid}: 
    get:
      operationId: getSubstanceByUUID
      summary: Get a substance
      description: Returns substance representation
      tags:
        - Substances            
      parameters:
        - in: path
          name: uuid
          description: Substance UUID
          schema:
            type: string
          required: true 
        - in: query
          name: property_uris[]	
          description: 	Property URIs
          schema:
            type: string
<#include "/apidocs2/parameters_page.ftl" >
                    
      responses: 
        '200':
          description: OK. Substances found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Substance'
        '404':
          description: Substances not found      
            
  '/substance/{uuid}/study': 
    get:
      operationId: getSubstanceStudy
      summary: get substance study
      description: Returns substance study representation
      tags:
        - Studies
        - Substances            
      parameters:
        - in: path
          name: uuid
          description: Substance UUID
          schema:
            type: string
          required: true 
        - in: query
          name: top
          description: 	Top endpoint category
          schema:
            type: string
            enum: ['P-CHEM','ECOTOX','ENV FATE','TOX','EXPOSURE']
        - in: query
          name: category
          description: 	Endpoint category (The value in the protocol.category.code field)
          schema:
            type: string
        - in: query
          name: property_uri
          description: 	Property URI https://data.enanomapper.net/property/{UUID} , see Property service
          schema:
            type: string
        - in: query
          name: property
          description: 	Property UUID
          schema:
            type: string        
        - in: query
          name: investigation_uuid
          description: 		Investigation UUID, a code to link different studies
          schema:
            type: string    
<#include "/apidocs2/parameters_page.ftl" >
                             
      responses: 
        '200':
          description: OK. Substances found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SubstanceStudy'
        '404':
          description: Substances not found          
            
  '/substance/{uuid}/composition': 
    get:
      operationId: getSubstanceComposition
      summary: Substance composition
      description: Returns substance composition
      tags:
        - Chemical structures
        - Substances            
      parameters:
        - in: path
          name: uuid
          description: Substance UUID
          schema:
            type: string
          required: true 
        - in: query
          name: all
          description:	true (Show all compositions) false (do not show hidden compositions)
          schema:
            type: boolean 
<#include "/apidocs2/parameters_page.ftl" >
                    
      responses: 
        '200':
          description: OK. compositions found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SubstanceComposition'
        '404':
          description: compositions not found             
  '/substance/{uuid}/structures': 
    get:
      operationId: getSubstanceStructures
      summary: Get substance composition as a dataset
      description: Returns substance composition
      tags:
        - Chemical structures
        - Substances            
      parameters:
        - in: path
          name: uuid
          description: Substance UUID
          schema:
            type: string
          required: true
<#include "/apidocs2/parameters_page.ftl" >
        
      responses: 
        '200':
          description: OK. compositions found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Dataset'
        '404':
          description: compositions not found       
                
  '/substance/{uuid}/studySummary': 
    get:
      operationId: getSubstanceStudySummary
      summary: Get study summary for the substance
      description: Study summary
      tags:
        - Studies
        - Substances            
      parameters:
        - in: path
          name: uuid
          description: Substance UUID
          schema:
            type: string
          required: true 
        - in: query
          name: top
          description: 	Top endpoint category
          schema:
            type: string
            enum: ['P-CHEM','ECOTOX','ENV FATE','TOX','EXPOSURE']
        - in: query
          name: category
          description: 	Endpoint category (The value in the protocol.category.code field)
          schema:
            type: string
        - in: query
          name: property_uri
          description: 		Property URI https://data.enanomapper.net/property/{UUID} , see Property service
          schema:
            type: string
        - in: query
          name: property
          description: Property UUID, see Property service
          schema:
            type: string   
        - in: query
          name: result
          description: 	If true will group by topcategory,endpointcategory,interpretation result
          schema:
            type: boolean
<#include "/apidocs2/parameters_page.ftl" >

      responses: 
        '200':
          description: OK. 
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SubstanceStudySummary'
        '404':
          description: Entries not found             