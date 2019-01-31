{
	"get" : {
		"operationId": "getInvestigationResults",
        "summary": "Query protocol applications",
        "produces": [
          "application/json",
        	"application/x-javascript",
        	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        	"text/csv",
        	"text/plain"
        ],
        "responses": {
          "200": {
            "description": "OK. Substance(s) found"
          },
          "300": {
            "description": "Substances not found"
          }
        }
	}
}   
 