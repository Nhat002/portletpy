
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>


<%@ page isELIgnored ="false" %> 

<portlet:renderURL var="editUrl" portletMode="edit"/>

<a href="${editUrl}">Edit script</a>

<pre>
${portletScript}
</pre>






