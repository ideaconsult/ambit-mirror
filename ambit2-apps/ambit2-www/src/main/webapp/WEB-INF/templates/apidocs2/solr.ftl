  '/solr/{id}/select':
    summary: Apache Solr powered search
    description: >-
      Backend of eNanoMapper search application as used by e.g.
      https://search.data.enanomapper.net/nanoreg/
    get:
      summary: Apache Solr powered search
      description: ''
      operationId: solrquery_get

      parameters:
        - in: path
          name: id
          description: Valid solr instance tag
          schema:
            type: string
          required: true 
          example: "nanoreg"  
        - in: query
          name: q
          description: The query
          schema:
            type: string
          required: false
          example: '*:*'           
        - in: query
          name: start
          description: Starting page
          schema:
            type: integer
          required: false
          example: 0 
        - in: query
          name: rows
          description: Page size
          schema:
            type: integer
          required: false
          example: 10    
        - in: query
          name: wt
          description: response format
          schema:
            type: string
            enum: ['json','xml']
          required: false
          example: json
        - in: query
          name: json2tsv
          description: Convert JSON to table format (AMBIT specific)
          schema:
            type: string
            enum: [false,true]
          required: false
          example: false          
        - in: query
          name: sep
          description: separator if json2tsv=true (AMBIT specific)
          schema:
            type: string
            enum: [',','\t']
          required: false
          example: ","          
      responses:
        '200':
          description: |-
            Query performed successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SolrResponse'
            application/xml:
              schema:
                $ref: '#/components/schemas/SolrResponse'
        '400':
          description: |-
            BAD_REQUEST
        '401':
          description: |-
            UNAUTHORIZED
        '403':
          description: |-
            FORBIDDEN
        '404':
          description: |-
            NOT_FOUND
        '409':
          description: |-
            CONFLICT
        '415':
          description: |-
            UNSUPPORTED_MEDIA_TYPE
        '500':
          description: |-
            SERVER_ERROR
        '503':
          description: |-
            SERVICE_UNAVAILABLE
        '510':
          description: |-
            INVALID_STATE          
    post:
      summary: Apache Solr powered search
 
      description: ''
      operationId: solrquery_post
      
      parameters:
        - in: path
          name: id
          description: Valid solr instance tag
          schema:
            type: string
          required: true 
          example: "nanoreg"      
        - in: query
          name: wt
          description: response format
          schema:
            type: string
            enum: ['json','xml']
          required: false
          example: json
        - in: query
          name: json2tsv
          description: Convert JSON to table format (AMBIT specific)
          schema:
            type: string
            enum: [false,true]
          required: false
          example: false          
        - in: query
          name: sep
          description: separator if json2tsv=true (AMBIT specific)
          schema:
            type: string
            enum: [',','\t']
          required: false
          example: ","    
      requestBody:
          $ref: '#/components/requestBodies/QueryBody'                  
      responses:
        '200':
          description: |-
            Query performed successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SolrResponse'
            application/xml:
              schema:
                $ref: '#/components/schemas/SolrResponse'
        '400':
          description: |-
            BAD_REQUEST
        '401':
          description: |-
            UNAUTHORIZED
        '403':
          description: |-
            FORBIDDEN
        '404':
          description: |-
            NOT_FOUND
        '409':
          description: |-
            CONFLICT
        '415':
          description: |-
            UNSUPPORTED_MEDIA_TYPE
        '500':
          description: |-
            SERVER_ERROR
        '503':
          description: |-
            SERVICE_UNAVAILABLE
        '510':
          description: |-
            INVALID_STATE          