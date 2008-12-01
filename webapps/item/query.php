<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="description" content="AMBIT - REPDOSE demo page ">
<meta name="keywords" content="ambit,repdose">
<meta name="robots"content="index,nofollow">
<meta name="copyright" content="Copyright 2008. Nina Jeliazkova nina@acad.bg">
<meta name="author" content="Nina Jeliazkova">
<meta name="language" content="English">
<meta name="revisit-after" content="7">
<link href="yourstyle.css" rel="stylesheet" type="text/css">
<title>Ambit-Repdose test</title>
</head>
<body >
<?php


// Connecting, selecting database
$time = time();
$link = mysql_connect('nina.acad.bg', 'repdose', 'item2007')
   or die('Could not connect: ' . mysql_error());
//echo "Connected successfully<br>\n";
mysql_select_db('repdose') or die('Could not select database');


echo "<a href='http://nina.acad.bg/repdose/demo.jsp'>Back to the query page</a>";
echo "<p>";

$idquery = $_REQUEST['idquery'];

$query  = "SELECT count(idstructure) as c,iddsname,name FROM dsname join datasets using(iddsname) where iddsname=" . $idquery . " group by iddsname";

$result = mysql_query($query) or die($query . "\nQuery failed: \n" . mysql_error());
$rows = mysql_num_rows($result);

if ($rows == 0) die('No compounds found.');
while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
	echo "Query <b>" .$row["name"] . "</b> "  .$row["c"] . " compounds found.";
}

mysql_free_result($result);

$query  = "SELECT idstructure,casno,iddsname FROM datasets join cas using(idstructure) where iddsname=" . $idquery;

$result = mysql_query($query) or die($query . "\nQuery failed: \n" . mysql_error());

$rows = mysql_num_rows($result);

if ($rows == 0) die('No compounds found.');

$nline = 0;
echo "<table >";
while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
	echo "\t<tr>\n";   
   $id = mysql_result($result,$nline,0); 
   echo "\t<td width='5%'>" . ($nline+1) . ".</td>\n";
   echo "\t<td width='10%'>" . $row["casno"] . "</td>\n";      
	echo "<td>";
	echo "<img border='0' src='http://nina.acad.bg/repdose/image.jsp?width=150&height=150&idstructure=" . $row["idstructure"] . "'>";
	echo "</td>";      
   $nline++;
   echo "\t</tr>\n";
   
   
}
echo "</table>";
// Free resultset
mysql_free_result($result);




// Closing connection
mysql_close($link);

?> 


</body>
</html>
