
<%
// See which menu item should be highlighted.
    String highlighted = request.getParameter("highlighted");

// Set the names for the individual menu items.

    String welcome = "/qmrf/images/home.png";
    if (highlighted.equalsIgnoreCase("welcome"))
        welcome = "/qmrf/images/home_over.png";

    String search = "/qmrf/images/search_documents.png";
    if (highlighted.equalsIgnoreCase("search"))
        search = "/qmrf/images/search_documents_over.png";

    String structures = "/qmrf/images/search_structures.png";
    if (highlighted.equalsIgnoreCase("structures"))
        structures = "/qmrf/images/search_structures_over.png";
        

    String admin = "/qmrf/images/pending_qmrfs.png";
    if (highlighted.equalsIgnoreCase("admin"))
        admin = "/qmrf/images/pending_qmrfs_over.png";

    String catalog = "/qmrf/images/statistics.png";
    if (highlighted.equalsIgnoreCase("catalog"))
        catalog = "/qmrf/images/statistics_over.png";

    String profile = "/qmrf/images/user_profiles.png";
    if (highlighted.equalsIgnoreCase("profile"))
        profile = "/qmrf/images/user_profiles_over.png";

    String importstruc = "/qmrf/images/import_structures.png";
    if (highlighted.equalsIgnoreCase("import"))
        importstruc = "/qmrf/images/import_structures_over.png";



%>

<div id="bartext">
<a href="index.jsp"><img src="<%=welcome%>" border="0" alt="Welcome"></a>
<a href="search_catalogs.jsp"><img src="<%=search%>" border="0"></a>
<a href="search_substances.jsp"><img src="<%=structures%>" border="0"></a>
<a href="admin.jsp"><img src="<%=admin%>" border="0" alt="Pending QMRFs"></a>
<%
    String review = "/qmrf/images/review_qmrf.png";
    if (highlighted.equalsIgnoreCase("review")) {
        review = "/qmrf/images/review_qmrf_over.png";
%>
<a href="admin_edit.jsp"><img src="<%=review%>" border="0"></a>
<%
    }

%>

<a href="import.jsp"><img src="<%=importstruc%>" border="0" alt="Structures"></a>
<a href="admin_status.jsp"><img src="<%=catalog%>" border="0" alt="catalogs"></a>

<%
    String advancedsearch = "/qmrf/images/results.png";
    if (highlighted.equalsIgnoreCase("advancedsearch")) {
        advancedsearch = "/qmrf/images/results_over.png";
%>
<a href="search_substances.jsp"><img src="<%=advancedsearch%>" border="0"></a>
<%
    }

%>

<a href="myprofile.jsp"><img src="<%=profile%>" border="0"></a>

</div>