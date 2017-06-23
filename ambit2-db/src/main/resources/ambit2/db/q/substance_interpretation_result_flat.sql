SELECT substance_prefix,hex(substance_uuid),topcategory,endpointcategory,interpretation_result,count(*),group_concat(distinct params) FROM cebs.substance_protocolapplication
group by topcategory,endpointcategory,substance_prefix,substance_uuid,interpretation_result
