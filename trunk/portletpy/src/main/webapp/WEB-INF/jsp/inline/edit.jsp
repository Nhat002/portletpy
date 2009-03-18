<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ page isELIgnored="false" %>

<portlet:actionURL var="editUrl" portletMode="edit"/>

<h2>Editing script</h2>

<form action="${editUrl}" method="post"/>
    <div class="fi_txt">
        <label for="portletScript">Script</label>
        <textarea id="portletScript" name="portletScript" cols="80" rows="40">
${portletScript}
        </textarea>
        <kbd>Python code, careful with that whitespace</kbd>
    </div>

    <div class="fi_btn">
        <button type="submit" name="cmd" value="save">Save</button>
    </div>
</form>



