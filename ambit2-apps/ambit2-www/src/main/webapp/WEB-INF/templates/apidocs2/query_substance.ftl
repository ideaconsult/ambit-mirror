  /query/study:
    get:
      operationId: getEndpointSummary
      summary: Search endpoint summary
      description: Returns endpoint summary
      tags: 
        - Studies
        - Facets
      parameters:
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
      responses: 
        '200':
          description: OK. 
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Facet'
        '404':
          description: Entries not found  