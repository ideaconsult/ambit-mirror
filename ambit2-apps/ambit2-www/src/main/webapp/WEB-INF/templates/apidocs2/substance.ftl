  /substance:
    get: 
      operationId: getSubstances
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
          description: If trie retrieves study summary for each substance
          schema: 
            type: boolean
      responses:
        '200':
          description: OK. Substances found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Substance'
        '404':
          description: Substances not found    