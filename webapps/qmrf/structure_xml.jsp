<%@ page contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/ambit" prefix="a" %>
<fmt:requestEncoding value="UTF-8"/>

	<?xml version="1.0"?>
	<molecule xmlns="http://www.xml-cml.org/schema"
	xmlns:cml="http://www.xml-cml.org/dict/cml"
	xmlns:units="http://www.xml-cml.org/units/units"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:iupac="http://www.iupac.org">

	<c:set var="substance" value="${param.idsubstance}"/>
	<c:if test="${empty param.idsubstance && (!empty param.idstructure)}">
		<sql:query var="rs" dataSource="jdbc/ambit_qmrf">
			select idsubstance,idstructure from structure where idstructure=? limit 1
			<sql:param value="${param.idstructure}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<c:set var="substance" value="${row.idsubstance}"/>
		</c:forEach>
	</c:if>
	
	<c:if test="${!empty substance}">
		<sql:query var="rs" dataSource="jdbc/ambit_qmrf">
			select name from name join structure using(idstructure) where idsubstance=? group by name;
			<sql:param value="${substance}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<identifier convention="Chemical name" value="${row.name}"/>			
		</c:forEach>

				
		<sql:query var="rs" dataSource="jdbc/ambit_qmrf">
			select casno from cas join structure using(idstructure) where idsubstance=? group by casno;
			<sql:param value="${substance}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<identifier convention="CAS RN" value="${row.casno}"/>			
		</c:forEach>
			
		<sql:query var="rs" dataSource="jdbc/ambit_qmrf">
			select alias,alias_type from alias join structure using(idstructure) where idsubstance=? group by alias_type;
			<sql:param value="${substance}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<identifier convention="${row.alias_type}" value="${row.alias}"/>			
		</c:forEach>
		
		<sql:query var="rs" dataSource="jdbc/ambit_qmrf">
			select smiles,formula,molweight from substance where idsubstance=?;
			<sql:param value="${substance}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<identifier convention="SMILES" value="${row.smiles}"/>			
			<identifier convention="FORMULA" value="${row.formula}"/>
			<identifier convention="Molecular weight" value="${row.molweight}"/>
		</c:forEach>
				
		<propertyList>			
			<sql:query var="rs" dataSource="jdbc/ambit_qmrf">
				select name,value,units,v.error from ambit_qmrf.ddictionary join ambit_qmrf.dvalues as v using(iddescriptor) join structure using(idstructure) where idsubstance=?
				<sql:param value="${substance}"/>
			</sql:query>
			<c:forEach var="row" items="${rs.rows}">
				<property dictRef="ambit:descriptors" title="${row.name}"> 
				<scalar dataType="xsd:double" errorValue="${row.error}" dictRef="ambit:descriptors" units="${row.units}">
				${row.value}
				</scalar>
				</property>
			</c:forEach>
		
			<sql:query var="rsexp" dataSource="jdbc/ambit_qmrf">
				select d.name,c,f.name as field,s.value from ambit_qmrf.study_fieldnames as f join ambit_qmrf.study_results as s using (id_fieldname) join ambit_qmrf.experiment using(idexperiment) join (select idstudy,value as c,fn.name from ambit_qmrf.study_conditions join  ambit_qmrf.study_fieldnames as fn using(id_fieldname)) as d using(idstudy) where idstructure=? and (d.c != "")
				<sql:param value="${param.idstructure}"/>
			</sql:query>
			<c:forEach var="row" items="${rs.rows}">
				<property dictRef="ambit:experimental" title="${row.field}"> 
				<scalar dataType="xsd:double" errorValue="" dictRef="ambit:experimental" units="">
				${row.f}
				</scalar>
				</property>
			</c:forEach>			
		</propertyList>
		</molecule>
	</c:if>

