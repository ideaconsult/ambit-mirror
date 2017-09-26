delete FROM models where name ="VEGA models";
delete FROM template where name="VEGA models";
delete v,p,c from property_values v, properties p, catalog_references c
where 
c.url regexp "^VEGA"
and p.idproperty=v.idproperty
and p.idreference=c.idreference;

