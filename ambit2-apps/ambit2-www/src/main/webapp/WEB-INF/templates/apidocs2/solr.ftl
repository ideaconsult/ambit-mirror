  '/solr/{id}/select':
    summary: Apache Solr powered search
    description: >-
      Backend of eNanoMapper search application as used by e.g.
      https://search.data.enanomapper.net/nanoreg/
    get:
      summary: Apache Solr powered search
      description: ''
      operationId: solrquery_get
      responses:
        default:
          description: Default error sample response
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
            enum: [true,false]
          required: false
          example: true          
        - in: query
          name: sep
          description: separator if json2tsv=true (AMBIT specific)
          schema:
            type: string
            enum: [',','\t']
          required: false
          example: ","          
    post:
      summary: Apache Solr powered search
      description: ''
      operationId: solrquery_post
      responses:
        default:
          description: response
      requestBody:
        description: https://lucene.apache.org/solr/guide/7_1/json-request-api.html
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/SolrQuery'
          text/plain:
            schema:
              type: string          
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
            enum: [true,false]
          required: false
          example: true          
        - in: query
          name: sep
          description: separator if json2tsv=true (AMBIT specific)
          schema:
            type: string
            enum: [',','\t']
          required: false
          example: ","            