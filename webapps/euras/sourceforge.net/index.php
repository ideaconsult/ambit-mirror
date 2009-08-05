<?php
include 'nocache.php';
?>
<html>
<head>
<meta name="description" content="AMBIT Project : Building blocks for a future QSAR Decision support system">
<meta name="keywords" content="ambit,ambitxt,toxicity, bioconcentration factor,database,ambit2,rest,cas,chemical,molecular,qsar, applicability domain, decision support,structure diagram,editor,aquire">
<meta name="robots"content="index,nofollow">
<META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
<meta name="copyright" content="Copyright 2005. Nina Jeliazkova nina@acad.bg">
<meta name="author" content="Nina Jeliazkova">
<meta name="language" content="English">
<meta name="revisit-after" content="7">
<title>Ambit project - QSAR DSS building blocks</title>
</head>
<link href="styles/ambit.css" rel="stylesheet" type="text/css">
<body >
<?php 
echo "<p>";
$reliability="";
$cas = "";
$page="";
$pagesize="100";
$maxpages="";

$a = explode('&', $_SERVER['QUERY_STRING']);
$i = 0;
while ($i < count($a)) {   $b = split('=', $a[$i]);	   

if (htmlspecialchars(urldecode($b[0])) == "cas")    	
$cas = htmlspecialchars(urldecode($b[1]));   

if (htmlspecialchars(urldecode($b[0])) == "reliability")    	
$reliability = htmlspecialchars(urldecode($b[1]));   

if (htmlspecialchars(urldecode($b[0])) == "name")    	
$name = htmlspecialchars(urldecode($b[1]));   

if (htmlspecialchars(urldecode($b[0])) == "page")    	
$page = htmlspecialchars(urldecode($b[1]));   

if (htmlspecialchars(urldecode($b[0])) == "pagesize")    	
$pagesize = htmlspecialchars(urldecode($b[1]));   

if (htmlspecialchars(urldecode($b[0])) == "maxpages")    	
$maxpages = htmlspecialchars(urldecode($b[1]));   

if (htmlspecialchars(urldecode($b[0])) == "species")    	
$species = htmlspecialchars(urldecode($b[1]));   

$i++;}
echo "<p>";
// Connecting, selecting database
$time = time();
$link = mysql_connect('mysql-a', 'a191756ro', 'guest')   or die('Could not connect: ' . mysql_error());

mysql_select_db('a191756_euras') or die('Could not select database');


if (($cas == '') && (isset($_POST['cas']))) $cas=$_POST['cas'];
if (($reliability== '') && (isset($_POST['reliability']))) $reliability=$_POST['reliability'];
if (($page == '') && (isset($_POST['page']))) $page=$_POST['page'];
if (($pagesize == '') && (isset($_POST['pagesize']))) $pagesize=$_POST['pagesize'];
if (($maxpages == '') && (isset($_POST['maxpages']))) $maxpages=$_POST['maxpages'];
if (($name == '') && (isset($_POST['name']))) $name=$_POST['name'];
if (($species == '') && (isset($_POST['species']))) $species=$_POST['species'];


//$cas = str_replace ("-","",$cas);

$display = "";

$urlfirst="index.php?page=0&pagesize=" . $pagesize;
$urlprev="index.php?pagesize=" . $pagesize;

