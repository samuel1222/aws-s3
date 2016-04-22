<%@page import="com.myapp.struts.*"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.zip.*, java.util.*"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%
    List<MyFile> files = AmazonS3Manager.listS3Objects();
%>
<html:html lang="true">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><bean:message key="welcome.title"/></title>
        <link rel="stylesheet" type="text/css" href="tree.css">
        <html:base/>
    </head>
    <body style="background-color: white">
        <div style="padding:15px;">
        <html:form action="/upload" styleId="uploadForm" enctype="multipart/form-data" method="post">
            <html:file property="file" size="50"/><button type="submit" class="positive" name="upload">Upload</button>
        </html:form>
        </div>
        <ul>
            <li class="root"><bean:message key="welcome.heading"/></li>
            <% for (int i = 0; i < files.size(); i++) {%>
                <%=files.get(i).toHtmlList()%>
            <%}%>
        </ul>  
    </body>
</html:html>
