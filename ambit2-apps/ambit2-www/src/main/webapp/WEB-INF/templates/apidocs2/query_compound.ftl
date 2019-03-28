  '/query/compound/{term}/{representation}':
    get:
      summary: Exact chemical structure search
      description: Returns compounds found
      operationId: searchByIdentifier
      parameters:
        - in: path
          name: term
          description: search term type
          schema:
            type: string
            enum: 
            - search
            - url
            - inchikey
          required: true
        - in: path
          name: representation
          schema:
            type: string
            enum: 
              - all
              - smiles
              - reach
              - stdinchi
              - stdinchikey
              - names
              - iupac_name
              - synonym
              - cas
              - einecs            
          required: true
        - in: query
          name: search
          description: Compound identifier (SMILES, InChI, name, registry identifiers)
          schema:
            type: string
          required: false    
        - in: query
          name: b64search
          description: Base64 encoded mol file; if included, will be used instead of the 'search' parameter
          schema:
            type: string
          required: false     
        - in: query
          name: casesens
          description: Case sensitive search if yes
          schema:
            type: boolean
          required: false
        - in: query
          name: bundle_uri
          description: Bundle URI
          schema:
            type: string
          required: false              
        - in: query
          name: sameas
          description: Ontology URI to define groups of columns
          schema:
            type: string
          required: false         
<#include "/apidocs2/parameters_page.ftl" >
                                            
      tags:
        - Chemical structures
      responses:
        '200':
          description: OK. Entries found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Dataset'

        '404':
          description: Entries not found
      externalDocs:
        description: Learn more about operations provided by this API.
        url: 'http://ambit.sf.net'       
        
  '/query/smarts':
    get:
      summary: Substructure search
      description: Returns compounds with the specified substructure
      operationId: searchBySmarts
      parameters:
        - in: query
          name: search
          description: Compound identifier (SMILES, InChI, name, registry identifiers)
          schema:
            type: string
          required: false    
        - in: query
          name: b64search
          description: Base64 encoded mol file; if included, will be used instead of the 'search' parameter
          schema:
            type: string
          required: false     
        - in: query
          name: type
          description: Defines the expected content of the search parameter
          schema:
            type: string
            enum:
             - smiles
             - mol
             - url
          required: false    
        - in: query
          name: dataset_uri
          description: Restrict the search within the AMBIT dataset specified with the URI
          schema:
            type: string
          required: false              
        - in: query
          name: filterBySubstance
          description: Restrict the search within the set of structures with assigned substances
          schema:
            type: boolean
          required: false       
        - in: query
          name: bundle_uri
          description: 	If the structure is used in the specified bundle URI, the selection tag will be returned
          schema:
            type: string
          required: false            
        - in: query
          name: sameas
          description: 	Ontology URI to define groups of columns
          schema:
            type: string
          required: false     
        - in: query
          name: mol
          description: 	Only for application/json; to include mol as JSON field
          schema:
            type: boolean
          required: false
<#include "/apidocs2/parameters_page.ftl" >
                       
      tags:
        - Chemical structures
      responses:
        '200':
          description: OK. Entries found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Dataset'

        '404':
          description: Entries not found
      externalDocs:
        description: Learn more about operations provided by this API.
        url: 'http://ambit.sf.net'       
        
  '/query/similarity':
    get:
      summary: Exact similarity search
      description: Returns similar compounds
      operationId: searchBySimilarity
      parameters:
        - in: query
          name: search
          description: Compound identifier (SMILES, InChI, name, registry identifiers)
          schema:
            type: string
          required: false    
        - in: query
          name: b64search
          description: Base64 encoded mol file; if included, will be used instead of the 'search' parameter
          schema:
            type: string
          required: false     
        - in: query
          name: type
          description: Defines the expected content of the search parameter
          schema:
            type: string
            enum:
             - smiles
             - mol
             - url
          required: false    
        - in: query
          name: threshold
          description: Similarity threshold
          schema:
            type: number
          required: false           
        - in: query
          name: dataset_uri
          description: Restrict the search within the AMBIT dataset specified with the URI
          schema:
            type: string
          required: false              
        - in: query
          name: filterBySubstance
          description: Restrict the search within the set of structures with assigned substances
          schema:
            type: boolean
          required: false       
        - in: query
          name: bundle_uri
          description: 	If the structure is used in the specified bundle URI, the selection tag will be returned
          schema:
            type: string
          required: false            
        - in: query
          name: sameas
          description: 	Ontology URI to define groups of columns
          schema:
            type: string
          required: false     
        - in: query
          name: mol
          description: 	Only for application/json; to include mol as JSON field
          schema:
            type: boolean
          required: false
<#include "/apidocs2/parameters_page.ftl" >
                       
      tags:
        - Chemical structures
      responses:
        '200':
          description: OK. Entries found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Dataset'

        '404':
          description: Entries not found
      externalDocs:
        description: Learn more about operations provided by this API.
        url: 'http://ambit.sf.net'    
                