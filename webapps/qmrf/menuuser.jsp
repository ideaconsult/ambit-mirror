
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
        
    String user = "/qmrf/images/my_documents.png";
    if (highlighted.equalsIgnoreCase("user"))
        user = "/qmrf/images/my_documents_over.png";

    String create = "/qmrf/images/new_document.png";
    if (highlighted.equalsIgnoreCase("create"))
        create = "/qmrf/images/new_document_over.png";

    String profile = "/qmrf/images/my_profile.png";
    if (highlighted.equalsIgnoreCase("profile"))
        profile = "/qmrf/images/my_profile_over.png";


%>

<div id="bartext">
<a href="index.jsp"><img src="<%=welcome%>" border="0"></a>
<a href="search_catalogs.jsp"><img src="<%=search%>" border="0"></a>
<a href="search_substances.jsp"><img src="<%=structures%>" border="0"></a>
<a href="user.jsp"><img src="<%=user%>" border="0"></a>
<a href="create.jsp"><img src="<%=create%>" border="0"></a>


<%
    String advancedsearch = "/qmrf/images/result.png";
    if (highlighted.equalsIgnoreCase("advancedsearch")) {
        advancedsearch = "/qmrf/images/result_over.png";
%>
<a href="search_substances.jsp"><img src="<%=advancedsearch%>" border="0"></a>
<%
    }

%>

<a href="myprofile.jsp"><img src="<%=profile%>" border="0"></a>

</div>