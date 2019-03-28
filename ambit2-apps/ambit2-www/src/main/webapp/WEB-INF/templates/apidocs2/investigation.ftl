  /investigation:
    get:
      operationId: getInvestigationResults
      summary: Details of multiple studies
      description: Multiple studies in tabular form
      tags:
        - Studies      
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

            
