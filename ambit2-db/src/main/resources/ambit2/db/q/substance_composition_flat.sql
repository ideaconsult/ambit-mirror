SELECT prefix,hex(uuid),idsubstance,idchemical,smiles,inchi,inchikey,relation FROM substance
join substance_relation using(idsubstance) join chemicals using(idchemical)