$urlnext="index.php?pagesize=" . $pagesize;
$urllast="index.php?pagesize=" . $pagesize;

	$query = 'select 
   bcfid,	
   CAS,
  `substance common name`,
  `reliability score`,
  `species scientific name`,
  `species common name`,
  `species sex`,
  `fish weight`,
  `fish length`,
  `fish age`,
  `temperature`,
  `pH`,
  `TOC`,
  `flow-through/static/renewal`,
  `exposure concentration`,
  `fresh/marine`,
  `k1`,
  `uptake phase duration1`,
  `k2`,
  `elimination phase duration`,
  `T95`,
  `T80`,
  `uptake>T80`,
  `T50,elim`,
  `elimination>5xT50`,
  `tissue analyzed`,
  `BCFss ww`,
  `BCFss lipid`,
  `BCFk ww`,
  `BCFk lipid`,
  `lipid`,
  `lipid method`,
  `statistics`,
  `ref` as Reference,
  `title`,
  `remarks`,
  `Reason for reliability 3/4`
 from bcf left join literature on literature.idref=bcf.ref ';

	$where = false;
	if ($cas != '')  {
		$query .= 'where  CAS="' . trim($cas) . '"';
		$where = true;
		$url .= "&cas=" . $cas;
		$display = "CAS Registry number= " . $cas;
	}
	if ($name != '')  {
		if (!$where) {
			$query .= " where ";
		} else {
			$query .= " and ";
			$display .= " and ";
		}
		$url .= "&name=" . $name;
//		$query .= ' `substance common name` regexp "^' . trim($name) . '" ';		
		$query .= ' `substance common name` sounds like "' . trim($name) . '" ';		

		$display .= " `substance common name`=<b>" . $name . "</b>";
		$where = true;
	}
	if ($reliability!= '')  {
		if (!$where) {
			$query .= " where ";
		} else {
			$query .= " and ";

			$display .= " and ";
		}
		$url .= "&reliability=" . $reliability;
		$query .= ' `reliability score`="' . trim($reliability) . '" ';		
		$display .= " reliability=<b>" . $reliability. "</b>";
		$where = true;

	}
	if ($species != '')  {
		if (!$where) {
			$query .= " where ";
		} else {
			$query .= " and ";

			$display .= " and ";
		}
		$url .= "&species=" . urlencode($species);
		$query .= ' `species scientific name`="' . trim($species) . '" ';		
		$display .= " Species=<b>" . $species . "</b>";
		$where = true;
	}
	$query .= " group by bcfid ";
	if ($page != "") {
		if ($pagesize == "") $pagesize =100;
		$startrecord = $page*$pagesize;
		$endrecord = ($page+1)*$pagesize;

		$query .= " limit " . $startrecord . "," . $endrecord;
	  

	} else {
		$page = 0;
	}

$result = mysql_query($query) or die($query . "\nQuery failed: \n" . mysql_error());   
$rows = mysql_num_rows($result);

if ($maxpages == "") $maxpages = floor($rows/$pagesize)+1;
if ($maxpages < 0) $maxpages=0;

//Display Form
//echo $query;



echo "<form method=POST action=''>";
echo "<table border='0' width='99%' bgcolor='#FFFFFF' >";
echo "<tr bgcolor='#FFFFFF'><td align='left'>";
echo "<a href='http://ambit.sourceforge.net/intro.html'><img src='images/ambit-logo.png' alt='ambit.sourceforge.net' title='AMBIT new web site is at http://ambit.sourceforge.net' border='0'/></a>";

echo "</td><td align='right'>";
//search
echo "<table width='99%' bgcolor='#DDDDDD' >";
echo "<tr><td colspan='3'>";
echo "Search <a href='http://www.euras.be/bcf' target='_blank'>EURAS bioconcentration factor (BCF) Gold Standard Database</a>";
echo "</td></tr>";
echo "<tr><td>";
echo 'CAS Registry number </td><td> <input type="text" name="cas" value=' . $cas . '>';
echo "</td><td><font color='#888888'>Enter CAS registry number (hyphenated) or click on the CAS field in the result list</font></td><tr><td>";
echo 'Chemical name </td><td> <input type="text" name="name" value="' . $name . '">';
echo "</td><td><font color='#888888'>Searches for names that sound like user specified name (e.g. 'naftalen' will hit 'naphthalene')</font></td><tr><td>";

echo 'Reliability score</td><td> <input type="text" name="reliability" value=' . $reliability. '>';
echo "</td><td><font color='#888888'>Enter one of <i>1,2,3 or MITI</i> or click on the 'Reliability score' field in the result list</font></td><tr><td>";
echo 'Species </td><td> <input type="text" name="species" value="' . $species . '">';
echo "</td><td><font color='#888888'>Enter species latin name (e.g. <i>Pimephales promelas</i>) or click on the species field in the result list</font></td><tr><td>";
echo '</td><td> <input type="submit" name="Search" value ="Search">';

