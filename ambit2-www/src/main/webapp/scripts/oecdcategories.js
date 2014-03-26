var funcgroups = [
{ "family" :"" , "name":"Custom SMARTS" , "smarts" : "",  "hint" :"Enter the SMARTS in the text box, or draw in JME, using the QRY option"},
{"family" : "Acid Chlorides" , "name":"Acid Chlorides" , "smarts": "C(=O)Cl" },
{"family" : "Acid Chlorides" , "name":"Acid Chlorides (sulfonyl chlorides, sulfochlorides)" , "smarts": "S(=O)Cl" },
/* 
{"family" : "Acid Dyes and Amphoteric Dyes" , "name":"Acid Dyes and Amphoteric Dyes" , "smarts": "nonionic, anionic, cationic, amphoteric" },
 */
{"family" : "Acrylamides" , "name":"Acrylamides" , "smarts": "[NX3][CX3](=[OX1])C=C" },
{"family" : "Acrylates/Methacrylates" , "name":"Acrylates/Methacrylates" , "smarts": "[CX3H1](=[CX3H2])[CX3](=[OX1])[O-,OX2]" },
{"family" : "Aldehydes" , "name":"Aldehydes" , "smarts": "[CX3H1](=O)[#6]" },
{"family" : "Aldehydes" , "name":"Aldehydes (polyaldehydes)" , "smarts": "([CX3H1](=O)[C,c].[CX3H1](=O)[C,c])" },
{"family" : "Aldehydes" , "name":"Aldehydes (vinyl)" , "smarts": "[CX3H2]=[CX3][CX3H1]=[O]" },
{"family" : "Aldehydes" , "name":"Aldehydes (allylic)" , "smarts": "[CX3]=[CX3][CH2][CX3H1]=[O]" },
{"family" : "Aliphatic Amines" , "name":"Aliphatic Amines (primary amines or monoalkyl amines)" , "smarts": "[C][NX3H2,NX4H3;!$(NC=[!#6])]" },
{"family" : "Aliphatic Amines" , "name":"Aliphatic Amines (secondary amines or dialkyl amines)" , "smarts": "[C][NX3H1,NX4H2;!$(NC=[!#6])][C]" },
{"family" : "Aliphatic Amines" , "name":"Aliphatic Amines (tertiary amines or trialkyl amines)" , "smarts": "[C][NX3H0,NX4H1;!$(NC=[!#6])](C)[C]" },
{"family" : "Aliphatic Amines" , "name":"Aliphatic Amines (amine oxides)" , "smarts": "[NX4;H0;+1][OX1;-1]" },
{"family" : "Aliphatic Amines" , "name":"Aliphatic Amines (fatty polyamines)" , "smarts": "[NX3,NX4;!$(NC=[!#6])].[NX3,NX4;!$(NC=[!#6])]" },
{"family" : "Alkoxysilanes" , "name":"Alkoxysilanes" , "smarts": "CO[Si]" },
{"family" : "Alkoxysilanes" , "name":"Alkoxysilanes (trialkoxysilyl)" , "smarts": "CO[Si](C)(OC)OC" },
{"family" : "Aluminum Compounds" , "name":"Aluminum Compounds" , "smarts": "[Al]" },
{"family" : "Aminobenzothiazole Azo Dyes" , "name":"Aminobenzothiazole Azo Dyes", "hint":"(2-aminobenzothiazole)" , "smarts": "N=Nc2nc1ccccc1s2" },
{"family" : "Aminobenzothiazole Azo Dyes" , "name":"Aminobenzothiazole Azo Dyes", "hint":"(2-(p-amino)phenylbenzothiazole)" , "smarts": "N=Nc3ccc(c2nc1ccccc1s2)cc3" },
{"family" : "Anhydrides, Carboxylic Acid" , "name":"Anhydrides, Carboxylic Acid" , "smarts": "[CX3](=[OX1])[OX2][CX3](=[OX1])" },
{"family" : "Anilines" , "name":"Anilines" , "smarts": "[N;R0&!$(NC=O)][cc]" },
{"family" : "Anilines" , "name":"Anilines (polyanilines)" , "smarts": "[N][c]:[c]:[c]:[c][N]" },
/* SMARTS is wrong
{"family" : "Anilines" , "name":"Anilines, Dianilines " , "smarts": "[NX3H2]-!:[cccc]-!:[C,O,N,S]" },
*/
/*smarts incomplete
{"family" : "Anionic Surfactants " , "name":"Anionic Surfactants (alkyl sulfonate)" , "smarts": "[SX4](=O)(=O)([OX2H1,OX1-])[CX4H2][CX4H2][CX4H2][CX4H2][CX4H2][CX4H2]" },
{"family" : "Anionic Surfactants " , "name":"Anionic Surfactants (alkyl benzene sulfonate)" , "smarts": "[SX4](=O)(=O)([OX2H1,OX1-])[cccccc][CX4H2][CX4H2][CX4H2][CX4H2][CX4H2][CX4H2]" },
{"family" : "Anionic Surfactants " , "name":"Anionic Surfactants (alkyl phosphate)" , "smarts": "[PX4](=O)([OX2H1,OX1-])([OX2H1,OX1-])[OX2][CX4H2]" },
{"family" : "Anionic Surfactants " , "name":"Anionic Surfactants (alkyl carboxylate)" , "smarts": "[CX4H2][CX3H0](=O)[OX2H1,OX1-]" },
*/
{"family" : "Azides" , "name":"Azides" , "smarts": "N=N=N" },
{"family" : "Benzotriazoles" , "name":"Benzotriazoles" , "smarts": "C12=CC=CC=C1NN=N2" },
/*
{"family" : "Benzotriazoles" , "name":"Benzotriazole-hindered phenols" , "smarts": "" },
*/
{"family" : "Boron Compounds" , "name":"Boron Compounds (borates and borate esters)" , "smarts": "[BX3]([O])([O])[O]" },
{"family" : "Boron Compounds (organoboranes)" , "name":"Boron Compounds (organoboranes)" , "smarts": "[BX3,BX4-]([C,c,O,H])([C,c,O,H])[C,c,O,H]" },
{"family" : "Boron Compounds (boroxines)" , "name":"Boron Compounds (boroxines)" , "smarts": "[BX3]([C,c])[O]" },
{"family" : "Boron Compounds (boron hydride)" , "name":"Boron Compounds (boron hydride)" , "smarts": "[BH2]1[H][BH2][H]1" },
/* 
{"family" : "Cationic Dyes" , "name":"Cationic Dyes" , "smarts": "" },
 */
{"family" : "Cationic (quaternary ammonium) surfactants" , "name":"Cationic (quaternary ammonium) surfactants" , "smarts": "[N+](*)(*)(*)*" },
{"family" : "Cobalt" , "name":"Cobalt" , "smarts": "[Co]" },
{"family" : "Diazoniums" , "name":"Diazoniums" , "smarts": "[NX1]#[NX2+]" },
{"family" : "Dichlorobenzidine-based Pigments" , "name":"Dichlorobenzidine-based Pigments" , "smarts": "Clc2cc(c1ccc(N)c(Cl)c1)ccc2N" },
{"family" : "Dithiocarbamates " , "name":"Dithiocarbamates" , "smarts": "N(C(S*)=S)(*)*" },
{"family" : "Dithiocarbamates " , "name":"Dithiocarbamates (N,N-dialkyldithiocarbamates)" , "smarts": "[NX3H0;!$(NC=O)][CX3H0](~[SX1-0,SX1-])[SX2][C,H]" },
{"family" : "Epoxides" , "name":"Epoxides" , "smarts": "[CX4]1[OX2H0][CX4]1" },
{"family" : "Esters" , "name":"Esters (carboxylic esters)" , "smarts": "[C,c][CX3H0](=[OX1])[OX2][C,c]" },
{"family" : "Esters" , "name":"Esters (sulfonate esters)" , "smarts": "[C,c][SX4H0](=[OX1])(=[OX1])[OX2][C,c]" },
{"family" : "Ethylene glycol wthers" , "name":"Ethylene Glycol Ethers" , "smarts": "[OX2H0;!$(OC=O)][CX4H2R0][CX4H2R0][OX2H0;!$(OC=O)]" },
{"family" : "Hydrazines and related compounds " , "name":"Hydrazines and related compounds (hydrazine)" , "smarts": "[NX3][NX3]" },
{"family" : "Hydrazines and related compounds " , "name":"Hydrazines and related compounds (hydrazones)" , "smarts": "[C]=[NX2][NX3]" },
{"family" : "Hydrazines and related compounds " , "name":"Hydrazines and related compounds (hydrazide)" , "smarts": "[C](=O)[NX3H1][NX3]" },
{"family" : "Hydrazines and related compounds " , "name":"Hydrazines and related compounds (semicarbazide)" , "smarts": "[#6][NX3H1][C](=O)[NX3H1][NX3]" },
/* 
{"family" : "Hindered Amines" , "name":"Hindered Amines" , "smarts": "" },
*/
{"family" : "Imides" , "name":"Imides" , "smarts": "[CX3](=[OX1])[NX3]([H,#6])[CX3]=[OX1]" },
{"family" : "Imides" , "name":"(N-halogenated imides)" , "smarts": "[CX3](=[OX1])[NX3]([F,Cl,Br,I])[CX3]=[OX1]" },
/*SMARTS wrong
{"family" : "Diisocyanates" , "name":"Diisocyanates" , "smarts": "([#6][NX2]=[CX2]=[OX1]).([#6][NX2]=[CX2]=[OX1])" },
*/
/*
{"family" : "beta-Naphthylamines, Sulfonated" , "name":"beta-Naphthylamines, Sulfonated" , "smarts": "" },
 */
{"family" : "Lanthanides or Rare Earth Metals" , "name":"Lanthanides or Rare Earth Metals" , "smarts": "[La],[Ce],[Pr],[Nd],[Sm],[Eu],[Gd],[Tb],[Dy],[Ho],[Er],[Tm],[Yb],[Lu]" },
{"family" : "Neutral Organics" , "name":"Neutral Organics (alcohol)" , "smarts": "[C][OX2H1;!$(OC=O)]" },
{"family" : "Neutral Organics" , "name":"Neutral Organics (ether)" , "smarts": "[#6][OX2H0;!$(OC=O)][#6]" },
{"family" : "Neutral Organics" , "name":"Neutral Organics (aromatic hydrocarbon)" , "smarts": "c" },
{"family" : "Neutral Organics" , "name":"Neutral Organics (ketone, alkyl or aryl)" , "smarts": "[#6][CX3](=[OX1])[#6]" },
{"family" : "Neutral Organics" , "name":"Neutral Organics (alkyl halide)" , "smarts": "[C][Cl,Br]" },
{"family" : "Neutral Organics" , "name":"Neutral Organics (aryl halide)" , "smarts": "[c]:[c][Cl,Br]" },
{"family" : "Nickel Compounds" , "name":"Nickel Compounds" , "smarts": "[Ni]" },
{"family" : "Nitriles" , "name":"Nitriles" , "smarts": "N#C" },
{"family" : "Nitriles" , "name":"Nitriles (vinyl)" , "smarts": "[NX1]#[CX2R0H0][CX3R0]=[CX3R0]" },
{"family" : "Nitriles" , "name":"Nitriles (allyl)" , "smarts": "[NX1]#[CX2H0R0][CX4R0][CX3R0]=[CX3R0]" },
{"family" : "Nitriles" , "name":"Nitriles (alfa-alkynyl-nitriles)" , "smarts": "[NX1]#[CX2H0R0][CX2R0]#[CX2R0]" },
{"family" : "Nitriles" , "name":"Nitriles (beta-alkynyl-nitriles)" , "smarts": "[NX1]#[CX2H0R0][CX4R0][CX2R0]#[CX2R0]" },
{"family" : "Nitriles" , "name":"Nitriles (gamma-alkynyl-nitriles)" , "smarts": "[NX1]#[CX2H0R0][CX4R0][CX4R0][CX2R0]#[CX2R0]" },
 /*
{"family" : "Nonionic Surfactants" , "name":"Nonionic Surfactants" , "smarts": "" },
 */
{"family" : "Organotins" , "name":"Organotins" , "smarts": "[Sn][#6,O]" },
{"family" : "Peroxides" , "name":"Peroxides" , "smarts": "[OX2][OX2]" },
{"family" : "Peroxides" , "name":"Dialkyl peroxide" , "smarts": "[C,c;!$(C=O)][OX2][OX2][C,c;!$(C=O)]" },
{"family" : "Peroxides" , "name":"Peroxides. Alkyl hydroperoxide" , "smarts": "[C,c][OX2][OX2H1]" },
{"family" : "Peroxy ester" , "name":"Peroxides. Peroxy ester" , "smarts": "[CX3](=[O])[OX2][OX2][C,c;!$(C=O)]" },
{"family" : "Piacyl peroxide" , "name":"Peroxides. Diacyl peroxide" , "smarts": "[CX3](=[O])[OX2][OX2][CX3]=[O]" },
{"family" : "Peroxy acid" , "name":"Peroxides. Peroxy acid" , "smarts": "[CX3](=[O])[OX2][OX2H1]" },
/* 
{"family" : "Persistent, Bioaccumulative, and Toxic (PBT) Chemicals" , "name":"Persistent, Bioaccumulative, and Toxic (PBT) Chemicals" , "smarts": "" },
*/
{"family" : "Phenolphthaleins" , "name":"Phenolphthaleins" , "smarts": "O=C1OC(c2ccccc12)(c3ccc(O)cc3)c4ccc(O)cc4", "hint":"This is example SMILES!" },
{"family" : "Phenols" , "name":"Phenols" , "smarts": "[OX2H1]c" },
{"family" : "Phosphates" , "name":"Phosphates" , "smarts": "[O][P](=O)(O)[O]" },
{"family" : "Pyrophosphates, polyphosphates" , "name":"Pyrophosphates, polyphosphates" , "smarts": "[O][P](=O)(O)[O][P](=O)(O)[O]" },
{"family" : "Phosphinate Esters" , "name":"Phosphinate Esters" , "smarts": "[C,c][P](=O)([C,c])[OX2][C,c]" },
/*
{"family" : "Polyanionic Polymers (and Monomers)" , "name":"Polyanionic Polymers (and Monomers)" , "smarts": "" },
{"family" : "Polycationic Polymers" , "name":"Polycationic Polymers" , "smarts": "" },
{"family" : "Polynitroaromatics" , "name":"Polynitroaromatics" , "smarts": "" },
{"family" : "Respirable, Poorly Soluble Particulates" , "name":"Respirable, Poorly Soluble Particulates" , "smarts": "" },
*/
{"family" : "Rosin (abietic=abietinic=sylvic acid)" , "name":"Rosin (abietic=abietinic=sylvic acid)" , "smarts": "O=C(O)C2(C)CCCC3(C)C1CCC(C(C)C)=CC1=CCC23" },
{"family" : "Stilbene, derivatives of 4,4-bis(triazin-2-ylamino)-" , "name":"Stilbene, derivatives of 4,4-bis(triazin-2-ylamino)-" , "smarts": "c2(C=Cc1ccccc1)ccccc2", "hint":"This is an example SMILES!" },
{"family" : "Thiols" , "name":"Thiols" , "smarts": "[SX2H1][C,c]" },
{"family" : "Substituted Triazines" , "name":"Substituted Triazines" , "smarts": "c1nnncc1" },
{"family" : "Triarylmethane Pigments/Dyes with Non-solubilizing Groups" , "name":"Triarylmethane Pigments" , "hint":"This is an example SMILES!",  "smarts": "c1c(cccc1)C(c2ccccc2)c3ccccc3" },
{"family" : "Vinyl Esters" , "name":"Vinyl Esters" , "smarts": "[#6][CX3H0](=[OX1])[OX2][CX3H1]=[CX3H2]" },
{"family" : "Vinyl Sulfones" , "name":"Vinyl Sulfones" , "smarts": "[SX4](=[OX1])(=[OX1])[CX3H1]=[CX3H2]" },
{"family" : "Vinyl Sulfones" , "name":"Vinyl Sulfones (beta-Sulfatoethyl-sulfonyl groups)" , "smarts": "[SX4](=[OX1])(=[OX1])[CX4H2][CX4H2][OX2][SX4](=[OX1])(=[OX1])[O,D1]" },
{"family" : "Zinc" , "name":"Zinc" , "smarts": "[Zn]" },
{"family" : "Zirconium Compounds" , "name":"Zirconium Compounds" , "smarts": "[Zr]" }
];