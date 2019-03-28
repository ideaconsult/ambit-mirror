    QueryBody:
      description: a JSON object with Solr query parameters
      content:
        application/json:
          schema:
            type: object
            properties:
              params:
                type: object
                properties:
                  fl:
                    type: array
                    items:
                      type: string
                  rows:
                    type: integer
              facet:
                type: object
            example: |
              {
                "params": {
                  "fl": [
                    "[child parentFilter=type_s:substance childFilter=type_s:composition limit=100]",
                    "dbtag_hss",
                    "name:name_hs",
                    "publicname:publicname_hs",
                    "owner_name:owner_name_hs",
                    "substanceType:substanceType_hs",
                    "s_uuid:s_uuid_hs",
                    "content:content_hss",
                    "SUMMARY.*"
                  ],
                  "rows": 20
                },
                "facet": {
                  "owner_name": {
                    "type": "terms",
                    "field": "owner_name_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "owner_name_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "reference_owner": {
                    "type": "terms",
                    "field": "reference_owner_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "reference_owner_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "substanceType": {
                    "type": "terms",
                    "field": "substanceType_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "substanceType_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "nm_name": {
                    "type": "terms",
                    "field": "publicname_s",
                    "mincount": 451,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "nm_name_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "protocol": {
                    "type": "terms",
                    "field": "guidance_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "protocol_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "method": {
                    "type": "terms",
                    "field": "E.method_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "method_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "cell": {
                    "type": "terms",
                    "field": "E.cell_type_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "cell_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "species": {
                    "type": "terms",
                    "field": "E.animal_model_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "species_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "route": {
                    "type": "terms",
                    "field": "E.exposure_route_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "route_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "interpretation": {
                    "type": "terms",
                    "field": "MEDIUM_s",
                    "mincount": 2,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "interpretation_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "dprotocol": {
                    "type": "terms",
                    "field": "Dispersion protocol_s",
                    "mincount": 2,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "dprotocol_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "reference_year": {
                    "type": "terms",
                    "field": "reference_year_s",
                    "mincount": 2,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "reference_year_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "reference": {
                    "type": "terms",
                    "field": "reference_s",
                    "mincount": 2,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "reference_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "release": {
                    "type": "terms",
                    "field": "updated_s",
                    "mincount": 1,
                    "limit": -1,
                    "domain": {
                      "excludeTags": "release_tag",
                      "blockChildren": "type_s:substance"
                    }
                  },
                  "topcategory": {
                    "type": "terms",
                    "field": "topcategory_s",
                    "mincount": 1,
                    "limit": -1,
                    "facet": {
                      "min": "min(loValue_d)",
                      "max": "max(loValue_d)",
                      "avg": "avg(loValue_d)",
                      "endpointcategory": {
                        "type": "terms",
                        "field": "endpointcategory_s",
                        "mincount": 1,
                        "limit": -1,
                        "facet": {
                          "min": "min(loValue_d)",
                          "max": "max(loValue_d)",
                          "avg": "avg(loValue_d)",
                          "effectendpoint": {
                            "type": "terms",
                            "field": "effectendpoint_s",
                            "mincount": 1,
                            "limit": -1,
                            "facet": {
                              "min": "min(loValue_d)",
                              "max": "max(loValue_d)",
                              "avg": "avg(loValue_d)",
                              "unit": {
                                "type": "terms",
                                "field": "unit_s",
                                "mincount": 1,
                                "limit": -1,
                                "facet": {
                                  "min": "min(loValue_d)",
                                  "max": "max(loValue_d)",
                                  "avg": "avg(loValue_d)"
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "domain": {
                      "blockChildren": "type_s:substance"
                    }
                  }
                }
              }