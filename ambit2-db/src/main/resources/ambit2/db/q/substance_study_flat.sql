SELECT 
`name`,`publicname`,owner_name, p.topcategory,p.endpointcategory,p.endpoint,
p.document_prefix,hex(p.document_uuid) as document_uuid,guidance,`params`,interpretation_result,interpretation_criteria,reference,reference_owner,
idresult,e.endpoint as effectendpoint, hex(endpointhash) as hash, `conditions`, unit, loQualifier, loValue, upQualifier, upValue,textValue,errQualifier,err,"study" as type_s,
prefix as s_prefix,hex(uuid) as s_uuid,substanceType,reference_year,content
FROM substance
join substance_protocolapplication p on p.substance_prefix=prefix and p.substance_uuid=uuid
left join substance_experiment e on e.document_prefix=p.document_prefix and e.document_uuid=p.document_uuid 
where (:all || ((prefix=:s_prefix) and uuid=unhex(:s_uuid)))