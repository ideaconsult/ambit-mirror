<jsp:include page="header.jsp" flush="true">
	<jsp:param name="title_short" value="Repdose"/>
	<jsp:param name="title" value="${initParam['application_title']}"/>
</jsp:include>

<blockquote>
<ul>
<li><a href="search_substances.jsp">Search by exact structure or similarity</a>
<li><a href="search_smarts.jsp">Search by substructures</a>
</ul>
</blockquote>


<jsp:include page="footer.jsp" flush="true"/>



</body>
</html>
