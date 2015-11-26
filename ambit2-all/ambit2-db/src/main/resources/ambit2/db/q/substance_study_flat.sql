SELECT 
`name`,`publicname`,owner_name, p.topcategory,p.endpointcategory,
guidance,`params`,interpretation_result,interpretation_criteria,reference,reference_owner,`conditions`,e.endpoint, unit, loQualifier, loValue, upQualifier, upValue,textValue,null as err_qualifier,err,"study" as type_s,
ifnull(idresult,hex(p.document_uuid)) as id,concat(prefix,"-",hex(uuid)) as s_uuid,substanceType,concat(p.document_prefix,"-",hex(p.document_uuid)) as doc_uuid ,hex(endpointhash) as e_hash
FROM substance
join substance_protocolapplication p on p.substance_prefix=prefix and p.substance_uuid=uuid
left join substance_experiment e on e.document_prefix=p.document_prefix and e.document_uuid=p.document_uuid 
