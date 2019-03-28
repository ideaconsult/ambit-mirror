    SolrQuery:      
      type: object
      properties:
        query:
          type: string
        filter:
          type: string
        facet:
          type: string
        sort:
          type: boolean
        start:
          type: integer
        rows:
          type: integer                
          
    SolrResponse:          
      type: object
      properties:
        responseHeader:
          type: object
          properties:
            zkConnected:
              type: boolean
            status:
              type: integer
            QTime:
              type: integer
            params:
              type: object
        response:
          type: object
          properties:
            numFound:
              type: integer
            start:
              type: integer
            maxScore:
              type: number
            docs:
              type: array
              items:
                type: object          