echo "</td><td><font color='#888888'>All criteria are combined with <i>'logical AND'</i>. If no criteria is specified, entire database is retrieved.</font></td></tr>";

echo "</table>";

//end search

echo "</td</tr></table>";


echo "</form>";

//End Display Form

echo "<table width=100% bgcolor=#EEEEEE><tr><td>";
echo $display;
echo "</td><td><b>" . $rows . "</b> records found</td></tr></table>";

if ($rows > 0) {

$urlfirst .= "&maxpages=" . $maxpages . $url;

$nextpage = $page+1;
if ($nextpage >= $maxpages) $nextpage=$maxpages-1;
$urlnext .= "&page=" . $nextpage . "&maxpages=" . $maxpages . $url;

$prevpage = $page -1;
if ($prevpage <= 0) $prevpage=0;
$urlprev .= "&page=" . $prevpage . "&maxpages=" . $maxpages . $url;

$lastpage=$maxpages-1;
$urllast .= "&maxpages=" . $maxpages . "&page=" . $lastpage . $url;


echo '<a href="' . $urlfirst . '">First</a> ';
echo '<a href="' . $urlprev . '">Previous</a> ' . "<b>Page </b>" . ($page+1) .  " of " . ($maxpages) . ' <a href="' . $urlnext . '">Next</a> ';
echo '<a href="' . $urllast . '">Last</a> ';
echo " Number of results per page " . $pagesize . " <br>";



echo "<table border=0>";
echo "<tr bgcolor='#DDDDDD'>";

$header = array(
  "CAS",
  "Substance common name",
  "Reliability score",
  "Species",
  "Species sex,M/F/MF/nd",
  "Fish weight,g",
  "Fish length,cm",
  "Fish age",
  "Temperature,°C",
  "pH",
  "TOC,mg/L",
  "flow-through/static/renewal,S/FT/R/nd",
  "Exposure concentration,mg/L",
  "fresh/marine",
  "k1,d-1",
  "Uptake phase duration1,d",
  "k2,d-1",
  "Elimination phase duration,d",
  "T95,d",
  "T80,d",
  "Uptake>T80,y/n/prob",
  "T50,elim",
  "Elimination>5xT50,y/n/prob",
  "Tissue analyzed",
  "BCFss ww, Cf/Cw (L/kg)",
  "BCFss lipid,Cf lipid/Cw (L/kg)",
  "BCFk ww,k1/k2",
  "BCFk lipid,BCFk ww/lipid%",
  "Lipid,%",
  "Lipid method",
  "Statistics",
  "Reference",
  "Remarks",
  "Reason for reliability 3/4"
);

echo "<th>#</th>";
foreach ($header as $value) {
  echo "<th>$value</th>";
}
echo "</tr>";


$nline = 0;
while ($line = mysql_fetch_array($result, MYSQL_ASSOC)) 
{   if (($nline % 2) == 0) $clr='#FFFFFF'; else $clr='#FFFFFF';    
	echo "<tr bgcolor='#EEEEEE'>";
	
	$fieldn = mysql_num_fields($result);

	$id = mysql_result($result,$nline,0);    
	$cas = mysql_result($result,$nline,1);       
	$name = mysql_result($result,$nline,2);          
	$reliability= mysql_result($result,$nline,3);          

	$species_latinname = mysql_result($result,$nline,4);
	$species_commonname = mysql_result($result,$nline,5);
	$species_sex = mysql_result($result,$nline,6);

	$fish_weight = mysql_result($result,$nline,7);
	$fish_length = mysql_result($result,$nline,8);
	$fish_age = mysql_result($result,$nline,9);

	$temperature = mysql_result($result,$nline,10);
	$pH = mysql_result($result,$nline,11);
	$TOC = mysql_result($result,$nline,12);

	$FT = mysql_result($result,$nline,13);
	$exposureconc = mysql_result($result,$nline,14);
	$fresh_marine = mysql_result($result,$nline,15);

	$k1 = mysql_result($result,$nline,16);
	$uptake1 = mysql_result($result,$nline,17);
	$k2 = mysql_result($result,$nline,18);
	$elimi2 = mysql_result($result,$nline,19);
	$T95 = mysql_result($result,$nline,20);
	$T80 = mysql_result($result,$nline,21);
	$uptakeT80  = mysql_result($result,$nline,22);
	$elimT50 = mysql_result($result,$nline,23);
	$elim5x50 = mysql_result($result,$nline,24);

	$tissue = mysql_result($result,$nline,25);
	$BCFssww= mysql_result($result,$nline,26);
	$BCFsslipid= mysql_result($result,$nline,27);
	$BCFkww= mysql_result($result,$nline,28);
	$BCFklipid= mysql_result($result,$nline,29);

	$lipid= mysql_result($result,$nline,30);
	$lipidmethod= mysql_result($result,$nline,31);
	$statistics= mysql_result($result,$nline,32);

	$ref= mysql_result($result,$nline,33);
	$title= mysql_result($result,$nline,34);
	$remarks= mysql_result($result,$nline,35);
	$reason= mysql_result($result,$nline,36);

	$idstructure= mysql_result($result,$nline,37);

	echo "<td>" . ($nline+1 + ($page)*$pagesize) . "</td>";
	echo "<td>" . '<a href="index.php?cas=' . $cas . '">' . $cas . "</a>" . "</td>";
	echo "<td>$name</td>";
	echo "<td><b>" . '<a href="index.php?reliability=' . $reliability . '">' . $reliability . "</a>" . "</b></td>" ;
	echo "<td><i>" . '<a href="index.php?species=' . $species_latinname  . '">' . $species_latinname  . "</a>" .  "</i><br><i>(" . $species_commonname. ")</i><br>". $species_lifestage . "</td>" ;
	echo "<td>" .  $species_sex . "</td>";	
	
	echo "<td>" .  $fish_weight . "</td>";	
	echo "<td>" .  $fish_length . "</td>";	
	echo "<td>" .  $fish_age  . "</td>";	

	echo "<td>" .  $temperature . "</td>";	
	echo "<td>" .  $pH . "</td>";	
	echo "<td>" .  $TOC  . "</td>";	

	echo "<td>" .  $FT . "</td>";	
	echo "<td>" .  $exposureconc . "</td>";	
	echo "<td>" .  $fresh_marine  . "</td>";	

	echo "<td>" .  $k1. "</td>";	
	echo "<td>" .  $uptake1 . "</td>";	
	echo "<td>" .  $k2  . "</td>";	
	echo "<td>" .  $elimi2  . "</td>";	


	echo "<td>" .  $T95. "</td>";	
	echo "<td>" .  $T80 . "</td>";	
	echo "<td>" .  $uptakeT80  . "</td>";	
	echo "<td>" .  $elimT50  . "</td>";	
	echo "<td>" .  $elim5x50  . "</td>";	

	echo "<td>" .  $tissue. "</td>";	
	echo "<td bgcolor='#DDDDDD'><b>" .  $BCFssww . "</b></td>";	
	echo "<td>" .  $BCFsslipid  . "</td>";	
	echo "<td>" .  $BCFkww  . "</td>";	
	echo "<td>" .  $BCFklipid  . "</td>";	

	echo "<td>" .  $lipid. "</td>";	
	echo "<td>" .  $lipidmethod. "</td>";	
	echo "<td>" .  $statistics. "</td>";	
	echo "<td>[" .  $ref  . "] " . $title . "</td>";	
	echo "<td>" .  trim($remarks) . "</td>";	
	echo "<td>" .  $reason. "</td>";	

	echo "</tr>";

	$nline++;
	if ($nline > $pagesize) break;
}
	echo "</table>";

} else echo "<b>No data found in EURAS bioconcentration factor database!</b><p>";

 mysql_free_result($result);   

    	 
 	 // Free resultset

 	 // Closing connection
 	 mysql_close($link);
 	 ?> 

 <?php

function getRow($value) {
	echo "<tr>" . $value . "</tr>";
}

function getRowColored($value,$clr) {
	echo "<tr bgcolor=" . $clr . ">"  . $value . "</tr>";
}

function getProperty($title,$value) {
	return "<td><b>" . $title . "</b></td><td>" . $value . "</td>";
}

function getName($name) {
	return "<b>" . $name . "</b>";
}
?>
 </body></html>